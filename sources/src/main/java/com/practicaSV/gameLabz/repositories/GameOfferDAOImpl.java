package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.domain.GameGenreLink;
import com.practicaSV.gameLabz.domain.GameOffer;
import com.practicaSV.gameLabz.domain.GameOfferLink;
import com.practicaSV.gameLabz.utils.GameOfferFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class GameOfferDAOImpl implements GameOfferDAO {

    private static final String GET_GAME_OFFER_LINKS = "select g from GameOfferLink g where g.gameOffer = :gameOffer";

    private static final String GET_GAME_OFFER_LINKS_BY_IDS = "select g from GameOfferLink g where g.gameOffer in :offerList";

    private static final String GET_GENRE_LINK_BY_IDS = "select l from GameGenreLink l where l.game in :gameList";

    private static final String GET_ALL_OFFERS = "select o from GameOffer o";

    @Autowired
    private GameDAO gameDAO;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public GameOffer saveGameOffer(GameOffer gameOffer) {

        entityManager.persist(gameOffer);

        gameOffer.getGames().stream()
                .map(g -> {
                    GameOfferLink link = new GameOfferLink();
                    link.setGame(g);
                    link.setGameOffer(gameOffer);
                    return link;
                })
                .forEach(link -> entityManager.persist(link));

        return gameOffer;
    }

    @Override
    public Collection<GameOffer> getAll() {
        TypedQuery<GameOffer> query = entityManager.createQuery(GET_ALL_OFFERS, GameOffer.class);
        return query.getResultList();
    }

    @Override
    public Optional<GameOffer> getGameOfferById(Long gameOfferId) {

        GameOffer gameOffer = entityManager.find(GameOffer.class, gameOfferId);

        if (gameOffer == null) {
            return Optional.empty();
        }

        TypedQuery<GameOfferLink> query = entityManager.createQuery(GET_GAME_OFFER_LINKS, GameOfferLink.class).setParameter("gameOffer", gameOffer);
        Collection<GameOfferLink> gameOfferLinks = query.getResultList();

        List<Game> games = gameOfferLinks.stream().map(gameOfferLink -> gameDAO.getGameById(gameOfferLink.getGame().getId()).get()).collect(Collectors.toList());

        gameOffer.setGames(games);

        return Optional.of(gameOffer);
    }

    @Override
    public Collection<GameOffer> getGameOffersByFilters(GameOfferFilter gameOfferFilter) {

        List<Predicate> predicates = new ArrayList<>();

        //main query for game offers

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<GameOffer> mainQuery = criteriaBuilder.createQuery(GameOffer.class);
        Root<GameOfferLink> mainRoot = mainQuery.from(GameOfferLink.class);
        mainQuery.select(mainRoot.get("gameOffer"));
        mainQuery.distinct(true);

        if (gameOfferFilter.getFilterName().isPresent() || gameOfferFilter.getFilterGenre().isPresent()) {

            List<Predicate> subQueryPredicates = new ArrayList<>();

            Subquery<Game> gameSubQuery = mainQuery.subquery(Game.class);
            Root<Game> gameRoot = gameSubQuery.from(Game.class);
            gameSubQuery.select(gameRoot);

            subQueryPredicates.add(criteriaBuilder.equal(mainRoot.get("game"), gameRoot));

            if (gameOfferFilter.getFilterName().isPresent()) {

                subQueryPredicates.add(criteriaBuilder.equal(gameRoot.get("name"), gameOfferFilter.getFilterName().get()));
            }

            if (gameOfferFilter.getFilterGenre().isPresent()) {

                Subquery<GameGenreLink> gameGenreLinkSubQuery = gameSubQuery.subquery(GameGenreLink.class);
                Root<GameGenreLink> gameGenreLinkRoot = gameGenreLinkSubQuery.from(GameGenreLink.class);
                gameGenreLinkSubQuery.select(gameGenreLinkRoot);

                gameGenreLinkSubQuery.where(criteriaBuilder.and(criteriaBuilder.equal(gameGenreLinkRoot.get("genre"), gameOfferFilter.getFilterGenre().get())),
                        criteriaBuilder.equal(gameGenreLinkRoot.get("game"), gameRoot));

                subQueryPredicates.add(criteriaBuilder.exists(gameGenreLinkSubQuery));
            }
            gameSubQuery.where(criteriaBuilder.and(subQueryPredicates.toArray(new Predicate[predicates.size()])));
            predicates.add(criteriaBuilder.exists(gameSubQuery));
        }

        //select by offer type

        if (gameOfferFilter.getFilterOfferType().isPresent()) {

            predicates.add(criteriaBuilder.equal(mainRoot.get("gameOffer").get("offerType"), gameOfferFilter.getFilterOfferType().get()));
        }

        //select by promotion

        if (gameOfferFilter.getFilterPromotion().isPresent()) {

            if (gameOfferFilter.getFilterPromotion().get()) {
                predicates.add(criteriaBuilder.isNotNull(mainRoot.get("gameOffer").get("promotion")));
            } else {
                predicates.add(criteriaBuilder.isNull(mainRoot.get("gameOffer").get("promotion")));
            }
        }

        //main query where

        if (!predicates.isEmpty()) {
            mainQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        }

        List<GameOffer> gameOffers = entityManager.createQuery(mainQuery).getResultList();

        List<Game> gameList = new ArrayList<>();
        TypedQuery<GameOfferLink> offerLinkQuery = entityManager.createQuery(GET_GAME_OFFER_LINKS_BY_IDS, GameOfferLink.class).setParameter("offerList", gameOffers);
        Map<GameOffer, List<Game>> gameOfferListMap = new HashMap<>();

        offerLinkQuery.getResultList().stream()
                .forEach(o -> {
                    if (gameOfferListMap.containsKey(o.getGameOffer())) {
                        List<Game> games = new ArrayList<>(gameOfferListMap.get(o.getGameOffer()));
                        games.add(o.getGame());
                        gameOfferListMap.replace(o.getGameOffer(), games);
                    } else {
                        gameOfferListMap.put(o.getGameOffer(), Arrays.asList(o.getGame()));
                    }
                    gameList.add(o.getGame());
                });

        TypedQuery<GameGenreLink> genreLinkQuery = entityManager.createQuery(GET_GENRE_LINK_BY_IDS, GameGenreLink.class).setParameter("gameList", gameList);
        Map<Game, List<Game.GameGenre>> gameListMap = new HashMap<>();

        genreLinkQuery.getResultList().stream()
                .forEach(g -> {
                    if (gameListMap.containsKey(g.getGame())) {
                        List<Game.GameGenre> genres = new ArrayList<>(gameListMap.get(g.getGame()));
                        genres.add(g.getGenre());
                        gameListMap.replace(g.getGame(), genres);
                    } else {
                        gameListMap.put(g.getGame(), Arrays.asList(g.getGenre()));
                    }
                });

        gameList.stream()
                .forEach(g -> g.setGameGenres(gameListMap.get(g)));

        gameOffers.stream()
                .forEach(o -> {
                    List<Game> games = gameOfferListMap.get(o).stream()
                            .map(g -> gameList.get(gameList.indexOf(g))).collect(Collectors.toList());
                    o.setGames(games);
                });

        return gameOffers;
    }

    @Override
    public void removeGameOffer(GameOffer gameOffer) {

        TypedQuery<GameOfferLink> query = entityManager.createQuery(GET_GAME_OFFER_LINKS, GameOfferLink.class).setParameter("gameOffer", gameOffer);
        Collection<GameOfferLink> gameOfferLinks = query.getResultList();

        gameOfferLinks.stream()
                .forEach(gameOfferLink -> entityManager.remove(gameOfferLink));

        entityManager.remove(gameOffer);
    }
}
