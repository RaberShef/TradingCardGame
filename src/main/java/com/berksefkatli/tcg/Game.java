package com.berksefkatli.tcg;

import com.berksefkatli.tcg.exception.TcgException.*;
import com.berksefkatli.tcg.model.Card;
import com.berksefkatli.tcg.model.Config;
import com.berksefkatli.tcg.model.Player;

import java.util.*;
import java.util.stream.Collectors;

public class Game {

    private final Config config;
    private final List<Player> players;
    private int activePlayerIndex;
    private boolean gameStarted;
    private boolean gameEnded;

    public Game() {
        this.config = new Config();
        this.players = new ArrayList<>();
    }

    public Game(Config config) {
        this.config = config;
        this.players = new ArrayList<>();
    }

    public void addPlayer(Player newPlayer) {
        validateGameNotStarted();
        validatePlayerNotExists(newPlayer);
        players.add(newPlayer);
    }

    private void validatePlayerNotExists(Player newPlayer) {
        if (players.stream().anyMatch(player -> player.equals(newPlayer))) {
            throw new UniquePlayerException();
        }
    }

    private void validateGameNotStarted() {
        if (gameStarted) {
            throw new CannotChangePlayersAfterGameStartException();
        }
    }

    public void removePlayer(Player playerToBeRemoved) {
        validateGameNotStarted();
        validatePlayerExists(playerToBeRemoved);
        players.remove(playerToBeRemoved);
    }

    private void validatePlayerExists(Player playerToBeRemoved) {
        if (players.stream().noneMatch(player -> player.equals(playerToBeRemoved))) {
            throw new NonExistentPlayerException();
        }
    }

    public void start() {
        if (players.size() < 2) {
            throw new NotEnoughPlayersException();
        }
        initializePlayers();
        activePlayerIndex = new Random().nextInt(players.size());
        gameStarted = true;
        advanceToNextPlayer();
    }

    public void initializePlayers() {
        players.forEach(player -> {
            player.setHealth(config.getInitialPlayerHealth());
            player.setManaSlot(config.getInitialPlayerManaSlot());
            player.setHand(new ArrayList<>());
            player.setDeck(getShuffledDeck(config.getStartingDeck()));
            for (int i = 0; i < config.getInitialPlayerHandSize(); i++) {
                player.getHand().add(player.getDeck().pop());
            }
        });
    }

    private Stack<Card> getShuffledDeck(List<Card> startingDeck) {
        Stack<Card> shuffledDeck = new Stack<>();
        List<Card> deck = new ArrayList<>(startingDeck);
        Collections.shuffle(deck);
        shuffledDeck.addAll(deck);
        return shuffledDeck;
    }

    private void advanceToNextPlayer() {
        activePlayerIndex = (activePlayerIndex + 1) % players.size();
        Player activePlayer = getActivePlayer();
        activePlayer.increaseManaSlots();
        draw(activePlayer);
        if (activePlayer.isDead()) {
            // Death from bleeding out
            removeDeadPlayer(activePlayer);
            if (isLastOneStanding()) {
                return;
            }
            advanceToNextPlayer();
        } else {
            advanceToNextPlayerIfNoPlayableCards();
        }
    }

    private void advanceToNextPlayerIfNoPlayableCards() {
        if (getActivePlayer().getPlayableCards().isEmpty()) {
            System.out.println("No playable cards exist. Auto skipping " + getActivePlayer().getName() + "'s turn.");
            advanceToNextPlayer();
        } else {
            printGameState();
        }
    }

    private void removeDeadPlayer(Player deadPlayer) {
        Player activePlayer = getActivePlayer();
        if (deadPlayer.equals(activePlayer)) {
            Player previousPlayer = players.get((activePlayerIndex + players.size() - 1) % players.size());
            players.remove(deadPlayer);
            activePlayerIndex = players.indexOf(previousPlayer);
        } else {
            players.remove(deadPlayer);
            activePlayerIndex = players.indexOf(activePlayer);
        }
        System.out.println(deadPlayer.getName() + " has lost!");
    }

    public void draw(Player player) {
        if (player.getDeck().isEmpty()) {
            System.out.println(player.getName() + " is bleeding out!");
            player.setHealth(player.getHealth() - config.getBleedingOutDamage());
        } else {
            Card drawnCard = player.getDeck().pop();
            if (player.getHand().size() == config.getMaxHandSize()) {
                System.out.println(player.getName() + " is overloaded!");
            } else {
                player.getHand().add(drawnCard);
            }
        }
    }

    public void playCard(Card card) {
        validatePlay(card);
        System.out.println(getActivePlayer().getName() + " played a card with " + card.getCost() + " cost");
        getActivePlayer().setMana(getActivePlayer().getMana() - card.getCost());
        getActivePlayer().getHand().remove(card);
        List<Player> deadPlayers = dealDamage(card);
        deadPlayers.forEach(this::removeDeadPlayer);
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

    private boolean isLastOneStanding() {
        if (players.size() == 1) {
            gameEnded = true;
            System.out.println(players.get(0).getName() + " has won!");
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
        // Change active player to next player.
        validateGameLive();
        System.out.println(getActivePlayer().getName() + "'s turn ended");
        advanceToNextPlayer();
    }

    private void printGameState() {
        System.out.println("===============================================================");
        System.out.println("Players: ");
        getPlayers().forEach(player ->
                System.out.println("Name: " + player.getName()
                        + ", Health: " + player.getHealth()
                        + ", Mana: " + player.getMana()
                        + ", ManaSlots: " + player.getManaSlot()
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

    public Config getConfig() {
        return config;
    }

    public boolean isGameLive() {
        return gameStarted && !gameEnded;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }
}
