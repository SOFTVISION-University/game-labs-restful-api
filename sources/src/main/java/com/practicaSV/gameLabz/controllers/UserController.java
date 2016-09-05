package com.practicaSV.gameLabz.controllers;

import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.domain.UserProfile;
import com.practicaSV.gameLabz.exceptions.AuthenticationException;
import com.practicaSV.gameLabz.exceptions.InvalidValueException;
import com.practicaSV.gameLabz.repositories.UserDAO;
import com.practicaSV.gameLabz.repositories.UserProfileDAO;
import com.practicaSV.gameLabz.services.AuthenticationService;
import com.practicaSV.gameLabz.utils.Authorization;
import com.practicaSV.gameLabz.utils.HttpHeadersConstants;
import com.practicaSV.gameLabz.utils.PathConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping
public class UserController {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserProfileDAO userProfileDAO;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @RequestMapping(path = PathConstants.USERS_PATH, method = RequestMethod.POST)
    public ResponseEntity<User> registerUser(@Valid @RequestBody User user) throws InvalidValueException {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User dbUser = userDAO.saveUser(user);

        String sharedLinkId = UUID.randomUUID().toString();
        UserProfile profile = new UserProfile.Builder().user(dbUser).sharedLinkId(sharedLinkId).sharedLink(PathConstants.SHARED_LINK + "/{"+ sharedLinkId +"}").points(0L).build();

        userProfileDAO.saveProfile(profile);

        ResponseEntity<User> result = new ResponseEntity<>(dbUser, HttpStatus.CREATED);

        return result;
    }

    @RequestMapping(path = PathConstants.LOGIN_PATH, method = RequestMethod.POST)
    public ResponseEntity userLogin(@RequestBody User user) {

        if (StringUtils.isBlank(user.getUserName()) || StringUtils.isBlank(user.getPassword())) {
            throw new AuthenticationException(HttpStatus.UNAUTHORIZED, "Username or password is wrong!");
        }

        String sessionId = authenticationService.doLogin(user.getUserName(), user.getPassword());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeadersConstants.SESSION_ID, sessionId);

        return new ResponseEntity(httpHeaders, HttpStatus.OK);
    }

    @Transactional
    @Authorization(userTypes = {User.UserType.CLIENT, User.UserType.ADMIN})
    @RequestMapping(path = PathConstants.USER_ID_PATH, method = RequestMethod.PUT)
    public ResponseEntity<User> updateUser(@PathVariable String userName, @RequestBody User user) {

        boolean changed = false;

        user.setUserName(userName);

        UserValidator userValidator = new UserValidator();
        userValidator.validateOptional(user);

        User dbUser = userDAO.getUserByUserName(userName).get();

        if (!StringUtils.isBlank(user.getPassword())) {
            dbUser.setPassword(passwordEncoder.encode(user.getPassword()));
            changed = true;
        }

        if (!StringUtils.isBlank(user.getEmail())) {
            dbUser.setEmail(user.getEmail());
            changed = true;
        }

        if (!changed) {
            throw new InvalidValueException(HttpStatus.BAD_REQUEST, "No new fields were inserted!");
        }

        return new ResponseEntity(dbUser, HttpStatus.OK);
    }

    @Authorization(userTypes = {User.UserType.CLIENT, User.UserType.ADMIN})
    @RequestMapping(path = PathConstants.LOGOUT_PATH, method = RequestMethod.POST)
    public ResponseEntity logoutUser(@PathVariable String userName) {

        authenticationService.doLogout(userName);

        return new ResponseEntity(HttpStatus.OK);
    }

    @Transactional
    @Authorization(userTypes = User.UserType.CLIENT)
    @RequestMapping(path = PathConstants.USER_ID_PATH, method = RequestMethod.DELETE)
    public ResponseEntity deleteUser(@PathVariable String userName) {

        User user = userDAO.getUserByUserName(userName).get();

        userDAO.removeUser(user);
        authenticationService.doLogout(userName);

        return new ResponseEntity(HttpStatus.OK);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(new UserValidator());
    }  
}
