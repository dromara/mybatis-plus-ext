# Mybatis-Plus-Ext

## 简介

​		本框架结合公司日常业务场景，对[Mybatis-Plus](https://gitee.com/baomidou/mybatis-plus)做了进一步的拓展封装，即保留MP原功能，又添加更多有用便捷的功能。具体拓展体现在`数据自动填充（类似JPA中的审计）`、`数据绑定（类似sql中的join）`、`自动建表（仅支持mysql）`、`冗余数据自动更新`、`固定条件`等功能做了补充完善。其中`自动建表`，是在[A.CTable](https://gitee.com/sunchenbin/mybatis-enhance)框架上的集成优化，部分优化也会反馈回原框架，绝大部分功能均是通用的，详细教程可以直接参考A.CTable官方。

## 快速开始

### 自动建表

> 根据实体上的注解及字段注解自动创建、更新数据库表。
>
> 官方的设计思路是默认Bean下的所有字段均不是表字段，需要手动通过@Column声明，我在引用过来之后，改为了默认所有字段均为表字段，只有被MP的@TableField(exist=false)修饰的才会被排除，具备@TableField(exist=false)功能的注解有：@Exclude、@Bind**系列，他们集成了@TableField，且内置exist属性为false了。

```java
@Data
// @Table、@Entity、@TableName均可被识别为需要自动创建表的Entity
@Table(comment = "用户")
public class User {
	
    // 自动识别id属性名为主键
    // @IsAutoIncrement声明为自增主键，什么都不声明的话，默认为雪花算法的唯一主键（MP的自带功能）
    @IsAutoIncrement
    // 字段注释
    @ColumnComment("主键")
    private Long id;

    // 索引
    @Index
    // 非空
    @IsNotNull
    @ColumnComment("名字")
    private String name;
    
    // 唯一索引
    @Unique
    @IsNotNull
    @ColumnComment("手机号")
    private String phone;
    
    // 省略其他属性
    ......
}
```

```java
// 启用自动生成数据库表功能，此处简化了A.CTable的复杂配置，均采用默认配置
@EnableAutoTable
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

```properties
# actable的配置信息保留了如下几项，均做了默认配置，正常无需配置
actable.table.auto=update
actable.model.pack=[Spring启动类所在包]
actable.database.type=mysql
actable.index.prefix=自己定义的索引前缀#该配置项不设置默认使用actable_idx_
actable.unique.prefix=自己定义的唯一约束前缀#该配置项不设置默认使用actable_uni_
```



### 数据填充

> 可以在数据插入或更新的时候，自动赋值数据操作人、操作时间、默认值等属性。
>
> 以文章发布为例，讲解一下数据填充的基本用法。通过如下例子可发现，在创建Artice的时候，我们无需再去关心过多的与业务无关的字段值，只需要关心`title`、`content`两个核心数据即可，其他的数据均会被框架处理。

```java
@Data
@Table(comment = "文章")
public class Article {
	
    // 字符串类型的ID，默认也是雪花算法的一串数字（MP的默认功能）
    @ColumnComment("主键")
    private String id;

    @ColumnComment("标题")
    private String title;
    
    @ColumnComment("内容")
    private String content;
    
    // 文章默认激活状态
    @DefaultValue("ACTIVE")
    @ColumnComment("内容")
    // ActicleStatusEnum(ACTIVE, INACTIVE)
    private ActicleStatusEnum status;

    @ColumnComment("发布时间")
    // 插入数据时候会自动获取系统当前时间赋值，支持多种数据类型，具体可参考@OptionDate注解详细介绍
    @InsertOptionDate
    private Date publishedTime;

    @ColumnComment("发布人")
    // 插入的时候，根据UserIdAutoFillHandler自动填充用户id
    @InsertOptionUser(UserIdAutoFillHandler.class)
    private String publishedUserId;

    @ColumnComment("发布人名字")
    // 插入的时候，根据UserIdAutoFillHandler自动填充用户名字
    @InsertOptionUser(UsernameAutoFillHandler.class)
    private String publishedUsername;

    @ColumnComment("最后更新时间")
    // 插入和更新数据时候会自动获取系统当前时间赋值，支持多种数据类型，具体可参考@OptionDate注解详细介绍
    @InsertUpdateOptionDate
    private Date publishedTime;

    @ColumnComment("最后更新人")
    // 插入和更新的时候，根据UserIdAutoFillHandler自动填充用户id
    @InsertUpdateOptionUser(UserIdAutoFillHandler.class)
    private String publishedUserId;

    @ColumnComment("最后更新人名字")
    // 插入和更新的时候，根据UserIdAutoFillHandler自动填充用户名字
    @InsertUpdateOptionUser(UsernameAutoFillHandler.class)
    private String publishedUsername;
}
```

```java
/**
 * 全局获取用户ID
 * 此处实现IOptionByAutoFillHandler接口和AutoFillHandler接口均可，建议实现IOptionByAutoFillHandler接口，
 * 因为框架内的BaseEntity默认需要IOptionByAutoFillHandler的实现。后面会讲到BaseEntity的使用。
 */
@Component
public class UserIdAutoFillHandler implements IOptionByAutoFillHandler<String> {

    /**
     * @param object 当前操作的数据对象
     * @param clazz  当前操作的数据对象的class
     * @param field  当前操作的数据对象上的字段
     * @return 当前登录用户id
     */
    @Override
    public String getVal(Object object, Class<?> clazz, Field field) {
      	RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        // 配合网关或者过滤器，token校验成功后就把用户信息塞到header中
        return request.getHeader("user-id");
    }
}
```

```java
/**
 * 全局获取用户名
 */
@Component
public class UsernameAutoFillHandler implements AutoFillHandler<String> {

    /**
     * @param object 当前操作的数据对象
     * @param clazz  当前操作的数据对象的class
     * @param field  当前操作的数据对象上的字段
     * @return 当前登录用户id
     */
    @Override
    public String getVal(Object object, Class<?> clazz, Field field) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        // 配合网关或者过滤器，token校验成功后就把用户信息塞到header中
        return request.getHeader("user-name");
    }
}
```

### 数据绑定

> 数据关联查询的解决方案，替代sql中的join方式，通过注解关联多表之间的关系，查询某实体的时候，自动带出其关联性的数据实体。
>
> 本示例以比较复杂的通过中间表关联数据的案例来讲解下，用户和角色之间多对多，通过中间表进行数据级联，@BindEntity\*系列是关联Entity的数据，@BindField\*系列是关联Entity下的某个字段。当@Bind\*系列注解用在对象上即表达一对一，当注解在List上时便表达一对多的意思，当外部对象本身就是查询集合的情况下便是多对多的场景了。

```java
@Data
@Table(comment = "角色信息")
public class Rule {

