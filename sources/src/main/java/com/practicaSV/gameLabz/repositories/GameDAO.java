package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.domain.GameGenreLink;
import com.practicaSV.gameLabz.utils.GameFilter;

import java.util.Collection;
import java.util.Optional;

public interface GameDAO {

    Game saveGame(Game game);

    void removeGame(Game game);

    Optional<Game> getGameById(Long gameId);

    Collection<Game> getGamesByFilters(GameFilter gameFilter);

    Collection<Game> getAll();

    void updateGenreLink(Game game);

    void removeGameGenreLink(GameGenreLink gameGenreLink);
}
