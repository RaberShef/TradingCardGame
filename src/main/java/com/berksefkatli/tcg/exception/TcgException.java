package com.berksefkatli.tcg.exception;

public class TcgException extends RuntimeException {
    public TcgException(String message) {
        super(message);
    }

    public static class CannotPlayCardNotInHandException extends TcgException {
        public static final String MESSAGE = "Only the cards in hand can be played.";

        public CannotPlayCardNotInHandException() {
            super(MESSAGE);
        }
    }

    public static class NotEnoughManaException extends TcgException {
        public static final String MESSAGE = "Player does not have enough mana to play requested card.";

        public NotEnoughManaException() {
            super(MESSAGE);
        }
    }

    public static class GameNotLiveException extends TcgException {
        public static final String MESSAGE = "Plays can only be made while the game is live.";

        public GameNotLiveException() {
            super(MESSAGE);
        }
    }

    public static class InvalidNameException extends TcgException {
        public static final String MESSAGE = "Player name cannot be empty or null";

        public InvalidNameException() {
            super(MESSAGE);
        }
    }

    public static class InvalidConfigurationException extends TcgException {
        public InvalidConfigurationException(String message) {
            super(message);
        }
    }
}
