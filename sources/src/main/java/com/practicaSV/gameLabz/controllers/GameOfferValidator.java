package com.practicaSV.gameLabz.controllers;

import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.domain.GameOffer;
import com.practicaSV.gameLabz.exceptions.InvalidValueException;
import com.practicaSV.gameLabz.repositories.GameDAO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameOfferValidator implements Validator {

    private GameDAO gameDAO;

    public GameOfferValidator(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    @Override
    public boolean supports(Class clazz) { return GameOffer.class.isAssignableFrom(clazz); }

    @Override
    public void validate(Object o, Errors errors) {

        GameOffer gameOffer = (GameOffer) o;

        if (gameOffer.getGames() == null || gameOffer.getGames().isEmpty()) {
            throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid offer!");
        }

        List<Game> games = new ArrayList<>();

        for (Game game: gameOffer.getGames()) {

            if (game.getId() == null) {
                throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid game!");
            }

            games.add(gameDAO.getGameById(game.getId()).orElseThrow(() -> new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid game!")));
        }

        gameOffer.setGames(games);

        if (gameOffer.getOfferType() == null) {
            throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid game offer type!");
        }

        if (gameOffer.getGames().size() == 1) {
            if (!gameOffer.getOfferType().equals(GameOffer.OfferType.SINGLE)) {
                throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid game offer type!");
            }
        }

        if (gameOffer.getGames().size() > 1) {
            if (!gameOffer.getOfferType().equals(GameOffer.OfferType.BUNDLE)) {
                throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid game offer type!");
            }
        }

        if (gameOffer.getPriceCash() == null || gameOffer.getPriceCash().compareTo(BigDecimal.ZERO) == -1) {
            throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid price in cash!");
        }

        if (gameOffer.getPricePoints() < 0) {
            throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid price in points!");
        }

        if (gameOffer.getPromotion() != null) {

            if (gameOffer.getPromotion().compareTo(BigDecimal.ZERO) == -1) {
                throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid promotion!");
            }

            if (gameOffer.getPromotion().compareTo(BigDecimal.valueOf(100L)) == 1) {
                throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid promotion!");
            }
        }

    }
}
