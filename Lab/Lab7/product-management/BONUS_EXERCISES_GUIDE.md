# Bonus Exercises Testing Guide

## 📋 Overview
This guide demonstrates the **Bonus Features** implemented for the Product Management System:
- **BONUS 1**: REST API Endpoints (8 points)
- **BONUS 2**: Image Upload (6 points)

---

## 🎯 BONUS 1: REST API Endpoints (8 points)

### Features Implemented
- ✅ `GET /api/products` - Get all products (JSON)
- ✅ `GET /api/products/{id}` - Get single product by ID
- ✅ `POST /api/products` - Create new product (Returns 201 Created)
- ✅ `PUT /api/products/{id}` - Update existing product
- ✅ `DELETE /api/products/{id}` - Delete product (Returns 204 No Content)
- ✅ `GET /api/products/category/{category}` - Get products by category
- ✅ `GET /api/products/search?keyword=` - Search products

### Testing with Browser (GET Requests Only)

#### Test 1: Get All Products
```
http://localhost:8080/api/products
```
**Expected:** JSON array of all products

#### Test 2: Get Single Product
```
http://localhost:8080/api/products/1
```
**Expected:** JSON object of product with ID 1

#### Test 3: Get Products by Category
```
http://localhost:8080/api/products/category/Electronics
```
**Expected:** JSON array of Electronics products

#### Test 4: Search Products
```
http://localhost:8080/api/products/search?keyword=laptop
```
**Expected:** JSON array of products matching "laptop"

### Testing with Thunder Client (VS Code Extension)

#### Install Thunder Client
1. Open VS Code Extensions (Ctrl+Shift+X)
2. Search for "Thunder Client"
3. Click Install
4. Open Thunder Client from sidebar

#### Test 1: GET All Products
- **Method:** GET
- **URL:** `http://localhost:8080/api/products`
- **Expected Status:** 200 OK
- **Expected Body:** JSON array of products

#### Test 2: GET Single Product
- **Method:** GET
- **URL:** `http://localhost:8080/api/products/1`
- **Expected Status:** 200 OK
- **Expected Body:** 
```json
{
  "id": 1,
  "productCode": "P001",
  "name": "Sample Product",
  "price": 99.99,
  "quantity": 10,
  "category": "Electronics",
  "description": "Description here",
  "imagePath": null,
  "createdAt": "2025-12-01T...",
  "updatedAt": null
}
```

#### Test 3: GET Non-Existent Product
- **Method:** GET
- **URL:** `http://localhost:8080/api/products/999999`
- **Expected Status:** 404 Not Found

#### Test 4: POST Create New Product
- **Method:** POST
- **URL:** `http://localhost:8080/api/products`
- **Headers:** `Content-Type: application/json`
- **Body (JSON):**
```json
{
  "productCode": "P999",
  "name": "API Test Product",
  "price": 49.99,
  "quantity": 5,
  "category": "Electronics",
  "description": "Created via API"
}
```
- **Expected Status:** 201 Created
- **Expected Body:** Created product with generated ID

#### Test 5: PUT Update Product
- **Method:** PUT
- **URL:** `http://localhost:8080/api/products/1`
- **Headers:** `Content-Type: application/json`
- **Body (JSON):**
```json
{
  "productCode": "P001",
  "name": "Updated Product Name",
  "price": 129.99,
  "quantity": 15,
  "category": "Electronics",
  "description": "Updated via API"
}
```
- **Expected Status:** 200 OK
- **Expected Body:** Updated product

#### Test 6: PUT Update Non-Existent Product
- **Method:** PUT
- **URL:** `http://localhost:8080/api/products/999999`
- **Body:** (any valid product JSON)
- **Expected Status:** 404 Not Found

#### Test 7: DELETE Product
- **Method:** DELETE
- **URL:** `http://localhost:8080/api/products/1`
- **Expected Status:** 204 No Content
- **Expected Body:** Empty

#### Test 8: DELETE Non-Existent Product
- **Method:** DELETE
- **URL:** `http://localhost:8080/api/products/999999`
- **Expected Status:** 404 Not Found

#### Test 9: POST with Validation Errors
- **Method:** POST
- **URL:** `http://localhost:8080/api/products`
- **Headers:** `Content-Type: application/json`
- **Body (Invalid):**
```json
{
  "productCode": "P1",
  "name": "AB",
  "price": -10,
  "quantity": -5,
  "category": ""
}
```
- **Expected Status:** 400 Bad Request
- **Expected Body:** Validation error messages

### Testing with Postman

Same tests as Thunder Client, but using Postman interface:
1. Download Postman from https://www.postman.com/downloads/
2. Create new collection "Product Management API"
3. Add requests for each endpoint
4. Use Postman features: environments, tests, collections

### Testing with cURL (Terminal)

#### GET All Products
```bash
curl http://localhost:8080/api/products
```

#### GET Single Product
```bash
curl http://localhost:8080/api/products/1
```

