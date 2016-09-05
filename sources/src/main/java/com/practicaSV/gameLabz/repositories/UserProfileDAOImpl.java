package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserProfileDAOImpl implements UserProfileDAO {

    private static final String GET_PROFILE_BY_USER = "select p from UserProfile p where p.user = :user";

    private static final String GET_PROFILE_BY_LINK_ID = "select p from UserProfile p where p.sharedLinkId = :sharedLinkId";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private FriendDAO friendDAO;

    @Autowired
    private GameOrderDAO gameOrderDAO;

    @Autowired
    private GeneratedKeyDAO generatedKeyDAO;

    @Override
    public UserProfile saveProfile(UserProfile userProfile) {

        entityManager.persist(userProfile);

        return userProfile;
    }

    @Override
    public Optional<UserProfile> getProfileByUser(User user) {

        TypedQuery<UserProfile> query = entityManager.createQuery(GET_PROFILE_BY_USER, UserProfile.class).setParameter("user", user);

        try {
            UserProfile profile = query.getSingleResult();
            UserProfile profileToReturn = entityManager.find(UserProfile.class, profile.getId());

            List<Friend> friends = new ArrayList<>(friendDAO.getAll(user));
            profileToReturn.setFriends(friends);

            List<Game> ownedGames = new ArrayList<>(gameOrderDAO.getGamesByUser(user));
            profileToReturn.setOwnedGames(ownedGames);

            List<GeneratedKey> keys = new ArrayList<>(generatedKeyDAO.getGeneratedKeyByUser(user));
            profileToReturn.setKeys(keys);

            return Optional.ofNullable(profileToReturn);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserProfile> getProfileByLinkId(String sharedLinkId) {

        TypedQuery<UserProfile> query = entityManager.createQuery(GET_PROFILE_BY_LINK_ID, UserProfile.class).setParameter("sharedLinkId", sharedLinkId);
        try {
            UserProfile profile = query.getSingleResult();
            UserProfile profileToReturn = entityManager.find(UserProfile.class, profile.getId());
            return Optional.ofNullable(profileToReturn);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updateProfile(UserProfile profile) {

        UserProfile profileToUpdate = entityManager.find(UserProfile.class, profile.getId());

        if (profile.getPoints() != null) {
            profileToUpdate.setPoints(profile.getPoints());
        }

        if (profile.getOwnedGames() != null || !profile.getOwnedGames().isEmpty()) {
            profileToUpdate.setOwnedGames(profile.getOwnedGames());
        }

        if (profile.getKeys() != null || !profile.getKeys().isEmpty()) {
            profileToUpdate.setKeys(profile.getKeys());
        }

        if (profile.getFriends() != null || !profile.getFriends().isEmpty()) {
            profileToUpdate.setFriends(profile.getFriends());
        }

        if (profile.getUser() != null) {
            profileToUpdate.setUser(profile.getUser());
        }

        if (profile.getSharedLinkId() != null) {
            profileToUpdate.setSharedLinkId(profile.getSharedLinkId());
        }

        if (profile.getSharedLink() != null) {
            profileToUpdate.setSharedLink(profile.getSharedLink());
        }

        entityManager.flush();
    }


}
