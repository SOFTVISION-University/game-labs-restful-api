package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.FriendRequest;
import com.practicaSV.gameLabz.domain.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Optional;

@Repository
@Transactional
public class FriendRequestDAOImpl implements FriendRequestDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String GET_ALL_REQUESTS = "select fr from FriendRequest fr where fr.toWhom = :user or fr.fromWho = :user";

    @Override
    public FriendRequest saveRequest(FriendRequest friendRequest) {

        entityManager.persist(friendRequest);

        return friendRequest;
    }

    @Override
    public void removeRequest(FriendRequest friendRequest) {

        if (!entityManager.contains(friendRequest)) {
            friendRequest = entityManager.find(FriendRequest.class, friendRequest.getId());
        }
        entityManager.remove(friendRequest);
    }

    @Override
    public Collection<FriendRequest> getAllByUser(User user) {

        TypedQuery<FriendRequest> query = entityManager.createQuery(GET_ALL_REQUESTS, FriendRequest.class).setParameter("user", user);

        return query.getResultList();
    }

    @Override
    public Optional<FriendRequest> getFriendRequestById(Long id) {

        return Optional.ofNullable(entityManager.find(FriendRequest.class, id));
    }
}
