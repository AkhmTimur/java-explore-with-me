create table if not exists app
(
    id       bigint generated by default as identity (maxvalue 2147483647)
        constraint app_pk
            primary key,
    app_name varchar(150) not null
);

create table if not exists hits
(
    id      bigint generated by default as identity (maxvalue 2147483647)
        constraint hits_pk
            primary key,
    app_id  bigint       not null
        constraint hits_app_id_fk
            references app,
    uri     varchar(512) not null,
    ip      varchar(15)  not null,
    created timestamp    not null
);


