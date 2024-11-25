//Skeleton implementation of a safe Alley class
//Mandatory assignment 2
//Course 02158 Concurrent Programming, DTU,  Fall 2024

//Hans Henrik Lovengreen       Oct 1, 2024

public class SafeAlley extends Alley {

    private int up, down;
    private Semaphore upSem, downSem, lock;
    
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
            try{ 
            if (down == 0) lock.P();    // block for up-going cars
            down++;
            } finally {
                downSem.V();
            }
        } else {
            upSem.P();
            try {
            if (up == 0) lock.P();    // block for down-going cars
            up++;
            } finally {
                upSem.V();
            }
        }

     }

    /* Register that car no. has left the alley */
    public void leave(int no) {
        if (no < 5) {           
            try {
                downSem.P();
                down--;
                if (down == 0) lock.V();    // enable up-going cars again
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                downSem.V();
            }

            
        } else {
            try{
                upSem.P();
                up--; 
                if (up == 0) lock.V();    // enable down-going cars again
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                upSem.V();
            }
        }
    }

}
