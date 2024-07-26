INSERT INTO product_categories (name, category_type)
    VALUES  ('HOT', 'DRINKS'),
            ('COLD', 'DRINKS'),
            ('HOT','STARTERS'),
            ('COLD','SALADS'),
            ('ALCOHOLIC','DRINKS'),
            ('HOT','SALADS');

INSERT INTO products (name, price, quantity, available)
    VALUES  ('Coffee', 100.00, 20, true),
            ('Gin', 250.00, 10, false),
            ('Chicken soup', 350.00, 15, true),
            ('Caesar', 300.00, 25, true),
            ('Greek', 320.00, 20, false),
            ('Mens health', 400.00, 10, true),
            ('Mulled wine', 150.00, 20, true);

INSERT INTO products_product_categories (product_id, product_category_id)
VALUES  (1, 1),
        (2, 2),
        (2, 5),
        (3, 3),
        (4, 4),
        (5, 4),
        (6, 6),
        (7, 1),
        (7, 5);

INSERT INTO order_details (order_status, total_amount)
VALUES  ('CREATED', 450.00),
        ('PAID', 1000.00);

INSERT INTO order_details_products (order_detail_id, product_id)
VALUES  (1, 1),
        (1, 3),
        (2, 2),
        (2, 6),
        (2, 3);