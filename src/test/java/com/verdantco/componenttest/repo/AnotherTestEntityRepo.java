package com.verdantco.componenttest.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.verdantco.componenttest.entity.AnotherTestEntity;

public interface AnotherTestEntityRepo extends JpaRepository<AnotherTestEntity, UUID> {
    
}
