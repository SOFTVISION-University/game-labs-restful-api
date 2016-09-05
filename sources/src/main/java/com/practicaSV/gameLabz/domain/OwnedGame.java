package com.practicaSV.gameLabz.domain;

import javax.persistence.*;

@Entity
@Table
public class OwnedGame {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    private User user;

    @OneToOne
    private Game game;

    @OneToOne
    private GameOrder order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
