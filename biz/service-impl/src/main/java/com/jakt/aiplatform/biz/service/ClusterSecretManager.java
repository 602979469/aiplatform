package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.common.framework.result.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 集群密钥管理用例编排：以 K8s 为唯一真相，SSH 实时操作，无本地落库。
 */
public interface ClusterSecretManager {

    /**
     * 分页查询密钥（实时读集群，键名只读）。
     *
     * @param namespace 命名空间（缺省 tsk）
     * @param keyword   名称关键字（可为空）
     * @param pageNum   页码
     * @param pageSize  每页条数
     * @return 分页结果
     */
    PageResult<ClusterSecretView> page(String namespace, String keyword, int pageNum, int pageSize);

    /**
     * 下拉选项：某命名空间下全部带 aiplatform-managed 标签的密钥（含键名）。
     *
     * @param namespace 命名空间
     * @return 密钥列表
     */
    List<ClusterSecretView> options(String namespace);

    /**
     * 密钥详情（键名列表）。
     *
     * @param namespace 命名空间
     * @param name      密钥名称
     * @return 详情
     */
    ClusterSecretView detail(String namespace, String name);

    /**
     * 新增/覆盖键值并同步集群：服务端读当前内容合并后 apply，未提交的键原样保留；
     * 自动打上 aiplatform-managed 标签。值不回显、不落日志。
     *
     * @param namespace 命名空间
     * @param name      密钥名称
     * @param type      类型（为空则沿用现有或 Opaque）
     * @param exists    编辑模式：true=仅更新（不存在报错）；false=仅新增（同名报错）；null=宽松 upsert
     * @param keyValues 需要覆盖/新增的键值
     */
    void upsert(String namespace, String name, String type, Boolean exists, Map<String, String> keyValues);
}
