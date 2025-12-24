package com.zpyx.zhongpingyouxuan.repository;

import com.zpyx.zhongpingyouxuan.entity.ProductFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProductFavoriteRepository extends JpaRepository<ProductFavorite, Long> {

    
    Optional<ProductFavorite> findByUserIdAndProductId(Long userId, Long productId);
    
    
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    
    void deleteByUserIdAndProductId(Long userId, Long productId);
    
    
    long countByProductId(Long productId);
    
    
    Page<ProductFavorite> findByUserId(Long userId, Pageable pageable);
}