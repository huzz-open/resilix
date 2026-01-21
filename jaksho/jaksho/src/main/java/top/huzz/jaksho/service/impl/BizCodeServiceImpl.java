package top.huzz.jaksho.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import top.huzz.jaksho.api.config.AppConfig;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.helper.QueryHelper;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.api.service.BizCodeService;
import top.huzz.jaksho.common.entity.BasicProperties;
import top.huzz.jaksho.common.entity.CombineResult;
import top.huzz.jaksho.common.entity.PageResult;
import top.huzz.jaksho.common.session.Session;
import top.huzz.jaksho.domain.entity.ApiBizCode;
import top.huzz.jaksho.domain.entity.BizCode;
import top.huzz.jaksho.domain.entity.Service;
import top.huzz.jaksho.domain.mapper.ApiBizCodeMapper;
import top.huzz.jaksho.domain.mapper.BizCodeMapper;
import top.huzz.jaksho.domain.mapper.ServiceMapper;
import top.huzz.resilix.core.RunHandlerManager;
import top.huzz.resilix.core.RunHandlerManagerHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 业务码管理服务实现类
 *
 * @author mybatis-plus-generator
 * @since 1.0.2
 */
@DubboService
public class BizCodeServiceImpl implements BizCodeService {

    @Resource
    private BizCodeMapper bizCodeMapper;

    @Resource
    private ServiceMapper serviceMapper;

    @Resource
    private ApiBizCodeMapper apiBizCodeMapper;

    @Resource
    private AppConfig appConfig;

    @Override
    @Transactional
    public Integer create(CreateBizCodeRequest request) {
        RunHandlerManager manager = RunHandlerManagerHelper.build(CreatePhase.class);
        CreateRunContext<CreateBizCodeRequest, BizCode, BasicProperties> context =
                new CreateRunContext<>(request, BizCode::new);

        // 使用 consumer 设置序号和业务码
        context.setPreCreatedObjectConsumer(bizCode -> {
            int workspaceId = Session.currentWorkspaceId();
            AppConfig.BizCodeConfig config = appConfig.getBizCode();

            // 验证服务存在性
            Service service = serviceMapper.selectById(request.getServiceId());
            if (service == null) {
                throw new RuntimeException("服务不存在");
            }
            if (!service.getWorkspaceId().equals(workspaceId)) {
                throw new RuntimeException("服务不属于当前工作空间");
            }

            // 处理序号
            Integer sequenceNumber;
            if ("auto".equals(config.getSequenceMode())) {
                sequenceNumber = getNextSequenceInternal(request.getServiceId(), workspaceId);
            } else {
                sequenceNumber = request.getSequenceNumber();
                if ("manual".equals(config.getSequenceMode())) {
                    if (sequenceNumber == null) {
                        throw new RuntimeException("手动模式下必须输入序号");
                    }
                    // 验证序号是否已存在
                    long existCount = QueryHelper.lambdaQuery(BizCodeMapper.class, wp -> wp.eq(BizCode::getServiceId, request.getServiceId())
                            .eq(BizCode::getSequenceNumber, sequenceNumber)
                            .eq(BizCode::getWorkspaceId, workspaceId)).size();
                    if (existCount > 0) {
                        throw new RuntimeException("该序号已存在");
                    }
                } else {
                    throw new RuntimeException("未知的序号生成模式: " + config.getSequenceMode());
                }
            }

            // 计算业务码
            int code = calculateCode(service.getServiceCode(), sequenceNumber, config);

            // 验证业务码是否已存在
            long existCodeCount = QueryHelper.lambdaQuery(BizCodeMapper.class, wp -> wp.eq(BizCode::getCode, code)
                    .eq(BizCode::getWorkspaceId, workspaceId)).size();
            if (existCodeCount > 0) {
                throw new RuntimeException("业务码已存在");
            }

            // 设置字段
            bizCode.setServiceId(request.getServiceId());
            bizCode.setSequenceNumber(sequenceNumber);
            bizCode.setCode(code);
        });

        manager.start(context);
        if (!context.isSuccess()) {
            throw new RuntimeException(context.getException());
        }
        return context.getId();
    }

    @Override
    public PageResult<CombineResult> pageQuery(PageQueryRequest request) {
        int workspaceId = Session.currentWorkspaceId();
        Page<CombineResult> page = request.toPage();
        BeanMap beanMap = new BeanMap(request);
        Map<Object, Object> p = new HashMap<>(beanMap);
        p.put("workspaceId", workspaceId);
        List<CombineResult> rows = bizCodeMapper.query(page, p);
        return PageResult.of(rows, page);
    }

    @Override
    @Transactional
    public int delete(Integer id) {
        return bizCodeMapper.deleteById(id);
    }

    @Override
    public CombineResult detail(Integer id) {
        int workspaceId = Session.currentWorkspaceId();
        Map<String, Object> p = Map.of("id", id, "workspaceId", workspaceId);
        return bizCodeMapper.query(null, p).stream().findFirst().orElse(null);
    }

