## 前言

#### 新版的教程正在编写中，届时会分别针对`数据自动填充（类似JPA中的审计）`、`数据绑定（类似sql中的join）`、`自动建表（仅支持mysql）`、`冗余数据自动更新`、`固定条件`等几个方面展开，增加更多的实例。
## 使用教程（旧版）

### 一、介绍

本框架结合目前所在公司的业务场景，对Mybatis-Plus做了进一步的轻度封装，更加方便使用，在数据（创建人、创建时间、修改人、修改时间、默认值等）自动填充、数据自动同步、固定查询条件、级联查询等方面通过注解做了增强。

> 本框架引入了mybatis-plus版本（3.3.4）以及相关所需的jar包。
>
> **注意：关于mybatis相关的jar包以及page-helper相关的jar包都不要再引入了，否则会产生冲突。**

### 二、数据新增、更新增强注解

### `@OptionDate`

**描述：**

> 自动赋值数据操作时间。需结合mybatis-plus原框架注解[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield) （该注解的使用请查看官方文档，懒得看的话，请往下读，有惊喜）一并使用才有效。
>
> 被标注的字段，在可允许的类型范围（`String`、`Long`、`long`、`Date`、`LocalDate`、`LocalDateTime`）内，数据被操作的情况下，会自动被赋值上当前时间。
>
> ***如果使用String的话需要同时指明`format`参，用以确认格式化后的样式。***

**字段：**

| 属性     | 类型    | 必需 | 默认值              | 描述                                     |
| -------- | ------- | ---- | ------------------- | ---------------------------------------- |
| format   | String  | 否   | yyyy-MM-dd HH:mm:ss | 如果字段类型为String，需要制定字符串格式 |
| override | boolean | 否   | true                | 若对象上存在值，是否覆盖                 |

**扩展注解：**

| 注解                    | 描述                                                         |
| ----------------------- | ------------------------------------------------------------ |
| `@InsertOptionDate`       | 基于`@OptionDate`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield) ，数据**插入**的时候，自动赋值数据操作时间。 |
| `@UpdateOptionDate`       | 基于`@OptionDate`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield) ，数据**更新**（***注意：update(Wrapper<T> updateWrapper)方法除外***）的时候，自动赋值数据操作时间。 |
| `@InsertUpdateOptionDate` | 基于`@OptionDate`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield) ，数据**插入**、**更新**（***注意：update(Wrapper<T> updateWrapper)方法除外***）的时候，自动赋值数据操作时间。 |

### `@OptionUser`

**描述：**

> 指定实现方式，自动赋值数据操作人员信息。需结合mybatis-plus原框架注解[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield) （该注解的使用请查看官方文档，懒得看的话，请往下读，有惊喜）一并使用才有效。
>
> 被标注的字段，会根据`@OptionUser`中`AuditHandler`的实现来返回对应的值。
>
> 通常的实现方案都是用户信息（id、name等）放入header中，全局定义函数来获取。

**字段：**

| 属性     | 类型                             | 必需 | 默认值 | 描述                     |
| -------- | -------------------------------- | ---- | ------ | ------------------------ |
| value    | Class<? extends AuditHandler<?>> | 是   |        | 自定义用户信息生成方式   |
| override | boolean                          | 否   | true   | 若对象上存在值，是否覆盖 |

**扩展注解：**

| 注解                      | 描述                                                         |
| ------------------------- | ------------------------------------------------------------ |
| `@InsertOptionUser`       | 基于`@OptionUser`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield) ，数据**插入**的时候，自动赋值操作人信息。 |
| `@UpdateOptionUser`       | 基于`@OptionUser`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield) ，数据**更新**（***注意：update(Wrapper<T> updateWrapper)方法除外***）的时候，自动赋值操作人信息。 |
| `@InsertUpdateOptionUser` | 基于`@OptionUser`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield) ，数据**插入**、**更新**（***注意：update(Wrapper<T> updateWrapper)方法除外***）的时候，自动赋值操作人信息。 |

### `@DefaultValue`

**描述：**

> 数据插入的时候字段的默认值，支持类型：String, Integer, int, Long, long, Boolean, boolean, Double, double, Float, float, BigDecimal, Date, LocalDate, LocalDateTime，枚举（仅支持枚举的名字作为默认值）

**字段：**

| 属性     | 类型    | 必需   | 默认值 | 描述                     |
| -------- | ------- | ------ | ------ | ------------------------ |
| value    | String  | 是   |        | 默认值   |
| format | boolean | 否 | yyyy-MM-dd HH:mm:ss | 如果字段类型为时间类型（Date,LocalDateTime等），需要制定字符串格式 |

