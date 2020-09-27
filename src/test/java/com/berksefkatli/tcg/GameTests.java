package com.berksefkatli.tcg;

import com.berksefkatli.tcg.exception.TcgException.*;
import com.berksefkatli.tcg.model.Card;
import com.berksefkatli.tcg.model.Config;
import com.berksefkatli.tcg.model.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {

    @Test
    public void when_addPlayer_expect_increaseInPlayerSize() {
        Game game = new Game();
        assertEquals(0, game.getPlayers().size());
        game.addPlayer(new Player("Rahmi"));
        assertEquals(1, game.getPlayers().size());
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
            game.removePlayer(new Player("Sefkatli"));
        });
    }

    @Test
    public void when_removeExistentPlayer_expect_reductionInPlayerSize() {
        Game game = new Game();
        game.addPlayer(new Player("Rahmi"));
        assertEquals(1, game.getPlayers().size());
        game.removePlayer(new Player("Rahmi"));
        assertEquals(0, game.getPlayers().size());
    }

    @Test
    public void when_atGameStart_expect_correctInitialization() {
        Game game = startNewTestGame(0);
        Config config = game.getConfig();
        game.getPlayers().forEach(player -> {
            if (player.equals(game.getActivePlayer())) {
                assertEquals(config.getInitialPlayerManaSlot() + 1, player.getManaSlot());
                assertEquals(config.getInitialPlayerManaSlot() + 1, player.getMana());
                assertEquals(config.getInitialPlayerHandSize() + 1, player.getHand().size());
                assertEquals(config.getStartingDeck().size() - config.getInitialPlayerHandSize() - 1, player.getDeck().size());
            } else {
                assertEquals(config.getInitialPlayerManaSlot(), player.getManaSlot());
                assertEquals(0, player.getMana());
                assertEquals(config.getInitialPlayerHandSize(), player.getHand().size());
                assertEquals(config.getStartingDeck().size() - config.getInitialPlayerHandSize(), player.getDeck().size());
            }
            assertEquals(config.getInitialPlayerHealth(), player.getHealth());
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
        Game game = startNewTestGame(0);

        assertEquals(1, game.getActivePlayer().getMana());
        assertEquals(1, game.getActivePlayer().getManaSlot());

        skipRound(game, 1);

        assertEquals(2, game.getActivePlayer().getMana());
        assertEquals(2, game.getActivePlayer().getManaSlot());
    }

    @Test
    public void when_startTurn_expect_manaAndManaSlotsNoMoreThan10() {
        Game game = startNewTestGame(0);

        skipRound(game, 12);

        assertEquals(10, game.getActivePlayer().getMana());
        assertEquals(10, game.getActivePlayer().getManaSlot());
    }

    @Test
    public void when_startTurn_expect_drawFromDeck() {
        Game game = startNewTestGame(0);

        assertEquals(4, game.getActivePlayer().getHand().size());

        skipRound(game, 1);

        assertEquals(5, game.getActivePlayer().getHand().size());
    }

    @Test
    public void when_startTurnWithFullHand_expect_overload() {
        Game game = startNewTestGame(0);

        assertEquals(4, game.getActivePlayer().getHand().size());
        skipRound(game, 1);
        assertEquals(5, game.getActivePlayer().getHand().size());
        skipRound(game, 1);
        assertEquals(5, game.getActivePlayer().getHand().size());
    }

    @Test
    public void when_noPlayableCard_expect_autoSkipTurn() {
        Game game = startNewTestGame(1);

        Player firstPlayer = game.getActivePlayer();

        assertEquals(1, firstPlayer.getMana());
        game.playCard(new Card(1));
        assertEquals(0, firstPlayer.getMana());

        // Active player should be changed since first player has no mana left.
        assertNotEquals(game.getActivePlayer(), firstPlayer);
    }

    @Test
    public void when_noPlayableCard_expect_autoSkipTurnMultiple() {
        Game game = startNewTestGame(4);

        // Since there are only 4 cost cards in the decks,
        // each player should automatically skip until they have 4 mana.
        assertEquals(4, game.getActivePlayer().getMana());
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
            Game game = startNewTestGame(1);
            // This is cheating :)
            game.getActivePlayer().getHand().add(new Card(20));
            game.playCard(new Card(20));
        });
    }

    @Test
    public void when_playCardBeforeStart_throw_GameNotStartedException() {
        assertThrows(GameNotLiveException.class, () -> {
            Game game = new Game();
            game.addPlayer(new Player("Rahmi"));
            game.addPlayer(new Player("Berk"));
            game.addPlayer(new Player("Sefkatli"));
            game.playCard(new Card(1));
        });
    }

    @Test
    public void when_playCardAfterGameEnded_throw_GameNotLiveException() {
        assertThrows(GameNotLiveException.class, () -> {
            Game game = new Game();
            game.addPlayer(new Player("Rahmi"));
            game.addPlayer(new Player("Berk"));
            game.addPlayer(new Player("Sefkatli"));
            game.start();

            // Use bleeding out effect to end the game.
            while (game.isGameLive()) {
                skipRound(game, 1);
            }

            game.playCard(new Card(1));
        });
    }

    @Test
    public void when_playerHealthLessThan0AfterReceivingDamage_expect_playerToLose() {
        Game game = startNewTestGame(1);

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
        Game game = startNewTestGame(1);

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
        while (game.getActivePlayer().getDeck().size() > 0) {
            skipRound(game, 1);
        }
        assertEquals(30, game.getActivePlayer().getHealth());

        skipRound(game, 1);
        assertEquals(29, game.getActivePlayer().getHealth());
    }

    @Test
    public void when_playCard_expect_doDamage_reduceMana_discardCard() {
        Game game = startNewTestGame(1);

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
        Game game = startNewTestGame(0);

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
        Game game = startNewTestGame(1);

        skipRound(game, 1);

        assertEquals(2, game.getActivePlayer().getMana());
        assertEquals(2, game.getActivePlayer().getManaSlot());

        game.playCard(new Card(1));

        assertEquals(1, game.getActivePlayer().getMana());
        assertEquals(2, game.getActivePlayer().getManaSlot());

        skipRound(game, 1);

        assertEquals(3, game.getActivePlayer().getMana());
        assertEquals(3, game.getActivePlayer().getManaSlot());
    }

    @Test
    public void when_startTurnWithMaxManaSlots_expect_manaToRefill() {
        Game game = startNewTestGame(1);

        skipRound(game, 12);

        assertEquals(10, game.getActivePlayer().getMana());
        assertEquals(10, game.getActivePlayer().getManaSlot());

        game.playCard(new Card(1));

        assertEquals(9, game.getActivePlayer().getMana());
        assertEquals(10, game.getActivePlayer().getManaSlot());

        skipRound(game, 1);

        assertEquals(10, game.getActivePlayer().getMana());
        assertEquals(10, game.getActivePlayer().getManaSlot());
    }

    private Game startNewTestGame(int allCardCosts) {
        Game game = new Game(getConfigWithAllSameCostDeck(allCardCosts));
        game.addPlayer(new Player("Rahmi"));
        game.addPlayer(new Player("Berk"));
        game.addPlayer(new Player("Sefkatli"));
        game.start();
        return game;
    }

    private void skipRound(Game game, int times) {
        // Skip without playing any cards for 'times' rounds
        for (int i = 0; i < times; i++) {
            for (int j = 0; j < game.getPlayers().size(); j++) {
                game.endTurn();
            }
        }
    }

    private Config getConfigWithAllSameCostDeck(int cost) {
        // Supplying an all zero or one deck as the starting deck of a game guarantees that
        // every player will have playable cards on their turn.
        // This effectively disables automatic turn skipping feature as long as players keep one card in hand.
        Config config = new Config();
        List<Card> customDeck = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            customDeck.add(new Card(cost));
        }
        config.setStartingDeck(customDeck);
        return config;
    }

}