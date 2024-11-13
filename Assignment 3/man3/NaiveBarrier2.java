//Naive implementation of Barrier class
//Mandatory assignment 3
//Course 02158 Concurrent Programming, DTU, Fall 2024

//Hans Henrik Lovengreen       Nov 1, 2024

class NaiveBarrier2 extends Barrier {
    
    int arrived = 0;
    boolean active = false;
   
    public NaiveBarrier2(CarDisplayI cd) {
        super(cd);
    }

    @Override
    public void sync(int no) throws InterruptedException {

        if (!active) return;
        //Thread.sleep(10000);        
        arrived++;

            
        synchronized(this) {
                
            if (arrived < 9) { 
                wait();
            } else {
                arrived = 0;
                notifyAll();
            }

        }
    }

    @Override
    public void on() {
        active = true;
    }

    @Override
    public void off() {
        active = false;
        arrived = 0;
        synchronized(this) {
            notifyAll();
        }
    }


    @Override
    // Here (ab)used for emulation of spurious wakeups
    public synchronized void set(int k) {
        for (int i = 0; i < k; i++) { notify(); }
    }
}    
