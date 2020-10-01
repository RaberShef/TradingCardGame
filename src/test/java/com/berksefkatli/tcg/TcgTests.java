package com.berksefkatli.tcg;

import com.berksefkatli.tcg.model.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TcgTests {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TEST_CONFIG_FILE_PATH = "src/test/resources/testConfig.json";

    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;
    private PrintStream out;
    private PrintStream err;

    @BeforeEach
    public void setUpStreams() throws IOException {
        objectMapper.writeValue(new File(TEST_CONFIG_FILE_PATH), new Config());
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        out = new PrintStream(outContent);
        err = new PrintStream(errContent);
    }

    @Test
    void when_quit_expect_close() {
        String stringBuilder = "3" + System.lineSeparator();
        InputStream in = new ByteArrayInputStream(stringBuilder.getBytes());
        System.setIn(in);
        System.setOut(out);
        System.setErr(err);
        Tcg.main(null);
        assertTrue(errContent.toString().isEmpty());
        assertTrue(outContent.toString().contains("1) Start game"));
    }
}
