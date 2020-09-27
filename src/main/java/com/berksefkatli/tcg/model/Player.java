package com.berksefkatli.tcg.model;

import com.berksefkatli.tcg.exception.TcgException.InvalidNameException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

public class Player {
    private final String name;
    private List<Card> hand;
    private Stack<Card> deck;

    private int health;
    private int mana;
    private int manaSlot;

    public Player(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidNameException();
        }
        this.name = name;
        this.deck = new Stack<>();
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

    public boolean isDead() {
        return health <= 0;
    }

    public int getManaSlot() {
        return manaSlot;
    }

    public void setManaSlot(int manaSlot) {
        this.manaSlot = manaSlot;
    }

    public void increaseManaSlots() {
        this.manaSlot = Math.min(manaSlot + 1, 10);
        this.mana = manaSlot;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public Stack<Card> getDeck() {
        return deck;
    }

    public void setDeck(Stack<Card> deck) {
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
}
