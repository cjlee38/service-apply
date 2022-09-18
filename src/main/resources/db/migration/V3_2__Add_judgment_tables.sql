create table judgment_item
(
    id                   bigint not null auto_increment,
    mission_id           bigint not null,
    evaluation_item_id   bigint,
    test_name            varchar(255),
    programming_language varchar(255),
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table judgment_history
(
    id             bigint       not null auto_increment,
    user_id        bigint       not null,
    mission_id     bigint       not null,
    request_key    char(36)     not null unique,
    commit_hash    char(40)     not null,
    judgment_type varchar(255) not null,
    status_code    varchar(255),
    pass_count     int,
    total_count    int,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;

create table judgment_fail_cause
(
    id          bigint   not null auto_increment,
    request_key char(36) not null unique,
    message     longtext,
    primary key (id)
) engine = InnoDB
  default charset = utf8mb4;