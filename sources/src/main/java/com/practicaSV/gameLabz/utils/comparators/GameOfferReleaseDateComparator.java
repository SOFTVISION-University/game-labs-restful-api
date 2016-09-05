package com.practicaSV.gameLabz.utils.comparators;

import com.practicaSV.gameLabz.domain.GameOffer;
import com.practicaSV.gameLabz.utils.GetFilter;

public class GameOfferReleaseDateComparator extends GameOfferComparator {

    @Override
    public int compare(GameOffer o1, GameOffer o2) {

        if (o1.getOfferType() == GameOffer.OfferType.SINGLE && o2.getOfferType() == GameOffer.OfferType.BUNDLE) {
            return -1;
        }

        if (o1.getOfferType() == GameOffer.OfferType.BUNDLE && o2.getOfferType() == GameOffer.OfferType.SINGLE) {
            return 1;
        }

        if (o1.getOfferType() == GameOffer.OfferType.BUNDLE && o2.getOfferType() == GameOffer.OfferType.BUNDLE) {
            return 0;
        }

        int result = o1.getGames().stream().findFirst().get().getReleaseDate().intValue() - o2.getGames().stream().findFirst().get().getReleaseDate().intValue();

        if (getOrderDirection() == null || getOrderDirection() == GetFilter.OrderDirection.ASC) {

            return result;
        } else {
            return (-1) * result;
        }
    }
}
