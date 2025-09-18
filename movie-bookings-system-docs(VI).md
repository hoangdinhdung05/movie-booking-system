# 📖 Hệ Thống Đặt Vé Xem Phim - Tài Liệu

---

## 1. Tổng Quan
Hệ thống **Đặt Vé Xem Phim** được thiết kế để hỗ trợ toàn bộ hoạt động của rạp chiếu phim: từ xác thực người dùng, phân quyền (RBAC), quản lý phim & rạp, đặt chỗ, khuyến mãi, thanh toán, gửi thông báo, cho đến theo dõi nhật ký (audit). Tài liệu này giải thích chi tiết cấu trúc cơ sở dữ liệu và cách tích hợp vào logic ứng dụng.

---

## 2. Thành Phần Chính

### 👤 Người Dùng & Phân Quyền (RBAC)
- **users**: Lưu thông tin người dùng (email, số điện thoại, mật khẩu đã mã hoá, trạng thái).
- **auth_providers**: Hỗ trợ đăng nhập qua bên thứ ba (Google, Facebook, Apple).
- **roles, permissions**: Định nghĩa vai trò và quyền truy cập.
- **role_permissions, user_roles**: Bảng quan hệ nhiều-nhiều để gán quyền.

### 🎬 Phim & Thể Loại
- **movies**: Thông tin phim (tên, thời lượng, trailer, banner, trạng thái phát hành).
- **movie_cast**: Diễn viên và ekip sản xuất.
- **movie_subtitles**: Hỗ trợ phụ đề nhiều ngôn ngữ.
- **genres, movie_genres**: Phân loại phim (Hành động, Hài, Tình cảm, ...).

### 🎥 Rạp, Phòng & Ghế
- **theaters**: Thông tin rạp (địa chỉ, tiện ích).
- **rooms**: Phòng chiếu (IMAX, 4DX, VIP).
- **seats**: Sơ đồ ghế (hàng, số ghế, loại ghế).

### 🕒 Suất Chiếu & Đặt Vé
- **showtimes**: Lịch chiếu phim.
- **seat_reservations**: Giữ ghế tạm thời có thời hạn.
- **bookings**: Đặt vé đã xác nhận.
- **booking_details**: Chi tiết từng ghế trong đặt vé.
- **payments**: Thông tin thanh toán (phương thức, nhà cung cấp, trạng thái).

### 🎟 Khuyến Mãi
- **promotions**: Quy tắc giảm giá (theo %, số tiền cố định, mua X tặng Y).
- **promotion_movies, promotion_theaters, promotion_user_groups**: Điều kiện áp dụng.
- **user_promotions**: Lịch sử sử dụng mã khuyến mãi.

### ⭐ Đánh Giá, Thông Báo & Nhật Ký
- **reviews**: Đánh giá và bình luận từ người dùng.
- **notifications**: Thông báo (xác nhận đặt vé, nhắc lịch, khuyến mãi).
- **audit_logs**: Ghi lại các hành động quan trọng của hệ thống.

### 📊 Thống Kê & Cache
- **movie_stats, theater_stats, showtime_availability**: Dữ liệu được tính trước để tối ưu hiệu năng.

### ⏱ Scheduler
- **cleanup_expired_reservations**: Xoá giữ ghế hết hạn.
- **cleanup_expired_bookings**: Hủy đặt vé quá hạn hoặc chưa thanh toán.

---

## 3. Quy Trình Nghiệp Vụ

### 🎬 Quy Trình Đặt Vé
1. Người dùng chọn phim & suất chiếu.
2. Hệ thống trả về danh sách ghế trống.
3. Người dùng chọn ghế → tạo giữ ghế có thời hạn.
4. Người dùng thanh toán:
    - Thành công → lưu booking + chi tiết ghế, xác nhận giữ ghế.
    - Thất bại hoặc hết hạn → huỷ booking, ghế được trả lại.
5. Người dùng nhận mã vé và email/SMS xác nhận.

### 🛠 Quy Trình Quản Trị
- **SUPER_ADMIN / ADMIN**: Quản lý người dùng, vai trò, quyền hạn.
- **THEATER_MANAGER**: Quản lý rạp, phòng, suất chiếu.
- **STAFF**: Hỗ trợ đặt vé tại quầy.

### 💳 Quy Trình Khuyến Mãi
1. Người dùng nhập mã khuyến mãi.
2. Hệ thống kiểm tra điều kiện (thời gian, giới hạn, phim/rạp áp dụng).
3. Giảm giá được áp dụng, lưu lại trong **user_promotions**.

---

## 4. Điểm Nổi Bật Kỹ Thuật

### ✅ Tính Năng Chính
- **RBAC**: Phân quyền dựa trên vai trò đảm bảo an toàn.
- **TTL giữ ghế**: Ngăn bán trùng ghế bằng cơ chế khoá tạm thời.
- **Dữ liệu tính trước**: Tăng tốc báo cáo.
- **Audit Logs**: Theo dõi chi tiết hoạt động.
- **Thông báo**: Cập nhật kịp thời cho người dùng.

### 🔗 Tích Hợp Spring Boot
1. **Spring Security + JWT**
    - Mapping `users`, `roles`, `permissions` vào cơ chế xác thực.
    - JWT filter xử lý bảo mật không trạng thái.

2. **Spring Data JPA**
    - Entity mapping trực tiếp với schema.
    - Repository pattern cho CRUD & query.

3. **Spring Scheduler / Quartz**
    - Thay thế hoặc bổ sung cho event của MySQL để dọn dẹp dữ liệu.

4. **Redis Cache (tuỳ chọn)**
    - Cache suất chiếu, ghế trống.
    - Lưu giữ ghế tạm thời.

5. **Thanh Toán**
    - Tích hợp MoMo, ZaloPay, Stripe, PayPal.

6. **Thông Báo**
    - Email qua Spring Mail.
    - Push notification qua WebSocket/Firebase.

---

## 5. Truy Vấn Mẫu

### 🔍 Ghế trống cho suất chiếu
```sql
SELECT s.id, s.row_label, s.seat_number
FROM seats s
LEFT JOIN seat_reservations r ON s.id = r.seat_id AND r.status = 'RESERVED'
LEFT JOIN booking_details bd ON s.id = bd.seat_id
WHERE s.room_id = :roomId AND bd.id IS NULL AND r.id IS NULL;
```

### 🔍 Top phim theo lượt đặt vé
```sql
SELECT m.title, COUNT(b.id) AS total_bookings
FROM movies m
JOIN showtimes st ON m.id = st.movie_id
JOIN bookings b ON st.id = b.showtime_id
WHERE b.status = 'CONFIRMED'
GROUP BY m.id
ORDER BY total_bookings DESC
LIMIT 10;
```

---

## 6. Phát Triển Tương Lai
- Hỗ trợ đa tiền tệ & đa ngôn ngữ.
- Chương trình khách hàng thân thiết (tích điểm).
- Gợi ý phim bằng AI.
- Giá vé động dựa trên nhu cầu & vị trí ghế.

---

## 7. Kết Luận
Schema này cung cấp một **nền tảng sẵn sàng triển khai thực tế** cho hệ thống đặt vé xem phim với đầy đủ tính năng: xác thực, RBAC, đặt vé, thanh toán, khuyến mãi, thông báo, thống kê và nhật ký. Hệ thống dễ dàng tích hợp với Spring Boot (Security, JPA, Scheduler, Cache).

