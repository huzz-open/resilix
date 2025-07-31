package top.huzz.jaksho.common.json;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.lang.reflect.Field;

/**
 * @author chenji
 * @since 1.0.2
 */
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class CustomFastjson2TypeHandler extends Fastjson2TypeHandler {
    public CustomFastjson2TypeHandler(Class<?> type) {
        super(type);
    }

    public CustomFastjson2TypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @Override
    public Object parse(String json) {
        CustomJSONType annotation = type.getAnnotation(CustomJSONType.class);
        if (annotation != null) {
            if (annotation.cacheable()) {
                Object cached = CustomJSONTypeCache.deserializeGet(json);
                if (cached != null) {
                    return cached;
                }
            }
            String dm = annotation.deserializerMethod();
            if (StringUtils.isNotBlank(dm)) {
                try {
                    Object invoked = type.getMethod(dm, String.class).invoke(dm, json);
                    if (invoked != null) {
                        if (annotation.cacheable()) {
                            CustomJSONTypeCache.deserializePut(json, invoked);
                        }
                    }
                    return invoked;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to invoke deserializer method: " + dm, e);
                }
            }
        }
        return super.parse(json);
    }

    @Override
    public String toJson(Object obj) {
        CustomJSONType annotation = type.getAnnotation(CustomJSONType.class);
        if (annotation != null) {
            if (annotation.cacheable()) {
                String cached = CustomJSONTypeCache.serializeGet(obj);
                if (cached != null) {
                    return cached;
                }
            }

            String sm = annotation.serializerMethod();
            if (StringUtils.isNotBlank(sm)) {
                // 反射调用该方法
                try {
                    String invoked = (String) type.getMethod(sm).invoke(obj);
                    if (invoked != null) {
                        if (annotation.cacheable()) {
                            CustomJSONTypeCache.serializePut(obj, invoked);
                        }
                    }
                    return invoked;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to invoke serializer method: " + sm, e);
                }
            }
        }
        return super.toJson(obj);
    }
}
