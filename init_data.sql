-- Initial Data Population Script
-- Database: homestay_db

USE homestay_db;

-- Ensure sys_user table exists
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(255) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `nickname` VARCHAR(255),
  `role` VARCHAR(50) NOT NULL,
  `phone` VARCHAR(50) UNIQUE,
  `email` VARCHAR(255),
  `avatar` VARCHAR(255),
  `create_time` DATETIME,
  `update_time` DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 1. Insert Users
-- Password 'password' -> $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7QA8qokPW
INSERT IGNORE INTO `sys_user` (`id`, `username`, `password`, `nickname`, `role`, `phone`, `avatar`, `create_time`, `update_time`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7QA8qokPW', '超级管理员', 'ADMIN', '13800138000', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin', NOW(), NOW()),
(2, 'host', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7QA8qokPW', '金牌房东', 'HOST', '13900139000', 'https://api.dicebear.com/7.x/avataaars/svg?seed=host', NOW(), NOW());

-- Insert Guests (User 3 to 12)
INSERT IGNORE INTO `sys_user` (`username`, `password`, `nickname`, `role`, `phone`, `avatar`, `create_time`, `update_time`) VALUES
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7QA8qokPW', '游客1', 'GUEST', '13700000001', 'https://api.dicebear.com/7.x/avataaars/svg?seed=1', NOW(), NOW()),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7QA8qokPW', '游客2', 'GUEST', '13700000002', 'https://api.dicebear.com/7.x/avataaars/svg?seed=2', NOW(), NOW()),
('user3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7QA8qokPW', '游客3', 'GUEST', '13700000003', 'https://api.dicebear.com/7.x/avataaars/svg?seed=3', NOW(), NOW()),
('user4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7QA8qokPW', '游客4', 'GUEST', '13700000004', 'https://api.dicebear.com/7.x/avataaars/svg?seed=4', NOW(), NOW()),
('user5', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7QA8qokPW', '游客5', 'GUEST', '13700000005', 'https://api.dicebear.com/7.x/avataaars/svg?seed=5', NOW(), NOW()),
('user6', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7QA8qokPW', '游客6', 'GUEST', '13700000006', 'https://api.dicebear.com/7.x/avataaars/svg?seed=6', NOW(), NOW()),
('user7', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7QA8qokPW', '游客7', 'GUEST', '13700000007', 'https://api.dicebear.com/7.x/avataaars/svg?seed=7', NOW(), NOW()),
('user8', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7QA8qokPW', '游客8', 'GUEST', '13700000008', 'https://api.dicebear.com/7.x/avataaars/svg?seed=8', NOW(), NOW()),
('user9', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7QA8qokPW', '游客9', 'GUEST', '13700000009', 'https://api.dicebear.com/7.x/avataaars/svg?seed=9', NOW(), NOW()),
('user10', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7QA8qokPW', '游客10', 'GUEST', '13700000010', 'https://api.dicebear.com/7.x/avataaars/svg?seed=10', NOW(), NOW());


-- 2. Insert Rooms
-- Ensure homestay_room exists
INSERT IGNORE INTO `homestay_room` (`name`, `description`, `city`, `address`, `base_price`, `capacity`, `status`, `images`, `facilities`, `create_time`, `update_time`) VALUES
('北京·故宫旁温馨四合院', '体验老北京的胡同文化，出门即是故宫，交通极其便利。', '北京', '北京市东城区南池子大街', 899.00, 4, 1, '["https://images.unsplash.com/photo-1522708323590-d24dbb6b0267", "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688"]', '["WiFi", "空调", "厨房"]', NOW(), NOW()),
('上海·外滩景观大平层', '直面陆家嘴三件套，超大落地窗，尽享魔都夜景。', '上海', '上海市黄浦区中山东一路', 1288.00, 2, 1, '["https://images.unsplash.com/photo-1560448204-e02f11c3d0e2", "https://images.unsplash.com/photo-1484154218962-a1c002085d2f"]', '["WiFi", "空调", "浴缸", "投影仪"]', NOW(), NOW()),
('成都·太古里轻奢公寓', '下楼即是太古里，吃喝玩乐一站式解决，房间设计感十足。', '成都', '成都市锦江区中纱帽街', 458.00, 2, 1, '["https://images.unsplash.com/photo-1554995207-c18c203602cb", "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267"]', '["WiFi", "空调", "冰箱"]', NOW(), NOW()),
('杭州·西湖边静谧民宿', '步行5分钟到西湖，闹中取静，带有独立小花园。', '杭州', '杭州市西湖区南山路', 680.00, 3, 1, '["https://images.unsplash.com/photo-1502672260266-1c1ef2d93688", "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2"]', '["WiFi", "空调", "花园"]', NOW(), NOW()),
('大理·苍山洱海观景房', '面朝洱海，春暖花开，超大露台，看日出日落。', '大理', '大理市双廊镇', 520.00, 2, 1, '["https://images.unsplash.com/photo-1484154218962-a1c002085d2f", "https://images.unsplash.com/photo-1554995207-c18c203602cb"]', '["WiFi", "空调", "阳台"]', NOW(), NOW()),
('三亚·亚龙湾海景别墅', '私人泳池，一线海景，适合全家度假，享受奢华体验。', '三亚', '三亚市吉阳区亚龙湾路', 2888.00, 6, 1, '["https://images.unsplash.com/photo-1522708323590-d24dbb6b0267", "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688"]', '["WiFi", "空调", "泳池", "厨房"]', NOW(), NOW()),
('广州·珠江新城CBD公寓', '俯瞰小蛮腰，商务出差首选，交通便捷。', '广州', '广州市天河区珠江新城', 568.00, 2, 1, '["https://images.unsplash.com/photo-1560448204-e02f11c3d0e2", "https://images.unsplash.com/photo-1484154218962-a1c002085d2f"]', '["WiFi", "空调", "办公桌"]', NOW(), NOW()),
('深圳·南山科技园Loft', '年轻人的聚集地，设计时尚，配套齐全。', '深圳', '深圳市南山区科兴科学园', 428.00, 2, 1, '["https://images.unsplash.com/photo-1554995207-c18c203602cb", "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267"]', '["WiFi", "空调", "洗衣机"]', NOW(), NOW()),
('重庆·洪崖洞江景房', '看洪崖洞夜景，赏嘉陵江风光，魔幻8D城市体验。', '重庆', '重庆市渝中区解放碑', 398.00, 2, 1, '["https://images.unsplash.com/photo-1502672260266-1c1ef2d93688", "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2"]', '["WiFi", "空调", "江景"]', NOW(), NOW()),
('西安·钟楼旁古风民宿', '紧邻回民街，吃货天堂，古色古香的装修风格。', '西安', '西安市碑林区南大街', 368.00, 2, 1, '["https://images.unsplash.com/photo-1484154218962-a1c002085d2f", "https://images.unsplash.com/photo-1554995207-c18c203602cb"]', '["WiFi", "空调", "茶具"]', NOW(), NOW());

-- 3. Insert Orders
-- Order 1: Pending
INSERT IGNORE INTO `homestay_order` (`order_no`, `user_id`, `room_id`, `check_in_date`, `check_out_date`, `guest_count`, `total_amount`, `status`, `create_time`) VALUES
(REPLACE(UUID(),'-',''), 3, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 2, 1798.00, 0, NOW());

-- Order 2: Paid (Upcoming)
INSERT IGNORE INTO `homestay_order` (`order_no`, `user_id`, `room_id`, `check_in_date`, `check_out_date`, `guest_count`, `total_amount`, `status`, `create_time`, `pay_time`) VALUES
(REPLACE(UUID(),'-',''), 4, 2, DATE_ADD(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 2, 2576.00, 1, NOW(), NOW());

-- Order 3: Completed (Past)
INSERT IGNORE INTO `homestay_order` (`order_no`, `user_id`, `room_id`, `check_in_date`, `check_out_date`, `guest_count`, `total_amount`, `status`, `create_time`, `pay_time`) VALUES
(REPLACE(UUID(),'-',''), 5, 3, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_SUB(CURDATE(), INTERVAL 3 DAY), 2, 916.00, 3, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY));

-- Order 4: Cancelled
INSERT IGNORE INTO `homestay_order` (`order_no`, `user_id`, `room_id`, `check_in_date`, `check_out_date`, `guest_count`, `total_amount`, `status`, `create_time`) VALUES
(REPLACE(UUID(),'-',''), 6, 4, DATE_ADD(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 12 DAY), 2, 1360.00, 4, NOW());

-- Order 5: Pending
INSERT IGNORE INTO `homestay_order` (`order_no`, `user_id`, `room_id`, `check_in_date`, `check_out_date`, `guest_count`, `total_amount`, `status`, `create_time`) VALUES
(REPLACE(UUID(),'-',''), 7, 5, DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 2, 520.00, 0, NOW());

-- Order 6: Paid
INSERT IGNORE INTO `homestay_order` (`order_no`, `user_id`, `room_id`, `check_in_date`, `check_out_date`, `guest_count`, `total_amount`, `status`, `create_time`, `pay_time`) VALUES
(REPLACE(UUID(),'-',''), 8, 6, DATE_ADD(CURDATE(), INTERVAL 15 DAY), DATE_ADD(CURDATE(), INTERVAL 20 DAY), 4, 14440.00, 1, NOW(), NOW());
