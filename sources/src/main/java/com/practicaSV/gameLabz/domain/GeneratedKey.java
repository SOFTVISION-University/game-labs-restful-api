package com.practicaSV.gameLabz.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.practicaSV.gameLabz.utils.JsonViews;

import javax.persistence.*;

@Entity
@Table
public class GeneratedKey {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String generatedKey;

    @OneToOne
    private User user;

    @OneToOne
    private Game game;

    @OneToOne
    @JsonView(JsonViews.Hidden.class)
    private GameOrder order;

    //true if deleted => not visible for app
    private boolean logicalDelete;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGeneratedKey() {
        return generatedKey;
    }

    public void setGeneratedKey(String generatedKey) {
        this.generatedKey = generatedKey;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public GameOrder getOrder() {
        return order;
    }

    public void setOrder(GameOrder order) {
        this.order = order;
    }

    public boolean getLogicalDelete() {
        return logicalDelete;
    }

    public void setLogicalDelete(boolean logicalDelete) {
        this.logicalDelete = logicalDelete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeneratedKey that = (GeneratedKey) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static class Builder {

        private String generatedKey;

        private User user;

        private Game game;

        private GameOrder order;

        private boolean logicalDelete;

        public Builder generatedKey(String generatedKey) {
            this.generatedKey = generatedKey;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder game(Game game) {
            this.game = game;
            return this;
        }

        public Builder order(GameOrder order) {
            this.order = order;
            return this;
        }

        public Builder logicalDelete(boolean logicalDelete) {
            this.logicalDelete = logicalDelete;
            return this;
        }

        public GeneratedKey build() {
            return new GeneratedKey(this);
        }
    }

    private GeneratedKey(Builder b) {

        this.generatedKey = b.generatedKey;
        this.user = b.user;
        this.game = b.game;
        this.order = b.order;
        this.logicalDelete = b.logicalDelete;
    }

    public GeneratedKey() {}
}
