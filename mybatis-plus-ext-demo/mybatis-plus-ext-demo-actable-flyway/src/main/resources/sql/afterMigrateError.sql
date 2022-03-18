-- SQL 执行失败，清理 flyway 元数据表中失败的执行记录
DELETE IGNORE FROM `${flyway-table}` WHERE success = 0;