package top.huzz.jaksho.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import top.huzz.jaksho.common.constant.FieldAttribute;

import java.io.IOException;

/**
 * FieldAttribute JSON 序列化器
 * 将 FieldAttribute 对象序列化为整数值
 *
 * @author system
 * @since 1.0.3
 */
public class FieldAttributeSerializer extends JsonSerializer<FieldAttribute> {
    
    @Override
    public void serialize(FieldAttribute value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeNumber(value.getValue());
        }
    }
}
