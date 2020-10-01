package com.berksefkatli.tcg;

import com.berksefkatli.tcg.exception.TcgException.CannotPlayCardNotInHandException;
import com.berksefkatli.tcg.model.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertTrue;


class UserInterfaceTests {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TEST_CONFIG_FILE_PATH = "src/test/resources/testConfig.json";
    private static final String CORRUPTED_TEST_CONFIG_FILE_PATH = "src/test/resources/testConfigCorrupted.json";
    private static final String INVALID_TEST_CONFIG_FILE_PATH = "src/test/resources/testConfigInvalid.json";

    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;
    private PrintStream out;
    private PrintStream err;

    @BeforeEach
    public void setUpStreams() throws IOException {
        objectMapper.writeValue(new File(TEST_CONFIG_FILE_PATH), new Config());
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        out = new PrintStream(outContent);
        err = new PrintStream(errContent);
    }

    @Test
    void when_invalidChoiceInMainMenu_expect_showErrorAndRetry() {
        String stringBuilder = "invalidChoice" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());
        UserInterface.mainMenu(TEST_CONFIG_FILE_PATH, in, out, err);

        assertTrue(errContent.toString().contains("Please enter a valid option."));
        assertTrue(outContent.toString().contains("Start game"));
    }

    @Test
    void when_invalidChoiceInGameplay_expect_showErrorAndRetry() {
        String stringBuilder = "1" + System.lineSeparator() +
                "invalidChoice" + System.lineSeparator() +
                "quit" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(TEST_CONFIG_FILE_PATH, in, out, err);

        assertTrue(errContent.toString().contains("Please enter a card's cost value, 'end' or 'quit'"));
    }

    @Test
    void when_invalidChoiceInCustomizeConfigMenu_expect_showErrorAndRetry() {
        String stringBuilder = "2" + System.lineSeparator() +
                "invalidChoice" + System.lineSeparator() +
                "10" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(TEST_CONFIG_FILE_PATH, in, out, err);

        assertTrue(errContent.toString().contains("Please enter a valid option."));
        assertTrue(outContent.toString().contains("Current game settings:"));
    }

    @Test
    void when_invalidIntegerInCustomizeConfigMenu_expect_showErrorAndRetry() {
        String stringBuilder = "2" + System.lineSeparator() +
                "3" + System.lineSeparator() +
                "notAnInteger" + System.lineSeparator() +
                "10" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(TEST_CONFIG_FILE_PATH, in, out, err);

        assertTrue(errContent.toString().contains(UserInterface.INTEGER_PROMPT));
        assertTrue(outContent.toString().contains("Current game settings:"));
    }

    @Test
    void when_setHealthToLessThan1InCustomizeConfigMenu_expect_showErrorAndRetry() {
        String stringBuilder = "2" + System.lineSeparator() +
                "3" + System.lineSeparator() +
                "0" + System.lineSeparator() +
                "10" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(TEST_CONFIG_FILE_PATH, in, out, err);

        assertTrue(errContent.toString().contains("Initial health cannot be less than 1"));
        assertTrue(outContent.toString().contains("Current game settings:"));
    }

    @Test
    void when_gameStart_expect_printGameState() {
        String stringBuilder = "1" + System.lineSeparator() +
                "quit" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(TEST_CONFIG_FILE_PATH, in, out, err);

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
    void when_playingCardNotInHand_expect_cardNotInHandError() {
        String stringBuilder = "1" + System.lineSeparator() +
                "20" + System.lineSeparator() +
                "quit" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(TEST_CONFIG_FILE_PATH, in, out, err);

        assertTrue(errContent.toString().contains(CannotPlayCardNotInHandException.MESSAGE));
    }

    @Test
    void when_skipEveryTurn_expect_bleedingOutVictory() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1").append(System.lineSeparator());

        // High enough number to pass every turn, we don't care about the invalid option errors here.
        for (int i = 0; i < 100; i++) {
            stringBuilder.append("end").append(System.lineSeparator());
        }

        stringBuilder.append("3").append(System.lineSeparator());

        InputStream in = new ByteArrayInputStream(stringBuilder.toString().getBytes());

        UserInterface.mainMenu(TEST_CONFIG_FILE_PATH, in, out, err);

        assertTrue(outContent.toString().contains("'s turn ended"));
        assertTrue(outContent.toString().contains("has lost!"));
        assertTrue(outContent.toString().contains("has won!"));
    }

    @Test
    void when_setPlayers_expect_printPlayerNames() {
        String stringBuilder = "2" + System.lineSeparator() +
                "1" + System.lineSeparator() +
                "Rahmi, Berk" + System.lineSeparator() +
                "10" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(TEST_CONFIG_FILE_PATH, in, out, err);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Players: Berk, Rahmi"));
    }

    @Test
    void when_setDeck_expect_printDeck() {
        String stringBuilder = "2" + System.lineSeparator() +
                "2" + System.lineSeparator() +
                "0, 3, 2, 5, 6, 4, 7" + System.lineSeparator() +
                "10" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(TEST_CONFIG_FILE_PATH, in, out, err);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Deck: 0, 2, 3, 4, 5, 6, 7"));
    }

    @Test
    void when_setInitialHealth_expect_printInitialHealth() {
        String stringBuilder = "2" + System.lineSeparator() +
                "3" + System.lineSeparator() +
                "20" + System.lineSeparator() +
                "10" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(TEST_CONFIG_FILE_PATH, in, out, err);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Initial health: 20"));
    }

    @Test
    void when_setInitialManaCapacity_expect_printInitialManaCapacity() {
        String stringBuilder = "2" + System.lineSeparator() +
                "4" + System.lineSeparator() +
                "5" + System.lineSeparator() +
                "10" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(TEST_CONFIG_FILE_PATH, in, out, err);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Initial mana capacity: 5"));
    }

    @Test
    void when_setInitialHandSize_expect_printInitialHandSize() {
        String stringBuilder = "2" + System.lineSeparator() +
                "5" + System.lineSeparator() +
                "2" + System.lineSeparator() +
                "10" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(TEST_CONFIG_FILE_PATH, in, out, err);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Initial hand size: 2"));
    }

    @Test
    void when_setMaxManaCapacity_expect_printMaxManaCapacity() {
        String stringBuilder = "2" + System.lineSeparator() +
                "6" + System.lineSeparator() +
                "25" + System.lineSeparator() +
                "10" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(TEST_CONFIG_FILE_PATH, in, out, err);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Max mana capacity: 25"));
    }

    @Test
    void when_setMaxHandSize_expect_printMaxHandSize() {
        String stringBuilder = "2" + System.lineSeparator() +
                "7" + System.lineSeparator() +
                "2" + System.lineSeparator() +
                "10" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(TEST_CONFIG_FILE_PATH, in, out, err);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Max hand size: 2"));
    }

    @Test
    void when_setBleedingDamageAmount_expect_printBleedingDamageAmount() {
        String stringBuilder = "2" + System.lineSeparator() +
                "8" + System.lineSeparator() +
                "3" + System.lineSeparator() +
                "10" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(TEST_CONFIG_FILE_PATH, in, out, err);

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("Bleeding damage amount: 3"));
    }

    @Test
    void when_revertToDefaultConfig_expect_printDefaultConfig() {
        String stringBuilder = "2" + System.lineSeparator() +
                "8" + System.lineSeparator() +
                "3" + System.lineSeparator() +
                "9" + System.lineSeparator() +
                "10" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(TEST_CONFIG_FILE_PATH, in, out, err);

        Config updatedConfig = new Config();
        updatedConfig.setBleedingDamageAmount(3);
        int firstConfigIndex = outContent.toString().indexOf(new Config().toString());
        int updatedConfigIndex = outContent.toString().substring(firstConfigIndex).indexOf(updatedConfig.toString());

        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().substring(updatedConfigIndex).contains(new Config().toString()));
    }

    @Test
    void when_corruptedConfig_expect_corruptedError() {
        String stringBuilder = "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(CORRUPTED_TEST_CONFIG_FILE_PATH, in, out, err);

        assertTrue(errContent.toString().contains("Previous configuration is corrupted, default configuration will be loaded instead."));
    }

    @Test
    void when_invalidConfig_expect_invalidError() {
        String stringBuilder = "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu(INVALID_TEST_CONFIG_FILE_PATH, in, out, err);

        assertTrue(errContent.toString().contains("Initial health cannot be less than 1"));
        assertTrue(errContent.toString().contains("Previous configuration could not be loaded for the reason above, default configuration will be loaded instead."));
    }

    @Test
    void when_cantReadConfig_expect_couldNotLoadError() {
        String stringBuilder = "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu("/nonExistentConfigPath", in, out, err);

        assertTrue(errContent.toString().contains("Previous configuration could not be loaded, default configuration will be loaded instead."));
    }

    @Test
    void when_cantWriteConfig_expect_couldNotSaveError() {
        String stringBuilder = "2" + System.lineSeparator() +
                "8" + System.lineSeparator() +
                "3" + System.lineSeparator() +
                "10" + System.lineSeparator() +
                "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());

        UserInterface.mainMenu("/nonExistentConfigPath", in, out, err);

        assertTrue(errContent.toString().contains("Unable to save config to disk, your customizations might get lost on exit."));
    }

}
