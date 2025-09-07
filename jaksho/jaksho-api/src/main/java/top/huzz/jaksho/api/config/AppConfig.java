package top.huzz.jaksho.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import top.huzz.jaksho.common.entity.CombineResult;

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
    private PackagesConfig packages = new PackagesConfig();
    private CombineResultConfig combineResult = new CombineResultConfig();

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
    public static class PackagesConfig {
        /**
         * 实体类所在的基础包
         */
        private String domainBase = "top.huzz.jaksho.domain";
    }

    @Getter
    @Setter
    public static class CombineResultConfig {
        /**
         * 是否将 {@link CombineResult} 的 domains属性展开平铺。示例如下：
         * 假如 CombineResult 内容为：
         * <pre>
         * {
         *   "domains": {
         *      "user": { "id": 1, "name": "Alice" },
         *      "order": { "id": 101, "amount": 250 }
         *   }
         * }
         * </pre>
         * <p>平铺后：</p>
         * <pre>
         * {
         *   "user": { "id": 1, "name": "Alice" },
         *   "order": { "id": 101, "amount": 250 }
         * }
         * </pre>
         *
         * @see top.huzz.jaksho.common.entity.CombineResult
         */
        private boolean expand = true;

        /**
         * 是否将 {@link CombineResult} 的 domains 属性 <b>完全</b> 展开平铺。 示例如下：
         * 假如 CombineResult 内容为：
         * <pre>
         * {
         *   "domains": {
         *      "user": { "id": 1, "name": "Alice" },
         *      "order": { "id": 101, "amount": 250 }
         *   }
         * }
         * </pre>
         * <p>完全平铺后有2种策略：</p>
         * <li>当{@link #fullExpandSmart}为true的时候，会智能平铺，如下所示：</li>
         * <pre>
         * {
         *   "id": 1,
         *   "name": "Alice",
         *   "orderId": 101,
         *   "amount": 250
         * }
         * </pre>
         * <li>当{@link #fullExpandSmart}为false的时候，会简单拼接key进行平铺，如下所示：</li>
         * <pre>
         * {
         *   "userId": 1,
         *   "userName": "Alice",
         *   "orderId": 101,
         *   "orderAmount": 250
         * }
         * </pre>
         */
        private boolean fullExpand = true;

        /**
         * 当开启 {@link #fullExpand} 时，是否智能地选择平铺策略，而不是简单地将 key 拼接后平铺。
         * <p>智能平铺策略：</p>
         * <li>如果 key 没有冲突，则直接使用原始 key 进行平铺。</li>
         * <li>如果 key 有冲突，则使用 "域关键字+字段名" 进行平铺。</li>
         * <p>智能平铺策略有的时候会出现不确定的情况，多个表联合查询的时候，就比较难以区分返回的字段到底是什么，这个字段的使用场景只有一种：当域关键字长度较长的时候，可以在一定程度上减少字段长度</p>
         */
        private boolean fullExpandSmart = false;

        /**
         * 当展开平铺后，是否使用实体类的简单类名作为域关键字。 例如上例中，使用 "User" 对象的简单类名 "user" 作为 域关键字。
         * <p>这种情况适用于表名和实体类名不一致的场景，比如表名称具备一些特殊前缀、或具备分表后缀等。</p>
         */
        private boolean useDomainClassAsKey = true;
    }
}
