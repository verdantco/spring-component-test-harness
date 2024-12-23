package com.verdantco.componenttest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;

import com.verdantco.componenttest.jpa.JPAExecutionListener;
import com.verdantco.componenttest.jpa.JPATable;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@TestExecutionListeners(listeners = {
    JPAExecutionListener.class
}, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
public @interface ComponentTest {
    JPATable[] tables() default {};
}
