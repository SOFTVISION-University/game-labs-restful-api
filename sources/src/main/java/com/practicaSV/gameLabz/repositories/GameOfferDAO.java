package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.GameOffer;
import com.practicaSV.gameLabz.utils.GameOfferFilter;

import java.util.Collection;
import java.util.Optional;

public interface GameOfferDAO {

    GameOffer saveGameOffer(GameOffer gameOffer);

    Collection<GameOffer> getAll();

    Optional<GameOffer> getGameOfferById(Long gameOfferId);

    Collection<GameOffer> getGameOffersByFilters(GameOfferFilter gameOfferFilter);

    void removeGameOffer(GameOffer gameOffer);
}
