//Basic mplementation of Alley class
//Mandatory assignment 3
//Course 02158 Concurrent Programming, DTU, Fall 2024

//Hans Henrik Lovengreen       Nov 1, 2024

public class BasicAlley extends Alley {

    int up = 0, down = 0;
    
    /* Block until car no. may enter alley */
    public synchronized void enter(int no) throws InterruptedException {
        if (no < 5) {
            while (up   > 0) { wait(); }
            down++;
        } else {
            while (down > 0) { wait(); }
            up++;
        }
     }

    /* Register that car no. has left the alley */
    public synchronized void leave(int no) {
        if (no < 5) {
            down--; 
            if (down == 0) notifyAll();
        } else {
            up--; 
            if (up   == 0) notifyAll();
        }
    }

}
