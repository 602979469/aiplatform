package com.jakt.aiplatform.common.util.tools;

import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.util.enums.ThreadPoolEnum;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 线程池调用工具：按枚举名获取/提交任务到对应线程池，业务方不直接依赖具体线程池 Bean。
 */
@Component
public class ThreadPoolUtil {

    /** 线程池 Bean 映射（key = Bean 名称）。 */
    private final Map<String, ThreadPoolTaskExecutor> threadPoolMap;

    public ThreadPoolUtil(Map<String, ThreadPoolTaskExecutor> threadPoolMap) {
        this.threadPoolMap = threadPoolMap;
    }

    /**
     * 异步执行任务。
     *
     * @param pool 线程池枚举
     * @param task 任务
     */
    public void execute(ThreadPoolEnum pool, Runnable task) {
        getExecutor(pool).execute(task);
    }

    /**
     * 提交带返回值的任务。
     *
     * @param pool 线程池枚举
     * @param task 任务
     * @param <T> 返回值类型
     * @return 异步结果
     */
    public <T> Future<T> submit(ThreadPoolEnum pool, Callable<T> task) {
        return getExecutor(pool).submit(task);
    }

    /**
     * 按枚举获取线程池，找不到时抛系统异常。
     *
     * @param pool 线程池枚举
     * @return 线程池执行器
     */
    public ThreadPoolTaskExecutor getExecutor(ThreadPoolEnum pool) {
        ThreadPoolTaskExecutor executor = threadPoolMap.get(pool.getBeanName());
        AssertUtil.throwErrWhenNull(executor, ErrorCodeEnum.SYSTEM_ERROR, "未找到线程池: " + pool.getBeanName());
        return executor;
    }
}
