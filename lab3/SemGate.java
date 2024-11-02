//Semaphore implementation of Gate
//CP Lab 3
//Course 02158 Concurrent Programming, DTU

//Hans Henrik Lovengreen


public class SemGate extends Gate {

    Semaphore g = new Semaphore(0);
    Semaphore e = new Semaphore(1);
    boolean isopen = false;

    public void pass() throws InterruptedException {
        g.P(); 
        g.V();
    }

    public void open() {
        try { 
            e.P(); 
            if (!isopen) { g.V();  isopen = true; }
            e.V();
        } catch (InterruptedException e) {}
    }

    public void close() {
        try { 
            e.P();
            if (isopen) { g.P();  isopen = false; }
            e.V();
        } catch (InterruptedException e) {}
    }

}
