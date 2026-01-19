package top.huzz.jaksho.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.helper.QueryHelper;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.api.service.ServiceService;
import top.huzz.jaksho.common.entity.BasicProperties;
import top.huzz.jaksho.common.entity.PageResult;
import top.huzz.jaksho.common.session.Session;
import top.huzz.jaksho.domain.entity.Service;
import top.huzz.jaksho.domain.mapper.ServiceMapper;
import top.huzz.resilix.core.RunHandlerManager;
import top.huzz.resilix.core.RunHandlerManagerHelper;

import java.util.List;

/**
 * 服务管理服务实现类
 *
 * @author mybatis-plus-generator
 * @since 1.0.2
 */
@DubboService
public class ServiceServiceImpl implements ServiceService {

    @Resource
    private ServiceMapper serviceMapper;

    @Override
    @Transactional
    public Integer create(CreateServiceRequest request) {
        RunHandlerManager manager = RunHandlerManagerHelper.build(CreatePhase.class);
        CreateRunContext<CreateServiceRequest, Service, BasicProperties> context = new CreateRunContext<>(request, Service::new);
        manager.start(context);
        if (!context.isSuccess()) {
            throw new RuntimeException(context.getException());
        }
        return context.getId();
    }

    @Override
    public PageResult<Service> pageQuery(ServiceService.PageQueryRequest request) {
        int workspaceId = Session.currentWorkspaceId();
        Page<Service> page = request.toPage();
        List<Service> rows = QueryHelper.lambdaQuery(ServiceMapper.class, page, wp -> {
            wp.eq(Service::getWorkspaceId, workspaceId);
            if (StringUtils.isNotBlank(request.getName())) {
                wp.like(Service::getName, request.getName());
            }
            if (request.getServiceCode() != null) {
                wp.eq(Service::getServiceCode, request.getServiceCode());
            }
            wp.orderByDesc(Service::getCreateTime);
        });
        return PageResult.of(rows, page);
    }

    @Override
    public int delete(Integer id) {
        return serviceMapper.deleteById(id);
    }

    @Override
    public Service detail(Integer id) {
        return serviceMapper.selectById(id);
    }

    @Override
    @Transactional
    public int update(Integer id, ServiceService.UpdateServiceRequest request) {
        int workspaceId = Session.currentWorkspaceId();
        
        // 检查服务是否存在
        Service service = serviceMapper.selectById(id);
        if (service == null || !service.getWorkspaceId().equals(workspaceId)) {
            throw new RuntimeException("服务不存在");
        }
        
        // 检查名称唯一性（排除当前记录）
        if (StringUtils.isNotBlank(request.getName())) {
            List<Service> existingServices = QueryHelper.lambdaQuery(ServiceMapper.class, wp -> wp.eq(Service::getWorkspaceId, workspaceId)
              .eq(Service::getName, request.getName())
              .ne(Service::getId, id));
            if (!existingServices.isEmpty()) {
                throw new RuntimeException("服务名称已存在");
            }
        }
        
        // 更新字段
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setRemark(request.getRemark());
        
        return serviceMapper.updateById(service);
    }
}

