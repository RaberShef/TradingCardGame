package com.berksefkatli.tcg;

import com.berksefkatli.tcg.exception.TcgException;
import com.berksefkatli.tcg.model.Card;
import com.berksefkatli.tcg.model.Config;
import com.berksefkatli.tcg.model.Player;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

public class UserInterface {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static final String INTEGER_PROMPT = "Please enter an integer: ";
    public static final String POSITIVE_INTEGER_PROMPT = "Please enter a positive integer: ";

    private UserInterface() {
    }

    public static void mainMenu(String configPath, InputStream in, PrintStream out, PrintStream err) {
        Scanner scanner = new Scanner(in);
        Config config = getConfigFromFile(configPath, out, err);
        while (true) {
            printMainMenu(out);
            String menuChoice = scanner.nextLine();
            switch (menuChoice) {
                case "1":
                    gameplay(scanner, err, new Game(out, config));
                    break;
                case "2":
                    customizeConfigMenu(configPath, scanner, out, err, config);
                    break;
                case "3":
                    return;
                default:
                    err.println("Please enter a valid option.");
            }
        }
    }

    private static Config getConfigFromFile(String configPath, PrintStream out, PrintStream err) {
        Config config = new Config();
        try {
            config = objectMapper.readValue(new File(configPath), Config.class);
            out.println("Configuration from the last game is loaded.");
        } catch (JsonParseException e) {
            err.println("Previous configuration is corrupted, default configuration will be loaded instead.");
        } catch (JsonMappingException e) {
            err.println(e.getMessage());
            err.println("Previous configuration could not be loaded for the reason above, default configuration will be loaded instead.");
        } catch (IOException e) {
            err.println("Previous configuration could not be loaded, default configuration will be loaded instead.");
        }
        return config;
    }

    private static void printMainMenu(PrintStream out) {
        out.println("Choose an option below: " + System.lineSeparator() +
                "1) Start game" + System.lineSeparator() +
                "2) Customize game settings" + System.lineSeparator() +
                "3) Quit");
    }

    private static void gameplay(Scanner scanner, PrintStream err, Game game) {
        while (game.isGameLive()) {
            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "end":
                        game.endTurn();
                        break;
                    case "quit":
                        return;
                    default:
                        int cost = Integer.parseInt(choice);
                        game.playCard(new Card(cost));
                }
            } catch (NumberFormatException e) {
                err.println("Please enter a card's cost value, 'end' or 'quit'");
            } catch (TcgException e) {
                err.println(e.getMessage());
            }
        }
    }

    private static void customizeConfigMenu(String configPath, Scanner scanner, PrintStream out, PrintStream err, Config config) {
        while (true) {
            printCustomConfigMenu(out, config);
            String menuChoice = scanner.nextLine();
            try {
                switch (menuChoice) {
                    case "1":
                        setPlayers(scanner, out, config);
                        break;
                    case "2":
                        setDeck(scanner, out, config);
                        break;
                    case "3":
                        setInitialHealth(scanner, out, config);
                        break;
                    case "4":
                        setInitialManaCapacity(scanner, out, config);
                        break;
                    case "5":
                        setInitialHandSize(scanner, out, config);
                        break;
                    case "6":
                        setMaxManaCapacity(scanner, out, config);
                        break;
                    case "7":
                        setMaxHandSize(scanner, out, config);
                        break;
                    case "8":
                        setBleedingDamageAmount(scanner, out, config);
                        break;
                    case "9":
                        config = new Config();
                        break;
                    case "10":
                        return;
                    default:
                        err.println("Please enter a valid option.");
                }
                objectMapper.writeValue(new File(configPath), config);
            } catch (NumberFormatException e) {
                err.println(INTEGER_PROMPT);
            } catch (TcgException e) {
                err.println(e.getMessage());
            } catch (IOException e) {
                err.println("Unable to save config to disk, your customizations might get lost on exit.");
            }
        }
    }

    private static void setPlayers(Scanner scanner, PrintStream out, Config config) {
        out.println("Please enter comma separated player names: ");
        String input = scanner.nextLine();
        String[] playerNames = input.split(",");
        Set<Player> players = new HashSet<>();
        for (String playerName : playerNames) {
            players.add(new Player(playerName.trim()));
        }
        config.setPlayers(players);
    }

    private static void setDeck(Scanner scanner, PrintStream out, Config config) {
        out.println("Please enter comma separated integers, each representing a card's cost: ");
        String input = scanner.nextLine();
        String[] cardCosts = input.split(",");
        List<Card> deck = new ArrayList<>();
        for (String cardCost : cardCosts) {
            deck.add(new Card(Integer.parseInt(cardCost.trim())));
        }
        Collections.sort(deck);
        config.setDeck(deck);
    }

    private static void setInitialHealth(Scanner scanner, PrintStream out, Config config) {
        out.println(POSITIVE_INTEGER_PROMPT);
        String input = scanner.nextLine();
        config.setInitialHealth(Integer.parseInt(input));
    }

    private static void setInitialManaCapacity(Scanner scanner, PrintStream out, Config config) {
        out.println(INTEGER_PROMPT);
        String input = scanner.nextLine();
        config.setInitialManaCapacity(Integer.parseInt(input));
    }

    private static void setInitialHandSize(Scanner scanner, PrintStream out, Config config) {
        out.println("Please enter an integer that is less than the deck size: ");
        String input = scanner.nextLine();
        config.setInitialHandSize(Integer.parseInt(input));
    }

    private static void setMaxManaCapacity(Scanner scanner, PrintStream out, Config config) {
        out.println(POSITIVE_INTEGER_PROMPT);
        String input = scanner.nextLine();
        config.setMaxManaCapacity(Integer.parseInt(input));
    }

    private static void setMaxHandSize(Scanner scanner, PrintStream out, Config config) {
        out.println(POSITIVE_INTEGER_PROMPT);
        String input = scanner.nextLine();
        config.setMaxHandSize(Integer.parseInt(input));
    }

    private static void setBleedingDamageAmount(Scanner scanner, PrintStream out, Config config) {
        out.println(INTEGER_PROMPT);
        String input = scanner.nextLine();
        config.setBleedingDamageAmount(Integer.parseInt(input));
    }

    private static void printCustomConfigMenu(PrintStream out, Config config) {
        out.println("Current game settings: " + System.lineSeparator() +
                config.toString() + System.lineSeparator() +
                "Choose an option below: " + System.lineSeparator() +
                "1) Set players" + System.lineSeparator() +
                "2) Set deck" + System.lineSeparator() +
                "3) Set initial health" + System.lineSeparator() +
                "4) Set initial mana capacity" + System.lineSeparator() +
                "5) Set initial hand size" + System.lineSeparator() +
                "6) Set max mana capacity" + System.lineSeparator() +
                "7) Set max hand size" + System.lineSeparator() +
                "8) Set bleeding damage amount" + System.lineSeparator() +
                "9) Revert to defaults" + System.lineSeparator() +
                "10) Back to main menu");
    }
}
