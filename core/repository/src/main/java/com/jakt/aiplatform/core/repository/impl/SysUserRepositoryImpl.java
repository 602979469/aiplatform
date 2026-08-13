package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysUserDO;
import com.jakt.aiplatform.common.dal.mapper.SysUserMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysUser;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.param.SysUserQueryParam;
import com.jakt.aiplatform.core.model.result.SysUserDetailResult;
import com.jakt.aiplatform.core.model.result.SysUserListResult;
import com.jakt.aiplatform.core.repository.SysUserRepository;
import com.jakt.aiplatform.core.repository.convertor.SysUserConvertor;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysUserRepositoryImpl implements SysUserRepository {

    /** 用户 Mapper。 */
    private final SysUserMapper sysUserMapper;

    public SysUserRepositoryImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    /** 按条件取单条（调用 detail 富结果集）：多条抛 RESULT_NOT_UNIQUE，空返回 null。 */
    private SysUser findOne(SysUserQueryParam query) {
        List<SysUserDetailResult> rows = sysUserMapper.selectUserDetail(query);
        if (CollUtil.isEmpty(rows)) {
            return null;
        }
        AiPlatformInvoker.throwErrWhenTrue(rows.size() > 1, ErrorCodeEnum.RESULT_NOT_UNIQUE, "查询结果不唯一");
        return SysUserConvertor.toModel(rows.get(0));
    }

    @Override
    public SysUser selectUserByLoginName(String loginName) {
        SysUserQueryParam query = new SysUserQueryParam();
        query.setLoginName(loginName);
        return findOne(query);
    }

    @Override
    public SysUser selectUserByPhoneNumber(String phonenumber) {
        SysUserQueryParam query = new SysUserQueryParam();
        query.setPhonenumber(phonenumber);
        return findOne(query);
    }

    @Override
    public SysUser selectUserByEmail(String email) {
        SysUserQueryParam query = new SysUserQueryParam();
        query.setEmail(email);
        return findOne(query);
    }

    @Override
    public List<SysUser> selectUserList(SysUser user) {
        List<SysUserListResult> list = sysUserMapper.selectUserList(SysUserConvertor.toQueryParam(user));
        return ListUtil.convert(list, SysUserConvertor::toModel);
    }

    @Override
    public List<SysUser> selectAllocatedList(SysUser user) {
        List<SysUserListResult> list = sysUserMapper.selectAllocatedList(SysUserConvertor.toQueryParam(user));
        return ListUtil.convert(list, SysUserConvertor::toModel);
    }

    @Override
    public List<SysUser> selectUnallocatedList(SysUser user) {
        List<SysUserListResult> list = sysUserMapper.selectUnallocatedList(SysUserConvertor.toQueryParam(user));
        return ListUtil.convert(list, SysUserConvertor::toModel);
    }

    @Override
    public boolean checkLoginNameUnique(SysUser user) {
        SysUserQueryParam query = new SysUserQueryParam();
        query.setLoginName(user.getLoginName());
        List<SysUserDO> list = sysUserMapper.selectList(query);
        if (CollUtil.isEmpty(list)) {
            return true;
        }
        if (list.size() > 1) {
            return false;
        }
        return ObjectUtil.equal(list.get(0).getUserId(), user.getUserId());
    }

    @Override
    public boolean checkPhoneUnique(SysUser user) {
        SysUserQueryParam query = new SysUserQueryParam();
        query.setPhonenumber(user.getPhonenumber());
        List<SysUserDO> list = sysUserMapper.selectList(query);
        if (CollUtil.isEmpty(list)) {
            return true;
        }
        if (list.size() > 1) {
            return false;
        }
        return ObjectUtil.equal(list.get(0).getUserId(), user.getUserId());
    }

    @Override
    public boolean checkEmailUnique(SysUser user) {
        SysUserQueryParam query = new SysUserQueryParam();
        query.setEmail(user.getEmail());
        List<SysUserDO> list = sysUserMapper.selectList(query);
        if (CollUtil.isEmpty(list)) {
            return true;
        }
        if (list.size() > 1) {
            return false;
        }
        return ObjectUtil.equal(list.get(0).getUserId(), user.getUserId());
    }

    @Override
    public SysUser selectUserById(Long userId) {
        SysUserQueryParam query = new SysUserQueryParam();
        query.setUserId(userId);
        return findOne(query);
    }

    @Override
    public int deleteUserById(Long userId) {
        return sysUserMapper.deleteById(userId);
    }

    @Override
    public int deleteUserByIds(String ids) {
        return sysUserMapper.deleteByIds(Convert.toLongArray(ids));
    }

    @Override
    public int updateUserAvatar(Long userId, String avatar) {
        SysUserDO data = new SysUserDO();
        data.setUserId(userId);
        data.setAvatar(avatar);
        return sysUserMapper.updateByCondition(data);
    }

    @Override
    public int resetUserPwd(SysUser user) {
        SysUserDO data = new SysUserDO();
        data.setUserId(user.getUserId());
        data.setPassword(user.getPassword());
        data.setSalt(user.getSalt());
        data.setPwdUpdateDate(user.getPwdUpdateDate());
        return sysUserMapper.updateByCondition(data);
    }

    @Override
    public int updateUserStatus(SysUser user) {
        SysUserDO data = new SysUserDO();
        data.setUserId(user.getUserId());
        data.setStatus(user.getStatus() == null ? null : user.getStatus().getCode());
        return sysUserMapper.updateByCondition(data);
    }

    @Override
    public int updateLoginInfo(SysUser user) {
        SysUserDO data = new SysUserDO();
        data.setUserId(user.getUserId());
        data.setLoginIp(user.getLoginIp());
        data.setLoginDate(user.getLoginDate());
        return sysUserMapper.updateByCondition(data);
    }

    @Override
    public int updateUser(SysUser user) {
        return sysUserMapper.update(SysUserConvertor.toDO(user));
    }

    @Override
    public int insertUser(SysUser user) {
        return sysUserMapper.insert(SysUserConvertor.toDO(user));
    }
}
