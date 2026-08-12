package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.SysDept;
import com.jakt.aiplatform.core.model.param.SysDeptQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 部门领域服务
 *
 * 实现类为 SysDeptServiceImpl（core.service.impl 包）。
 */
public interface SysDeptService {

    /**
     * 创建部门
     *
     * @param sysDept 部门
     * @return 创建后的部门（主键已回填）
     */
    SysDept createSysDept(SysDept sysDept);

    /**
     * 更新部门（全量）
     *
     * @param sysDept 部门（含主键）
     */
    void updateSysDept(SysDept sysDept);

    /**
     * 按条件更新部门（只更新传入的非空字段）。
     *
     * @param sysDept 部门（至少含主键）
     */
    void updateByCondition(SysDept sysDept);

    /**
     * 删除部门
     *
     * @param id 部门 ID
     */
    void deleteSysDept(Long id);

    /**
     * 按 ID 获取部门
     *
     * @param id 部门 ID
     * @return 部门
     */
    SysDept getSysDept(Long id);

    /**
     * 分页查询部门
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysDept> findPage(SysDeptQueryParam query);

    /**
     * 列表查询部门
     *
     * @param query 查询参数
     * @return 部门列表
     */
    List<SysDept> findList(SysDeptQueryParam query);
}
