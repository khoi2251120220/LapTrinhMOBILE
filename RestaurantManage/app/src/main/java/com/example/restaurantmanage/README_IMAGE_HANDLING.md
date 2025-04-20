# Hướng dẫn quản lý hình ảnh trong ứng dụng Restaurant Management

Tài liệu này mô tả cách quản lý và hiển thị hình ảnh trong ứng dụng quản lý nhà hàng.

## Cách lưu trữ hình ảnh

Ứng dụng hỗ trợ 3 cách lưu trữ hình ảnh:

1. **Hình ảnh từ thư mục drawable**: Đơn giản nhất, sử dụng tên file làm giá trị trường `image`
2. **Hình ảnh từ bộ nhớ trong ứng dụng**: Lưu đường dẫn đầy đủ đến file trong bộ nhớ trong
3. **Hình ảnh từ URL**: Lưu đường dẫn URL internet (cần kết nối mạng)

## Quy trình quản lý hình ảnh

### 1. Thêm hình ảnh mới

Để thêm hình ảnh cho một món ăn hoặc bàn, chúng ta sử dụng các bước sau:

1. Người dùng nhấn vào vùng chọn hình ảnh (ImagePicker)
2. Ứng dụng yêu cầu quyền truy cập bộ nhớ (lần đầu tiên)
3. Người dùng chọn hình ảnh từ thư viện
4. Hình ảnh được sao chép vào thư mục riêng của ứng dụng (`menu_images`)
5. Đường dẫn đến hình ảnh được lưu vào cơ sở dữ liệu

### 2. Hiển thị hình ảnh

Khi hiển thị một món ăn hoặc bàn, hệ thống sẽ:

1. Đọc đường dẫn hình ảnh từ database
2. Kiểm tra loại đường dẫn (đường dẫn tệp, tên drawable hoặc URL)
3. Tải và hiển thị hình ảnh phù hợp

## Cấu trúc thư mục lưu trữ

```
[Bộ nhớ trong ứng dụng]
└── files/
    └── menu_images/
        ├── [UUID1].jpg
        ├── [UUID2].jpg
        └── ...
```

## Classes và thành phần liên quan

1. **ImageUtils**: Chứa các hàm tiện ích để lưu, tải và xoá hình ảnh
2. **ImagePicker**: Component hiển thị UI cho người dùng chọn hình ảnh
3. **AsyncImage**: Component hiển thị hình ảnh từ các nguồn khác nhau

## Cách sử dụng

### Trong ViewModel

```kotlin
// Thêm món ăn mới với hình ảnh
fun addMenuItem(name: String, price: Double, categoryId: String, imagePath: String?, description: String = "") {
    // imagePath là đường dẫn đến hình ảnh đã lưu trong bộ nhớ ứng dụng
}

// Cập nhật món ăn với hình ảnh mới
fun updateMenuItem(id: String, name: String, price: Double, category: String? = null, imagePath: String? = null, description: String? = null) {
    // Nếu imagePath != null, hình ảnh sẽ được cập nhật
}
```

### Trong UI

```kotlin
// Component chọn hình ảnh
ImagePicker(
    currentImagePath = imagePath,
    onImageSelected = { path ->
        imagePath = path ?: ""
    }
)

// Hiển thị hình ảnh từ đường dẫn
AsyncImage(
    model = imageModel, // Đã xử lý để hỗ trợ nhiều loại đường dẫn
    contentDescription = "Image description",
    modifier = Modifier.fillMaxSize(),
    contentScale = ContentScale.Crop
)
```

## Xử lý quyền

Ứng dụng yêu cầu các quyền sau để quản lý hình ảnh:

- `READ_EXTERNAL_STORAGE` hoặc `READ_MEDIA_IMAGES` (từ Android 13): Để chọn hình ảnh từ thư viện
- `WRITE_EXTERNAL_STORAGE` (cho Android dưới 10): Để lưu hình ảnh

## Lưu ý

- Đối với ứng dụng production, có thể cân nhắc sử dụng Firebase Storage để lưu trữ ảnh
- Nên tối ưu hóa kích thước ảnh trước khi lưu vào bộ nhớ
- Đổi tên trường `image` trong database thành `imagePath` trong các bản cập nhật tương lai để rõ ràng hơn 