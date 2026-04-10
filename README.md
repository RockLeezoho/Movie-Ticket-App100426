# Movie Ticket App

Ứng dụng đặt vé xem phim Android với giao diện tiếng Việt, tích hợp Firebase Auth, Realtime Database, lịch sử thanh toán, chi tiết vé và nhắc lịch chiếu.

## Tính năng chính

- Đăng ký, đăng nhập, đăng xuất
- Xem danh sách phim, rạp, suất chiếu
- Chọn ghế và đặt vé
- Thanh toán sandbox
- Lưu dữ liệu lên Firebase Realtime Database
- Xem lịch sử thanh toán và chi tiết vé
- Thông báo xác nhận đặt vé và nhắc giờ chiếu

## Ảnh giao diện

Bạn có thể chèn ảnh vào thư mục `docs/screenshots/` rồi cập nhật đường dẫn bên dưới.

### 1. Màn hình đăng nhập

<img src="docs/screenshots/login.jpg" alt="Màn hình đăng nhập" width="260" />

### 2. Màn hình đăng ký

<img src="docs/screenshots/register.jpg" alt="Màn hình đăng ký" width="260" />

### 3. Danh sách phim

<img src="docs/screenshots/movies.jpg" alt="Danh sách phim" width="260" />

### 4. Danh sách lịch chiếu

<img src="docs/screenshots/showtimes.jpg" alt="Danh sách lịch chiếu" width="260" />

### 5. Danh sách phim

<img src="docs/screenshots/theaters.jpg" alt="Danh sách rạp" width="260" />

### 6. Chi tiết phim

<img src="docs/screenshots/movie-detail.jpg" alt="Chi tiết phim" width="260" />

### 7. Chọn ghế và thanh toán
<img src="docs/screenshots/select-seat.jpeg" alt="Chọn ghế" width="260" />

<img src="docs/screenshots/payment.jpg" alt="Thanh toán" width="260" />

### 9. Chi tiết vé

<img src="docs/screenshots/ticket-detail.jpg" alt="Chi tiết vé" width="260" />

### 10. Thông báo nhắc giờ chiếu

<img src="docs/screenshots/ticket-notification.jpg" alt="Thông báo nhắc giờ chiếu" width="260" />

## Cấu hình Firebase

- Project hiện đang dùng Firebase Realtime Database và Firebase Auth.
- File cấu hình: `app/google-services.json`
- URL Realtime Database được khai báo trong app để trỏ đúng instance hiện tại.

## Chạy ứng dụng

```bash
./gradlew :app:assembleDebug
```

Sau khi build xong, cài APK debug lên máy/emulator để kiểm tra giao diện và luồng đặt vé.
