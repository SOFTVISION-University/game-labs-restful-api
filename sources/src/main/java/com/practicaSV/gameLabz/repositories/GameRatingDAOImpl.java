package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.domain.GameRating;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
@Transactional
public class GameRatingDAOImpl implements GameRatingDAO {

    private static final String GET_RATING_BY_GAME = "select r from GameRating r where r.game = :game";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public GameRating saveRating(GameRating rating) {

        entityManager.persist(rating);

        return rating;
    }

    @Override
    public Optional<GameRating> getRatingByGame(Game game) {

        TypedQuery<GameRating> query = entityManager.createQuery(GET_RATING_BY_GAME, GameRating.class).setParameter("game", game);

        try {
            GameRating rating = query.getSingleResult();
            rating = entityManager.find(GameRating.class, rating.getId());
            return Optional.ofNullable(rating);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }


}
