DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS compilation_event CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY UNIQUE,
    name    VARCHAR                                 NOT NULL,
    email   VARCHAR                                 NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories
(
    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY UNIQUE,
    name        VARCHAR                                 NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events
(
    event_id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    annotation         VARCHAR(2000)                           NOT NULL,
    category_id        BIGINT                                  NOT NULL REFERENCES categories (category_id),
    create_date        TIMESTAMP,
    description        VARCHAR(7000)                           NOT NULL,
    event_date         TIMESTAMP,
    initiator_id       BIGINT                                  NOT NULL REFERENCES users (user_id),
    lat                FLOAT                                   NOT NULL,
    lon                FLOAT                                   NOT NULL,
    paid               BOOLEAN                                 NOT NULL,
    participant_limit  BIGINT                                  NOT NULL,
    available          BOOLEAN,
    published_on       TIMESTAMP,
    request_moderation BOOLEAN                                 NOT NULL,
    state              VARCHAR                                 NOT NULL,
    title              VARCHAR(120)                            NOT NULL
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    created      TIMESTAMP                               NOT NULL,
    event_id     BIGINT                                  NOT NULL REFERENCES events (event_id),
    requester_id BIGINT                                  NOT NULL REFERENCES users (user_id),
    status       VARCHAR(32)                             NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations
(
    compilation_id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    pinned            BOOLEAN                                 NOT NULL,
    compilation_title VARCHAR                                 NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS compilation_event
(
    event_id       BIGINT NOT NULL REFERENCES events (event_id) ON DELETE CASCADE,
    compilation_id BIGINT NOT NULL REFERENCES compilations (compilation_id) ON DELETE CASCADE,
    PRIMARY KEY(event_id, compilation_id)
);