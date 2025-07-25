package top.huzz.jaksho.service;

import org.apache.dubbo.remoting.http12.HttpMethods;
import org.apache.dubbo.remoting.http12.rest.Mapping;

/**
 * @author chenji
 * @since 1.0.2
 */
@Mapping("/biz-field-type")
public interface BizFieldTypeService {
    /**
     * 获取业务字段类型
     *
     * @return 业务字段类型
     */
    @Mapping(method = HttpMethods.GET, value = "")
    String getBizFieldType();
}
