CREATE TABLE member
(
    id        BIGINT         NOT NULL AUTO_INCREMENT,
    name      VARCHAR(10)    NOT NULL,
    email     VARCHAR(255)   NOT NULL,
    password  VARCHAR(255)   NOT NULL,
    role      VARCHAR(10)    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE store
(
    id   BIGINT      NOT NULL AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE admin_store
(
    member_id BIGINT NOT NULL,
    store_id  BIGINT NOT NULL,
    PRIMARY KEY (member_id, store_id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (store_id) REFERENCES store (id)
);

CREATE TABLE reservation_time
(
    id       BIGINT NOT NULL AUTO_INCREMENT,
    start_at TIME   NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE theme
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    store_id    BIGINT       NOT NULL,
    name        VARCHAR(30)  NOT NULL,
    description VARCHAR(100) NOT NULL,
    thumbnail   VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (store_id) REFERENCES store (id)
);

CREATE TABLE reservation
(
    id        BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT,
    `date`    DATE   NOT NULL,
    time_id   BIGINT,
    theme_id  BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (time_id) REFERENCES reservation_time (id),
    FOREIGN KEY (theme_id) REFERENCES theme (id)
);
