package top.huzz.resilix.util;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;

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

    /**
     * 获取指定类型的Bean列表
     *
     * @param beanClass Bean的类型
     * @param <T>       Bean的类型参数
     * @return 返回指定类型的Bean实例列表
     */
    public static <T> List<T> listBean(@Nonnull Class<T> beanClass) {
        return applicationContext.getBeansOfType(beanClass).values().stream().toList();
    }
}
