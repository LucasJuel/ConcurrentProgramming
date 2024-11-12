#define N 9
bool waiting[N];       /* Track waiting cars */
bool in_critical[N];   /* Track cars in alley */
int up = 0;
int down = 0;
int upSem = 1;
int downSem = 1;

/* P(semaphore) - aka wait() or down() */
inline P(sem) {
    atomic {
        sem > 0 -> sem--;
    }
}

/* V(semaphore) - aka signal() or up() */
inline V(sem) {
    atomic {
        sem++;
    }
}



/* Inline function for cars entering the alley */
inline enter_alley() {
    waiting[_pid] = true;
    if 
    :: _pid < 5 ->/* For cars 0-4 (down) */
        atomic{            
        	P(downSem);
        }
            if
            :: down == 0 ->
                P(upSem);
			:: else -> skip;
            fi;
        down = down + 1;
        V(downSem);
    :: else ->                      /* For cars 5-8 (up) */
        atomic{		
        	P(upSem);
        }
            if
            :: up == 0 ->
                P(downSem);
			:: else -> skip;
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
            V(upSem);
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
            V(downSem);
       :: else ->
    		in_critical[_pid] = false;
    		waiting[_pid] = false;
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
        else -> 
            assert(!(in_critical[0] || in_critical[1] || in_critical[2] || in_critical[3] || in_critical[4]));  /* Safety check that no downCars are in crit when _pid > 4 is in crit*/
        fi;
    exit:
        exit_alley();
    od
}
/* LTL formula to check for deadlock: any car waiting eventually enters critical */
ltl deadlock { []((waiting[0]) -> <>(in_critical[0])) }