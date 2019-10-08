package net.sunxu.website.blog.service.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AuthorDTO {

    private Long id;

    private String name;

    private String avatarUrl;
}
