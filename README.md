## 简介

> 本框架结合公司日常业务场景，对[Mybatis-Plus](https://gitee.com/baomidou/mybatis-plus) 做了进一步的拓展封装，即保留MP原功能，又添加更多有用便捷的功能。具体拓展体现在`数据自动填充（类似JPA中的审计）`、`关联查询（类似sql中的join）`、`自动建表（仅支持mysql）`、`冗余数据自动更新`、`动态条件`等功能做了补充完善。其中`自动建表`，是在[A.CTable](https://gitee.com/sunchenbin/mybatis-enhance) 框架上的基础上改进适配本框架的，只保留了其表创建功能，因此改动较大不与原框架兼容。

## 前言

##### 如果感觉框架对您有所帮助，请给个小星星⭐️，作者二线不知名小公司码农一枚，欢迎来撩共同进步。![image-20210826172002744](https://raw.githubusercontent.com/imtzc/pic-store/main/markdown/20210826172002.png)

## 原理介绍

​		基于注解的形式，将日常工作中重复的模板式代码进行了封装，底层实现完全调用的Mybatis-Plus的框架，全都是走的单表查询的方式，所以不用担心数据库兼容问题（自动建表功能除外，只支持mysql），同样也不需要担心性能问题（前提是正确使用[捂脸]），因为框架内部会自动做查询整合。

## 快速开始

### 引入jar包

> starter内自带了MybatisPlus及spring-boot的依赖管理，如果要更改springboot的版本，可以排除掉，但是如果要变更MybatisPlus的版本，请注意了，框架中重写了MP中的TableInfoHelper类，不同版本的MP该类有所变动，同时框架内也采用了MP的部分工具类，例如LambdaUtils、ReflectionKit等在不同的版本也有所变动，需要小心，哈哈哈哈，可以联系我帮你改~~
>
> 框架在设计上，尽量以拓展的功能为单位做了模块拆分，所有功能均能独立引入也可以合并引入，大家视情况选用吧。

```xml
<!-- 全功能整体引入 -->
<dependency>
    <groupId>com.tangzc</groupId>
    <artifactId>mybatis-plus-ext-boot-starter</artifactId>
    <version>1.3.0</version>
</dependency>
<!-- 如果想只引入自动建表 -->
<dependency>
    <groupId>com.tangzc</groupId>
    <artifactId>mybatis-plus-ext-actable-core</artifactId>
    <version>1.3.0</version>
</dependency>
<!-- 如果想只引入关联查询 -->
<dependency>
    <groupId>com.tangzc</groupId>
    <artifactId>mybatis-plus-ext-bind</artifactId>
    <version>1.3.0</version>
</dependency>
<!-- 如果想只引入数据冗余（关联更新） -->
<dependency>
    <groupId>com.tangzc</groupId>
    <artifactId>mybatis-plus-ext-datasource</artifactId>
    <version>1.3.0</version>
</dependency>
<!-- 如果想只引入动态条件 -->
<dependency>
    <groupId>com.tangzc</groupId>
    <artifactId>mybatis-plus-ext-condition</artifactId>
    <version>1.3.0</version>
</dependency>
```

### 自动建表

> 根据实体上的注解及字段注解自动创建、更新数据库表。
>
> 官方的设计思路是默认Bean下的所有字段均不是表字段，需要手动通过@Column声明，我在引用过来之后，改为了默认所有字段均为表字段，只有被MP的@TableField(exist=false)修饰的才会被排除，具备@TableField(exist=false)功能的注解有：@Exclude、@Bind**系列，他们集成了@TableField，且内置exist属性为false了。
>
> 另外A.CTable框架内部集成了类似MP的功能，不如MP完善，所以我也剔除掉了，顺带解决了不兼容和bug。同时像DefaultValue注解重名了，也给它改名为ColumnDefault了，另外整理了一遍内部的注解利用spring的AliasFor做了关联，更方便管理。
>
> 其中还有一点，@Table里面加了一个primary属性，表示是否为主表，为了支持多个Entity对应一个数据库表（正常用不到请忽略^_^）

```java
@Data
// @Table标记的可被识别为需要自动创建表的Entity
@Table(comment = "用户")
public class User {

    // 自动识别id属性名为主键
    // @IsAutoIncrement声明为自增主键，什么都不声明的话，默认为雪花算法的唯一主键（MP的自带功能），推荐默认便于后期的数据分布式存储等处理。
    @IsAutoIncrement
    // 字段注释
    @ColumnComment("主键")
    // 字段长度
    @ColumnLength(32)
    private String id;

    // 索引
    @Index
    // 非空
    @IsNotNull
    @ColumnComment("名字")
    private String name;

    // 唯一索引
    @Unique
    // 非空
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

### 关联查询

> 数据关联查询的解决方案，替代sql中的join方式，通过注解关联多表之间的关系，查询某实体的时候，自动带出其关联性的数据实体。
>
> 本示例以比较复杂的通过中间表关联数据的案例来讲解下，用户和角色之间多对多，通过中间表进行数据级联，@BindEntity\*系列是关联Entity的数据，@BindField\*系列是关联Entity下的某个字段。当@Bind\*系列注解用在对象上即表达一对一，当注解在List上时便表达一对多的意思，当外部对象本身就是查询集合的情况下便是多对多的场景了。

```java
@Data
@Table(comment = "角色信息")
public class Role {

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
    @BindEntityByMid(conditions = @MidCondition(
            midEntity = UserRole.class, selfMidField = "userId", joinMidField = "roleId"
    ))
    private List<Role> roles;
}
```

```java
@Data
@Table(comment = "用户-角色关联关系")
public class UserRole {

    @ColumnComment("主键")
    private String id;

    @ColumnComment("用户id")
    private String userId;

    @ColumnComment("角色id")
    private String roleId;
}
```

```java
/**
 * 用户服务
 */
@Slf4j
@Service
public class UserService {

    // UserRepository继承了BaseRepository<UserMapper, User>，后面会讲BaseRepository
    @Resource
    private UserRepository userRepository;

    /**
     * 根据用户的名字模糊查询所有用户的详细信息
     */
    @Transactional(readOnly = true)
    public List<UserDetailWithRoleDto> searchUserByNameWithRule(String name) {

        // MP的lambda查询方式
        List<User> userList = userRepository.lambdaQuery()
                .eq(name != null, User::getUsername, name)
                .list();
        // 关键步骤，指定关联角色数据。如果你打开sql打印，会看到3条sql语句，第一条根据id去User表查询user信息，第二条根据userId去UserRule中间表查询所有的ruleId，第三条sql根据ruleId集合去Rule表查询全部的权限
        Binder.bindOn(userList, User::getRoles);
        // Binder.bind(userList); 此种用法默认关联user下所有声明需要绑定的元素

        return UserMapping.MAPPER.toDto5(userList);
    }

    /**
     * 根据用户的名字模糊查询所有用户的详细信息，等价于上一个查询方式
     */
    @Transactional(readOnly = true)
    public List<UserDetailWithRoleDto> searchUserByNameWithRule2(String name) {

        // 本框架拓展的lambda查询器lambdaQueryPlus，增加了bindOne、bindList、bindPage
        // 显然这是一种更加简便的查询方式，但是如果存在多级深度的关联关系，此种方法就不适用了，还需要借助Binder
        List<User> userList = userRepository.lambdaQueryPlus()
                .eq(name != null, User::getUsername, name)
                .bindList(User::getRoles);

        return UserMapping.MAPPER.toDto5(userList);
    }
}
```

==提示==: 假如存在此种场景：`User`、`Role`、`Menu`三个实体，他们之间的关系是：`User` 多对多 `Role`、`Role` 多对多`Menu`，当我查询出User的集合后，如何获取Role和Menu的数据呢？

```java
// 数据库查询出了用户列表 【1】
List<User> userList = userRepository.list();
// 为所有用户关联角色信息 【2】
Binder.bindOn(userList, User::getRoles);
// 为所有角色信息关联菜单信息 【3】
// Deeper为一个深度遍历工具，可以深入到对象的多层属性内部，从而获取全局上该层级的所有对象同一属性
Binder.bindOn(Deeper.with(userList).inList(User::getRoles), Role::getMenus);
```

###### 注意📢：【2】和【3】存在顺序依赖，必须先执行【2】才能执行【3】

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
    @DataSource(source = User.class, field = "username", conditions = @Condition(selfField = "userId"))
    @ColumnComment("评论人名称")
    private String userName;

    @DataSource(source = User.class, field = "icon", condition = @Condition(selfField = "userId"))
    @ColumnComment("评论人头像")
    private String userIcon;
}
```

### 动态条件

> 适用场景：数据筛选，比如根据不同权限获取不同数据，用户只能看到自己的数据，管理员能看到所有人的数据。
>
> 此种场景，我们通常需要在每一个查询、更新、删除的sql操作上都追加上某个条件，很容易忘记，但是可以抽象成注解直接配置到Entity上，就省去了每个数据操作关心这个特殊条件了。

```java
/**
 * congfig中注册动态条件拦截器【1.3.0之前的版本（不包括1.3.0）可以忽略，不注册该Bean】
 */
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    // 添加动态条件，若同时添加了其他的拦截器，继续添加即可
    interceptor.addInnerInterceptor(new DynamicConditionInterceptor());
    return interceptor;
}
```

```java
@Data
@Table(comment = "文章")
public class Article {

    @ColumnComment("主键")
    private String id;

    @ColumnComment("标题")
    private String title;
    
    @ColumnComment("内容")
    private String content;

    @ColumnComment("发布人")
    @InsertOptionUser(UserIdAutoFillHandler.class)
    // 添加了该注解后，针对文章的查询、修改、删除操作，均会被自动带上 published_user_id=或者in的添加
    @DynamicCondition(CurrentUserDynamicConditionHandler.class)
    private String publishedUserId;
    
    // 省略其他字段
    ......
}
```

```java
@Component
public class CurrentUserDynamicConditionHandler implements IDynamicConditionHandler {

    @Resource
    private HttpServletRequest request;

    @Override
    public List<Object> values() {
		// 只有当enable()返回true的时候 本动态条件才 生效
        // 返回空集合或者null的时候，sql上体现的是 [column] is null，只返回一个值的时候sql上体现的是 [column]=***，返回集合的时候，sql上体现的是 [column] in (***)
        String userId = request.getHeader("USER_ID");
        return Collections.singletonList(userId);
    }

    @Override
    public boolean enable() {
        // 简单例子：header中取用户权限，如果是非管理员则执行该过滤条件，如果是管理员默认查全部，返回false，本动态条件失效
        String userRule = request.getHeader("USER_ROLE");
        return !"ADMIN".equals(userRule);
    }
}
```



### BaseEntity使用

> 通常的表设计中，都会要求添加一些审计数据，比如创建人、创建时间、最后修改人、最后修改时间，但是这些属性又不应该属于业务的，更多的是为了数据管理使用的。如果业务需要使用的话，建议起一个有意义的业务名称与上述的创建时间区分开，比如用户的注册时间（registrationTime）。为了简化数据审计字段的工作量，框架内部集成了BaseEntity

```java
@Getter
@Setter
public class BaseEntity<ID_TYPE extends Serializable, TIME_TYPE> {

    // 这里就是数据填充样例那里提到的IOptionByAutoFillHandler接口
    // 此处单独指定一个标记性的接口是为了区别用户其他数据的自动填充，例如用户名、用户电话等都会实现AutoFillHandler接口，框架上根据该接口无法拿到唯一的实现，因此同样IOptionByAutoFillHandler在整个系统中也只能有一个实现，不然会报错。
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

> 还存在某些情况下数据表要求设计成逻辑删除（逻辑删除存在很多弊端，不建议无脑所有表都设计为逻辑删除），所以框架同时提供了一个BaseLogicEntity，该实现方式利用的是MP本身自带的逻辑删除策略。

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

> 建议开发中以此为数据基本操作类，而不是以\*Mapper为基础操作类，如果需要使用\*Mapper中的方法，可以直接通过getMapper()取得Entity对应的\*Mapper类，此类与\*Mapper类相比做了很多的增强功能，尤其是其lambda语法，非常高效便捷。

```java
// 集成了MP的ServiceImpl，实现了IBaseRepository接口（内部拓展了lambda查询操作）
public abstract class BaseRepository<M extends BaseMapper<E>, E> extends ServiceImpl<M, E> implements IBaseRepository<E> {

    @Override
    public boolean updateById(E entity) {
        boolean result = super.updateById(entity);
        if(result) {
            // 数据自动更新@DataSource注解的配合逻辑
            SpringContextUtil.getApplicationContext()
                    .publishEvent(EntityUpdateEvent.create(entity));
        }
        return result;
    }

    @Override
    public boolean updateBatchById(Collection<E> entityList, int batchSize) {
        boolean result = super.updateBatchById(entityList, batchSize);
        if(result) {
            // 数据自动更新@DataSource注解的配合逻辑
            for (E entity : entityList) {
                SpringContextUtil.getApplicationContext().publishEvent(EntityUpdateEvent.create(entity));
            }
        }
        return result;
    }

    @Override
    protected Class<M> currentMapperClass() {
        return (Class<M>) ReflectionKit.getSuperClassGenericType(this.getClass(), BaseRepository.class, 0);
    }

    @Override
    protected Class<E> currentModelClass() {
        return (Class<E>) ReflectionKit.getSuperClassGenericType(this.getClass(), BaseRepository.class, 1);
    }
}
```

## 注解详细介绍

### 自动建表注解

> 只有小部分注解，进行了轻微改动，基本所有注解均是通用的，详细教程可以直接参考A.CTable官方。

#### `@Table`

> 新增一个primary属性，isNull属性为了一致性改为了isNotNull属性默认false

#### `@TableCharset`

#### `@TableComment`

#### `@TableEngine`

#### `@TablePrimary`

> 新增注解，同步@Table中的primary属性，在多个Entity映射一张表的情况下，确定主Entity是哪个，数据表生成的时候根据主表来生成。

#### `@IgnoreTable`

#### `@EnableTimeSuffix`

#### `@Column`

#### `@ColumnComment`

#### `@ColumnDefault`

> 原@DefaultValue，跟本框架中的数据插入的时候指定默认值的注解重名了，因此把这里改名字了

#### `@ColumnType`

#### `@IsAutoIncrement`

#### `@IsKey`

#### `@IsNotNull`

#### `@IsNativeDefValue`

#### `@Unique`

#### `@Index`

#### `@IgnoreUpdate`

---

### 数据填充类注解

#### `@OptionDate`

**描述：**

> 自动赋值数据操作时间。需结合mybatis-plus原框架注解[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield) （该注解的使用请查看官方文档，懒得看的话，请往下读，有惊喜）一并使用才有效。
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
| `@InsertOptionDate`       | 基于`@OptionDate`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield) ，数据**插入**的时候，自动赋值数据操作时间。 |
| `@UpdateOptionDate`       | 基于`@OptionDate`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield) ，数据**更新**（***注意：update(Wrapper<T> updateWrapper)方法除外***）的时候，自动赋值数据操作时间。 |
| `@InsertUpdateOptionDate` | 基于`@OptionDate`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield) ，数据**插入**、**更新**（***注意：update(Wrapper<T> updateWrapper)方法除外***）的时候，自动赋值数据操作时间。 |

#### `@OptionUser`

**描述：**

> 指定实现方式，自动赋值数据操作人员信息。需结合mybatis-plus原框架注解[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield) （该注解的使用请查看官方文档，懒得看的话，请往下读，有惊喜）一并使用才有效。
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
| `@InsertOptionUser`       | 基于`@OptionUser`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield) ，数据**插入**的时候，自动赋值操作人信息。 |
| `@UpdateOptionUser`       | 基于`@OptionUser`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield) ，数据**更新**（***注意：update(Wrapper<T> updateWrapper)方法除外***）的时候，自动赋值操作人信息。 |
| `@InsertUpdateOptionUser` | 基于`@OptionUser`的拓展，无需结合[`@TableField`](https://mybatis.plus/guide/annotation.html#tablefield) ，数据**插入**、**更新**（***注意：update(Wrapper<T> updateWrapper)方法除外***）的时候，自动赋值操作人信息。 |

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
| conditions      | @JoinCondition[] | 是   |        | 关联Entity所需要的条件                                       |
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
| conditions      | @JoinCondition[] | 是   |              | 关联Entity所需要的条件                                       |
| customCondition | String           | 否   |              | 被关联的Entity所需要的额外条件，通常指被关联的Entity自身的特殊条件，例如：enable=1 and is_deleted=0 |
| orderBy         | @JoinOrderBy[]   | 否   |              | 排序条件，被关联的Entity或者字段为结果集的时候生效           |
| deepBind        | boolean          | 否   | false        | 深度绑定，列表数据的情况下会产生性能问题。（不熟悉的，不建议使用） |

#### `@JoinCondition`

**描述：**

> 绑定条件

**字段：**

| 属性      | 类型   | 必需 | 默认值 | 描述                                         |
| --------- | ------ | ---- | ------ | -------------------------------------------- |
| selfField | String | 是   |        | 关联Entity所需的自身字段                     |
| joinField | String | 是   | "id"   | 被关联Entity的关联字段，默认为关联Entity的id |

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
| conditions      | @MidCondition  | 是   |        | 中间表关联条件                                               |
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
| conditions      | @MidCondition  | 是   |        | 中间表关联条件                                               |
| customCondition | String         | 否   |        | 被关联的Entity所需要的额外条件，通常指被关联的Entity自身的特殊条件，例如：enable=1 and is_deleted=0 |
| orderBy         | @JoinOrderBy[] | 否   |        | 排序条件，被关联的Entity或者字段为结果集的时候生效           |
| deepBind        | boolean        | 否   | false  | 深度绑定，列表数据的情况下会产生性能问题。（不熟悉的，不建议使用） |

#### `@MidCondition`

**描述：**

> 中间表条件描述

**字段：**

| 属性         | 类型     | 必需 | 默认值 | 描述                                   |
| ------------ | -------- | ---- | ------ | -------------------------------------- |
| midEntity    | Class<?> | 是   |        | 中间表Entity，需要对应创建其Mapper     |
| selfField    | String   | 是   | "Id"   | 关联Entity所需的自身字段               |
| selfMidField | String   | 是   |        | 关联Entity所需的自身字段，中间表字段名 |
| joinField    | String   | 是   | "id"   | 被关联Entity的关联字段                 |
| joinMidField | String   | 是   |        | 被关联Entity的关联字段，中间表字段名   |

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
| conditions | Condition[] | 是                       |            | 被关联的Entity所需要的条件                       |

#### `@Condition`

**描述：**

> 数据来源的关联条件

**字段：**

| 属性        | 类型   | 必需 | 默认值 | 描述                             |
| ----------- | ------ | ---- | ------ | -------------------------------- |
| selfField   | String | 是   |        | 关联数据来源Entity所需的自身字段 |
| sourceField | String | 是   | "id"   | 数据来源的Entity的字段，默认为id |

---

### 动态条件注解

#### `@DynamicCondition`

**描述：**

> 适用场景：数据筛选，比如根据不同权限获取不同数据，用户只能看到自己的数据，管理员能看到所有人的数据。
>
> 具体demo移步快速开始的例子。

**字段：**

| 属性  | 类型                                      | 必需 | 默认值 | 描述                                                         |
| ----- | ----------------------------------------- | ---- | ------ | ------------------------------------------------------------ |
| value | Class<? extends IDynamicConditionHandler> | 是   |        | IDynamicConditionHandler接口有两个方法，enable()决定了该条件是否生效，values()是条件匹配的值。 |

