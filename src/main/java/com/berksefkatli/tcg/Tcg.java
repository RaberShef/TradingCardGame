package com.berksefkatli.tcg;

import java.util.Scanner;
import java.util.stream.Collectors;

public class Tcg {

    public static void main(String[] args) {
        mainMenuLoop(new Scanner(System.in), new Game());
    }

    private static void mainMenuLoop(Scanner in, Game game) {
        while (!game.isGameStarted()) {
            printMainMenu(game);

            String menuChoice = in.nextLine();
            String playerName;
            try {
                switch (menuChoice) {
                    case "1":
                        System.out.println("Enter player name");
                        playerName = in.nextLine();
                        game.addPlayer(new Player(playerName));
                        break;
                    case "2":
                        System.out.println("Enter player name");
                        playerName = in.nextLine();
                        game.removePlayer(new Player(playerName));
                        break;
                    case "3":
                        game.start();
                        gameplayLoop(in, game);
                        break;
                    case "quit":
                        return;
                    default:
                        System.err.println("Please enter a valid option.");
                }
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static void printMainMenu(Game game) {
        System.out.println("Current players: " + game.getPlayers().stream().map(Player::getName).collect(Collectors.joining(", ")));
        System.out.println("Choose an option below: ");
        System.out.println("1) Add player");
        System.out.println("2) Remove player");
        System.out.println("3) Start game");
        System.out.println("quit) Quit game");
    }

    private static void gameplayLoop(Scanner in, Game game) {
        while (game.isGameNotEnded()) {
            String choice = in.nextLine();
            if (choice.equals("end")) {
                game.endTurn();
            } else if (choice.equals("quit")) {
                return;
            } else {
                try {
                    int cost = Integer.parseInt(choice);
                    game.playCard(new Card(cost));
                } catch (NumberFormatException e) {
                    System.err.println("Please enter a valid integer, 'end' or 'quit'");
                } catch (RuntimeException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
}
