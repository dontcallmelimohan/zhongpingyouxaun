package com.zpyx.zhongpingyouxuan.controller;

import com.zpyx.zhongpingyouxuan.entity.Carousel;
import com.zpyx.zhongpingyouxuan.service.CarouselService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/carousels")
    public ResponseEntity<List<Carousel>> getCarousels() {
        return ResponseEntity.ok(carouselService.findActiveCarousels());
    }
    
    // 获取地区数据
    @GetMapping("/regions")
    public ResponseEntity<JsonNode> getRegions() throws IOException {
        ClassPathResource resource = new ClassPathResource("ChinaCitys.json");
        JsonNode regions = objectMapper.readTree(resource.getInputStream());
        return ResponseEntity.ok(regions);
    }
}