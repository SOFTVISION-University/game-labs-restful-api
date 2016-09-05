package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.Friend;
import com.practicaSV.gameLabz.domain.User;

import java.util.Collection;

public interface FriendDAO {

    Friend saveFriend(Friend friend);

    void removeFriend(Friend friend);

    Collection<Friend> getAll(User user);
}
