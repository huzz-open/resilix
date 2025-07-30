package top.huzz.jaksho.common.constant;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONType;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.writer.ObjectWriter;
import lombok.Getter;

import java.lang.reflect.Type;

/**
 * @author chenji
 * @since 1.0.2
 */
@Getter
@JSONType(serializer = BasicFieldType.BasicFieldTypeSerializer.class, deserializer = BasicFieldType.BasicFieldTypeDeserializer.class)
public final class BasicFieldType {
    public static final BasicFieldType BOOLEAN = new BasicFieldType("boolean");
    public static final BasicFieldType BYTE = new BasicFieldType("byte");
    public static final BasicFieldType SHORT = new BasicFieldType("short");
    public static final BasicFieldType INTEGER = new BasicFieldType("int");
    public static final BasicFieldType LONG = new BasicFieldType("long");
    public static final BasicFieldType FLOAT = new BasicFieldType("float");
    public static final BasicFieldType DOUBLE = new BasicFieldType("double");
    public static final BasicFieldType STRING = new BasicFieldType("string");
    public static final BasicFieldType OBJECT = new BasicFieldType("object");
    public static final BasicFieldType FILE = new BasicFieldType("file");

    private final String name;

    public BasicFieldType(String name) {
        this.name = name;
    }

    public static BasicFieldType of(String name) {
        return switch (name) {
            case "boolean" -> BOOLEAN;
            case "byte" -> BYTE;
            case "short" -> SHORT;
            case "int" -> INTEGER;
            case "long" -> LONG;
            case "float" -> FLOAT;
            case "double" -> DOUBLE;
            case "string" -> STRING;
            case "object" -> OBJECT;
            case "file" -> FILE;
            default -> throw new IllegalArgumentException("Unknown BasicFieldType: " + name);
        };
    }

    @Override
    public String toString() {
        return name;
    }

    public static class BasicFieldTypeSerializer implements ObjectWriter<BasicFieldType> {
        @Override
        public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
            jsonWriter.writeRaw(object.toString());
        }
    }

    public static class BasicFieldTypeDeserializer implements ObjectReader<BasicFieldType> {
        @Override
        public BasicFieldType readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
            return of(jsonReader.readString());
        }
    }
}
