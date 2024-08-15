package com.matchify.repository;

import com.matchify.model.InterestCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestRepository extends JpaRepository<InterestCategory, Integer> {
    List<InterestCategory> findAllByGroupId(Integer groupId);
}
