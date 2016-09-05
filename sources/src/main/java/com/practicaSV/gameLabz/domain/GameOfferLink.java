package com.practicaSV.gameLabz.domain;

import javax.persistence.*;

@Entity
@Table
public class GameOfferLink {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    private Game game;

    @OneToOne
    private GameOffer gameOffer;

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

    public GameOffer getGameOffer() {
        return gameOffer;
    }

    public void setGameOffer(GameOffer gameOffer) {
        this.gameOffer = gameOffer;
    }
}
