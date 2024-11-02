//Prototype implementation of Field class
//CP Lab 3
//Course 02158 Concurrent Programming, DTU

//Hans Henrik Lovengreen

public class Field {
    
    Semaphore[][] tileMutex = new Semaphore[Layout.ROWS][Layout.COLS];

    public Field() {
        for (int i = 0; i < Layout.ROWS; i++) {
            for (int j = 0; j < Layout.COLS; j++) {
                tileMutex[i][j] = new Semaphore(1);
            }
        }
    }

    /* Block until car no. may safely enter tile at pos */
    public void enter(int no, Pos pos) throws InterruptedException {
        tileMutex[pos.row][pos.col].P();
    }

    /* Release tile at position pos */
    public void leave(Pos pos) {
        tileMutex[pos.row][pos.col].V();
    }

}
