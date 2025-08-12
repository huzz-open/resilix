package top.huzz.resilix.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识这是一个业务检查方法。对于业务检查方法，可以被用到{@link BizCheck}注解上。
 * 实际上，任意一个public方法（包含静态方法）都可以被作为业务检查方法，添加该注解作为标识，为了就是提醒其他人在修改这个业务方法的时候，不要随意修改其方法签名。
 *
 * @author huzz
 * @see BizCheck
 * @since 1.0.2
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BizCheckFunction {
    /**
     * 返回方法的唯一id，全局只能存在一个。假如给BizUtils的checkCorn方法添加了该注解，且value返回了"checkCornSpecial"，那么在{@link BizCheck}注解中就可以这样使用
     * <pre>{@code
     * public class BizUtils {
     *     @BizCheckFunction("checkCornSpecial")
     *     public static void checkCorn(String cron) throws InvalidCronExpressionException {
     *         if (StringUtils.isBlank(cron)) {
     *             return;
     *         }
     *         // 其他逻辑...
     *     }
     * }
     * public class DynamicVariableRequest {
     *     @BizCheck(value = "#checkCornSpecial(#corn)")
     *     private String cron;
     * }}
     * </pre>
     * <p>在没有指定value的情况下，则可以使用两种：“#简单类名_方法名”，“#方法名”</p>
     * <pre>{@code
     * public class BizUtils {
     *     @BizCheckFunction
     *     public static void checkCorn(String cron) throws InvalidCronExpressionException {
     *         if (StringUtils.isBlank(cron)) {
     *             return;
     *         }
     *         // 其他逻辑...
     *     }
     * }
     * public class DynamicVariableRequest {
     *     @BizCheck(value = "#checkCorn(#corn)")
     *     private String cron;
     *
     *     @BizCheck(value = "#BizUtils_checkCorn(#corn2)")
     *     private String cron2;
     * }}
     * </pre>
     */
    String value() default "";
}
