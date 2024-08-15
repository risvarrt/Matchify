package com.matchify.repository;

import com.matchify.model.UserInterest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInterestRepository extends JpaRepository<UserInterest, Integer> {
  @Query("SELECT um.interestId FROM UserInterest um WHERE um.userId = :userId")
  List<Integer> findUserInterests(@Param("userId") Integer userId);
}
