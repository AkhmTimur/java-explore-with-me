CREATE TABLE IF NOT EXISTS hits (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL CONSTRAINT hits_pk PRIMARY KEY,
    app_id BIGINT NOT NULL CONSTRAINT hits_app_id_fk REFERENCES app,
    uri VARCHAR(512) NOT NULL,
    ip VARCHAR(15) NOT NULL,
    created TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS app (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL CONSTRAINT app_pk PRIMARY KEY,
    app_name VARCHAR(150) NOT NULL
);