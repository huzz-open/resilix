package top.huzz.jaksho.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.dubbo.config.annotation.DubboService;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.helper.QueryHelper;
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
        String sql = """
                select * from sr_biz_field_domain bfd
                    inner join sr_biz_field_type bft on bfd.biz_field_type_id = bft.id and bfd.workspace_id = bft.workspace_id
                    inner join sr_biz_field bf on bfd.biz_field_id = bf.id and bfd.workspace_id = bf.workspace_id
                where bfd.workspace_id = #{p.workspaceId}
                """;
        Page<CombineResult> page = request.toPage();
        List<CombineResult> rows = QueryHelper.query(sql, page, Map.of("workspaceId", workspaceId));
        return PageResult.of(rows, page);
    }

    @Override
    public int delete(Integer id) {
        return bizFieldDomainMapper.deleteById(id);
    }
}
