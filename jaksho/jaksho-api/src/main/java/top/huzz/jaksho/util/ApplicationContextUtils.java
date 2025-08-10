package top.huzz.jaksho.util;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author huzz
 * @since 1.0.2
 */
public class ApplicationContextUtils implements ApplicationContextAware {

    @Getter
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtils.applicationContext = applicationContext;
    }

    /**
     * 获取指定类型的Bean
     *
     * @param beanClass Bean的类型
     * @param <T>       Bean的类型参数
     * @return 返回指定类型的Bean实例
     */
    public static <T> T getBean(@Nonnull Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }
}
