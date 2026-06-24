package org.njupt.njuptphysim.server.controller.common;


import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.pojo.dto.CommentDTO;
import org.njupt.njuptphysim.pojo.dto.ExpEvaluationDTO;
import org.njupt.njuptphysim.pojo.dto.ReplyDTO;
import org.njupt.njuptphysim.server.service.CommentService;
import org.njupt.njuptphysim.server.service.ResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/common/exps")
public class ExpController {

    @Autowired
    private ResourcesService resourcesService;
    @Autowired
    private CommentService commentService;

    /**
     * 查找现有实验list
     * @return id,title
     */
    @GetMapping("")
    public Result getAllExpList(){
        return Result.success(resourcesService.getAllExpList());
    }

    /**
     * 查询实验详情
     * @param id 实验id
     * @return ExpVO
     */
    @GetMapping("/{expId}")
    public Result getExpDetail(@PathVariable("expId") Integer id){
        return Result.success(resourcesService.getExpDetail(id));
    }

    /**
     * 更改实验点赞和难度总数
     * @param id 实验id
     * @param expEvaluationDTO likes easyCount hardCount
     * @return
     */
    @PostMapping("/{expId}/evaluation")
    public Result updateEvaluation(@PathVariable("expId") int id, @RequestBody ExpEvaluationDTO expEvaluationDTO){
        resourcesService.updateEvaluation(id,expEvaluationDTO);
        return Result.success();
    }

    /**
     * 查询评论列表
     * @param expId 实验id
     * @return 评论列表
     */
    @GetMapping("/{expId}/comments")
    public Result getComments(@PathVariable int expId){
        return Result.success(commentService.getComments(expId));
    }

    /**
     * 查询回复列表
     * @param expId 实验id
     * @param commentId 评论id
     * @return 回复列表
     */
    @GetMapping("/{expId}/comments/{commentId}/replies")
    public Result getReplies(@PathVariable int expId, @PathVariable int commentId){
        return Result.success(commentService.getReplies(expId, commentId));
    }

    /**
     * 发送评论
     * @param expId 实验id
     * @param comment 评论内容
     * @return success
     */
    @PostMapping("/{expId}/comments")
    public Result postComment(@PathVariable int expId, @RequestBody CommentDTO comment){
        commentService.postComment(expId, comment);
        return Result.success();
    }

    /**
     * 发送回复
     * @param expId 实验id
     * @param commentId 评论id
     * @return success
     */
    @PostMapping("/{expId}/comments/{commentId}/replies")
    public Result postReply(@PathVariable int expId, @PathVariable int commentId, @RequestBody ReplyDTO reply){
        commentService.postReply(expId, commentId, reply);
        return Result.success();
    }

    /**
     * 评论点赞数+1
     * @param commentId 评论id
     * @return success
     */
    @PostMapping("/{expId}/comments/{commentId}/evaluation")
    public Result addCommentLikes(@PathVariable int commentId){
        commentService.addCommentLikes(commentId);
        return Result.success();
    }

    /**
     * 回复点赞数+1
     * @param replyId 回复id
     * @return success
     */
    @PostMapping("/{expId}/comments/{commentId}/replies/{replyId}/evaluation")
    public Result addReplyLikes(@PathVariable int replyId){
        commentService.addReplyLikes(replyId);
        return Result.success();
    }

}
