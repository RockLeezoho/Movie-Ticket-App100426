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

![Màn hình đăng nhập](docs/screenshots/login.jpg)

### 2. Màn hình đăng ký

![Màn hình đăng ký](docs/screenshots/register.jpg)

### 3. Danh sách phim

![Danh sách phim](docs/screenshots/movies.jpg)

### 4. Danh sách lịch chiếu

![Danh sách phim](docs/screenshots/showtimes.jpg)

### 5. Danh sách phim

![Danh sách phim](docs/screenshots/theaters.jpg)

### 6. Chi tiết phim

![Chi tiết phim](docs/screenshots/movie-details.jpg)

### 7. Chọn ghế và thanh toán
![Chọn ghế và thanh toán](docs/screenshots/select-seat.jpeg)

![Chọn ghế và thanh toán](docs/screenshots/payment.jpg)

### 8. Chi tiết giao dịch

![Chi tiết giao dịch](docs/screenshots/payment-detail.jpg)

### 9. Chi tiết vé

![Chi tiết vé](docs/screenshots/ticket-detail.jpg)

### 10. Thông báo nhắc giờ chiếu

![Thông báo nhắc giờ chiếu](docs/screenshots/ticket-notification.jpg)

## Cấu hình Firebase

- Project hiện đang dùng Firebase Realtime Database và Firebase Auth.
- File cấu hình: `app/google-services.json`
- URL Realtime Database được khai báo trong app để trỏ đúng instance hiện tại.

## Chạy ứng dụng

```bash
./gradlew :app:assembleDebug
```

Sau khi build xong, cài APK debug lên máy/emulator để kiểm tra giao diện và luồng đặt vé.
