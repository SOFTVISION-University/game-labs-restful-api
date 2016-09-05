package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.domain.UserProfile;
import com.practicaSV.gameLabz.exceptions.InvalidValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Optional;

@Repository
@Transactional
public class UserDAOImpl implements UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserProfileDAO userProfileDAO;

    private static final String GET_ALL_USERS = "select u from User u";

    @Override
    public User saveUser(User user) {
        entityManager.persist(user);
        return user;
    }

    @Override
    public Collection<User> getAll() {
        TypedQuery<User> query = entityManager.createQuery(GET_ALL_USERS, User.class);
        return query.getResultList();
    }

    @Override
    public Optional<User> getUserByUserName(String userName) {
        return Optional.ofNullable(entityManager.find(User.class, userName));
    }

    @Override
    public void removeUser(User user) {

        UserProfile profileFromDb = userProfileDAO.getProfileByUser(user).orElseThrow(() -> new InvalidValueException(HttpStatus.BAD_REQUEST, "No user to delete!"));
        UserProfile profileToDelete = entityManager.find(UserProfile.class, profileFromDb.getId());

        entityManager.remove(profileToDelete);

        if(!entityManager.contains(user)){
            user = entityManager.find(User.class, user.getUserName());
        }
        entityManager.remove(user);
    }
}
