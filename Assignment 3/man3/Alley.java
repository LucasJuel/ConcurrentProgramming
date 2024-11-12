//Abstract Alley class
//Mandatory assignment 3
//Course 02158 Concurrent Programming, DTU, Fall 2024

//Hans Henrik Lovengreen       Nov 1, 2024

public abstract class Alley {

    /* Factory method -- change to switch implementation */
    public static Alley create() {
        return new BasicAlley();
    }
    
    /* Block until car no. may enter alley */
    public void enter(int no) throws InterruptedException {
    }

    /* Register that car no. has left the alley */
    public void leave(int no) {
    }

    /* Register that car no. has left the inner alley */
    public void leaveInner(int no) {
    }

}
