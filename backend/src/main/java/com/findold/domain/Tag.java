package com.findold.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tag", indexes = @Index(columnList = "category"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String name;

    @Column(length = 32)
    private String category;
}
