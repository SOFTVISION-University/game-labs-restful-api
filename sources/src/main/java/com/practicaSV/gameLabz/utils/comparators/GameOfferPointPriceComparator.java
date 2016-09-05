package com.practicaSV.gameLabz.utils.comparators;

import com.practicaSV.gameLabz.domain.GameOffer;
import com.practicaSV.gameLabz.utils.GetFilter;

public class GameOfferPointPriceComparator extends GameOfferComparator {
    @Override
    public int compare(GameOffer o1, GameOffer o2) {

        if (getOrderDirection() == null || getOrderDirection() == GetFilter.OrderDirection.ASC) {
            return o1.getPricePoints().intValue() - o2.getPricePoints().intValue();
        } else {
            return o2.getPricePoints().intValue() - o1.getPricePoints().intValue();
        }
    }
}
