package com.berksefkatli.tcg.model;

import com.berksefkatli.tcg.model.Config.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigTests {

    @Test
    public void when_setInitialHandSizeGreaterThanInitialDeckSize_throw_invalidConfigurationException() {
        assertThrows(InvalidConfigurationException.class, () -> {
            Config config = new Config();
            config.setInitialPlayerHandSize(21);
        });

        assertThrows(InvalidConfigurationException.class, () -> {
            Config config = new Config();
            List<Card> deck = new ArrayList<>();
            deck.add(new Card(1));
            deck.add(new Card(1));
            config.setStartingDeck(deck);
        });
    }

    @Test
    public void when_setInitialPlayerHealthLessThan1_throw_invalidConfigurationException() {
        assertThrows(InvalidConfigurationException.class, () -> {
            Config config = new Config();
            config.setInitialPlayerHealth(0);
        });

        assertThrows(InvalidConfigurationException.class, () -> {
            Config config = new Config();
            config.setInitialPlayerHealth(-1);
        });
    }

    @Test
    public void when_setMaxHandSizeLessThan1_throw_invalidConfigurationException() {
        assertThrows(InvalidConfigurationException.class, () -> {
            Config config = new Config();
            config.setMaxHandSize(0);
        });

        assertThrows(InvalidConfigurationException.class, () -> {
            Config config = new Config();
            config.setMaxHandSize(-1);
        });
    }

    @Test
    public void when_setBleedingOutDamageLessThan0_throw_invalidConfigurationException() {
        assertThrows(InvalidConfigurationException.class, () -> {
            Config config = new Config();
            config.setBleedingOutDamage(-1);
        });
    }

    @Test
    public void when_setInitialPlayerManaSlotLessThan0_throw_invalidConfigurationException() {
        assertThrows(InvalidConfigurationException.class, () -> {
            Config config = new Config();
            config.setInitialPlayerManaSlot(-1);
        });
    }

    @Test
    public void when_setAll_expect_successful() {
        Config config = new Config();

        config.setMaxHandSize(3);
        assertEquals(3, config.getMaxHandSize());

        config.setBleedingOutDamage(5);
        assertEquals(5, config.getBleedingOutDamage());

        config.setInitialPlayerHealth(20);
        assertEquals(20, config.getInitialPlayerHealth());

        config.setInitialPlayerManaSlot(4);
        assertEquals(4, config.getInitialPlayerManaSlot());

        config.setInitialPlayerHandSize(2);
        assertEquals(2, config.getInitialPlayerHandSize());

        List<Card> startingDeck = new ArrayList<>();
        startingDeck.add(new Card(20));
        startingDeck.add(new Card(20));
        startingDeck.add(new Card(20));
        startingDeck.add(new Card(20));
        startingDeck.add(new Card(20));
        config.setStartingDeck(startingDeck);
        assertEquals(startingDeck, config.getStartingDeck());

    }
}
