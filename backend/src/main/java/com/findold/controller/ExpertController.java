package com.findold.controller;

import com.findold.dto.expert.ExpertListItem;
import com.findold.dto.expert.ExpertMeResponse;
import com.findold.service.ExpertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/experts")
@RequiredArgsConstructor
public class ExpertController {

    private final ExpertService expertService;

    @GetMapping
    public Page<ExpertListItem> list(
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "validHelpCount"));
        return expertService.list(status, pageable);
    }

    @PostMapping("/apply")
    public void apply() {
        expertService.applyCertification();
    }

    @GetMapping("/me")
    public ExpertMeResponse getMe() {
        return expertService.getMe();
    }
}
