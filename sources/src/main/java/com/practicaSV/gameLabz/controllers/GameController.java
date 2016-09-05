package com.practicaSV.gameLabz.controllers;

import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.exceptions.InvalidValueException;
import com.practicaSV.gameLabz.repositories.GameDAO;
import com.practicaSV.gameLabz.utils.Authorization;
import com.practicaSV.gameLabz.utils.GameFilter;
import com.practicaSV.gameLabz.utils.GetFilter;
import com.practicaSV.gameLabz.utils.PathConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(path = PathConstants.GAMES_PATH)
public class GameController {

    @Autowired
    private GameDAO gameDAO;

    @Transactional
    @Authorization(userTypes = User.UserType.ADMIN)
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Game> addGame(@Valid @RequestBody Game game) {

        Game dbGame = gameDAO.saveGame(game);

        return new ResponseEntity<>(dbGame, HttpStatus.CREATED);
    }

    @Authorization(userTypes = User.UserType.ADMIN)
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Game>> getGameByFilter(@RequestParam(value = GetFilter.NAME, required = false) String name,
                                                      @RequestParam(value = GetFilter.HAS_OFFER, required = false) String hasOffer,
                                                      @RequestParam(value = GetFilter.GENRE, required = false) String genre,
                                                      @RequestParam(value = GetFilter.RELEASED_BEFORE, required = false) String releasedBefore,
                                                      @RequestParam(value = GetFilter.RELEASED_AFTER, required = false) String releasedAfter) {

        GameFilter gameFilter = new GameFilter();

        if (!StringUtils.isBlank(name)) {

            gameFilter.createFilter(GetFilter.NAME, name);
        }

        if (!StringUtils.isBlank(hasOffer)) {

                Boolean hasOfferBoolean = Boolean.valueOf(hasOffer);
                gameFilter.createFilter(GetFilter.HAS_OFFER, hasOfferBoolean);
        }

        if (!StringUtils.isBlank(genre)) {
            try {
                Game.GameGenre gameGenre = Game.GameGenre.valueOf(genre);
                gameFilter.createFilter(GetFilter.GENRE, gameGenre);
            } catch (IllegalArgumentException e) {
                throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid genre parameter!");
            }
        }

        if(!StringUtils.isBlank(releasedBefore)){
            try {
                Long releasedBeforeLong = Long.parseLong(releasedBefore);
                if (releasedBeforeLong < 0) {
                    throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid released before parameter!");
                }
                gameFilter.createFilter(GetFilter.RELEASED_BEFORE, releasedBeforeLong);
            }catch (NumberFormatException e){
                throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid released before parameter!");
            }
        }

        if (!StringUtils.isBlank(releasedAfter)) {
            try {
                Long relesedAfterLong = Long.parseLong(releasedAfter);
                if (relesedAfterLong < 0) {
                    throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid released after parameter!");
                }
                gameFilter.createFilter(GetFilter.RELEASED_AFTER, relesedAfterLong);
            } catch (NumberFormatException e) {
                throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid released after parameter!");
            }
        }

        Collection<Game> games = gameDAO.getGamesByFilters(gameFilter);

        List<Game> gameList = new ArrayList<>(games);

        return new ResponseEntity<>(gameList, HttpStatus.OK);
    }

    @Transactional
    @Authorization(userTypes = User.UserType.ADMIN)
    @RequestMapping(path = "/{"+ PathConstants.GAME_ID_KEY +"}", method = RequestMethod.PUT)
    public ResponseEntity updateGame(@PathVariable Long gameId, @RequestBody Game game) {

        boolean changed = false;

        Game gameToUpdate = gameDAO.getGameById(gameId).orElseThrow(() -> new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid game id!"));

        if (!StringUtils.isBlank(game.getName())) {
            gameToUpdate.setName(game.getName());
            changed = true;
        }

        if (!StringUtils.isBlank(game.getDescription())) {
            gameToUpdate.setDescription(game.getDescription());
            changed = true;
        }

        if (game.getReleaseDate() != null) {
            if (game.getReleaseDate() >= 0) {
                gameToUpdate.setReleaseDate(game.getReleaseDate());
                changed = true;
            } else {
                throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid release date parameter!");
            }
        }

        if (game.getGameGenres() != null && !game.getGameGenres().isEmpty()) {

            Set<Game.GameGenre> set = new HashSet<>();
            boolean duplicateGenres = game.getGameGenres().stream()
                    .anyMatch(gameGenre -> !set.add(gameGenre));

            if (duplicateGenres) {
                throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Duplicate genre!");
            }

            gameToUpdate.setGameGenres(game.getGameGenres());
            gameDAO.updateGenreLink(gameToUpdate);
            changed = true;
        }

        if (!changed) {
            throw new InvalidValueException(HttpStatus.BAD_REQUEST, "No new fields were inserted!");
        }

        return new ResponseEntity(gameToUpdate, HttpStatus.OK);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(new GameValidator());
    }
}
