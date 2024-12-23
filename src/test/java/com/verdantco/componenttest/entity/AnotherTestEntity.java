package com.verdantco.componenttest.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(schema = "test_schema", name = "another_test_entity")
public class AnotherTestEntity {
    
    @Id
    public UUID id;

    public String status;

}
