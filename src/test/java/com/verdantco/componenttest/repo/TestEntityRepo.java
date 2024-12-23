package com.verdantco.componenttest.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.verdantco.componenttest.entity.TestEntity;

public interface TestEntityRepo extends JpaRepository<TestEntity, UUID> {
    
}
