package com.berksefkatli.tcg;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class TcgTests {

    @Test
    public void when_quit_expect_close() {
        String stringBuilder = "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());
        System.setIn(in);
        Tcg.main(null);
    }
}
