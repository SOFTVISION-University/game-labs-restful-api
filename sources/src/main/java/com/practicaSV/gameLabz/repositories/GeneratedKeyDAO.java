package com.practicaSV.gameLabz.repositories;

import com.practicaSV.gameLabz.domain.GeneratedKey;
import com.practicaSV.gameLabz.domain.User;

import java.util.Collection;
import java.util.Optional;

public interface GeneratedKeyDAO {

    GeneratedKey saveKey(GeneratedKey key);

    Optional<GeneratedKey> getGeneratedKeyByKey(String key);

    Collection<GeneratedKey> getGeneratedKeyByUser(User user);
}