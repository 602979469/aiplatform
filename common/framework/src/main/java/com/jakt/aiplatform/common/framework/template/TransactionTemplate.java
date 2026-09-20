package com.jakt.aiplatform.common.framework.template;

/**
 * common-framework 层事务执行器抽象：屏蔽具体事务实现，业务只依赖本接口。
 */
public interface TransactionTemplate {

    /**
     * 在事务内执行并返回结果。
     *
     * @param action 事务回调
     * @param <T> 返回类型
     * @return 回调返回值
     */
    <T> T execute(TransactionCallback<T> action);

    /**
     * 在事务内执行，无返回值。
     *
     * @param action 事务回调
     */
    void executeWithoutResult(TransactionCallbackWithoutResult action);

    /**
     * 有返回值的事务回调。
     *
     * @param <T> 返回类型
     */
    @FunctionalInterface
    interface TransactionCallback<T> {

        /**
         * 执行事务体。
         *
         * @return 返回值
         */
        T execute();
    }

    /**
     * 无返回值的事务回调。
     */
    @FunctionalInterface
    interface TransactionCallbackWithoutResult {

        /**
         * 执行事务体。
         */
        void execute();
    }
}
