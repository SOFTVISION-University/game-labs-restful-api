package com.practicaSV.gameLabz.domain;

import javax.persistence.*;

@Entity
@Table
public class GameGenreLink {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    private Game game;

    @Enumerated(EnumType.STRING)
    private Game.GameGenre genre;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game.GameGenre getGenre() {
        return genre;
    }

    public void setGenre(Game.GameGenre genre) {
        this.genre = genre;
    }
}
