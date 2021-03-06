package com.berksefkatli.tcg.model;

import com.berksefkatli.tcg.exception.TcgException.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigTests {

    @Test
    void when_setInitialHandSizeLessThan0_throw_invalidConfigurationException() {
        Config config = new Config();
        assertThrows(InvalidConfigurationException.class, () -> config.setInitialHandSize(-1));
    }

    @Test
    void when_setDeckSizeLessThan1_throw_invalidConfigurationException() {
        Config config = new Config();
        List<Card> deck = new ArrayList<>();
        assertThrows(InvalidConfigurationException.class, () -> config.setDeck(deck));
    }

    @Test
    void when_setInitialHandSizeGreaterThanInitialDeckSize_throw_invalidConfigurationException() {
        Config config = new Config();
        assertThrows(InvalidConfigurationException.class, () -> config.setInitialHandSize(21));

        List<Card> deck = new ArrayList<>();
        deck.add(new Card(1));
        deck.add(new Card(1));
        assertThrows(InvalidConfigurationException.class, () -> config.setDeck(deck));
    }

    @Test
    void when_setInitialHealthLessThan1_throw_invalidConfigurationException() {
        Config config = new Config();

        assertThrows(InvalidConfigurationException.class, () -> config.setInitialHealth(0));

        assertThrows(InvalidConfigurationException.class, () -> config.setInitialHealth(-1));
    }

    @Test
    void when_setMaxHandSizeLessThan1_throw_invalidConfigurationException() {
        Config config = new Config();
        assertThrows(InvalidConfigurationException.class, () -> config.setMaxHandSize(0));

        assertThrows(InvalidConfigurationException.class, () -> config.setMaxHandSize(-1));
    }

    @Test
    void when_setBleedingDamageLessThan0_throw_invalidConfigurationException() {
        Config config = new Config();
        assertThrows(InvalidConfigurationException.class, () -> config.setBleedingDamageAmount(-1));
    }

    @Test
    void when_setInitialManaCapacityLessThan0_throw_invalidConfigurationException() {
        Config config = new Config();
        assertThrows(InvalidConfigurationException.class, () -> config.setInitialManaCapacity(-1));
    }

    @Test
    void when_setMaxManaCapacityLessThan1_throw_invalidConfigurationException() {
        Config config = new Config();
        assertThrows(InvalidConfigurationException.class, () -> config.setMaxManaCapacity(0));
    }

    @Test
    void when_setMaxManaCapacityLessThanInitialManaCapacity_throw_invalidConfigurationException() {
        Config config = new Config();
        assertThrows(InvalidConfigurationException.class, () -> config.setInitialManaCapacity(11));

        config.setInitialManaCapacity(2);
        assertThrows(InvalidConfigurationException.class, () -> config.setMaxManaCapacity(1));
    }

    @Test
    void when_setPlayers2NonUnique_throw_invalidConfigurationException() {
        Config config = new Config();
        Set<Player> players = new HashSet<>();
        players.add(new Player("Berk"));
        players.add(new Player("Berk"));
        assertThrows(InvalidConfigurationException.class, () -> config.setPlayers(players));
    }

    @Test
    void when_setPlayers1_throw_invalidConfigurationException() {
        Config config = new Config();
        Set<Player> players = new HashSet<>();
        players.add(new Player("Berk"));
        assertThrows(InvalidConfigurationException.class, () -> config.setPlayers(players));
    }

    @Test
    void when_setAll_expect_successful() {
        Config config = new Config();

        Set<Player> players = new HashSet<>();
        players.add(new Player("Rahmi"));
        players.add(new Player("Berk"));
        players.add(new Player("Sefkatli"));
        config.setPlayers(players);
        assertEquals(players, config.getPlayers());

        List<Card> deck = new ArrayList<>();
        deck.add(new Card(20));
        deck.add(new Card(20));
        deck.add(new Card(20));
        deck.add(new Card(20));
        deck.add(new Card(20));
        config.setDeck(deck);
        assertEquals(deck, config.getDeck());

        config.setInitialHealth(20);
        assertEquals(20, config.getInitialHealth());

        config.setInitialManaCapacity(4);
        assertEquals(4, config.getInitialManaCapacity());

        config.setInitialHandSize(2);
        assertEquals(2, config.getInitialHandSize());

        config.setMaxManaCapacity(10);
        assertEquals(10, config.getMaxManaCapacity());

        config.setMaxHandSize(3);
        assertEquals(3, config.getMaxHandSize());

        config.setBleedingDamageAmount(5);
        assertEquals(5, config.getBleedingDamageAmount());
    }

    @Test
    void when_toString_expect_correct() {
        Config config = new Config();
        String expectedString = "Players: " + config.getPlayers().stream().map(Player::getName)
                .collect(Collectors.joining(", ")) + System.lineSeparator() +
                "Deck: " + config.getDeck().stream().map(card -> "" + card.getCost())
                .collect(Collectors.joining(", ")) + System.lineSeparator() +
                "Initial health: " + config.getInitialHealth() + System.lineSeparator() +
                "Initial mana capacity: " + config.getInitialManaCapacity() + System.lineSeparator() +
                "Initial hand size: " + config.getInitialHandSize() + System.lineSeparator() +
                "Max mana capacity: " + config.getMaxManaCapacity() + System.lineSeparator() +
                "Max hand size: " + config.getMaxHandSize() + System.lineSeparator() +
                "Bleeding damage amount: " + config.getBleedingDamageAmount() + System.lineSeparator();
        assertEquals(expectedString, config.toString());
    }
}
