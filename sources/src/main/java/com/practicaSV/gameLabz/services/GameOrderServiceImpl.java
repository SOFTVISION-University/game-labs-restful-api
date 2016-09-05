package com.practicaSV.gameLabz.services;

import com.practicaSV.gameLabz.domain.*;
import com.practicaSV.gameLabz.exceptions.AuthorizationException;
import com.practicaSV.gameLabz.exceptions.InvalidValueException;
import com.practicaSV.gameLabz.repositories.*;
import com.practicaSV.gameLabz.utils.visitors.GameOrderVisitor;
import com.practicaSV.gameLabz.utils.websocket.TopicConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class GameOrderServiceImpl implements GameOrderService, GameOrderVisitor {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private GameOfferDAO gameOfferDAO;

    @Autowired
    private GameOrderDAO gameOrderDAO;

    @Autowired
    private FriendDAO friendDAO;

    @Autowired
    private GeneratedKeyDAO generatedKeyDAO;

    @Autowired
    private GameOrderPDFService gameOrderPDFService;

    @Autowired
    private UserProfileDAO userProfileDAO;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Value("${email.enabled}")
    private Boolean emailEnabled;

    @Override
    public GameOrder execute(GameOrder gameOrder) {

        gameOrder.accept(this);

        return gameOrder;
    }

    @Override
    public void visit(CashGameOrder cashGameOrder) {

        validateGameOffers(cashGameOrder);
        cashGameOrder.setDateOfOrder(System.currentTimeMillis());

        handleKeys(cashGameOrder, cashGameOrder.getUser());

        BigDecimal priceCash = cashGameOrder.getGameOfferList().stream()
                .map(gameOffer -> {
                    if (gameOffer.getPromotion() != null) {
                        return gameOffer.getPriceCash().subtract(gameOffer.getPriceCash().multiply(gameOffer.getPromotion().divide(BigDecimal.valueOf(100L))));
                    }
                    return gameOffer.getPriceCash();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cashGameOrder.setCashValue(priceCash);

        saveOrder(cashGameOrder, cashGameOrder.getUser());

        if (emailEnabled) {
            gameOrderPDFService.execute(cashGameOrder);
        }

    }

    @Override
    public void visit(PointsGameOrder pointsGameOrder) {

        validateGameOffers(pointsGameOrder);
        Long pricePoints = pointsGameOrder.getGameOfferList().stream()
                .mapToLong(GameOffer::getPricePoints).sum();
        pointsGameOrder.setPointsValue(pricePoints);

        UserProfile profile = userProfileDAO.getProfileByUser(pointsGameOrder.getUser()).get();
        if (profile.getPoints() <= pointsGameOrder.getPointsValue()) {
            throw new AuthorizationException(HttpStatus.UNAUTHORIZED, "Insufficient funds!");
        }

        profile.decreasePoints(pointsGameOrder.getPointsValue());
        pointsGameOrder.setDateOfOrder(System.currentTimeMillis());

        handleKeys(pointsGameOrder, pointsGameOrder.getUser());

        saveOrder(pointsGameOrder, pointsGameOrder.getUser());

        if (emailEnabled) {
            gameOrderPDFService.execute(pointsGameOrder);
        }
    }

    @Override
    public void visit(KeyGameOrder keyGameOrder) {

        GeneratedKey key = generatedKeyDAO.getGeneratedKeyByKey(keyGameOrder.getKeyValue()).orElseThrow(() -> new AuthorizationException(HttpStatus.UNAUTHORIZED, "Invalid key!"));

        if (key.getLogicalDelete()) {
            throw new AuthorizationException(HttpStatus.UNAUTHORIZED, "Invalid key!");
        }
        keyGameOrder.setDateOfOrder(System.currentTimeMillis());

        key.setLogicalDelete(true);
        keyGameOrder.setOwnedGames(Arrays.asList(key.getGame()));
        keyGameOrder.setKeys(Arrays.asList(key));
        gameOrderDAO.saveGameOrder(keyGameOrder);

        OwnedGame ownedGame = new OwnedGame();
        ownedGame.setGame(key.getGame());
        ownedGame.setUser(keyGameOrder.getUser());
        ownedGame.setOrder(keyGameOrder);

        if (emailEnabled) {
            gameOrderPDFService.execute(keyGameOrder);
        }
    }

    @Override
    public void visit(GiftGameOrder giftGameOrder) {

        if (giftGameOrder.getRecieverUser() == null || !userDAO.getUserByUserName(giftGameOrder.getRecieverUser().getUserName()).isPresent()) {
            throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid user!");
        }

        validateGameOffers(giftGameOrder);
        giftGameOrder.setDateOfOrder(System.currentTimeMillis());

        Collection<Friend> friends = friendDAO.getAll(giftGameOrder.getUser());

        boolean areFriends = friends.stream()
                .anyMatch(friend -> friend.getFriend().equals(giftGameOrder.getRecieverUser()));

        if (!areFriends) {
            throw new AuthorizationException(HttpStatus.UNAUTHORIZED, "Users are not friend!");
        }

        handleKeys(giftGameOrder, giftGameOrder.getRecieverUser());

        BigDecimal priceCash = giftGameOrder.getGameOfferList().stream()
                .map(gameOffer -> gameOffer.getPriceCash())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        giftGameOrder.setGiftValue(priceCash);

        saveOrder(giftGameOrder, giftGameOrder.getRecieverUser());

        GameGiftNotification notification = new GameGiftNotification.Builder().user(giftGameOrder.getRecieverUser()).date(giftGameOrder.getDateOfOrder())
                .keys(giftGameOrder.getKeys()).games(giftGameOrder.getOwnedGames()).build();

        messagingTemplate.convertAndSend("/topic/" + giftGameOrder.getRecieverUser().getUserName() + TopicConstants.GAME_GIFT, notification);

        if (emailEnabled) {
            gameOrderPDFService.execute(giftGameOrder);
        }
    }

    private void validateGameOffers(GameOrder gameOrder) {

        if (gameOrder.getGameOfferList() == null || gameOrder.getGameOfferList().isEmpty()) {
            throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid game offers! List is empty!");
        }

        List<GameOffer> gameOffers = new ArrayList<>();

        for (GameOffer offer: gameOrder.getGameOfferList()) {

            gameOffers.add(gameOfferDAO.getGameOfferById(offer.getId()).orElseThrow(() -> new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid game offers!")));
        }

        gameOrder.setGameOfferList(gameOffers);

    }

    private void saveOrder(GameOrder order, User user) {

        gameOrderDAO.saveGameOrder(order);

        order.getOwnedGames().forEach(game -> {
            OwnedGame owned = new OwnedGame();
            owned.setGame(game);
            owned.setUser(user);
            owned.setOrder(order);
            gameOrderDAO.saveBoughtGame(owned);
        });

        order.getKeys().forEach(generatedKey -> generatedKeyDAO.saveKey(generatedKey));
    }

    private void handleKeys(GameOrder gameOrder, User user) {

        List<Game> gameList = gameOrder.getGameOfferList().stream().map(GameOffer::getGames).flatMap(games -> games.stream()).collect(Collectors.toList());
        List<Game> gamesAlreadyOwned = gameOrderDAO.getGamesByUser(gameOrder.getUser());
        List<Game> gamesToBuy = new ArrayList<>();
        List<GeneratedKey> keys = new ArrayList<>();

        gameList.stream()
                .forEach(game -> {
                    if (!gamesAlreadyOwned.contains(game) && !gamesToBuy.contains(game)) {
                        gamesToBuy.add(game);
                    } else {
                        GeneratedKey key = new GeneratedKey.Builder().generatedKey(generateKey()).game(game).order(gameOrder).user(user).build();
                        keys.add(key);
                    }
                });

        gameOrder.setOwnedGames(gamesToBuy);
        gameOrder.setKeys(keys);
    }

    private String generateKey() {
        return UUID.randomUUID().toString();
    }
}
