package net.sunxu.website.blog.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableFeignClients("net.sunxu.website")
@EnableJpaRepositories("net.sunxu.website.blog.service.repo.jpa")
@EnableElasticsearchRepositories("net.sunxu.website.blog.service.repo.es")
@EnableRedisRepositories("net.sunxu.website.blog.service.repo.redis")
public class BlogApplication {

    public static void main(String[] args) {
        // 避免 elastic search 的一个相关错误
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(BlogApplication.class, args);
    }
}
