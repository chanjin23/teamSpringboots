-- Users 테이블에 테스트 데이터 삽입
INSERT INTO users (username, user_real_id, email, password, is_deleted, delete_reason, role, provider, created_at, updated_at)
VALUES ('test_user', 'test_real_id', 'test_user@example.com', '$2a$10$V1hEUNfKSMDNC2lkh58L0uebtdMuwl0tCf2mwlmz4j47Vt.7OgZNe', 0, NULL, 'USER', 'NONE', NOW(), NOW());

INSERT INTO users (username, user_real_id, email, password, is_deleted, delete_reason, role, provider, created_at, updated_at)
VALUES ('second_user', 'second_real_id', 'second_user@example.com', '$2a$10$WKGvXJc/mTeYBvCTHu0t/uAxuh3NS5u.Tv4BqhR8Geby1DVchWLxO', 0, NULL, 'USER', 'NONE', NOW(), NOW());

INSERT INTO users (username, user_real_id, email, password, is_deleted, delete_reason, role, provider, created_at, updated_at)
VALUES ('third_user', 'third_real_id', 'third_user@example.com', '$2a$10$WKGvXJc/mTeYBvCTHu0t/uAxuh3NS5u.Tv4BqhR8Geby1DVchWLxO', 0, NULL, 'USER', 'NONE', NOW(), NOW());

INSERT INTO users (username, user_real_id, email, password, is_deleted, delete_reason, role, provider, created_at, updated_at)
VALUES ('fourth_user', 'fourth_real_id', 'fourth_user@example.com', '$2a$10$WKGvXJc/mTeYBvCTHu0t/uAxuh3NS5u.Tv4BqhR8Geby1DVchWLxO', 0, NULL, 'USER', 'NONE', NOW(), NOW());

INSERT INTO users (username, user_real_id, email, password, is_deleted, delete_reason, role, provider, created_at, updated_at)
VALUES ('fifth_user', 'fifth_real_id', 'fifth_user@example.com', '$2a$10$WKGvXJc/mTeYBvCTHu0t/uAxuh3NS5u.Tv4BqhR8Geby1DVchWLxO', 0, NULL, 'USER', 'NONE', NOW(), NOW());

INSERT INTO users (username, user_real_id, email, password, is_deleted, delete_reason, role, provider, created_at, updated_at)
VALUES ('sixth_user', 'sixth_real_id', 'sixth_user@example.com', '$2a$10$WKGvXJc/mTeYBvCTHu0t/uAxuh3NS5u.Tv4BqhR8Geby1DVchWLxO', 0, NULL, 'USER', 'NONE', NOW(), NOW());

INSERT INTO users (username, user_real_id, email, password, is_deleted, delete_reason, role, provider, created_at, updated_at)
VALUES ('seventh_user', 'seventh_real_id', 'seventh_user@example.com', '$2a$10$WKGvXJc/mTeYBvCTHu0t/uAxuh3NS5u.Tv4BqhR8Geby1DVchWLxO', 0, NULL, 'USER', 'NONE', NOW(), NOW());

INSERT INTO users (username, user_real_id, email, password, is_deleted, delete_reason, role, provider, created_at, updated_at)
VALUES ('eighth_user', 'eighth_real_id', 'eighth_user@example.com', '$2a$10$WKGvXJc/mTeYBvCTHu0t/uAxuh3NS5u.Tv4BqhR8Geby1DVchWLxO', 0, NULL, 'USER', 'NONE', NOW(), NOW());

INSERT INTO users (username, user_real_id, email, password, is_deleted, delete_reason, role, provider, created_at, updated_at)
VALUES ('ninth_user', 'ninth_real_id', 'ninth_user@example.com', '$2a$10$WKGvXJc/mTeYBvCTHu0t/uAxuh3NS5u.Tv4BqhR8Geby1DVchWLxO', 0, NULL, 'USER', 'NONE', NOW(), NOW());

INSERT INTO users (username, user_real_id, email, password, is_deleted, delete_reason, role, provider, created_at, updated_at)
VALUES ('tenth_user', 'tenth_real_id', 'tenth_user@example.com', '$2a$10$WKGvXJc/mTeYBvCTHu0t/uAxuh3NS5u.Tv4BqhR8Geby1DVchWLxO', 0, NULL, 'USER', 'NONE', NOW(), NOW());

INSERT INTO users (username, user_real_id, email, password, is_deleted, delete_reason, role, provider, created_at, updated_at)
VALUES ('eleventh_user', 'eleventh_real_id', 'eleventh_user@example.com', '$2a$10$WKGvXJc/mTeYBvCTHu0t/uAxuh3NS5u.Tv4BqhR8Geby1DVchWLxO', 0, NULL, 'USER', 'NONE', NOW(), NOW());

