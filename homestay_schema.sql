-- 创建数据库
CREATE DATABASE IF NOT EXISTS homestay_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE homestay_db;

-- 1. 用户表 (sys_user)
-- 用于存储所有类型的用户：管理员(ADMIN)、房东(HOST)、游客(GUEST)
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(加密存储)',
    nickname VARCHAR(50) COMMENT '昵称',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    role VARCHAR(20) NOT NULL DEFAULT 'GUEST' COMMENT '角色: ADMIN, HOST, GUEST',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_phone (phone)
) ENGINE=InnoDB COMMENT='用户表';

-- 2. 房源表 (homestay_room)
-- 存储房源的基本信息
CREATE TABLE IF NOT EXISTS homestay_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '房源ID',
    name VARCHAR(100) NOT NULL COMMENT '房源名称',
    description TEXT COMMENT '房源描述',
    base_price DECIMAL(10, 2) NOT NULL COMMENT '基础价格(每晚)',
    capacity INT NOT NULL DEFAULT 2 COMMENT '可入住人数',
    images JSON COMMENT '图片列表(JSON数组)',
    facilities JSON COMMENT '设施列表(JSON数组)',
    address VARCHAR(255) COMMENT '详细地址',
    city VARCHAR(50) COMMENT '城市',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-上架, 0-下架, 2-维护中',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='房源表';

-- 3. 定价规则表 (pricing_rule)
-- 用于动态定价：周末、节假日、特殊日期
CREATE TABLE IF NOT EXISTS pricing_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规则ID',
    room_id BIGINT COMMENT '关联房源ID，NULL表示全局规则',
    rule_type VARCHAR(20) NOT NULL COMMENT '规则类型: WEEKEND(周末), HOLIDAY(节假日), CUSTOM(自定义日期)',
    target_date DATE COMMENT '目标日期(仅CUSTOM类型有效)',
    price_multiplier DECIMAL(3, 2) DEFAULT 1.0 COMMENT '价格倍率 (例如 1.2 表示涨价20%)',
    price_add DECIMAL(10, 2) DEFAULT 0 COMMENT '价格增量 (例如 50 表示加价50元)',
    priority INT DEFAULT 0 COMMENT '优先级(数值越大越优先)',
    description VARCHAR(100) COMMENT '规则描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB COMMENT='定价规则表';

-- 4. 订单表 (homestay_order)
-- 核心交易记录
CREATE TABLE IF NOT EXISTS homestay_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(64) NOT NULL UNIQUE COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '预订用户ID',
    room_id BIGINT NOT NULL COMMENT '预订房源ID',
    check_in_date DATE NOT NULL COMMENT '入住日期',
    check_out_date DATE NOT NULL COMMENT '退房日期',
    guest_count INT NOT NULL DEFAULT 1 COMMENT '入住人数',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待支付, 1-已支付, 2-已入住, 3-已完成, 4-已取消, 5-退款中',
    remark VARCHAR(255) COMMENT '订单备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    pay_time DATETIME COMMENT '支付时间',
    INDEX idx_user_id (user_id),
    INDEX idx_room_id (room_id),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB COMMENT='订单表';

-- 5. 房态表 (room_availability)
-- 用于快速查询某天房间是否被占用 (作为订单的衍生数据，提高查询效率)
CREATE TABLE IF NOT EXISTS room_availability (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    room_id BIGINT NOT NULL COMMENT '房源ID',
    date DATE NOT NULL COMMENT '日期',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-已预订, 2-维护锁定',
    order_id BIGINT COMMENT '关联订单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_room_date (room_id, date) COMMENT '同一房间同一天只能有一条记录'
) ENGINE=InnoDB COMMENT='房态表';

-- 6. 初始化管理员账号 (密码为 admin123 的 BCrypt 加密示例，实际开发中请替换)
-- 假设使用 Spring Security，密码需加密
INSERT INTO sys_user (username, password, nickname, role, phone) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnutj8iAt8AE52zmgHSF.l2k.r.y7.u', '系统管理员', 'ADMIN', '13800000000');
