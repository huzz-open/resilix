package top.huzz.jaksho.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.dubbo.config.annotation.DubboService;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.helper.QueryHelper;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.api.service.BizFieldTypeService;
import top.huzz.jaksho.common.constant.BasicFieldType;
import top.huzz.jaksho.common.constant.CollectionType;
import top.huzz.jaksho.common.entity.PageResult;
import top.huzz.jaksho.common.session.Session;
import top.huzz.jaksho.domain.entity.BizFieldType;
import top.huzz.jaksho.domain.entity.ObjectBizFieldTypeRef;
import top.huzz.jaksho.domain.mapper.BizFieldTypeMapper;
import top.huzz.resilix.core.RunHandlerManager;
import top.huzz.resilix.core.RunHandlerManagerHelper;

import java.util.List;

/**
 * @author chenji
 * @since 1.0.2
 */
@DubboService
public class BizFieldTypeServiceImpl implements BizFieldTypeService {

    @Resource
    private BizFieldTypeMapper bizFieldTypeMapper;

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

    @Override
    public PageResult<BizFieldType> pageQuery(PageQueryRequest request) {
        int workspaceId = Session.currentWorkspaceId();
        Page<BizFieldType> page = request.toPage();
        List<BizFieldType> rows = QueryHelper.lambdaQuery(BizFieldTypeMapper.class, page, wp -> {
            wp.eq(BizFieldType::getWorkspaceId, workspaceId);
            BasicFieldType basicFieldType = request.getBasicFieldType();
            if (basicFieldType != null) {
                wp.eq(BizFieldType::getBasicFieldType, basicFieldType);
            }
            CollectionType collectionType = request.getCollectionType();
            if (collectionType != null) {
                wp.eq(BizFieldType::getCollectionType, collectionType);
            }
        });
        return PageResult.of(rows, page);
    }

    @Override
    public int delete(Integer id) {
        return bizFieldTypeMapper.deleteById(id);
    }
}
