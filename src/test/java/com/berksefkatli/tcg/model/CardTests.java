package com.berksefkatli.tcg.model;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CardTests {

    @Test
    public void when_equals_expect_equalsOfCost() {
        Card card = new Card(3);
        Card card2 = new Card(3);
        Card card3 = new Card(4);
        assertEquals(card, card2);
        assertNotEquals(card, card3);
    }

    @Test
    public void when_hashCode_expect_hashOfName() {
        Card card = new Card(3);
        assertEquals(Objects.hash(card.getCost()), card.hashCode());
    }

    @Test
    public void when_negativeCost_expect_successful() {
        Card card = new Card(-3);
        assertEquals(-3, card.getCost());
    }

    @Test
    public void when_positiveCost_expect_successful() {
        Card card = new Card(3);
        assertEquals(3, card.getCost());
    }

    @Test
    public void when_zeroCost_expect_successful() {
        Card card = new Card(0);
        assertEquals(0, card.getCost());
    }

    @Test
    public void when_compareTo_expect_successful() {
        Card card = new Card(0);
        Card card2 = new Card(5);
        Card card3 = new Card(5);
        assertEquals(-1, card.compareTo(card2));
        assertEquals(0, card2.compareTo(card3));
        assertEquals(1, card3.compareTo(card));
    }

}
