package ru.hselabwork.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.hselabwork.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {

}
