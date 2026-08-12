package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysDept;
import com.jakt.aiplatform.core.model.param.SysDeptQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysDeptRepository;
import com.jakt.aiplatform.core.service.SysDeptService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门领域服务实现：承载部门相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysDeptServiceImpl implements SysDeptService {

    /** 部门仓储。 */
    private final SysDeptRepository sysDeptRepository;

    public SysDeptServiceImpl(SysDeptRepository sysDeptRepository) {
        this.sysDeptRepository = sysDeptRepository;
    }

    @Override
    public SysDept createSysDept(SysDept sysDept) {
        return sysDeptRepository.insert(sysDept);
    }

    @Override
    public void updateSysDept(SysDept sysDept) {
        sysDeptRepository.update(sysDept);
    }

    @Override
    public void updateByCondition(SysDept sysDept) {
        sysDeptRepository.updateByCondition(sysDept);
    }

    @Override
    public void deleteSysDept(Long id) {
        sysDeptRepository.deleteById(id);
    }

    @Override
    public SysDept getSysDept(Long id) {
        return sysDeptRepository.findById(id);
    }

    @Override
    public PageResult<SysDept> findPage(SysDeptQueryParam query) {
        return sysDeptRepository.findPage(query);
    }

    @Override
    public List<SysDept> findList(SysDeptQueryParam query) {
        return sysDeptRepository.findList(query);
    }
}
