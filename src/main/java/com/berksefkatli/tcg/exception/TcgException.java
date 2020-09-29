package com.berksefkatli.tcg.exception;

public class TcgException extends RuntimeException {
    public TcgException(String message) {
        super(message);
    }

    public static class CannotPlayCardNotInHandException extends TcgException {
        public static final String message = "Only the cards in hand can be played.";

        public CannotPlayCardNotInHandException() {
            super(message);
        }
    }

    public static class NotEnoughManaException extends TcgException {
        public static final String message = "Player does not have enough mana to play requested card.";

        public NotEnoughManaException() {
            super(message);
        }
    }

    public static class GameNotLiveException extends TcgException {
        public static final String message = "Plays can only be made while the game is live.";

        public GameNotLiveException() {
            super(message);
        }
    }

    public static class InvalidNameException extends TcgException {
        public static final String message = "Player name cannot be empty or null";

        public InvalidNameException() {
            super(message);
        }
    }

    public static class InvalidConfigurationException extends TcgException {
        public InvalidConfigurationException(String message) {
            super(message);
        }
    }
}
