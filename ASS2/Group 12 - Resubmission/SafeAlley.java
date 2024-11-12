//Skeleton implementation of a safe Alley class
//Mandatory assignment 2
//Course 02158 Concurrent Programming, DTU,  Fall 2024

//Hans Henrik Lovengreen       Oct 1, 2024

public class SafeAlley extends Alley {

    int up, down;
    Semaphore upSem, downSem, lock;
    
    protected SafeAlley() {
        up = 0;   down = 0;
        upSem   = new Semaphore(1);
        downSem = new Semaphore(1);
        lock = new Semaphore(1);
    }

    /* Block until car no. may enter alley */
    public void enter(int no) throws InterruptedException {
        if (no < 5) {
            downSem.P();
            if (down == 0) lock.P();;    // block for up-going cars
            down++;
            downSem.V();
        } else {
            upSem.P();
            if (up == 0) lock.P();    // block for down-going cars
            up++;
            upSem.V();
        }

     }

    /* Register that car no. has left the alley */
    public void leave(int no) {
        if (no < 5) {
            down--;
            if (down == 0) lock.V();    // enable up-going cars again
        } else {
            up--; 
            if (up == 0) lock.V();    // enable down-going cars again
        }
    }

}
