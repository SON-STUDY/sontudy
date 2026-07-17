-- 로컬 개발용 상품 더미데이터 프로시저
--
-- 사용법:
--   mysql -h 127.0.0.1 -P 3366 -uroot -p son_db < seed/product_seed.sql
--   docker exec sonstudy-db mysql -uroot -plocaldev1234 son_db -e "CALL seed_products(50);"
--   CALL seed_products(50); -- 더미 데이터 50개 생성
--   CALL clear_seed_products(); -- 생성한 더미 전체 삭제
--
-- 생성되는 데이터의 id는 모두 'SEED' 접두사를 가지므로
-- 손으로 만든 데이터와 섞이지 않고, clear_seed_products()로만 정확히 지워집니다.

DELIMITER $$

DROP PROCEDURE IF EXISTS seed_products$$
DROP PROCEDURE IF EXISTS clear_seed_products$$
DROP FUNCTION IF EXISTS fn_shoe_image_url$$

-- 신발 사진 URL 10종 (Wikimedia Commons, 자유 라이선스)
-- n: 1~10. 범위를 벗어나면 1번으로 되돌린다.
-- 파일명이 경로에 중복되지 않는 Special:FilePath 형식을 쓴다
-- (thumb URL 형식은 파일명이 두 번 들어가 image_url varchar(255)를 넘김)
CREATE FUNCTION fn_shoe_image_url(n INT)
    RETURNS VARCHAR(255)
    DETERMINISTIC
    NO SQL
BEGIN
    DECLARE v_base VARCHAR(64) DEFAULT 'https://commons.wikimedia.org/wiki/Special:FilePath/';
    DECLARE v_file VARCHAR(200);

    SET v_file = ELT(IF(n BETWEEN 1 AND 10, n, 1),
        '2023_Adidas_Yeezy_350_V2_EF2905_(1).jpg',
        'Avia_Shoes.jpg',
        'Black_Converse_sneakers.JPG',
        'FILA_FLOAT_MAXXI_and_FILA_AXILUS_ACE_in_Brazil.jpg',
        'Michael_Kors_Kristy_Lace_Up_Sneaker_43S4KRFS2E_Kalbsleder_braun_(1)_(16286299927).jpg',
        'Nike_SB_Dunk_High_MF_Doom.jpg',
        'Nike_SB_Dunk_High_MF_Doom_edited.jpg',
        'Professional_Sneakers_-_Sneakers_for_hostesses,_pilots_and_other_professionals._Comfortable_sneakers_produced_in_leather_inside_and_outside.jpg',
        'Reebok_Royal_Glide_Ripple_Clip_shoe.jpg',
        'Street_dance_sneakers_(Fuego).jpg');

    RETURN CONCAT(v_base, v_file, '?width=600');
END$$

