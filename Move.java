import java.util.List;
/**
 * Represents a move in the Reversi game, encapsulating information about the disc placed,
 * the positions of discs that changed color, and the other player's state.
 */
public class Move {

    private final Disc newDiscPlaced; // The disc that was placed during this move.
    private final Position newDiscPlacedPosition; //The position on the board where the new disc was placed.
    private final List<Position> discsChangedColorPosition; // The list of positions on the board where discs changed color as a result of this move.
    private final List<Disc> discsChangedColor; //The list of discs that changed color as a result of this move.
    private final Player otherPlayer; // The opponent player during this move.
    private Position position; // A position
    private Disc disc;//A disc

    /**
     * Creates a new Move instance with the specified parameters.
     * @param newDiscPlaced   --->       The disc that was placed during this move.
     * @param newDiscPlacedPosition  ---> The position where the new disc was placed.
     * @param discsChangedColorPosition ---> List of positions where discs changed color due to the move.
     * @param discsChangedColor  --->  List of discs that changed color due to the move.
     * @param otherPlayer  --->        The opponent player at the time of the move.
     */

    public Move(Disc newDiscPlaced, Position newDiscPlacedPosition, List<Position> discsChangedColorPosition, List<Disc> discsChangedColor, Player otherPlayer) {
        this.newDiscPlaced = newDiscPlaced;
        this.newDiscPlacedPosition = newDiscPlacedPosition;
        this.discsChangedColorPosition = discsChangedColorPosition;
        this.discsChangedColor = discsChangedColor;
        this.otherPlayer = otherPlayer;
    }


    /**
     * Gets the position associated with this move.
     * @return ---> The position.
     */

    public Position position() {
        return this.position;
    }
    /**
     * Gets the disc associated with this move.
     * @return ---> The disc.
     */

    public Disc disc() {
        return this.disc;
    }
    /**
     * Gets the position where the new disc was placed.
     * @return --->  The position of the newly placed disc.
     */
    public Position getNewDiscPlacedPosition() {
        return newDiscPlacedPosition;
    }

    /**
     * Gets the disc that was newly placed during this move.
     * @return ---> The newly placed disc.
     */
    public Disc newDiscPlaced() {
        return newDiscPlaced;
    }

    /**
     * Gets the list of positions where discs changed color during this move.
     * @return  ---> A list of positions of changed discs.
     */

    public List<Position> getDiscsChangedColorPosition() {
        return discsChangedColorPosition;
    }
    /**
     * Gets the list of discs that changed color during this move.
     * @return  ---> A list of changed discs.
     */
    public List<Disc> getDiscsChangedColor() {
        return discsChangedColor;
    }


}
