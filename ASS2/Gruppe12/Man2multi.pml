//Simple model to demonstrate deadlock for sync

#define N 9

bool waiting[N];       /* Track waiting cars */
bool in_critical[N];   /* Track cars in alley */



int up = 0;
int down = 0;
int upSem = 1;
int downSem = 1;

active [N] proctype Car() {
	do 
	:: skip;

	enter:
		waiting[_pid] = true;
		if 
	:: _pid < 5 ->
		downSem > 0 ->
		downSem--;
			if
			:: down == 0 ->
			upSem = 0;
			fi;
		down++;
		downSem++;
	:: else ->
		upSem > 0 ->
		upSem--;
			if
			:: up == 0 ->
			downSem = 0;
			fi;
		up++;
		upSem++;		
	fi; 

crit:
	in_critical[_pid] = true;
	waiting[_pid] = false;
	//assert(!(in_critical[4] && in_critical[5]));
	
exit:
	if 
	:: _pid < 5 ->
		down--;
		if 
		:: down == 0 ->
		upSem = 1;
		fi;
	:: else ->
		up--;
		if 
		:: up == 0 ->
		downSem = 1;
		fi;
	fi;

	in_critical[_pid] = false;

	od
}

ltl deadlock{ []((waiting[0]) -> <>(in_critical[0]))}
