package org.njupt.njuptphysim.server.controller.admin;

import org.njupt.njuptphysim.common.annotation.OperationLog;
import org.njupt.njuptphysim.common.exceptions.BaseException;
import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.server.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员评论管理：删除违规评论与回复（软删除，visible置0，前端不再展示）
 */
@RestController
@RequestMapping("/admin/comments")
public class AdminCommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 删除评论（软删除）
     * @param commentId 评论id
     */
    @OperationLog(value = "删除", detail = "管理员删除评论")
    @DeleteMapping("/{commentId}")
    public Result deleteComment(@PathVariable int commentId) {
        commentService.deleteComment(commentId);
        return Result.success();
    }

    /**
     * 删除评论下的回复（软删除）
     * @param commentId 评论id
     * @param replyId 回复id
     */
    @OperationLog(value = "删除", detail = "管理员删除回复")
    @DeleteMapping("/{commentId}/replies/{replyId}")
    public Result deleteReply(@PathVariable int commentId, @PathVariable int replyId) {
        commentService.deleteReply(replyId);
        return Result.success();
    }

    /**
     * 按时间范围搜索实验下的评论与回复
     * 搜索结果为主评论id列表：评论命中返回其id, 回复命中返回其所属主评论id（已去重, 按命中时间降序）
     * @param resourceId 实验id
     * @param start 起始时间(含), yyyy-MM-dd HH:mm:ss
     * @param end 结束时间(不含), yyyy-MM-dd HH:mm:ss
     */
    @OperationLog(value = "查询", detail = "按时间搜索评论与回复")
    @GetMapping("/search")
    public Result search(@RequestParam int resourceId,
                         @RequestParam String start,
                         @RequestParam String end) {
        //校验时间格式, 避免非法值直接打到数据库
        requireDateTime(start, "start");
        requireDateTime(end, "end");
        return Result.success(commentService.searchCommentIds(resourceId, start, end));
    }

    private void requireDateTime(String value, String name) {
        String s = value == null ? "" : value.trim();
        if (s.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}(:\\d{2})?") || s.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return;
        }
        throw new BaseException(name + " 时间格式应为 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss");
    }
}