    @ColumnComment("主键")
    private String id;

    @ColumnComment("角色名")
    private String name;
}
```

```java
@Data
@Table(comment = "用户信息")
public class User {

    @ColumnComment("主键")
    private String id;

    @ColumnComment("用户名")
    private String username;

    @ColumnComment("密码")
    private String password;

    // 关键配置，声明了User想关联对应的Rule集合，中间表是UserRule
    @BindEntityByMid(condition = @MidCondition(
            midEntity = UserRule.class, selfMidField = "userId", joinMidField = "ruleId"
    ))
    private List<Rule> rules;
}
```

```java
@Data
@Table(comment = "用户-角色关联关系")
public class UserRule {

    @ColumnComment("主键")
    private String id;

    @ColumnComment("用户id")
    private String userId;

    @ColumnComment("角色id")
    private String ruleId;
}
```

```java
/**
 * 用户服务
 */
@Slf4j
@Service
public class UserService {

    // UserRepository继承了BaseRepository<UserMapper, User>
    @Resource
    private UserRepository userRepository;

    /**
     * 根据用户的名字模糊查询所有用户的详细信息
     */
    @Transactional(readOnly = true)
    public List<UserDetailWithRuleDto> searchUserByNameWithRule(String name) {

        // MP的lambda查询方式
        List<User> userList = this.lambdaQuery()
               .eq(name != null, User::getUsername, name)
               .list();
        // 关键步骤，指定关联角色数据。如果你打开sql打印，会看到3条sql语句，第一条根据id去User表查询user信息，第二条根据userId去UserRule中间表查询所有的ruleId，第三条sql根据ruleId集合去Rule表查询全部的权限
        Binder.bindOn(userList, User::getRules);
        // Binder.bind(userList); 此种用法默认关联user下所有声明需要绑定的元素
        
        return UserMapping.MAPPER.toDto5(userList);
    }

