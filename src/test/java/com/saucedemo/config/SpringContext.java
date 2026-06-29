package com.saucedemo.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Singleton Spring context holder.
 * Initialised once per JVM — thread-safe for parallel test execution.
 */
public class SpringContext {

    private static final AnnotationConfigApplicationContext context;

    static {
        context = new AnnotationConfigApplicationContext(TestConfig.class);
        context.registerShutdownHook();
    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static TestConfig config() {
        return getBean(TestConfig.class);
    }
}
