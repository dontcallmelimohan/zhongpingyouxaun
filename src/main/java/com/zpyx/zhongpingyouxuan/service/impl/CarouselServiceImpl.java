package com.zpyx.zhongpingyouxuan.service.impl;

import com.zpyx.zhongpingyouxuan.dto.request.CarouselUpdateRequest;
import com.zpyx.zhongpingyouxuan.entity.Carousel;
import com.zpyx.zhongpingyouxuan.exception.ResourceNotFoundException;
import com.zpyx.zhongpingyouxuan.repository.CarouselRepository;
import com.zpyx.zhongpingyouxuan.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarouselServiceImpl implements CarouselService {

    @Autowired
    private CarouselRepository carouselRepository;

    @Override
    public List<Carousel> findActiveCarousels() {
        return carouselRepository.findAllByOrderByDisplayOrderAsc();
    }
    
    
    @Override
    public List<Carousel> getAllCarousels() {
        return carouselRepository.findAllByOrderByDisplayOrderAsc();
    }
    
    @Override
    public Carousel createCarousel(Carousel carousel) {
        return carouselRepository.save(carousel);
    }
    
    @Override
    public Carousel updateCarousel(Long id, CarouselUpdateRequest carouselUpdateRequest) {
        Carousel carousel = carouselRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carousel", "id", id));
        
        
        if (carouselUpdateRequest.getImageUrl() != null) {
            carousel.setImageUrl(carouselUpdateRequest.getImageUrl());
        }
        if (carouselUpdateRequest.getTargetUrl() != null) {
            carousel.setTargetUrl(carouselUpdateRequest.getTargetUrl());
        }
        if (carouselUpdateRequest.getDisplayOrder() != null) {
            carousel.setDisplayOrder(carouselUpdateRequest.getDisplayOrder());
        }
        
        return carouselRepository.save(carousel);
    }
    
    @Override
    public void deleteCarousel(Long id) {
        Carousel carousel = carouselRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carousel", "id", id));
        carouselRepository.delete(carousel);
    }
}