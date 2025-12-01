package com.example.productmanagement.controller;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String showDashboard(Model model) {
        // Total products count
        long totalProducts = productService.getTotalProductCount();
        model.addAttribute("totalProducts", totalProducts);

        // Products by category
        List<String> categories = productService.getAllCategories();
        Map<String, Long> categoryStats = new HashMap<>();
        for (String category : categories) {
            long count = productService.countByCategory(category);
            categoryStats.put(category, count);
        }
        model.addAttribute("categoryStats", categoryStats);
        model.addAttribute("categories", categories);

        // Total inventory value
        BigDecimal totalValue = productService.calculateTotalValue();
        model.addAttribute("totalValue", totalValue);

        // Average product price
        BigDecimal averagePrice = productService.calculateAveragePrice();
        model.addAttribute("averagePrice", averagePrice);

        // Low stock alerts (quantity < 10)
        List<Product> lowStockProducts = productService.findLowStockProducts(10);
        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("lowStockCount", lowStockProducts.size());

        // Recent products (last 5 added)
        List<Product> recentProducts = productService.getRecentProducts(5);
        model.addAttribute("recentProducts", recentProducts);

        return "dashboard";
    }
}