    /**
     * 根据用户的名字模糊查询所有用户的详细信息，等价于上一个查询方式
     */
    @Transactional(readOnly = true)
    public List<UserDetailWithRuleDto> searchUserByNameWithRule2(String name) {

        // 本框架拓展的lambda查询器lambdaQueryPlus，增加了bindOne、bindList、bindPage
        // 显然这是一种更加简便的查询方式
        List<User> userList = this.lambdaQueryPlus()
               .eq(name != null, User::getUsername, name)
               .bindList(User::getRules);
        
        return UserMapping.MAPPER.toDto5(userList);
    }
}
```

### 数据冗余

> 当其他表的数据需要作为当前表的查询条件的时候，多数情况下会使用sql的join语法，另一种方案是做数据冗余，讲其他表的字段作为当前表的字段，但是牵扯一个数据修改后同步的问题，本框架可以解决。
>
> 假设用户评论的场景，评论上需要冗余用户名和头像，如果用户的名字和头像有改动，则需要同步新的改动，代码如下：

```java
@Data
@Table(comment = "用户信息")
public class User {

    @ColumnComment("主键")
    private String id;

    @ColumnComment("用户名")
    private String username;

    @ColumnComment("头像")
    private String icon;
    
    // 省略其他属性
    ......
}
```

```java
@Data
@Table(comment = "评论")
public class Comment {

    @ColumnComment("主键")
    private String id;

    @ColumnComment("评论内容")
    private String content;

    @ColumnComment("评论人id")
    private String userId;

    // 基于该注解，框架会自动注册监听EntityUpdateEvent事件，User的updateById和updateBatchById两个方法会自动发布EntityUpdateEvent事件
    @DataSource(source = User.class, field = "username", condition = @Condition(selfField = "userId"))
    @ColumnComment("评论人名称")
    private String userName;

    @DataSource(source = User.class, field = "icon", condition = @Condition(selfField = "userId"))
    @ColumnComment("评论人头像")
    private String userIcon;
}
```

### 固定条件

> 使用场景比较少，之所以添加这样一个注解是因为平时工作中当针对Entity做充血模型的设计的时候，会出现一个表对应多个实体的情况，每个实体必然有一个固定值的字段，类似类型，对应表中的一个类型区分不同的实体，此时可以使用该方案。
>
> 假设一个场景，设备表，里面有个字段标记了设备类型（大、中、小），然后不同类型设备存在不同的行为，比如，大型设备在充血模型设计下，有一个定期保养行为，此时就可以如下设计表与Entity的关系

```java
@Data
@TableName("device")
@TableComment("大型设备")
public class BigDevice {

    @ColumnComment("主键")
    private String id;

    @ColumnComment("设备名")
    private String score;

    // 只要是通过BigDevice的Mapper对数据进行更新和查询，都会自动带上条件type='BIG'
    // DeviceTypeEnum(BIG, MEDIUM, TINY)
    @FixedCondition("BIG")
    @ColumnComment("设备类型")
    private DeviceTypeEnum type;
    
    @ColumnComment("保养日期")
    private Date maintainTime;

    /**
     * 维修保养
     */
    public void maintain() {
        // 省略其他逻辑
        this.maintainTime = new Date();
    }
}
```

### BaseEntity使用

> 通常的表设计中，都会要求添加一些审计数据，比如创建人、创建时间、最后修改人、最后修改时间，但是这些属性又不应该属于业务的，更多的是为了数据管理使用的。如果业务需要使用的话，建议起一个有意义的业务名称与上述的创建时间区分开，比如用户的注册时间。为了简化数据审计字段的工作量，框架内部集成了BaseEntity

```java
@Getter
@Setter
public class BaseEntity<ID_TYPE extends Serializable, TIME_TYPE> {

