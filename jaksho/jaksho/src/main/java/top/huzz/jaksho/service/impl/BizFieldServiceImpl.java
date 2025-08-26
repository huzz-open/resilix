package top.huzz.jaksho.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.api.service.BizFieldService;
import top.huzz.jaksho.common.able.Saver;
import top.huzz.jaksho.common.able.SaverBuilder;
import top.huzz.jaksho.common.entity.BasicProperties;
import top.huzz.jaksho.domain.entity.BizField;
import top.huzz.jaksho.domain.entity.BizFieldDomain;
import top.huzz.resilix.core.RunHandlerManager;
import top.huzz.resilix.core.RunHandlerManagerHelper;

/**
 * @author chenji
 * @since 1.0.2
 */
@DubboService
public class BizFieldServiceImpl implements BizFieldService {

    @Override
    @Transactional
    public Integer create(CreateBizFieldRequest request) {
        RunHandlerManager manager = RunHandlerManagerHelper.build(CreatePhase.class);
        CreateRunContext<CreateBizFieldRequest, BizField, BasicProperties> context = new CreateRunContext<>(request, BizField::new);
        context.setCreatedObjectConsumer((bucketAble, bizField) -> {
            BizFieldDomain bizFieldDomain = new BizFieldDomain();
            bizFieldDomain.setBizFieldId(bizField.getId());
            bizFieldDomain.setBizDomainId(-1);
            bizFieldDomain.setBizFieldTypeId(bizField.getBizFieldTypeId());
            SaverBuilder<BizFieldDomain, Integer> saverBuilder = bucketAble.getSaverBuilder();
            Saver<BizFieldDomain, Integer> saver = saverBuilder.getSaver(bizFieldDomain);
            saver.save(bizFieldDomain);
        });
        manager.start(context);
        if (!context.isSuccess()) {
            throw new RuntimeException(context.getException());
        }
        return context.getId();
    }
}
