package com.berksefkatli.tcg;

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

}
