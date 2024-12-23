package com.verdantco.componenttest.jpa;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.lang.NonNull;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.verdantco.componenttest.ComponentTest;

public class JPAExecutionListener implements TestExecutionListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper mapper = new ObjectMapper();

    public void beforeTestClass(@NonNull TestContext tc) {

        var classTables = classTables(tc);
        for (JPATable t : classTables) {
            logger.info("{} - loading {} class-level data from {}", tc.getTestClass().getSimpleName(),
                    t.entityType().getSimpleName(), t.path());

            loadAndSave(tc, t.path(), t.entityType());
        }
    }

    public void afterTestClass(@NonNull TestContext tc) {

        var classTables = classTables(tc);
        for (JPATable t : classTables) {
            logger.info("{} - flushing {} class-level data", tc.getTestClass().getSimpleName(),
                    t.entityType().getSimpleName());

            repoFor(tc.getApplicationContext(), t.entityType()).deleteAll();
        }
    }

    public void beforeTestMethod(@NonNull TestContext tc) {

        var overrides = overrides(tc);
        for (JPATable t : overrides) {
            logger.info("{} - overriding class-level {} data", tc.getTestMethod().getName(),
                    t.entityType().getSimpleName());

            repoFor(tc.getApplicationContext(), t.entityType()).deleteAll();
        }

        var methodTables = methodTables(tc);
        for (JPATable t : methodTables) {
            logger.info("{} - loading {} method-level data from {}", tc.getTestMethod().getName(),
                    t.entityType().getSimpleName(), t.path());

            loadAndSave(tc, t.path(), t.entityType());
        }
    }

    public void afterTestMethod(@NonNull TestContext tc) {

        var methodTables = methodTables(tc);
        for (JPATable t : methodTables) {
            logger.info("{} - flushing {} method-level data", tc.getTestMethod().getName(),
                    t.entityType().getSimpleName());

            repoFor(tc.getApplicationContext(), t.entityType()).deleteAll();
        }

        var overrides = overrides(tc);
        for (JPATable t : overrides) {
            logger.info("{} - reloading class-level {} data from {}", tc.getTestMethod().getName(),
                    t.entityType().getSimpleName(), t.path());

            loadAndSave(tc, t.path(), t.entityType());
        }
    }

    private JPATable[] classTables(TestContext tc) {

        var tables = tc.getTestClass().getAnnotationsByType(JPATable.class);
        var componentTest = tc.getTestClass().getAnnotation(ComponentTest.class);

        var classTables = new JPATable[componentTest.tables().length + tables.length];
        for (int i = 0; i < classTables.length; i++) {
            classTables[i] = i < tables.length
                    ? tables[i]
                    : componentTest.tables()[i - tables.length];
        }

        return classTables;
    }

    private JPATable[] methodTables(TestContext tc) {
        return tc.getTestMethod().getAnnotationsByType(JPATable.class);
    }

    private JPATable[] overrides(TestContext tc) {

        var classTables = classTables(tc);
        var methodTables = methodTables(tc);

        return Arrays.stream(classTables)
                .filter(ct -> {
                    for (JPATable mt : methodTables) {
                        if (mt.entityType().equals(ct.entityType())) {
                            return true;
                        }
                    }

                    return false;
                }).toArray(n -> new JPATable[n]);
    }

    private <T> void loadAndSave(TestContext tc, String path, Class<T> entityType)
            throws IllegalArgumentException, UnsupportedOperationException {

        try {
            var data = loadDataFile(path, entityType);

            var repo = repoFor(tc.getApplicationContext(), entityType);
            if (repo == null) {
                throw new UnsupportedOperationException(tc.getTestMethod().getName() + " - No repository found for "
                        + entityType.getSimpleName() + " entities");
            }

            repo.saveAll(data);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read data at " + path, e);
        }
    }

    private <T> List<T> loadDataFile(String path, Class<T> type) throws IOException {

        var file = new ClassPathResource(path).getFile();

        return mapper.readerForListOf(type).readValue(file);
    }

    @SuppressWarnings("unchecked")
    private <T> JpaRepository<T, ?> repoFor(ApplicationContext context, Class<T> entityType) {

        var repos = new Repositories(context);
        var repo = repos.getRepositoryFor(entityType)
                .orElse(null);

        return (JpaRepository<T, ?>) repo;
    }

}
