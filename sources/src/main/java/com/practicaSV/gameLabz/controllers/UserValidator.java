package com.practicaSV.gameLabz.controllers;

import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.exceptions.InvalidValueException;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class UserValidator implements Validator {

    public static final String EMAIL_REG = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,3})$";

    public static final String PASS_REG = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,16}$";

    @Override
    public boolean supports(Class clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {

        User user = (User) o;

        StringBuilder errorMessage = new StringBuilder();
        boolean failed = false;

        if (StringUtils.isBlank(user.getUserName())) {

            failed = true;
            errorMessage.append("Username field is empty! ");
        } else if (user.getUserName().length() < 4 || user.getUserName().length() > 16) {

            failed = true;
            errorMessage.append("Your userName field is invalid! ");
        }

        if (StringUtils.isBlank(user.getPassword())) {

            failed = true;
            errorMessage.append("Password field is empty! ");
        } else if (!validatePassword(user.getPassword())) {

            failed = true;
            errorMessage.append("Your password field is invalid! ");
        }

        if (StringUtils.isBlank(user.getEmail())) {

            failed = true;
            errorMessage.append("Email field is empty! ");
        } else if (!validateEmail(user.getEmail())) {

            failed = true;
            errorMessage.append("Your email field is invalid!");
        }

        if (failed) {
            throw new InvalidValueException(HttpStatus.BAD_REQUEST, errorMessage.toString());
        }

    }

    public void validateOptional(User user) {
        StringBuilder errorMessage = new StringBuilder();
        boolean failed = false;

        if (!StringUtils.isBlank(user.getPassword()) && !validatePassword(user.getPassword())) {
            failed = true;
            errorMessage.append("Your password field is invalid! ");
        }

        if (!StringUtils.isBlank(user.getEmail()) && !validateEmail(user.getEmail())) {
            failed = true;
            errorMessage.append("Your email field is invalid!");
        }

        if (failed) {
            throw new InvalidValueException(HttpStatus.BAD_REQUEST, errorMessage.toString());
        }
    }

    private boolean validatePassword(String password) {
        return password.matches(PASS_REG);
    }

    private boolean validateEmail(String email) {
        return email.matches(EMAIL_REG);
    }
}
