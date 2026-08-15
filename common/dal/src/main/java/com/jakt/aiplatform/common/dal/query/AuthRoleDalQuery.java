package com.jakt.aiplatform.common.dal.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthRoleDalQuery extends DalPageQuery {

    private Long roleId;

    private String roleName;

    private String roleKey;

    private Integer roleSort;

    private String status;

    private String remark;

    private LocalDateTime createTimeBegin;

    private LocalDateTime createTimeEnd;

    private LocalDateTime updateTimeBegin;

    private LocalDateTime updateTimeEnd;
}