#### POST Create Product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "productCode": "P888",
    "name": "cURL Test Product",
    "price": 39.99,
    "quantity": 8,
    "category": "Books",
    "description": "Created with cURL"
  }'
```

#### PUT Update Product
```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "productCode": "P001",
    "name": "Updated via cURL",
    "price": 149.99,
    "quantity": 20,
    "category": "Electronics",
    "description": "Updated"
  }'
```

#### DELETE Product
```bash
curl -X DELETE http://localhost:8080/api/products/1
```

---

## 🎯 BONUS 2: Image Upload (6 points)

### Features Implemented
- ✅ `imagePath` field added to Product entity
- ✅ File upload handling with `MultipartFile`
- ✅ Images stored in `uploads/products/` directory
- ✅ Unique filename generation (UUID)
- ✅ Image preview in form before upload
- ✅ Images displayed in product list
- ✅ Current image shown when editing

### Directory Structure
```
product-management/
├── uploads/
│   └── products/
│       ├── abc123-def456-ghi789.jpg
│       ├── xyz789-uvw456-rst123.png
│       └── ...
```

### Testing Image Upload

#### Test 1: Add Product with Image

**Steps:**
1. Navigate to `http://localhost:8080/products`
2. Click **"➕ Add New Product"**
3. Fill in product details:
   - Code: P100
   - Name: Test Product with Image
   - Price: 29.99
   - Quantity: 5
   - Category: Electronics
4. Click **"Choose File"** under "Product Image"
5. Select an image file (JPG, PNG, GIF)
6. Observe **image preview** appears below file input
7. Click **"💾 Save Product"**

**Expected Results:**
- ✅ Product saved successfully
- ✅ Image uploaded to `uploads/products/` directory
- ✅ Unique filename generated (UUID)
- ✅ Image displayed in product list

**Verification:**
1. Check product list - image appears in "Image" column
2. Check `uploads/products/` folder - file exists
3. Filename format: `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx.jpg`

#### Test 2: Add Product Without Image

**Steps:**
1. Add new product
2. Fill in all required fields
3. **Do not** select an image
4. Click **"💾 Save Product"**

**Expected Results:**
- ✅ Product saved successfully
- ✅ No image uploaded
- ✅ Product list shows "No image" text
- ✅ No errors

#### Test 3: Edit Product and Add Image

**Steps:**
1. Edit an existing product (without image)
2. Click **"Choose File"**
3. Select an image
4. Preview appears
5. Click **"💾 Save Product"**

**Expected Results:**
- ✅ Product updated
- ✅ Image added
- ✅ Image displayed in list

#### Test 4: Edit Product and Change Image

**Steps:**
1. Edit a product that already has an image
2. Observe current image displayed
3. Select a **different** image file
4. Preview shows new image
5. Click **"💾 Save Product"**

**Expected Results:**
- ✅ Product updated
- ✅ New image saved
- ✅ Old image replaced
- ✅ New image displayed in list

#### Test 5: Image Preview Functionality

**Steps:**
1. Go to add/edit product form
2. Click **"Choose File"**
3. Select an image

**Expected Results:**
- ✅ Preview appears immediately
- ✅ Preview shows selected image
- ✅ Preview has proper styling (max 200px width, rounded corners)

**Steps to Cancel:**
1. Clear file input or select different file
2. Preview updates accordingly

#### Test 6: Image Display in Product List

**Steps:**
1. Go to product list
2. Find products with images

**Expected Results:**
- ✅ Images displayed in "Image" column
- ✅ Thumbnail size: 50x50px
- ✅ Object-fit: cover (no distortion)
- ✅ Rounded corners
- ✅ Products without images show "No image" text

#### Test 7: Supported Image Formats

**Test with:**
- ✅ JPEG/JPG files
- ✅ PNG files
- ✅ GIF files
- ✅ WebP files (if browser supports)

**Expected:** All formats upload and display correctly

#### Test 8: Large Image Upload

**Steps:**
1. Upload a large image (e.g., 5MB, 4000x3000px)
2. Save product

**Expected Results:**
- ✅ Image uploads successfully
- ✅ Browser may resize for display, but original saved
- ✅ Thumbnail in list scales properly

#### Test 9: Special Characters in Filename

**Steps:**
1. Select image with special characters: `My Photo (2024) #1.jpg`
2. Upload

**Expected Results:**
- ✅ Upload successful
- ✅ UUID filename generated (no special characters)
- ✅ Original extension preserved

### Image Storage Verification

#### Check Uploaded Files
1. Navigate to project root directory
2. Open `uploads/products/` folder
3. Verify files exist with UUID names
4. Example: `a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg`

#### Check Database
```sql
SELECT id, product_code, name, image_path FROM products WHERE image_path IS NOT NULL;
```

**Expected:** Records show `/uploads/products/uuid-filename.ext`

### Image Display Verification

