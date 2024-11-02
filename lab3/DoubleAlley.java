//Implementation of Alley class with inner alley (skeleton)
//CP Lab 3
//Course 02158 Concurrent Programming, DTU

//Hans Henrik Lovengreen

public class DoubleAlley extends Alley {
   int carUp, carDown, inOuterAlley;
    //1-4 ned
    //>4 op

   
    DoubleAlley() {
        carUp = 0;
        carDown = 0;
        inOuterAlley = 0;
    }

    @Override
    /* Block until car no. may enter alley */
    public synchronized void enter(int no) throws InterruptedException {
        if(no > 4){
            while(carDown > 0){ //If car is 5, 6, 7 or 8 and some cars are going down.
                wait(); //Make it wait.
            } 
            carUp++; //When wait -> notify, set carUp++;
        } else if(no < 5){ 
            while(carUp > 0 || ((no == 3 || no == 4) && inOuterAlley > 0)){ //If car is 4, 3, 2, 1 and some cars are going up
                wait(); //Make it wait.
            }
            // if((no == 3 || no == 4) && inOuterAlley == 0) wait();
            carDown++;                
        } 
    }

    //Leave is called after shed.
    @Override
    /* Register that car no. has left the alley */
    public synchronized void leave(int no) {
        if(no > 4){ 
            if (inOuterAlley > 0) inOuterAlley--; // Decrement if car was in outer alley
            if(inOuterAlley == 0) notifyAll();
        }
        
        if(no < 5){
            carDown--;
            if(carDown == 0) notifyAll();
        }
    }
    @Override
    /* Register that car no. has left the inner alley */
    public synchronized void leaveInner(int no) {
        if(no > 4){ //If car leaves inner add to outerAlley and check if no more cars are going up.
            inOuterAlley++; 
            carUp--;
            if(carUp == 0) notifyAll(); //If no more up cars, call to wake all cars.
        }

    }

}
