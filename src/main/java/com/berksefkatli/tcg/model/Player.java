package com.berksefkatli.tcg.model;

import com.berksefkatli.tcg.exception.TcgException.InvalidNameException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;
import java.util.stream.Collectors;

public class Player {
    private String name;
    private List<Card> hand;
    private Deque<Card> deck;

    private int health;
    private int mana;
    private int manaCapacity;

    public Player() {
    }

    public Player(Player player) {
        this.name = player.name;
        this.hand = player.hand;
        this.deck = player.deck;
        this.health = player.health;
        this.mana = player.mana;
        this.manaCapacity = player.manaCapacity;
    }

    public Player(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidNameException();
        }
        this.name = name.trim();
        this.deck = new ArrayDeque<>();
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

    @JsonIgnore
    public boolean isDead() {
        return health <= 0;
    }

    public int getManaCapacity() {
        return manaCapacity;
    }

    public void setManaCapacity(int manaCapacity) {
        this.manaCapacity = manaCapacity;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public Deque<Card> getDeck() {
        return deck;
    }

    public void setDeck(Deque<Card> deck) {
        this.deck = deck;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public List<Card> getPlayableCards() {
        return hand.stream().filter(card -> card.getCost() <= mana).collect(Collectors.toList());
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

    @Override
    public String toString() {
        return "Name: " + name +
                ", Health: " + health +
                ", Mana: " + mana +
                ", Mana capacity: " + manaCapacity +
                ", Cards in hand: " + hand.size();
    }
}
