#define N 9

bool waiting[N];       /* Track waiting cars */
bool in_critical[N];   /* Track cars in alley */

int up = 0;
int down = 0;
int upSem = 1;
int downSem = 1;

/* Inline function for cars entering the alley */
inline enter_alley() {
    waiting[_pid] = true;
    if 
    :: _pid < 5 ->/* For cars 0-4 (down) */
        atomic{               /*Ensuring atomicity in P actions*/  
        	downSem > 0 -> downSem--;
        }
            if
            :: down == 0 ->
                atomic{ upSem--; }
            fi;
        donw = down + 1;
        atomic{ downSem++; }
    :: else ->                      /* For cars 5-8 (up) */
        atomic{		/*Ensuring atomicity for P action*/
        	upSem > 0 -> upSem--;
        }
            if
            :: up == 0 ->
                atomic{ downSem--; }
            fi;
        up = up + 1;
        atomic { upSem++; }        
    fi;
}

/* Inline function for cars exiting the alley */
inline exit_alley() {
    if 
    :: _pid < 5 ->                 /* For cars 0-4 (down) */
        down = down - 1;
        if
        :: down == 0 ->
            atomic{ upSem++; }
       :: else -> skip;
        fi;
    :: else ->                      /* For cars 5-8 (up) */
        up = up - 1;
        if
        :: up == 0 ->
            atomic{ downSem++; }
       :: else -> skip;
        fi;
    fi;
    in_critical[_pid] = false;
    waiting[_pid] = false;
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
        fi
    exit:
        exit_alley();

    od
}

/* LTL formula to check for deadlock: any car waiting eventually enters critical */
ltl deadlock { []((waiting[0]) -> <>(in_critical[0])) }