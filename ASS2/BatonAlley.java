//Skeleton implementation of an Alley class  using passing-the-baton
//Mandatory assignment 2
//Course 02158 Concurrent Programming, DTU,  Fall 2024

//Hans Henrik Lovengreen       Oct 1, 2024

public class BatonAlley extends Alley {

    protected BatonAlley() {
        
    }

    /* Block until car no. may enter alley */
    public void enter(int no) throws InterruptedException {
        if (no < 5) {
            
        } else {
            
        }

     }

    /* Register that car no. has left the alley */
    public void leave(int no) {
        if (no < 5) {

        } else {

        }
    }

}
