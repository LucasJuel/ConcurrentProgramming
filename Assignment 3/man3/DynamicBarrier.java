//Implementation of dynamic Barrier class (skeleton)
//Mandatory assignment 3
//Course 02158 Concurrent Programming, DTU, Fall 2024

//Hans Henrik Lovengreen       Nov 1, 2024

class DynamicBarrier extends Barrier {
    
    public DynamicBarrier(CarDisplayI cd) {
        super(cd);
    }

    @Override
    public void sync(int no) throws InterruptedException {
    }

    @Override
    public void on() {
    }

    @Override
    public void off() {
    }

    @Override
    /* Set barrier threshold */
    public void set(int k) {
    }

}
