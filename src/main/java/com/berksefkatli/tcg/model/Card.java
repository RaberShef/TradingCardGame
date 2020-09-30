package com.berksefkatli.tcg.model;

import java.util.Objects;

public class Card implements Comparable<Card> {
    private int cost;

    public Card() {
    }

    public Card(int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return cost == card.cost;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cost);
    }

    @Override
    public int compareTo(Card other) {
        return Integer.compare(this.getCost(), other.getCost());
    }
}
