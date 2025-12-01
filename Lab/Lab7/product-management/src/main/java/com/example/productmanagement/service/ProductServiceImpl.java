package com.example.productmanagement.service;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getAllProducts(Sort sort) {
        return productRepository.findAll(sort);
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product saveProduct(Product product) {
        // Validation logic can go here
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContaining(keyword);
    }

    @Override
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContaining(keyword, pageable);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> getAllProductByAdvSearch(String name, String category, BigDecimal minPrice,
            BigDecimal maxPrice) {
        return productRepository.searchProducts(name, category, minPrice, maxPrice);
    }

    @Override
    public Page<Product> getAllProductByAdvSearch(String name, String category, BigDecimal minPrice,
            BigDecimal maxPrice, Pageable pageable) {
        return productRepository.searchProducts(name, category, minPrice, maxPrice, pageable);
    }

    @Override
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }

    // Statistics methods implementation (Task 8.1)
    @Override
    public long getTotalProductCount() {
        return productRepository.count();
    }

    @Override
    public long countByCategory(String category) {
        return productRepository.countByCategory(category);
    }

    @Override
    public BigDecimal calculateTotalValue() {
        BigDecimal total = productRepository.calculateTotalValue();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateAveragePrice() {
        BigDecimal average = productRepository.calculateAveragePrice();
        return average != null ? average : BigDecimal.ZERO;
    }

    @Override
    public List<Product> findLowStockProducts(int threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    @Override
    public List<Product> getRecentProducts(int limit) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return productRepository.findAll(sort).stream().limit(limit).toList();
    }

}
