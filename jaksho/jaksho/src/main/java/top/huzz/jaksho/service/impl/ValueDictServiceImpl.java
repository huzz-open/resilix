package top.huzz.jaksho.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.helper.QueryHelper;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.api.service.ValueDictService;
import top.huzz.jaksho.common.entity.BasicProperties;
import top.huzz.jaksho.common.entity.PageResult;
import top.huzz.jaksho.common.session.Session;
import top.huzz.jaksho.domain.entity.ValueDict;
import top.huzz.jaksho.domain.entity.ValueDictItem;
import top.huzz.jaksho.domain.mapper.ValueDictItemMapper;
import top.huzz.jaksho.domain.mapper.ValueDictMapper;
import top.huzz.resilix.core.RunHandlerManager;
import top.huzz.resilix.core.RunHandlerManagerHelper;

import java.util.List;

/**
 * 值字典服务实现
 *
 * @author huzz
 * @since 1.0.2
 */
@DubboService
public class ValueDictServiceImpl implements ValueDictService {

    @Resource
    private ValueDictMapper valueDictMapper;

    @Resource
    private ValueDictItemMapper valueDictItemMapper;

    @Override
    @Transactional
    public Integer create(CreateValueDictRequest request) {
        // 创建值字典
        RunHandlerManager manager = RunHandlerManagerHelper.build(CreatePhase.class);
        CreateRunContext<CreateValueDictRequest, ValueDict, BasicProperties> context =
                new CreateRunContext<>(request, ValueDict::new);
        manager.start(context);

        if (!context.isSuccess()) {
            throw new RuntimeException(context.getException());
        }

        Integer dictId = context.getId();
        int workspaceId = Session.currentWorkspaceId();

        // 创建字典项
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            List<ValueDictItem> items = request.getItems().stream()
                    .map(dto -> {
                        ValueDictItem item = new ValueDictItem();
                        item.setValueDictId(dictId);
                        item.setName(dto.getName());
                        item.setRawValue(dto.getRawValue());
                        item.setDescription(dto.getDescription());
                        item.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
                        item.setRemark(dto.getRemark());
                        item.setWorkspaceId(workspaceId);
                        return item;
                    })
                    .toList();

            items.forEach(valueDictItemMapper::insert);
        }

        return dictId;
    }

    @Override
    public PageResult<ValueDict> pageQuery(PageQueryRequest request) {
        int workspaceId = Session.currentWorkspaceId();
        Page<ValueDict> page = request.toPage();

        List<ValueDict> rows = QueryHelper.lambdaQuery(ValueDictMapper.class, page, wrapper -> {
            wrapper.eq(ValueDict::getWorkspaceId, workspaceId);
            if (request.getType() != null) {
                wrapper.eq(ValueDict::getType, request.getType());
            }
        });

        return PageResult.of(rows, page);
    }

    @Override
    public DetailResponse detail(Integer id) {
        ValueDict dict = valueDictMapper.selectById(id);
        if (dict == null) {
            throw new RuntimeException("值字典不存在");
        }

        DetailResponse response = new DetailResponse();
        response.setId(dict.getId());
        response.setName(dict.getName());
        response.setDescription(dict.getDescription());
        response.setType(dict.getType());
        response.setRemark(dict.getRemark());
        response.setWorkspaceId(dict.getWorkspaceId());
        response.setCreateTime(dict.getCreateTime());
        response.setUpdateTime(dict.getUpdateTime());

        // 查询字典项
        LambdaQueryWrapper<ValueDictItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ValueDictItem::getValueDictId, id)
                .orderByAsc(ValueDictItem::getSortOrder);
        List<ValueDictItem> items = valueDictItemMapper.selectList(queryWrapper);
        response.setItems(items);

        return response;
    }

    @Override
    @Transactional
    public int update(Integer id, UpdateValueDictRequest request) {
        ValueDict dict = valueDictMapper.selectById(id);
        if (dict == null) {
            throw new RuntimeException("值字典不存在");
        }

        // 更新字典基本信息
        if (request.getDescription() != null) {
            dict.setDescription(request.getDescription());
        }
        if (request.getRemark() != null) {
            dict.setRemark(request.getRemark());
        }
        valueDictMapper.updateById(dict);

        // 更新字典项：先删除所有旧的，再插入新的
        LambdaQueryWrapper<ValueDictItem> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(ValueDictItem::getValueDictId, id);
        valueDictItemMapper.delete(deleteWrapper);

        // 插入新的字典项
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            int workspaceId = Session.currentWorkspaceId();
            List<ValueDictItem> items = request.getItems().stream()
                    .map(dto -> {
                        ValueDictItem item = new ValueDictItem();
                        item.setValueDictId(id);
                        item.setName(dto.getName());
                        item.setRawValue(dto.getRawValue());
                        item.setDescription(dto.getDescription());
                        item.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
                        item.setRemark(dto.getRemark());
                        item.setWorkspaceId(workspaceId);
                        return item;
                    })
                    .toList();

            items.forEach(valueDictItemMapper::insert);
        }

        return 1;
    }

    @Override
    @Transactional
    public int delete(Integer id) {
        // 删除字典（字典项会被数据库外键级联删除）
        return valueDictMapper.deleteById(id);
    }
}


