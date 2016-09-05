package com.practicaSV.gameLabz.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.practicaSV.gameLabz.domain.Friend;
import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.domain.UserProfile;
import com.practicaSV.gameLabz.exceptions.AuthorizationException;
import com.practicaSV.gameLabz.exceptions.InvalidValueException;
import com.practicaSV.gameLabz.repositories.FriendDAO;
import com.practicaSV.gameLabz.repositories.UserDAO;
import com.practicaSV.gameLabz.repositories.UserProfileDAO;
import com.practicaSV.gameLabz.utils.Authorization;
import com.practicaSV.gameLabz.utils.JsonViews;
import com.practicaSV.gameLabz.utils.PathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping
public class UserProfileController {

    @Autowired
    private UserProfileDAO userProfileDAO;

    @Autowired
    private FriendDAO friendDAO;

    @Autowired
    private UserDAO userDAO;

    @Transactional
    @RequestMapping(path = PathConstants.SHARED_LINK_ID_PATH, method = RequestMethod.PUT)
    public ResponseEntity clickedOnLink(@PathVariable String sharedLinkId) {

        UserProfile profileFromDb = userProfileDAO.getProfileByLinkId(sharedLinkId).orElseThrow(() -> new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid shared link id"));

        profileFromDb.addPoints(1L);

        return new ResponseEntity(HttpStatus.OK);
    }

    @JsonView(JsonViews.Hidden.class)
    @Authorization(userTypes = User.UserType.CLIENT)
    @RequestMapping(path = PathConstants.PROFILE, method = RequestMethod.GET)
    public ResponseEntity getOwnProfile(@PathVariable String userName) {

        User user = userDAO.getUserByUserName(userName).get();

        UserProfile profile = userProfileDAO.getProfileByUser(user).orElseThrow(() -> new InvalidValueException(HttpStatus.INTERNAL_SERVER_ERROR, "No profile was found!"));

        return new ResponseEntity(profile, HttpStatus.OK);
    }

    @JsonView(JsonViews.Default.class)
    @Authorization(userTypes = User.UserType.CLIENT)
    @RequestMapping(path = PathConstants.PROFILE_OF_FRIEND, method = RequestMethod.GET)
    public ResponseEntity getFriendProfile(@PathVariable String userName, @PathVariable String friendUserName){

        User user = userDAO.getUserByUserName(userName).get();
        User friendUser = userDAO.getUserByUserName(friendUserName).get();
        Collection<Friend> friendship = friendDAO.getAll(user);

        boolean areFriends = friendship.stream()
                .anyMatch(friend -> friend.getFriend().equals(friendUser));

        if (!areFriends) {
            throw new AuthorizationException(HttpStatus.UNAUTHORIZED, "Users are not friend!");
        }

        UserProfile profile = userProfileDAO.getProfileByUser(user).orElseThrow(() -> new InvalidValueException(HttpStatus.INTERNAL_SERVER_ERROR, "No profile was found!"));

        return new ResponseEntity(profile, HttpStatus.OK);
    }
}
