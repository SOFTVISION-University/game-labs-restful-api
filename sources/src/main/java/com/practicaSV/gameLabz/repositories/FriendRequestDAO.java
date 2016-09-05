package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.FriendRequest;
import com.practicaSV.gameLabz.domain.User;

import java.util.Collection;
import java.util.Optional;

public interface FriendRequestDAO {

    FriendRequest saveRequest(FriendRequest friendRequest);

    void removeRequest(FriendRequest friendRequest);

    Collection<FriendRequest> getAllByUser(User user);

    Optional<FriendRequest> getFriendRequestById(Long id);
}
