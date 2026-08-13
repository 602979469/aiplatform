package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysUserDO;
import com.jakt.aiplatform.core.model.domain.SysDept;
import com.jakt.aiplatform.core.model.domain.SysRole;
import com.jakt.aiplatform.core.model.domain.SysUser;
import com.jakt.aiplatform.core.model.enums.DataScopeEnum;
import com.jakt.aiplatform.core.model.enums.DeptStatusEnum;
import com.jakt.aiplatform.core.model.enums.RoleStatusEnum;
import com.jakt.aiplatform.core.model.enums.UserTypeEnum;
import com.jakt.aiplatform.core.model.enums.SexEnum;
import com.jakt.aiplatform.core.model.enums.UserStatus;
import com.jakt.aiplatform.core.model.param.SysUserQueryParam;
import com.jakt.aiplatform.core.model.result.SysUserDetailResult;
import com.jakt.aiplatform.core.model.result.SysUserListResult;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ObjectUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 用户 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysUserConvertor {

    private SysUserConvertor() {
    }

    /**
     * 领域模型 → 查询参数（枚举转 code，显式赋值）。
     *
     * @param user 用户领域模型
     * @return 用户查询参数
     */
    public static SysUserQueryParam toQueryParam(SysUser user) {
        SysUserQueryParam query = new SysUserQueryParam();
        query.setUserId(user.getUserId());
        query.setDeptId(user.getDeptId());
        query.setRoleId(user.getRoleId());
        query.setLoginName(user.getLoginName());
        query.setUserName(user.getUserName());
        query.setUserType(user.getUserType() == null ? null : user.getUserType().getCode());
        query.setEmail(user.getEmail());
        query.setPhonenumber(user.getPhonenumber());
        query.setSex(user.getSex() == null ? null : user.getSex().getCode());
        query.setAvatar(user.getAvatar());
        query.setPassword(user.getPassword());
        query.setSalt(user.getSalt());
        query.setStatus(user.getStatus() == null ? null : user.getStatus().getCode());
        query.setLoginIp(user.getLoginIp());
        query.setLoginDate(user.getLoginDate());
        query.setPwdUpdateDate(user.getPwdUpdateDate());
        query.setRemark(user.getRemark());
        return query;
    }

    /**
     * 用户详情投影 → 领域模型（组装 dept + 本行角色）。
     *
     * @param row 用户详情投影（一行 = 用户 × 一个角色）
     * @return 用户领域模型
     */
    public static SysUser toModel(SysUserDetailResult row) {
        if (row == null) {
            return null;
        }
        SysUser target = new SysUser();
        target.setUserId(row.getUserId());
        target.setDeptId(row.getDeptId());
        target.setLoginName(row.getLoginName());
        target.setUserName(row.getUserName());
        target.setUserType(StrUtil.isBlank(row.getUserType()) ? null : UserTypeEnum.fromCode(row.getUserType()));
        target.setEmail(row.getEmail());
        target.setPhonenumber(row.getPhonenumber());
        target.setSex(StrUtil.isBlank(row.getSex()) ? null : SexEnum.fromCode(row.getSex()));
        target.setAvatar(row.getAvatar());
        target.setPassword(row.getPassword());
        target.setSalt(row.getSalt());
        target.setStatus(StrUtil.isBlank(row.getStatus()) ? null : UserStatus.fromCode(row.getStatus()));
        target.setLoginIp(row.getLoginIp());
        target.setLoginDate(row.getLoginDate());
        target.setPwdUpdateDate(row.getPwdUpdateDate());
        target.setRemark(row.getRemark());
        target.setCreateBy(row.getCreateBy());
        target.setCreateTime(row.getCreateTime());
        target.setUpdateBy(row.getUpdateBy());
        target.setUpdateTime(row.getUpdateTime());
        target.setDelFlag(row.getDelFlag());
        // 组装部门
        SysDept dept = new SysDept();
        dept.setDeptId(row.getDeptId());
        dept.setParentId(row.getParentId());
        dept.setAncestors(row.getAncestors());
        dept.setDeptName(row.getDeptName());
        dept.setOrderNum(row.getOrderNum());
        dept.setLeader(row.getLeader());
        dept.setStatus(StrUtil.isBlank(row.getDeptStatus()) ? null : DeptStatusEnum.fromCode(row.getDeptStatus()));
        target.setDept(dept);
        // 本行角色（单角色行；多角色由 findOne 按 rows.size() 判定）
        List<SysRole> roles = new ArrayList<>();
        if (row.getRoleId() != null) {
            SysRole role = new SysRole();
            role.setRoleId(row.getRoleId());
            role.setRoleName(row.getRoleName());
            role.setRoleKey(row.getRoleKey());
            role.setRoleSort(row.getRoleSort());
            role.setDataScope(StrUtil.isBlank(row.getDataScope()) ? null : DataScopeEnum.fromCode(row.getDataScope()));
            role.setStatus(StrUtil.isBlank(row.getRoleStatus()) ? null : RoleStatusEnum.fromCode(row.getRoleStatus()));
            roles.add(role);
        }
        target.setRoles(roles);
        return target;
    }

    /**
     * 用户投影结果 → 领域模型（组装 dept）。
     *
     * @param source 用户投影结果
     * @return 用户领域模型
     */
    public static SysUser toModel(SysUserListResult source) {
        if (source == null) {
            return null;
        }
        SysUser target = new SysUser();
        target.setUserId(source.getUserId());
        target.setDeptId(source.getDeptId());
        target.setLoginName(source.getLoginName());
        target.setUserName(source.getUserName());
        target.setUserType(StrUtil.isBlank(source.getUserType()) ? null : UserTypeEnum.fromCode(source.getUserType()));
        target.setEmail(source.getEmail());
        target.setPhonenumber(source.getPhonenumber());
        target.setSex(StrUtil.isBlank(source.getSex()) ? null : SexEnum.fromCode(source.getSex()));
        target.setAvatar(source.getAvatar());
        target.setStatus(StrUtil.isBlank(source.getStatus()) ? null : UserStatus.fromCode(source.getStatus()));
        target.setLoginIp(source.getLoginIp());
        target.setLoginDate(source.getLoginDate());
        target.setPwdUpdateDate(source.getPwdUpdateDate());
        target.setRemark(source.getRemark());
        target.setCreateBy(source.getCreateBy());
        target.setCreateTime(source.getCreateTime());
        target.setDelFlag(source.getDelFlag());
        // 组装部门
        SysDept dept = new SysDept();
        dept.setDeptId(source.getDeptId());
        dept.setDeptName(source.getDeptName());
        dept.setLeader(source.getLeader());
        target.setDept(dept);
        target.setRoles(new ArrayList<>());
        return target;
    }

    /**
     * DO → 领域模型。
     *
     * @param sysUserDO 用户数据对象；为空返回 null
     * @return 用户领域模型
     */
    public static SysUser toModel(SysUserDO source) {
        if (source == null) {
            return null;
        }
        SysUser target = new SysUser();
        target.setUserId(source.getUserId());
        target.setDeptId(source.getDeptId());
        target.setLoginName(source.getLoginName());
        target.setUserName(source.getUserName());
        target.setUserType(UserTypeEnum.fromCode(source.getUserType()));
        target.setEmail(source.getEmail());
        target.setPhonenumber(source.getPhonenumber());
        target.setSex(SexEnum.fromCode(source.getSex()));
        target.setAvatar(source.getAvatar());
        target.setPassword(source.getPassword());
        target.setSalt(source.getSalt());
        target.setStatus(UserStatus.fromCode(source.getStatus()));
        target.setLoginIp(source.getLoginIp());
        target.setLoginDate(source.getLoginDate());
        target.setPwdUpdateDate(source.getPwdUpdateDate());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysUser 用户领域模型
     * @return 用户数据对象
     */
    public static SysUserDO toDO(SysUser source) {
        SysUserDO target = new SysUserDO();
        target.setUserId(source.getUserId());
        target.setDeptId(source.getDeptId());
        target.setLoginName(source.getLoginName());
        target.setUserName(source.getUserName());
        target.setUserType(ObjectUtil.isNull(source.getUserType()) ? null : source.getUserType().getCode());
        target.setEmail(source.getEmail());
        target.setPhonenumber(source.getPhonenumber());
        target.setSex(ObjectUtil.isNull(source.getSex()) ? null : source.getSex().getCode());
        target.setAvatar(source.getAvatar());
        target.setPassword(source.getPassword());
        target.setSalt(source.getSalt());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setLoginIp(source.getLoginIp());
        target.setLoginDate(source.getLoginDate());
        target.setPwdUpdateDate(source.getPwdUpdateDate());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
