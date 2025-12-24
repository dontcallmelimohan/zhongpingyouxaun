package com.zpyx.zhongpingyouxuan.repository;

import com.zpyx.zhongpingyouxuan.entity.Carousel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarouselRepository extends JpaRepository<Carousel, Long> {

    List<Carousel> findAllByOrderByDisplayOrderAsc();
}