package net.sunxu.website.blog.service.controller;

import java.util.List;
import net.sunxu.website.blog.service.dto.CommentCreationDTO;
import net.sunxu.website.blog.service.dto.CommentDTO;
import net.sunxu.website.blog.service.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article/{id}/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/list")
    public List<CommentDTO> getComments(@PathVariable("id") Long articleId,
            @RequestParam("pageSize") Integer pageSize, @RequestParam("pageIndex") Integer pageIndex) {
        return commentService.listCommentsByArticle(articleId, PageRequest.of(pageIndex - 1, pageSize));
    }

    @PostMapping("")
    public CommentDTO postComment(@PathVariable("id") Long articleId, @RequestBody CommentCreationDTO createDTO) {
        return commentService.createComment(articleId, createDTO);
    }

}
