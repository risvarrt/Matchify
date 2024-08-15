package com.matchify.repository;

import com.matchify.model.InterestGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestGroupRepository extends JpaRepository<InterestGroup, Integer> {
}