INSERT INTO users (username, user_real_id, email, password, is_deleted, delete_reason, role, provider, created_at, updated_at)
VALUES ('twelfth_user', 'twelfth_real_id', 'twelfth_user@example.com', '$2a$10$WKGvXJc/mTeYBvCTHu0t/uAxuh3NS5u.Tv4BqhR8Geby1DVchWLxO', 0, NULL, 'USER', 'NONE', NOW(), NOW());

INSERT INTO users (username, user_real_id, email, password, is_deleted, delete_reason, role, provider, created_at, updated_at)
VALUES ('admin', 'admin', 'admin@example.com', '$2a$10$V1hEUNfKSMDNC2lkh58L0uebtdMuwl0tCf2mwlmz4j47Vt.7OgZNe', 0, NULL, 'ADMIN', 'NONE', NOW(), NOW());

-- UsersInfo 테이블에 테스트 데이터 삽입
INSERT INTO users_info (
    user_id, address, street_address, detailed_address, phone, created_at, updated_at
) VALUES
    (1, '13494', '경기도 성남시 분당구 대왕판교로 670', '1동 1103호', '01098765432', NOW(), NOW());

INSERT INTO users_info (
    user_id, address, street_address, detailed_address, phone, created_at, updated_at
) VALUES
    (2, '03925', '서울특별시 마포구 월드컵북로 396', '503동 304호', '01087654321', NOW(), NOW());


-- Category 테이블에 테스트 데이터 삽입
INSERT INTO category (
    category_name, category_thema, category_content, display_order, created_at, updated_at, image_url
) VALUES
    ('Shoes', 'common', 'All types of shoes', 1, NOW(), NOW(), NULL),
    ('Shoes', 'women', 'All types of shoes', 1, NOW(), NOW(), NULL),
    ('Shoes', 'men', 'All types of shoes', 1, NOW(), NOW(), NULL),
    ('Socks', 'accessories', 'All types of socks', 1, NOW(), NOW(), NULL),
    ('24SS SUMMER RECOMMEND STYLING', 'recommend', 'Styling Guide', 1, NOW(), NOW(), 'https://project-springboots.s3.amazonaws.com/20241021141431-2138281678'),
    ('Comfortable', 'recommend', 'recommend', 2, NOW(), NOW(), null);

-- Item 테이블에 테스트 데이터 삽입
INSERT INTO item (item_id, category_id, item_name, item_price, item_description, item_maker, item_color, created_at, updated_at, image_url, item_size, item_quantity)
VALUES (101, 1, 'Running Shoes', 5000, 'Comfortable running shoes', 'Brand A', 'Red', NOW(), NOW(), 'https://project-springboots.s3.amazonaws.com/20241021174149-1638484837', 230, 0);
-- 아이템 키워드 리스트
INSERT INTO item_keywords (item_id, keyword) VALUES
(101, 'Running Shoes'),
(101, 'common'),
(101, 'comfortable'),
(101, 'athletic'),
(101, 'mesh'),
(101, '24SS SUMMER RECOMMEND STYLING');

INSERT INTO item (item_id, category_id, item_name, item_price, item_description, item_maker, item_color, created_at, updated_at, image_url, item_size, item_quantity)
VALUES (102, 2, 'Walking Shoes', 7000, 'Comfortable walking shoes', 'Brand B', 'Blue', NOW(), NOW(), 'https://project-springboots.s3.amazonaws.com/20241021174149-1638484837', 240, 0);
INSERT INTO item_keywords (item_id, keyword) VALUES
(102, 'Walking Shoes'),
(102, 'common'),
(102, 'comfortable'),
(102, 'casual'),
(102, 'leather'),
(102, '24SS SUMMER RECOMMEND STYLING');


-- Orders 테이블에 테스트 데이터 삽입
INSERT INTO orders (
     user_id, quantity, orders_total_price, order_status, is_canceled, created_at, updated_at
) VALUES
    (1, 3, 15000, '주문완료', false, NOW(), NOW());

-- Orders 테이블에 테스트 데이터 삽입 (유저 2의 주문 추가)
INSERT INTO orders (
    user_id, quantity, orders_total_price, order_status, is_canceled, created_at, updated_at
) VALUES
    (2, 2, 14000, '주문완료', false, NOW(), NOW());
INSERT INTO orders (
    user_id, quantity, orders_total_price, order_status, is_canceled, created_at, updated_at
) VALUES
    (1, 3, 21000, '상품배송중', false, NOW(), NOW());