#### Check Browser Console
1. Open product list
2. Open browser DevTools (F12)
3. Check **Console** tab for errors
4. Check **Network** tab - images load with status 200

#### Check Image URLs
Right-click on product image → "Open Image in New Tab"
- **Expected URL:** `http://localhost:8080/uploads/products/uuid-filename.jpg`
- **Expected:** Image displays full-size

### Troubleshooting

#### Problem: Images Don't Display
**Solutions:**
1. Check `WebConfig.java` exists and configured
2. Verify `uploads/products/` directory exists
3. Check file permissions
4. Restart application
5. Clear browser cache

#### Problem: Upload Fails
**Solutions:**
1. Check form has `enctype="multipart/form-data"`
2. Verify controller parameter: `@RequestParam MultipartFile imageFile`
3. Check file size limits (Spring default: 1MB)
4. Check disk space

#### Problem: Preview Not Working
**Solutions:**
1. Check JavaScript function: `previewImage(event)`
2. Verify `onchange="previewImage(event)"` on file input
3. Check browser console for JS errors
4. Test in different browser

---

## ✅ Acceptance Criteria

### BONUS 1: REST API (8 points)
- [ ] All 5 CRUD endpoints implemented
- [ ] Proper HTTP status codes:
  - 200 OK for successful GET/PUT
  - 201 Created for POST
  - 204 No Content for DELETE
  - 404 Not Found for missing resources
  - 400 Bad Request for validation errors
- [ ] JSON request/response bodies
- [ ] Validation enforced (@Valid)
- [ ] Optional endpoints work (category, search)
- [ ] Tested with Thunder Client/Postman
- [ ] All tests pass

### BONUS 2: Image Upload (6 points)
- [ ] `imagePath` field in Product entity
- [ ] File upload handling in controller
- [ ] Images saved to `uploads/products/`
- [ ] Unique filenames generated
- [ ] Images displayed in product list
- [ ] Image preview works in form
- [ ] Current image shown when editing
- [ ] Form validation still works
- [ ] Products work with/without images
- [ ] Multiple image formats supported

---

## 🎓 Demonstration Script

### Quick Demo (10 minutes)

#### Part 1: REST API (5 min)
1. **Open Thunder Client**
2. **GET all products:** `http://localhost:8080/api/products`
   - "See JSON response with all products"
3. **POST create product:** Send JSON body
   - "Product created, status 201"
4. **PUT update:** Modify product
   - "Product updated, status 200"
5. **DELETE:** Remove product
   - "Product deleted, status 204"
6. **GET deleted:** Try to fetch
   - "404 Not Found - resource gone"

#### Part 2: Image Upload (5 min)
1. **Add product with image**
   - Fill form, select image file
   - "Preview appears instantly"
   - Save
   - "Product list shows thumbnail"
2. **Edit product**
   - Show current image
   - Change to different image
   - "New image replaces old"
3. **View image**
   - Right-click → Open in new tab
   - "Full-size image accessible"
4. **Check file system**
   - Show `uploads/products/` folder
   - "UUID filenames for security"

---

## 🚀 Advanced Testing

### API Integration Test
Create a script to:
1. POST create 10 products
2. GET all products
3. PUT update each product
4. DELETE all products
5. Verify counts at each step

### Image Stress Test
1. Upload 50 products with images
2. Verify all display correctly
3. Check disk usage
4. Test pagination performance

### Concurrent Upload Test
1. Open 5 browser tabs
2. Upload images simultaneously
3. Verify no conflicts
4. Check all images saved

---

## 📚 API Documentation

### Endpoint Summary

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| GET | `/api/products` | Get all products | 200 |
| GET | `/api/products/{id}` | Get product by ID | 200, 404 |
| POST | `/api/products` | Create product | 201, 400 |
| PUT | `/api/products/{id}` | Update product | 200, 400, 404 |
| DELETE | `/api/products/{id}` | Delete product | 204, 404 |
| GET | `/api/products/category/{category}` | Get by category | 200 |
| GET | `/api/products/search?keyword=` | Search products | 200 |

### Example Responses

#### Success Response (GET /api/products/1)
```json
{
  "id": 1,
  "productCode": "P001",
  "name": "Laptop",
  "price": 999.99,
  "quantity": 10,
  "category": "Electronics",
  "description": "High-performance laptop",
  "imagePath": "/uploads/products/abc-123.jpg",
  "createdAt": "2025-12-01T10:00:00",
  "updatedAt": null
}
```

#### Error Response (404 Not Found)
```
Status: 404 Not Found
Body: (empty)
```

#### Validation Error (400 Bad Request)
```json
{
  "timestamp": "2025-12-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "price",
      "message": "Price must be greater than 0"
    }
  ]
}
```

---

**Last Updated:** December 1, 2025  
**Bonus Exercises:** REST API (8 pts) + Image Upload (6 pts)  
**Total Bonus Points:** 14 points  
**Status:** ✅ Implementation Complete
