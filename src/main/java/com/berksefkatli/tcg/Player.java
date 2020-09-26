package com.berksefkatli.tcg;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class Player {
    private final String name;
    private List<Card> hand;
    private List<Card> deck;

    private int health;
    private int mana;
    private int manaSlots;

    public Player(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidNameException();
        }
        this.name = name;
        this.deck = new ArrayList<>();
        this.hand = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getManaSlots() {
        return manaSlots;
    }

    public void increaseManaSlots() {
        this.manaSlots = Math.min(manaSlots + 1, 10);
        this.mana = manaSlots;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = Math.min(manaSlots, mana);
    }

    public int getDeckSize() {
        return deck.size();
    }

    public List<Card> getHand() {
        return hand;
    }

    public void initialize(int health, int mana, int manaSlots, int initialHandSize, List<Card> deck) {
        this.health = health;
        this.mana = mana;
        this.manaSlots = manaSlots;

        this.deck = new ArrayList<>(deck);
        this.hand = new ArrayList<>();
        for (int i = 0; i < initialHandSize; i++) {
            draw();
        }
    }

    public List<Card> getPlayableCards() {
        return hand.stream().filter(card -> card.getCost() <= mana).collect(Collectors.toList());
    }

    public void draw() {
        if (deck.isEmpty()) {
            System.out.println(name + " is bleeding out!");
            setHealth(getHealth() - 1);
        } else {
            Card drawnCard = deck.remove(new Random().nextInt(deck.size()));
            if (hand.size() == 5) {
                System.out.println(name + " is overloaded!");
            } else {
                hand.add(drawnCard);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return name.equals(player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static class InvalidNameException extends RuntimeException {
        public static final String message = "Player name cannot be empty or null";

        public InvalidNameException() {
            super(message);
        }
    }
}
