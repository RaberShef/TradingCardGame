package com.berksefkatli.tcg;

import com.berksefkatli.tcg.exception.TcgException.CannotPlayCardNotInHandException;
import com.berksefkatli.tcg.exception.TcgException.GameNotLiveException;
import com.berksefkatli.tcg.exception.TcgException.NotEnoughManaException;
import com.berksefkatli.tcg.model.Card;
import com.berksefkatli.tcg.model.Config;
import com.berksefkatli.tcg.model.Player;

import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

public class Game {

    private final Config config;
    private final List<Player> players;
    private final PrintStream out;
    private int activePlayerIndex;
    private boolean gameStarted;
    private boolean gameEnded;

    public Game(PrintStream out, Config config) {
        this.out = out;
        this.config = config;
        this.players = config.getPlayers().stream().map(Player::new).collect(Collectors.toList());
        start();
    }

    private void start() {
        initializePlayers();
        activePlayerIndex = new Random().nextInt(players.size());
        gameStarted = true;
        advanceToNextPlayer();
    }

    private void initializePlayers() {
        players.forEach(player -> {
            player.setHealth(config.getInitialHealth());
            player.setManaCapacity(config.getInitialManaCapacity());
            player.setHand(new ArrayList<>());
            player.setDeck(getShuffledDeck(config.getDeck()));
            for (int i = 0; i < config.getInitialHandSize(); i++) {
                player.getHand().add(player.getDeck().pop());
            }
        });
    }

    private Stack<Card> getShuffledDeck(List<Card> deck) {
        Stack<Card> shuffledDeck = new Stack<>();
        Collections.shuffle(deck);
        shuffledDeck.addAll(deck);
        return shuffledDeck;
    }

    private void advanceToNextPlayer() {
        activePlayerIndex = (activePlayerIndex + 1) % players.size();
        initializeNextTurn();
    }

    private void initializeNextTurn() {
        Player activePlayer = getActivePlayer();
        increaseManaCapacity(activePlayer);
        tryToDrawFromDeck(activePlayer);
    }

    public void increaseManaCapacity(Player player) {
        player.setManaCapacity(Math.min(player.getManaCapacity() + 1, config.getMaxManaCapacity()));
        player.setMana(player.getManaCapacity());
    }

    public void tryToDrawFromDeck(Player player) {
        if (player.getDeck().isEmpty()) {
            bleedOut(player);
        } else {
            drawFromDeck(player);
            advanceToNextPlayerIfNoPlayableCards();
        }
    }

    private void bleedOut(Player player) {
        out.println(player.getName() + " is bleeding out!");
        player.setHealth(player.getHealth() - config.getBleedingDamageAmount());
        if (player.isDead()) {
            removeActiveDeadPlayer(player);
            if (isLastOneStanding()) {
                return;
            }
            advanceToNextPlayer();
        } else {
            advanceToNextPlayerIfNoPlayableCards();
        }
    }

    private void drawFromDeck(Player player) {
        Card drawnCard = player.getDeck().pop();
        if (player.getHand().size() == config.getMaxHandSize()) {
            out.println(player.getName() + " is overloaded!");
        } else {
            player.getHand().add(drawnCard);
        }
    }

    private void advanceToNextPlayerIfNoPlayableCards() {
        if (getActivePlayer().getPlayableCards().isEmpty()) {
            out.println("No playable cards exist. Auto skipping " + getActivePlayer().getName() + "'s turn.");
            advanceToNextPlayer();
        } else {
            printGameState();
        }
    }

    private void removeActiveDeadPlayer(Player deadPlayer) {
        Player previousPlayer = players.get((activePlayerIndex + players.size() - 1) % players.size());
        players.remove(deadPlayer);
        activePlayerIndex = players.indexOf(previousPlayer);
        out.println(deadPlayer.getName() + " has lost!");
    }

    private void removeInactiveDeadPlayer(Player deadPlayer) {
        Player activePlayer = getActivePlayer();
        players.remove(deadPlayer);
        activePlayerIndex = players.indexOf(activePlayer);
        out.println(deadPlayer.getName() + " has lost!");
    }

    public void playCard(Card card) {
        validatePlay(card);
        out.println(getActivePlayer().getName() + " played a card with " + card.getCost() + " cost");
        getActivePlayer().setMana(getActivePlayer().getMana() - card.getCost());
        getActivePlayer().getHand().remove(card);
        dealDamage(card);
        if (isLastOneStanding()) {
            return;
        }
        advanceToNextPlayerIfNoPlayableCards();
    }

    private void validatePlay(Card card) {
        validateGameLive();
        if (!getActivePlayer().getHand().contains(card)) {
            throw new CannotPlayCardNotInHandException();
        }
        if (getActivePlayer().getMana() < card.getCost()) {
            throw new NotEnoughManaException();
        }
    }

    private void dealDamage(Card card) {
        List<Player> deadPlayers = new ArrayList<>();
        if (card.getCost() > 0) {
            players.forEach(player -> {
                if (!player.equals(getActivePlayer())) {
                    player.setHealth(player.getHealth() - card.getCost());
                    out.println(player.getName() + " took " + card.getCost() + " damage!");
                    if (player.isDead()) {
                        deadPlayers.add(player);
                    }
                }
            });
            deadPlayers.forEach(this::removeInactiveDeadPlayer);
        }
    }

    private boolean isLastOneStanding() {
        if (players.size() == 1) {
            gameEnded = true;
            out.println(players.get(0).getName() + " has won!");
            return true;
        }
        return false;
    }

    private void validateGameLive() {
        if (!isGameLive()) {
            throw new GameNotLiveException();
        }
    }

    public void endTurn() {
        validateGameLive();
        out.println(getActivePlayer().getName() + "'s turn ended");
        advanceToNextPlayer();
    }

    private void printGameState() {
        out.println("===============================================================");
        out.println("Players: ");
        players.forEach(player -> out.println(player.toString()));
        out.println("===============================================================");
        out.println("Active player: " + getActivePlayer().getName());
        out.println("Active player's hand: " + getActivePlayer().getHand()
                .stream().map(Card::getCost).collect(Collectors.toList()));
        out.println("Choose a card to play by entering its cost, " +
                "end your turn by entering 'end' or quit the game by entering 'quit': ");
    }

    List<Player> getCopyOfPlayers() {
        // Return a copy of player objects to make it read-only.
        return players.stream().map(Player::new).collect(Collectors.toList());
    }

    Player getCopyOfActivePlayer() {
        // Return a copy of player object to make it read-only.
        return new Player(players.get(activePlayerIndex));
    }

    private Player getActivePlayer() {
        return players.get(activePlayerIndex);
    }

    public boolean isGameLive() {
        return gameStarted && !gameEnded;
    }

}
