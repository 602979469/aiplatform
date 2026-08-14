package com.jakt.aiplatform.common.dal.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthLoginLogDalQuery extends DalPageQuery {

    private Long logId;

    private Long userId;

    private String username;

    private String loginIp;

    private String userAgent;

    private String status;

    private String message;

    private LocalDateTime loginTime;

    private LocalDateTime createTimeBegin;

    private LocalDateTime createTimeEnd;

    private LocalDateTime updateTimeBegin;

    private LocalDateTime updateTimeEnd;
}
