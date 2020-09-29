package com.berksefkatli.tcg;

import com.berksefkatli.tcg.exception.TcgException.CannotPlayCardNotInHandException;
import com.berksefkatli.tcg.exception.TcgException.GameNotLiveException;
import com.berksefkatli.tcg.exception.TcgException.NotEnoughManaException;
import com.berksefkatli.tcg.model.Card;
import com.berksefkatli.tcg.model.Config;
import com.berksefkatli.tcg.model.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {

    @Test
    public void when_atGameStart_expect_correctInitialization() {
        Config config = getConfigWithAllSameCostDeck(0);
        Game game = new Game(System.out, config);
        game.getCopyOfPlayers().forEach(player -> {
            if (player.equals(game.getCopyOfActivePlayer())) {
                assertEquals(config.getInitialManaCapacity() + 1, player.getManaCapacity());
                assertEquals(config.getInitialManaCapacity() + 1, player.getMana());
                assertEquals(config.getInitialHandSize() + 1, player.getHand().size());
                assertEquals(config.getDeck().size() - config.getInitialHandSize() - 1, player.getDeck().size());
            } else {
                assertEquals(config.getInitialManaCapacity(), player.getManaCapacity());
                assertEquals(0, player.getMana());
                assertEquals(config.getInitialHandSize(), player.getHand().size());
                assertEquals(config.getDeck().size() - config.getInitialHandSize(), player.getDeck().size());
            }
            assertEquals(config.getInitialHealth(), player.getHealth());
        });
    }

    @Test
    public void when_startTurn_expect_increasedManaAndManaCapacity() {
        Game game = startNewTestGame(0);

        assertEquals(1, game.getCopyOfActivePlayer().getMana());
        assertEquals(1, game.getCopyOfActivePlayer().getManaCapacity());

        skipRound(game, 1);

        assertEquals(2, game.getCopyOfActivePlayer().getMana());
        assertEquals(2, game.getCopyOfActivePlayer().getManaCapacity());
    }

    @Test
    public void when_startTurn_expect_manaAndManaCapacityNoMoreThan10() {
        Game game = startNewTestGame(0);

        skipRound(game, 12);

        assertEquals(10, game.getCopyOfActivePlayer().getMana());
        assertEquals(10, game.getCopyOfActivePlayer().getManaCapacity());
    }

    @Test
    public void when_startTurn_expect_drawFromDeck() {
        Game game = startNewTestGame(0);

        assertEquals(4, game.getCopyOfActivePlayer().getHand().size());

        skipRound(game, 1);

        assertEquals(5, game.getCopyOfActivePlayer().getHand().size());
    }

    @Test
    public void when_startTurnWithFullHand_expect_overload() {
        Game game = startNewTestGame(0);

        assertEquals(4, game.getCopyOfActivePlayer().getHand().size());
        skipRound(game, 1);
        assertEquals(5, game.getCopyOfActivePlayer().getHand().size());
        skipRound(game, 1);
        assertEquals(5, game.getCopyOfActivePlayer().getHand().size());
    }

    @Test
    public void when_noPlayableCard_expect_autoSkipTurn() {
        Game game = startNewTestGame(1);

        Player firstPlayer = game.getCopyOfActivePlayer();

        assertEquals(1, firstPlayer.getMana());
        game.playCard(new Card(1));

        // Active player should be changed since first player has no mana left.
        assertNotEquals(game.getCopyOfActivePlayer(), firstPlayer);
    }

    @Test
    public void when_noPlayableCard_expect_autoSkipTurnMultiple() {
        Game game = startNewTestGame(4);

        // Since there are only 4 cost cards in the decks,
        // each player should automatically skip until they have 4 mana.
        assertEquals(4, game.getCopyOfActivePlayer().getMana());
    }

    @Test
    public void when_playCardNotInHand_throw_CannotPlayCardNotInHandException() {
        assertThrows(CannotPlayCardNotInHandException.class, () -> {
            Game game = startNewTestGame(1);
            game.playCard(new Card(9));
        });
    }

    @Test
    public void when_playCardWithCostHigherThanMana_throw_NotEnoughManaException() {
        assertThrows(NotEnoughManaException.class, () -> {
            // Make sure all players get one zero cost card to avoid auto skipping
            // and one card with cost higher than their mana to be able to play it and cause the exception.
            Config config = new Config();
            config.setInitialHandSize(2);
            config.setInitialManaCapacity(0);
            List<Card> customDeck = new ArrayList<>();
            customDeck.add(new Card(2));
            customDeck.add(new Card(0));
            config.setDeck(customDeck);

            Game game = new Game(System.out, config);
            game.playCard(new Card(2));
        });
    }

    @Test
    public void when_playCardAfterGameEnded_throw_GameNotLiveException() {
        assertThrows(GameNotLiveException.class, () -> {
            Game game = new Game(System.out, new Config());

            // Use bleeding out effect to end the game.
            while (game.isGameLive()) {
                skipRound(game, 1);
            }

            game.playCard(new Card(1));
        });
    }

    @Test
    public void when_playerHealthLessThan0AfterReceivingDamage_expect_playerToLose() {
        // Increase initial mana capacity to avoid auto skipping even with all 3 cost deck.
        Config config = getConfigWithAllSameCostDeck(3);
        config.setInitialManaCapacity(3);

        Game game = new Game(System.out, config);

        // Use bleeding out effect to leave starting player with 1 health and all other players with 2 health.
        while (game.getCopyOfActivePlayer().getHealth() > 1) {
            skipRound(game, 1);
        }
        assertTrue(game.getCopyOfPlayers().size() >= 2);
        game.getCopyOfPlayers().forEach(player -> {
            if (player.equals(game.getCopyOfActivePlayer())) {
                assertEquals(1, player.getHealth());
            } else {
                assertEquals(2, player.getHealth());
            }
        });

        game.playCard(new Card(3));

        assertEquals(1, game.getCopyOfPlayers().size());
    }

    @Test
    public void when_playerHealthEqualTo0AfterReceivingDamage_expect_playerToLose() {
        // Increase initial mana capacity to avoid auto skipping even with all 2 cost deck.
        Config config = getConfigWithAllSameCostDeck(2);
        config.setInitialManaCapacity(2);

        Game game = new Game(System.out, config);

        // Use bleeding out effect to leave starting player with 1 health and all other players with 2 health.
        while (game.getCopyOfActivePlayer().getHealth() > 1) {
            skipRound(game, 1);
        }
        assertTrue(game.getCopyOfPlayers().size() >= 2);

        for (Player player : game.getCopyOfPlayers()) {
            if (player.equals(game.getCopyOfActivePlayer())) {
                assertEquals(1, player.getHealth());
            } else {
                assertEquals(2, player.getHealth());
            }
        }

        game.playCard(new Card(2));

        assertEquals(1, game.getCopyOfPlayers().size());
    }

    @Test
    public void when_drawCardFromEmptyDeck_expect_bleedingDamage() {
        Game game = new Game(System.out, new Config());

        // Skip rounds until there are no more cards in the deck
        while (game.getCopyOfActivePlayer().getDeck().size() > 0) {
            skipRound(game, 1);
        }
        assertEquals(30, game.getCopyOfActivePlayer().getHealth());

        skipRound(game, 1);
        assertEquals(29, game.getCopyOfActivePlayer().getHealth());
    }

    @Test
    public void when_deathFromBleedingOut_expect_nextPlayerToDraw() {
        // Auto skip every turn
        Game game = startNewTestGame(20);

        // First player dies while drawing a new card, next player does the same and last player wins.
        assertEquals(1, game.getCopyOfPlayers().size());
    }

    @Test
    public void when_playCard_expect_doDamage_reduceMana_discardCard() {
        Game game = startNewTestGame(1);

        Player firstPlayer = game.getCopyOfActivePlayer();
        int initialMana = firstPlayer.getMana();
        int initialHandSize = firstPlayer.getHand().size();

        game.playCard(new Card(1));

        game.getCopyOfPlayers().forEach(player -> {
            if (player.equals(firstPlayer)) {
                // Use one mana and discard one card from hand.
                assertEquals(1, initialMana - player.getMana());
                assertEquals(1, initialHandSize - player.getHand().size());
            } else {
                // Deal 1 damage to all other players.
                assertEquals(29, player.getHealth());
            }
        });
    }

    @Test
    public void when_playDudCard_expect_discardCard() {
        Game game = startNewTestGame(0);

        Player firstPlayer = game.getCopyOfActivePlayer();
        int initialMana = firstPlayer.getMana();
        int initialHandSize = firstPlayer.getHand().size();

        game.playCard(new Card(0));

        game.getCopyOfPlayers().forEach(player -> {
            if (player.equals(firstPlayer)) {
                // Discard one card from hand.
                assertEquals(0, initialMana - player.getMana());
                assertEquals(1, initialHandSize - player.getHand().size());
            } else {
                // Deal 0 damage to all other players.
                assertEquals(30, player.getHealth());
            }
        });
    }

    @Test
    public void when_startTurn_expect_manaToRefill() {
        Game game = startNewTestGame(1);

        skipRound(game, 1);

        assertEquals(2, game.getCopyOfActivePlayer().getMana());
        assertEquals(2, game.getCopyOfActivePlayer().getManaCapacity());

        game.playCard(new Card(1));

        assertEquals(1, game.getCopyOfActivePlayer().getMana());
        assertEquals(2, game.getCopyOfActivePlayer().getManaCapacity());

        skipRound(game, 1);

        assertEquals(3, game.getCopyOfActivePlayer().getMana());
        assertEquals(3, game.getCopyOfActivePlayer().getManaCapacity());
    }

    @Test
    public void when_startTurnWithMaxManaCapacity_expect_manaToRefill() {
        Game game = startNewTestGame(1);

        skipRound(game, 12);

        assertEquals(10, game.getCopyOfActivePlayer().getMana());
        assertEquals(10, game.getCopyOfActivePlayer().getManaCapacity());

        game.playCard(new Card(1));

        assertEquals(9, game.getCopyOfActivePlayer().getMana());
        assertEquals(10, game.getCopyOfActivePlayer().getManaCapacity());

        skipRound(game, 1);

        assertEquals(10, game.getCopyOfActivePlayer().getMana());
        assertEquals(10, game.getCopyOfActivePlayer().getManaCapacity());
    }

    @Test
    public void when_playerIsModified_expect_noChange() {
        Game game = new Game(System.out, new Config());
        game.getCopyOfPlayers().get(0).setHealth(99);
        assertNotEquals(99, game.getCopyOfPlayers().get(0).getHealth());
    }

    private Game startNewTestGame(int allCardCosts) {
        return new Game(System.out, getConfigWithAllSameCostDeck(allCardCosts));
    }

    private void skipRound(Game game, int times) {
        // Skip without playing any cards for 'times' rounds
        for (int i = 0; i < times; i++) {
            for (int j = 0; j < game.getCopyOfPlayers().size(); j++) {
                game.endTurn();
            }
        }
    }

    private Config getConfigWithAllSameCostDeck(int cost) {
        // Supplying an all zero or one deck guarantees that every player will have playable cards on their turn
        // This effectively disables automatic turn skipping feature as long as players keep one card in hand.
        Config config = new Config();

        List<Card> customDeck = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            customDeck.add(new Card(cost));
        }
        config.setDeck(customDeck);

        Set<Player> players = new HashSet<>();
        players.add(new Player("Rahmi"));
        players.add(new Player("Berk"));
        players.add(new Player("Sefkatli"));
        config.setPlayers(players);
        return config;
    }

}