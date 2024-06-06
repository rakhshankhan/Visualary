DROP TABLE IF EXISTS chat_message;
DROP TABLE IF EXISTS chats;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id          SERIAL          PRIMARY KEY,
    first_name  VARCHAR(50)     NOT NULL,
    last_name   VARCHAR(50)     NOT NULL,
    email       VARCHAR(255)    UNIQUE NOT NULL,
    password    VARCHAR(255)    NOT NULL
);

CREATE TABLE chats (
    id          SERIAL          PRIMARY KEY,
    chat_id     INTEGER         NOT NULL,
    owner       INTEGER         NOT NULL,
    FOREIGN KEY (owner) REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE (chat_id, owner)
);

CREATE TABLE chat_message (
    id                      SERIAL          PRIMARY KEY,
    chat_id                 INTEGER         NOT NULL,
    role                    VARCHAR(255)    NOT NULL,
    content                 TEXT            NOT NULL,
    unprocessed_content     TEXT,
    timestamp               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (chat_id) references chats (id) ON DELETE CASCADE
);