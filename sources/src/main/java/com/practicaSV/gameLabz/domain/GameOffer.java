package com.practicaSV.gameLabz.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table
public class GameOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Transient
    private List<Game> games;

    private BigDecimal priceCash;

    private Long pricePoints;

    private BigDecimal promotion;

    @Enumerated(EnumType.STRING)
    private OfferType offerType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public BigDecimal getPriceCash() {
        return priceCash;
    }

    public void setPriceCash(BigDecimal priceCash) {
        this.priceCash = priceCash;
    }

    public Long getPricePoints() {
        return pricePoints;
    }

    public void setPricePoints(Long pricePoints) {
        this.pricePoints = pricePoints;
    }

    public BigDecimal getPromotion() {
        return promotion;
    }

    public void setPromotion(BigDecimal promotion) {
        this.promotion = promotion;
    }

    public OfferType getOfferType() {
        return offerType;
    }

    public void setOfferType(OfferType offerType) {
        this.offerType = offerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameOffer gameOffer = (GameOffer) o;

        return id != null ? id.equals(gameOffer.id) : gameOffer.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return games.toString() + " - " + priceCash +  "$ - " + pricePoints + "p - " + offerType;
    }

    public enum OfferType {
        BUNDLE,
        SINGLE
    }
}
