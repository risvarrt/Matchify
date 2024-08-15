package com.matchify.repository;

import com.matchify.dto.response.FindMatchesResponse;
import com.matchify.model.User;
import com.matchify.model.UserMatches;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface UserMatchesRepository extends JpaRepository<UserMatches, Integer> {
  boolean existsByUser_UserIdAndMatchedUser_UserId(Integer userId, Integer matchedUserId);
}