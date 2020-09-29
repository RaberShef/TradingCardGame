package com.berksefkatli.tcg;

import com.berksefkatli.tcg.exception.TcgException;
import com.berksefkatli.tcg.model.Card;
import com.berksefkatli.tcg.model.Config;
import com.berksefkatli.tcg.model.Player;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

public class UserInterface {

    public static void mainMenu(InputStream in, PrintStream out, PrintStream err) {
        Scanner scanner = new Scanner(in);
        Config config = new Config();
        while (true) {
            printMainMenu(out);
            String menuChoice = scanner.nextLine();
            switch (menuChoice) {
                case "1":
                    gameplay(scanner, err, new Game(out, config));
                    break;
                case "2":
                    customizeConfigMenu(scanner, out, err, config);
                    break;
                case "3":
                    return;
                default:
                    err.println("Please enter a valid option.");
            }
        }
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

    private static void customizeConfigMenu(Scanner scanner, PrintStream out, PrintStream err, Config config) {
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
                        return;
                    default:
                        err.println("Please enter a valid option.");
                }
            } catch (NumberFormatException e) {
                err.println("Please enter a valid integer");
            } catch (TcgException e) {
                err.println(e.getMessage());
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
        out.println("Please enter a positive integer: ");
        String input = scanner.nextLine();
        config.setInitialHealth(Integer.parseInt(input));
    }

    private static void setInitialManaCapacity(Scanner scanner, PrintStream out, Config config) {
        out.println("Please enter an integer: ");
        String input = scanner.nextLine();
        config.setInitialManaCapacity(Integer.parseInt(input));
    }

    private static void setInitialHandSize(Scanner scanner, PrintStream out, Config config) {
        out.println("Please enter an integer less than deck size: ");
        String input = scanner.nextLine();
        config.setInitialHandSize(Integer.parseInt(input));
    }

    private static void setMaxManaCapacity(Scanner scanner, PrintStream out, Config config) {
        out.println("Please enter a positive integer: ");
        String input = scanner.nextLine();
        config.setMaxManaCapacity(Integer.parseInt(input));
    }

    private static void setMaxHandSize(Scanner scanner, PrintStream out, Config config) {
        out.println("Please enter a positive integer: ");
        String input = scanner.nextLine();
        config.setMaxHandSize(Integer.parseInt(input));
    }

    private static void setBleedingDamageAmount(Scanner scanner, PrintStream out, Config config) {
        out.println("Please enter an integer: ");
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
                "9) Back to main menu");
    }
}
