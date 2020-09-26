package com.berksefkatli.tcg;

import com.berksefkatli.tcg.Game.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {

    @Test
    public void when_addPlayer_expect_increaseInPlayerSize() {
        Game game = new Game();
        game.addPlayer(new Player("Rahmi"));
        game.addPlayer(new Player("Berk"));
        assertEquals(2, game.getPlayers().size());
        game.addPlayer(new Player("Sefkatli"));
        assertEquals(3, game.getPlayers().size());
    }

    @Test
    public void when_playerNamesAreEqual_throw_uniquePlayerException() {
        assertThrows(UniquePlayerException.class, () -> {
            Game game = new Game();
            game.addPlayer(new Player("Berk"));
            game.addPlayer(new Player("Berk"));
        });
    }

    @Test
    public void when_onlyOnePlayer_throw_notEnoughPlayersException() {
        assertThrows(NotEnoughPlayersException.class, () -> {
            Game game = new Game();
            game.addPlayer(new Player("Berk"));
            game.start();
        });
    }

    @Test
    public void when_removeNonExistentPlayer_throw_nonExistentPlayerException() {
        assertThrows(NonExistentPlayerException.class, () -> {
            Game game = new Game();
            game.addPlayer(new Player("Berk"));
            game.addPlayer(new Player("Rahmi"));
            game.removePlayer(new Player("Sefkatli"));
        });
    }

    @Test
    public void when_removeExistentPlayer_expect_reductionInPlayerSize() {
        Game game = new Game();
        game.addPlayer(new Player("Rahmi"));
        game.addPlayer(new Player("Berk"));
        assertEquals(2, game.getPlayers().size());
        game.removePlayer(new Player("Berk"));
        assertEquals(1, game.getPlayers().size());
    }

    @Test
    public void when_atGameStart_expect_correctInitialization() {
        Game game = new Game(getDeckWithSameCostCards(0));
        game.addPlayer(new Player("Rahmi"));
        game.addPlayer(new Player("Berk"));
        game.addPlayer(new Player("Sefkatli"));
        game.start();
        game.getPlayers().forEach(player -> {
            if (player.equals(game.getActivePlayer())) {
                assertEquals(1, player.getManaSlots());
                assertEquals(1, player.getMana());
                assertEquals(4, player.getHand().size());
                assertEquals(16, player.getDeckSize());
            } else {
                assertEquals(0, player.getManaSlots());
                assertEquals(0, player.getMana());
                assertEquals(3, player.getHand().size());
                assertEquals(17, player.getDeckSize());
            }
            assertEquals(30, player.getHealth());
        });
    }

    @Test
    public void when_addPlayerAfterGameStart_throw_cannotChangePlayersAfterGameStartException() {
        assertThrows(CannotChangePlayersAfterGameStartException.class, () -> {
            Game game = new Game();
            game.addPlayer(new Player("Rahmi"));
            game.addPlayer(new Player("Berk"));
            game.start();
            game.addPlayer(new Player("Sefkatli"));
        });
    }

    @Test
    public void when_removePlayerAfterGameStart_throw_cannotChangePlayersAfterGameStartException() {
        assertThrows(CannotChangePlayersAfterGameStartException.class, () -> {
            Game game = new Game();
            game.addPlayer(new Player("Rahmi"));
            game.addPlayer(new Player("Berk"));
            game.start();
            game.removePlayer(new Player("Berk"));
        });
    }

    @Test
    public void when_startTurn_expect_increasedManaAndManaSlots() {
        Game game = new Game(getDeckWithSameCostCards(0));
        game.addPlayer(new Player("Rahmi"));
        game.addPlayer(new Player("Berk"));
        game.addPlayer(new Player("Sefkatli"));
        game.start();

        assertEquals(1, game.getActivePlayer().getMana());
        assertEquals(1, game.getActivePlayer().getManaSlots());

        skipRound(game, 1);

        assertEquals(2, game.getActivePlayer().getMana());
        assertEquals(2, game.getActivePlayer().getManaSlots());
    }

    @Test
    public void when_startTurn_expect_manaAndManaSlotsNoMoreThan10() {
        Game game = new Game(getDeckWithSameCostCards(0));
        game.addPlayer(new Player("Rahmi"));
        game.addPlayer(new Player("Berk"));
        game.addPlayer(new Player("Sefkatli"));
        game.start();

        skipRound(game, 12);

        assertEquals(10, game.getActivePlayer().getMana());
        assertEquals(10, game.getActivePlayer().getManaSlots());
    }

    @Test
    public void when_startTurn_expect_drawFromDeck() {
        Game game = new Game(getDeckWithSameCostCards(0));
        game.addPlayer(new Player("Rahmi"));
        game.addPlayer(new Player("Berk"));
        game.addPlayer(new Player("Sefkatli"));
        game.start();

        assertEquals(4, game.getActivePlayer().getHand().size());

        skipRound(game, 1);

        assertEquals(5, game.getActivePlayer().getHand().size());
    }

    @Test
    public void when_startTurnWithFullHand_expect_overload() {
        Game game = new Game(getDeckWithSameCostCards(0));
        game.addPlayer(new Player("Rahmi"));
        game.addPlayer(new Player("Berk"));
        game.addPlayer(new Player("Sefkatli"));
        game.start();

        assertEquals(4, game.getActivePlayer().getHand().size());
        skipRound(game, 1);
        assertEquals(5, game.getActivePlayer().getHand().size());
        skipRound(game, 1);
        assertEquals(5, game.getActivePlayer().getHand().size());
    }

    @Test
    public void when_noPlayableCard_expect_autoSkipTurn() {
        Game game = new Game(getDeckWithSameCostCards(1));
        game.addPlayer(new Player("Rahmi"));
        game.addPlayer(new Player("Berk"));
        game.addPlayer(new Player("Sefkatli"));
        game.start();

        Player firstPlayer = game.getActivePlayer();

        assertEquals(1, firstPlayer.getMana());
        game.playCard(new Card(1));
        assertEquals(0, firstPlayer.getMana());

        // Active player should be changed since first player has no mana left.
        assertNotEquals(game.getActivePlayer(), firstPlayer);
    }

    @Test
    public void when_noPlayableCard_expect_autoSkipTurnMultiple() {
        Game game = new Game(getDeckWithSameCostCards(4));
        game.addPlayer(new Player("Rahmi"));
        game.addPlayer(new Player("Berk"));
        game.addPlayer(new Player("Sefkatli"));
        game.start();

        // Since there are only 4 cost cards in the decks,
        // each player should automatically skip until they have 4 mana.
        assertEquals(4, game.getActivePlayer().getMana());
    }

    @Test
    public void when_playCardNotInHand_throw_CannotPlayCardNotInHandException() {
        assertThrows(CannotPlayCardNotInHandException.class, () -> {
            Game game = new Game(getDeckWithSameCostCards(1));
            game.addPlayer(new Player("Rahmi"));
            game.addPlayer(new Player("Berk"));
            game.addPlayer(new Player("Sefkatli"));
            game.start();
            game.playCard(new Card(9));
        });
    }

    @Test
    public void when_playCardWithCostHigherThanMana_throw_NotEnoughManaException() {
        assertThrows(NotEnoughManaException.class, () -> {
            Game game = new Game(getDeckWithSameCostCards(1));
            game.addPlayer(new Player("Rahmi"));
            game.addPlayer(new Player("Berk"));
            game.addPlayer(new Player("Sefkatli"));
            game.start();
            // This is cheating :)
            game.getActivePlayer().getHand().add(new Card(20));
            game.playCard(new Card(20));
        });
    }

    @Test
    public void when_playCardBeforeStart_throw_GameNotStartedException() {
        assertThrows(GameNotStartedException.class, () -> {
            Game game = new Game();
            game.addPlayer(new Player("Rahmi"));
            game.addPlayer(new Player("Berk"));
            game.addPlayer(new Player("Sefkatli"));
            game.playCard(new Card(1));
        });
    }

    @Test
    public void when_playCardWithCostHigherThanMana_throw_GameEndedException() {
        assertThrows(GameEndedException.class, () -> {
            Game game = new Game();
            game.addPlayer(new Player("Rahmi"));
            game.addPlayer(new Player("Berk"));
            game.addPlayer(new Player("Sefkatli"));
            game.start();

            // Use bleeding out effect to end the game.
            while (game.isGameNotEnded()) {
                skipRound(game, 1);
            }

            game.playCard(new Card(1));
        });
    }

    @Test
    public void when_playerHealthLessThan0AfterReceivingDamage_expect_playerToLose() {
        Game game = new Game(getDeckWithSameCostCards(1));
        game.addPlayer(new Player("Rahmi"));
        game.addPlayer(new Player("Berk"));
        game.addPlayer(new Player("Sefkatli"));
        game.start();

        Player firstPlayer = game.getActivePlayer();
        // Use bleeding out effect to leave starting player with 1 health and all other players with 2 health.
        while (firstPlayer.getHealth() > 1) {
            skipRound(game, 1);
        }
        assertEquals(3, game.getPlayers().size());
        game.getPlayers().forEach(player -> {
            if (player.equals(firstPlayer)) {
                assertEquals(1, player.getHealth());
            } else {
                assertEquals(2, player.getHealth());
            }
        });

        // This is cheating :)
        firstPlayer.getHand().add(new Card(10));
        game.playCard(new Card(10));

        assertEquals(1, game.getPlayers().size());
    }

    @Test
    public void when_playerHealthEqualTo0AfterReceivingDamage_expect_playerToLose() {
        Game game = new Game(getDeckWithSameCostCards(1));
        game.addPlayer(new Player("Rahmi"));
        game.addPlayer(new Player("Berk"));
        game.addPlayer(new Player("Sefkatli"));
        game.start();

        Player firstPlayer = game.getActivePlayer();
        // Use bleeding out effect to leave starting player with 1 health and all other players with 2 health.
        while (firstPlayer.getHealth() > 1) {
            skipRound(game, 1);
        }
        assertEquals(3, game.getPlayers().size());
        game.getPlayers().forEach(player -> {
            if (player.equals(firstPlayer)) {
                assertEquals(1, player.getHealth());
            } else {
                assertEquals(2, player.getHealth());
            }
        });

        // This is cheating :)
        firstPlayer.getHand().add(new Card(2));
        game.playCard(new Card(2));

        assertEquals(1, game.getPlayers().size());
    }

    @Test
    public void when_drawCardFromEmptyDeck_expect_bleedingOut() {
        Game game = new Game();
        game.addPlayer(new Player("Rahmi"));
        game.addPlayer(new Player("Berk"));
        game.addPlayer(new Player("Sefkatli"));
        game.start();

        // Skip rounds until there are no more cards in the deck
        while (game.getActivePlayer().getDeckSize() > 0) {
            skipRound(game, 1);
        }
        assertEquals(30, game.getActivePlayer().getHealth());

        skipRound(game, 1);
        assertEquals(29, game.getActivePlayer().getHealth());
    }

    @Test
    public void when_playCard_expect_doDamage_reduceMana_discardCard() {
        Game game = new Game(getDeckWithSameCostCards(1));
        game.addPlayer(new Player("Rahmi"));
        game.addPlayer(new Player("Berk"));
        game.addPlayer(new Player("Sefkatli"));
        game.start();

        Player firstPlayer = game.getActivePlayer();
        int initialMana = firstPlayer.getMana();
        int initialHandSize = firstPlayer.getHand().size();

        game.playCard(new Card(1));

        game.getPlayers().forEach(player -> {
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
        Game game = new Game(getDeckWithSameCostCards(0));
        game.addPlayer(new Player("Rahmi"));
        game.addPlayer(new Player("Berk"));
        game.addPlayer(new Player("Sefkatli"));
        game.start();

        Player firstPlayer = game.getActivePlayer();
        int initialMana = firstPlayer.getMana();
        int initialHandSize = firstPlayer.getHand().size();

        game.playCard(new Card(0));

        game.getPlayers().forEach(player -> {
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
        Game game = new Game(getDeckWithSameCostCards(1));
        game.addPlayer(new Player("Rahmi"));
        game.addPlayer(new Player("Berk"));
        game.addPlayer(new Player("Sefkatli"));
        game.start();

        skipRound(game, 1);

        assertEquals(2, game.getActivePlayer().getMana());
        assertEquals(2, game.getActivePlayer().getManaSlots());

        game.playCard(new Card(1));

        assertEquals(1, game.getActivePlayer().getMana());
        assertEquals(2, game.getActivePlayer().getManaSlots());

        skipRound(game, 1);

        assertEquals(3, game.getActivePlayer().getMana());
        assertEquals(3, game.getActivePlayer().getManaSlots());
    }

    @Test
    public void when_startTurnWithMaxManaSlots_expect_manaToRefill() {
        Game game = new Game(getDeckWithSameCostCards(1));
        game.addPlayer(new Player("Rahmi"));
        game.addPlayer(new Player("Berk"));
        game.addPlayer(new Player("Sefkatli"));
        game.start();

        skipRound(game, 12);

        assertEquals(10, game.getActivePlayer().getMana());
        assertEquals(10, game.getActivePlayer().getManaSlots());

        game.playCard(new Card(1));

        assertEquals(9, game.getActivePlayer().getMana());
        assertEquals(10, game.getActivePlayer().getManaSlots());

        skipRound(game, 1);

        assertEquals(10, game.getActivePlayer().getMana());
        assertEquals(10, game.getActivePlayer().getManaSlots());
    }

    private void skipRound(Game game, int times) {
        // Skip without playing any cards for 'times' rounds
        for (int i = 0; i < times; i++) {
            for (int j = 0; j < game.getPlayers().size(); j++) {
                game.endTurn();
            }
        }
    }

    private List<Card> getDeckWithSameCostCards(int cost) {
        // Supplying an all zero or one deck as the starting deck of a game guarantees that
        // every player will have playable cards on their turn.
        // This effectively disables automatic turn skipping feature as long as players keep one card in hand.
        List<Card> customDeck = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            customDeck.add(new Card(cost));
        }
        return customDeck;
    }

}