//Basic implementation of Alley class (skeleton)
//CP Lab 3
//Course 02158 Concurrent Programming, DTU

//Hans Henrik Lovengreen

public class BasicAlley extends Alley {
    int carUp, carDown;
    //1-4 ned
    //>4 op

   
    BasicAlley() {
        carUp = 0;
        carDown = 0;
    }

    @Override
    /* Block until car no. may enter alley */
    public synchronized void enter(int no) throws InterruptedException {
        if(no > 4){
            if(carDown > 0){
                wait();
                carUp++;
            } else {
                carUp++;
            }
        } else if(no < 5){
            if(carUp > 0){
                wait();
                carDown++;                
            } else {
                carDown++;
            }
        } 
    }

    @Override
    /* Register that car no. has left the alley */
    public synchronized void leave(int no) {
        if(no > 4){
            carUp--;
            if(carUp == 0) notify();
        } else if(no < 5){
            carDown--;
            if(carDown == 0) notify();
        } else{
            return;
        }
    }
}
