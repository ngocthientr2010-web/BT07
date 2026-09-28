package com.example.BTVN.controller.api;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.BTVN.entity.Category;
import com.example.BTVN.entity.Product;
import com.example.BTVN.model.Response;
import com.example.BTVN.service.ICategoryService;
import com.example.BTVN.service.IProductService;
import com.example.BTVN.service.IStorageService;

@RestController
@RequestMapping("/api/product")
public class ProductApiController {

    @Autowired
    private IProductService productService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IStorageService storageService;


    // =========================================================
    // 1. GET ALL PRODUCT
    // GET /api/product
    // =========================================================
    @GetMapping
    public ResponseEntity<?> getAllProduct() {

        return ResponseEntity.ok(new Response(true, "Lấy danh sách sản phẩm thành công", productService.findAll()));
    }


    // =========================================================
    // 2. GET PRODUCT BY ID
    // GET /api/product/{id}
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {

        Optional<Product> product = productService.findById(id);

        if (product.isPresent()) {

            return ResponseEntity.ok(new Response(true, "Lấy sản phẩm thành công", product.get()));

        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(false, "Không tìm thấy sản phẩm có ID: " + id, null));
    }


    // =========================================================
    // 3. SEARCH PRODUCT
    // GET /api/product/search?name=ao
    // =========================================================
    @GetMapping("/search")
    public ResponseEntity<?> searchProduct(@RequestParam("name") String name) {

        return ResponseEntity.ok(new Response(true, "Tìm kiếm sản phẩm thành công", productService.findByProductNameContaining(name)));
    }


    // =========================================================
    // 4. CREATE PRODUCT
    // POST /api/product/addProduct
    // =========================================================
    @PostMapping("/addProduct")
    public ResponseEntity<?> addProduct(

            @RequestParam("productName") String productName,

            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,

            @RequestParam("unitPrice") Double unitPrice,

            @RequestParam("discount") Double discount,

            @RequestParam("description") String description,

            @RequestParam("categoryId") Long categoryId,

            @RequestParam("quantity") Integer quantity,

            @RequestParam("status") Short status) {

        // -----------------------------------------------------
        // Kiểm tra sản phẩm đã tồn tại
        // -----------------------------------------------------
        Optional<Product> existedProduct = productService.findByProductName(productName);

        if (existedProduct.isPresent()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, "Sản phẩm này đã tồn tại trong hệ thống", existedProduct.get()));
        }


        // -----------------------------------------------------
        // Kiểm tra Category
        // -----------------------------------------------------
        Optional<Category> category = categoryService.findById(categoryId);

        if (category.isEmpty()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, "Không tìm thấy Category có ID: " + categoryId, null));
        }


        // -----------------------------------------------------
        // Tạo Product
        // -----------------------------------------------------
        Product product = new Product();

        product.setProductName(productName);
        product.setUnitPrice(unitPrice);
        product.setDiscount(discount);
        product.setDescription(description);
        product.setQuantity(quantity);
        product.setStatus(status);

        product.setCreateDate(new Timestamp(System.currentTimeMillis()));


        // -----------------------------------------------------
        // Gán Category
        // -----------------------------------------------------
        product.setCategory(category.get());


        // -----------------------------------------------------
        // Upload image
        // -----------------------------------------------------
        try {

            if (imageFile != null && !imageFile.isEmpty()) {

                UUID uuid = UUID.randomUUID();

                String filename = storageService.getSorageFilename(imageFile, uuid.toString());

                product.setImages(filename);

                storageService.store(imageFile, filename);
            }

            // -------------------------------------------------
            // Save database
            // -------------------------------------------------
            Product savedProduct = productService.save(product);

            return ResponseEntity.status(HttpStatus.CREATED).body(new Response(true, "Thêm sản phẩm thành công", savedProduct));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(false, "Có lỗi xảy ra khi thêm sản phẩm", null));
        }
    }


    // =========================================================
    // 5. UPDATE PRODUCT
    // PUT /api/product/updateProduct
    // =========================================================
    @PutMapping("/updateProduct")
    public ResponseEntity<?> updateProduct(

            @RequestParam("productId") Long productId,

            @RequestParam("productName") String productName,

            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,

            @RequestParam("unitPrice") Double unitPrice,

            @RequestParam("discount") Double discount,

            @RequestParam("description") String description,

            @RequestParam("categoryId") Long categoryId,

            @RequestParam("quantity") Integer quantity,

            @RequestParam("status") Short status) {

        // -----------------------------------------------------
        // Tìm Product cần update
        // -----------------------------------------------------
        Optional<Product> optionalProduct = productService.findById(productId);

        if (optionalProduct.isEmpty()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(false, "Không tìm thấy sản phẩm có ID: " + productId, null));
        }

        Product product = optionalProduct.get();


        // -----------------------------------------------------
        // Kiểm tra Category
        // -----------------------------------------------------
        Optional<Category> category = categoryService.findById(categoryId);

        if (category.isEmpty()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, "Không tìm thấy Category có ID: " + categoryId, null));
        }


        // -----------------------------------------------------
        // Cập nhật thông tin
        // -----------------------------------------------------
        product.setProductName(productName);
        product.setUnitPrice(unitPrice);
        product.setDiscount(discount);
        product.setDescription(description);
        product.setQuantity(quantity);
        product.setStatus(status);

        product.setCategory(category.get());


        // -----------------------------------------------------
        // Nếu có upload ảnh mới
        // -----------------------------------------------------
        try {

            if (imageFile != null && !imageFile.isEmpty()) {

                UUID uuid = UUID.randomUUID();

                String filename = storageService.getSorageFilename(imageFile, uuid.toString());

                product.setImages(filename);

                storageService.store(imageFile, filename);
            }


            // -------------------------------------------------
            // Save update
            // -------------------------------------------------
            Product updatedProduct = productService.save(product);

            return ResponseEntity.ok(new Response(true, "Cập nhật sản phẩm thành công", updatedProduct));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(false, "Có lỗi xảy ra khi cập nhật sản phẩm", null));
        }
    }


    // =========================================================
    // 6. DELETE PRODUCT
    // DELETE /api/product/{id}
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {

        // -----------------------------------------------------
        // Tìm Product
        // -----------------------------------------------------
        Optional<Product> optionalProduct = productService.findById(id);

        if (optionalProduct.isEmpty()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(false, "Không tìm thấy sản phẩm có ID: " + id, null));
        }


        // -----------------------------------------------------
        // Xóa Product
        // -----------------------------------------------------
        try {

            productService.deleteById(id);

            return ResponseEntity.ok(new Response(true, "Xóa sản phẩm thành công", optionalProduct.get()));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(false, "Không thể xóa sản phẩm", null));
        }
    }
}