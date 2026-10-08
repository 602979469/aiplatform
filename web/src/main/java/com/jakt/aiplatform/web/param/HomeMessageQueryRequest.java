package com.jakt.aiplatform.web.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 留言板分页查询请求：留言板无过滤条件，只用分页参数（默认每页 10 条）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HomeMessageQueryRequest extends PageQueryRequest {
}
