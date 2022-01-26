-- ----------------------------
-- Table structure for t_country
-- ----------------------------
DROP TABLE IF EXISTS `t_country`;
create table t_country
(
    id   bigint(30) auto_increment primary key,
    code varchar(255) null,
    name varchar(255) null
);

INSERT INTO t_country (id, code, name)
VALUES (1, 'CN', '中国');
INSERT INTO t_country (id, code, name)
VALUES (2, 'US', '美国');
INSERT INTO t_country (id, code, name)
VALUES (3, 'GB', '英国');
INSERT INTO t_country (id, code, name)
VALUES (4, 'FR', '法国');
INSERT INTO t_country (id, code, name)
VALUES (5, 'RU', '俄罗斯');

-- ----------------------------
-- Table structure for t_city
-- ----------------------------
DROP TABLE IF EXISTS `t_city`;
create table t_city
(
    id      bigint auto_increment
        primary key,
    name    varchar(255) null,
    state   varchar(255) null,
    country varchar(255) null
);
INSERT INTO t_city (id, name, state, country)
VALUES (6, 'San Francisco', 'CA', 'US');
INSERT INTO t_city (id, name, state, country)
VALUES (7, '合肥', '安徽', '中国');
INSERT INTO t_city (id, name, state, country)
VALUES (8, '杨浦', '上海', '中国');
INSERT INTO t_city (id, name, state, country)
VALUES (9, '徐汇', '上海', '中国');
INSERT INTO t_city (id, name, state, country)
VALUES (10, '南京', '江苏', '中国');


-- ----------------------------
-- Table structure for sys_privilege
-- ----------------------------
DROP TABLE IF EXISTS `sys_privilege`;
CREATE TABLE `sys_privilege`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `privilege_name` varchar(50)  DEFAULT NULL COMMENT '权限名称',
    `privilege_url`  varchar(200) DEFAULT NULL COMMENT '权限URL',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  DEFAULT CHARSET = utf8 COMMENT ='权限表';

-- ----------------------------
-- Records of sys_privilege
-- ----------------------------
INSERT INTO `sys_privilege`
VALUES ('1', '用户管理', '/users');
INSERT INTO `sys_privilege`
VALUES ('2', '角色管理', '/roles');
INSERT INTO `sys_privilege`
VALUES ('3', '系统日志', '/logs');
INSERT INTO `sys_privilege`
VALUES ('4', '人员维护', '/persons');
INSERT INTO `sys_privilege`
VALUES ('5', '单位维护', '/companies');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`   varchar(50) DEFAULT NULL COMMENT '角色名',
    `enabled`     int(11)     DEFAULT NULL COMMENT '有效标志',
    `create_by`   bigint(20)  DEFAULT NULL COMMENT '创建人',
    `create_time` datetime    DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8 COMMENT ='角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role`
VALUES ('1', '管理员', '1', '1', '2016-04-01 17:02:14');
INSERT INTO `sys_role`
VALUES ('2', '普通用户', '1', '1', '2016-04-01 17:02:34');

-- ----------------------------
-- Table structure for sys_role_privilege
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_privilege`;
CREATE TABLE `sys_role_privilege`
(
    `role_id`      bigint(20) DEFAULT NULL COMMENT '角色ID',
    `privilege_id` bigint(20) DEFAULT NULL COMMENT '权限ID'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='角色权限关联表';

-- ----------------------------
-- Records of sys_role_privilege
-- ----------------------------
INSERT INTO `sys_role_privilege`
VALUES ('1', '1');
INSERT INTO `sys_role_privilege`
VALUES ('1', '3');
INSERT INTO `sys_role_privilege`
VALUES ('1', '2');
INSERT INTO `sys_role_privilege`
VALUES ('2', '4');
INSERT INTO `sys_role_privilege`
VALUES ('2', '5');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `user_name`     varchar(50) DEFAULT NULL COMMENT '用户名',
    `user_password` varchar(50) DEFAULT NULL COMMENT '密码',
    `user_email`    varchar(50) DEFAULT 'test@mybatis.tk' COMMENT '邮箱',
    `user_info`     text COMMENT '简介',
    `head_img`      blob COMMENT '头像',
    `create_time`   datetime    DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1035
  DEFAULT CHARSET = utf8 COMMENT ='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user`
VALUES ('1', 'admin', '123456', 'admin@mybatis.tk', '管理员用户', 0x1231231230, '2016-06-07 01:11:12');
INSERT INTO `sys_user`
VALUES ('1001', 'test', '123456', 'test@mybatis.tk', '测试用户', 0x1231231230, '2016-06-07 00:00:00');
-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
    `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='用户角色关联表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role`
VALUES ('1', '1');
INSERT INTO `sys_user_role`
VALUES ('1', '2');
INSERT INTO `sys_user_role`
VALUES ('1001', '2');

-- ----------------------------
-- Table structure for user info
-- ----------------------------
DROP TABLE IF EXISTS `user info`;
CREATE TABLE `user info`
(
    `id` int(11) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- t_user
-- ----------------------------
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user
(
    id    BIGINT(20)  NOT NULL COMMENT '主键ID',
    name  VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
    age   INT(11)     NULL DEFAULT NULL COMMENT '年龄',
    email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
    PRIMARY KEY (id)
);
DELETE
FROM t_user;
INSERT INTO t_user (id, name, age, email)
VALUES (1, 'Jone', 18, 'test1@baomidou.com'),
       (2, 'Jack', 20, 'test2@baomidou.com'),
       (3, 'Tom', 28, 'test3@baomidou.com'),
       (4, 'Sandy', 21, 'test4@baomidou.com'),
       (5, 'Billie', 24, 'test5@baomidou.com');