### `例子`

> 针对Entity增强注解，此处以文章实体为例

```java
/**
 * 文章
 */
@Data
public class Article {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 创建人
     */
    @InsertOptionUser(UserIdAutoFillHandler.class)
    private String publisherId;
    /**
     * 创建人名字
     */
    @InsertOptionUser(UserNameAutoFillHandler.class)
    private String publisherName;
    /**
     * 发布时间
     */
    @InsertOptionDate
    private Long publishTime;
    /**
     * 最后发布时间
     */
    @InsertUpdateOptionDate
    private Long lastPublishTime;
    /**
     * 文章审核状态: 0:未审核，1:审核通过，2:审核不通过
     */
    @DefaultValue("0")
    private Integer status;
}
```

> 网关层在鉴权的时候，将user的信息放入了header中，微服务的场景下可以通过feign的传递进而整个服务获取了header信息，共享了当前登录的用户信息

```java
/**
 * 全局获取用户ID
 */
public class UserIdAutoFillHandler implements AutoFillHandler<String> {

    @Override
    public String getVal(Object object, Class<?> clazz, Field field) {
      	RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        return request.getHeader("user-id");
    }
}
```

```java
/**
 * 全局获取用户名
 */
public class UserNameAutoFillHandler implements AutoFillHandler<String> {

    @Override
    public String getVal(Object object, Class<?> clazz, Field field) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        return request.getHeader("user-name");
    }
}
```

以上例子，针对文章实体的操作，创建、修改的时候只关注`title`与`content`，审核操作只关注`status`，简化了所关注的字段数量，提升了开发效率的同时也降低了错误发生几率。

### 三、数据查询增强注解

> mybatis-plus的查询已经极大的简化了单表查询的工作量，但是面对多表联合查询，仍然存在不足，尤其是面对深层次的多表联合查询，需要大量与业务无关的代码逻辑来实现数据组合，特此拓展了数据查询增强注解。

#### 三.一、单表数据绑定操作

### `@BindField`

**描述：**

> 绑定其他表的某个字段，可实现一对一、一对多的绑定查询。

**字段：**

| 属性            | 类型             | 必需 | 默认值 | 描述                                                         |
| --------------- | ---------------- | ---- | ------ | ------------------------------------------------------------ |
| entity          | Class<?>         | 是   |        | 被关联的Entity                                               |
| field           | String           | 是   |        | 被关联的Entity的具体字段                                     |
| condition       | @JoinCondition[] | 是   |        | 关联Entity所需要的条件                                       |
| customCondition | String           | 否   |        | 被关联的Entity所需要的额外条件，通常指被关联的Entity自身的特殊条件，例如：enable=1 and is_deleted=0 |
| orderBy         | @JoinOrderBy[]   | 否   |        | 排序条件，被关联的Entity或者字段为结果集的时候生效           |

### `@BindEntity`

**描述：**

> 绑定其他表的整体Entity，可实现一对一、一对多的绑定查询。

**字段：**

| 属性            | 类型             | 必需 | 默认值       | 描述                                                         |
| --------------- | ---------------- | ---- | ------------ | ------------------------------------------------------------ |
| entity          | Class<?>         | 否   | 字段声明类型 | 被关联的Entity，不再需要显示的指明，默认取字段上的声明类型   |
| condition       | @JoinCondition[] | 是   |              | 关联Entity所需要的条件                                       |
| customCondition | String           | 否   |              | 被关联的Entity所需要的额外条件，通常指被关联的Entity自身的特殊条件，例如：enable=1 and is_deleted=0 |
| orderBy         | @JoinOrderBy[]   | 否   |              | 排序条件，被关联的Entity或者字段为结果集的时候生效           |
| deepBind        | boolean          | 否   | false        | 深度绑定，列表数据的情况下会产生性能问题。（不熟悉的，不建议使用） |

### `@JoinCondition`

**描述：**

> 绑定条件

**字段：**

