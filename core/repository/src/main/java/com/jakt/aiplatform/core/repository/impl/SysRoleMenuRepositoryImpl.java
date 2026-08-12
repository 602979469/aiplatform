package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysRoleMenuDO;
import com.jakt.aiplatform.common.dal.mapper.SysRoleMenuMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysRoleMenu;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysRoleMenuQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysRoleMenuRepository;
import com.jakt.aiplatform.core.repository.convertor.SysRoleMenuConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色菜单关联仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysRoleMenuRepositoryImpl implements SysRoleMenuRepository {

    /** 角色菜单关联 Mapper。 */
    private final SysRoleMenuMapper sysRoleMenuMapper;

    public SysRoleMenuRepositoryImpl(SysRoleMenuMapper sysRoleMenuMapper) {
        this.sysRoleMenuMapper = sysRoleMenuMapper;
    }

    @Override
    public SysRoleMenu findById(Long id) {
        return SysRoleMenuConvertor.toModel(sysRoleMenuMapper.selectById(id));
    }

    @Override
    public List<SysRoleMenu> findList(SysRoleMenuQueryParam query) {
        return sysRoleMenuMapper.selectList(query).stream().map(SysRoleMenuConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysRoleMenu> findPage(SysRoleMenuQueryParam query) {
        List<SysRoleMenuDO> doList = sysRoleMenuMapper.selectPage(query);
        long total = sysRoleMenuMapper.countByQuery(query);
        List<SysRoleMenu> list = doList.stream().map(SysRoleMenuConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysRoleMenu insert(SysRoleMenu sysRoleMenu) {
        SysRoleMenuDO sysRoleMenuDO = SysRoleMenuConvertor.toDO(sysRoleMenu);
        sysRoleMenuMapper.insert(sysRoleMenuDO);
        return SysRoleMenuConvertor.toModel(sysRoleMenuDO);
    }

    @Override
    public void update(SysRoleMenu sysRoleMenu) {
        SysRoleMenuDO sysRoleMenuDO = SysRoleMenuConvertor.toDO(sysRoleMenu);
        int affected = sysRoleMenuMapper.update(sysRoleMenuDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysRoleMenuRepository.update id={} 影响行数={}", sysRoleMenu.getId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysRoleMenu sysRoleMenu) {
        int affected = sysRoleMenuMapper.updateByCondition(SysRoleMenuConvertor.toDO(sysRoleMenu));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysRoleMenuRepository.updateByCondition id={} 影响行数={}", sysRoleMenu.getId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysRoleMenuMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysRoleMenuRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
