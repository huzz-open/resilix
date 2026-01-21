package top.huzz.jaksho.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.helper.QueryHelper;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.api.service.BizFieldDomainService;
import top.huzz.jaksho.api.service.BizFieldTypeService;
import top.huzz.jaksho.common.constant.BasicFieldType;
import top.huzz.jaksho.common.constant.CollectionType;
import top.huzz.jaksho.common.entity.CombineResult;
import top.huzz.jaksho.common.entity.PageResult;
import top.huzz.jaksho.common.session.Session;
import top.huzz.jaksho.domain.entity.BizFieldType;
import top.huzz.jaksho.domain.entity.ObjectBizFieldTypeRef;
import top.huzz.jaksho.domain.mapper.BizFieldTypeMapper;
import top.huzz.jaksho.domain.mapper.ObjectBizFieldTypeRefMapper;
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

    @Resource
    private ObjectBizFieldTypeRefMapper objectBizFieldTypeRefMapper;

    @Resource
    private BizFieldDomainService bizFieldDomainService;

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

    @Override
    public int update(Integer id, UpdateBizFieldTypeRequest request) {
        BizFieldType bizFieldType = bizFieldTypeMapper.selectById(id);
        if (bizFieldType == null) {
            throw new IllegalStateException("BizFieldType not found for id: " + id);
        }
        if (request.getName() != null) {
            bizFieldType.setName(request.getName());
        }
        if (request.getDescription() != null) {
            bizFieldType.setDescription(request.getDescription());
        }
        return bizFieldTypeMapper.updateById(bizFieldType);
    }

    @Override
    @Transactional
    public int updateObjectRefs(Integer id, UpdateObjectRefsRequest request) {
        BizFieldType bizFieldType = bizFieldTypeMapper.selectById(id);
        if (bizFieldType == null) {
            throw new IllegalStateException("BizFieldType not found for id: " + id);
        }

        // 验证是 OBJECT 类型
        if (!BasicFieldType.OBJECT.equals(bizFieldType.getBasicFieldType())) {
            throw new IllegalStateException("Only OBJECT type can have object refs");
        }

        int workspaceId = Session.currentWorkspaceId();

        // 删除该字段类型的所有旧关联
        objectBizFieldTypeRefMapper.delete(
                Wrappers.lambdaQuery(ObjectBizFieldTypeRef.class)
                        .eq(ObjectBizFieldTypeRef::getBizFieldTypeId, id)
                        .eq(ObjectBizFieldTypeRef::getWorkspaceId, workspaceId)
        );

        // 插入新的关联
        if (CollectionUtils.isNotEmpty(request.getObjectBizFieldTypeRefDTOList())) {
            List<ObjectBizFieldTypeRef> refs = request.getObjectBizFieldTypeRefDTOList().stream()
                    .map(dto -> {
                        ObjectBizFieldTypeRef ref = new ObjectBizFieldTypeRef();
                        ref.setBizFieldTypeId(id);
                        ref.setBizFieldDomainId(dto.getBizFieldDomainId());
                        ref.setUlid(dto.getUlid());
                        ref.setParentUlid(dto.getParentUlid());
                        ref.setSortOrder(dto.getSortOrder());
                        ref.setWorkspaceId(workspaceId);
                        return ref;
                    })
                    .toList();

            // 批量插入
            refs.forEach(ref -> objectBizFieldTypeRefMapper.insert(ref));
        }

        return 1;
    }

    @Override
    public DetailResponse detail(Integer id) {
        BizFieldType bizFieldType = bizFieldTypeMapper.selectById(id);
        if (bizFieldType == null) {
            throw new IllegalStateException("BizFieldType not found for id: " + id);
        }
        DetailResponse response = new DetailResponse();
        BeanUtils.copyProperties(bizFieldType, response);

        // 如果是 OBJECT 类型，查询关联的字段列表
        if (BasicFieldType.OBJECT.equals(bizFieldType.getBasicFieldType())) {
            List<ObjectBizFieldTypeRef> objectBizFieldTypeRefs = objectBizFieldTypeRefMapper.selectList(
                    Wrappers.lambdaQuery(ObjectBizFieldTypeRef.class)
                            .eq(ObjectBizFieldTypeRef::getBizFieldTypeId, id)
                            .orderByAsc(ObjectBizFieldTypeRef::getSortOrder)
            );
            if (CollectionUtils.isNotEmpty(objectBizFieldTypeRefs)) {
                List<CombineResult> list = objectBizFieldTypeRefs.stream().map(ref -> {
                    Integer bizFieldDomainId = ref.getBizFieldDomainId();
                    CombineResult detail = bizFieldDomainService.detail(bizFieldDomainId);
                    detail.put("objectBizFieldTypeRef", ref);
                    return detail;
                }).toList();
                response.setObjectBizFieldTypeRefList(list);
            }
        }

        return response;
    }
}
