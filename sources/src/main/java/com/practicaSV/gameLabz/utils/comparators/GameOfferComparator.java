package com.practicaSV.gameLabz.utils.comparators;

import com.practicaSV.gameLabz.domain.GameOffer;
import com.practicaSV.gameLabz.utils.GetFilter;

import java.util.Comparator;

public abstract class GameOfferComparator implements Comparator<GameOffer> {

    private GetFilter.OrderDirection orderDirection;

    public GetFilter.OrderDirection getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(GetFilter.OrderDirection orderDirection) {
        this.orderDirection = orderDirection;
    }

}
