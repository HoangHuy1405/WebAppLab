package com.example.productmanagement.service;

import com.example.productmanagement.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> getAllProducts();

    List<Product> getAllProducts(Sort sort);

    Optional<Product> getProductById(Long id);

    Product saveProduct(Product product);

    void deleteProduct(Long id);

    List<Product> searchProducts(String keyword);

    Page<Product> searchProducts(String keyword, Pageable pageable);

    List<Product> getProductsByCategory(String category);

    List<Product> getAllProductByAdvSearch(String name, String category, BigDecimal minPrice, BigDecimal maxPrice);

    Page<Product> getAllProductByAdvSearch(String name, String category, BigDecimal minPrice, BigDecimal maxPrice,
            Pageable pageable);

    List<String> getAllCategories();

    // Statistics methods for dashboard (Task 8.1)
    long getTotalProductCount();

    long countByCategory(String category);

    BigDecimal calculateTotalValue();

    BigDecimal calculateAveragePrice();

    List<Product> findLowStockProducts(int threshold);

    List<Product> getRecentProducts(int limit);
}
