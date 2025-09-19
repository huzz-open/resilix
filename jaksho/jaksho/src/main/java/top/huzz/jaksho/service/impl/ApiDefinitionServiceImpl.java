package top.huzz.jaksho.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.helper.QueryHelper;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.api.service.ApiDefinitionService;
import top.huzz.jaksho.common.entity.PageResult;
import top.huzz.jaksho.common.session.Session;
import top.huzz.jaksho.domain.entity.ApiDefinition;
import top.huzz.jaksho.domain.entity.ApiDefinitionField;
import top.huzz.jaksho.domain.mapper.ApiDefinitionMapper;
import top.huzz.resilix.core.RunHandlerManager;
import top.huzz.resilix.core.RunHandlerManagerHelper;

import java.util.List;

/**
 * @author chenji
 * @since 1.0.2
 */
@DubboService
public class ApiDefinitionServiceImpl implements ApiDefinitionService {
    @Resource
    private ApiDefinitionMapper apiDefinitionMapper;

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

    @Override
    public PageResult<ApiDefinition> pageQuery(PageQueryRequest request) {
        int workspaceId = Session.currentWorkspaceId();
        Page<ApiDefinition> page = request.toPage();
        List<ApiDefinition> rows = QueryHelper.lambdaQuery(ApiDefinitionMapper.class, page, wp -> {
            wp.eq(ApiDefinition::getWorkspaceId, workspaceId);
        });
        return PageResult.of(rows, page);
    }

    @Override
    public int delete(Integer id) {
        return apiDefinitionMapper.deleteById(id);
    }
}
