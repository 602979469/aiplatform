package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.SysDept;
import com.jakt.aiplatform.core.model.param.SysDeptQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 部门管理类接口定义
 * 
 */
public interface SysDeptManager {

    /**
     * 创建部门
     *
     * @param sysDept 部门
     * @return 创建成功后的部门
     */
    SysDept createSysDept(SysDept sysDept);

    /**
     * 按 ID 查询部门
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
    PageResult<SysDept> pageSysDepts(SysDeptQueryParam query);

    /**
     * 列表查询部门
     *
     * @param query 查询参数
     * @return 部门列表
     */
    List<SysDept> listSysDepts(SysDeptQueryParam query);

    /**
     * 更新部门（全量）。
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
     * 删除部门。
     *
     * @param id 部门 ID
     */
    void deleteSysDept(Long id);
}
