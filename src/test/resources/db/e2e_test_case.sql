INSERT INTO user (id, name, email) VALUES (1,'test', 'test@gmail.com');

-- 1. Product
INSERT INTO product (id, name, description, base_price, created_at)
VALUES (1, 'test', 'test', 1000, NOW() - INTERVAL 1 DAY);

-- 2. ProductOption
INSERT INTO product_option (id, name, description, additional_price, product_id, created_at)
VALUES (1, 'test', 'test', 1000, 1, NOW() - INTERVAL 1 DAY);

-- 3. ProductInventory
INSERT INTO product_inventory (id, quantity, product_option_id, created_at)
VALUES (1, 100, 1, NOW() - INTERVAL 1 DAY);

-- 4. Coupon
INSERT INTO coupon (id, name, description, discount_rate, discount_amount, quantity, coupon_type, `from`, `to`) VALUES
                                                                                                                    (1, '할인 쿠폰', '정액 쿠폰', null, 1000, null, 'FIXED', NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 1 DAY);

-- 5. UserCoupon
INSERT INTO user_coupon (id, user_id, coupon_id, version) VALUES (1, 1, 1, 1);