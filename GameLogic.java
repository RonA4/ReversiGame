import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * The 'GameLogic' class represents the main logic for playing a board game, it implements the 'PlayableLogic' interface
 and provides the rules and methods needed to manage game play, including the game board, players and moves.
 * The game alternates between the two players, verifying moves, flipping discs and tracking the state of the game. It supports cancel functionality and checks to end the game when there are no valid moves left.
 * BOARD_SIZE ---> Sets the size of the board (8x8).
 * player1 and player2 ---> represent the two players in the game.
 * isFirstPlayerTurn ---> Player's turn
 * discs ---> a two-dimensional array representing the game board, where each element is a `disc' object.
 * moveStack ---> A stack that follows the sequence of moves made during the game.
 */

public class GameLogic implements PlayableLogic {
    private static final int BOARD_SIZE = 8;
    private Player player1;
    private Player player2;
    private boolean isFirstPlayerTurn = true;
    private Disc[][] discs = new Disc[getBoardSize()][getBoardSize()];
    private Stack<Move> moveStack;

    /**
     * Places a disc on the board, flips appropriate discs, and updates the game state.
     * This method handles special disc types like BombDisc and UnflippableDisc, manages resources, and tracks the move history.
     * @param a ---> The position for locating a new disc on the board.
     * @param disc ---> The disc to be placed.
     * @return ---> true if the move was successful, false otherwise.
     */
    @Override
    public boolean locate_disc(Position a, Disc disc) {
        List<Position> validMoves = ValidMoves();
        if (!validMoves.contains(a)) {
            return false;
        }
        if (disc instanceof BombDisc && disc.getOwner().getNumber_of_bombs() <= 0) {
            return false;
        }
        if (disc instanceof UnflippableDisc && disc.getOwner().getNumber_of_unflippedable() <= 0) {
            return false;
        }
        List<Position> positionsChanged = new ArrayList<>();
        List<Disc> discsPositionsChanged = new ArrayList<>();
        discs[a.row()][a.col()] = disc;
        int row = a.row();
        int col = a.col();
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                if (dRow == 0 && dCol == 0) continue;
                int neighborRow = row + dRow;
                int neighborCol = col + dCol;
                if (isInBounds(neighborRow, neighborCol)) {
                    Disc enemyDisc = discs[neighborRow][neighborCol];
                    if (isOppositePlayerDisc(enemyDisc)) {
                        Position enemyPosition = new Position(neighborRow, neighborCol);
                        List<Position> positions = findDiscsToFlip(enemyPosition, a);
                        if (positions != null) {
                            for (Position position : positions) {
                                Disc currentDisc = discs[position.row()][position.col()];
                                if (currentDisc instanceof BombDisc) {
                                    positionsChanged.add(position);
                                    discsPositionsChanged.add(currentDisc);
                                    discs[position.row()][position.col()] = new BombDisc(disc.getOwner());
                                } else if (currentDisc instanceof SimpleDisc) {
                                    positionsChanged.add(position);
                                    discsPositionsChanged.add(currentDisc);
                                    discs[position.row()][position.col()] = new SimpleDisc(disc.getOwner());
                                }
                            }
                        }
                    }
                }
            }
        }
        if (disc instanceof UnflippableDisc) {
            disc.getOwner().reduce_unflippedable();
        } else if (disc instanceof BombDisc) {
            disc.getOwner().reduce_bomb();
        }
        Move currentMove = new Move(disc, a, positionsChanged, discsPositionsChanged, getOppositePlayer());
        moveStack.push(currentMove);
        if (isGameFinished()) {
            if (isFirstPlayerTurn) {
                player1.addWin();
            } else {
                player2.addWin();
            }
        } else {
            isFirstPlayerTurn = !isFirstPlayerTurn;
        }
        System.out.println("Player " + getNumberOfCurrentPlayer() + " placed a " + disc.getType() + " in " + a.toString());
        return true;
    }
    /**
     * A function for the required prints according to the pdf document returns the player number.
     * @return ---> The number of player.
     */

    private String getNumberOfCurrentPlayer() {
        if (isFirstPlayerTurn) {
            return "1";
        } else {
            return "2";
        }
    }

    /**
     * Returns the disc at the specified position on the board.
     * @param position ---> The position for which to retrieve the disc.
     * @return ---> The disc at the given position, or null if there is no disc.
     */
    @Override
    public Disc getDiscAtPosition(Position position) {
        return discs[position.row()][position.col()];
    }

    /**
     * Returns the size of the game board.
     * @return ---> the number of rows and columns in the board.
     */
    @Override
    public int getBoardSize() {
        return BOARD_SIZE;
    }

    /**
     * Checks if the given disc belongs to the opposite player.
     * @param disc --->  The disc to check.
     * @return ---> true if the disc belongs to the opposite player, false otherwise.
     */
    private boolean isOppositePlayerDisc(Disc disc) {
        if (disc == null) {
            return false;
        }
        return isFirstPlayerTurn && !disc.getOwner().isPlayerOne() || !isFirstPlayerTurn && disc.getOwner().isPlayerOne();
    }

    /**
     * Checks if the given disc belongs to the current player.
     * @param disc ---> The disc to check.
     * @return ---> true if the disc belongs to the current player, false otherwise.
     */
    private boolean isCurrentPlayerDisc(Disc disc) {
        if (disc == null) {
            return false;
        }
        return isFirstPlayerTurn && disc.getOwner().isPlayerOne() || !isFirstPlayerTurn && !disc.getOwner().isPlayerOne();
    }

    /**
     * Calculates the horizontal offset (difference in row) between the enemy's position and the current player's option position.
     * @param enemyPosition --->the position of the enemy disc.
     * @param currentPlayerPositionOption --->  the current player's potential move position.
     * @return ---> the difference in row between the two positions.
     */
    private int getXOffset(Position enemyPosition, Position currentPlayerPositionOption) {
        return enemyPosition.row() - currentPlayerPositionOption.row();
    }

    /**
     * Calculates the vertical offset (difference in column) between the enemy's position and the current player's option position.
     * @param enemyPosition ---> the position of the enemy disc.
     * @param currentPlayerPositionOption ---> the current player's potential move position.
     * @return ---> the difference in col between the two positions.
     */

    private int getYOffset(Position enemyPosition, Position currentPlayerPositionOption) {
        return enemyPosition.col() - currentPlayerPositionOption.col();
    }

    /**
     * Determines all discs that can be flipped between the given enemy position and the current player position based on the direction of play.
     * This method checks the positions between the given 'enemyPosition' and 'currentPlayerPositionOption'
     * To see if there are any opponent's discs that can be flipped by placing a disc at the current player's position.
     * The flippable positions are added to the list and returned.
     * @param enemyPosition ---> the location of the enemy's disk that may be flipped.
     * @param currentPlayerPositionOption ---> The current position option where the player is considering placing their disc.
     * @return ---> a list of positions of the opponent's disks that can be flipped between `enemy Position' and 'currentPlayerPositionOption'.
     */
    private List<Position> getAllFLippedFromPosition(Position enemyPosition, Position currentPlayerPositionOption) {
        if (!isInBounds(currentPlayerPositionOption.row(), currentPlayerPositionOption.col())) {
            return null;
        }
        List<Position> result = new ArrayList<>();
        int xOffset = getXOffset(enemyPosition, currentPlayerPositionOption);
        int yOffset = getYOffset(enemyPosition, currentPlayerPositionOption);
        int startXPosition = currentPlayerPositionOption.row() + 2 * xOffset;
        int startYPosition = currentPlayerPositionOption.col() + 2 * yOffset;
        result.add(enemyPosition);
        Position optinalPlacePosition = new Position(startXPosition, startYPosition);
        while (isInBounds(optinalPlacePosition)) {
            Disc disc = discs[optinalPlacePosition.row()][optinalPlacePosition.col()];
            if (disc == null) {
                return result;
            } else if (isCurrentPlayerDisc(disc)) {
                return null;
            } else if (isOppositePlayerDisc(disc)) {
                if (!(disc instanceof UnflippableDisc)) {
                    result.add(optinalPlacePosition);
                }
                optinalPlacePosition = new Position(optinalPlacePosition.row() + xOffset, optinalPlacePosition.col() + yOffset);
            }
        }
        return null;
    }

    /**
     * Finds all valid moves for the current player.
     * A legal move is a move where the current player can place a disc on the board so that at least one of the opponent's discs is turned over as a result.
     * The method iterates through all the positions in the board and checks each one to see if
     * It could be a target for a legal move. For each potential target location, it checks all 8 directions around all enemy discs. If there is a legal move, the target location
       calculated and added to the list of valid moves.
     * @return ---> A list of positions where the current player can place a flip disc
     At least one of the opponent's disks. If no valid moves are found, An empty list is returned.
     */
    @Override
    public List<Position> ValidMoves() {
        List<Position> result = new ArrayList<>();
        for (int row = 0; row < getBoardSize(); row++) {
            for (int col = 0; col < getBoardSize(); col++) {
                Disc enemyDisc = discs[row][col];
                if (enemyDisc != null && !(enemyDisc instanceof UnflippableDisc) && isOppositePlayerDisc(enemyDisc)) {
                    for (int dRow = -1; dRow <= 1; dRow++) {
                        for (int dCol = -1; dCol <= 1; dCol++) {
                            if (dRow == 0 && dCol == 0) continue;
                            int neighborRow = row + dRow;
                            int neighborCol = col + dCol;
                            if (!isInBounds(new Position(neighborRow, neighborCol))) {
                                continue;
                            }
                            Disc currentPlayerDisc = discs[neighborRow][neighborCol];
                            if (isCurrentPlayerDisc(currentPlayerDisc)) {
                                Position enemyPosition = new Position(row, col);
                                Position currentPlayerPosition = new Position(neighborRow, neighborCol);
                                List<Position> allPositionThatCanBeFlipped = getAllFLippedFromPosition(enemyPosition, currentPlayerPosition);
                                if (allPositionThatCanBeFlipped != null && allPositionThatCanBeFlipped.size() > 0) {
                                    int xOffset = getXOffset(enemyPosition, currentPlayerPosition);
                                    int yOffset = getYOffset(enemyPosition, currentPlayerPosition);
                                    int xTargetPosition = currentPlayerPosition.row() + (xOffset * (allPositionThatCanBeFlipped.size() + 1));
                                    int yTargetPosition = currentPlayerPosition.col() + (yOffset * (allPositionThatCanBeFlipped.size() + 1));
                                    result.add(new Position(xTargetPosition, yTargetPosition));
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
    /**
     * Checks if the given row and column are within the valid bounds of the game board (8x8).
     * @param row ---> the row index to check.
     * @param col ---> the column index to check.
     * @return ---> true if the row and column are within bounds (0 to 7), otherwise false.
     */
    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /**
     * Checks if the given position is within the valid bounds of the game board (8x8).
     * @param position --->position the position object containing the row and column to check.
     * @return ---> true if the position is within bounds (0 to 7 for both row and column), otherwise false.
     */

    private boolean isInBounds(Position position) {
        return isInBounds(position.row(), position.col());
    }

    /**
     *Recursively retrieves all positions affected by a bomb explosion reaction.
     *The method explores neighboring positions, identifying discs that should be affected,
     * including triggering further reactions if another bomb is encountered.
     * @param bombPosition --->  The initial position of the bomb that triggered the reaction.
     * @param checkedPositions --->  A list of positions already checked to avoid infinite loops.
     * @return ---> A list of all positions affected by the bomb reaction.
     */

    private List<Position> getAllPositionByBombReaction(Position bombPosition, List<Position> checkedPositions) {
        //checkedPositions.add(bombPosition);
        List<Position> result = new ArrayList<>();
        int row = bombPosition.row();
        int col = bombPosition.col();
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                if (dRow == 0 && dCol == 0) continue;
                int neighborRow = row + dRow;
                int neighborCol = col + dCol;
                Position neighbourPosition = new Position(neighborRow, neighborCol);
                if (checkedPositions.contains(neighbourPosition)) {
                    continue;
                }
                if (isInBounds(neighborRow, neighborCol)) {
                    Disc enemyDisc = discs[neighborRow][neighborCol];
                    if (isOppositePlayerDisc(enemyDisc)) {
                        if (enemyDisc instanceof SimpleDisc) {
                            result.add(new Position(neighborRow, neighborCol));
                        } else if (enemyDisc instanceof BombDisc) {
                            List<Position> positions = getAllPositionByBombReaction(neighbourPosition, checkedPositions);
                            if (positions.size() > 0) {
                                result.addAll(positions);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Identifies the sequence of opponent's discs that would have been turned if the current player
     * places a disk in the specified location, in the direction defined by the given locations.
     * This method verifies if the move is legal ie :
       It starts from the opponent's puck at 'enemyPosition' and moves in the direction
       ahead of the next potential drives, a streak check of an opponent's drives ends
       with a disc of the current player. If such a sequence exists, it collects all positions of the opponent's disks to be turned over.
     * @param enemyPosition ---> places the opponent's disc position near the potential move.
     * @param currentPlayerPositionOption ---> position where the current player weighs.
     * @return ---> A list of Position objects representing the opponent's disks.
     */
    private List<Position> findDiscsToFlip(Position enemyPosition, Position currentPlayerPositionOption) {
        List<Position> result = new ArrayList<>();
        int xOffset = getXOffset(enemyPosition, currentPlayerPositionOption);
        int yOffset = getYOffset(enemyPosition, currentPlayerPositionOption);
        int startXPosition = currentPlayerPositionOption.row() + 2 * xOffset;
        int startYPosition = currentPlayerPositionOption.col() + 2 * yOffset;
        result.add(enemyPosition);
        Disc enemyDisc = discs[enemyPosition.row()][enemyPosition.col()];
        if (enemyDisc instanceof BombDisc) {
            List<Position> bombReactionPositions = getAllPositionByBombReaction(enemyPosition, new ArrayList<>());
            if (bombReactionPositions.size() > 0) {
                result.addAll(bombReactionPositions);
            }
        }
        Position optinalPlacePosition = new Position(startXPosition, startYPosition);
        while (isInBounds(optinalPlacePosition)) {
            Disc disc = discs[optinalPlacePosition.row()][optinalPlacePosition.col()];
            if (disc == null) {
                //cannot happen
                return null;
            } else if (isCurrentPlayerDisc(disc)) {
                return result;
            } else if (isOppositePlayerDisc(disc)) {
                if (!(disc instanceof UnflippableDisc)) {
                    result.add(optinalPlacePosition);
                }
                if (disc instanceof BombDisc) {
                    List<Position> bombReactionPositions = getAllPositionByBombReaction(optinalPlacePosition, new ArrayList<>());
                    if (bombReactionPositions.size() > 0) {
                        result.addAll(bombReactionPositions);
                    }
                }
                optinalPlacePosition = new Position(optinalPlacePosition.row() + xOffset, optinalPlacePosition.col() + yOffset);
            }
        }
        return null;
    }

    /**
     * Count the total number of opponent discs that would have been turned over if the current player placed a disc in the given position.
     * This method repeats in all possible directions (horizontal, vertical and diagonal) from the specified location. For each direction, it checks for valid sequences of
       Opponent's discs that can be kicked, based on the rules of the game.
     *The method uses the following helper function "findDiscsToFlip" to determine the reversible discs
       in any direction. If no valid sequence is found in a particular direction, it skips that direction.
     * @param a ---> The position where the current player is considering placing his disc.
     * @return ---> The total number of disks to flip if the move is valid.
     */
    @Override
    public int countFlips(Position a) {
        int result = 0;
        int row = a.row();
        int col = a.col();
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                if (dRow == 0 && dCol == 0) continue;
                int neighborRow = row + dRow;
                int neighborCol = col + dCol;
                if (isInBounds(neighborRow, neighborCol)) {
                    Disc enemyDisc = discs[neighborRow][neighborCol];
                    if (isOppositePlayerDisc(enemyDisc)) {
                        Position enemyPosition = new Position(neighborRow, neighborCol);
                        List<Position> positions = findDiscsToFlip(enemyPosition, a);
                        if (positions != null ) {
                            result += positions.size();
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Gets the first player.
     * @return ---> the first player (player1).
     */
    @Override
    public Player getFirstPlayer() {
        return this.player1;
    }
    /**
     * Gets the second player.
     * @return ---> the second player (player2).
     */
    @Override
    public Player getSecondPlayer() {
        return this.player2;
    }
    /**
     * Sets the two players for the game.
     *
     * @param ---> player1 the first player to be set.
     * @param ---> player2 the second player to be set.
     */
    @Override
    public void setPlayers(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }
    /**
     * Checks if it's the first player's turn.
     * @return ---> true if it's the first player's turn, otherwise false.
     */
    @Override
    public boolean isFirstPlayerTurn() {
        return isFirstPlayerTurn;
    }

    /**
     * Checks if the game is over.
     * The game ends when both players have no legal moves to make.
     * @return ---> rue if the game is over (ie, both players have no valid moves), false otherwise.
     */
    @Override
    public boolean isGameFinished() {
        return ValidMoves().isEmpty();
    }

    /**
     * Resets the game board to its initial state, preparing it for a new game.
     * This method performs the following actions:
     -Initializes the game board (`discs`) as an empty 2D array with the correct size.
     -Clears the stack of moves (`moveStack`) to remove any previous game history.
      -Places the initial discs on the board:
     * Two discs for Player 1 at positions ---> (3, 3) and (4, 4).
     * Two discs for Player 2 at positions ---> (3, 4) and (4, 3).
     */

    @Override
    public void reset() {
        discs = new Disc[getBoardSize()][getBoardSize()];
        moveStack = new Stack<>();
        //player 1
        discs[3][3] = new SimpleDisc(player1);
        discs[4][4] = new SimpleDisc(player1);
        //player 2
        discs[3][4] = new SimpleDisc(player2);
        discs[4][3] = new SimpleDisc(player2);
    }

    /**
     * Returns the last move made in the game, restoring the state of the board to its previous configuration.
     *  This method removes the disk placed during the last move and regains ownership of all disks
        that were overturned as a result of this move. It also returns the turn to the previous player.
     * This method assumes that the game tracks moves using a stack (`moveStack`) and that each move contains the following details:
     -The position of the placed disc
     -A list of all the positions of the flipped discs
     -The player who performed the move.
     */
    @Override
    public void undoLastMove() {
        if (!moveStack.isEmpty()) {
            System.out.println("Undoing last move :");
            Move lastMove = moveStack.pop();
            Position placedPosition = lastMove.getNewDiscPlacedPosition();
            discs[placedPosition.row()][placedPosition.col()] = null;
            System.out.printf("\tUndo: removing %s from (%d, %d)%n", lastMove.newDiscPlaced().getType(), placedPosition.row(), placedPosition.col());
            List<Position> positions = lastMove.getDiscsChangedColorPosition();
            List<Disc> discPositions = lastMove.getDiscsChangedColor();
            for (int i = 0; i < positions.size(); i++) {
                Position currentPosition = positions.get(i);
                Disc currentDisc = discPositions.get(i);
                discs[currentPosition.row()][currentPosition.col()] = discPositions.get(i);
                System.out.printf("\tUndo: flipping back %s in (%d, %d)%n", currentDisc.getType(), currentPosition.row(), currentPosition.col());
                if (currentDisc instanceof UnflippableDisc) {
                    currentDisc.getOwner().number_of_unflippedable++;
                } else if (currentDisc instanceof BombDisc) {
                    currentDisc.getOwner().number_of_bombs++;
                }
            }
            isFirstPlayerTurn = !isFirstPlayerTurn;
        } else {
            System.out.println("\tNo previous move available to undo .");

        }
    }


    /**
     * Retrieves the player whose turn is currently inactive.
     * This method determines the opposite player based on the value of isFirstPlayerTurn
     * @return --->
     * If it is the first player's turn, the second player is returned.
     * If it is not the first player's turn, the first player is returned.
     */
    private Player getOppositePlayer() {
        return isFirstPlayerTurn ? player2 : player1;
    }

    public void setDiscs(Disc[][] discs) {
       this.discs=discs;
    }

    public void setCurrentPlayer(Player player1) {
        this.player1=player1;
    }
}
