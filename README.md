# spring-boot-component-test-harness

Annotation-driven component testing utilities for Spring Boot apps. 

This library aims to minimize the configuration necessary to include infrastructure layers in tests which invoke the Spring context, leveraging TestContainers in order to create a production-like environment.

---
## Getting Started

### Automatically instantiating TestContainer infrastructure
`spring-boot-test-harness` detects infrastructure via associated beans during post-processing.

### `@ComponentTest`
The `@ComponentTest` annotation indicates that a test class contains component tests. It serves as a point of for declaring class-level test data, as well as invoking the Spring Context via `@SpringBootTest`. Usage of this annotation imports all `TestExecutionListener` and `BeanPostProcessor` implementations compatible with the application.
```java
public @interface ComponentTest {
    JPATable[] tables() default {};  // see JPATable
}
```
---
## TestContainer Database Setup and Managing test data

**Databases** are detected via the `DataSourceProperties` bean. Schema must be provided via an initialization script loaded from the classpath resource location `sql/${dataSourceProperties.name}.sql` 

Supported JDBC Drivers:
- PostgreSQL

### `@JPATable`
Declares repository contents for a test method or class. Method-level annotations will override class-level datasets
```java
public @interface JPATable {
    String path();          // classpath resource, JSON array of entities  
    Class<?> entityType();  // entity class
}
```
### `@JPATables`
`@Repeatable` container for `@JPATable` annotations created at the method level
```java
public @interface JPATables {
    JPATable[] value();  // see JPATable
}
```