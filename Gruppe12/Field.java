//Prototype implementation of Field class
//Mandatory assignment 2
//Course 02158 Concurrent Programming, DTU,  Fall 2024

//Hans Henrik Lovengreen       Oct 1, 2024

public class Field {
private Semaphore[][] tiles;
    public Field() {
        tiles = new Semaphore[11][12];

        for (int row = 0; row < 11; row++) {
            for (int col = 0; col < 12; col++) {
                tiles[row][col] = new Semaphore(1);
            }
        }
    }


    public void enter(int no, Pos pos) throws InterruptedException {
        tiles[pos.row][pos.col].P();  
    }


    public void leave(Pos pos) {
        tiles[pos.row][pos.col].V();  
    }

}
