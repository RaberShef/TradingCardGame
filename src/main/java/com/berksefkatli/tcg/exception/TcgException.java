package com.berksefkatli.tcg.exception;

public class TcgException extends RuntimeException {
    public TcgException(String message) {
        super(message);
    }

    public static class UniquePlayerException extends TcgException {
        public static final String message = "Players in a game must be unique";

        public UniquePlayerException() {
            super(message);
        }
    }

    public static class NonExistentPlayerException extends TcgException {
        public static final String message = "The player does not exist.";

        public NonExistentPlayerException() {
            super(message);
        }
    }

    public static class CannotChangePlayersAfterGameStartException extends TcgException {
        public static final String message = "Players cannot be changed after the preparation phase";

        public CannotChangePlayersAfterGameStartException() {
            super(message);
        }
    }

    public static class NotEnoughPlayersException extends TcgException {
        public static final String message = "The game cannot be started unless there are at least two players.";

        public NotEnoughPlayersException() {
            super(message);
        }
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

}
