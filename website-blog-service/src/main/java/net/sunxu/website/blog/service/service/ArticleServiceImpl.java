package net.sunxu.website.blog.service.service;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.sunxu.website.blog.service.dto.ArticleBreifDTO;
import net.sunxu.website.blog.service.dto.ArticleCreationDTO;
import net.sunxu.website.blog.service.dto.ArticleEditDTO;
import net.sunxu.website.blog.service.dto.ArticleResourceDTO;
import net.sunxu.website.blog.service.dto.AuthorDTO;
import net.sunxu.website.blog.service.entity.es.ArticleEs;
import net.sunxu.website.blog.service.entity.jpa.ArticleInfo;
import net.sunxu.website.blog.service.event.ArticleEvent;
import net.sunxu.website.blog.service.event.ArticleEvent.Type;
import net.sunxu.website.blog.service.repo.es.ArticleEsRepo;
import net.sunxu.website.blog.service.repo.jpa.ArticleInfoRepo;
import net.sunxu.website.config.feignclient.exception.InvalidException;
import net.sunxu.website.config.feignclient.exception.ServiceException;
import net.sunxu.website.config.security.authentication.SecurityHelpUtils;
import net.sunxu.website.file.feignclient.FileFeignClient;
import net.sunxu.website.help.util.ObjectHelpUtils;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Element;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleServiceImpl implements ArticleService, ApplicationEventPublisherAware {

    @Autowired
    private ArticleInfoRepo articleInfoRepo;

    @Autowired
    private ArticleEsRepo articleEsRepo;

    @Autowired
    @Lazy
    private StatisticsService statisticsService;

    @Autowired
    private FileFeignClient fileFeignClient;

    @Autowired
    private UserService userService;

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public ArticleResourceDTO viewArticle(Long id) {
        ArticleInfo article = getArticle(id);
        ArticleResourceDTO dto = new ArticleResourceDTO();
        BeanUtils.copyProperties(article, dto);
        dto.setEditTime(article.getLastUpdateTime());
        dto.setTimeKey(article.timeKey());
        dto.setStatistics(statisticsService.getStatistics(id));

        ArticleEvent event = new ArticleEvent(this, Type.VIEWED, article);
        event.setEventUserId(ObjectHelpUtils.nvl(SecurityHelpUtils.getCurrentUserId(), 0L));
        applicationEventPublisher.publishEvent(event);
        return dto;
    }

    @Override
    public ArticleInfo getArticle(Long id) {
        return articleInfoRepo.findById(id)
                .orElseThrow(() -> ServiceException.newException("找不到对应文章"));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long createArticle(ArticleCreationDTO dto) {
        ArticleInfo article = new ArticleInfo();
        BeanUtils.copyProperties(dto, article);
        Long creatorId = SecurityHelpUtils.getCurrentUserId();
        Date now = new Date();
        article.setCreatorId(creatorId);
        article.setCreateTime(now);
        article.setLastUpdateTime(now);
        article.setLastUpdateUserId(creatorId);
        article.setFileIds(handleFileIds(article, dto.getFiles(), Collections.emptySet()));
        articleInfoRepo.save(article);

        String text = extractContentText(dto.getContentType(), dto.getContent());
        ArticleEs es = new ArticleEs();
        es.setArticleId(article.getId());
        es.setTitle(article.getTitle());
        es.setText(text);
        es.setCreatorId(creatorId);
        es.setCreateTime(now);
        es.setUpdateTime(now);
        articleEsRepo.save(es);

        ArticleEvent event = new ArticleEvent(this, Type.CREATED, article);
        event.setEventUserId(creatorId);
        applicationEventPublisher.publishEvent(event);

        return article.getId();
    }

    private Set<String> handleFileIds(ArticleInfo article, Set<String> newFileIds, Set<String> oldFileIds) {
        if (newFileIds.isEmpty() && oldFileIds.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> savedFileIds = parseFileIdsFromText(article.getContentType(), article.getContent());
        Set<String> toDeleteFileIds = new HashSet<>();
        Set<String> fileIds = new HashSet<>(newFileIds);
        for (Iterator<String> it = fileIds.iterator(); it.hasNext(); ) {
            String fileId = it.next();
            if (!savedFileIds.contains(fileId)) {
                toDeleteFileIds.add(fileId);
                it.remove();
            }
        }
        for (String fileId : oldFileIds) {
            if (!fileIds.contains(fileId)) {
                toDeleteFileIds.add(fileId);
            }
        }
        if (!toDeleteFileIds.isEmpty()) {
            fileFeignClient.deleteFiles(toDeleteFileIds.toArray(new String[0]));
        }
        if (!fileIds.isEmpty()) {
            fileFeignClient.makeFilesPublic(fileIds.toArray(new String[0]));
        }
        return fileIds;

    }

    private String extractContentText(String contentType, String content) {
        if ("text".equalsIgnoreCase(contentType)) {
            return content;
        } else if ("markdown".equalsIgnoreCase(contentType)) {
            return convertMarkdown2Text(content);
        } else {
            throw ServiceException.newException("不识别的文章格式");
        }
    }

    private String convertMarkdown2Text(String content) {
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        Node document = parser.parse(content);
        String html = renderer.render(document);
        HtmlToPlainText formatter = new HtmlToPlainText();
        return formatter.getPlainText(Jsoup.parse(html));
    }

    private Set<String> parseFileIdsFromText(String contentType, String content) {
        if ("text".equalsIgnoreCase(contentType)) {
            return Collections.emptySet();
        }
        if (!"markdown".equalsIgnoreCase(contentType)) {
            throw ServiceException.newException("不识别的文章格式");
        }

        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Node document = parser.parse(content);
        String html = renderer.render(document);
        var doc = Jsoup.parse(html);
        Set<String> res = new HashSet<>();
        for (Element img : doc.getElementsByTag("img")) {
            String[] path = img.attr("src").split("[\\\\/]");
            res.add(path[path.length - 1]);
        }
        return res;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateArticle(ArticleEditDTO dto) {
        ArticleInfo info = getArticle(dto.getId());
        var user = SecurityHelpUtils.getCurrentUser();
        InvalidException.assertEquals(info.getId(), user.getId(), "只有作者才能更新文章");
        info.setLastUpdateUserId(SecurityHelpUtils.getCurrentUserId());
        info.setLastUpdateTime(new Date());
        info.setContentType(dto.getContentType());
        info.setContent(dto.getContent());
        info.setFileIds(handleFileIds(info, dto.getFileIds(), info.getFileIds()));
        articleInfoRepo.save(info);

        var es = articleEsRepo.findByArticleId(info.getId());
        es.setUpdateTime(info.getLastUpdateTime());
        String text = extractContentText(dto.getContentType(), dto.getContent());
        es.setText(text);
        articleEsRepo.save(es);

        ArticleEvent event = new ArticleEvent(this, Type.UPDATED, info);
        event.setEventUserId(user.getId());
        applicationEventPublisher.publishEvent(event);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteArticle(Long id) {
        ArticleInfo info = getArticle(id);
        Long userId = SecurityHelpUtils.getCurrentUserId();
        InvalidException.assertEquals(info.getId(), userId, "只有作者才能删除文章");
        articleInfoRepo.deleteById(id);
        articleEsRepo.deleteByArticleId(id);

        ArticleEvent event = new ArticleEvent(this, Type.DELETED, info);
        event.setEventUserId(userId);
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public List<ArticleBreifDTO> listLatestArticle(long lastTimeKey, int count) {
        var list = articleEsRepo.findAllByUpdateTimeLessThanOrderByUpdateTimeDesc(
                lastTimeKey, PageRequest.of(0, count));
        return convertToBreifDTO(list);
    }

    private List<ArticleBreifDTO> convertToBreifDTO(List<ArticleEs> list) {
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, AuthorDTO> creators = new HashMap<>();
        var userInfos = userService.getUserInfos(list.stream().map(ArticleEs::getCreatorId)
                .collect(Collectors.toSet()));
        var statistics = statisticsService.listStatistics(
                list.stream().map(ArticleEs::getArticleId).collect(Collectors.toSet()));
        return list.stream().map(es -> {
            ArticleBreifDTO dto = new ArticleBreifDTO();
            dto.setTitle(es.getTitle());
            dto.setText(es.getText());
            dto.setId(es.getArticleId());
            dto.setCreateTime(es.getCreateTime());
            dto.setEditTime(es.getUpdateTime());

            dto.setStatistics(statistics.get(es.getArticleId()));
            AuthorDTO author = creators.get(es.getCreatorId());
            if (author == null) {
                var userInfo = userInfos.get(es.getCreatorId());
                if (userInfo != null) {
                    author = new AuthorDTO();
                    BeanUtils.copyProperties(userInfo, author);
                    creators.put(userInfo.getId(), author);
                }
            }
            dto.setCreator(author);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ArticleBreifDTO> listLatestArticleByUser(long lastTimeKey, int count, Long userId) {
        var list = articleEsRepo.findAllByUpdateTimeLessThanAndCreatorIdEqualsOrderByUpdateTimeDesc(
                lastTimeKey, userId, PageRequest.of(0, count));
        return convertToBreifDTO(list);
    }

    @Override
    public List<ArticleBreifDTO> search(String text, Pageable pageable) {
        var list = articleEsRepo.findAllByTextLikeOrderByUpdateTime(text, pageable);
        return convertToBreifDTO(list);
    }

    @Override
    public long countSearch(String text) {
        return articleEsRepo.countByTextLike(text);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
