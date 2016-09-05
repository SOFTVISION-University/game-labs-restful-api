package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.domain.GameOrder;
import com.practicaSV.gameLabz.domain.OwnedGame;
import com.practicaSV.gameLabz.domain.User;

import java.util.List;
import java.util.Optional;

public interface GameOrderDAO {

    GameOrder saveGameOrder(GameOrder gameOrder);

    OwnedGame saveBoughtGame(OwnedGame ownedGame);

    List<Game> getGamesByUser(User user);

    Optional<Game> getGameByUserAndGame(User user, Game game);
}
