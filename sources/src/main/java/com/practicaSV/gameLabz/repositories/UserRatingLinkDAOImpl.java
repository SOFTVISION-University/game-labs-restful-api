package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.domain.UserRatingLink;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Optional;

@Repository
@Transactional
public class UserRatingLinkDAOImpl implements UserRatingLinkDAO {

    private static final String GET_RATING_LINK_BY_USER = "select r from UserRatingLink r where r.user = :user";

    private static final String GET_RATING_LINK_BY_GAME = "select r from UserRatingLink r where r.game = :game";

    private static final String GET_RATING_LINK_BY_GAME_AND_USER = "select r from UserRatingLink r where r.game = :game and r.user = :user";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserRatingLink saveRatingLink(UserRatingLink ratingLink) {

        entityManager.persist(ratingLink);
        return ratingLink;
    }

    @Override
    public Collection<UserRatingLink> getRatingLinksByUser(User user) {

        TypedQuery<UserRatingLink> query = entityManager.createQuery(GET_RATING_LINK_BY_USER, UserRatingLink.class).setParameter("user", user);

        return query.getResultList();
    }

    @Override
    public Collection<UserRatingLink> getRatingLinksByGame(Game game) {

        TypedQuery<UserRatingLink> query = entityManager.createQuery(GET_RATING_LINK_BY_GAME, UserRatingLink.class).setParameter("game", game);

        return query.getResultList();
    }

    @Override
    public Optional<UserRatingLink> getRatingLinkById(Long ratingLinkId) {

        return Optional.ofNullable(entityManager.find(UserRatingLink.class, ratingLinkId));
    }

    @Override
    public Optional<UserRatingLink> getRatingLinkByGameAndUser(Game game, User user) {

        TypedQuery<UserRatingLink> query = entityManager.createQuery(GET_RATING_LINK_BY_GAME_AND_USER, UserRatingLink.class).setParameter("game", game).setParameter("user", user);

        try {
            UserRatingLink userRatingLink = query.getSingleResult();
            userRatingLink = entityManager.find(UserRatingLink.class, userRatingLink.getId());
            return Optional.ofNullable(userRatingLink);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

}
