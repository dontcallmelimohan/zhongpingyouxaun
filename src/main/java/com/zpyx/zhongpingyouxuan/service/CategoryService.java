package com.zpyx.zhongpingyouxuan.service;

import com.zpyx.zhongpingyouxuan.entity.Category;
import java.util.List;

public interface CategoryService {
    
    Category createCategory(String name);
    
    List<Category> getAllCategories();
    
    Category getCategoryById(Long id);
    
    Category updateCategory(Long id, String name);
    
    void deleteCategory(Long id);
}