package com.practicaSV.gameLabz.domain;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
public class Game {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private String description;

    private Long releaseDate;

    @Transient
    private List<GameGenre> gameGenres;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Long releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<GameGenre> getGameGenres() {
        return gameGenres;
    }

    public void setGameGenres(List<GameGenre> gameGenres) {
        this.gameGenres = gameGenres;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        return id.equals(game.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public enum GameGenre {
        RPG,
        FPS,
        MOBA
    }

    public static class Builder {

        private String name;

        private String description;

        private Long releaseDate;

        private List<GameGenre> gameGenres;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder desc(String description) {
            this.description = description;
            return this;
        }

        public Builder relDate(Long releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public Builder genres(List<GameGenre> gameGenres) {
            this.gameGenres = gameGenres;
            return this;
        }

        public Game build() {
            return new Game(this);
        }
    }

    private Game(Builder b) {

        this.name = b.name;
        this.description = b.description;
        this.releaseDate = b.releaseDate;
        this.gameGenres = b.gameGenres;
    }

    public Game() {}

    @Override
    public String toString() {
        return name + " - " + releaseDate + " - " + gameGenres + " - " + releaseDate;
    }
}
