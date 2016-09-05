package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.Game;
import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.domain.UserRatingLink;

import java.util.Collection;
import java.util.Optional;

public interface UserRatingLinkDAO {

    UserRatingLink saveRatingLink(UserRatingLink ratingLink);

    Collection<UserRatingLink> getRatingLinksByUser(User user);

    Collection<UserRatingLink> getRatingLinksByGame(Game game);

    Optional<UserRatingLink> getRatingLinkById(Long ratingLinkId);

    Optional<UserRatingLink> getRatingLinkByGameAndUser(Game game, User user);
}
