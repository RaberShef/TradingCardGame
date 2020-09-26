package com.berksefkatli.tcg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Game {
    private static final int STARTING_PLAYER_HEALTH = 30;
    private static final int STARTING_PLAYER_MANA = 0;
    private static final int STARTING_PLAYER_MANA_SLOT = 0;
    private static final int STARTING_PLAYER_HAND_SIZE = 3;

    private final List<Card> startingDeck;
    private final List<Player> players;
    private int activePlayerIndex;
    private boolean gameStarted;
    private boolean gameEnded;

    public Game() {
        this.players = new ArrayList<>();
        this.gameStarted = false;
        this.gameEnded = false;
        this.startingDeck = getDefaultDeck();
    }

    public Game(List<Card> customDeck) {
        this.players = new ArrayList<>();
        this.gameStarted = false;
        this.gameEnded = false;
        this.startingDeck = customDeck;
    }

    public static List<Card> getDefaultDeck() {
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(0));
        cards.add(new Card(0));
        cards.add(new Card(1));
        cards.add(new Card(1));
        cards.add(new Card(2));
        cards.add(new Card(2));
        cards.add(new Card(2));
        cards.add(new Card(3));
        cards.add(new Card(3));
        cards.add(new Card(3));
        cards.add(new Card(3));
        cards.add(new Card(4));
        cards.add(new Card(4));
        cards.add(new Card(4));
        cards.add(new Card(5));
        cards.add(new Card(5));
        cards.add(new Card(6));
        cards.add(new Card(6));
        cards.add(new Card(7));
        cards.add(new Card(8));
        return cards;
    }

    public void addPlayer(Player newPlayer) {
        checkGameNotStarted();
        checkUniquePlayer(newPlayer);
        players.add(newPlayer);
    }

    private void checkUniquePlayer(Player newPlayer) {
        if (players.stream().anyMatch(player -> player.equals(newPlayer))) {
            throw new UniquePlayerException();
        }
    }

    public void removePlayer(Player playerToBeRemoved) {
        checkGameNotStarted();
        if (!players.remove(playerToBeRemoved)) {
            throw new NonExistentPlayerException();
        }
    }

    private void checkGameNotStarted() {
        if (gameStarted) {
            throw new CannotChangePlayersAfterGameStartException();
        }
    }

    public void start() {
        if (players.size() < 2) {
            throw new NotEnoughPlayersException();
        }

        players.forEach(player -> player.initialize(
                STARTING_PLAYER_HEALTH,
                STARTING_PLAYER_MANA,
                STARTING_PLAYER_MANA_SLOT,
                STARTING_PLAYER_HAND_SIZE,
                startingDeck
        ));

        activePlayerIndex = new Random().nextInt(players.size());
        gameStarted = true;
        advanceTurn();
        advanceTurnsUntilPlayable();
    }

    private void advanceTurn() {
        activePlayerIndex = (activePlayerIndex + 1) % players.size();
        Player activePlayer = getActivePlayer();
        activePlayer.increaseManaSlots();
        activePlayer.draw();
        if (activePlayer.getHealth() <= 0) {
            activePlayerIndex = (activePlayerIndex + 1) % players.size();
            Player newActivePlayer = getActivePlayer();
            players.remove(activePlayer);
            activePlayerIndex = players.indexOf(newActivePlayer);
            System.out.println(activePlayer.getName() + " has lost!");
            decideTheWinner();
        }
    }

    private void advanceTurnsUntilPlayable() {
        // Automatic turn skipping
        if (gameStarted && !gameEnded) {
            while (getActivePlayer().getPlayableCards().isEmpty()) {
                System.out.println("No playable cards exist. Auto skipping " + getActivePlayer().getName() + "'s turn.");
                advanceTurn();
            }
        }
        printGameState();
    }

    public void playCard(Card card) {
        validatePlay(card);
        System.out.println(getActivePlayer().getName() + " played a card with " + card.getCost() + " cost");
        getActivePlayer().setMana(getActivePlayer().getMana() - card.getCost());
        getActivePlayer().getHand().remove(card);
        removeDeadPlayers(dealDamage(card));
        advanceTurnsUntilPlayable();
    }

    private void removeDeadPlayers(List<Player> deadPlayers) {
        deadPlayers.forEach(deadPlayer -> {
            Player activePlayer = getActivePlayer();
            players.remove(deadPlayer);
            activePlayerIndex = players.indexOf(activePlayer);
            System.out.println(deadPlayer.getName() + " has lost!");
            decideTheWinner();
        });
    }

    private void decideTheWinner() {
        if (players.size() == 1) {
            gameEnded = true;
            System.out.println(players.get(0).getName() + " has won!");
        }
    }

    private List<Player> dealDamage(Card card) {
        List<Player> deadPlayers = new ArrayList<>();
        if (card.getCost() > 0) {
            players.forEach(player -> {
                if (!player.equals(getActivePlayer())) {
                    player.setHealth(player.getHealth() - card.getCost());
                    System.out.println(player.getName() + " took " + card.getCost() + " damage!");
                    if (player.getHealth() <= 0) {
                        deadPlayers.add(player);
                    }
                }
            });
        }
        return deadPlayers;
    }

    private void validatePlay(Card card) {
        if (!gameStarted) {
            throw new GameNotStartedException();
        }
        if (gameEnded) {
            throw new GameEndedException();
        }
        if (!getActivePlayer().getHand().contains(card)) {
            throw new CannotPlayCardNotInHandException();
        }
        if (getActivePlayer().getMana() < card.getCost()) {
            throw new NotEnoughManaException();
        }
    }

    public void endTurn() {
        // Change active player to next player.
        System.out.println(getActivePlayer().getName() + "'s turn ended");
        advanceTurn();
        advanceTurnsUntilPlayable();
    }

    private void printGameState() {
        System.out.println("===============================================================");
        System.out.println("Players: ");
        getPlayers().forEach(player ->
                System.out.println("Name: " + player.getName()
                        + ", Health: " + player.getHealth()
                        + ", Mana: " + player.getMana()
                        + ", ManaSlots: " + player.getManaSlots()
                        + ", CardsInHand: " + player.getHand().size()));
        System.out.println("===============================================================");
        System.out.println("Active player: " + getActivePlayer().getName());
        System.out.println("Active player's hand: " + getActivePlayer().getHand()
                .stream().map(Card::getCost).collect(Collectors.toList()));
        System.out.println("Choose a card to play by entering its cost, " +
                "end your turn by entering 'end' or quit the game by entering 'quit': ");
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getActivePlayer() {
        return players.get(activePlayerIndex);
    }

    public boolean isGameNotEnded() {
        return !gameEnded;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public static class UniquePlayerException extends RuntimeException {
        public static final String message = "Players in a game must be unique";

        public UniquePlayerException() {
            super(message);
        }
    }

    public static class NonExistentPlayerException extends RuntimeException {
        public static final String message = "The player does not exist.";

        public NonExistentPlayerException() {
            super(message);
        }
    }

    public static class CannotChangePlayersAfterGameStartException extends RuntimeException {
        public static final String message = "Players cannot be changed after the preparation phase";

        public CannotChangePlayersAfterGameStartException() {
            super(message);
        }
    }

    public static class NotEnoughPlayersException extends RuntimeException {
        public static final String message = "The game cannot be started unless there are at least two players.";

        public NotEnoughPlayersException() {
            super(message);
        }
    }

    public static class CannotPlayCardNotInHandException extends RuntimeException {
        public static final String message = "Only the cards in hand can be played.";

        public CannotPlayCardNotInHandException() {
            super(message);
        }
    }

    public static class NotEnoughManaException extends RuntimeException {
        public static final String message = "Player does not have enough mana to play requested card.";

        public NotEnoughManaException() {
            super(message);
        }
    }

    public static class GameEndedException extends RuntimeException {
        public static final String message = "No more plays can be made since the game has ended.";

        public GameEndedException() {
            super(message);
        }
    }

    public static class GameNotStartedException extends RuntimeException {
        public static final String message = "Plays can't be made since the game has not started yet.";

        public GameNotStartedException() {
            super(message);
        }
    }
}