    // 这里就是数据填充样例那里提到的IOptionByAutoFillHandler接口
    @InsertOptionUser(IOptionByAutoFillHandler.class)
    @ColumnComment("创建人")
    protected ID_TYPE createBy;
    @InsertUpdateOptionUser(IOptionByAutoFillHandler.class)
    @ColumnComment("最后更新人")
    protected ID_TYPE updateBy;
    @InsertOptionDate
    @ColumnComment("创建时间")
    protected TIME_TYPE createTime;
    @InsertUpdateOptionDate
    @ColumnComment("最后更新时间")
    protected TIME_TYPE updateTime;
}
```

> 还存在某些情况下数据表要求设计成逻辑删除（逻辑删除存在很多弊端，不建议无脑所有表都设计为逻辑删除），所以框架同时提供了一个BaseLogicEntity

```java
@Getter
@Setter
public class BaseLogicEntity<ID_TYPE extends Serializable, TIME_TYPE> extends BaseEntity<ID_TYPE, TIME_TYPE> {

    // 使用了MP支持的逻辑删除注解
    @TableLogic
    @DefaultValue("0")
    @ColumnComment("逻辑删除标志")
    protected Integer deleted;
}
```

### BaseRepository使用

> 建议以此为数据基本操作类，而不是以Mapper为基础操作类，该service可以直接通过getMapper()取得Entity对应的Mapper类，此类与Mapper类相比做了很多的增强功能，尤其是其lambda语法，非常高效便捷。

```java
// 集成了MP的ServiceImpl，实现了IBaseRepository接口（内部拓展了lambda查询操作）
public abstract class BaseRepository<M extends BaseMapper<E>, E> extends ServiceImpl<M, E> implements IBaseRepository<E> {

    @Override
    public boolean updateById(E entity) {
        boolean result = super.updateById(entity);
        if(result) {
            // 数据自动更新@DataSource注解的配合逻辑，
            SpringContextUtil.getApplicationContext()
                .publishEvent(EntityUpdateEvent.create(entity));
        }
        return result;
    }

