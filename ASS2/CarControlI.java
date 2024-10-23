//Specification of Car Control interface 
//Mandatory assignment 2
//Course 02158 Concurrent Programming, DTU,  Fall 2024

//Hans Henrik Lovengreen       Oct 1, 2024

interface CarControlI {

    /*  
     *  The following methods will be called sequentially.  
     *  No particular ordering of calls can be assumed. 
     *  
     *  All these methods should return without blocking (for long time) since 
     *  they may be called directly by the window event dispatcher thread 
     */   

    public void stopCar(int no);                 // Stop  Car no. by closing gate

    public void startCar(int no);                // Start Car no. by opening gate

    public void barrierOn();                     // Activate barrier

    public void barrierOff();                    // Deactivate barrier

    public void removeCar(int no);               // Remove Car no. wherever it is
    // [SHIFT+Click at gate no.]

    public void restoreCar(int no);              // Restore Car no. at gate position
    // [CTRL+Click at gate no.]

    public void setSpeed(int no, double speed);  // Set speed of car no. (for testing)

    public void setVariation(int no,int var);    // Set speed variation (percentage) of car no. (for testing)

    /*
     *  The barrierSet() method may be called concurrently with the other methods,
     *  but not with itself.
     *  
     *  barrierSet(k) should set the barrier threshold to k.  However, if the threshold is
     *  raised while cars are waiting, the threshold must not be changed until the next release 
     *  of the barrier (using the current threshold).  In this case, the call must block until then.
     */

    public void barrierSet(int k);               // Set barrier threshold
}
