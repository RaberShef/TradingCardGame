package com.berksefkatli.tcg.model;

import com.berksefkatli.tcg.exception.TcgException.InvalidConfigurationException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Config {

    private Set<Player> players = getDefaultPlayers();
    private List<Card> deck = getDefaultDeck();
    private int initialHealth = 30;
    private int initialManaCapacity = 0;
    private int initialHandSize = 3;
    private int maxManaCapacity = 10;
    private int maxHandSize = 5;
    private int bleedingDamageAmount = 1;

    public Set<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Set<Player> players) {
        if (players.size() < 2) {
            throw new InvalidConfigurationException("There cannot be less than 2 unique players");
        }
        this.players = players;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public void setDeck(List<Card> deck) {
        if (deck.isEmpty()) {
            throw new InvalidConfigurationException("Deck must contain at least 1 card");
        }
        if (initialHandSize > deck.size()) {
            throw new InvalidConfigurationException("Deck size cannot be less than the initial hand size");
        }
        this.deck = deck;
    }

    private List<Card> getDefaultDeck() {
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

    private Set<Player> getDefaultPlayers() {
        Set<Player> defaultPlayers = new HashSet<>();
        defaultPlayers.add(new Player("Player1"));
        defaultPlayers.add(new Player("Player2"));
        return defaultPlayers;
    }

    public int getInitialHealth() {
        return initialHealth;
    }

    public void setInitialHealth(int initialHealth) {
        if (initialHealth < 1) {
            throw new InvalidConfigurationException("Initial health cannot be less than 1");
        }
        this.initialHealth = initialHealth;
    }

    public int getInitialManaCapacity() {
        return initialManaCapacity;
    }

    public void setInitialManaCapacity(int initialManaCapacity) {
        if (initialManaCapacity < 0) {
            throw new InvalidConfigurationException("Initial mana capacity cannot be less than 0");
        }
        if (initialManaCapacity > maxManaCapacity) {
            throw new InvalidConfigurationException(
                    "Initial mana capacity cannot be greater than the max mana capacity");
        }
        this.initialManaCapacity = initialManaCapacity;
    }

    public int getInitialHandSize() {
        return initialHandSize;
    }

    public void setInitialHandSize(int initialHandSize) {
        if (initialHandSize < 0) {
            throw new InvalidConfigurationException("Initial hand size cannot be less than 0");
        }
        if (initialHandSize > deck.size()) {
            throw new InvalidConfigurationException(
                    "Initial hand size cannot be greater than the number of cards in the initial deck");
        }
        this.initialHandSize = initialHandSize;
    }

    public int getMaxManaCapacity() {
        return maxManaCapacity;
    }

    public void setMaxManaCapacity(int maxManaCapacity) {
        if (maxManaCapacity < 1) {
            throw new InvalidConfigurationException("Max mana capacity cannot be less than 1");
        }
        if (initialManaCapacity > maxManaCapacity) {
            throw new InvalidConfigurationException(
                    "Max mana capacity cannot be less than the initial mana capacity");
        }
        this.maxManaCapacity = maxManaCapacity;
    }

    public int getMaxHandSize() {
        return maxHandSize;
    }

    public void setMaxHandSize(int maxHandSize) {
        if (maxHandSize < 1) {
            throw new InvalidConfigurationException("Max player hand size cannot be less than 1");
        }
        this.maxHandSize = maxHandSize;
    }

    public int getBleedingDamageAmount() {
        return bleedingDamageAmount;
    }

    public void setBleedingDamageAmount(int bleedingDamageAmount) {
        if (bleedingDamageAmount < 0) {
            throw new InvalidConfigurationException("Bleeding damage cannot be less than 0");
        }
        this.bleedingDamageAmount = bleedingDamageAmount;
    }

    @Override
    public String toString() {
        return "Players: " + players.stream().map(Player::getName)
                .collect(Collectors.joining(", ")) + System.lineSeparator() +
                "Deck: " + deck.stream().map(card -> "" + card.getCost())
                .collect(Collectors.joining(", ")) + System.lineSeparator() +
                "Initial health: " + initialHealth + System.lineSeparator() +
                "Initial mana capacity: " + initialManaCapacity + System.lineSeparator() +
                "Initial hand size: " + initialHandSize + System.lineSeparator() +
                "Max mana capacity: " + maxManaCapacity + System.lineSeparator() +
                "Max hand size: " + maxHandSize + System.lineSeparator() +
                "Bleeding damage amount: " + bleedingDamageAmount + System.lineSeparator();
    }
}
