/**
 * Represents an AI player that makes random moves in the game.
 * The `RandomAI` class extends the `AIPlayer` class and provides a basic implementation
 * for an AI player that selects moves randomly.
 */
public class RandomAI extends AIPlayer{

    /**
     * Constructs a new `RandomAI` player, associated with a specific player position (Player 1 or Player 2).
     * @param isPlayerOne ---> a boolean indicating whether the AI player is Player 1 (true) or Player 2 (false).
     */
    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    /**
     * Makes a random move for the AI player.
     * @param gameStatus --->the current game state
     * @return ---> ?
     */

    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        return null;
    }
}
