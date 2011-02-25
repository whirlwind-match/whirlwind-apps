package com.wwm.proto.email.integration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Meta-annotation to capture test config in one place for DRYness.
 * @author Neale Upstone
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/spring/common/*-context.xml")
public @interface SpringIntegrationTest {

}
