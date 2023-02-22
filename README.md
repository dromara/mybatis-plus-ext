<p align="center"><img src="https://s2.loli.net/2022/04/02/wibvoFgKym4NY57.png" alt="1648883788444-1068117e-f573-4b0b-bbb9-8a3208810860.png" width="150px" /></p>



<p align="center">借用MybatisPlus的口号：为简化开发工作、提高生产率而生</p>

<p align="center">
<img src="https://img.shields.io/maven-central/v/com.baomidou/mybatis-plus.svg?style=for-the-badge" alt="img" /> 
<img src="https://img.shields.io/badge/license-Apache 2-4EB1BA.svg?style=for-the-badge" alt="img" />
</p>



&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;尽管[MybatisPlus](https://gitee.com/baomidou/mybatis-plus) （后文简称MP）相比较Mybatis丝滑了很多，但是，日常使用中，是否偶尔仍会怀念JPA（Hibernate）的那种纵享丝滑的感受，更好的一心投入业务开发中，如果你也是如此，那么恭喜你发现了MybatisPlusExt（后文简称MPE）。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MPE对MP做了进一步的拓展封装，即保留MP原功能，又添加更多有用便捷的功能。同样坚持与MP对Mybatis的原则，只做增强不做改变，所以，即便是在使用MPE的情况下，也完全可以百分百的使用MP的方式，因此MP能做的，MPE不仅能做还能做的更多。实际上MPE只入侵了MP的一个类（TableInfoHelper），因为要完成注解继承合并的操作，必须重写MP的注解读取逻辑。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;增强功能具体体现在几个方面：自动建表（仅支持mysql）、数据自动填充（类似JPA中的审计）、关联查询（类似sql中的join）、冗余数据自动更新、动态条件等功能做了补充完善。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;如果感觉本框架对您有所帮助，烦请去[Gitee](https://gitee.com/tangzc/mybatis-plus-ext) 给个小星星⭐️，欢迎来撩共同进步。

<p align="center"><img src="https://s2.loli.net/2022/04/02/Sc6uMsaKNY9nWBE.png" alt="img" width="200px" /></p>

## TODO

> 如果大家有什么好的建议，欢迎提出来，视能力与时间，慢慢实现。

* 自动建表，多种数据库支持（3.0.0分支）
  - [x] 重构自动建表相关的代码
  - [x] 默认支持MySQL
  - [x] 支持SQLite
  - [ ] 支持PgSql
* 自动建表，添加SQL执行过程留痕功能，方便生产环境直接导出使用
  - [ ] 新增配置项：`mpe.auto-table.record-sql`配置项，配置SQL记录到数据库
* 加密解密场景
  - [ ] 新增`@Encrypt`: {autoDecode: boolean(默认ture), mode: enum(默认BASE64), encryptHandle?: class\<IEncryptHandler>}，字段自动加密（类似密码字段的需求，内置几种常用算法）
  - [ ] 新增`IEncryptHandler`接口，开放给使用方自定义加密解密算法。
  - [ ] 新增`IEncryptCompare`接口（空接口，内涵默认实现），Bean实现该接口，可具备对比不可逆加密算法的值，比如用户密码的场景。

## 官方教程

<a style="font-size:20px" href="https://www.yuque.com/dontang/codewiki/gzqgd8" target="_blank">Mybatis-Plus-Ext教程</a>

## 特别感谢
> 感谢JetBrains提供的软件支持

<img width="200" src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png" alt="JetBrains Logo (Main) logo.">