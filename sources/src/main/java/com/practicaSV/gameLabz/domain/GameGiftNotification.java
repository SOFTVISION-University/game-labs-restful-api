package com.practicaSV.gameLabz.domain;

import java.util.List;

public class GameGiftNotification {

    private User user;

    private Long date;

    private List<Game> games;

    private List<GeneratedKey> keys;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public List<GeneratedKey> getKeys() {
        return keys;
    }

    public void setKeys(List<GeneratedKey> keys) {
        this.keys = keys;
    }

    public static class Builder {

        private User user;

        private Long date;

        private List<Game> games;

        private List<GeneratedKey> keys;

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder date(Long date) {
            this.date = date;
            return this;
        }

        public Builder games(List<Game> games) {
            this.games = games;
            return this;
        }

        public Builder keys(List<GeneratedKey> keys) {
            this.keys = keys;
            return this;
        }

        public GameGiftNotification build() {
            return new GameGiftNotification(this);
        }
    }

    private GameGiftNotification(Builder b) {
        this.user = b.user;
        this.date = b.date;
        this.games = b.games;
        this.keys = b.keys;
    }

    public GameGiftNotification() {
    }
}
