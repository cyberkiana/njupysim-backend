package org.njupt.njuptphysim.server.mapper;

import org.apache.ibatis.annotations.Mapper;
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
}
