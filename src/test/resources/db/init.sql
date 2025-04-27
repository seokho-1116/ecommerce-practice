create table coupon
(
    id              bigint auto_increment
        primary key,
    is_active       bit          null,
    created_at      datetime     null,
    updated_at      datetime     null,
    name            varchar(255) null,
    description     varchar(255) null,
    discount_rate   double       null,
    discount_amount bigint       null,
    quantity        bigint       null,
    coupon_type     varchar(30)     null,
    `from`          datetime     null,
    `to`            datetime     null
);

create table `order`
(
    id             bigint auto_increment
        primary key,
    is_active      bit      null,
    created_at     datetime null,
    updated_at     datetime null,
    total_price    bigint   null,
    discount_price bigint   null,
    final_price    bigint   null,
    status         varchar(30) null,
    user_id        bigint   null,
    version       bigint   null
);

create table order_item
(
    id                         bigint auto_increment
        primary key,
    is_active                  bit          null,
    created_at                 datetime     null,
    updated_at                 datetime     null,
    product_name               varchar(255) null,
    product_description        varchar(255) null,
    product_option_name        varchar(255) null,
    product_option_description varchar(255) null,
    base_price                 bigint       null,
    additional_price           bigint       null,
    total_price                bigint       null,
    amount                     bigint       null,
    order_id                   bigint       null,
    product_option_id          bigint       null
);

create table point_history
(
    id               bigint auto_increment
        primary key,
    is_active        bit      null,
    created_at       datetime null,
    updated_at       datetime null,
    amount           bigint   null,
    transaction_type smallint null,
    user_id          bigint   null
);

create table product
(
    id          bigint auto_increment
        primary key,
    is_active   bit          null,
    created_at  datetime     null,
    updated_at  datetime     null,
    name        varchar(255) null,
    description varchar(255) null,
    base_price  bigint       null
);

create table product_inventory
(
    id                bigint auto_increment
        primary key,
    is_active         bit      null,
    created_at        datetime null,
    updated_at        datetime null,
    quantity          bigint   null,
    product_option_id bigint   null
);

create table product_option
(
    id               bigint auto_increment
        primary key,
    is_active        bit          null,
    created_at       datetime     null,
    updated_at       datetime     null,
    name             varchar(255) null,
    description      varchar(255) null,
    additional_price bigint       null,
    product_id       bigint       null
);

create table user
(
    id         bigint auto_increment
        primary key,
    is_active  bit          null,
    created_at datetime     null,
    updated_at datetime     null,
    name       varchar(255) null,
    email      varchar(255) null
);

create table user_coupon
(
    id         bigint auto_increment
        primary key,
    is_active  bit      null,
    created_at datetime null,
    updated_at datetime null,
    is_used    bit      null,
    coupon_id  bigint   null,
    user_id    bigint   null,
    version    bigint   null
);

create table user_point
(
    id         bigint auto_increment
        primary key,
    is_active  bit      null,
    created_at datetime null,
    updated_at datetime null,
    amount     bigint   null,
    user_id    bigint   null,
    version    bigint   null
);

create table product_selling_rank_view(
    id          bigint auto_increment
        primary key,
    is_active   bit          null,
    created_at  datetime     null,
    updated_at  datetime     null,
    product_id  bigint       null,
    `rank`        bigint       null,
    total_sales bigint       null,
    `from`       datetime     null,
    `to`         datetime     null
);