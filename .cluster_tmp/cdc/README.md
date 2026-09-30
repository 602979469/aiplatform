# Canal 部署说明（/home/ubuntu/cdc/）— MySQL → ES 增量同步

## 组成

| 组件 | 说明 |
|---|---|
| canal-server | 读 MySQL binlog，通过 TCP(11111) 暴露变更事件；`canal/canal-server:v1.1.8` 镜像 |
| canal-adapter | 消费变更事件并写入 ES；官方只有 tar.gz 包，现挂在 `/home/ubuntu/cdc/adapter`，用同一镜像的 Java 8 运行 |

## 部署

```bash
bash /home/ubuntu/cdc/deploy.sh          # 幂等，可反复执行
CANAL_DB_PASSWORD=xxx bash /home/ubuntu/cdc/deploy.sh   # 自定义 canal 账号密码
```

脚本 6 步：镜像导入 → 校验 adapter 包 → MySQL 建/更新同步账号 → namespace+secret → apply → 等就绪。

## 连接信息

- MySQL 同步账号：`canal / Canal@2026`（权限：SELECT + REPLICATION SLAVE/CLIENT）
- 同步范围：`aiplatform` 库（`instance.properties` 的 `canal.instance.filter.regex`）
- ES：`elasticsearch.efk.svc.cluster.local:9200`，账号密码取自 `efk-secret`（写入 `cdc-secret`）

## 已接的表（PoC）

`aiplatform.cluster_pod_config` → ES 索引 `biz_cluster_pod_config`，配置在
`manifests/20-canal-adapter.yaml` 的 `canal-adapter-es8-mapping` ConfigMap 里。

**加表**：在同一个 ConfigMap 里按 `cluster_pod_config.yml` 的格式加一段（`_index` / `_id` / `sql`），
然后 `kubectl apply -f manifests/20-canal-adapter.yaml && kubectl -n cdc rollout restart deploy/canal-adapter`。

## 验证

```bash
# MySQL 改一行
kubectl -n tsk exec mysql-0 -- mysql -uroot -p<pw> aiplatform -e "update cluster_pod_config set remark='cdc-test' where id=<id>;"
# ES 看文档
kubectl -n efk exec deploy/elasticsearch -- curl -s -u elastic:<pw> "http://localhost:9200/biz_cluster_pod_config/_search?pretty&size=3"
```

## 注意

1. canal-server 首次启动会从当前 binlog 位点开始（不做历史全量）；需要历史数据要先跑 adapter 的全量
   （`curl http://canal-adapter:8081/etl/es8/cluster_pod_config.yml -X POST`）。
2. MySQL 的 `binlog_expire_logs_seconds` 要大于可能停机时长，否则位点失效需重来。
3. 两个组件都钉在 k8s-master，不写 CPU request（master CPU requests 已满）。
