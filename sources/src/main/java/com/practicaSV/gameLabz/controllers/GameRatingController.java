package com.practicaSV.gameLabz.controllers;

import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.domain.GameRating;
import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.domain.UserRatingLink;
import com.practicaSV.gameLabz.exceptions.AuthorizationException;
import com.practicaSV.gameLabz.exceptions.InvalidValueException;
import com.practicaSV.gameLabz.repositories.*;
import com.practicaSV.gameLabz.utils.Authorization;
import com.practicaSV.gameLabz.utils.PathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

@RestController
@RequestMapping(path = PathConstants.GAME_RATING_ID_PATH)
public class GameRatingController {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private GameDAO gameDAO;

    @Autowired
    private UserRatingLinkDAO userRatingLinkDAO;

    @Autowired
    private GameRatingDAO gameRatingDAO;

    @Autowired
    private GameOrderDAO gameOrderDAO;

    @Transactional
    @Authorization(userTypes = User.UserType.CLIENT)
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity rateGame(@PathVariable String userName, @PathVariable Long gameId,
                                   @RequestParam(value = PathConstants.RATING) String ratingString) {

        BigDecimal rating;
        UserRatingLink ratingLinkToReturn;

        try {
            rating = BigDecimal.valueOf(Double.parseDouble(ratingString));
            if (rating.compareTo(BigDecimal.ZERO) == -1 || rating.compareTo(BigDecimal.valueOf(100)) == 1) {
                throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid rating parameter!");
            }
            rating = rating.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid rating parameter!");
        }

        Game game = gameDAO.getGameById(gameId).orElseThrow(() -> new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid game id!"));
        User user = userDAO.getUserByUserName(userName).get();

        if (!gameOrderDAO.getGameByUserAndGame(user, game).isPresent()) {
            throw new AuthorizationException(HttpStatus.UNAUTHORIZED, "User doesn't own the game!");
        }

        if (userRatingLinkDAO.getRatingLinksByGame(game).isEmpty()) {       //if the game hasn't been rated yet

            GameRating gameRating = new GameRating();
            gameRating.setGame(game);
            gameRating.setNumberOfRatings(1L);
            gameRating.setRating(rating);

            UserRatingLink ratingLink = new UserRatingLink();
            ratingLink.setGame(game);
            ratingLink.setUser(user);
            ratingLink.setRating(rating);

            gameRatingDAO.saveRating(gameRating);
            ratingLinkToReturn = userRatingLinkDAO.saveRatingLink(ratingLink);

        } else {

            if (!userRatingLinkDAO.getRatingLinkByGameAndUser(game, user).isPresent()) {            //if the user didn't rate the game yet

                UserRatingLink ratingLink = new UserRatingLink();
                ratingLink.setGame(game);
                ratingLink.setUser(user);
                ratingLink.setRating(rating);

                GameRating gameRating = gameRatingDAO.getRatingByGame(game).get();
                BigDecimal newRating = gameRating.getRating().multiply(BigDecimal.valueOf(gameRating.getNumberOfRatings())).add(rating);
                gameRating.setNumberOfRatings(gameRating.getNumberOfRatings()+1);
                gameRating.setRating(newRating.divide(BigDecimal.valueOf(gameRating.getNumberOfRatings())));

                ratingLinkToReturn = userRatingLinkDAO.saveRatingLink(ratingLink);

            } else {            //if the user updates the rating of the game

                UserRatingLink linkToUpdate = userRatingLinkDAO.getRatingLinkByGameAndUser(game, user).orElseThrow(() -> new InvalidValueException(HttpStatus.INTERNAL_SERVER_ERROR, "Couldn't find link!"));
                linkToUpdate.setRating(rating);

                Collection<UserRatingLink> ratingLinks = userRatingLinkDAO.getRatingLinksByGame(game);
                BigDecimal newRating = ratingLinks.stream()
                        .map(UserRatingLink::getRating)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(ratingLinks.size()), 2, RoundingMode.HALF_UP);

                GameRating gameRating = gameRatingDAO.getRatingByGame(game).get();
                gameRating.setRating(newRating);

                ratingLinkToReturn = linkToUpdate;
            }
        }

        return new ResponseEntity(ratingLinkToReturn, HttpStatus.OK);
    }
}
