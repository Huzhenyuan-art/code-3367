SET NAMES utf8mb4;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS restaurant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE restaurant;

-- 用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 菜品表
CREATE TABLE dishes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    dish_name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ON_SHELF',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 收藏表
CREATE TABLE favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    dish_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_dish (user_id, dish_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (dish_id) REFERENCES dishes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单表
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单明细表
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    dish_id BIGINT NOT NULL,
    dish_name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (dish_id) REFERENCES dishes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入管理员账号 (密码: admin, BCrypt加密)
INSERT INTO users (username, password, role, balance) VALUES
('admin', '$2b$12$PIMBhcBM0.2JZgc5b2hu7.KjinUiD88IGxCmo0LCT9Xo6d9/mw.Pi', 'ADMIN', 0.00);

-- 插入默认普通用户账号 (密码: 123456, BCrypt加密)
INSERT INTO users (username, password, role, balance) VALUES
('user', '$2b$12$1WwC9nzvf.cc.t5gN4GLZ.Oor/WQdJ.OwGRTYoeB4P.fFlCgcVSKa', 'USER', 1000.00);

-- 插入测试菜品
INSERT INTO dishes (dish_name, price, description) VALUES
('宫保鸡丁', 38.00, '经典川菜，鸡肉鲜嫩，花生酥脆'),
('麻婆豆腐', 28.00, '麻辣鲜香，豆腐嫩滑'),
('鱼香肉丝', 32.00, '酸甜可口，肉丝细嫩'),
('红烧肉', 48.00, '肥而不腻，入口即化'),
('西红柿炒蛋', 18.00, '家常小炒，营养丰富');

-- 插入下架演示菜品
INSERT INTO dishes (dish_name, price, description, status) VALUES
('限量烤鸭', 88.00, '每日限量供应，皮脆肉嫩', 'OFF_SHELF');

-- 为默认普通用户预置收藏（含一个已下架菜品，用于演示下架后收藏可见但不可加购）
INSERT INTO favorites (user_id, dish_id) VALUES
(2, 1),
(2, 3),
(2, 6);
