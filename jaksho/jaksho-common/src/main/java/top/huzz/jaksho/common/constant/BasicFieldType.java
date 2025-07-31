package top.huzz.jaksho.common.constant;

import lombok.Getter;
import top.huzz.jaksho.common.json.CustomJSONType;

/**
 * @author chenji
 * @since 1.0.2
 */
@Getter
@CustomJSONType
public final class BasicFieldType {
    public static final BasicFieldType BOOLEAN = new BasicFieldType("boolean");
    public static final BasicFieldType INT8 = new BasicFieldType("int8");
    public static final BasicFieldType INT16 = new BasicFieldType("int16");
    public static final BasicFieldType INT32 = new BasicFieldType("int32");
    public static final BasicFieldType INT64 = new BasicFieldType("int64");
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
            case "int8" -> INT8;
            case "int16" -> INT16;
            case "int32" -> INT32;
            case "int64" -> INT64;
            case "float" -> FLOAT;
            case "double" -> DOUBLE;
            case "string" -> STRING;
            case "object" -> OBJECT;
            case "file" -> FILE;
            default -> new BasicFieldType(name);
        };
    }

    @Override
    public String toString() {
        return name;
    }

}
