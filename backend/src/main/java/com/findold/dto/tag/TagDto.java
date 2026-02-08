package com.findold.dto.tag;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagDto {

    private Long id;
    private String name;
    private String category;
}
