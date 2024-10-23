//Dummy implementation of Alley class
//Mandatory assignment 2
//Course 02158 Concurrent Programming, DTU,  Fall 2024

//Hans Henrik Lovengreen       Oct 1, 2024

public class Alley {

    protected Alley() { }

    public static Alley create() {
        return new Alley();                                     // Change to use desired implementation
    }

    /* Block until car no. may enter alley */
    public void enter(int no) throws InterruptedException { }

    /* Register that car no. has left the alley */
    public void leave(int no) { }
    
}
