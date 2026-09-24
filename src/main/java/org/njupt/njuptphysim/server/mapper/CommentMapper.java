package org.njupt.njuptphysim.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.njupt.njuptphysim.pojo.po.Comments;
import org.njupt.njuptphysim.pojo.po.Replies;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 查询实验下方可见评论详情
     * @param resourceId 实验id
     * @return 可见评论详情（visible=1）
     */
    List<Comments> getCommentsByResourceId(int resourceId);

    /**
     * 查询评论的可见回复
     * @param commentId 评论id
     * @return 可见回复
     */
    List<Replies> getRepliesByCommentId(int commentId);

    /**
     * 新增评论
     * @param comment 评论内容
     */
    void postComment(Comments comment);


    /**
     * 新增回复
     * @param reply 回复内容
     */
    void postReply(Replies reply);

    /**
     * 评论点赞数+1
     * @param commentId 评论id
     */
    void addCommentLikes(int commentId);

    /**
     * 回复点赞数+1
     * @param replyId 回复id
     */
    void  addReplyLikes(int replyId);

    /**
     * 管理员删除评论（软删除：visible置0，查询接口只返回visible=1的数据）
     * @param id 评论id
     * @return 影响行数（0表示评论不存在）
     */
    @Update("update njupt_physim.comments set visible = 0 where id = #{id}")
    int deleteComment(int id);

    /**
     * 管理员删除回复（软删除：visible置0）
     * @param id 回复id
     * @return 影响行数（0表示回复不存在）
     */
    @Update("update njupt_physim.replies set visible = 0 where id = #{id}")
    int deleteReply(int id);

    /**
     * 按时间范围搜索实验下的可见评论（走 idx_rvt 索引）
     * @param resourceId 实验id
     * @param start 起始时间(含)
     * @param end 结束时间(不含)
     * @return 命中的评论
     */
    @Select("select id, user_id, time, content, likes from njupt_physim.comments " +
            "where resource_id = #{resourceId} and visible = 1 and time >= #{start} and time < #{end} " +
            "order by time desc")
    List<Comments> searchCommentsByTime(@Param("resourceId") int resourceId,
                                        @Param("start") String start,
                                        @Param("end") String end);

    /**
     * 按时间范围搜索实验下的可见回复（走 idx_vtc 覆盖索引）
     * @param resourceId 实验id
     * @param start 起始时间(含)
     * @param end 结束时间(不含)
     * @return 命中的回复（含comment_id用于回挂主评论）
     */
    @Select("select id, comment_id, user_id, time, content from njupt_physim.replies " +
            "where resource_id = #{resourceId} and visible = 1 and time >= #{start} and time < #{end} " +
            "order by time desc")
    List<Replies> searchRepliesByTime(@Param("resourceId") int resourceId,
                                      @Param("start") String start,
                                      @Param("end") String end);
}
