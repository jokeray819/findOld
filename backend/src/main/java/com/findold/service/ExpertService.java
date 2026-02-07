package com.findold.service;

import com.findold.domain.Expert;
import com.findold.domain.User;
import com.findold.dto.expert.ExpertListItem;
import com.findold.dto.expert.ExpertMeResponse;
import com.findold.repository.ExpertRepository;
import com.findold.repository.UserRepository;
import com.findold.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ExpertService {

    private final ExpertRepository expertRepository;
    private final UserRepository userRepository;

    @Transactional
    public void incrementValidHelpCount(Long userId) {
        Expert expert = expertRepository.findByUserId(userId).orElseGet(() -> {
            Expert e = Expert.builder()
                .userId(userId)
                .validHelpCount(0)
                .status("pending")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
            return expertRepository.save(e);
        });
        expert.setValidHelpCount(expert.getValidHelpCount() + 1);
        expertRepository.save(expert);
    }

    @Transactional
    public void applyCertification() {
        Long userId = CurrentUser.requireUserId();
        Expert expert = expertRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("暂无有效帮寻记录，完成 3 次及以上有效帮寻后可申请认证"));
        if (expert.getValidHelpCount() < 3) {
            throw new IllegalArgumentException("需完成 3 次及以上有效帮寻方可申请认证");
        }
        if ("certified".equals(expert.getStatus())) {
            throw new IllegalArgumentException("已认证");
        }
        expert.setStatus("certified");
        expert.setCertifiedAt(Instant.now());
        expertRepository.save(expert);
    }

    public ExpertMeResponse getMe() {
        Long userId = CurrentUser.requireUserId();
        return expertRepository.findByUserId(userId)
            .map(e -> ExpertMeResponse.builder()
                .userId(e.getUserId())
                .validHelpCount(e.getValidHelpCount())
                .status(e.getStatus())
                .certifiedAt(e.getCertifiedAt())
                .build())
            .orElse(ExpertMeResponse.builder()
                .userId(userId)
                .validHelpCount(0)
                .status("pending")
                .certifiedAt(null)
                .build());
    }

    public Page<ExpertListItem> list(String status, Pageable pageable) {
        Page<Expert> experts = status != null && !status.isBlank()
            ? expertRepository.findByStatus(status, pageable)
            : expertRepository.findAll(pageable);
        return experts.map(e -> {
            User u = userRepository.findById(e.getUserId()).orElse(null);
            return ExpertListItem.builder()
                .userId(e.getUserId())
                .nickname(u != null ? u.getNickname() : null)
                .avatarUrl(u != null ? u.getAvatarUrl() : null)
                .validHelpCount(e.getValidHelpCount())
                .status(e.getStatus())
                .certifiedAt(e.getCertifiedAt())
                .build();
        });
    }
}
