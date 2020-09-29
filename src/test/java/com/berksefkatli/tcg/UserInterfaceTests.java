package com.berksefkatli.tcg;

import com.berksefkatli.tcg.exception.TcgException.CannotPlayCardNotInHandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class UserInterfaceTests {

    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;
    private PrintStream out;
    private PrintStream err;

    @BeforeEach
    public void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        out = new PrintStream(outContent);
        err = new PrintStream(errContent);
    }

    @Test
    public void when_invalidChoiceInMainMenu_expect_showErrorAndRetry() {
        String stringBuilder = "invalidChoice" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());
        UserInterface.mainMenu(in, out, err);

        assertTrue(errContent.toString().contains("Please enter a valid option."));
        assertTrue(outContent.toString().contains("Start game"));
    }

    @Test
    public void when_invalidChoiceInGameplay_expect_showErrorAndRetry() {
        String stringBuilder = "1" + System.lineSeparator() +
                "invalidChoice" + System.lineSeparator() +
                "quit" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(in, out, err);

        assertTrue(errContent.toString().contains("Please enter a card's cost value, 'end' or 'quit'"));
    }

    @Test
    public void when_invalidChoiceInCustomizeConfigMenu_expect_showErrorAndRetry() {
        String stringBuilder = "2" + System.lineSeparator() +
                "invalidChoice" + System.lineSeparator() +
                "9" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(in, out, err);

        assertTrue(errContent.toString().contains("Please enter a valid option."));
        assertTrue(outContent.toString().contains("Current game settings:"));
    }

    @Test
    public void when_invalidIntegerInCustomizeConfigMenu_expect_showErrorAndRetry() {
        String stringBuilder = "2" + System.lineSeparator() +
                "3" + System.lineSeparator() +
                "notAnInteger" + System.lineSeparator() +
                "9" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(in, out, err);

        assertTrue(errContent.toString().contains("Please enter a valid integer"));
        assertTrue(outContent.toString().contains("Current game settings:"));
    }

    @Test
    public void when_setHealthToLessThan1InCustomizeConfigMenu_expect_showErrorAndRetry() {
        String stringBuilder = "2" + System.lineSeparator() +
                "3" + System.lineSeparator() +
                "0" + System.lineSeparator() +
                "9" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(in, out, err);

        assertTrue(errContent.toString().contains("Initial health cannot be less than 1"));
        assertTrue(outContent.toString().contains("Current game settings:"));
    }

    @Test
    public void when_gameStart_expect_printGameState() {
        String stringBuilder = "1" + System.lineSeparator() +
                "quit" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(in, out, err);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Name: "));
        assertTrue(outContent.toString().contains("Health: "));
        assertTrue(outContent.toString().contains("Mana: "));
        assertTrue(outContent.toString().contains("Mana capacity: "));
        assertTrue(outContent.toString().contains("Cards in hand: "));
        assertTrue(outContent.toString().contains("Active player: "));
        assertTrue(outContent.toString().contains("Active player's hand: "));
    }

    @Test
    public void when_playingCardNotInHand_expect_cardNotInHandError() {
        String stringBuilder = "1" + System.lineSeparator() +
                "20" + System.lineSeparator() +
                "quit" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(in, out, err);

        assertTrue(errContent.toString().contains(CannotPlayCardNotInHandException.message));
    }

    @Test
    public void when_skipEveryTurn_expect_bleedingOutVictory() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1").append(System.lineSeparator());

        // High enough number to pass every turn, we don't care about the invalid option errors here.
        for (int i = 0; i < 100; i++) {
            stringBuilder.append("end").append(System.lineSeparator());
        }

        stringBuilder.append("3").append(System.lineSeparator());

        InputStream in = new ByteArrayInputStream(stringBuilder.toString().getBytes());

        UserInterface.mainMenu(in, out, err);

        assertTrue(outContent.toString().contains("'s turn ended"));
        assertTrue(outContent.toString().contains("has lost!"));
        assertTrue(outContent.toString().contains("has won!"));
    }

    @Test
    public void when_setPlayers_expect_printPlayerNames() {
        String stringBuilder = "2" + System.lineSeparator() +
                "1" + System.lineSeparator() +
                "Rahmi, Berk" + System.lineSeparator() +
                "9" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(in, out, err);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Players: Berk, Rahmi"));
    }

    @Test
    public void when_setDeck_expect_printDeck() {
        String stringBuilder = "2" + System.lineSeparator() +
                "2" + System.lineSeparator() +
                "0, 3, 2, 5, 6, 4, 7" + System.lineSeparator() +
                "9" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(in, out, err);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Deck: 0, 2, 3, 4, 5, 6, 7"));
    }

    @Test
    public void when_setInitialHealth_expect_printInitialHealth() {
        String stringBuilder = "2" + System.lineSeparator() +
                "3" + System.lineSeparator() +
                "20" + System.lineSeparator() +
                "9" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(in, out, err);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Initial health: 20"));
    }

    @Test
    public void when_setInitialManaCapacity_expect_printInitialManaCapacity() {
        String stringBuilder = "2" + System.lineSeparator() +
                "4" + System.lineSeparator() +
                "5" + System.lineSeparator() +
                "9" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(in, out, err);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Initial mana capacity: 5"));
    }

    @Test
    public void when_setInitialHandSize_expect_printInitialHandSize() {
        String stringBuilder = "2" + System.lineSeparator() +
                "5" + System.lineSeparator() +
                "2" + System.lineSeparator() +
                "9" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(in, out, err);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Initial hand size: 2"));
    }

    @Test
    public void when_setMaxManaCapacity_expect_printMaxManaCapacity() {
        String stringBuilder = "2" + System.lineSeparator() +
                "6" + System.lineSeparator() +
                "25" + System.lineSeparator() +
                "9" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(in, out, err);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Max mana capacity: 25"));
    }

    @Test
    public void when_setMaxHandSize_expect_printMaxHandSize() {
        String stringBuilder = "2" + System.lineSeparator() +
                "7" + System.lineSeparator() +
                "2" + System.lineSeparator() +
                "9" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(in, out, err);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Max hand size: 2"));
    }

    @Test
    public void when_setBleedingDamageAmount_expect_printBleedingDamageAmount() {
        String stringBuilder = "2" + System.lineSeparator() +
                "8" + System.lineSeparator() +
                "3" + System.lineSeparator() +
                "9" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(in, out, err);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Bleeding damage amount: 3"));
    }

}
