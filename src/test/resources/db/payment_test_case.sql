INSERT INTO user (id, name, email) VALUES (1,'test', 'test@gmail.com');

-- 1. Product
INSERT INTO product (id, name, description, base_price, created_at)
VALUES (1, 'test', 'test', 1000, NOW() - INTERVAL 1 DAY),
       (2, 'test2', 'test2', 2000, NOW() - INTERVAL 1 DAY),
       (3, 'test3', 'test3', 2000, NOW() - INTERVAL 1 DAY);

-- 2. ProductOption
INSERT INTO product_option (id, name, description, additional_price, product_id, created_at)
VALUES (1, 'test', 'test', 1000, 1, NOW() - INTERVAL 1 DAY),
       (2, 'test2', 'test2', 2000, 1, NOW() - INTERVAL 1 DAY),
       (3, 'test3', 'test3', 3000, 2, NOW() - INTERVAL 1 DAY),
       (4, 'test4', 'test4', 4000, 2, NOW() - INTERVAL 1 DAY),
       (5, 'test5', 'test5', 5000, 2, NOW() - INTERVAL 1 DAY),
       (6, 'test6', 'test6', 6000, 3, NOW() - INTERVAL 1 DAY),
       (7, 'test7', 'test7', 7000, 3, NOW() - INTERVAL 1 DAY);

-- 3. ProductInventory
INSERT INTO product_inventory (id, quantity, product_option_id, created_at)
VALUES (1, 100, 1, NOW() - INTERVAL 1 DAY),
       (2, 200, 2, NOW() - INTERVAL 1 DAY),
       (3, 300, 3, NOW() - INTERVAL 1 DAY),
       (4, 400, 4, NOW() - INTERVAL 1 DAY),
       (5, 500, 5, NOW() - INTERVAL 1 DAY),
         (6, 600, 6, NOW() - INTERVAL 1 DAY),
         (7, 700, 7, NOW() - INTERVAL 1 DAY);

-- 4. UserPoint
INSERT INTO user_point (id, user_id, amount) VALUES (1, 1, 20000);

-- 5. Coupon
INSERT INTO coupon (id, name, description, discount_rate, discount_amount, quantity, coupon_type, `from`, `to`) VALUES
                                                                                                                    (1, '할인 쿠폰', '정액 쿠폰', null, 1000, null, 'FIXED', NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 1 DAY);

-- 6. UserCoupon
INSERT INTO user_coupon (id, user_id, coupon_id) VALUES (1, 1, 1);

-- 5. Order
INSERT INTO `order` (id, total_price, discount_price, final_price, status, user_id)
VALUES (1, 20000, 0, 20000, 'CREATED', 1);

-- 6. OrderItem
INSERT INTO order_item (id, product_name, product_description, product_option_name,
                        product_option_description, base_price, additional_price, total_price,
                        amount, order_id, product_option_id, created_at)
VALUES (1, 'test', 'test', 'test', 'test', 1000, 1000, 2000, 1, 1, 1,  NOW() - INTERVAL 1 DAY),
         (2, 'test2', 'test2', 'test2', 'test2', 2000, 2000, 4000, 1, 1, 2, NOW() - INTERVAL 1 DAY),
         (3, 'test3', 'test3', 'test3', 'test3', 3000, 3000, 6000, 1, 1, 3, NOW() - INTERVAL 1 DAY),
         (4, 'test4', 'test4', 'test4', 'test4', 4000, 4000, 8000, 1, 1, 4, NOW() - INTERVAL 1 DAY);