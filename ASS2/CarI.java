//Specification of Car interface 
//Mandatory assignment 2
//Course 02158 Concurrent Programming, DTU,  Fall 2024

//Hans Henrik Lovengreen       Oct 1, 2024


interface CarI {
    /* 
     * Operations for controlling car movement
     * 
     * All methods may be called concurrently
     */

    // Set new nominal speed (tiles/sec).  Negative values represent infinite speed.
    public void setSpeed(double speed); 

    // Sets pos as destination and awaits arrival at this
    public void driveTo(Pos pos) throws InterruptedException;


    /* Below methods for asynchronous driving are supposed to be used only in Step 7 */

    // Adds pos as an extension to the current route (potentially defining a new one).
    // Returns a unique id for this destination.
    public long addDestination(Pos pos);

    // As above.  In addition, the run() method of the callback is executed when the destination 
    // is reached. Callbacks are called sequentially in arrival order by an external thread.  
    // The callbacks must not block.
    // NB. Use of callbacks on infinitely fast cars should be avoided
    public long addDestination(Pos pos, Runnable callback);

    // Polls for whether the car has arrived at the destination identified by id.//
    public boolean hasArrivedAt(long id);

    // Awaits arrival at the destination identified by id.
    public void awaitArrivalAt(long id) throws InterruptedException;

}



