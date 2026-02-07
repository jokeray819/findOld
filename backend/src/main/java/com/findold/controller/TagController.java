package com.findold.controller;

import com.findold.dto.tag.TagDto;
import com.findold.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagRepository tagRepository;

    @GetMapping
    public List<TagDto> list(@RequestParam(required = false) String category) {
        if (category != null && !category.isBlank()) {
            return tagRepository.findByCategory(category).stream()
                .map(t -> TagDto.builder().id(t.getId()).name(t.getName()).category(t.getCategory()).build())
                .collect(Collectors.toList());
        }
        return tagRepository.findAll().stream()
            .map(t -> TagDto.builder().id(t.getId()).name(t.getName()).category(t.getCategory()).build())
            .collect(Collectors.toList());
    }
}
