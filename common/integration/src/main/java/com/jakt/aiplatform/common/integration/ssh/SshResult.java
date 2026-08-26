package com.jakt.aiplatform.common.integration.ssh;

import lombok.Data;

/**
 * SSH 执行结果。
 */
@Data
public class SshResult {

    /** 退出码（0 成功；-1 异常；-2 超时）。 */
    private int exitCode = -1;

    /** 标准输出 + 错误输出。 */
    private String output = "";

    /** 是否超时。 */
    private boolean timeout;

    /** 是否执行成功。 */
    public boolean isSuccess() {
        return !timeout && exitCode == 0;
    }
}
