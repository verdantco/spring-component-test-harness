package com.verdantco.componenttest;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.verdantco.componenttest.entity.TestEntity;
import com.verdantco.componenttest.entity.AnotherTestEntity;
import com.verdantco.componenttest.jpa.JPATable;
import com.verdantco.componenttest.jpa.JPATables;
import com.verdantco.componenttest.repo.AnotherTestEntityRepo;
import com.verdantco.componenttest.repo.TestEntityRepo;

@ComponentTest(tables = {
    @JPATable(path = "json/test_entity_1.json", entityType = TestEntity.class),
    @JPATable(path = "json/another_test_entity_1.json", entityType = AnotherTestEntity.class)
})
public class JPATests {

    @Autowired
    TestEntityRepo testEntityRepo;

    @Autowired
    AnotherTestEntityRepo anotherTestEntityRepo;

    @Test
    public void loadsTablesFromFiles_class() {
        assertEquals(1, testEntityRepo.count());
        assertEquals(1, anotherTestEntityRepo.count());   
    }
    
    @Test
    @JPATable(path = "json/test_entity_2.json", entityType = TestEntity.class)
    public void loadsTableFromFile_method() {
        assertEquals(2, testEntityRepo.count());
    }

    @Test
    @JPATables({
        @JPATable(path = "json/test_entity_2.json", entityType = TestEntity.class),
        @JPATable(path = "json/another_test_entity_2.json", entityType = AnotherTestEntity.class)
    })
    public void loadsTablesFromFiles_method() {
        assertEquals(2, testEntityRepo.count());
        assertEquals(2, anotherTestEntityRepo.count());
    }

}
