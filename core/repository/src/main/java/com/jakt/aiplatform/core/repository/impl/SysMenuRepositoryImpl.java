package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysMenuDO;
import com.jakt.aiplatform.common.dal.mapper.SysMenuMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysMenu;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysMenuQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysMenuRepository;
import com.jakt.aiplatform.core.repository.convertor.SysMenuConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜单仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysMenuRepositoryImpl implements SysMenuRepository {

    /** 菜单 Mapper。 */
    private final SysMenuMapper sysMenuMapper;

    public SysMenuRepositoryImpl(SysMenuMapper sysMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
    }

    @Override
    public SysMenu findById(Long id) {
        return SysMenuConvertor.toModel(sysMenuMapper.selectById(id));
    }

    @Override
    public List<SysMenu> findList(SysMenuQueryParam query) {
        return sysMenuMapper.selectList(query).stream().map(SysMenuConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysMenu> findPage(SysMenuQueryParam query) {
        List<SysMenuDO> doList = sysMenuMapper.selectPage(query);
        long total = sysMenuMapper.countByQuery(query);
        List<SysMenu> list = doList.stream().map(SysMenuConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysMenu insert(SysMenu sysMenu) {
        SysMenuDO sysMenuDO = SysMenuConvertor.toDO(sysMenu);
        sysMenuMapper.insert(sysMenuDO);
        return SysMenuConvertor.toModel(sysMenuDO);
    }

    @Override
    public void update(SysMenu sysMenu) {
        SysMenuDO sysMenuDO = SysMenuConvertor.toDO(sysMenu);
        int affected = sysMenuMapper.update(sysMenuDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysMenuRepository.update menuId={} 影响行数={}", sysMenu.getMenuId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysMenu sysMenu) {
        int affected = sysMenuMapper.updateByCondition(SysMenuConvertor.toDO(sysMenu));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysMenuRepository.updateByCondition menuId={} 影响行数={}", sysMenu.getMenuId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysMenuMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysMenuRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
