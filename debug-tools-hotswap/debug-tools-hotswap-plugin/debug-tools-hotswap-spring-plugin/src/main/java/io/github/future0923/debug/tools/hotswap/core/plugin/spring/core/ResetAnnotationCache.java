package io.github.future0923.debug.tools.hotswap.core.plugin.spring.core;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * This class provides a method to reset the Spring annotation scanner cache.
 */
public class ResetAnnotationCache {

    private static final Logger LOGGER = Logger.getLogger(ResetAnnotationCache.class);
    /**
     * Reset Spring annotation scanner.
     * @since 5.x
     */
    @SuppressWarnings("unchecked")
    public static void resetAnnotationScanner(DefaultListableBeanFactory defaultListableBeanFactory) {
        Map<Method, String> declaredAnnotationCache = (Map<Method, String>) ReflectionHelper.getNoException(null,
                "org.springframework.core.annotation.AnnotationsScanner",
                defaultListableBeanFactory.getClass().getClassLoader(), "declaredAnnotationCache");
        if (declaredAnnotationCache != null) {
            LOGGER.trace("Cache cleared: AnnotationsScanner.beanNameCache");
            declaredAnnotationCache.clear();
        }

        Map<Method, String> baseTypeMethodsCache = (Map<Method, String>) ReflectionHelper.getNoException(null,
                "org.springframework.core.annotation.AnnotationsScanner",
                defaultListableBeanFactory.getClass().getClassLoader(), "baseTypeMethodsCache");
        if (baseTypeMethodsCache != null) {
            LOGGER.trace("Cache cleared: BeanAnnotationHelper.baseTypeMethodsCache");
            baseTypeMethodsCache.clear();
        }
    }
}
