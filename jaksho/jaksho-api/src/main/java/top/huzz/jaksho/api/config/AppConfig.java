package top.huzz.jaksho.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author huzz
 * @since 1.0.2
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "jaksho.app")
public class AppConfig {

    private BizFieldTypeConfig bizFieldType = new BizFieldTypeConfig();
    private ApiDefinitionConfig apiDefinition = new ApiDefinitionConfig();
    private Packages packages = new Packages();

    @Getter
    @Setter
    public static class BizFieldTypeConfig {
        /**
         * object类型的业务字段，允许的最大递归深度
         */
        private int refMaxDepth = 5;
    }

    @Getter
    @Setter
    public static class ApiDefinitionConfig {
        /**
         * API定义的业务字段，允许的最大递归深度
         */
        private int refMaxDepth = 10;
    }

    @Getter
    @Setter
    public static class Packages {
        /**
         * 实体类所在的基础包
         */
        private String domainBase = "top.huzz.jaksho.domain";
    }
}
