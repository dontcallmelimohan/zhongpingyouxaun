package com.zpyx.zhongpingyouxuan.repository;

import com.zpyx.zhongpingyouxuan.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByMerchantId(Long merchantId, Pageable pageable);

    Page<Product> findByMerchantProvince(String province, Pageable pageable);
    
    Page<Product> findByMerchantProvinceAndMerchantCity(String province, String city, Pageable pageable);
    
    Page<Product> findByMerchantProvinceAndMerchantCityAndMerchantArea(String province, String city, String area, Pageable pageable);
}