| 属性            | 类型             | 必需 | 默认值       | 描述                                                         |
| --------------- | ---------------- | ---- | ------------ | ------------------------------------------------------------ |
| entity          | Class<?>         | 否   | 字段声明类型 | 被关联的Entity，不再需要显示的指明，默认取字段上的声明类型   |
| condition       | @JoinCondition[] | 是   |              | 关联Entity所需要的条件                                       |
| customCondition | String           | 否   |              | 被关联的Entity所需要的额外条件，通常指被关联的Entity自身的特殊条件，例如：enable=1 and is_deleted=0，同时该属性中可以使用`{当前对象属性名}`的形式，引用当前对象的属性值 |
| orderBy         | @JoinOrderBy[]   | 否   |              | 被关联的Entity的结果集，排序条件                             |
| deepBind        | boolean          | 否   | false        | 深度绑定，列表数据的情况下会产生性能问题。（不熟悉的，不建议使用） |

### `@JoinOrderBy`

**描述：**

> 绑定结果的排序

**字段：**

| 属性  | 类型    | 必需 | 默认值 | 描述                           |
| ----- | ------- | ---- | ------ | ------------------------------ |
| field | String  | 是   |        | 被关联的Entity中结果集排序字段 |
| isAsc | boolean | 否   | false  | 排序，true:正序，false:倒序    |

### `例子`

> 身份证实体，对应数据库Identity_card表

```java
@Data
@Accessors(chain = true)
public class IdentityCard {
    /**
     * 主键
     */
    private String id;
    /**
     * 身份证号
     */
    private String number;
    /**
     * 姓名
     */
    private String name;
    /**
     * 照片地址
     */
    private String photo;
    /**
     * 用户id
     */
    private String userId;
}
```

> 账号实体，对应数据库account表

```java
@Data
@Accessors(chain = true)
public class Account {
    /**
     * 主键
     */
    private String id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 状态
     */
    private ActiveStatus status;
    
    /**
     * 绑定身份证号码
     */
    @BindField(
        entity = IdentityCard.class,
        field = "number",
        condition = {@JoinCondition(selfField = "userId", joinField = "userId")}
    )
    private String cardNumber;
}
```

> 用户实体，对应数据库user表，需要绑定account表及Identity_card表的信息

```java
@Data
@Accessors(chain = true)
public class User {
    /**
     * 主键
     */
    private String id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 性别
     */
    private SexType sex;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 根据userId绑定账户实体，同时增加限制条件status='ACTIVE'（账户必须是激活状态）
     */
    @BindEntity(
            condition = @JoinCondition(selfField = "id", joinField = "userId"),
            customCondition = "status='ACTIVE'"
    )
    private Account account;
	/**
     * 根据多个条件值绑定身份证号码，同时增加限制条件，其中{age}在执行时会被替换为当前对象内的age属性值
     * PS：此处的绑定条件都是编造的，不含实际业务场景
     */
    @BindField(
            entity = IdentityCard.class,
            field = "number",
            condition = {@JoinCondition(selfField = "id", joinField = "userId"),
                    @JoinCondition(selfField = "name", joinField = "name")},
            customCondition = "number like '{age}%'")
    )
    private String cardNumber;
}
```

> 绑定数据的操作

```java
// 查询User列表
List<User> users = innerService.lambdaQuery().list();
// 普通绑定：绑定User中的Account和cardNumber属性
Binder.bindOn(users, User::getAccount, User::getCardNumber);
// 深度绑定：绑定User中的Account中的cardNumber属性
Binder.bindOn(Deeper.with(users).in(User::getAccount), Account::getCardNumber);
// 绑定方法中，还可以使用Binder.bind(users)，该绑定方法默认绑定所有字段
```

感兴趣的可以查看下sql执行情况，会生成4条sql，分别是：

1. 第二行，查询所有user信息
2. 第四行，查询指定的user集合，对应的account信息
3. 第四行，查询指定的user集合，对应的cardNumber信息
4. 第六行，查询指定的account集合，对应的cardNumber信息

#### 三.二、中间表数据绑定操作

#### `@BindFieldByMid`

**描述：**

> 通过中间表的形式绑定其他表的某个字段，可实现一对一、一对多的绑定查询。

**字段：**

| 属性            | 类型           | 必需 | 默认值 | 描述                                                         |
| --------------- | -------------- | ---- | ------ | ------------------------------------------------------------ |
| entity          | Class<?>       | 是   |        | 被关联的Entity                                               |
| field           | String         | 是   |        | 被关联的Entity的具体字段                                     |
| condition       | @MidCondition  | 是   |        | 中间表关联条件                                               |
| customCondition | String         | 否   |        | 被关联的Entity所需要的额外条件，通常指被关联的Entity自身的特殊条件，例如：enable=1 and is_deleted=0 |
| orderBy         | @JoinOrderBy[] | 否   |        | 排序条件，被关联的Entity或者字段为结果集的时候生效           |

