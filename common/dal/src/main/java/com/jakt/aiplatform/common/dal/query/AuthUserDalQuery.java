package com.jakt.aiplatform.common.dal.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthUserDalQuery extends DalPageQuery {

    private Long userId;

    private String username;

    private String nickname;

    private String email;

    private String avatar;

    private String status;

    private String remark;

    private LocalDateTime createTimeBegin;

    private LocalDateTime createTimeEnd;

    private LocalDateTime updateTimeBegin;

    private LocalDateTime updateTimeEnd;
}
