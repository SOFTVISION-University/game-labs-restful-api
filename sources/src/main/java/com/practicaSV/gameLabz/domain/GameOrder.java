package com.practicaSV.gameLabz.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.practicaSV.gameLabz.utils.visitors.GameOrderVisitor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "orderType")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes(value = {@JsonSubTypes.Type(value = CashGameOrder.class),
        @JsonSubTypes.Type(value = PointsGameOrder.class),
        @JsonSubTypes.Type(value = KeyGameOrder.class),
        @JsonSubTypes.Type(value = GiftGameOrder.class)})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class GameOrder {

    public static final String CASH_TYPE = "cash";

    public static final String POINT_TYPE = "point";

    public static final String KEY_TYPE = "key";

    public static final String GIFT_TYPE = "gift";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    protected User user;

    @Transient
    protected List<GameOffer> gameOfferList;

    @Transient
    protected List<Game> ownedGames;

    @Transient
    protected List<GeneratedKey> keys;

    protected Long dateOfOrder;

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

    public List<GameOffer> getGameOfferList() {
        return gameOfferList;
    }

    public void setGameOfferList(List<GameOffer> gameOfferList) {
        this.gameOfferList = gameOfferList;
    }

    public List<Game> getOwnedGames() {
        return ownedGames;
    }

    public void setOwnedGames(List<Game> ownedGames) {
        this.ownedGames = ownedGames;
    }

    public List<GeneratedKey> getKeys() {
        return keys;
    }

    public void setKeys(List<GeneratedKey> keys) {
        this.keys = keys;
    }

    public Long getDateOfOrder() {
        return dateOfOrder;
    }

    public void setDateOfOrder(Long dateOfOrder) {
        this.dateOfOrder = dateOfOrder;
    }

    public abstract void accept(GameOrderVisitor gameOrderVisitor);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameOrder gameOrder = (GameOrder) o;

        return id != null ? id.equals(gameOrder.id) : gameOrder.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
