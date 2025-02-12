/**
 *  Represents a position on a game board using a row and column.
 */

public class Position {
    /**
     * These are the department fields:
     *  row ---> The row index of the position.
     *  The ---> column index of the position.
     */
    private int row;
    private int column;

    /**
     * Constructs a Position object with the specified row and column.
     * @param row --->  row the row index of the position.
     * @param column ---> column the column index of the position.
     */
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Returns the row index of this position.
     * @return --->the row index.
     */
    public int row() {
        return this.row;
    }
    /**
     * Returns the column index of this position.
     * @return ---> the column index .
     */
    public int col() {
        return this.column;
    }

    /**
     * Compares this position with another object for equality.
     * Two positions are equal if their row and column indices are the same.
     * @param obj ---> the object to compare with
     * @return ---> true if the specified object is equal to this position, false otherwise
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Position other = (Position) obj;
        return row == other.row && column == other.column;
    }

    /**
     * Returns a string representation of this position in the format (row,column).
     * @return ---> a string representation of the position
     */

    @Override
    public String toString() {
        return "(" + row + "," + col() + ")";
    }
}
