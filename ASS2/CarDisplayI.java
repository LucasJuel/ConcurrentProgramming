//Specification of Car Control interface 
//Mandatory assignment 2
//Course 02158 Concurrent Programming, DTU,  Fall 2024

//Hans Henrik Lovengreen       Oct 1, 2024

import java.awt.Color;

interface CarDisplayI {

    // Get hold of a car  and give it a colour, a number and a position.  
    public CarI newCar(int no, Color col, Pos pos);

    // Register a car for driving and visual appearance.  Also enables collision detection.
    public void register(CarI car);

    // Deregister a car.  Will no longer be seen on the playground and cannot collide with
    // other cars. Any movement is ceased.  May be re-registered and will continue from 
    // previous location. [Think crane.]
    public boolean deregister(CarI car);

    // Print (error) message on screen
    public void println(String message);

    // Get start/gate position of Car no. (on private part of track)
    public Pos getStartPos(int no);       

    // Get position right before barrier line for Car no. (on private part of track).
    public Pos getBarrierPos(int no);    

    // Get next position for Car no.
    public Pos nextPos(int no, Pos pos);

}



