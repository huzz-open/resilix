package top.huzz.jaksho.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.api.service.BizFieldTypeService;
import top.huzz.jaksho.domain.entity.BizFieldType;
import top.huzz.jaksho.domain.entity.ObjectBizFieldTypeRef;
import top.huzz.resilix.core.RunHandlerManager;
import top.huzz.resilix.core.RunHandlerManagerHelper;

/**
 * @author chenji
 * @since 1.0.2
 */
@DubboService
public class BizFieldTypeServiceImpl implements BizFieldTypeService {

	@Override
	@Transactional
	public Integer create(CreateBizFieldTypeRequest request) {
		RunHandlerManager manager = RunHandlerManagerHelper.build(CreatePhase.class);
		CreateRunContext<CreateBizFieldTypeRequest, BizFieldType, ObjectBizFieldTypeRef> context
				= new CreateRunContext<>(request, BizFieldType::new, ObjectBizFieldTypeRef::new, ObjectBizFieldTypeRef::setBizFieldTypeId);
		manager.start(context);
		if (!context.isSuccess()) {
			throw new RuntimeException(context.getException());
		}
		return context.getId();
	}
}
