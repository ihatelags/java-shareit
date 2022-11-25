DROP TABLE IF EXISTS users, request, items, booking, comments CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    user_id
          BIGINT
        GENERATED
            BY
            DEFAULT AS
            IDENTITY
                       NOT
                           NULL,
    name
          VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY
        (
         user_id
            ),
    CONSTRAINT UQ_USER_EMAIL UNIQUE
        (
         email
            )
);

CREATE TABLE IF NOT EXISTS request
(
    request_id
                 bigint
        GENERATED
            BY
            DEFAULT AS
            IDENTITY
        PRIMARY
            KEY,
    description
                 varchar(200),
    requestor_id int REFERENCES users
        (
         user_id
            )
);

CREATE TABLE IF NOT EXISTS items
(
    item_id
                 bigint
        GENERATED
            BY
            DEFAULT AS
            IDENTITY
        PRIMARY
            KEY,
    name
                 varchar(100),
    description  varchar(200),
    is_available boolean,
    owner_id     int REFERENCES users
        (
         user_id
            ),
    request_id   int REFERENCES request
        (
         request_id
            )
);

CREATE TABLE IF NOT EXISTS booking
(
    booking_id
              bigint
        GENERATED
            BY
            DEFAULT AS
            IDENTITY
        PRIMARY
            KEY,
    start_date
              TIMESTAMP
                  WITHOUT
                      TIME
                      ZONE,
    end_date
              TIMESTAMP
                  WITHOUT
                      TIME
                      ZONE,
    item_id
              int
        REFERENCES
            items
                (
                 item_id
                    ),
    booker_id int REFERENCES users
        (
         user_id
            ),
    status    varchar(20)
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id
                 bigint
        GENERATED
            BY
            DEFAULT AS
            IDENTITY
        PRIMARY
            KEY,
    text
                 varchar(255),
    item_id      int REFERENCES items
        (
         item_id
            ),
    author_id    int REFERENCES users
        (
         user_id
            ),
    created_date TIMESTAMP WITHOUT TIME ZONE
);