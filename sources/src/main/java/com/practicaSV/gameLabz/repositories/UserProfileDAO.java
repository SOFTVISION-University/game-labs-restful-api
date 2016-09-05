package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.domain.UserProfile;

import java.util.Optional;

public interface UserProfileDAO {

    UserProfile saveProfile(UserProfile userProfile);

    Optional<UserProfile> getProfileByUser(User user);

    Optional<UserProfile> getProfileByLinkId(String sharedLinkId);

    void updateProfile(UserProfile profile);
}
