package com.berksefkatli.tcg;

import com.berksefkatli.tcg.Game.CannotPlayCardNotInHandException;
import com.berksefkatli.tcg.Game.NonExistentPlayerException;
import com.berksefkatli.tcg.Game.NotEnoughPlayersException;
import com.berksefkatli.tcg.Game.UniquePlayerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class TcgTests {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void when_addingNewPlayer_expect_newPlayerName() {
        String stringBuilder = "1" + System.lineSeparator() +
                "Rahmi" + System.lineSeparator() +
                "quit" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());
        System.setIn(in);

        Tcg.main(null);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Current players: Rahmi"));
    }

    @Test
    public void when_addingDuplicatePlayer_expect_duplicateError() {
        String stringBuilder = "1" + System.lineSeparator() +
                "Rahmi" + System.lineSeparator() +
                "1" + System.lineSeparator() +
                "Rahmi" + System.lineSeparator() +
                "quit" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());
        System.setIn(in);

        Tcg.main(null);

        assertTrue(errContent.toString().contains(UniquePlayerException.message));
    }

    @Test
    public void when_removingPlayer_expect_noPlayerName() {
        String stringBuilder = "1" + System.lineSeparator() +
                "Rahmi" + System.lineSeparator() +
                "1" + System.lineSeparator() +
                "Berk" + System.lineSeparator() +
                "2" + System.lineSeparator() +
                "Rahmi" + System.lineSeparator() +
                "quit" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());
        System.setIn(in);

        Tcg.main(null);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Current players: Berk"));
    }

    @Test
    public void when_removingNonExistentPlayer_expect_nonExistentPlayerError() {
        String stringBuilder = "2" + System.lineSeparator() +
                "NonExistentPlayer" + System.lineSeparator() +
                "quit" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());
        System.setIn(in);

        Tcg.main(null);

        assertTrue(errContent.toString().contains(NonExistentPlayerException.message));
    }

    @Test
    public void when_tryingToStartWith1Player_expect_notEnoughPlayersError() {
        String stringBuilder = "1" + System.lineSeparator() +
                "Rahmi" + System.lineSeparator() +
                "3" + System.lineSeparator() +
                "quit" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());
        System.setIn(in);

        Tcg.main(null);

        assertTrue(errContent.toString().contains(NotEnoughPlayersException.message));
    }

    @Test
    public void when_invalidChoice_expect_retry() {
        String stringBuilder = "invalidChoice" + System.lineSeparator() +
                "1" + System.lineSeparator() +
                "Rahmi" + System.lineSeparator() +
                "quit" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());
        System.setIn(in);

        Tcg.main(null);

        assertTrue(errContent.toString().contains("Please enter a valid option."));
        assertTrue(outContent.toString().contains("Current players: Rahmi"));
    }

    @Test
    public void when_gameStart_expect_printGameState() {
        String stringBuilder = "1" + System.lineSeparator() +
                "Rahmi" + System.lineSeparator() +
                "1" + System.lineSeparator() +
                "Berk" + System.lineSeparator() +
                "3" + System.lineSeparator() +
                "quit" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());
        System.setIn(in);

        Tcg.main(null);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Current players: Rahmi, Berk"));
        assertTrue(outContent.toString().contains("Health: "));
        assertTrue(outContent.toString().contains("Mana: "));
        assertTrue(outContent.toString().contains("ManaSlots: "));
        assertTrue(outContent.toString().contains("CardsInHand: "));
    }

    @Test
    public void when_invalidGameplayChoice_expect_invalidChoiceError() {
        String stringBuilder = "1" + System.lineSeparator() +
                "Rahmi" + System.lineSeparator() +
                "1" + System.lineSeparator() +
                "Berk" + System.lineSeparator() +
                "3" + System.lineSeparator() +
                "invalidChoice" + System.lineSeparator() +
                "quit" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());
        System.setIn(in);

        Tcg.main(null);

        assertTrue(errContent.toString().contains("Please enter a valid integer, 'end' or 'quit'"));
    }

    @Test
    public void when_playingCardNotInHand_expect_cardNotInHandError() {
        String stringBuilder = "1" + System.lineSeparator() +
                "Rahmi" + System.lineSeparator() +
                "1" + System.lineSeparator() +
                "Berk" + System.lineSeparator() +
                "3" + System.lineSeparator() +
                "20" + System.lineSeparator() +
                "quit" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());
        System.setIn(in);

        Tcg.main(null);

        assertTrue(errContent.toString().contains(CannotPlayCardNotInHandException.message));
    }

    @Test
    public void when_skipEveryTurn_expect_bleedingOutVictory() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1").append(System.lineSeparator())
                .append("Rahmi").append(System.lineSeparator())
                .append("1").append(System.lineSeparator())
                .append("Berk").append(System.lineSeparator())
                .append("3").append(System.lineSeparator());

        for (int i = 0; i < 200; i++) {
            stringBuilder.append("end").append(System.lineSeparator());
        }

        InputStream in = new ByteArrayInputStream(stringBuilder.toString().getBytes());
        System.setIn(in);

        Tcg.main(null);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("'s turn ended"));
        assertTrue(outContent.toString().contains("has lost!"));
        assertTrue(outContent.toString().contains("has won!"));
    }

}
