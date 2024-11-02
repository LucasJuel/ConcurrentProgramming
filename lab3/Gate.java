//Abstract Gate class
//CP Lab 3
//Course 02158 Concurrent Programming, DTU

//Hans Henrik Lovengreen


public abstract class Gate {

    public static Gate create() {
        return new SemGate();              // Change to select implementation
    }
    
    public abstract void pass() throws InterruptedException; 

    public abstract void open();

    public abstract void close();
}
