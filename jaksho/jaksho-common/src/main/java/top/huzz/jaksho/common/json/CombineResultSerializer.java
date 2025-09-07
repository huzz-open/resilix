package top.huzz.jaksho.common.json;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import top.huzz.jaksho.common.entity.CombineResult;

import java.lang.reflect.Type;

/**
 * CombineResult 的自定义序列化器
 * <p>
 * 将 CombineResult 序列化为更友好的 JSON 格式
 *
 * @author huzz
 * @since 1.0.2
 */
public class CombineResultSerializer implements ObjectWriter<CombineResult> {

    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        if (object == null) {
            jsonWriter.writeNull();
            return;
        }

        if (object instanceof CombineResult cr) {
            jsonWriter.write(cr.getDomains());
        }
    }
}
