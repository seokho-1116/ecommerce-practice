INSERT INTO user (id, name, email) VALUES (1,'test', 'test@gmail.com'),
                                          (2, 'test2', 'test2@gmail.com'),
                                          (3, 'test3', 'test3@gmail.com'),
                                          (4, 'test4', 'test4@gmail.com'),
                                          (5, 'test5', 'test5@gmail.com'),
                                          (6, 'test6', 'test6@gmail.com'),
                                          (7, 'test7', 'test7@gmail.com'),
                                          (8, 'test8', 'test8@gmail.com'),
                                          (9, 'test9', 'test9@gmail.com'),
                                          (10, 'test10', 'test10@gmail.com');

INSERT INTO coupon (id, name, description, discount_rate, discount_amount, quantity, coupon_type, `from`, `to`) VALUES
                                                                                                                    (1, '할인 쿠폰', '정액 쿠폰', null, 1000, null, 'FIXED', NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 1 DAY),
                                                                                                                    (2, '할인 쿠폰2', '정률 쿠폰', 0.1, null, 5, 'PERCENTAGE', NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 1 DAY);
