package top.huzz.jaksho.api.json;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import top.huzz.jaksho.api.config.AppConfig;
import top.huzz.jaksho.common.entity.BasicProperties;
import top.huzz.jaksho.common.entity.CombineResult;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CombineResult 的自定义序列化器
 * <p>
 * 将 CombineResult 序列化为更友好的 JSON 格式
 *
 * @author huzz
 * @since 1.0.2
 */
public class CombineResultSerializer implements ObjectWriter<CombineResult> {

    private final AppConfig appConfig;

    public CombineResultSerializer(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        if (object == null) {
            jsonWriter.writeNull();
            return;
        }

        JSONObject jsonObject = new JSONObject();


        AppConfig.CombineResultConfig config = appConfig.getCombineResult();
        CombineResult cr = (CombineResult) object;


        if (config.isFullExpand()) {
            // 完全展开平铺模式
            writeFullyExpanded(jsonObject, cr, config);
        } else if (config.isExpand()) {
            // 简单展开模式：将 domains 中的内容直接平铺到根级别
            writeExpanded(jsonObject, cr, config);
        } else {
            jsonObject.put(CombineResult.FIELD_DOMAINS, cr.getDomains());
        }

        jsonWriter.write(jsonObject);
    }

    /**
     * 简单展开模式：将 domains 中的内容直接平铺到根级别
     */
    private void writeExpanded(JSONObject jsonObject, CombineResult cr, AppConfig.CombineResultConfig config) {
        for (Map.Entry<String, BasicProperties> entry : cr.getDomains().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 如果配置了使用实体类简单类名作为域关键字，则使用类名
            if (config.isUseDomainClassAsKey() && value != null) {
                key = value.getClass().getSimpleName();
                // 将首字母转为小写
                if (!key.isEmpty()) {
                    key = Character.toLowerCase(key.charAt(0)) + key.substring(1);
                }
            }

            jsonObject.put(key, value);
        }
    }

    /**
     * 完全展开平铺模式：将 domains 中的所有字段完全平铺到根级别
     */
    private void writeFullyExpanded(JSONObject jsonObject, CombineResult cr, AppConfig.CombineResultConfig config) {
        Map<String, Object> flattenedData = new LinkedHashMap<>();

        for (Map.Entry<String, BasicProperties> entry : cr.getDomains().entrySet()) {
            String domainKey = entry.getKey();
            BasicProperties domainValue = entry.getValue();

            if (domainValue == null) {
                continue;
            }

            // 如果配置了使用实体类简单类名作为域关键字，则使用类名
            if (config.isUseDomainClassAsKey()) {
                domainKey = domainValue.getClass().getSimpleName();
                if (!domainKey.isEmpty()) {
                    domainKey = Character.toLowerCase(domainKey.charAt(0)) + domainKey.substring(1);
                }
            }

            // 将对象转换为 Map 进行平铺
            Map<String, Object> domainMap = domainValue.properties();

            if (config.isFullExpandSmart()) {
                // 智能平铺策略：检查 key 冲突
                for (Map.Entry<String, Object> fieldEntry : domainMap.entrySet()) {
                    String fieldKey = fieldEntry.getKey();
                    Object fieldValue = fieldEntry.getValue();

                    if (flattenedData.containsKey(fieldKey)) {
                        // 有冲突，使用 "域关键字+字段名" 进行平铺
                        String newKey = domainKey + Character.toUpperCase(fieldKey.charAt(0)) + fieldKey.substring(1);
                        flattenedData.put(newKey, fieldValue);
                    } else {
                        // 无冲突，直接使用原始 key
                        flattenedData.put(fieldKey, fieldValue);
                    }
                }
            } else {
                // 简单拼接策略：使用 "域关键字+字段名" 进行平铺
                for (Map.Entry<String, Object> fieldEntry : domainMap.entrySet()) {
                    String fieldKey = fieldEntry.getKey();
                    Object fieldValue = fieldEntry.getValue();
                    String newKey = domainKey + Character.toUpperCase(fieldKey.charAt(0)) + fieldKey.substring(1);
                    flattenedData.put(newKey, fieldValue);
                }
            }
        }

        jsonObject.putAll(flattenedData);
    }
}
