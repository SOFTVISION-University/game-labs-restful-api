package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.User;

import java.util.Collection;
import java.util.Optional;

public interface UserDAO {

    User saveUser(User user);

    Collection<User> getAll();

    Optional<User> getUserByUserName(String userName);

    void removeUser(User user);
}
