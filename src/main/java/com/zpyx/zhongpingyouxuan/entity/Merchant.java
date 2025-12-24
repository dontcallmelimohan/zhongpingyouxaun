package com.zpyx.zhongpingyouxuan.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "merchants")
@Data
@NoArgsConstructor
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private String address;

    @Column(name = "province", length = 50)
    private String province;
    
    @Column(name = "city", length = 50)
    private String city;
    
    @Column(name = "area", length = 50)
    private String area;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User owner;
}