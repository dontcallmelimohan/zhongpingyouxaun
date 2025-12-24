package com.zpyx.zhongpingyouxuan.repository;

import com.zpyx.zhongpingyouxuan.entity.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    
    
    Optional<ProductLike> findByUserIdAndProductId(Long userId, Long productId);
    
    
    long countByProductId(Long productId);
    
    
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    
    void deleteByUserIdAndProductId(Long userId, Long productId);
}