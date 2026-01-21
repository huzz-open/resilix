package top.huzz.jaksho.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.dubbo.config.annotation.DubboService;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.api.service.BizDomainService;
import top.huzz.jaksho.common.entity.BasicProperties;
import top.huzz.jaksho.common.entity.PageResult;
import top.huzz.jaksho.common.session.Session;
import top.huzz.jaksho.domain.entity.BizDomain;
import top.huzz.jaksho.domain.mapper.BizDomainMapper;
import top.huzz.resilix.core.RunHandlerManager;
import top.huzz.resilix.core.RunHandlerManagerHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenji
 * @since 1.0.2
 */
@DubboService
public class BizDomainServiceImpl implements BizDomainService {

    @Resource
    private BizDomainMapper bizDomainMapper;

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

    @Override
    public PageResult<BizDomain> pageQuery(BizDomainService.PageQueryRequest request) {
        int workspaceId = Session.currentWorkspaceId();
        Page<BizDomain> page = request.toPage();
        
        Map<String, Object> params = new HashMap<>();
        params.put("workspaceId", workspaceId);
        // 如果传入了 bizFieldId，则在 SQL 中会通过 NOT EXISTS 排除已分配的领域
        if (request.getBizFieldId() != null) {
            params.put("bizFieldId", request.getBizFieldId());
        }
        
        List<BizDomain> rows = bizDomainMapper.pageQuery(page, params);
        return PageResult.of(rows, page);
    }

    @Override
    public int delete(Integer id) {
        return bizDomainMapper.deleteById(id);
    }
}
