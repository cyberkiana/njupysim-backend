package org.njupt.njuptphysim.server.service.impl;

import org.njupt.njuptphysim.pojo.dto.CommentDTO;
import org.njupt.njuptphysim.pojo.dto.ReplyDTO;
import org.njupt.njuptphysim.pojo.po.Comments;
import org.njupt.njuptphysim.pojo.po.Replies;
import org.njupt.njuptphysim.pojo.vo.CommentVO;
import org.njupt.njuptphysim.pojo.vo.ReplyVO;
import org.njupt.njuptphysim.server.mapper.CommentMapper;
import org.njupt.njuptphysim.server.service.CommentService;
import org.njupt.njuptphysim.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserService userService;

    @Override
    public List<CommentVO> getComments(int expId) {
        List<Comments> comments = commentMapper.getCommentsByResourceId(expId);
        List<CommentVO> commentVOList = new ArrayList<>();
        for (Comments comment : comments) {
            commentVOList.add(
                    CommentVO.builder()
                            .id(comment.getId())
                            .time(comment.getTime())
                            .content(comment.getContent())
                            .likes(comment.getLikes())
                            .avatar(userService.getAvatarById(comment.getUserId()))
                            .userName(userService.getNameById(comment.getUserId()))
                            .build()
            );
        }
        return commentVOList;
    }

    @Override
    public List<ReplyVO> getReplies(int expId, int commentId) {
        List<Replies> replies = commentMapper.getRepliesByCommentId(commentId);
        List<ReplyVO> replyVOList = new ArrayList<>();
        for (Replies reply : replies) {
            replyVOList.add(
                    ReplyVO.builder()
                            .id(reply.getId())
                            .time(reply.getTime())
                            .content(reply.getContent())
                            .likes(reply.getLikes())
                            .repliedUserName(reply.getRepliedUserName())
                            .userName(userService.getNameById(reply.getUserId()))
                            .avatar(userService.getAvatarById(reply.getUserId()))
                            .build()
            );
        }
        return replyVOList;
    }

    @Override
    public void postComment(int expId, CommentDTO comment) {
        Comments c = Comments.builder()
                .userId(comment.getUserId())
                .content(comment.getContent())
                .time(comment.getTime())
                .resourceId(expId)
                .build();
        commentMapper.postComment(c);
    }

    @Override
    public void postReply(int expId, int commentId, ReplyDTO reply) {
        Replies r = Replies.builder()
                .commentId(commentId)
                .userId(reply.getUserId())
                .content(reply.getContent())
                .time(reply.getTime())
                .repliedUserName(reply.getRepliedUserName())
                .build();
        commentMapper.postReply(r);
    }

    @Override
    public void addCommentLikes(int commentId) {
        commentMapper.addCommentLikes(commentId);
    }

    @Override
    public void addReplyLikes(int replyId) {
        commentMapper.addReplyLikes(replyId);
    }

}
