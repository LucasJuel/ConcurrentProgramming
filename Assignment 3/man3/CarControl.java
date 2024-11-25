//Implementation of CarControl class
//Mandatory assignment 3
//Course 02158 Concurrent Programming, DTU, Fall 2024

//Hans Henrik Lovengreen       Nov 1, 2024


import java.awt.Color;

class Conductor extends Thread {

    double basespeed = 7.0;          // Tiles per second
    double variation =  40;          // Percentage of base speed

    CarDisplayI cd;                  // GUI part
    
    Field field;                     // Field control
    Alley alley;                     // Alley control    
    Barrier barrier;                 // Barrier control    

    int no;                          // Car number
    Pos startpos;                    // Start position (provided by GUI)
    Pos barpos;                      // Barrier position (provided by GUI)
    Color col;                       // Car  color
    Gate mygate;                     // Gate at start position

    Pos curpos;                      // Current position 
    Pos newpos;                      // New position to go to

    CarI car;                        // Reference to graphical car

    Boolean inAlley = false;            // Boolean to check if car is in alley
    Boolean hasEntered = false;

    public Conductor(int no, CarDisplayI cd, Gate g, Field field, Alley alley, Barrier barrier) {

        this.no = no;
        this.cd = cd;
        this.field = field;
        this.alley = alley;
        this.barrier = barrier;
        mygate = g;
        startpos = cd.getStartPos(no);
        barpos   = cd.getBarrierPos(no);  // For later use

        col = chooseColor();

        // special settings for car no. 0
        if (no==0) {
            basespeed = -1.0;  
            variation = 0; 
        }
    }

    public synchronized void setSpeed(double speed) { 
        basespeed = speed;
    }

    public synchronized void setVariation(int var) { 
        if (no != 0 && 0 <= var && var <= 100) {
            variation = var;
        }
        else
            cd.println("Illegal variation settings");
    }

    public CarI getCar() {
        return car;
    }

    public Pos getPos() {
        return curpos;
    }
    synchronized double chooseSpeed() { 
        double factor = (1.0D+(Math.random()-0.5D)*2*variation/100);
        return factor*basespeed;
    }

    Color chooseColor() { 
        return Color.blue; // You can get any color, as longs as it's blue 
    }

    Pos nextPos(Pos pos) {
        // Get my track from display
        return cd.nextPos(no,pos);
    }

    boolean atGate(Pos pos) {
        return pos.equals(startpos);
    }

    boolean atEntry(Pos pos) {
        return (pos.row ==  1 && pos.col ==  1) || (pos.row ==  2 && pos.col ==  1) || 
               (pos.row == 10 && pos.col ==  0);
    }

    boolean atExit(Pos pos) {
        return (pos.row ==  0 && pos.col ==  0) || (pos.row ==  9 && pos.col ==  1);
    }
    
    boolean atBarrier(Pos pos) {
        return pos.equals(barpos);
    }

    public void run() {
        try {
            car = cd.newCar(no, col, startpos);
            
            curpos = startpos;
            field.enter(no, curpos);
            cd.register(car);

            while (true) { 

                if (atGate(curpos)) { 
                    mygate.pass(); 
                    car.setSpeed(chooseSpeed());
                }

                newpos = nextPos(curpos);

                if (atBarrier(curpos)) barrier.sync(no);
                
                if (atEntry(curpos)) {
                    alley.enter(no);
                    inAlley = true;
                }
                
                field.enter(no, newpos);
                hasEntered = true;
                car.driveTo(newpos);

                field.leave(curpos);
                hasEntered = false;
                if (atExit(newpos)) {
                    alley.leave(no);
                    inAlley = false;
                }

                curpos = newpos;

            }

        } catch (Exception e) {
            cd.println("Exception in Conductor no. " + no);
            System.err.println("Exception in Conductor no. " + no + ":" + e);
            e.printStackTrace();
            cd.deregister(car);
            if(inAlley) alley.leave(no);
            if(hasEntered) field.leave(curpos);
            
            
        }
    }

}

public class CarControl implements CarControlI{

    CarDisplayI cd;           // Reference to GUI
    Conductor[] conductor;    // Car controllers
    Gate[] gate;              // Gates
    Field field;              // Field
    Alley alley;              // Alley
    Barrier barrier;          // Barrier

    public CarControl(CarDisplayI cd) {
        this.cd = cd;
        conductor = new Conductor[9];
        gate = new Gate[9];
        field = new Field();
        alley = Alley.create();
        barrier = Barrier.create(cd);

        for (int no = 0; no < 9; no++) {
            gate[no] = Gate.create();
            conductor[no] = new Conductor(no,cd,gate[no],field,alley,barrier);
            conductor[no].setName("Conductor-" + no);
            conductor[no].start();
        } 
    }

    public void startCar(int no) {
        gate[no].open();
    }

    public void stopCar(int no) {
        gate[no].close();
    }

    public void barrierOn() { 
        barrier.on();
    }

    public void barrierOff() {
        barrier.off();
    }

   public void barrierSet(int k) {
        barrier.set(k);
   }
    
    public synchronized void removeCar(int no) { 
        if(!conductor[no].isAlive()) return;


        conductor[no].interrupt();
        try {
            conductor[no].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        

        cd.println("Remove Car no: " + no);
    }

    public synchronized void restoreCar(int no) { 
        if(conductor[no].isAlive()) return;
        cd.println("Restore Car no:" + no);
        conductor[no] = new Conductor(no,cd,gate[no],field,alley,barrier);
        conductor[no].setName("Conductor-" + no);
        conductor[no].start();

    }

    /* Speed settings for testing purposes */

    public void setSpeed(int no, double speed) { 
        conductor[no].setSpeed(speed);
    }

    public void setVariation(int no, int var) { 
        conductor[no].setVariation(var);
    }

}