-- OrderItems 테이블에 테스트 데이터 삽입
INSERT INTO order_items (
    orders_id, item_id, orderitems_total_price, orderitems_quantity, item_size,
    shipping_address, recipient_name, recipient_contact, delivery_message,
    created_at, updated_at
) VALUES
      (1, 101, 5000, 1, 290, '서울특별시 강남구 선릉로 433, 신관 6층', '엘리스', '010-4234-3424', '문앞에 배송해주세요', NOW(), NOW()),
      (1, 101, 10000, 2, 290, '서울특별시 강남구 선릉로 433, 신관 6층', 'John Doe', '031-434-223', '부재시 경비실에 맡겨주세요', NOW(), NOW());

INSERT INTO order_items (
    orders_id, item_id, orderitems_total_price, orderitems_quantity, item_size,
    shipping_address, recipient_name, recipient_contact, delivery_message,
    created_at, updated_at
) VALUES
      (2, 102, 7000, 1, 280, '서울특별시 마포구 월드컵북로 396', 'Second User', '010-8765-4321', '부재 시 경비실에 맡겨주세요', NOW(), NOW()),
      (2, 102, 7000, 1, 280, '서울특별시 마포구 월드컵북로 396', 'Second User', '010-8765-4321', '조심히 다뤄주세요', NOW(), NOW());


INSERT INTO order_items (
    orders_id, item_id, orderitems_total_price, orderitems_quantity, item_size,
    shipping_address, recipient_name, recipient_contact, delivery_message,
    created_at, updated_at
) VALUES
      (3, 101, 7000, 1, 220 ,'경기도 성남시 분당구 대왕판교로 670', '김이박', '010-9876-5432', '경비실에 맡겨주세요', NOW(), NOW()),
      (3, 102, 14000, 2, 230 ,'경기도 성남시 분당구 대왕판교로 670', '김이박', '010-9876-5432', '조심히 다뤄주세요', NOW(), NOW());


-- Event 테이블에 테스트 데이터 삽입
INSERT INTO event (event_title, event_content, thumbnail_image_url, start_date, end_date, is_active, created_at, updated_at)
VALUES ('여름 신발 세일', '모든 여름 신발 20% 할인! 더운 여름을 시원하게 보내세요.', 'https://example.com/summer_sale_thumb.jpg', '2024-07-01', '2024-07-31', true, NOW(), NOW());
-- 개별 이미지 URL을 각각의 레코드로 삽입
INSERT INTO event_content_images (event_id, content_image_url)
SELECT 1, 'https://example.com/summer_sale_content.jpg';

INSERT INTO event (event_title, event_content, thumbnail_image_url, start_date, end_date, is_active, created_at, updated_at)
VALUES ('전 상품 무료배송', '전 품목 전 지역 무료 배송! 배송비 부담없이 Spring Boots! 낮 12시 이전 구매 시 당일 출고', 'https://project-springboots.s3.amazonaws.com/20241022202630-754842258', '2024-07-31', '2025-12-31', true, NOW(), NOW());
-- 개별 이미지 URL을 각각의 레코드로 삽입
INSERT INTO event_content_images (event_id, content_image_url)
SELECT 2, 'https://project-springboots.s3.amazonaws.com/202410222032562099949708';

INSERT INTO event (event_title, event_content, thumbnail_image_url, start_date, end_date, is_active, created_at, updated_at)
VALUES ('2024 F/W 프리뷰', '다가오는 가을, 겨울 시즌 부츠 미리보기. 따뜻하고 스타일리시한 갈결을 준비하세요.', 'https://project-springboots.s3.amazonaws.com/202410222320171543612425', '2024-10-01', '2025-01-31', true, NOW(), NOW());
-- 개별 이미지 URL을 각각의 레코드로 삽입
--INSERT INTO event_content_images (event_id, content_image_url)
--SELECT 3, 'https://example.com/winter_preview_content.jpg';


INSERT INTO event (event_title, event_content, thumbnail_image_url, start_date, end_date, is_active, created_at, updated_at)
VALUES ('Spring Boots의 공식몰 혜택', 'Spring Boots의 다양한 혜택을 만나보세요!', 'https://project-springboots.s3.amazonaws.com/20241022211709118777626', '2024-11-09', '2025-02-09', true, NOW(), NOW());
-- 개별 이미지 URL을 각각의 레코드로 삽입
INSERT INTO event_content_images (event_id, content_image_url)
SELECT 4, 'https://project-springboots.s3.amazonaws.com/20241022211709-133861261';
INSERT INTO event_content_images (event_id, content_image_url)
SELECT 4, 'https://project-springboots.s3.amazonaws.com/20241022211709-1567183934';
INSERT INTO event_content_images (event_id, content_image_url)
SELECT 4, 'https://project-springboots.s3.amazonaws.com/20241022211709208475465';
INSERT INTO event_content_images (event_id, content_image_url)
SELECT 4, 'https://project-springboots.s3.amazonaws.com/202410222117091839143847';
