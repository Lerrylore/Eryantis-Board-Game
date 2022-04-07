package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests Game class using getter method provided by Game class.
 *
 * @author Dario d'Abate
 */

class GameTest {
    Game g;

    @BeforeEach
    void setup() {
        //first player choose player's number
        g = new Game("Dario", 3);
    }

    /**
     * This method tests addPlayer() adding more players than the
     * established number.
     * The player list should only contain players added when the
     * set number had not yet been reached
     */
    @DisplayName("Adding more players than set number test")
    @Test
    void addingTooPlayer() {
        assertEquals(1, g.getNumPlayers());
        g.addPlayer("Lorenzo");
        g.addPlayer("Luca");
        assertEquals(3, g.getNumPlayers());
        g.addPlayer("Matteo");
        assertEquals(3, g.getNumPlayers());
    }

    /**
     * This method tests adding a player with empty string as nickname
     *
     * @throws IllegalStateException when empty string is passed as nickname's player
     */
    @DisplayName("Adding a player with empty nickname test ")
    @Test
    void addEmptyPlayer() {
        assertThrows(IllegalArgumentException.class,
                () -> g.addPlayer(""));
        assertEquals(1, g.getNumPlayers());
    }

    /**
     * This method tests starting a game when there are missing player
     *
     * @throws IllegalStateException when required number of players has not been reached
     */
    @DisplayName("Starting a game without reaching required number of players test")
    @Test
    void illegalStarting() {
        assertThrows(IllegalStateException.class,
                () -> g.startGame());
    }

    //helper method for other tests method that adds 2
    //players
    void setupFullPlayer() {
        g.addPlayer("Lorenzo");
        g.addPlayer("Luca");
    }

    /**
     * This method tests the setup for a set of clouds
     */
    @Test
    @DisplayName("Init cloud tiles test")
    void checkCloudInit() {
        setupFullPlayer();
        assertEquals(3, g.getNumPlayers());
        g.startGame();

        //clouds initially empty
        for (CloudTile c : g.getCloudTiles()) {
            assertEquals(0, c.numStudOn());
        }

    }

    /**
     * This method tests if a player's entrance has been filled with students
     */
    @Test
    @DisplayName("Filling entrance test")
    void checkEntrancePlayer() {
        setupFullPlayer();
        g.startGame();

        for (Player p : g.getPlayers()) {
            assertFalse(p.getBoard().entranceIsFillable());
        }

    }


    /**
     * This method tests if mother nature was initialised within the archipelago
     */
    @Test
    @DisplayName("Mother nature init test")
    void checkInitMotherNature() {
        setupFullPlayer();
        g.startGame();
        assertTrue(g.getMotherNature() < 12 && g.getMotherNature() >= 0);
    }

    /**
     * This method tests if all the island tiles has one student, except that one with mother nature and the
     * tile at its opposite
     */
    @Test
    @DisplayName("Init tiles with students test")
    void checkStudentOnIsland() {
        setupFullPlayer();
        g.startGame();
        for (int i = 0; i < 12; i++) {
            if (i == g.getMotherNature() || i == (g.getMotherNature() + 6) % 12)
                assertEquals(0, g.getArchipelago().get(i).getIslandStudents().numStudents());
            else
                assertEquals(1, g.getArchipelago().get(i).getIslandStudents().numStudents());
        }
    }

    /**
     * This method tests the mechanism of refilling cloud tiles
     * when all the cloud tiles are empty
     */
    @Test
    @DisplayName("Refill empty cloud tile test")
    void bagToCloudEmpty() {
        setupFullPlayer();
        g.startGame();

        for (CloudTile cloudTile : g.getCloudTiles())
            assertTrue(cloudTile.isEmpty());

        g.bagToClouds();
        for (CloudTile cloudTile : g.getCloudTiles())
            assertFalse(cloudTile.isFillable());
    }

    /**
     * This method tests the mechanism of refilling cloud tiles
     * when  the cloud tiles are not empty
     *
     * @throws IllegalStateException when a not empty cloud tile is
     *                               filled with students
     */
    @DisplayName("Refill not empty cloud tile test")
    @Test
    void bagToCloudNotEmpty() {
        setupFullPlayer();
        g.startGame();

        g.getCloudTiles().get(2).fill(Color.YELLOW);
        assertThrows(IllegalStateException.class,
                () -> g.bagToClouds());

    }

    /**
     * This method test the mechanism of refilling the current player's board
     * with students on a specified cloud tile when all the movable students in the
     * board have been moved and the current players has chosen an existent cloud tile
     */
    @DisplayName("Refill current player board's entrance with students on cloud, normal condition test")
    @Test
    void cloudToBoard() {
        setupFullPlayer();
        g.startGame();
        g.bagToClouds();
        //all clouds filled

        Board boardCurrentPlayer = g.getCurrentPlayer().getBoard();
        assertFalse(boardCurrentPlayer.entranceIsFillable());
        int i = 0;
        for (Color color : Color.values()) {
            while (boardCurrentPlayer.studentInEntrance(color) && i < 4) {
                boardCurrentPlayer.removeStudentFromEntrance(color);
                ++i;
            }
        }

        assertTrue(boardCurrentPlayer.entranceIsFillable());
        g.cloudToBoard(0); //using a stub in game that returns the first player in the list
        assertFalse(boardCurrentPlayer.entranceIsFillable());

    }

    /**
     * This method test the mechanism of refilling the current player's board
     * with students on a specified cloud tile when all the movable students in the
     * board have not been moved and the current players has chosen an existent cloud tile
     */
    @DisplayName("Refill current player board's entrance with students on cloud, entrance not fillable test")
    @Test
    void cloudToBoard2() {
        setupFullPlayer();
        g.startGame();
        g.bagToClouds();
        //all clouds filled

        Board boardCurrentPlayer = g.getCurrentPlayer().getBoard();
        assertFalse(boardCurrentPlayer.entranceIsFillable());
        int i = 0;
        for (Color color : Color.values()) {
            while (boardCurrentPlayer.studentInEntrance(color) && i < 3) {
                boardCurrentPlayer.removeStudentFromEntrance(color);
                ++i;
            }
        }

        assertTrue(boardCurrentPlayer.entranceIsFillable());
        assertThrows(IllegalStateException.class, () -> g.cloudToBoard(0));


    }

    /**
     * This method test the mechanism of refilling the current player's board
     * with students on a specified cloud tile when all the movable students in the
     * board have been moved and the current players has chosen a not existent cloud tile
     */
    @DisplayName("Refill current player board's entrance with students on not existing cloud test")
    @Test
    void cloudToBoard3() {
        setupFullPlayer();
        g.startGame();
        g.bagToClouds();
        //all clouds filled

        Board boardCurrentPlayer = g.getCurrentPlayer().getBoard();
        assertFalse(boardCurrentPlayer.entranceIsFillable());
        int i = 0;
        for (Color color : Color.values()) {
            while (boardCurrentPlayer.studentInEntrance(color) && i < 4) {
                boardCurrentPlayer.removeStudentFromEntrance(color);
                ++i;
            }
        }

        assertTrue(boardCurrentPlayer.entranceIsFillable());
        assertThrows(IndexOutOfBoundsException.class, () -> g.cloudToBoard(3));


    }
}