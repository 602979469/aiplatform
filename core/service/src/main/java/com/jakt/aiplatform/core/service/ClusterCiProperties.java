package com.jakt.aiplatform.core.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 集群管理（cluster-ci）配置：master 工作目录与节点地址。
 */
@Data
@ConfigurationProperties(prefix = "cluster.ci")
public class ClusterCiProperties {

    /** master 上的工作根目录（挂载/脚本目录）。 */
    private String workDir = "/home/ubuntu/cluster-ci";

    /** master 主机（SSH 用户@IP）。 */
    private String masterHost = "ubuntu@192.168.3.131";

    /** worker 主机（SSH 用户@IP）。 */
    private String workerHost = "ubuntu@192.168.3.217";
}
