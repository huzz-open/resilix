package top.huzz.jaksho.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import top.huzz.jaksho.service.BizFieldTypeService;

import java.util.UUID;

/**
 * @author chenji
 * @since 1.0.2
 */
@DubboService
public class BizFieldTypeServiceImpl implements BizFieldTypeService {

    @Override
    public String getBizFieldType() {
        return UUID.randomUUID().toString();
    }
}
