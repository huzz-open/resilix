package top.huzz.jaksho.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.api.service.BizFieldDomainService;
import top.huzz.jaksho.common.entity.BasicProperties;
import top.huzz.jaksho.common.entity.CombineResult;
import top.huzz.jaksho.common.entity.PageResult;
import top.huzz.jaksho.common.session.Session;
import top.huzz.jaksho.domain.entity.BizFieldDomain;
import top.huzz.jaksho.domain.mapper.BizFieldDomainMapper;
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
public class BizFieldDomainServiceImpl implements BizFieldDomainService {

    @Resource
    private BizFieldDomainMapper bizFieldDomainMapper;

    @Override
    @Transactional
    public Integer create(CreateBizFieldDomainRequest request) {
        RunHandlerManager manager = RunHandlerManagerHelper.build(CreatePhase.class);
        CreateRunContext<CreateBizFieldDomainRequest, BizFieldDomain, BasicProperties> context = new CreateRunContext<>(request, BizFieldDomain::new);
        manager.start(context);
        if (!context.isSuccess()) {
            throw new RuntimeException(context.getException());
        }
        return context.getId();
    }

    @Override
    public PageResult<CombineResult> pageQuery(BizFieldDomainService.PageQueryRequest request) {
        int workspaceId = Session.currentWorkspaceId();
        Page<CombineResult> page = request.toPage();
        Map<String, Object> p = new HashMap<>();
        p.put("bizFieldId", request.getBizFieldId());
        p.put("excludeDefaultFieldDomain", request.getExcludeDefaultFieldDomain());
        p.put("workspaceId", workspaceId);
        List<CombineResult> rows = bizFieldDomainMapper.query(page, p);
        return PageResult.of(rows, page);
    }

    @Override
    public int delete(Integer id) {
        return bizFieldDomainMapper.deleteById(id);
    }
}
