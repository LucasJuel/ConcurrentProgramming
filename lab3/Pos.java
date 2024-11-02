//Implementation of position class 
//CP Lab 3
//Course 02158 Concurrent Programming, DTU

//Hans Henrik Lovengreen
//

public class Pos {

    public int row;       // Note: public
    public int col;

    public Pos(int i, int j) { row = i;  col = j; }

    public static boolean equal(Pos p1, Pos p2) {
        if (p1 == null || p2 == null) return false;
        return (p1.row == p2.row) && (p1.col == p2.col);
    }

    public Pos copy() {
        return new Pos(row, col);
    }

    public boolean equals(Object p) {
        return (p instanceof Pos && Pos.equal(this,(Pos) p));
    }

    public int hashCode() { 
        // Borrowed from java.awt.geom.Point2D
        long bits = java.lang.Double.doubleToLongBits(row);
        bits ^= java.lang.Double.doubleToLongBits(col) * 31;
        return (((int) bits) ^ ((int) (bits >> 32)));
    }

    public String toString() {
        return "("+row+","+col+")";
    }

}

