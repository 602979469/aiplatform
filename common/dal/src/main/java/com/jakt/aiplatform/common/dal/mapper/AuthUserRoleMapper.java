package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.AuthUserRoleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联表 Mapper。
 */
@Mapper
public interface AuthUserRoleMapper {

    /**
     * 批量绑定用户角色。
     *
     * @param list 关联列表
     * @return 受影响行数
     */
    int batchInsert(@Param("list") List<AuthUserRoleDO> list);

    /**
     * 删除用户全部角色。
     *
     * @param userId 用户ID
     * @return 受影响行数
     */
    int deleteByUserId(Long userId);

    /**
     * 删除角色下全部用户。
     *
     * @param roleId 角色ID
     * @return 受影响行数
     */
    int deleteByRoleId(Long roleId);

    /**
     * 查询用户角色ID列表。
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByUserId(Long userId);
}
