package com.firstspring.firststore.controllers;

import java.io.InputStream;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.firstspring.firststore.models.Product;
import com.firstspring.firststore.models.ProductDto;
import com.firstspring.firststore.services.ProductRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/products")
public class ProductsControllers {

    @Autowired
    private ProductRepository repo;

    @GetMapping({ "", "/" })
    public String showProductList(Model model) {
        List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/CreateProduct";
    }

    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result) {

        if (productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "The image file is required"));
        }

        if (result.hasErrors()) {
            return "products/CreateProduct";
        }

        // Save image file
        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try {
            // Absolute path to the directory
            String uploadDir = "D:/SpringProject/firststore/firststore/src/main/resources/static/public/images/";
            Path uploadPath = Paths.get(uploadDir);

            // Check if the directory exists
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Copy the file to the target directory
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createdAt);
        product.setImageFileName(storageFileName);

        repo.save(product);

        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditPage(Model model, @PathVariable int id) {
        try {
            Product product = repo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());
            model.addAttribute("product", product);
            model.addAttribute("productDto", productDto);
            return "products/EditProduct";
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/products";
        }
    }

    ///
    @PostMapping("/edit")
    public String updateProduct(
            Model model,
            @RequestParam int id,
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result) {
        try {
            Product product = repo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
            model.addAttribute("product", product);

            if (result.hasErrors()) {
                return "products/EditProduct";
            }

            if (!productDto.getImageFile().isEmpty()) {
                // Delete old image
                String uploadDir = "D:/SpringProject/firststore/firststore/src/main/resources/static/public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());

                try {
                    Files.delete(oldImagePath);
                } catch (Exception ex) {
                    System.out.println("Exception: " + ex.getMessage());
                }

                // Save new image file
                MultipartFile image = productDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }

                product.setImageFileName(storageFileName);
            }

            // Update product details
            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());

            // Save updated product
            repo.save(product);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/products"; // Redirect to products page in case of an error
        }

        return "redirect:/products"; // Redirect to products page after successful update
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id, Model model) {
        try {
            Product product = repo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

            // delete product image
            Path imagePath = Paths.get("D:/SpringProject/firststore/firststore/src/main/resources/static/public/images/"
                    + product.getImageFileName());

            try {
                Files.delete(imagePath);
            } catch (Exception ex) {
                System.out.println("Failed to delete image: " + ex.getMessage());
            }

            // delete the product
            repo.delete(product);
            model.addAttribute("message", "Product deleted successfully");

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            model.addAttribute("message", "Failed to delete product");
        }

        return "redirect:/products";
    }
}