package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.domain.GameGenreLink;
import com.practicaSV.gameLabz.domain.GameOfferLink;
import com.practicaSV.gameLabz.utils.GameFilter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Transactional
public class GameDAOImpl implements GameDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String GET_ALL_GAMES = "select g from Game g";

    private static final String GET_GAME_GENRE_BY_GAME = "select g from GameGenreLink g where g.game = :game";

    @Override
    public Game saveGame(Game game) {

        entityManager.persist(game);

        game.getGameGenres().stream()
                .map((a) -> {
                    GameGenreLink link = new GameGenreLink();
                    link.setGame(game);
                    link.setGenre(a);
                    return link;
                })
                .forEach((link) -> entityManager.persist(link));

        return game;
    }

    @Override
    public void removeGame(Game game) {
        
    }

    @Override
    public Optional<Game> getGameById(Long gameId) {

        Game game = entityManager.find(Game.class, gameId);

        if (game == null) {
            return Optional.empty();
        }

        game.setGameGenres(getGenreByGame(game).stream().map(GameGenreLink::getGenre).collect(Collectors.toList()));

        return Optional.of(game);
    }

    @Override
    public Collection<Game> getGamesByFilters(GameFilter gameFilter) {

        List<Predicate> predicates = new ArrayList<>();

        //main query for games

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Game> mainQuery = criteriaBuilder.createQuery(Game.class);
        Root<Game> mainRoot = mainQuery.from(Game.class);
        mainQuery.select(mainRoot);

        //select games by name

        if (gameFilter.getFilterName().isPresent()) {

            predicates.add(criteriaBuilder.equal(mainRoot.get("name"), gameFilter.getFilterName().get()));
        }

        //select games with certain genres

        if (gameFilter.getFilterGenre().isPresent()) {

            Subquery<GameGenreLink> gameGenreLinkSubquery = mainQuery.subquery(GameGenreLink.class);
            Root<GameGenreLink> gameGenreLinkRoot = gameGenreLinkSubquery.from(GameGenreLink.class);
            gameGenreLinkSubquery.select(gameGenreLinkRoot);
            gameGenreLinkSubquery.where(criteriaBuilder.and(criteriaBuilder.equal(gameGenreLinkRoot.get("genre"), gameFilter.getFilterGenre().get())), criteriaBuilder.equal(gameGenreLinkRoot.get("game"), mainRoot));

            predicates.add(criteriaBuilder.exists(gameGenreLinkSubquery));
        }

        //select games that already have offer

        if (gameFilter.getFilterOffer().isPresent()) {

            Subquery<GameOfferLink> gameOfferLinkSubquery = mainQuery.subquery(GameOfferLink.class);
            Root<GameOfferLink> gameOfferLinkRoot = gameOfferLinkSubquery.from(GameOfferLink.class);
            gameOfferLinkSubquery.select(gameOfferLinkRoot);
            gameOfferLinkSubquery.where(criteriaBuilder.equal(gameOfferLinkRoot.get("game"), mainRoot));

            if (gameFilter.getFilterOffer().get()) {
                predicates.add(criteriaBuilder.exists(gameOfferLinkSubquery));
            } else {
                predicates.add(criteriaBuilder.not(criteriaBuilder.exists(gameOfferLinkSubquery)));
            }
        }

        if (gameFilter.getFilterRelAfter().isPresent()) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(mainRoot.get("releaseDate"), gameFilter.getFilterRelAfter().get()));
        }

        if (gameFilter.getFilterRelBefore().isPresent()) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(mainRoot.get("releaseDate"), gameFilter.getFilterRelBefore().get()));
        }

        //main query where

        if (!predicates.isEmpty()) {
            mainQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        }

        List<Game> gameList = entityManager.createQuery(mainQuery).getResultList();
        gameList.stream()
                .forEach(game -> game.setGameGenres(getGenreByGame(game).stream().map(GameGenreLink::getGenre).collect(Collectors.toList())));

        return entityManager.createQuery(mainQuery).getResultList();
    }

    @Override
    public Collection<Game> getAll() {

        TypedQuery<Game> query = entityManager.createQuery(GET_ALL_GAMES, Game.class);
        return query.getResultList();
    }

    @Override
    public void updateGenreLink(Game game) {

        Collection<GameGenreLink> genreLinksDB = getGenreByGame(game);

        //Add new genre links
        game.getGameGenres().stream()
                .filter(gameGenre -> genreLinksDB.stream().noneMatch(gameGenreLink -> gameGenreLink.getGenre().equals(gameGenre)))
                .map(gameGenre -> {
                    GameGenreLink gameGenreLink = new GameGenreLink();
                    gameGenreLink.setGenre(gameGenre);
                    gameGenreLink.setGame(game);
                    return gameGenreLink;
                })
                .forEach(gameGenreLink -> entityManager.persist(gameGenreLink));

        //Remove unused genre links
        genreLinksDB.stream()
                .filter(gameGenreLink -> !game.getGameGenres().contains(gameGenreLink.getGenre()))
                .forEach(gameGenreLink -> {
                    if (!entityManager.contains(gameGenreLink)) {
                        gameGenreLink = entityManager.find(GameGenreLink.class, gameGenreLink.getId());
                    }
                    entityManager.remove(gameGenreLink);
                });
    }

    private Collection<GameGenreLink> getGenreByGame(Game game) {

        TypedQuery<GameGenreLink> query = entityManager.createQuery(GET_GAME_GENRE_BY_GAME, GameGenreLink.class).setParameter("game", game);

        return query.getResultList();
    }

    @Override
    public void removeGameGenreLink(GameGenreLink gameGenreLink) {

        if (!entityManager.contains(gameGenreLink)) {
            gameGenreLink = entityManager.find(GameGenreLink.class, gameGenreLink.getId());
        }
        entityManager.remove(gameGenreLink);
    }

}
