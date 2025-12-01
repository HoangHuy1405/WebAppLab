package com.example.productmanagement.controller;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String category,
            Model model) {

        List<Product> products;

        if (category != null && !category.trim().isEmpty()) {
            products = productService.getProductsByCategory(category.trim());
        } else {
            products = productService.getAllProducts();
        }

        if (sortBy != null && !sortBy.trim().isEmpty()) {
            Sort sort = sortDir.equals("asc")
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
            // combine sorting with cateogory
            if (category != null && !category.trim().isEmpty()) {
                products = productService.getAllProducts(sort).stream()
                        .filter(p -> p.getCategory().equals(category.trim()))
                        .toList();
            } else {
                products = productService.getAllProducts(sort);
            }
        }

        List<String> categories = productService.getAllCategories();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("selectedCategory", category);

        return "product-list";
    }

    // Show form for new product
    @GetMapping("/new")
    public String showNewForm(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        return "product-form";
    }

    // Show form for editing product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return productService.getProductById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "product-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Product not found");
                    return "redirect:/products";
                });
    }

    // Save product (create or update) with image upload
    @PostMapping("/save")
    public String saveProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "product-form";
        }

        try {
            // Handle image upload if file is provided
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = saveImageFile(imageFile);
                product.setImagePath(imagePath);
            }

            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("message",
                    product.getId() == null ? "Product added successfully!" : "Product updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving product: " + e.getMessage());
        }
        return "redirect:/products";
    }

    // Helper method to save image file
    private String saveImageFile(MultipartFile file) throws IOException {
        // Create uploads directory if it doesn't exist
        String uploadDir = "uploads/products";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Save file
        Path filePath = Paths.get(uploadDir, uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return relative path for database
        return "/uploads/products/" + uniqueFilename;
    }

    // Delete product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/products";
    }

    // Search products with pagination
    @GetMapping("/search")
    public String searchProducts(
            @RequestParam("keyword") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.searchProducts(keyword, pageable);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("keyword", keyword);

        return "product-list";
    }

    @GetMapping("/advanced-search")
    public String advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        List<String> categories = productService.getAllCategories();
        model.addAttribute("categories", categories);

        String searchName = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
        String searchCategory = (category != null && !category.trim().isEmpty()) ? category.trim() : null;

        // Validate minPrice < maxPrice
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            model.addAttribute("error",
                    "Min price cannot be greater than max price. Please adjust your search criteria.");
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("searchName", searchName);
            model.addAttribute("searchCategory", searchCategory);
            model.addAttribute("searchMinPrice", minPrice);
            model.addAttribute("searchMaxPrice", maxPrice);
            return "product-list";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.getAllProductByAdvSearch(searchName, searchCategory, minPrice,
                maxPrice, pageable);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("searchName", searchName);
        model.addAttribute("searchCategory", searchCategory);
        model.addAttribute("searchMinPrice", minPrice);
        model.addAttribute("searchMaxPrice", maxPrice);

        return "product-list";
    }
}
