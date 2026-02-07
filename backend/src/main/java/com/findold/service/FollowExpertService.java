package com.findold.service;

import com.findold.domain.FollowExpert;
import com.findold.dto.expert.ExpertListItem;
import com.findold.repository.ExpertRepository;
import com.findold.repository.FollowExpertRepository;
import com.findold.repository.UserRepository;
import com.findold.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowExpertService {

    private final FollowExpertRepository followExpertRepository;
    private final ExpertRepository expertRepository;
    private final UserRepository userRepository;

    @Transactional
    public void follow(Long expertUserId) {
        Long userId = CurrentUser.requireUserId();
        if (userId.equals(expertUserId)) {
            throw new IllegalArgumentException("不能关注自己");
        }
        if (followExpertRepository.existsByUserIdAndExpertUserId(userId, expertUserId)) {
            return;
        }
        FollowExpert fe = FollowExpert.builder().userId(userId).expertUserId(expertUserId).build();
        followExpertRepository.save(fe);
    }

    @Transactional
    public void unfollow(Long expertUserId) {
        Long userId = CurrentUser.requireUserId();
        followExpertRepository.deleteByUserIdAndExpertUserId(userId, expertUserId);
    }

    public Page<ExpertListItem> listMyFollowings(Pageable pageable) {
        Long userId = CurrentUser.requireUserId();
        return followExpertRepository.findByUserId(userId, pageable)
            .map(fe -> {
                var expert = expertRepository.findByUserId(fe.getExpertUserId()).orElse(null);
                var user = userRepository.findById(fe.getExpertUserId()).orElse(null);
                return ExpertListItem.builder()
                    .userId(fe.getExpertUserId())
                    .nickname(user != null ? user.getNickname() : null)
                    .avatarUrl(user != null ? user.getAvatarUrl() : null)
                    .validHelpCount(expert != null ? expert.getValidHelpCount() : 0)
                    .status(expert != null ? expert.getStatus() : "pending")
                    .certifiedAt(expert != null ? expert.getCertifiedAt() : null)
                    .build();
            });
    }
}
