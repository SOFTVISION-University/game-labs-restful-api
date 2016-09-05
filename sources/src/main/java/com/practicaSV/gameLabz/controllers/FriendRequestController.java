package com.practicaSV.gameLabz.controllers;

import com.practicaSV.gameLabz.domain.Friend;
import com.practicaSV.gameLabz.domain.FriendRequest;
import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.exceptions.AuthorizationException;
import com.practicaSV.gameLabz.exceptions.InvalidValueException;
import com.practicaSV.gameLabz.repositories.FriendDAO;
import com.practicaSV.gameLabz.repositories.FriendRequestDAO;
import com.practicaSV.gameLabz.repositories.UserDAO;
import com.practicaSV.gameLabz.utils.Authorization;
import com.practicaSV.gameLabz.utils.PathConstants;
import com.practicaSV.gameLabz.utils.websocket.TopicConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(path = PathConstants.FRIEND_REQUEST_PATH)
public class FriendRequestController {

    @Autowired
    private FriendRequestDAO friendRequestDAO;

    @Autowired
    private FriendDAO friendDAO;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserDAO userDAO;

    @Transactional
    @Authorization(userTypes = User.UserType.CLIENT)
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity addFriendRequest(@PathVariable String userName, @RequestBody FriendRequest friendRequest) {

        User userToWhom = userDAO.getUserByUserName(friendRequest.getToWhom().getUserName()).orElseThrow(() -> new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid target user for request!"));

        User userFrom = userDAO.getUserByUserName(userName).get();

        if (userFrom.equals(userToWhom)) {
            throw new AuthorizationException(HttpStatus.FORBIDDEN, "A user cannot send friend request to himself!");
        }

        Collection<FriendRequest> requests = friendRequestDAO.getAllByUser(userToWhom);

        boolean exists = requests.stream()
                .anyMatch(request -> request.getFromWho().equals(userFrom) || request.getToWhom().equals(userFrom));

        if (exists) {
            throw new AuthorizationException(HttpStatus.UNAUTHORIZED, "Friend request already exists!");
        }

        friendRequest.setFromWho(userFrom);
        friendRequest.setToWhom(userToWhom);

        FriendRequest friendRequestDb = friendRequestDAO.saveRequest(friendRequest);

        friendRequest.setStatus(FriendRequest.Status.CREATED);

        messagingTemplate.convertAndSend("/topic/" + userToWhom.getUserName() + TopicConstants.FRIEND_REQUEST, friendRequest);

        return new ResponseEntity(friendRequestDb, HttpStatus.CREATED);
    }

    @Transactional
    @Authorization(userTypes = User.UserType.CLIENT)
    @RequestMapping(path = "{" + PathConstants.FRIEND_REQUEST_ID_KEY + "}", method = RequestMethod.DELETE)
    public ResponseEntity cancelFriendRequest(@PathVariable String userName, @PathVariable Long friendRequestId) {

        FriendRequest friendRequestDb = friendRequestDAO.getFriendRequestById(friendRequestId).orElseThrow(() -> new InvalidValueException(HttpStatus.BAD_REQUEST, "Friend request doesn't exist!"));

        User userFrom = userDAO.getUserByUserName(userName).get();

        if (!userFrom.equals(friendRequestDb.getFromWho())) {
            throw new AuthorizationException(HttpStatus.FORBIDDEN, "Not authorized to make this call!");
        }

        friendRequestDAO.removeRequest(friendRequestDb);

        friendRequestDb.setStatus(FriendRequest.Status.CANCELLED);
        messagingTemplate.convertAndSend("/topic/" + friendRequestDb.getToWhom().getUserName() + TopicConstants.FRIEND_REQUEST, friendRequestDb);

        return new ResponseEntity(HttpStatus.OK);
    }

    @Transactional
    @Authorization(userTypes = User.UserType.CLIENT)
    @RequestMapping(path = "{" + PathConstants.FRIEND_REQUEST_ID_KEY + "}", method = RequestMethod.POST)
    public ResponseEntity acceptOrRejectRequest(@RequestParam(value = FriendRequest.CHOICE) String choice,
                                                @PathVariable String userName,
                                                @PathVariable Long friendRequestId) {

        FriendRequest friendRequestDb = friendRequestDAO.getFriendRequestById(friendRequestId).orElseThrow(() -> new InvalidValueException(HttpStatus.BAD_REQUEST, "Friend request doesn't exist!"));
        FriendRequest.Status requestChoice;

        try {
            requestChoice = FriendRequest.Status.valueOf(choice);
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid choice!");
        }

        if (requestChoice != FriendRequest.Status.ACCEPTED && requestChoice != FriendRequest.Status.REJECTED) {
            throw new InvalidValueException(HttpStatus.BAD_REQUEST, "Invalid choice!");
        }

        User userFromPath = userDAO.getUserByUserName(userName).get();

        if (!friendRequestDb.getToWhom().equals(userFromPath)) {
            throw new AuthorizationException(HttpStatus.FORBIDDEN, "Not authorized to make this call!");
        }

        friendRequestDb.setStatus(requestChoice);
        friendRequestDAO.removeRequest(friendRequestDb);

        if (requestChoice == FriendRequest.Status.ACCEPTED) {

            Friend friend = new Friend();
            friend.setUser(friendRequestDb.getFromWho());
            friend.setFriend(friendRequestDb.getToWhom());

            friendDAO.saveFriend(friend);
        }

        messagingTemplate.convertAndSend("/topic/" + friendRequestDb.getFromWho().getUserName() + TopicConstants.FRIEND_REQUEST, friendRequestDb);

        return new ResponseEntity(friendRequestDb, HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(method = RequestMethod.GET)
    @Authorization(userTypes = User.UserType.CLIENT)
    public ResponseEntity<List<FriendRequest>> getAllFriendRequests(@PathVariable String userName) {

        User user = userDAO.getUserByUserName(userName).get();

        Collection<FriendRequest> friendRequests = friendRequestDAO.getAllByUser(user);
        List<FriendRequest> friendRequestList = new ArrayList<>(friendRequests);

        return new ResponseEntity(friendRequestList, HttpStatus.OK);
    }
}
