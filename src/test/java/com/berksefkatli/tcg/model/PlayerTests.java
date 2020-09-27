package com.berksefkatli.tcg.model;

import com.berksefkatli.tcg.exception.TcgException.InvalidNameException;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTests {

    @Test
    public void when_playerNameIsNull_throw_invalidNameException() {
        assertThrows(InvalidNameException.class, () -> new Player(null));
    }

    @Test
    public void when_playerNameIsEmpty_throw_invalidNameException() {
        assertThrows(InvalidNameException.class, () -> new Player(""));
    }

    @Test
    public void when_playerNameIsEmptySpace_throw_invalidNameException() {
        assertThrows(InvalidNameException.class, () -> new Player(" "));
    }

    @Test
    public void when_equals_expect_equalsOfName() {
        Player player = new Player("Berk");
        Player player2 = new Player("Berk");
        Player player3 = new Player("Rahmi");
        assertEquals(player, player2);
        assertNotEquals(player, player3);
    }

    @Test
    public void when_hashCode_expect_hashOfName() {
        Player player = new Player("Berk");
        assertEquals(Objects.hash(player.getName()), player.hashCode());
    }

}
