package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.HomeMessageDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 首页留言板留言 Mapper。SQL 全部在 resources/mapper/HomeMessageMapper.xml 中。
 */
@Mapper
public interface HomeMessageMapper {

    /**
     * 新增，自增主键回填到 {@code homeMessageDO.id}。
     *
     * @param homeMessageDO 数据对象
     * @return 受影响行数
     */
    int insert(HomeMessageDO homeMessageDO);

    /**
     * 按主键倒序分页查询（最新在前）。
     *
     * @param offset   偏移量
     * @param pageSize 每页条数
     * @return 留言列表
     */
    List<HomeMessageDO> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 统计有效留言总数。
     *
     * @return 总数
     */
    long countAll();

    /**
     * 查询同一 IP 最近一条留言（发帖频率限制用）。
     *
     * @param clientIp 客户端IP
     * @return 最近一条留言；没有返回 null
     */
    HomeMessageDO selectLatestByIp(@Param("clientIp") String clientIp);
}
