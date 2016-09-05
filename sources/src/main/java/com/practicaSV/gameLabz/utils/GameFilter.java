package com.practicaSV.gameLabz.utils;

import com.practicaSV.gameLabz.domain.Game;

import java.util.Optional;

public class GameFilter extends GetFilter {

    public Optional<String> getFilterName() {

        return Optional.ofNullable((String) map.get(NAME));
    }

    public Optional<Boolean> getFilterOffer() {

        return Optional.ofNullable((Boolean) map.get(HAS_OFFER));
    }

    public Optional<Game.GameGenre> getFilterGenre() {

        return Optional.ofNullable((Game.GameGenre) map.get(GENRE));
    }

    public Optional<Long> getFilterRelBefore() {

        return Optional.ofNullable((Long) map.get(RELEASED_BEFORE));
    }

    public Optional<Long> getFilterRelAfter() {

        return Optional.ofNullable((Long) map.get(RELEASED_AFTER));
    }

}
