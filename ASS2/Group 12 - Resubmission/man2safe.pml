#define N 9
bool waiting[N];       /* Track waiting cars */
bool in_critical[N];   /* Track cars in alley */
int up = 0;
int down = 0;
bool upSem = true;
bool downSem = true;
bool lock = true;

/* P(semaphore) - aka wait() or down() */
inline P(sem) {
    atomic {
        sem -> sem = false;
    }
}

/* V(semaphore) - aka signal() or up() */
inline V(sem) {
    atomic {
        sem = true;
    }
}


/* Inline function for cars entering the alley */
inline enter_alley() {
    waiting[_pid] = true;
    if 
    :: _pid < 5 ->/* For cars 0-4 (down) */

			P(downSem);
           if
            :: down == 0 ->
                P(lock);
				assert((up == 0));
            fi;
        	down = down + 1;
        	V(downSem); 
	
    :: else ->                      /* For cars 5-8 (up) */

			P(upSem);
            if
            :: up == 0 ->
                P(lock);
				assert((down == 0)); 
            fi;
        	up = up + 1;
        	V(upSem); 
	  
    fi;
}

/* Inline function for cars exiting the alley */



inline exit_alley() {
    if 
    :: _pid < 5 ->                 /* For cars 0-4 (down) */
        down = down - 1;
        if
        :: down == 0 ->
    		in_critical[_pid] = false;
    		waiting[_pid] = false;
            V(lock);
       :: else -> 
    		in_critical[_pid] = false;
    		waiting[_pid] = false;

        fi;
    :: else ->                      /* For cars 5-8 (up) */
        up = up - 1;
        if
        :: up == 0 ->
    	in_critical[_pid] = false;
    	waiting[_pid] = false;
            V(lock);
       :: else -> 
atomic{
		in_critical[_pid] = false;
    	waiting[_pid] = false;
}
        fi;
    fi;
}
active [N] proctype Car() {
    do
    :: skip;
    enter:
        enter_alley();
    crit:
        in_critical[_pid] = true;
        waiting[_pid] = false; 
        if
        :: _pid < 5 ->
            assert(!(in_critical[5] || in_critical[6] || in_critical[7] || in_critical[8]));  /* Safety check that no upCars are in crit when _pid < 5 is in crit*/
        :: _pid > 4 -> 
            assert(!(in_critical[0] || in_critical[1] || in_critical[2] || in_critical[3] || in_critical[4]));  /* Safety check that no downCars are in crit when _pid > 4 is in crit*/
        fi;
    exit:
        exit_alley();
    od
}

/* LTL formula to check for deadlock: any car waiting eventually enters critical */
ltl deadlock { []((waiting[0]) -> <>(in_critical[0])) }