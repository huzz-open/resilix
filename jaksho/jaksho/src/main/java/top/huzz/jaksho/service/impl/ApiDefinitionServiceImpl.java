package top.huzz.jaksho.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.api.service.ApiDefinitionService;
import top.huzz.jaksho.domain.entity.ApiDefinition;
import top.huzz.jaksho.domain.entity.ApiDefinitionField;
import top.huzz.resilix.core.RunHandlerManager;
import top.huzz.resilix.core.RunHandlerManagerHelper;

/**
 * @author chenji
 * @since 1.0.2
 */
@DubboService
public class ApiDefinitionServiceImpl implements ApiDefinitionService {

	@Override
	@Transactional
	public Integer create(CreateApiDefinitionRequest request) {
		RunHandlerManager manager = RunHandlerManagerHelper.build(CreatePhase.class);
		CreateRunContext<CreateApiDefinitionRequest, ApiDefinition, ApiDefinitionField> context
				= new CreateRunContext<>(request, ApiDefinition::new, ApiDefinitionField::new, ApiDefinitionField::setApiId);
		manager.start(context);
		if (!context.isSuccess()) {
			throw new RuntimeException(context.getException());
		}
		return context.getId();
	}
}
