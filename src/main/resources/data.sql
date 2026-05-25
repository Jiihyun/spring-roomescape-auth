-- reservation_time
INSERT INTO reservation_time (start_at)
VALUES ('10:00:00'),
       ('11:00:00'),
       ('12:00:00'),
       ('13:00:00'),
       ('14:00:00'),
       ('15:00:00'),
       ('16:00:00'),
       ('17:00:00'),
       ('18:00:00'),
       ('19:00:00'),
       ('20:00:00'),
       ('21:00:00'),
       ('22:00:00');

-- store
INSERT INTO store (name)
VALUES ('강남점'),
       ('잠실점'),
       ('홍대점');

-- theme (12 unique themes with high-stability thumbnails)
INSERT INTO theme (name, description, thumbnail)
VALUES ('공포의 저택', '오래된 저택에서 탈출하세요',
        'https://images.unsplash.com/photo-1508739773434-c26b3d09e071?q=80&w=400&auto=format&fit=crop'),
       ('사라진 연구소', '비밀 연구소의 진실을 밝혀내세요',
        'https://images.unsplash.com/photo-1581093458791-9f3c3250f8b9?q=80&w=400&auto=format&fit=crop'),
       ('시간 여행자', '시간의 틈에서 탈출하세요',
        'https://images.unsplash.com/photo-1501139083538-0139583c060f?q=80&w=400&auto=format&fit=crop'),
       ('감옥 탈출', '제한 시간 안에 감옥을 탈출하세요',
        'https://images.unsplash.com/photo-1552508744-1696d4464960?q=80&w=400&auto=format&fit=crop'),
       ('마법사의 방', '마법사의 숨겨진 방을 탐험하세요', 'https://picsum.photos/seed/wizard-room/400/300'),
       ('좀비 바이러스', '바이러스가 퍼진 도시에서 살아남으세요',
        'https://images.unsplash.com/photo-1509248961158-e54f6934749c?q=80&w=400&auto=format&fit=crop'),
       ('해적의 보물', '해적선에 숨겨진 보물을 찾으세요',
        'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=400&auto=format&fit=crop'),
       ('스파이 미션', '비밀 요원이 되어 임무를 완수하세요',
        'https://images.unsplash.com/photo-1524178232363-1fb28f74b0cd?q=80&w=400&auto=format&fit=crop'),
       ('우주 정거장', '고장난 우주 정거장에서 탈출하세요',
        'https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?q=80&w=400&auto=format&fit=crop'),
       ('고대 유적', '고대 유적의 수수께끼를 풀어보세요',
        'https://images.unsplash.com/photo-1503177119275-0aa32b3a9368?q=80&w=400&auto=format&fit=crop'),
       ('미스터리 호텔', '호텔에서 벌어진 사건을 해결하세요',
        'https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=400&auto=format&fit=crop'),
       ('지하 벙커', '폐쇄된 지하 벙커에서 탈출하세요', 'https://picsum.photos/seed/bunker/400/300');

-- store_theme
INSERT INTO store_theme (store_id, theme_id)
VALUES (1, 1),
       (3, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (2, 5),
       (2, 6),
       (2, 7),
       (2, 8),
       (3, 9),
       (3, 10),
       (3, 11),
       (3, 12);

-- member
INSERT INTO member (name, email, password, role)
VALUES ('브라운', 'brown@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('제임스', 'james@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('코니', 'connie@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('샐리', 'sally@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('네오', 'neo@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('프로도', 'frodo@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('무지', 'muzi@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('어피치', 'apeach@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('레오나드', 'leonard@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('문', 'moon@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('포비', 'pobi@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('크롱', 'crong@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('루피', 'loopy@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('에디', 'eddy@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('패티', 'petty@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('해리', 'harry@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('로디', 'roady@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('뽀로로', 'pororo@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('타요', 'tayo@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('로기', 'rogi@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('라니', 'rani@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('가니', 'gani@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('시투', 'citu@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('하나', 'hana@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('토토로', 'totoro@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('지브리', 'ghibli@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('카논', 'kanon@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('치히로', 'chihiro@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'USER'),
       ('관리자', 'admin@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'ADMIN'),
       ('잠실관리자', 'adminn@email.com', '$2a$10$.6HkvA/I6pYI5so0KJGlgOY/yJBkvNwFcYIclTGME/fT6BIHRhjwi', 'ADMIN');

-- admin_store
INSERT INTO admin_store (member_id, store_id)
VALUES (29, 1),
       (29, 2);

-- reservation
-- 인기 테마 산정 기준: 2026-05-15 기준 최근 7일(05-08 ~ 05-14)
INSERT INTO reservation (member_id, store_id, `date`, time_id, theme_id)
VALUES (1, 1, '2026-05-14', 1, 1),
       (2, 1, '2026-05-14', 2, 1),
       (3, 1, '2026-05-13', 3, 1),
       (4, 1, '2026-05-13', 4, 1),
       (5, 1, '2026-05-12', 5, 1),
       (6, 1, '2026-05-12', 1, 1),
       (7, 1, '2026-05-11', 2, 1),
       (8, 1, '2026-05-10', 3, 1),
       (9, 1, '2026-05-09', 4, 1),
       (10, 1, '2026-05-08', 5, 1);

INSERT INTO reservation (member_id, store_id, `date`, time_id, theme_id)
VALUES (11, 1, '2026-05-14', 1, 2),
       (12, 1, '2026-05-14', 2, 2),
       (13, 1, '2026-05-13', 3, 2),
       (14, 1, '2026-05-13', 4, 2),
       (15, 1, '2026-05-12', 5, 2),
       (16, 1, '2026-05-11', 6, 2),
       (17, 1, '2026-05-10', 7, 2),
       (18, 1, '2026-05-09', 1, 2);

INSERT INTO reservation (member_id, store_id, `date`, time_id, theme_id)
VALUES (19, 1, '2026-05-14', 1, 3),
       (20, 1, '2026-05-13', 2, 3),
       (21, 1, '2026-05-12', 3, 3),
       (22, 1, '2026-05-11', 4, 3),
       (23, 1, '2026-05-10', 5, 3),
       (24, 1, '2026-05-09', 6, 3);

INSERT INTO reservation (member_id, store_id, `date`, time_id, theme_id)
VALUES (25, 1, '2026-05-14', 1, 4),
       (26, 1, '2026-05-13', 2, 4),
       (27, 1, '2026-05-12', 3, 4),
       (28, 1, '2026-05-11', 4, 4);

-- 미래 예약 데이터
INSERT INTO reservation (member_id, store_id, `date`, time_id, theme_id)
VALUES (1, 1, '2026-05-15', 10, 1),
       (2, 1, '2026-05-16', 11, 2),
       (3, 1, '2026-05-17', 12, 3);
