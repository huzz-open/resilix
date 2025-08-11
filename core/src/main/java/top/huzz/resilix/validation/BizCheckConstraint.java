package top.huzz.resilix.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.expression.spel.support.StandardTypeLocator;
import top.huzz.resilix.annotation.BizCheck;
import top.huzz.resilix.constants.EnvType;
import top.huzz.resilix.util.ApplicationContextUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * @author huzz
 * @since 1.0.2
 */
public class BizCheckConstraint implements ConstraintValidator<BizCheck, Object> {

    private String when;
    private String value;

    @Override
    public void initialize(BizCheck constraintAnnotation) {
        this.when = constraintAnnotation.when();
        this.value = constraintAnnotation.value();
    }

    @SneakyThrows
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        StandardTypeLocator typeLocator = new StandardTypeLocator() {
            @Override
            public Class<?> findType(String typeName) throws EvaluationException {
                return super.findType(typeName);
            }
        };
        // 注册自定义类型，就可以省略包名直接使用类名
        typeLocator.registerImport("top.huzz.resilix.constants");
        TypeConverter typeConverter = new StandardTypeConverter();
        SpelExpressionParser parser = new SpelExpressionParser();

        // 依次是调用bean的方法，调用当前对象的name属性，调用变量hello
        SpelExpression spelExpression = parser.parseRaw("""
                @bizFieldTypeMapper.toString() + '
                ' +
                name + '
                ' +
                #test1(#hello) + '
                ' +
                test2(#hello) + '
                ' +
                test3(#hello) + '
                ' +
                (T(EnvType).X == type)"""
        );

        StandardEvaluationContext ctx = new StandardEvaluationContext(new XXX());
        ctx.setBeanResolver(new BeanFactoryResolver(ApplicationContextUtils.getApplicationContext()));
        ctx.setTypeConverter(typeConverter);
        ctx.setTypeLocator(typeLocator);
        ctx.setVariable("hello", "555dadad");
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle test1 = lookup.findStatic(BizCheckConstraint.class, "test1", MethodType.methodType(String.class, String.class));
        ctx.registerFunction("test1", test1);
        Object value1 = spelExpression.getValue(ctx);
        System.out.println(value1);
        return false;
    }

    @Getter
    @Setter
    static class XXX {
        private String name;
        private int age;
        private EnvType type;

        public XXX() {
            this.name = "huzz";
            this.age = 18;
            this.type = EnvType.X;
        }

        public static String test2(String world) {
            return "length2: " + world.length();
        }

        public String test3(String world) {
            return "length3: " + world.length();
        }
    }

    public static String test1(String world) {
        return "length: " + world.length();
    }

}
