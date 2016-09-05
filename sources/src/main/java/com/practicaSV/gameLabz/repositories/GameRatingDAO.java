package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.domain.GameRating;

import java.util.Optional;

public interface GameRatingDAO {

    GameRating saveRating(GameRating rating);

    Optional<GameRating> getRatingByGame(Game game);
}
