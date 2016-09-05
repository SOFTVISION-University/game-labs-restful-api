package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.Friend;
import com.practicaSV.gameLabz.domain.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collection;

@Repository
@Transactional
public class FriendDAOImpl implements FriendDAO {

    private static final String GET_ALL_FRIENDS = "select f from Friend f where f.user = :user";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Friend saveFriend(Friend friend) {

        entityManager.persist(friend);

        return friend;
    }

    @Override
    public void removeFriend(Friend friend) {}

    @Override
    public Collection<Friend> getAll(User user) {

        TypedQuery<Friend> query = entityManager.createQuery(GET_ALL_FRIENDS, Friend.class).setParameter("user", user);
        return query.getResultList();
    }
}
