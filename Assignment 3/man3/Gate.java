//Abstract Gate class
//Mandatory assignment 3
//Course 02158 Concurrent Programming, DTU, Fall 2024

//Hans Henrik Lovengreen       Nov 1, 2024


public abstract class Gate {

    public static Gate create() {
        return new MonGate();              // Change to select implementation
    }
    
    public abstract void pass() throws InterruptedException; 

    public abstract void open();

    public abstract void close();
}
