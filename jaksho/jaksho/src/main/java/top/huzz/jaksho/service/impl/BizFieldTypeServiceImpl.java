package top.huzz.jaksho.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import top.huzz.jaksho.biz.phase.CreatePhase;
import top.huzz.jaksho.service.BizFieldTypeService;
import top.huzz.resilix.core.RunHandlerManager;
import top.huzz.resilix.core.RunHandlerManagerHelper;

/**
 * @author chenji
 * @since 1.0.2
 */
@DubboService
public class BizFieldTypeServiceImpl implements BizFieldTypeService {

    @Override
    public Integer create(CreateBizFieldTypeRequest request) {
        RunHandlerManager manager = RunHandlerManagerHelper.build(CreatePhase.class);
        manager.start();
        return null;
    }
}
