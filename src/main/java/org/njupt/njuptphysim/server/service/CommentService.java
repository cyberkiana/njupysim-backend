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
}
