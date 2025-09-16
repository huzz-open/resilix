package top.huzz.jaksho.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.helper.QueryHelper;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.api.service.BizFieldService;
import top.huzz.jaksho.common.able.Saver;
import top.huzz.jaksho.common.able.SaverBuilder;
import top.huzz.jaksho.common.entity.BasicProperties;
import top.huzz.jaksho.common.entity.CombineResult;
import top.huzz.jaksho.common.entity.PageResult;
import top.huzz.jaksho.common.session.Session;
import top.huzz.jaksho.domain.entity.BizField;
import top.huzz.jaksho.domain.entity.BizFieldDomain;
import top.huzz.jaksho.domain.mapper.BizFieldMapper;
import top.huzz.resilix.core.RunHandlerManager;
import top.huzz.resilix.core.RunHandlerManagerHelper;

import java.util.List;
import java.util.Map;

/**
 * @author chenji
 * @since 1.0.2
 */
@DubboService
public class BizFieldServiceImpl implements BizFieldService {

    @Resource
    private BizFieldMapper bizFieldMapper;

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

    @Override
    public PageResult<CombineResult> pageQuery(BizFieldService.PageQueryRequest request) {
        int workspaceId = Session.currentWorkspaceId();
        String sql = """
                SELECT bf.*, bft.* FROM sr_biz_field bf INNER JOIN sr_biz_field_type bft ON bf.biz_field_type_id = bft.id and bf.workspace_id = bft.workspace_id
                where bf.workspace_id = #{p.workspaceId}
                """;
        Page<CombineResult> page = request.toPage();
        List<CombineResult> rows = QueryHelper.query(sql, page, Map.of("workspaceId", workspaceId));
        return PageResult.of(rows, page);
    }


    @Override
    public int delete(Integer id) {
        return bizFieldMapper.deleteById(id);
    }
}
