//Implementation of a basic Barrier class (skeleton)
//Mandatory assignment 3
//Course 02158 Concurrent Programming, DTU, Fall 2024

//Hans Henrik Lovengreen       Nov 1, 2024

//Implementation of sticky Barrier class (skeleton)
//Mandatory assignment 3
//Course 02158 Concurrent Programming, DTU, Fall 2024

//Hans Henrik Lovengreen       Nov 1, 2024

class SafeBarrier extends Barrier {
    
    int arrived = 0;
    int cycles = 0;
    boolean active = false;

    public SafeBarrier(CarDisplayI cd) {
        super(cd);
    }

    @Override
    public synchronized void sync(int no) throws InterruptedException {
        if (!active) return;
        
        //Thread.sleep(10000);

        int currentCycle = cycles; // Store the current cycle number
        arrived++;

        if(arrived < 9) { 
            while(currentCycle == cycles && active){
                wait();
            }
            
        } else {
            
            arrived = 0;
            cycles++;
            notifyAll();
        }

    }

    @Override
    public synchronized void on() {
        active = true;
    }

    @Override
    public synchronized void off() {
        active = false;
        arrived = 0;
        cycles++;
        notifyAll();        
    }


    @Override
    // Here (ab)used for emulation of spurious wakeups
    public synchronized void set(int k) {
        for (int i = 0; i < k; i++) { notify(); }
    }    
 
    
}
