//Monitor implementation of Gate (skeleton)
//Mandatory assignment 3
//Course 02158 Concurrent Programming, DTU, Fall 2024

//Hans Henrik Lovengreen       Nov 1, 2024


public class MonGate extends Gate {
    //Monitor is class with mutual exclusion between methods.

    boolean isOpen = false;

    public synchronized void pass() throws InterruptedException {
        while(!isOpen) wait(); //When car reaches the pass, it checks if it is open. If not it waits.
    }

    public synchronized void open() {
        if(!isOpen) {isOpen = true; notifyAll();} //When the open flag is called, it notifies all cars.
    } 

    public synchronized void close() {
        if(isOpen) {isOpen = false;} //Just set isOpen flag = false;
    }
}
