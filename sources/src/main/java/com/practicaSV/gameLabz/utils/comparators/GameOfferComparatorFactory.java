package com.practicaSV.gameLabz.utils.comparators;

import com.practicaSV.gameLabz.utils.GameOfferFilter;
import com.practicaSV.gameLabz.utils.GetFilter;

public class GameOfferComparatorFactory {

    public static GameOfferComparator buildComparator(GameOfferFilter gameOfferFilter) {

        GameOfferComparator gameOfferComparator;

        switch (gameOfferFilter.getOrderBy().get()) {
            case GetFilter.PRICE_CASH:
                gameOfferComparator = new GameOfferCashPriceComparator();

            case GetFilter.PRICE_POINTS:
                gameOfferComparator = new GameOfferPointPriceComparator();

            case GetFilter.RELEASE_DATE:
                gameOfferComparator = new GameOfferReleaseDateComparator();

            case GetFilter.NAME:
                gameOfferComparator = new GameOfferNameComparator();

            default: gameOfferComparator = new GameOfferNameComparator();
        }

        if (gameOfferFilter.getOrderDirection().isPresent()) {
            gameOfferComparator.setOrderDirection(gameOfferFilter.getOrderDirection().get());
        }

        return gameOfferComparator;
    }
}
