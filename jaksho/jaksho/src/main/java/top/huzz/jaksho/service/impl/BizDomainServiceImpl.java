package top.huzz.jaksho.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.api.service.BizDomainService;
import top.huzz.jaksho.common.entity.BasicProperties;
import top.huzz.jaksho.domain.entity.BizDomain;
import top.huzz.resilix.core.RunHandlerManager;
import top.huzz.resilix.core.RunHandlerManagerHelper;

/**
 * @author chenji
 * @since 1.0.2
 */
@DubboService
public class BizDomainServiceImpl implements BizDomainService {

    @Override
    @Transactional
    public Integer create(CreateBizDomainRequest request) {
        RunHandlerManager manager = RunHandlerManagerHelper.build(CreatePhase.class);
        CreateRunContext<CreateBizDomainRequest, BizDomain, BasicProperties> context = new CreateRunContext<>(request, BizDomain::new);
        manager.start(context);
        if (!context.isSuccess()) {
            throw new RuntimeException(context.getException());
        }
        return context.getId();
    }
}
