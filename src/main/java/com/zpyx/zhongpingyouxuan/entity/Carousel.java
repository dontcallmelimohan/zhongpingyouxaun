package com.zpyx.zhongpingyouxuan.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "carousels")
@Data
@NoArgsConstructor
public class Carousel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "target_url", length = 255)
    private String targetUrl;

    @Column(nullable = false)
    private Integer displayOrder;
}