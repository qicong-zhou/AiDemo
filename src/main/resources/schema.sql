create table memo_db.memo
(
    id          int auto_increment
        primary key,
    title       varchar(100)                        not null,
    content     text                                null,
    create_time timestamp default CURRENT_TIMESTAMP null,
    tag         varchar(200)                        null,
    update_time timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
);

