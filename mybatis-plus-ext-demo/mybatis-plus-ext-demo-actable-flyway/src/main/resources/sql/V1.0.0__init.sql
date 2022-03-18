-- 注意，1.0.0版本的sql在actable之前执行，属于特殊情况，该条数据不会插入库
INSERT INTO `auto_create_table` (`id`, `username`) VALUES (1, '张三');
COMMIT;