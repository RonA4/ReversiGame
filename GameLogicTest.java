import org.junit.Test;

import static org.junit.Assert.*;

public class GameLogicTest {
    private HumanPlayer player1 = new HumanPlayer(true);
    private HumanPlayer player2 = new HumanPlayer(false);


    @Test
    public void countFlips() {
        GameLogic gameLogic = new GameLogic();
        Disc[][] discs = new Disc[8][8];
        gameLogic.reset();
        gameLogic.setDiscs(discs);
        HumanPlayer player1 = new HumanPlayer(true);
        discs[4][2] = new SimpleDisc(player1);
        discs[4][3] = new SimpleDisc(player1);
        discs[4][4] = new SimpleDisc(player1);
        HumanPlayer player2 = new HumanPlayer(false);
        discs[3][2] = new SimpleDisc(player2);
        discs[3][3] = new SimpleDisc(player2);
        discs[3][4] = new SimpleDisc(player2);
        gameLogic.setCurrentPlayer(player1);
        Position a = new Position(2, 2);
        int test = gameLogic.countFlips(a);
        assertEquals(2, test);

    }
    }