-- 더미 상품 + 옵션 + 이미지 생성
-- p_count: 생성할 상품 개수
-- 기존 SEED 데이터의 마지막 번호 다음부터 이어붙이므로 여러 번 호출해도 된다
CREATE PROCEDURE seed_products(IN p_count INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE v_start INT;
    DECLARE v_end INT;
    DECLARE j INT;
    DECLARE v_pid        VARCHAR(255);
    DECLARE v_brand      VARCHAR(50);
    DECLARE v_model      VARCHAR(50);
    DECLARE v_category   VARCHAR(20);
    DECLARE v_status     VARCHAR(20);
    DECLARE v_released   DATETIME(6);
    DECLARE v_color_id   BIGINT;
    DECLARE v_size       INT;
    DECLARE v_stock      INT;
    DECLARE v_cost       INT;

    -- 색상 마스터가 비어 있을 때만 채운다 (기존 데이터 보존)
    IF (SELECT COUNT(*) FROM color) = 0 THEN
        INSERT INTO color (color_name, hex_code) VALUES
            ('Black', '#000000'),
            ('White', '#FFFFFF'),
            ('Red',   '#D32F2F'),
            ('Blue',  '#1976D2');
    END IF;

    -- 이미 있는 SEED 상품의 마지막 번호 다음부터 시작한다 (id 충돌 방지)
    SELECT IFNULL(MAX(CAST(SUBSTRING(id, 5) AS UNSIGNED)), 0) INTO v_start
    FROM product WHERE id LIKE 'SEED%';

    SET i     = v_start + 1;
    SET v_end = v_start + p_count;

    WHILE i <= v_end DO
        SET v_pid   = CONCAT('SEED', LPAD(i, 15, '0'));

        -- FE 브랜드 필터 칩과 동일한 4개 브랜드로 순환
        SET v_brand = ELT((i % 4) + 1, 'Nike', 'Adidas', 'New Balance', 'Converse');
        SET v_model = ELT((i % 8) + 1,
                          'Air Force 1', 'Dunk Low', 'Samba OG', 'Gazelle',
                          'NB 550', 'NB 992', 'Chuck 70', 'One Star');
        SET v_category = ELT((i % 5) + 1, 'SNEAKERS', 'LOAFERS', 'SANDALS', 'BOOTS', 'ETC');

        SET v_color_id = (SELECT id FROM color
                          WHERE color_name = ELT((i % 4) + 1, 'Black', 'White', 'Red', 'Blue')
                          LIMIT 1);

        -- status 분배: SCHEDULED는 반드시 released_at이 미래여야 목록에 노출된다
        --   (findScheduledDropsByCursor: status=SCHEDULED AND released_at >= now())
        --   ON_SALE / END 은 과거 출시일
        --   (findLiveDrops: status=ON_SALE OR (status=END AND option.stock > 0))
        CASE (i % 3)
            WHEN 0 THEN
                SET v_status   = 'SCHEDULED';
                SET v_released = NOW(6) + INTERVAL ((i % 30) + 1) DAY;
            WHEN 1 THEN
                SET v_status   = 'ON_SALE';
                SET v_released = NOW(6) - INTERVAL ((i % 60) + 1) DAY;
            ELSE
                SET v_status   = 'END';
                SET v_released = NOW(6) - INTERVAL ((i % 120) + 30) DAY;
        END CASE;

        INSERT INTO product (id, name, description, brand, color_id,
                             released_at, category, status, is_display)
        VALUES (v_pid,
                CONCAT(v_brand, ' ', v_model, ' #', i),
                CONCAT(v_brand, ' ', v_model, ' 한정판 드랍. 로컬 테스트용 더미 상품입니다.'),
                v_brand,
                v_color_id,
                v_released,
                v_category,
                v_status,
                b'1');

        -- 사이즈 240~290 (10mm 단위) 6개 옵션
        SET j = 0;
        WHILE j < 6 DO
            SET v_size  = 240 + (j * 10);
            SET v_cost  = 89000 + (((i + j) % 12) * 10000);

            -- END 상태에도 재고가 남은 상품이 섞이도록 분포를 준다
            -- (재고 0인 END 상품은 live 목록에서 정상적으로 제외됨)
            IF v_status = 'SOLD_OUT' THEN
                SET v_stock = 0;
            ELSE
                SET v_stock = (i + j) % 15;
            END IF;

            INSERT INTO product_option (id, cost, size, status, stock, total_sales, product_id)
            VALUES (CONCAT(v_pid, '-OPT', j),
                    v_cost, v_size, v_status, v_stock, (i + j) % 40, v_pid);
            SET j = j + 1;
        END WHILE;

        -- 대표 이미지 2장 (실제 신발 사진, Wikimedia Commons)
        -- 상품마다 서로 다른 2장이 걸리도록 i 기준으로 오프셋을 준다
        SET j = 0;
        WHILE j < 2 DO
            INSERT INTO product_image (id, image_url, `orders`, product_id)
            VALUES (CONCAT(v_pid, '-IMG', j),
                    fn_shoe_image_url(((i + j) % 10) + 1),
                    j, v_pid);
            SET j = j + 1;
        END WHILE;

        SET i = i + 1;
    END WHILE;

    SELECT CONCAT(p_count, '개 상품 생성 완료') AS result;
END$$

-- seed_products()로 만든 데이터만 삭제 (FK 때문에 자식부터)
CREATE PROCEDURE clear_seed_products()
BEGIN
    DELETE FROM product_image  WHERE product_id LIKE 'SEED%';
    DELETE FROM product_option WHERE product_id LIKE 'SEED%';
    DELETE FROM product_like   WHERE product_id LIKE 'SEED%';
    DELETE FROM product_notification WHERE product_id LIKE 'SEED%';
    DELETE FROM product        WHERE id LIKE 'SEED%';

    SELECT '더미 상품 삭제 완료' AS result;
END$$

DELIMITER ;
