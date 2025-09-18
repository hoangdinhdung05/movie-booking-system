# 📖 Hệ Thống Đặt Vé Xem Phim – Tài Liệu Hoàn Chỉnh (Optimized v1.1)

---

## 1. Quản Lý Người Dùng & Phân Quyền (RBAC)

* **roles**: Danh sách vai trò (SUPER\_ADMIN, ADMIN, THEATER\_MANAGER, STAFF, USER).
* **permissions**: Quản lý quyền ở mức resource + action (vd: movies.read, users.update).
* **role\_permissions**: Bảng mapping N-N giữa roles và permissions.
* **user\_roles**: Gán vai trò cho người dùng, có cột *assigned\_by* để tracking.

➡️ Ý nghĩa: RBAC chuẩn chỉnh, mở rộng dễ dàng khi thêm module mới.

---

## 2. Bảng Cốt Lõi

### 👤 Người Dùng (users)

* Thông tin cơ bản + hỗ trợ login local & OAuth.
* Tracking bảo mật: failed\_login\_attempts, locked\_until.
* Index mạnh: email, phone, provider\_id, status.

### 🎬 Phim (movies)

* Quản lý phim: title, slug, duration, rating, cast, director.
* Status: COMING\_SOON / NOW\_SHOWING / ENDED.
* Fulltext index hỗ trợ tìm kiếm.

### 🎭 Thể Loại (genres + movie\_genres)

* Chuẩn hóa để 1 phim thuộc nhiều thể loại.

### 🎥 Rạp (theaters)

* Thông tin rạp: location, facilities, opening hours.
* Index theo city + status.

### 🏢 Phòng Chiếu (rooms)

* Mỗi rạp có nhiều phòng.
* Loại phòng: STANDARD, IMAX, VIP, 4DX…

### 💺 Ghế (seats)

* Từng ghế với row\_name, seat\_number, type.
* position\_x, position\_y để render sơ đồ ghế.
* Unique theo (room\_id, row\_name, seat\_number).

---

## 3. Đặt Vé & Suất Chiếu

### ⏱ showtimes

* Lịch chiếu: movie + room + ngày/giờ.
* Giá cơ bản + giá VIP/couple.
* Quản lý ghế khả dụng.
* Status: SCHEDULED → OPEN → FULL/ONGOING → ENDED.
* Có thể partition theo tháng để tối ưu.

### 🎟 seat\_reservations

* Đặt chỗ tạm thời (hold ghế).
* Có reserved\_until để auto expire.
* Index idx\_expiry để cleanup nhanh.

### 📑 bookings

* Đơn đặt vé chính thức.
* Thông tin: total, discount, final\_amount.
* Trạng thái: PENDING, CONFIRMED, CANCELLED, REFUNDED, USED.
* expiry\_time: 15 phút cho thanh toán.
* Index mạnh: user\_id, showtime\_id, status.

### 🪑 booking\_details

* Danh sách ghế cụ thể cho từng booking.
* Denormalized: row\_name, seat\_number để hiển thị nhanh.

### 💳 payments

* Liên kết booking.
* Thông tin: transaction\_id, provider, response JSON.
* Trạng thái: PENDING, SUCCESS, FAILED, REFUNDED.
* Index: idx\_transaction, idx\_status.

---

## 4. Tính Năng Bổ Sung

### 🎁 promotions

* Quản lý mã khuyến mãi: PERCENTAGE, FIXED\_AMOUNT, BUY\_X\_GET\_Y.
* Scope: movie, theater, user group.
* valid\_from / valid\_until để giới hạn thời gian.

### 👥 user\_promotions

* Tracking user sử dụng khuyến mãi nào.

### ⭐ reviews

* Đánh giá phim: rating 1–5, title, content.
* is\_verified để xác minh từ user đã đặt vé.
* Index: movie, rating, created.

### 🔔 notifications

* Gửi thông báo: đặt vé, thanh toán, khuyến mãi.
* is\_read, read\_at để tracking.

---

## 5. Audit & Logging

* **audit\_logs**: Log hành động nhạy cảm.
* Trường: user, action, resource, old\_values, new\_values.
* Partition theo năm.
* Index: user, resource, action.

➡️ Dùng để forensic, compliance, điều tra sự cố.

---

## 6. Thống Kê & Cache

* **movie\_stats**: Tổng vé bán, doanh thu, rating TB.
* **theater\_stats**: Tổng quan booking + occupancy.
* **showtime\_availability**: Cache ghế còn trống theo loại.

➡️ Update qua trigger/job để query nhanh.

---

## 7. Bảo Trì & Dọn Dẹp

* **cleanup\_expired\_reservations**: Mỗi 1 phút expire ghế giữ.
* **cleanup\_expired\_bookings**: Mỗi 5 phút hủy booking chưa thanh toán.

---

## 8. Best Practices

* Race condition giữ ghế: dùng UNIQUE(showtime\_id, seat\_id) + transaction lock.
* Booking code: generate ngẫu nhiên, retry khi trùng.
* Partition/Archive: tạo job quản lý partition showtimes hàng tháng.
* Promotions cache: lưu Redis để giảm lookup DB.
* Stats update: nên dùng Kafka/event sourcing thay vì trigger khi tải lớn.
* Import seed data: disable FK trước khi insert.
* Observability: push audit\_logs sang ELK/ClickHouse để phân tích.

---

## 9. Kết Luận

Schema v1.1 được tối ưu theo hướng:

* Chuẩn hóa dữ liệu, giảm dư thừa JSON.
* Index hợp lý cho query phổ biến.
* Partition & archive để mở rộng lâu dài.
* RBAC + audit đảm bảo bảo mật & traceable.
* Cache/materialized views để API tốc độ cao.

👉 Đây là nền tảng ổn định cho hệ thống booking **production-ready**, có thể mở rộng thành **microservices hoặc CQRS** nếu tải cực lớn.
