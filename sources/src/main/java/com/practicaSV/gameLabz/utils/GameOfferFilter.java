package com.practicaSV.gameLabz.utils;

import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.domain.GameOffer;

import java.util.Optional;

public class GameOfferFilter extends GetFilter {

    public Optional<String> getFilterName() {

        return Optional.ofNullable((String) map.get(NAME));
    }

    public Optional<Game.GameGenre> getFilterGenre() {

        return Optional.ofNullable((Game.GameGenre) map.get(GENRE));
    }

    public Optional<GameOffer.OfferType> getFilterOfferType() {

        return Optional.ofNullable((GameOffer.OfferType) map.get(OFFER_TYPE));
    }

    public Optional<Boolean> getFilterPromotion() {

        return Optional.ofNullable((Boolean) map.get(IS_PROMOTION));
    }

    public Optional<String> getOrderBy() {

        return Optional.ofNullable((String) map.get(ORDER_BY));
    }

    public Optional<OrderDirection> getOrderDirection() {

        return Optional.ofNullable((OrderDirection) map.get(ORDER_DIRECTION));
    }
}