    @Override
    public boolean updateBatchById(Collection<E> entityList, int batchSize) {
        boolean result = super.updateBatchById(entityList, batchSize);
        if(result) {
            for (E entity : entityList) {
                SpringContextUtil.getApplicationContext().publishEvent(EntityUpdateEvent.create(entity));
            }
        }
        return result;
    }
}
```

## 注解详细介绍

### 自动建表注解

所有注解均是通用的，详细教程可以直接参考A.CTable官方。

---

### 数据填充类注解

#### `@OptionDate`

**描述：**

> 自动赋值数据操作时间。需结合mybatis-plus原框架注解[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield)（该注解的使用请查看官方文档，懒得看的话，请往下读，有惊喜）一并使用才有效。
>
> 被标注的字段，在可允许的类型范围（`String`、`Long`、`long`、`Date`、`LocalDate`、`LocalDateTime`）内，数据被操作的情况下，会自动被赋值上当前时间。
>
> ***如果使用String的话需要同时指明`format`参，用以确认格式化后的样式。***

**字段：**

| 属性     | 类型    | 必需   | 默认值              | 描述                                     |
| -------- | ------- | ------ | ------------------- | ---------------------------------------- |
| format   | String  | 非必需 | yyyy-MM-dd HH:mm:ss | 如果字段类型为String，需要制定字符串格式 |
| override | boolean | 非必需 | true                | 若对象上存在值，是否覆盖                 |

**扩展注解：**

| 注解                      | 描述                                                         |
| ------------------------- | ------------------------------------------------------------ |
| `@InsertOptionDate`       | 基于`@OptionDate`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield)，数据**插入**的时候，自动赋值数据操作时间。 |
| `@UpdateOptionDate`       | 基于`@OptionDate`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield)，数据**更新**（***注意：update(Wrapper<T> updateWrapper)方法除外***）的时候，自动赋值数据操作时间。 |
| `@InsertUpdateOptionDate` | 基于`@OptionDate`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield)，数据**插入**、**更新**（***注意：update(Wrapper<T> updateWrapper)方法除外***）的时候，自动赋值数据操作时间。 |

#### `@OptionUser`

**描述：**

> 指定实现方式，自动赋值数据操作人员信息。需结合mybatis-plus原框架注解[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield)（该注解的使用请查看官方文档，懒得看的话，请往下读，有惊喜）一并使用才有效。
>
> 被标注的字段，会根据`@OptionUser`中`AuditHandler`的实现来返回对应的值。
>
> 通常的实现方案都是用户信息（id、name等）放入header中，全局定义函数来获取。

**字段：**

| 属性     | 类型                             | 必需   | 默认值 | 描述                     |
| -------- | -------------------------------- | ------ | ------ | ------------------------ |
| value    | Class<? extends AuditHandler<?>> | 必需   |        | 自定义用户信息生成方式   |
| override | boolean                          | 非必需 | true   | 若对象上存在值，是否覆盖 |

**扩展注解：**

| 注解                      | 描述                                                         |
| ------------------------- | ------------------------------------------------------------ |
| `@InsertOptionUser`       | 基于`@OptionUser`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield)，数据**插入**的时候，自动赋值操作人信息。 |
| `@UpdateOptionUser`       | 基于`@OptionUser`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield)，数据**更新**（***注意：update(Wrapper<T> updateWrapper)方法除外***）的时候，自动赋值操作人信息。 |
| `@InsertUpdateOptionUser` | 基于`@OptionUser`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield)，数据**插入**、**更新**（***注意：update(Wrapper<T> updateWrapper)方法除外***）的时候，自动赋值操作人信息。 |

#### `@DefaultValue`

**描述：**

> 数据插入的时候字段的默认值，支持类型：String, Integer, int, Long, long, Boolean, boolean, Double, double, Float, float, BigDecimal, Date, LocalDate, LocalDateTime，枚举（仅支持枚举的名字作为默认值）

**字段：**

| 属性   | 类型    | 必需   | 默认值              | 描述                                                         |
| ------ | ------- | ------ | ------------------- | ------------------------------------------------------------ |
| value  | String  | 必需   |                     | 默认值                                                       |
| format | boolean | 非必需 | yyyy-MM-dd HH:mm:ss | 如果字段类型为时间类型（Date,LocalDateTime等），需要制定字符串格式 |

---

### 关联查询类注解

#### `@BindField`

**描述：**

> 绑定其他Entity的某个字段，可实现一对一、一对多的绑定查询。
>
> 注意：所有Bind注解底层均依赖相关Entity的Mapper，且Mapper必须继承MybatisPlus的BaseMapper<Entity, ID>

**字段：**

| 属性            | 类型             | 必需 | 默认值 | 描述                                                         |
| --------------- | ---------------- | ---- | ------ | ------------------------------------------------------------ |
| entity          | Class<?>         | 是   |        | 被关联的Entity                                               |
| field           | String           | 是   |        | 被关联的Entity的具体字段                                     |
| condition       | @JoinCondition[] | 是   |        | 关联Entity所需要的条件                                       |
| customCondition | String           | 否   |        | 被关联的Entity所需要的额外条件，通常指被关联的Entity自身的特殊条件，例如：enable=1 and is_deleted=0 |
| orderBy         | @JoinOrderBy[]   | 否   |        | 排序条件，被关联的Entity或者字段为结果集的时候生效           |

#### `@BindEntity`

**描述：**

> 绑定其他Entity，可实现一对一、一对多的绑定查询。
>
> 注意：所有Bind注解底层均依赖相关Entity的Mapper，且Mapper必须继承MybatisPlus的BaseMapper<Entity, ID>

**字段：**

| 属性            | 类型             | 必需 | 默认值       | 描述                                                         |
| --------------- | ---------------- | ---- | ------------ | ------------------------------------------------------------ |
| entity          | Class<?>         | 否   | 字段声明类型 | 被关联的Entity，不再需要显示的指明，默认取字段上的声明类型   |
| condition       | @JoinCondition[] | 是   |              | 关联Entity所需要的条件                                       |
| customCondition | String           | 否   |              | 被关联的Entity所需要的额外条件，通常指被关联的Entity自身的特殊条件，例如：enable=1 and is_deleted=0 |
| orderBy         | @JoinOrderBy[]   | 否   |              | 排序条件，被关联的Entity或者字段为结果集的时候生效           |
| deepBind        | boolean          | 否   | false        | 深度绑定，列表数据的情况下会产生性能问题。（不熟悉的，不建议使用） |

#### `@JoinCondition`

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

#### `@JoinOrderBy`

**描述：**

> 绑定结果的排序

**字段：**

| 属性  | 类型    | 必需 | 默认值 | 描述                           |
| ----- | ------- | ---- | ------ | ------------------------------ |
| field | String  | 是   |        | 被关联的Entity中结果集排序字段 |
| isAsc | boolean | 否   | false  | 排序，true:正序，false:倒序    |

#### `@BindFieldByMid`

**描述：**

> 通过中间关系Entity的形式绑定其他Entity的某个字段，可实现一对一、一对多、多对多的绑定查询。
>
> 注意：所有Bind注解底层均依赖相关Entity的Mapper，且Mapper必须继承MybatisPlus的BaseMapper<Entity, ID>

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

> 通过中间关系Entity的形式绑定其他Entity，可实现一对一、一对多、多对多的绑定查询。
>
> 注意：所有Bind注解底层均依赖相关Entity的Mapper，且Mapper必须继承MybatisPlus的BaseMapper<Entity, ID>

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

---

### 数据同步注解

#### `@DataSource`

**描述：**

> 通过注解指定数据来源，底层框架自动通过Spring中的事件机制监听EntityUpdateEvent事件，完成数据自动更新。在BaseRepository<Mapper, Entity>的基类中，默认实现了updateById、updateBatchById两个方法自动发布EntityUpdateEvent事件，所以只要对应Entity的Repository继承了BaseRepository<Mapper, Entity>便具备了通过ID更新数据的自动同步数据的功能。
>
> **拓展**：分布式情况下如何同步其他服务的数据^_^？不妨先想一想。其实sourceName属性就是为此情况预留的，引入外部MQ，监听Spring下的EntityUpdateEvent事件，然后推送至MQ，另一边消费MQ中的事件，再还原出EntityUpdateEvent事件广播到各个系统即可，这其中还需要考虑和解决时序和事务的问题。

**字段：**

| 属性       | 类型        | 必需                     | 默认值     | 描述                                             |
| ---------- | ----------- | ------------------------ | ---------- | ------------------------------------------------ |
| source     | Class<?>    | 否，与`sourceName`二选一 | Void.class | 数据来源的Entity class                           |
| sourceName | String      | 否，与`source`二选一     | ""         | 数据来源的Entity class 的全路径名称（包名.类名） |
| field      | String      | 是                       |            | 数据来源的Entity对应的属性                       |
| condition  | Condition[] | 是                       |            | 被关联的Entity所需要的条件                       |

#### `@Condition`

**描述：**

> 数据来源的关联条件

**字段：**

| 属性        | 类型   | 必需 | 默认值 | 描述                             |
| ----------- | ------ | ---- | ------ | -------------------------------- |
| selfField   | String | 是   |        | 关联数据来源Entity所需的自身字段 |
| sourceField | String | 是   | "id"   | 数据来源的Entity的字段，默认为id |

---

### 固定条件注解

#### `@FixedCondition`

**描述：**

> 固定查询条件，自带DefaultValue特性。
>
> 该注解主要用于某些特殊场景，例如多个Entity对应一张数据库表。@FixedCondition就可以自动在数据插入的时候自动填充指定的值（主要是DefaultValue的作用），数据更新和数据查询的时候，自动添加固定条件过滤数据。

**字段：**

| 属性  | 类型   | 必需 | 默认值 | 描述   |
| ----- | ------ | ---- | ------ | ------ |
| value | String | 是   |        | 固定值 |

