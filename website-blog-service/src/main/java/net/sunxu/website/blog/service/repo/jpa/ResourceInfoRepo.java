package net.sunxu.website.blog.service.repo.jpa;

import net.sunxu.website.blog.service.entity.jpa.ResourceInfo;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface ResourceInfoRepo<T extends ResourceInfo> extends PagingAndSortingRepository<T, Long> {

}