    @Override
    @Transactional
    public int update(Integer id, UpdateBizCodeRequest request) {
        BizCode bizCode = bizCodeMapper.selectById(id);
        if (bizCode == null) {
            throw new RuntimeException("业务码不存在");
        }

        // 只允许修改描述和备注
        if (StringUtils.isNotBlank(request.getShortDesc())) {
            bizCode.setShortDesc(request.getShortDesc());
        }
        bizCode.setDetailDesc(request.getDetailDesc());
        bizCode.setRemark(request.getRemark());

        return bizCodeMapper.updateById(bizCode);
    }

    @Override
    public Integer getNextSequence(Integer serviceId) {
        int workspaceId = Session.currentWorkspaceId();

        // 验证服务是否存在
        Service service = serviceMapper.selectById(serviceId);
        if (service == null) {
            throw new RuntimeException("服务不存在");
        }
        if (!service.getWorkspaceId().equals(workspaceId)) {
            throw new RuntimeException("服务不属于当前工作空间");
        }

        return getNextSequenceInternal(serviceId, workspaceId);
    }

    @Override
    public BizCodeService.ConfigResponse getConfig() {
        AppConfig.BizCodeConfig config = appConfig.getBizCode();
        BizCodeService.ConfigResponse response = new BizCodeService.ConfigResponse();
        response.setServiceCodeLength(config.getServiceCodeLength());
        response.setSequenceLength(config.getSequenceLength());
        response.setSequenceMode(config.getSequenceMode());
        response.setI18nEnabled(config.isI18nEnabled());
        response.setDefaultLocale(config.getDefaultLocale());
        return response;
    }

    @Override
    public List<CombineResult> getByApiId(Integer apiId) {
        int workspaceId = Session.currentWorkspaceId();

        // 获取关联关系
        List<ApiBizCode> relations = QueryHelper.lambdaQuery(ApiBizCodeMapper.class, wp -> wp.eq(ApiBizCode::getApiId, apiId)
                .eq(ApiBizCode::getWorkspaceId, workspaceId)
                .orderByAsc(ApiBizCode::getSortOrder));

        if (relations.isEmpty()) {
            return List.of();
        }

        // 获取业务码列表（通过联表查询）
        List<Integer> bizCodeIds = relations.stream()
                .map(ApiBizCode::getBizCodeId)
                .collect(Collectors.toList());

        Map<String, Object> p = new HashMap<>();
        p.put("workspaceId", workspaceId);
        p.put("bizCodeIds", bizCodeIds);
        return bizCodeMapper.query(null, p);
    }

    @Override
    @Transactional
    public int bindToApi(BindBizCodeRequest request) {
        int workspaceId = Session.currentWorkspaceId();

        // 先删除已有的关联关系
        QueryHelper.lambdaQuery(ApiBizCodeMapper.class, wp -> wp.eq(ApiBizCode::getApiId, request.getApiId())
                .eq(ApiBizCode::getWorkspaceId, workspaceId)).forEach(relation -> apiBizCodeMapper.deleteById(relation.getId()));

        // 创建新的关联关系
        int sortOrder = 0;
        for (Integer bizCodeId : request.getBizCodeIds()) {
            ApiBizCode relation = new ApiBizCode();
            relation.setApiId(request.getApiId());
            relation.setBizCodeId(bizCodeId);
            relation.setSortOrder(sortOrder++);
            relation.setWorkspaceId(workspaceId);
            apiBizCodeMapper.insert(relation);
        }

        return request.getBizCodeIds().size();
    }

    @Override
    @Transactional
    public int unbindFromApi(Integer apiId, Integer bizCodeId) {
        int workspaceId = Session.currentWorkspaceId();

        List<ApiBizCode> relations = QueryHelper.lambdaQuery(ApiBizCodeMapper.class, wp -> wp.eq(ApiBizCode::getApiId, apiId)
                .eq(ApiBizCode::getBizCodeId, bizCodeId)
                .eq(ApiBizCode::getWorkspaceId, workspaceId));

        if (relations.isEmpty()) {
            return 0;
        }

        return apiBizCodeMapper.deleteById(relations.get(0).getId());
    }

    /**
     * 获取下一个可用序号（内部方法）
     */
    private Integer getNextSequenceInternal(Integer serviceId, int workspaceId) {
        List<BizCode> codes = QueryHelper.lambdaQuery(BizCodeMapper.class, wp -> wp.eq(BizCode::getServiceId, serviceId)
                .eq(BizCode::getWorkspaceId, workspaceId)
                .orderByDesc(BizCode::getSequenceNumber)
                .last("LIMIT 1"));

        if (codes.isEmpty()) {
            return 1; // 第一个序号从1开始
        }

        return codes.get(0).getSequenceNumber() + 1;
    }

    /**
     * 计算完整业务码
     *
     * @param serviceCode 服务码
     * @param sequence    序号
     * @param config      配置
     * @return 完整业务码
     */
    private int calculateCode(int serviceCode, int sequence, AppConfig.BizCodeConfig config) {
        // 业务码 = 服务码 * 10^序号位数 + 序号
        int sequenceLength = config.getSequenceLength();
        int multiplier = (int) Math.pow(10, sequenceLength);
        return serviceCode * multiplier + sequence;
    }

}

