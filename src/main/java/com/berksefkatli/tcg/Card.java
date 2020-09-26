package com.berksefkatli.tcg;

import java.util.Objects;

public class Card {
    private final int cost;

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

}
