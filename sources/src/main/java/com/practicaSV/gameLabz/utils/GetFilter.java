package com.practicaSV.gameLabz.utils;

import java.util.HashMap;
import java.util.Map;

public abstract class GetFilter {

    protected Map<String, Object> map = new HashMap<>();

    public static final String NAME = "name";

    public static final String HAS_OFFER = "hasOffer";

    public static final String GENRE = "genre";

    public static final String RELEASED_BEFORE = "relBefore";

    public static final String RELEASED_AFTER = "relAfter";

    public static final String OFFER_TYPE = "type";

    public static final String IS_PROMOTION = "isPromotion";

    public static final String ORDER_BY = "orderBy";

    public static final String ORDER_DIRECTION = "orderDirection";

    public static final String PRICE_CASH = "priceCash";

    public static final String PRICE_POINTS = "pricePoints";

    public static final String RELEASE_DATE = "releaseDate";

    public enum OrderDirection {
        ASC,
        DESC
    }

    public void createFilter(String key, Object value) {

        map.put(key, value);
    }
}
