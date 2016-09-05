package com.practicaSV.gameLabz.controllers;

import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.exceptions.InvalidValueException;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.HashSet;
import java.util.Set;

public class GameValidator implements Validator {

    @Override
    public boolean supports(Class clazz) {
        return Game.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {

        Game game = (Game) o;

        boolean failed = false;
        StringBuilder errorMessage = new StringBuilder();

        if (StringUtils.isBlank(game.getName())) {

            failed = true;
            errorMessage.append("Invalid game name! ");
        }

        if (game.getReleaseDate() < 0) {

            failed = true;
            errorMessage.append("Invalid release date! ");
        }

        if (game.getGameGenres() != null && !game.getGameGenres().isEmpty()) {

            Set<Game.GameGenre> set = new HashSet<>();
            boolean duplicateGenres = game.getGameGenres().stream()
                    .anyMatch(gameGenre -> !set.add(gameGenre));

            if (duplicateGenres) {
                failed = true;
                errorMessage.append("Duplicate genre!");
            }
        } else {
            failed = true;
            errorMessage.append("Invalid genre!");
        }

        if (failed) {
            throw new InvalidValueException(HttpStatus.BAD_REQUEST, errorMessage.toString().trim());
        }
    }
}
