//Semaphore implementation of Gate
//Mandatory assignment 3
//Course 02158 Concurrent Programming, DTU, Fall 2024

//Hans Henrik Lovengreen       Nov 1, 2024


public class SemGate extends Gate {

    Semaphore g = new Semaphore(0);
    Semaphore e = new Semaphore(1);
    boolean isopen = false;

    public void pass() throws InterruptedException {
        g.P(); 
        g.V();
        System.out.println("Gate was passed!");
    }

    public void open() {
        System.out.println("Gate was opened!");
        try { 
            e.P(); 
            if (!isopen) { g.V();  isopen = true; }
            e.V();
        } catch (InterruptedException e) {}
    }

    public void close() {
        System.out.println("Gate was closed!");
        try { 
            e.P();
            if (isopen) { g.P();  isopen = false; }
            e.V();
        } catch (InterruptedException e) {}
    }

}
