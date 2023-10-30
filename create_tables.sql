Create database library_app;
drop database library_app;
use library_app;

CREATE TABLE book
(
    isbn              VARCHAR(17) PRIMARY KEY,
    title             VARCHAR(150) NOT NULL,
    year              INT          NOT NULL,
    is_active         BOOLEAN      NOT NULL DEFAULT TRUE,
    deactivate_reason VARCHAR(40),
    publisher         VARCHAR(100) NOT NULL,
    date_added        TIMESTAMP             DEFAULT CURRENT_TIMESTAMP NOT NULL
);

create table author
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL
);
create table genre
(
    id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL
);
create table user
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    display_name  VARCHAR(50)  NOT NULL,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    `password`    VARCHAR(255) NOT NULL,
    date_of_birth DATE         NOT NULL,
    `role`        INT          NOT NULL
);
create table book_author
(
    isbn      VARCHAR(17) NOT NULL,
    author_id BIGINT      NOT NULL,
    CONSTRAINT pk_book_author
        primary key (isbn, author_id),
    constraint fk_book_author_book
        foreign key (isbn)
            references book (isbn),
    constraint fk_book_author_author
        foreign key (author_id)
            references author (id)
);
create table book_genre
(
    isbn     VARCHAR(17) NOT NULL,
    genre_id BIGINT      NOT NULL,
    CONSTRAINT pk_book_genre
        primary key (isbn, genre_id),
    constraint fk_book_genre_book
        foreign key (isbn)
            references book (isbn),
    constraint fk_book_genre_genre
        foreign key (genre_id)
            references genre (id)
);

CREATE TABLE book_audit
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    operation_type VARCHAR(100)                        NOT NULL,
    field_name     VARCHAR(100),
    old_value      LONGTEXT,
    new_value      LONGTEXT,
    user_id        BIGINT                              NOT NULL,
    book_isbn      VARCHAR(17),
    CONSTRAINT fk_book_audit_user FOREIGN KEY (user_id)
        REFERENCES user (id),
    CONSTRAINT fk_book_audit_book FOREIGN KEY (book_isbn)
        REFERENCES book (isbn)
);

CREATE TABLE rent
(
    id                   BIGINT PRIMARY KEY AUTO_INCREMENT,
    rent_date            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    expected_return_date TIMESTAMP                           NOT NULL,
    actual_return_date   TIMESTAMP,
    user_id              BIGINT                              NOT NULL,
    book_isbn            VARCHAR(17)                         NOT NULL,
    CONSTRAINT fk_rent_user FOREIGN KEY (user_id)
        REFERENCES user (id),
    CONSTRAINT fk_rent_book FOREIGN KEY (book_isbn)
        REFERENCES book (isbn)
);
ALTER TABLE user
    ADD has_prolonged_rent BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE book
    ADD available_quantity INT NOT NULL DEFAULT 0 AFTER is_active;
ALTER TABLE book
    ADD total_quantity INT NOT NULL DEFAULT 0 AFTER available_quantity;
ALTER TABLE user
    ADD subscription VARCHAR(30) NOT NULL DEFAULT 'BRONZE';
create table subscription
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    subscription_type VARCHAR(40) NOT NULL
);

ALTER TABLE user
    ADD subscription_id BIGINT;
ALTER TABLE user
    add has_unsubscribed BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE subscription
    add days_allowed smallint NOT NULL DEFAULT 30;
ALTER TABLE subscription
    add rents_allowed smallint NOT NULL DEFAULT 3;
ALTER TABLE subscription
    add price DECIMAL NOT NULL DEFAULT 0;