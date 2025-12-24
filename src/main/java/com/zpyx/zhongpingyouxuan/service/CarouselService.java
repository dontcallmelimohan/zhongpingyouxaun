package com.zpyx.zhongpingyouxuan.service;

import com.zpyx.zhongpingyouxuan.dto.request.CarouselUpdateRequest;
import com.zpyx.zhongpingyouxuan.entity.Carousel;
import java.util.List;

public interface CarouselService {
    List<Carousel> findActiveCarousels();
    
    
    List<Carousel> getAllCarousels();
    Carousel createCarousel(Carousel carousel);
    Carousel updateCarousel(Long id, CarouselUpdateRequest carouselUpdateRequest);
    void deleteCarousel(Long id);
}