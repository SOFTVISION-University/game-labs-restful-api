package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.domain.GameOrder;
import com.practicaSV.gameLabz.domain.OwnedGame;
import com.practicaSV.gameLabz.domain.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class GameOrderDAOImpl implements GameOrderDAO {

    private static final String GET_GAMES_BY_USER = "select o from OwnedGame o where o.user = :user";

    private static final String GET_GAME_BY_USER_AND_GAME = "select o from OwnedGame o where o.user = :user and o.game = :game";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public GameOrder saveGameOrder(GameOrder gameOrder) {

        entityManager.persist(gameOrder);

        return gameOrder;
    }

    @Override
    public OwnedGame saveBoughtGame(OwnedGame ownedGame) {

        entityManager.persist(ownedGame);

        return ownedGame;
    }

    @Override
    public List<Game> getGamesByUser(User user) {

        TypedQuery<OwnedGame> query = entityManager.createQuery(GET_GAMES_BY_USER, OwnedGame.class).setParameter("user", user);
        List<Game> gameList = new ArrayList<>();

        query.getResultList().stream()
                .forEach(ownedGame -> gameList.add(ownedGame.getGame()));
        return gameList;
    }

    @Override
    public Optional<Game> getGameByUserAndGame(User user, Game game) {

        TypedQuery<OwnedGame> query = entityManager.createQuery(GET_GAME_BY_USER_AND_GAME, OwnedGame.class).setParameter("user", user).setParameter("game", game);

        try {
            OwnedGame ownedGame = query.getSingleResult();
            ownedGame = entityManager.find(OwnedGame.class, ownedGame.getId());
            return Optional.ofNullable(ownedGame.getGame());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

}
