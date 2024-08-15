package com.matchify.repository;

import com.matchify.model.ChatStatusForUser;
import com.matchify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    User findByUserId(Integer userId);

    Boolean existsByEmail(String email);

    List<User> findAllByStatus(ChatStatusForUser status);


    List<User> findAllByUserIdIn(List<Integer> userIds);
}
