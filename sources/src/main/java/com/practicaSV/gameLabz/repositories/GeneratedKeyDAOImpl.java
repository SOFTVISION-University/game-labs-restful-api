package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.GeneratedKey;
import com.practicaSV.gameLabz.domain.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Optional;

@Repository
@Transactional
public class GeneratedKeyDAOImpl implements GeneratedKeyDAO {

    private static final String GET_GENERATED_KEY_BY_KEY = "select k from GeneratedKey k where k.generatedKey = :generatedKey";

    private static final String GET_GENERATED_KEYS_BY_USER = "select k from GeneratedKey k where k.user = :user";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public GeneratedKey saveKey(GeneratedKey key) {

        entityManager.persist(key);
        return key;
    }

    @Override
    public Optional<GeneratedKey> getGeneratedKeyByKey(String key) {

        TypedQuery<GeneratedKey> query = entityManager.createQuery(GET_GENERATED_KEY_BY_KEY, GeneratedKey.class).setParameter("generatedKey", key);

        try{
            GeneratedKey generatedKey = query.getSingleResult();
            generatedKey = entityManager.find(GeneratedKey.class, generatedKey.getId());
            return Optional.ofNullable(generatedKey);
        } catch (NoResultException e){
            return Optional.empty();
        }
    }

    @Override
    public Collection<GeneratedKey> getGeneratedKeyByUser(User user) {

        TypedQuery<GeneratedKey> query = entityManager.createQuery(GET_GENERATED_KEYS_BY_USER, GeneratedKey.class).setParameter("user", user);

        return query.getResultList();
    }
}
