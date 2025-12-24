package com.zpyx.zhongpingyouxuan.service.impl;

import com.zpyx.zhongpingyouxuan.entity.Category;
import com.zpyx.zhongpingyouxuan.repository.CategoryRepository;
import com.zpyx.zhongpingyouxuan.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Override
    @Transactional
    public Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }
    
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found with id: " + id));
    }
    
    @Override
    @Transactional
    public Category updateCategory(Long id, String name) {
        Category category = getCategoryById(id);
        category.setName(name);
        return categoryRepository.save(category);
    }
    
    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}