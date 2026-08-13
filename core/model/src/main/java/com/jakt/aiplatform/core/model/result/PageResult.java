package com.jakt.aiplatform.core.model.result;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页结果容器（RuoYi TableDataInfo 语义：total/rows/code/msg，并保留分页参数）。
 *
 * <p>rows 为当前页数据列表；code=0 成功；msg 供表格提示。生成器 CRUD 与 RuoYi 表格查询共用本类。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResult<T> {

    /** 总条数。 */
    private long total;

    /** 当前页数据列表。 */
    private List<T> rows;

    /** 页码，从 1 开始。 */
    private int pageNum = 1;

    /** 每页条数。 */
    private int pageSize = 10;

    /** 消息状态码（0 成功）。 */
    private int code = 0;

    /** 消息内容。 */
    private String msg;

    /**
     * 兼容生成器旧调用：返回当前页数据列表（rows 别名）。
     *
     * @return 当前页数据列表
     */
    public List<T> getDataList() {
        return rows;
    }

    /**
     * 分页快捷构造（生成器 CRUD 使用）。
     *
     * @param total    总条数
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @param rows     当前页数据列表
     */
    public PageResult(long total, int pageNum, int pageSize, List<T> rows) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.rows = rows;
        this.code = 0;
    }
}
