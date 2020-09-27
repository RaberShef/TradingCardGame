package com.berksefkatli.tcg.model;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private int maxHandSize = 5;
    private int bleedingOutDamage = 1;
    private int initialPlayerHealth = 30;
    private int initialPlayerManaSlot = 0;
    private int initialPlayerHandSize = 3;
    private List<Card> startingDeck = getDefaultDeck();

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

    public int getMaxHandSize() {
        return maxHandSize;
    }

    public void setMaxHandSize(int maxHandSize) {
        if (maxHandSize < 1) {
            throw new InvalidConfigurationException("Max player hand size cannot be less than 1");
        }
        this.maxHandSize = maxHandSize;
    }

    public int getBleedingOutDamage() {
        return bleedingOutDamage;
    }

    public void setBleedingOutDamage(int bleedingOutDamage) {
        if (bleedingOutDamage < 0) {
            throw new InvalidConfigurationException("Bleeding out damage cannot be less than 0");
        }
        this.bleedingOutDamage = bleedingOutDamage;
    }

    public int getInitialPlayerHealth() {
        return initialPlayerHealth;
    }

    public void setInitialPlayerHealth(int initialPlayerHealth) {
        if (initialPlayerHealth < 1) {
            throw new InvalidConfigurationException("Initial player health cannot be less than 1");
        }
        this.initialPlayerHealth = initialPlayerHealth;
    }

    public int getInitialPlayerManaSlot() {
        return initialPlayerManaSlot;
    }

    public void setInitialPlayerManaSlot(int initialPlayerManaSlot) {
        if (initialPlayerManaSlot < 0) {
            throw new InvalidConfigurationException("Initial player mana slots cannot be less than 0");
        }
        this.initialPlayerManaSlot = initialPlayerManaSlot;
    }

    public int getInitialPlayerHandSize() {
        return initialPlayerHandSize;
    }

    public void setInitialPlayerHandSize(int initialPlayerHandSize) {
        if (initialPlayerHandSize > startingDeck.size()) {
            throw new InvalidConfigurationException(
                    "Initial player hand size cannot be greater than the number of cards in the initial deck");
        }
        this.initialPlayerHandSize = initialPlayerHandSize;
    }

    public List<Card> getStartingDeck() {
        return startingDeck;
    }

    public void setStartingDeck(List<Card> startingDeck) {
        if (initialPlayerHandSize > startingDeck.size()) {
            throw new InvalidConfigurationException(
                    "Initial player hand size cannot be greater than the number of cards in the initial deck");
        }
        this.startingDeck = startingDeck;
    }

    public static class InvalidConfigurationException extends RuntimeException {
        public InvalidConfigurationException(String message) {
            super(message);
        }
    }
}
