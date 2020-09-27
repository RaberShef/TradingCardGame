package com.berksefkatli.tcg;

import com.berksefkatli.tcg.exception.TcgException;
import com.berksefkatli.tcg.model.Card;
import com.berksefkatli.tcg.model.Player;

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
                        System.out.println("Enter a player name for the new player");
                        playerName = in.nextLine();
                        game.addPlayer(new Player(playerName));
                        break;
                    case "2":
                        System.out.println("Enter the player name of the player to be removed");
                        playerName = in.nextLine();
                        game.removePlayer(new Player(playerName));
                        break;
                    case "3":
                        gameplayLoop(in, game);
                        break;
                    case "4":
                        return;
                    default:
                        System.err.println("Please enter a valid option.");
                }
            } catch (TcgException e) {
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
        System.out.println("4) Quit game");
    }

    private static void gameplayLoop(Scanner in, Game game) {
        game.start();
        while (game.isGameLive()) {
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
                } catch (TcgException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
}
