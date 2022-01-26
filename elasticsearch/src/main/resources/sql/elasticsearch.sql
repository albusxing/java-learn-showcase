create table elasticsearch.t_goods_store
(
    id                 int auto_increment comment '主键id'
        primary key,
    store_name         varchar(256)  null comment '店铺名称',
    store_introduction varchar(512)  null comment '店铺简介',
    store_brand        varchar(64)   null comment '店铺品牌',
    open_date          date          null comment '开店时间',
    store_photo        varchar(1024) null comment '店铺图片',
    store_tags         varchar(256)  null comment '店铺标签'
);

INSERT INTO elasticsearch.t_goods_store (id, store_name, store_introduction, store_brand, open_date, store_photo, store_tags) VALUES (1, '小米销售店', '小米产品销售店', '小米', '2021-06-20', '1', '手机,电子');
INSERT INTO elasticsearch.t_goods_store (id, store_name, store_introduction, store_brand, open_date, store_photo, store_tags) VALUES (2, '华为销售店', '华为产品销售店', '华为', '2021-06-10', '1', '手机,电子');
INSERT INTO elasticsearch.t_goods_store (id, store_name, store_introduction, store_brand, open_date, store_photo, store_tags) VALUES (3, '苹果销售店', '苹果产品销售店', '苹果', '2021-06-27', '1', '手机,电子');