package com.practicaSV.gameLabz.controllers;

import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.domain.GameOffer;
import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.exceptions.InvalidValueException;
import com.practicaSV.gameLabz.repositories.GameDAO;
import com.practicaSV.gameLabz.repositories.GameOfferDAO;
import com.practicaSV.gameLabz.utils.Authorization;
import com.practicaSV.gameLabz.utils.GameOfferFilter;
import com.practicaSV.gameLabz.utils.GetFilter;
import com.practicaSV.gameLabz.utils.PathConstants;
import com.practicaSV.gameLabz.utils.comparators.GameOfferComparator;
import com.practicaSV.gameLabz.utils.comparators.GameOfferComparatorFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
public class GameOfferController {

    @Autowired
    private GameOfferDAO gameOfferDAO;

    @Autowired
    private GameDAO gameDAO;

    @Transactional
    @Authorization(userTypes = User.UserType.ADMIN)
    @RequestMapping(path = PathConstants.GAME_OFFERS_PATH, method = RequestMethod.POST)
    public ResponseEntity<GameOffer> addGameOffer(@Valid @RequestBody GameOffer gameOffer) {

        GameOffer dbGameOffer = gameOfferDAO.saveGameOffer(gameOffer);

        return new ResponseEntity<>(dbGameOffer, HttpStatus.CREATED);
    }

    @Transactional
    @Authorization(userTypes = User.UserType.ADMIN)
    @RequestMapping(path = PathConstants.GAME_OFFER_ID_PATH, method = RequestMethod.PUT)
    public ResponseEntity updateGameOffer(@PathVariable Long gameOfferId, @RequestBody GameOffer gameOffer) {

        boolean changed = false;

        GameOffer gameOfferToUpdate = gameOfferDAO.getGameOfferById(gameOfferId).orElseThrow(() -> new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid game offer id!"));

        if (gameOffer.getPriceCash() != null) {
            if (gameOffer.getPriceCash().compareTo(BigDecimal.ZERO) == -1) {
                throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid price in cash!");
            } else {
                changed = true;
                gameOfferToUpdate.setPriceCash(gameOffer.getPriceCash());
            }
        }

        if (gameOffer.getPricePoints() != null) {
            if (gameOffer.getPricePoints() >= 0) {
                changed = true;
                gameOfferToUpdate.setPricePoints(gameOffer.getPricePoints());
            } else {
                throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid price in points!");
            }
        }

        if (gameOffer.getPromotion() != null) {
            if (gameOffer.getPromotion().compareTo(BigDecimal.ZERO) == -1) {
                throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid promotion!");
            }

            if (gameOffer.getPromotion().compareTo(BigDecimal.valueOf(100L)) == 1) {
                throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid promotion!");
            }
            changed = true;
            gameOfferToUpdate.setPromotion(gameOffer.getPromotion());
        }

        if (!changed) {
            throw new InvalidValueException(HttpStatus.BAD_REQUEST, "No new fields were inserted!");
        }

        return new ResponseEntity(gameOfferToUpdate, HttpStatus.OK);
    }

    @Transactional
    @Authorization(userTypes = User.UserType.ADMIN)
    @RequestMapping(path = PathConstants.GAME_OFFER_ID_PATH, method = RequestMethod.DELETE)
    public ResponseEntity removeGameOffer(@PathVariable Long gameOfferId) {

        GameOffer gameOfferToRemove = gameOfferDAO.getGameOfferById(gameOfferId).orElseThrow(() -> new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid game offer id!"));

        gameOfferDAO.removeGameOffer(gameOfferToRemove);

        return new ResponseEntity(HttpStatus.OK);
    }

    @Transactional
    @Authorization(userTypes = {User.UserType.ADMIN, User.UserType.CLIENT})
    @RequestMapping(path = PathConstants.GAME_OFFERS_PATH_PUBLIC, method = RequestMethod.GET)
    public ResponseEntity<List<GameOffer>> getGameOffersByFilter(@RequestParam(value = GetFilter.NAME, required = false) String name,
                                                                 @RequestParam(value = GetFilter.GENRE, required = false) String genre,
                                                                 @RequestParam(value = GetFilter.OFFER_TYPE, required = false) String type,
                                                                 @RequestParam(value = GetFilter.IS_PROMOTION, required = false) String promotion,
                                                                 @RequestParam(value = GetFilter.ORDER_BY, required = false) String orderBy,
                                                                 @RequestParam(value = GetFilter.ORDER_DIRECTION, required = false) String orderDirection) {

        GameOfferFilter gameOfferFilter = new GameOfferFilter();

        if (!StringUtils.isBlank(name)) {

            gameOfferFilter.createFilter(GetFilter.NAME, name);
        }

        if (!StringUtils.isBlank(genre)) {
            try {
                Game.GameGenre gameGenre = Game.GameGenre.valueOf(genre);
                gameOfferFilter.createFilter(GetFilter.GENRE, gameGenre);
            } catch (IllegalArgumentException e) {
                throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid genre parameter!");
            }
        }

        if (!StringUtils.isBlank(type)) {
            try {
                GameOffer.OfferType offerType = GameOffer.OfferType.valueOf(type);
                gameOfferFilter.createFilter(GetFilter.OFFER_TYPE, offerType);
            } catch (IllegalArgumentException e) {
                throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid offer type parameter!");
            }
        }

        if (!StringUtils.isBlank(promotion)) {

            Boolean offerPromotion = Boolean.valueOf(promotion);
            gameOfferFilter.createFilter(GetFilter.IS_PROMOTION, offerPromotion);
        }
        Collection<GameOffer> gameOffers = gameOfferDAO.getGameOffersByFilters(gameOfferFilter);

        List<GameOffer> gameOfferList = new ArrayList<>(gameOffers);

        if (!StringUtils.isBlank(orderBy)) {

            gameOfferFilter.createFilter(GetFilter.ORDER_BY, orderBy);

            if (!StringUtils.isBlank(orderDirection)) {
                try {
                    GetFilter.OrderDirection order = GetFilter.OrderDirection.valueOf(orderDirection);
                    gameOfferFilter.createFilter(GetFilter.ORDER_DIRECTION, order);
                } catch (IllegalArgumentException e) {
                    throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid order direction parameter!");
                }
            }
            GameOfferComparator gameOfferComparator = GameOfferComparatorFactory.buildComparator(gameOfferFilter);
            Collections.sort(gameOfferList, gameOfferComparator);
        }

        return new ResponseEntity<>(gameOfferList, HttpStatus.OK);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(new GameOfferValidator(gameDAO));
    }
}
