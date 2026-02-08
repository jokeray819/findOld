package com.findold.service;

import com.findold.domain.Post;
import com.findold.domain.PostTag;
import com.findold.domain.Tag;
import com.findold.dto.post.PostCreateRequest;
import com.findold.dto.post.PostDetailResponse;
import com.findold.dto.post.PostListItem;
import com.findold.dto.post.PostUpdateRequest;
import com.findold.repository.PostRepository;
import com.findold.repository.PostTagRepository;
import com.findold.repository.TagRepository;
import com.findold.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final TagRepository tagRepository;
    private final AnswerService answerService;

    @Transactional
    public Post create(PostCreateRequest req) {
        Long userId = CurrentUser.requireUserId();
        Post post = Post.builder()
            .userId(userId)
            .type(req.getType())
            .description(req.getDescription())
            .sceneEra(req.getSceneEra())
            .regionZone(req.getRegionZone())
            .bountyAmount(req.getBountyAmount())
            .bountyMode(req.getBountyMode() != null ? req.getBountyMode() : "none")
            .status(req.getStatus() != null ? req.getStatus() : "open")
            .coverImage(req.getCoverImage())
            .sketchImage(req.getSketchImage())
            .build();
        final Post savedPost = postRepository.save(post);
        if (req.getTagIds() != null && !req.getTagIds().isEmpty()) {
            for (Long tagId : req.getTagIds()) {
                tagRepository.findById(tagId).ifPresent(tag -> {
                    PostTag pt = PostTag.builder().post(savedPost).tag(tag).build();
                    postTagRepository.save(pt);
                });
            }
        }
        return post;
    }

    public PostDetailResponse getById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("帖子不存在"));
        List<PostDetailResponse.TagDto> tags = postTagRepository.findByPostId(post.getId()).stream()
            .map(pt -> PostDetailResponse.TagDto.builder()
                .id(pt.getTag().getId())
                .name(pt.getTag().getName())
                .category(pt.getTag().getCategory())
                .build())
            .collect(Collectors.toList());
        long answerCount = answerService.countByPostId(post.getId());
        return PostDetailResponse.builder()
            .id(post.getId())
            .userId(post.getUserId())
            .type(post.getType())
            .description(post.getDescription())
            .sceneEra(post.getSceneEra())
            .regionZone(post.getRegionZone())
            .bountyAmount(post.getBountyAmount())
            .bountyMode(post.getBountyMode())
            .status(post.getStatus())
            .coverImage(post.getCoverImage())
            .sketchImage(post.getSketchImage())
            .tags(tags)
            .answerCount(answerCount)
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .build();
    }

    @Transactional
    public void update(Long id, PostUpdateRequest req) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("帖子不存在"));
        if (!post.getUserId().equals(CurrentUser.requireUserId())) {
            throw new IllegalArgumentException("无权限编辑");
        }
        if (!"draft".equals(post.getStatus()) && !"open".equals(post.getStatus())) {
            throw new IllegalArgumentException("当前状态不可编辑");
        }
        if (req.getType() != null) post.setType(req.getType());
        if (req.getDescription() != null) post.setDescription(req.getDescription());
        if (req.getSceneEra() != null) post.setSceneEra(req.getSceneEra());
        if (req.getRegionZone() != null) post.setRegionZone(req.getRegionZone());
        if (req.getBountyAmount() != null) post.setBountyAmount(req.getBountyAmount());
        if (req.getBountyMode() != null) post.setBountyMode(req.getBountyMode());
        if (req.getStatus() != null) post.setStatus(req.getStatus());
        if (req.getCoverImage() != null) post.setCoverImage(req.getCoverImage());
        if (req.getSketchImage() != null) post.setSketchImage(req.getSketchImage());
        if (req.getTagIds() != null) {
            postTagRepository.deleteByPostId(post.getId());
            for (Long tagId : req.getTagIds()) {
                tagRepository.findById(tagId).ifPresent(tag -> {
                    PostTag pt = PostTag.builder().post(post).tag(tag).build();
                    postTagRepository.save(pt);
                });
            }
        }
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Page<PostListItem> list(String type, String sceneEra, List<Long> tagIds, String bountyMode, Pageable pageable) {
        Specification<Post> spec = buildSpec(type, sceneEra, tagIds, bountyMode, null);
        Page<Post> page = postRepository.findAll(spec, pageable);
        return mapToListItemPage(page, pageable);
    }

    @Transactional(readOnly = true)
    public Page<PostListItem> search(String q, String type, String sceneEra, List<Long> tagIds, String bountyMode, Pageable pageable) {
        Specification<Post> spec = buildSearchSpec(type, sceneEra, tagIds, bountyMode, q);
        Page<Post> page = postRepository.findAll(spec, pageable);
        return mapToListItemPage(page, pageable);
    }

    @Transactional(readOnly = true)
    public Page<PostListItem> listByUserId(Long userId, Pageable pageable) {
        Page<Post> page = postRepository.findByUserId(userId, pageable);
        return mapToListItemPage(page, pageable);
    }

    /** 在事务内完成转换，避免 Page.map 懒执行导致 LazyInitializationException */
    private Page<PostListItem> mapToListItemPage(Page<Post> page, Pageable pageable) {
        List<PostListItem> content = page.getContent().stream().map(this::toListItem).collect(Collectors.toList());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    private Specification<Post> buildSpec(String type, String sceneEra, List<Long> tagIds, String bountyMode, String keyword) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), "open"));
            if (type != null && !type.isBlank()) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (sceneEra != null && !sceneEra.isBlank()) {
                predicates.add(cb.equal(root.get("sceneEra"), sceneEra));
            }
            if (bountyMode != null && !bountyMode.isBlank()) {
                predicates.add(cb.equal(root.get("bountyMode"), bountyMode));
            }
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(root.get("description"), "%" + keyword + "%"));
            }
            if (tagIds != null && !tagIds.isEmpty()) {
                var subq = query.subquery(Long.class);
                var pt = subq.from(PostTag.class);
                var t = pt.join("tag");
                subq.select(pt.get("post").get("id")).where(
                    t.get("id").in(tagIds),
                    cb.equal(pt.get("post").get("id"), root.get("id"))
                );
                predicates.add(cb.exists(subq));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    Specification<Post> buildSearchSpec(String type, String sceneEra, List<Long> tagIds, String bountyMode, String q) {
        return buildSpec(type, sceneEra, tagIds, bountyMode, q);
    }

    private PostListItem toListItem(Post post) {
        List<PostDetailResponse.TagDto> tags = postTagRepository.findByPostId(post.getId()).stream()
            .map(pt -> PostDetailResponse.TagDto.builder()
                .id(pt.getTag().getId())
                .name(pt.getTag().getName())
                .category(pt.getTag().getCategory())
                .build())
            .collect(Collectors.toList());
        long answerCount = answerService.countByPostId(post.getId());
        return PostListItem.builder()
            .id(post.getId())
            .userId(post.getUserId())
            .type(post.getType())
            .description(post.getDescription())
            .sceneEra(post.getSceneEra())
            .bountyAmount(post.getBountyAmount())
            .bountyMode(post.getBountyMode())
            .status(post.getStatus())
            .coverImage(post.getCoverImage())
            .tags(tags)
            .answerCount(answerCount)
            .createdAt(post.getCreatedAt())
            .build();
    }

    public Post getPostEntity(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("帖子不存在"));
    }
}
