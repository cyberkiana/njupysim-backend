package org.njupt.njuptphysim.server.service;

import org.njupt.njuptphysim.pojo.dto.CommentDTO;
import org.njupt.njuptphysim.pojo.dto.ReplyDTO;
import org.njupt.njuptphysim.pojo.vo.CommentVO;
import org.njupt.njuptphysim.pojo.vo.ReplyVO;

import java.util.List;

public interface CommentService {

    /**
     * 根据实验id获取评论列表
     * @param expId 实验id
     * @return 评论列表List<CommentVO>
     */
    List<CommentVO> getComments(int expId);

    /**
     * 查询评论下方的回复
     * @param expId 实验id
     * @param commentId 评论id
     * @return 评论列表
     */
    List<ReplyVO> getReplies(int expId, int commentId);

    /**
     * 发送评论
     * @param comment 评论内容
     */
    void postComment(int expId, CommentDTO comment);

    /**
     * 发送回复
     * @param expId 实验id
     * @param commentId 评论id
     * @param reply 回复内容
     */
    void postReply(int expId, int commentId, ReplyDTO reply);

    /**
     * 评论点赞数+1
     * @param commentId 评论id
     */
    void addCommentLikes(int commentId);

    /**
     * 回复点赞数+1
     * @param replyId 回复id
     */
    void addReplyLikes(int replyId);

    /**
     * 管理员删除评论（软删除visible）
     * @param commentId 评论id
     */
    void deleteComment(int commentId);

    /**
     * 管理员删除回复（软删除visible）
     * @param replyId 回复id
     */
    void deleteReply(int replyId);

    /**
     * 按时间范围搜索实验下的评论与回复（评论命中返回其id, 回复命中返回其主评论id, 已去重）
     * @param resourceId 实验id
     * @param start 起始时间(含), yyyy-MM-dd HH:mm:ss
     * @param end 结束时间(不含), yyyy-MM-dd HH:mm:ss
     * @return 命中的主评论id列表（按命中时间降序）
     */
    java.util.List<Integer> searchCommentIds(int resourceId, String start, String end);
}
