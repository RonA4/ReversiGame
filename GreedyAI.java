/**
 * Represents an AI player making the "greedy" move in the game.
 * The `GreedyAI` class extends the `AIPlayer` class and provides a basic implementation
 * For an AI player who will ideally make moves to maximize their immediate profit in the game.
 */
public class GreedyAI extends AIPlayer{

    /**
     * Constructs a new `GreedyAI` player, associated with a specific player position (Player 1 or Player 2).
     * @param isPlayerOne ---> isPlayerOne a boolean indicating whether the AI player is Player 1 (true) or Player 2 (false).
     */
    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    /**
     * Makes a greedy move for the AI player, where the AI selects the move that maximizes its advantage.
     * @param gameStatus --->  the current game state (includes the game board, current player)
     * @return ---> ?
     */
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        return null;
    }
}
