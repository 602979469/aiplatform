package com.jakt.aiplatform.common.dal.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class AiChatSessionDalQuery extends DalPageQuery {

    private Long sessionId;

    private String sessionName;

    private Long userId;

    private String userName;

    private String status;

    private String remark;

    private LocalDateTime createTimeBegin;

    private LocalDateTime createTimeEnd;

    private LocalDateTime updateTimeBegin;

    private LocalDateTime updateTimeEnd;
}
