package com.zpyx.zhongpingyouxuan.repository;

import com.zpyx.zhongpingyouxuan.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    
    Optional<Merchant> findByOwnerId(Long ownerId);
    
    
    Optional<Merchant> findByOwner_Id(Long ownerId);
    
    
    boolean existsByOwnerId(Long ownerId);
    
    
    boolean existsByOwnerIdAndIdNot(Long ownerId, Long id);
}