#### `@BindEntityByMid`

**描述：**

> 通过中间表的形式绑定其他表的某个字段，可实现一对一、一对多的绑定查询。

**字段：**

| 属性            | 类型           | 必需 | 默认值 | 描述                                                         |
| --------------- | -------------- | ---- | ------ | ------------------------------------------------------------ |
| entity          | Class<?>       | 是   |        | 被关联的Entity                                               |
| condition       | @MidCondition  | 是   |        | 中间表关联条件                                               |
| customCondition | String         | 否   |        | 被关联的Entity所需要的额外条件，通常指被关联的Entity自身的特殊条件，例如：enable=1 and is_deleted=0 |
| orderBy         | @JoinOrderBy[] | 否   |        | 排序条件，被关联的Entity或者字段为结果集的时候生效           |
| deepBind        | boolean        | 否   | false  | 深度绑定，列表数据的情况下会产生性能问题。（不熟悉的，不建议使用） |

#### `@MidCondition`

**描述：**

> 中间表条件描述

**字段：**

| 属性            | 类型           | 必需 | 默认值 | 描述                                                         |
| --------------- | -------------- | ---- | ------ | ------------------------------------------------------------ |
| entity          | Class<?>       | 是   |        | 被关联的Entity                                               |
| condition       | @MidCondition  | 是   |        | 中间表关联条件                                               |
| customCondition | String         | 否   |        | 被关联的Entity所需要的额外条件，通常指被关联的Entity自身的特殊条件，例如：enable=1 and is_deleted=0 |
| orderBy         | @JoinOrderBy[] | 否   |        | 排序条件，被关联的Entity或者字段为结果集的时候生效           |
| deepBind        | boolean        | 否   | false  | 深度绑定，列表数据的情况下会产生性能问题。（不熟悉的，不建议使用） |

### 四、数据绑定自动更新增强注解

#### `@DataSource`

**描述：**

> 数据源绑定注解，绑定指定的数据源字段后，框架会自动监听数据源表的updateById方法和updateBatchById方法，当数据源调用上述两个方法进行数据变更，对应字段会自动更新最新数据。

**字段：**

| 属性       | 类型       | 必需                     | 默认值     | 描述                                      |
| ---------- | ---------- | ------------------------ | ---------- | ----------------------------------------- |
| source     | Class<?>   | 是（与sourceName二选一） | Void.class | 需要监听数据来源的Entity                  |
| sourceName | String     | 是（与source二选一）     | ""         | 需要监听数据来源的Entity全名称（包.类名） |
| field      | String     | 是                       |            | 需要监听数据来源的Entity的具体字段        |
| condition  | @Condition | 是                       |            | 关联数据变化的Entity所需要的条件          |

### 五、固定条件增强注解

#### `@FixedCondition`

**描述：**

> 固定条件注解，常用于一表对应多实体的场景，比如一张表记录了所有用户的信息，其中分为管理员用户和普通用户，业务模型中管理员与普通用户分别为两个实体，此种情况下，可以在不同的实体声明固定类型条件，后续所有的更新、查询、删除操作，均会自动以等于的条件表达式追加该固定条件。

**字段：**

| 属性  | 类型   | 必需 | 默认值 | 描述                     |
| ----- | ------ | ---- | ------ | ------------------------ |
| value | String | 是   |        | 需要监听数据来源的Entity |

### 六、service 增强

> 框架内部提供了一个BaseService，该类基于Mybatis-Plus的[ServiceImpl](https://mybatis.plus/guide/crud-interface.html#service-crud-接口) 做了拓展，主要拓展目的是为了结合前面增强功能做的优化，其中涉及几个方面：

1. 提供了新的MyLambdaQueryChainWrapper类，用于在做级联查询的时候拓展了新的lambda语法。

   > 比如User对象上绑定了Account的信息，需要查询User信息的时候一并级联出来。

   ```java
   Page<User> page = new Page<>(1, 10);
   
   // 默认UserService继承ServiceImpl的情况下
   Page<User> pageResult = userService.lambdaQuery()
                   .like(userName != null, User::getName, userName)
                   .page(page);
   Binder.bind(pageResult);
   
   // UserService继承BaseService的情况下。该继承类兼容上面的用法
   Page<User> pageResult = userService.lambdaQueryPlus()
                   .like(userName != null, User::getName, userName)
                   .bindPage(page);
   ```

2. 提供了数据更新（@DataSource）功能自动化，拦截了updateById和updateBatchById，自动发送spring的事件通知。


