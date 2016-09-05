package com.practicaSV.gameLabz.utils.comparators;

import com.practicaSV.gameLabz.domain.GameOffer;
import com.practicaSV.gameLabz.utils.GetFilter;

import java.math.BigDecimal;

public class GameOfferCashPriceComparator extends GameOfferComparator {

    @Override
    public int compare(GameOffer o1, GameOffer o2) {

        BigDecimal result = o1.getPriceCash().subtract(o2.getPriceCash());

        if (getOrderDirection() == null || getOrderDirection() == GetFilter.OrderDirection.ASC) {
            return result.intValue();
        } else {
            return result.negate().intValue();
        }
    }
}
