package org.huzz.resilix.api.util;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenji
 * @since 1.0.0
 */
@Configuration("applicationBeanUtil")
public class ApplicationBeanUtil implements ApplicationContextAware {
    private static ApplicationContext context;

    ApplicationBeanUtil() {
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext context) throws BeansException {
        ApplicationBeanUtil.context = context;
    }

    public static Object getBean(String beanName) {
        try {
            return context != null && !StringUtils.isBlank(beanName) ? context.getBean(beanName) : null;
        } catch (BeansException e) {
            return null;
        }
    }

    public static <T> T getBean(Class<T> className) {
        try {
            return context != null && className != null ? context.getBean(className) : null;
        } catch (BeansException e) {
            return null;
        }
    }
}
