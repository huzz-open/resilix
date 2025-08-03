package top.huzz.jaksho.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import top.huzz.jaksho.service.BizFieldTypeService;

/**
 * @author chenji
 * @since 1.0.2
 */
@DubboService
public class BizFieldTypeServiceImpl implements BizFieldTypeService {

    @Override
    public int create(CreateBizFieldTypeRequest request) {
        return 1;
    }
}
