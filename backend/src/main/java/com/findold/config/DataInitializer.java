package com.findold.config;

import com.findold.domain.Tag;
import com.findold.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 预设标签：80后/90后/00后、童年玩具、老零食、绝版文具等
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final TagRepository tagRepository;

    @Bean
    ApplicationRunner initTags() {
        return args -> {
            if (tagRepository.count() > 0) return;
            List<Tag> tags = List.of(
                Tag.builder().name("80后").category("era").build(),
                Tag.builder().name("90后").category("era").build(),
                Tag.builder().name("00后").category("era").build(),
                Tag.builder().name("童年玩具").category("type").build(),
                Tag.builder().name("老零食").category("type").build(),
                Tag.builder().name("绝版文具").category("type").build(),
                Tag.builder().name("国产").category("origin").build(),
                Tag.builder().name("进口").category("origin").build(),
                Tag.builder().name("街边小摊").category("channel").build(),
                Tag.builder().name("超市").category("channel").build(),
                Tag.builder().name("老物件").category("type").build()
            );
            tagRepository.saveAll(tags);
            log.info("Initialized {} preset tags", tags.size());
        };
    }
}
