# AOP、注解与反射深度解析

## 一、AOP（面向切面编程）

### 1.1 AOP 概念

AOP（Aspect-Oriented Programming，面向切面编程）是一种**编程范式**，它通过**横切关注点**（Cross-cutting Concerns）的分离来提高代码的模块化程度。它是对面向对象编程（OOP）的补充，解决了 OOP 难以处理的横切问题。

---

#### 1.1.1 什么是横切关注点

**横切关注点**是指那些**跨越多个模块**、**与核心业务逻辑无关**但又**必须存在**的功能。

**典型的横切关注点**：

| 关注点类型 | 说明 | 示例场景 |
|-----------|------|---------|
| **日志记录** | 记录方法的调用、参数、返回值、执行时间 | 监控系统性能 |
| **事务管理** | 管理数据库事务的开始、提交、回滚 | 保证数据一致性 |
| **权限校验** | 检查用户是否有权限执行某个操作 | 保护敏感资源 |
| **异常处理** | 统一处理应用程序中的异常 | 记录错误日志 |
| **性能监控** | 统计方法执行时间 | 发现性能瓶颈 |
| **缓存管理** | 管理数据缓存的读取和更新 | 提高系统性能 |

**传统方式的问题**：

在没有 AOP 的情况下，我们通常会这样编写代码：

```java
public class UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    public User createUser(User user) {
        // 日志记录（横切逻辑）
        log.info("开始创建用户: {}", user.getUsername());
        long startTime = System.currentTimeMillis();
        
        try {
            // 权限校验（横切逻辑）
            if (!hasPermission("CREATE_USER")) {
                throw new SecurityException("无权限");
            }
            
            // 核心业务逻辑
            user.setCreateTime(LocalDateTime.now());
            userMapper.insert(user);
            
            // 日志记录（横切逻辑）
            log.info("用户创建成功: {}", user.getId());
            return user;
        } catch (Exception e) {
            // 异常处理（横切逻辑）
            log.error("用户创建失败", e);
            throw e;
        } finally {
            // 性能监控（横切逻辑）
            long duration = System.currentTimeMillis() - startTime;
            log.info("创建用户耗时: {}ms", duration);
        }
    }
    
    public User getUserById(Long id) {
        // 同样的横切逻辑又要重复编写...
        log.info("开始查询用户: {}", id);
        // ...
    }
}
```

**问题分析**：
1. **代码重复**：每个方法都要重复编写相同的日志、权限、异常处理代码
2. **关注点混杂**：业务代码和横切代码混杂在一起，难以阅读
3. **难以维护**：如果要修改日志格式，需要修改所有方法
4. **违反开闭原则**：修改横切逻辑需要修改业务代码

---

#### 1.1.2 AOP 的核心思想

AOP 的核心思想是**分离关注点**：

```
┌─────────────────────────────────────────────────────────────┐
│                    应用程序                                 │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │  UserService│    │ OrderService│    │ DiaryService│      │
│  │  (业务逻辑)  │    │  (业务逻辑)  │    │  (业务逻辑)  │      │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘      │
│         │                 │                 │               │
│         ▼                 ▼                 ▼               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              切面（横切逻辑）                        │    │
│  │  ┌────────┐ ┌──────────┐ ┌──────────┐ ┌─────────┐   │    │
│  │  │ 日志   │ │ 事务管理  │ │ 权限校验  │ │ 异常处理 │   │    │
│  │  └────────┘ └──────────┘ └──────────┘ └─────────┘   │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**AOP 的解决方案**：

1. **抽取横切逻辑**：将日志、事务、权限等逻辑抽取到独立的"切面"类中
2. **声明式织入**：通过配置或注解声明在哪些地方应用这些切面
3. **自动增强**：框架自动将切面逻辑织入到目标方法中

**使用 AOP 后的代码**：

```java
// 业务代码 - 只关注业务逻辑
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Transactional  // 事务管理注解
    @LogOperation(module = "用户", action = "创建")  // 日志注解
    @RequirePermission("CREATE_USER")  // 权限注解
    public User createUser(User user) {
        // 只保留核心业务逻辑
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }
}

// 切面类 - 横切逻辑独立管理
@Aspect
@Component
public class LogAspect {	
    
    @Around("@annotation(com.moodnote.annotation.LogOperation)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        // 前置日志
        log.info("方法开始执行");
        
        // 执行目标方法
        Object result = joinPoint.proceed();
        
        // 后置日志
        log.info("方法执行完成");
        
        return result;
    }
}
```

---

#### 1.1.3 AOP 核心术语详解

理解 AOP 必须掌握以下核心术语：

##### 1. 切面（Aspect）
- **定义**：横切关注点的模块化实现
- **本质**：一个类，包含通知和切点定义
- **示例**：`LogAspect`、`TransactionAspect`

##### 2. 通知（Advice）
- **定义**：切面在特定连接点执行的代码
- **类型**：@Before、@After、@AfterReturning、@AfterThrowing、@Around
- **示例**：日志记录逻辑、事务管理逻辑

##### 3. 切点（Pointcut）
- **定义**：匹配连接点的表达式
- **作用**：定义哪些方法会被切面增强
- **示例**：`execution(* com.moodnote.service..*.*(..))`

##### 4. 连接点（JoinPoint）
- **定义**：程序执行过程中的某个特定点
- **常见类型**：方法调用、字段访问、异常抛出
- **示例**：`UserService.createUser()` 方法的执行

##### 5. 织入（Weaving）
- **定义**：将切面应用到目标对象的过程
- **时机**：编译时、类加载时、运行时
- **示例**：Spring AOP 在运行时创建代理对象

##### 6. 目标对象（Target）
- **定义**：被切面增强的对象
- **示例**：`UserService` 实例

##### 7. 代理对象（Proxy）
- **定义**：包含切面逻辑的对象
- **作用**：拦截目标方法调用，执行切面逻辑
- **示例**：Spring 创建的 `UserService` 代理对象

**术语关系图**：

```
┌──────────────────────────────────────────────────────────────┐
│                        切面 (Aspect)                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  切点 (Pointcut)  ────── 匹配 ──────► 连接点       │   │
│  │  execution(* service..*.*(..))        (JoinPoint)   │   │
│  └──────────────────────────────────────────────────────┘   │
│                           │                                 │
│                           ▼                                 │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  通知 (Advice)                                        │   │
│  │  @Before / @After / @Around / @AfterReturning       │   │
│  └──────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼ 织入 (Weaving)
┌──────────────────────────────────────────────────────────────┐
│  代理对象 (Proxy)  ─── 拦截 ───►  目标对象 (Target)       │
│  (包含切面逻辑)                    (原始业务对象)          │
└──────────────────────────────────────────────────────────────┘
```

---

#### 1.1.4 AOP 与 OOP 的对比

| 特性 | OOP（面向对象编程） | AOP（面向切面编程） |
|------|-------------------|-------------------|
| **核心思想** | 封装、继承、多态 | 分离横切关注点 |
| **组织方式** | 按业务领域划分（类） | 按功能类型划分（切面） |
| **关注点** | 纵向关注点（业务逻辑） | 横向关注点（横切逻辑） |
| **适用场景** | 业务逻辑实现 | 日志、事务、权限等 |
| **关系** | 基础编程范式 | OOP 的补充和扩展 |

**两者配合使用**：

```
OOP 负责：业务逻辑的模块化
    │
    ▼
AOP 负责：横切逻辑的模块化
    │
    ▼
组合使用：构建完整的企业级应用
```

---

#### 1.1.5 AOP 解决的问题详解

##### 1. 代码重复问题

**问题**：
```java
// 每个方法都要写日志
public void methodA() { log.info("开始执行"); ... }
public void methodB() { log.info("开始执行"); ... }
public void methodC() { log.info("开始执行"); ... }
```

**解决方案**：
```java
@Aspect
public class LogAspect {
    @Before("execution(* com.moodnote..*.*(..))")
    public void logBefore() {
        log.info("开始执行");
    } 
}
```

##### 2. 关注点分离问题

**问题**：业务代码和横切代码混杂
```java
public void createUser(User user) {
    // 日志 + 权限 + 事务 + 业务 + 异常处理
}
```

**解决方案**：业务代码只包含业务逻辑
```java
@Transactional
@LogOperation
@RequirePermission
public void createUser(User user) {
    // 只有业务逻辑
}
```

##### 3. 易于维护问题

**问题**：修改日志格式需要修改所有方法
```java
// 需要修改成统一格式
log.info("[" + className + "] " + methodName + " 开始执行");
```

**解决方案**：只需要修改一个切面
```java
@Aspect
public class LogAspect {
    @Before("execution(* com.moodnote..*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        // 统一修改格式
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.info("[{}] {} 开始执行", className, methodName);
    }
}
```

##### 4. 开闭原则问题

**问题**：添加新的横切功能需要修改业务代码
```java
// 添加性能监控需要修改所有方法
public void createUser(User user) {
    long start = System.currentTimeMillis();
    // 业务逻辑
    long duration = System.currentTimeMillis() - start;
}
```

**解决方案**：添加新切面，无需修改业务代码
```java
@Aspect
public class PerformanceAspect {
    @Around("execution(* com.moodnote.service..*.*(..))")
    public Object monitor(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        log.info("方法耗时: {}ms", duration);
        return result;
    }
}
```

---

#### 1.1.6 AOP 的优势总结

| 优势 | 说明 |
|------|------|
| **模块化** | 横切逻辑独立封装，提高代码复用 |
| **可维护性** | 修改横切逻辑只需修改一个地方 |
| **可读性** | 业务代码更加清晰，只关注业务本身 |
| **可扩展性** | 新增横切功能无需修改现有代码 |
| **声明式编程** | 通过注解或配置声明，无需手动编写增强逻辑 |
| **解耦合** | 业务逻辑与横切逻辑解耦，降低依赖 |

### 1.2 切面家族框架

#### 1.2.1 AspectJ

AspectJ 是一个**功能最完整、最强大**的 AOP 框架，由 Xerox PARC 研究中心开发，后来成为 Eclipse 基金会的项目。它提供了完整的 AOP 支持，是 AOP 领域的事实标准。

**核心特点详解**：

##### 1. 支持多种织入时机

**编译时织入（Compile-time Weaving）**：
- **原理**：在 Java 编译阶段，AspectJ 的专用编译器（ajc）会直接修改目标类的字节码，将切面逻辑嵌入到目标类中。
- **过程**：`.java` 源文件 → ajc 编译器 → 包含切面逻辑的 `.class` 文件
- **优点**：性能最佳，因为切面逻辑在编译时就已经嵌入，运行时无需额外处理
- **缺点**：需要使用 ajc 编译器，不能使用标准的 javac

**类加载时织入（Load-time Weaving）**：
- **原理**：在 JVM 加载类的时候，通过 Java 的 Instrumentation API 动态修改字节码
- **过程**：标准编译 → JVM 类加载 → AspectJ Agent 修改字节码 → 加载到 JVM
- **优点**：可以使用标准 javac 编译，灵活性更高
- **缺点**：需要在 JVM 启动时指定 `-javaagent` 参数

**编译后织入（Post-compile Weaving）**：
- **原理**：对已经编译好的 `.class` 文件进行字节码修改
- **适用场景**：需要对第三方库进行切面增强时

##### 2. 丰富的切点表达式

AspectJ 支持非常强大的切点表达式语法，可以精确匹配：
- **方法执行**：`execution()`
- **方法调用**：`call()`
- **字段访问**：`get()`、`set()`
- **构造方法**：`execution(*.new(..))`
- **异常处理**：`handler()`
- **类初始化**：`staticinitialization()`

**示例**：
```java
// 匹配所有 public 方法的执行
execution(public * *(..))

// 匹配指定包下所有类的所有方法
execution(* com.moodnote.service..*.*(..))

// 匹配字段设置
set(* com.moodnote.entity.User.*)

// 匹配异常处理
handler(java.lang.Exception)
```

##### 3. 支持字段级别的拦截

这是 AspectJ 最强大的特性之一，可以拦截字段的读取和写入：

```java
// 拦截 User 类所有字段的设置
pointcut setUserField() : set(* com.moodnote.entity.User.*);

after() : setUserField() {
    System.out.println("字段被修改");
}
```

##### 4. 需要特殊的编译工具

AspectJ 需要使用专门的 ajc 编译器，这增加了一点学习成本。在 Maven 项目中需要配置 AspectJ Maven 插件：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <version>1.14.0</version>
    <configuration>
        <source>17</source>
        <target>17</target>
        <complianceLevel>17</complianceLevel>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

##### 适用场景

| 场景 | 是否推荐使用 AspectJ | 原因 |
|------|-------------------|------|
| 需要拦截字段访问 | **是** | Spring AOP 不支持字段拦截 |
| 需要拦截构造方法 | **是** | Spring AOP 只支持方法拦截 |
| 性能要求极高 | **是** | 编译时织入性能更好 |
| 复杂的切点匹配 | **是** | AspectJ 语法更强大 |
| 简单的日志/事务 | **否** | Spring AOP 足够用 |

---

#### 1.2.2 Spring AOP

Spring AOP 是 Spring 框架**内置的轻量级 AOP 实现**，它不是一个独立的框架，而是 Spring 框架的一部分。它的设计目标是提供简单、易用的 AOP 功能。

**核心特点详解**：

##### 1. 基于代理模式实现

Spring AOP 使用**动态代理**技术来实现切面增强：

**JDK 动态代理**：
- **原理**：基于 Java 的 `java.lang.reflect.Proxy` 类实现
- **要求**：目标类必须实现至少一个接口
- **特点**：创建代理对象实现目标接口，调用代理方法时拦截

**CGLIB 代理**：
- **原理**：基于字节码生成库 CGLIB（Code Generation Library）实现
- **要求**：目标类可以不实现接口
- **特点**：创建目标类的子类作为代理对象

**代理选择策略**：
```java
// 如果目标类实现了接口，使用 JDK 动态代理
if (target.getClass().getInterfaces().length > 0) {
    return Proxy.newProxyInstance(...);
} else {
    // 否则使用 CGLIB 代理
    return Enhancer.create(...);
}
```

##### 2. 运行时织入（Runtime Weaving）

Spring AOP 的织入发生在**运行时**：

```
Spring 容器启动
    │
    ▼
创建目标对象（Target）
    │
    ▼
创建代理对象（Proxy）
    │
    ▼
将代理对象注入到需要的地方
    │
    ▼
客户端调用方法 → 代理对象 → 切面逻辑 → 目标对象
```

**优点**：
- 无需特殊编译工具，使用标准 javac 即可
- 更加灵活，可以在运行时动态配置切面
- 与 Spring IoC 容器无缝集成

**缺点**：
- 性能开销较大，每次方法调用都需要通过代理
- 只支持方法级别的拦截

##### 3. 与 Spring 框架深度集成

Spring AOP 与 Spring IoC 容器深度集成，可以使用：
- **@Autowired** 注入依赖
- **@Value** 读取配置
- **@Qualifier** 限定注入

**示例**：
```java
@Aspect
@Component
public class LogAspect {
    
    @Autowired
    private LoggerService loggerService;  // 可以注入 Spring Bean
    
    @Around("execution(* com.moodnote.controller..*.*(..))")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        loggerService.log("方法执行前");
        Object result = joinPoint.proceed();
        loggerService.log("方法执行后");
        return result;
    }
}
```

##### 4. 只支持方法级别的拦截

这是 Spring AOP 的主要限制：

| 拦截类型 | Spring AOP | AspectJ |
|----------|-----------|---------|
| 方法执行 | ✅ | ✅ |
| 方法调用 | ❌ | ✅ |
| 字段读取 | ❌ | ✅ |
| 字段写入 | ❌ | ✅ |
| 构造方法 | ❌ | ✅ |
| 异常处理 | ❌ | ✅ |

##### 适用场景

| 场景 | 是否推荐使用 Spring AOP | 原因 |
|------|----------------------|------|
| 日志记录 | **是** | 简单易用 |
| 事务管理 | **是** | Spring 事务基于 AOP |
| 权限校验 | **是** | 方法级别足够 |
| 性能监控 | **是** | 方法级别足够 |
| 字段级拦截 | **否** | 需要 AspectJ |

---

#### 1.2.3 深入对比

##### 织入时机对比

| 织入时机 | 发生阶段 | 性能 | 灵活性 | 工具要求 |
|----------|---------|------|--------|----------|
| 编译时 | 编译阶段 | **最佳** | 最低 | 需要 ajc |
| 类加载时 | 类加载阶段 | 较好 | 较高 | 需要 javaagent |
| 运行时 | 运行阶段 | **较差** | **最高** | 无需特殊工具 |

##### 功能对比

| 功能 | AspectJ | Spring AOP | 说明 |
|------|---------|------------|------|
| 方法执行拦截 | ✅ | ✅ | 两者都支持 |
| 方法调用拦截 | ✅ | ❌ | AspectJ 特有 |
| 字段访问拦截 | ✅ | ❌ | AspectJ 特有 |
| 构造方法拦截 | ✅ | ❌ | AspectJ 特有 |
| 异常处理拦截 | ✅ | ❌ | AspectJ 特有 |
| 静态初始化拦截 | ✅ | ❌ | AspectJ 特有 |
| 注解驱动 | ✅ | ✅ | 两者都支持 |
| XML 配置 | ✅ | ✅ | 两者都支持 |

##### 性能对比

**基准测试结果**（每次方法调用的平均耗时）：

| 场景 | 原生调用 | Spring AOP | AspectJ |
|------|---------|-----------|---------|
| 无切面 | ~1 ns | ~1 ns | ~1 ns |
| 单个 @Around | ~100 ns | ~500 ns | ~10 ns |
| 多个 @Around | ~200 ns | ~1000 ns | ~20 ns |

**结论**：
- AspectJ 的性能几乎与原生调用相当
- Spring AOP 有明显的性能开销（约 50 倍）
- 对于高频调用的方法，建议使用 AspectJ

##### 选择建议

```
                      ┌─────────────────────────────┐
                      │     需要字段级拦截？         │
                      └─────────────┬─────────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    ▼                               ▼
                是（需要）                       否（不需要）
                    │                               │
                    ▼                               ▼
              使用 AspectJ              ┌───────────┴───────────┐
                                       ▼                       ▼
                                  性能要求高？            性能要求一般？
                                       │                       │
                                       ▼                       ▼
                                 使用 AspectJ           使用 Spring AOP
```

**简单总结**：
1. **日常开发**：优先使用 Spring AOP，简单易用
2. **性能敏感场景**：使用 AspectJ
3. **需要字段/构造方法拦截**：使用 AspectJ
4. **团队技术栈**：如果团队熟悉 Spring，用 Spring AOP

### 1.3 通知类型详解

通知（Advice）是切面在特定连接点执行的代码。Spring AOP 支持五种通知类型，它们在目标方法执行的不同时机执行。

---

#### 1.3.1 @Before（前置通知）

**定义**：在目标方法执行**之前**执行的通知。

**核心特点**：
- 在目标方法执行前执行
- 可以获取方法签名、参数等信息
- 无法阻止目标方法执行（除非抛出异常）
- 无法获取目标方法的返回值

**适用场景**：
- 权限校验
- 日志记录（方法入口）
- 参数校验
- 资源准备

**完整示例**：

```java
@Aspect
@Component
public class SecurityAspect {
    
    @Autowired
    private AuthService authService;
    
    /**
     * 前置通知：权限校验
     */
    @Before("execution(* com.moodnote.controller..*.*(..))")
    public void checkPermission(JoinPoint joinPoint) {
        // 获取方法签名
        Signature signature = joinPoint.getSignature();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();
        
        // 获取请求信息
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String uri = request.getRequestURI();
        
        // 获取用户信息
        Long userId = (Long) request.getAttribute("userId");
        
        // 权限校验
        if (!authService.hasPermission(userId, uri)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限访问");
        }
        
        log.info("[权限校验] 用户[{}]访问 {}#{}, URI: {}", userId, className, methodName, uri);
    }
}
```

---

#### 1.3.2 @After（后置通知）

**定义**：在目标方法执行**之后**执行的通知，**无论方法是否抛出异常**。

**核心特点**：
- 在目标方法执行后执行（包括异常情况）
- 无法获取目标方法的返回值
- 无法获取异常信息
- 常用于资源清理

**适用场景**：
- 资源释放
- 日志记录（方法出口，不关心结果）
- 清理工作

**完整示例**：

```java
@Aspect
@Component
public class ResourceAspect {
    
    /**
     * 后置通知：资源清理
     */
    @After("execution(* com.moodnote.service..*.*(..))")
    public void cleanup(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        
        // 清理 ThreadLocal 变量，防止内存泄漏
        RequestContextHolder.resetRequestAttributes();
        
        log.info("[资源清理] 方法 {} 执行完毕，清理资源", methodName);
    }
}
```

---

#### 1.3.3 @AfterReturning（返回通知）

**定义**：在目标方法**正常返回**后执行的通知。

**核心特点**：
- 只有在方法正常返回时才执行
- 可以获取目标方法的返回值
- 可以修改返回值（通过修改 `returning` 参数）

**适用场景**：
- 日志记录（记录返回结果）
- 数据转换
- 缓存更新

**完整示例**：

```java
@Aspect
@Component
public class ResponseAspect {
    
    /**
     * 返回通知：统一响应包装
     */
    @AfterReturning(
        pointcut = "execution(* com.moodnote.controller..*.*(..))",
        returning = "result"  // 绑定返回值到 result 参数
    )
    public void wrapResponse(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        
        // 如果返回的是 Result 对象，记录日志
        if (result instanceof Result) {
            Result<?> response = (Result<?>) result;
            log.info("[响应] 方法 {} 返回: code={}, message={}", 
                methodName, response.getCode(), response.getMessage());
        } else {
            log.info("[响应] 方法 {} 返回: {}", methodName, result);
        }
    }
}
```

---

#### 1.3.4 @AfterThrowing（异常通知）

**定义**：在目标方法**抛出异常**时执行的通知。

**核心特点**：
- 只有在方法抛出异常时才执行
- 可以获取异常信息
- 可以选择是否重新抛出异常

**适用场景**：
- 异常日志记录
- 异常处理
- 告警通知

**完整示例**：

```java
@Aspect
@Component
public class ExceptionAspect {
    
    @Autowired
    private AlertService alertService;
    
    /**
     * 异常通知：统一异常处理
     */
    @AfterThrowing(
        pointcut = "execution(* com.moodnote.service..*.*(..))",
        throwing = "ex"  // 绑定异常到 ex 参数
    )
    public void handleException(JoinPoint joinPoint, Exception ex) {
        // 获取方法信息
        Signature signature = joinPoint.getSignature();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();
        
        // 获取参数信息
        Object[] args = joinPoint.getArgs();
        String params = Arrays.toString(args);
        
        // 记录异常日志
        log.error("[异常] {}.{} 执行失败, 参数: {}, 异常: {}", 
            className, methodName, params, ex.getMessage(), ex);
        
        // 发送告警
        alertService.sendAlert("服务异常", className + "." + methodName + ": " + ex.getMessage());
    }
}
```

---

#### 1.3.5 @Around（环绕通知）

**定义**：包裹目标方法，可以在方法执行前后都添加逻辑，并且可以控制目标方法的执行。

**核心特点**：
- 最强大的通知类型
- 可以在方法执行前后添加逻辑
- 可以控制是否执行目标方法
- 可以修改参数
- 可以修改返回值
- 可以处理异常

**适用场景**：
- 性能监控
- 事务管理
- 缓存控制
- 重试机制

**完整示例**：

```java
@Aspect
@Component
public class PerformanceAspect {
    
    /**
     * 环绕通知：性能监控
     */
    @Around("execution(* com.moodnote.service..*.*(..))")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        // 前置逻辑：记录开始时间
        long startTime = System.currentTimeMillis();
        
        // 获取方法信息
        Signature signature = joinPoint.getSignature();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();
        
        // 获取参数
        Object[] args = joinPoint.getArgs();
        
        log.info("[性能监控] 开始执行 {}.{}, 参数: {}", className, methodName, Arrays.toString(args));
        
        Object result = null;
        Exception exception = null;
        
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            // 后置逻辑：计算耗时
            long duration = System.currentTimeMillis() - startTime;
            
            // 记录日志
            if (exception == null) {
                log.info("[性能监控] 完成 {}.{}, 耗时: {}ms, 返回值: {}", 
                    className, methodName, duration, result);
            } else {
                log.error("[性能监控] 失败 {}.{}, 耗时: {}ms, 异常: {}", 
                    className, methodName, duration, exception.getMessage());
            }
            
            // 如果耗时超过阈值，记录警告
            if (duration > 2000) {
                log.warn("[性能警告] {}.{} 执行耗时 {}ms，超过阈值", className, methodName, duration);
            }
        }
    }
}
```

---

#### 1.3.6 通知执行顺序

当同一个切点有多个通知时，执行顺序如下：

```
目标方法执行流程：
    │
    ▼
1. @Before（前置通知）
    │
    ▼
2. @Around.proceed() 之前的逻辑
    │
    ▼
3. 目标方法执行
    │
    ├─ 正常返回 ─────► @AfterReturning（返回通知）
    │
    └─ 抛出异常 ─────► @AfterThrowing（异常通知）
    │
    ▼
4. @Around.proceed() 之后的逻辑
    │
    ▼
5. @After（后置通知）
```

**多个切面的执行顺序**：

```java
// 通过 @Order 注解控制顺序
@Aspect
@Component
@Order(1)  // 数字越小，优先级越高
public class FirstAspect { ... }

@Aspect
@Component
@Order(2)
public class SecondAspect { ... }
```

执行顺序：
```
FirstAspect @Before
    │
    ▼
SecondAspect @Before
    │
    ▼
目标方法
    │
    ▼
SecondAspect @AfterReturning/@AfterThrowing
    │
    ▼
SecondAspect @After
    │
    ▼
FirstAspect @AfterReturning/@AfterThrowing
    │
    ▼
FirstAspect @After
```

---

#### 1.3.7 通知类型对比

| 通知类型 | 执行时机 | 能否获取返回值 | 能否获取异常 | 能否控制执行 | 典型用途 |
|---------|---------|--------------|-------------|-------------|---------|
| @Before | 方法执行前 | ❌ | ❌ | ❌（除非抛异常） | 权限校验、日志入口 |
| @After | 方法执行后（无论是否异常） | ❌ | ❌ | ❌ | 资源清理 |
| @AfterReturning | 方法正常返回后 | ✅ | ❌ | ❌ | 日志记录、数据转换 |
| @AfterThrowing | 方法抛出异常时 | ❌ | ✅ | ❌ | 异常处理、告警 |
| @Around | 方法执行前后 | ✅ | ✅ | ✅ | 性能监控、事务管理 |

---

#### 1.3.8 实践建议

##### 1. 选择合适的通知类型

```
需要在方法执行前做些什么？
    │
    ├─ 权限校验 → @Before
    ├─ 参数校验 → @Before
    └─ 资源准备 → @Before

需要在方法执行后做些什么？
    │
    ├─ 记录返回值 → @AfterReturning
    ├─ 处理异常 → @AfterThrowing
    └─ 清理资源 → @After

需要完全控制方法执行？
    │
    ├─ 性能监控 → @Around
    ├─ 事务管理 → @Around
    └─ 重试机制 → @Around
```

##### 2. 避免过度使用 @Around

@Around 虽然强大，但也最复杂。如果 @Before、@AfterReturning 等能满足需求，就不要使用 @Around。

##### 3. 注意异常处理

在 @Around 中，如果捕获了异常但没有重新抛出，会导致异常被吞掉：

```java
// 错误示例
@Around("execution(* com.moodnote.service..*.*(..))")
public Object around(ProceedingJoinPoint joinPoint) {
    try {
        return joinPoint.proceed();
    } catch (Exception e) {
        log.error("出错了", e);
        // 没有重新抛出，异常被吞掉！
        return null;
    }
}

// 正确示例
@Around("execution(* com.moodnote.service..*.*(..))")
public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    try {
        return joinPoint.proceed();
    } catch (Exception e) {
        log.error("出错了", e);
        throw e;  // 重新抛出异常
    }
}
```

### 1.4 设计理念

AOP 的设计理念源于对软件设计原则的深刻理解和实践。以下从多个维度深入剖析 AOP 的设计思想。

---

#### 1.4.1 开闭原则（Open/Closed Principle）

**定义**：软件实体（类、模块、函数）应该**对扩展开放，对修改关闭**。

**AOP 如何体现开闭原则**：

```
┌─────────────────────────────────────────────────────────┐
│                    现有业务代码                          │
│  ┌─────────────────────────────────────────────────┐   │
│  │  class UserService {                           │   │
│  │      public User createUser(User user) {       │   │
│  │          // 核心业务逻辑                        │   │
│  │      }                                         │   │
│  │  }                                             │   │
│  └─────────────────────────────────────────────────┘   │
│                           │                           │
│                           ▼                           │
│  ┌─────────────────────────────────────────────────┐   │
│  │              扩展方式对比                        │   │
│  │  ┌──────────────────┐  ┌──────────────────┐     │   │
│  │  │    传统方式       │  │     AOP 方式      │     │   │
│  │  ├──────────────────┤  ├──────────────────┤     │   │
│  │  │ 修改 UserService │  │ 添加新切面       │     │   │
│  │  │ 添加日志代码      │  │ LogAspect       │     │   │
│  │  │ 修改业务逻辑      │  │ 不修改原代码     │     │   │
│  │  └──────────────────┘  └──────────────────┘     │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

**实践案例**：

```java
// 原始业务代码 - 不需要修改
@Service
public class UserService {
    public User createUser(User user) {
        userMapper.insert(user);
        return user;
    }
}

// 扩展功能 - 添加新切面，不修改原有代码
@Aspect
@Component
public class LogAspect {
    @Before("execution(* com.moodnote.service..*.*(..))")
    public void log(JoinPoint joinPoint) {
        log.info("方法执行: {}", joinPoint.getSignature().getName());
    }
}
```

**优势**：
- **降低风险**：不修改原有代码，避免引入新 bug
- **提高效率**：只需添加新切面即可扩展功能
- **保持稳定**：原有业务逻辑保持不变

---

#### 1.4.2 关注点分离（Separation of Concerns）

**定义**：将系统分解为不同的关注点，每个关注点独立处理。

**传统代码的问题**：

```java
public class UserService {
    // 混合了多个关注点
    public User createUser(User user) {
        // 关注点1：日志记录
        log.info("开始创建用户");
        
        // 关注点2：权限校验
        if (!hasPermission()) throw new SecurityException();
        
        // 关注点3：事务管理
        transaction.begin();
        
        // 关注点4：核心业务
        userMapper.insert(user);
        
        // 关注点5：异常处理
        try {
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
        
        // 关注点6：性能监控
        log.info("创建用户耗时: {}ms", duration);
        
        return user;
    }
}
```

**AOP 实现关注点分离**：

```
┌─────────────────────────────────────────────────────────────┐
│                     用户服务层                               │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  UserService.createUser(User user)                 │   │
│  │  ┌─────────────────────────────────────────────┐   │   │
│  │  │         核心业务逻辑                         │   │   │
│  │  │         userMapper.insert(user)             │   │   │
│  │  └─────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────┘   │
│                           │                               │
│         ┌─────────────────┼─────────────────┐             │
│         ▼                 ▼                 ▼             │
│  ┌───────────┐    ┌───────────┐    ┌───────────┐         │
│  │ 日志切面  │    │ 权限切面  │    │ 事务切面  │         │
│  │ LogAspect │    │ AuthAspect│    │ TxAspect  │         │
│  └───────────┘    └───────────┘    └───────────┘         │
└─────────────────────────────────────────────────────────────┘
```

**分离后的代码**：

```java
// 业务层 - 只关注业务
@Service
public class UserService {
    @Transactional
    @LogOperation
    @RequirePermission("CREATE_USER")
    public User createUser(User user) {
        userMapper.insert(user);
        return user;
    }
}

// 日志切面 - 只关注日志
@Aspect
@Component
public class LogAspect {
    @Before("@annotation(LogOperation)")
    public void log(JoinPoint joinPoint) { ... }
}

// 权限切面 - 只关注权限
@Aspect
@Component
public class AuthAspect {
    @Before("@annotation(RequirePermission)")
    public void checkPermission(JoinPoint joinPoint) { ... }
}
```

**关注点分类**：

| 关注点类型 | 职责 | 示例切面 |
|-----------|------|---------|
| **业务关注点** | 核心业务逻辑 | UserService, OrderService |
| **横切关注点** | 跨越多个模块的功能 | 日志、事务、权限、监控 |

---

#### 1.4.3 代理模式（Proxy Pattern）

**定义**：为其他对象提供代理，控制对原对象的访问。

**Spring AOP 的代理架构**：

```
客户端 ──► 代理对象(Proxy) ──► 目标对象(Target)
              │                        │
              ▼                        ▼
         切面逻辑处理              业务逻辑执行
         (Advice)                   (Method)
```

**代理对象的作用**：

1. **拦截调用**：拦截客户端对目标对象的方法调用
2. **执行切面逻辑**：在目标方法执行前后执行切面代码
3. **调用目标方法**：通过 `joinPoint.proceed()` 调用原始方法
4. **返回结果**：将目标方法的返回值传递给客户端

**代理方式概览**：

Spring AOP 支持两种代理方式：
- **JDK 动态代理**：基于接口实现，性能较高，只能代理接口方法
- **CGLIB 代理**：基于继承实现，可以代理任意类，无法代理 final 方法

> **详细内容请参考 [1.7 Spring AOP 增强机制](#17-spring-aop-增强机制)**

---

#### 1.4.4 织入时机（Weaving Timing）

**定义**：将切面应用到目标对象的过程称为织入。

**三种织入时机**：

| 时机 | 阶段 | 工具 | 特点 |
|------|------|------|------|
| **编译时织入** | 源代码编译阶段 | AspectJ 编译器 (ajc) | 性能最佳，需要特殊编译器 |
| **类加载时织入** | 类加载到 JVM 阶段 | Load-Time Weaver | 灵活性较高，需要 javaagent |
| **运行时织入** | 应用运行阶段 | Spring AOP | 最灵活，性能稍差 |

> **详细内容请参考 [1.2.1 AspectJ](#121-aspectj)**

---

#### 1.4.5 依赖注入与 AOP 集成

**Spring IoC 与 AOP 的协作**：

```
┌──────────────────────────────────────────────────────────┐
│                    Spring IoC 容器                        │
│  ┌────────────────────────────────────────────────────┐  │
│  │              Bean 定义与注册                       │  │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐        │  │
│  │  │UserService│  │LogAspect │  │AuthAspect│        │  │
│  │  └────┬─────┘  └────┬─────┘  └────┬─────┘        │  │
│  │       │             │             │                │  │
│  └───────┼─────────────┼─────────────┼──────────────┘  │
│          │             │             │                 │
│          ▼             ▼             ▼                 │
│  ┌────────────────────────────────────────────────────┐  │
│  │              AOP 代理创建                          │  │
│  │  UserService 被增强为 Proxy                        │  │
│  │  Proxy = UserService + LogAspect + AuthAspect     │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
```

**核心要点**：
- Spring 在 Bean 初始化完成后，会检查是否有匹配的切面
- 如果有切面匹配，Spring 会创建代理对象来包装原始 Bean
- 代理对象会被注入到其他依赖该 Bean 的组件中

> **代理对象生命周期的详细内容请参考 [1.7.5 代理对象的生命周期](#175-代理对象的生命周期)**  
> **自调用问题的详细内容请参考 [1.7.6 重要注意事项](#176-重要注意事项)**

---

#### 1.4.6 AOP 设计模式应用

**AOP 中体现的设计模式**：

| 设计模式 | 在 AOP 中的应用 | 示例 |
|---------|----------------|------|
| **代理模式** | 核心实现方式 | JDK 动态代理、CGLIB |
| **策略模式** | 不同通知类型 | @Before、@Around 等 |
| **模板方法模式** | 通知执行流程 | 固定的通知执行顺序 |
| **责任链模式** | 多个切面的链式调用 | 多个 @Before 依次执行 |

**责任链模式示例**：

```java
// 多个切面形成责任链
@Aspect
@Order(1)
public class FirstAspect {
    @Before("execution(* com.moodnote..*.*(..))")
    public void before1() { System.out.println("First"); }
}

@Aspect  
@Order(2)
public class SecondAspect {
    @Before("execution(* com.moodnote..*.*(..))")
    public void before2() { System.out.println("Second"); }
}

// 执行顺序：First → Second → 目标方法 → Second → First
```

### 1.5 JoinPoint API 详解

JoinPoint 是 AOP 中的核心接口，用于封装连接点的信息。通过 JoinPoint，我们可以在切面中获取目标方法的详细信息。

---

#### 1.5.1 类继承关系

```
Object
  └── JoinPoint (接口)
        └── ProceedingJoinPoint (接口)
```

**接口关系说明**：

| 接口 | 说明 | 适用通知类型 |
|------|------|-------------|
| `JoinPoint` | 基础接口，提供连接点基本信息 | @Before、@After、@AfterReturning、@AfterThrowing |
| `ProceedingJoinPoint` | 继承 JoinPoint，增加 proceed() 方法 | @Around |

---

#### 1.5.2 JoinPoint 常用方法详解

##### 1. 获取方法签名（Signature）

**Signature 接口提供的方法**：

```java
@Before("execution(* com.moodnote.service..*.*(..))")
public void before(JoinPoint joinPoint) {
    // 获取方法签名
    Signature signature = joinPoint.getSignature();
    
    // 获取方法名
    String methodName = signature.getName();  // 如: "createUser"
    
    // 获取声明该方法的类的全限定名
    String declaringTypeName = signature.getDeclaringTypeName();  // 如: "com.moodnote.service.UserService"
    
    // 获取声明该方法的类的 Class 对象
    Class<?> declaringType = signature.getDeclaringType();  // 如: UserService.class
    
    // 获取方法的修饰符（如 public, private, protected）
    int modifiers = signature.getModifiers();  // 如: 1 (public)
    
    // 判断是否是方法签名
    if (signature instanceof MethodSignature) {
        MethodSignature methodSignature = (MethodSignature) signature;
        
        // 获取方法参数类型
        Class<?>[] parameterTypes = methodSignature.getParameterTypes();
        
        // 获取方法返回类型
        Class<?> returnType = methodSignature.getReturnType();
        
        // 获取方法参数名（需要配置 -parameters 编译参数）
        String[] parameterNames = methodSignature.getParameterNames();
    }
}
```

**MethodSignature 特有方法**：

| 方法 | 返回值 | 说明 |
|------|--------|------|
| `getMethod()` | `Method` | 获取目标方法对象 |
| `getParameterTypes()` | `Class<?>[]` | 获取参数类型数组 |
| `getParameterNames()` | `String[]` | 获取参数名数组 |
| `getReturnType()` | `Class<?>` | 获取返回类型 |
| `getExceptionTypes()` | `Class<?>[]` | 获取异常类型数组 |

##### 2. 获取方法参数

```java
@Before("execution(* com.moodnote.controller..*.*(..))")
public void logParams(JoinPoint joinPoint) {
    // 获取所有参数
    Object[] args = joinPoint.getArgs();
    
    // 获取方法签名
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String[] parameterNames = signature.getParameterNames();
    
    // 遍历参数
    for (int i = 0; i < args.length; i++) {
        String paramName = parameterNames[i];
        Object paramValue = args[i];
        log.info("参数[{}]: {} = {}", i + 1, paramName, paramValue);
    }
}
```

**参数处理实践**：

```java
// 过滤敏感参数
private Object[] filterSensitiveParams(Object[] args, String[] paramNames) {
    Object[] filtered = new Object[args.length];
    for (int i = 0; i < args.length; i++) {
        if (paramNames[i].toLowerCase().contains("password")) {
            filtered[i] = "******";
        } else {
            filtered[i] = args[i];
        }
    }
    return filtered;
}
```

##### 3. 获取目标对象与代理对象

```java
@Before("execution(* com.moodnote.service..*.*(..))")
public void getObjects(JoinPoint joinPoint) {
    // 获取被代理的真实对象（目标对象）
    Object target = joinPoint.getTarget();
    log.info("目标对象: {}", target.getClass().getSimpleName());
    
    // 获取代理对象本身
    Object proxy = joinPoint.getThis();
    log.info("代理对象: {}", proxy.getClass().getSimpleName());
    
    // 判断是否是同一个对象（通常不是）
    log.info("目标对象与代理对象是否相同: {}", target == proxy);
}
```

**target vs this 的区别**：

| 属性 | `getTarget()` | `getThis()` |
|------|---------------|-------------|
| 返回对象 | 原始目标对象 | 代理对象 |
| 类型 | 目标类本身 | 代理类（如 $Proxy0） |
| 使用场景 | 获取目标对象的属性/方法 | 获取代理对象的信息 |

##### 4. 获取请求属性（Web 环境）

```java
@Before("execution(* com.moodnote.controller..*.*(..))")
public void getRequestInfo(JoinPoint joinPoint) {
    // 获取请求属性
    ServletRequestAttributes attributes = 
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    
    if (attributes != null) {
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        
        // 获取请求信息
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        String remoteAddr = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        
        log.info("请求: {} {} from {}", method, requestUri, remoteAddr);
    }
}
```

**常用 Request 属性**：

| 属性 | 方法 | 说明 |
|------|------|------|
| 请求 URI | `getRequestURI()` | 如 `/api/user/login` |
| 请求方法 | `getMethod()` | GET/POST/PUT/DELETE |
| 客户端 IP | `getRemoteAddr()` | 客户端 IP 地址 |
| 请求头 | `getHeader(String name)` | 获取指定请求头 |
| Session | `getSession()` | 获取 Session 对象 |

##### 5. 获取连接点所在文件信息

```java
@Before("execution(* com.moodnote.service..*.*(..))")
public void getSourceLocation(JoinPoint joinPoint) {
    // 获取源代码位置信息
    SourceLocation sourceLocation = joinPoint.getSourceLocation();
    
    // 获取文件名
    String fileName = sourceLocation.getFileName();
    
    // 获取行号
    int lineNumber = sourceLocation.getLine();
    
    // 获取类文件的 URL
    URL url = sourceLocation.getWithinType().getProtectionDomain().getCodeSource().getLocation();
    
    log.info("方法位于: {} 第 {} 行", fileName, lineNumber);
}
```

---

#### 1.5.3 JoinPoint 完整示例

```java
@Aspect
@Component
public class LogAspect {
    
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
    
    @Before("execution(* com.moodnote.service..*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        // 1. 获取方法签名
        Signature signature = joinPoint.getSignature();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();
        
        // 2. 获取方法参数
        Object[] args = joinPoint.getArgs();
        String[] paramNames = ((MethodSignature) signature).getParameterNames();
        
        // 3. 过滤敏感参数
        Object[] filteredArgs = filterSensitiveParams(args, paramNames);
        
        // 4. 获取请求信息（Web 环境）
        String requestInfo = getRequestInfo();
        
        // 5. 记录日志
        log.info("[方法调用] {}.{}({}) - {}", 
            className.substring(className.lastIndexOf(".") + 1),
            methodName,
            formatParams(paramNames, filteredArgs),
            requestInfo
        );
    }
    
    private String getRequestInfo() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return String.format("请求: %s %s", request.getMethod(), request.getRequestURI());
        }
        return "非 Web 请求";
    }
    
    private String formatParams(String[] names, Object[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < names.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(names[i]).append("=").append(values[i]);
        }
        return sb.toString();
    }
}
```

---

### 1.6 ProceedingJoinPoint API 详解

`ProceedingJoinPoint` 是 `JoinPoint` 的子接口，专门用于环绕通知（@Around）。它增加了 `proceed()` 方法，允许控制目标方法的执行。

---

#### 1.6.1 与 JoinPoint 的关系

```
JoinPoint (基础接口)
    │
    ├── getSignature()     获取方法签名
    ├── getArgs()          获取方法参数
    ├── getTarget()        获取目标对象
    ├── getThis()          获取代理对象
    └── getSourceLocation() 获取源代码位置
    
ProceedingJoinPoint (扩展接口)
    │
    ├── 继承 JoinPoint 的所有方法
    └── proceed()          执行目标方法
```

---

#### 1.6.2 proceed() 方法详解

**基本用法**：

```java
@Around("execution(* com.moodnote.service..*.*(..))")
public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    // 前置逻辑
    log.info("方法执行前");
    
    // 执行目标方法
    Object result = joinPoint.proceed();
    
    // 后置逻辑
    log.info("方法执行后");
    
    return result;
}
```

**带参数的用法**：

```java
@Around("execution(* com.moodnote.service..*.*(..))")
public Object aroundWithModifiedArgs(ProceedingJoinPoint joinPoint) throws Throwable {
    // 获取原始参数
    Object[] originalArgs = joinPoint.getArgs();
    
    // 修改参数
    Object[] newArgs = modifyArgs(originalArgs);
    
    // 使用修改后的参数执行目标方法
    Object result = joinPoint.proceed(newArgs);
    
    return result;
}

private Object[] modifyArgs(Object[] args) {
    // 例如：对字符串参数进行 trim
    Object[] newArgs = new Object[args.length];
    for (int i = 0; i < args.length; i++) {
        if (args[i] instanceof String) {
            newArgs[i] = ((String) args[i]).trim();
        } else {
            newArgs[i] = args[i];
        }
    }
    return newArgs;
}
```

**proceed() 方法的关键点**：

| 特性 | 说明 |
|------|------|
| **返回值** | 目标方法的返回值，必须显式返回 |
| **异常处理** | 如果目标方法抛出异常，proceed() 会抛出该异常 |
| **参数传递** | 可以通过 proceed(Object[] args) 传递修改后的参数 |
| **调用次数** | 可以多次调用 proceed()，实现重试逻辑 |

---

#### 1.6.3 ProceedingJoinPoint 完整示例

```java
@Aspect
@Component
public class RetryAspect {
    
    private static final Logger log = LoggerFactory.getLogger(RetryAspect.class);
    
    @Around("@annotation(com.moodnote.annotation.Retry)")
    public Object retry(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Retry retryAnnotation = signature.getMethod().getAnnotation(Retry.class);
        
        int maxRetries = retryAnnotation.maxRetries();
        long delayMs = retryAnnotation.delayMs();
        
        Exception lastException = null;
        
        // 重试逻辑
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.info("第 {} 次尝试执行 {}", attempt, signature.getName());
                return joinPoint.proceed();
            } catch (Exception e) {
                lastException = e;
                log.warn("第 {} 次尝试失败: {}", attempt, e.getMessage());
                
                // 如果不是最后一次尝试，等待后重试
                if (attempt < maxRetries) {
                    Thread.sleep(delayMs);
                }
            }
        }
        
        // 所有重试都失败，抛出最后一次异常
        throw lastException;
    }
}

// 自定义重试注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {
    int maxRetries() default 3;
    long delayMs() default 1000;
}

// 使用示例
@Service
public class UserService {
    @Retry(maxRetries = 3, delayMs = 2000)
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
}
```

---

#### 1.6.4 proceed() 与异常处理

```java
@Around("execution(* com.moodnote.service..*.*(..))")
public Object aroundWithExceptionHandling(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    String methodName = joinPoint.getSignature().getName();
    
    try {
        log.info("[{}] 开始执行", methodName);
        Object result = joinPoint.proceed();
        log.info("[{}] 执行成功，耗时 {}ms", methodName, System.currentTimeMillis() - startTime);
        return result;
    } catch (Exception e) {
        log.error("[{}] 执行失败，耗时 {}ms，异常: {}", 
            methodName, System.currentTimeMillis() - startTime, e.getMessage());
        
        // 根据异常类型决定是否重新抛出
        if (e instanceof BusinessException) {
            // 业务异常，直接抛出
            throw e;
        } else {
            // 系统异常，包装后抛出
            throw new SystemException("系统内部错误", e);
        }
    } finally {
        // 无论成功还是失败都会执行
        log.info("[{}] 方法执行完毕", methodName);
    }
}
```

---

#### 1.6.5 调用目标方法时的注意事项

##### 1. 必须显式返回结果

```java
// 错误示例：忘记返回结果
@Around("execution(* com.moodnote.service..*.*(..))")
public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    joinPoint.proceed();  // 返回值被忽略！
    return null;  // 调用者会收到 null
}

// 正确示例
@Around("execution(* com.moodnote.service..*.*(..))")
public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    Object result = joinPoint.proceed();
    return result;  // 正确返回
}
```

##### 2. 修改参数的影响范围

```java
@Around("execution(* com.moodnote.service..*.*(..))")
public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    Object[] args = joinPoint.getArgs();
    
    // 直接修改数组中的对象（如果是可变对象）
    if (args.length > 0 && args[0] instanceof User) {
        User user = (User) args[0];
        user.setUsername(user.getUsername().toUpperCase());
    }
    
    // 这种修改会影响目标方法的参数
    return joinPoint.proceed();
}
```

##### 3. 避免无限递归

```java
// 错误示例：切面方法调用了被拦截的方法
@Aspect
@Component
public class LogAspect {
    @Around("execution(* com.moodnote.service..*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 这里会导致无限递归！
        logMethodCall();  // 如果 logMethodCall() 也被这个切点匹配
        return joinPoint.proceed();
    }
    
    // 这个方法也会被拦截
    public void logMethodCall() {
        log.info("方法调用");
    }
}
```

### 1.7 Spring AOP 增强机制

Spring AOP 的增强机制基于**动态代理**实现，通过创建代理对象来拦截目标方法调用并执行切面逻辑。

---

#### 1.7.1 代理方式详解

##### 1. JDK 动态代理

**原理**：基于 Java 反射机制，通过 `java.lang.reflect.Proxy` 类动态生成代理类。

**适用条件**：目标类必须实现至少一个接口。

**实现过程**：

```java
public class JdkDynamicProxy implements InvocationHandler {
    
    private Object target;  // 目标对象
    
    public JdkDynamicProxy(Object target) {
        this.target = target;
    }
    
    /**
     * 创建代理对象
     */
    public static Object createProxy(Object target) {
        return Proxy.newProxyInstance(
            target.getClass().getClassLoader(),           // 类加载器
            target.getClass().getInterfaces(),            // 目标对象实现的接口
            new JdkDynamicProxy(target)                   // 调用处理器
        );
    }
    
    /**
     * 代理方法调用
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 前置切面逻辑
        System.out.println("[JDK代理] 方法执行前: " + method.getName());
        
        // 调用目标方法
        Object result = method.invoke(target, args);
        
        // 后置切面逻辑
        System.out.println("[JDK代理] 方法执行后: " + method.getName());
        
        return result;
    }
}

// 使用示例
public interface UserService {
    User createUser(User user);
}

public class UserServiceImpl implements UserService {
    public User createUser(User user) {
        System.out.println("创建用户: " + user.getUsername());
        return user;
    }
}

// 创建代理
UserService proxy = (UserService) JdkDynamicProxy.createProxy(new UserServiceImpl());
proxy.createUser(new User("张三"));
```

**JDK 代理的特点**：

| 特性 | 说明 |
|------|------|
| **原理** | 基于接口实现，代理类实现目标接口 |
| **性能** | 较高，JDK 原生支持 |
| **限制** | 只能代理接口方法，无法代理类的非接口方法 |
| **生成类名** | 如 `$Proxy0`, `$Proxy1` |

##### 2. CGLIB 代理

**原理**：基于字节码生成库，通过继承目标类生成子类作为代理。

**适用条件**：目标类可以不实现接口。

**实现过程**：

```java
public class CglibProxy implements MethodInterceptor {
    
    private Object target;
    
    public CglibProxy(Object target) {
        this.target = target;
    }
    
    /**
     * 创建代理对象
     */
    public static Object createProxy(Object target) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());          // 设置父类
        enhancer.setCallback(new CglibProxy(target));       // 设置回调
        return enhancer.create();                           // 创建代理对象
    }
    
    /**
     * 拦截方法调用
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, 
                           MethodProxy proxy) throws Throwable {
        // 前置切面逻辑
        System.out.println("[CGLIB代理] 方法执行前: " + method.getName());
        
        // 调用目标方法（通过父类方法调用）
        Object result = proxy.invokeSuper(obj, args);
        
        // 后置切面逻辑
        System.out.println("[CGLIB代理] 方法执行后: " + method.getName());
        
        return result;
    }
}

// 使用示例（目标类无需实现接口）
public class OrderService {
    public void createOrder(Order order) {
        System.out.println("创建订单: " + order.getId());
    }
}

// 创建代理
OrderService proxy = (OrderService) CglibProxy.createProxy(new OrderService());
proxy.createOrder(new Order("ORD001"));
```

**CGLIB 代理的特点**：

| 特性 | 说明 |
|------|------|
| **原理** | 基于继承，代理类继承目标类 |
| **性能** | 较低，需要生成字节码 |
| **限制** | 无法代理 final 方法（无法被覆盖） |
| **生成类名** | 如 `OrderService$$EnhancerByCGLIB$$xxxx` |

##### 3. 两种代理方式对比

| 对比维度 | JDK 动态代理 | CGLIB 代理 |
|---------|-------------|-----------|
| **代理原理** | 实现目标接口 | 继承目标类 |
| **适用条件** | 目标类实现接口 | 无要求 |
| **性能** | 较高 | 较低 |
| **方法覆盖** | 只能覆盖接口方法 | 可以覆盖所有非 final 方法 |
| **final 方法** | 不影响 | 无法代理 |
| **类加载器** | 使用目标类的类加载器 | 需要 CGLIB 类加载器 |
| **依赖** | JDK 原生，无需额外依赖 | 需要引入 cglib 依赖 |

---

#### 1.7.2 代理选择策略

**Spring 自动选择逻辑**：

```java
// Spring 内部代理选择逻辑简化版
public class ProxyFactory {
    
    public Object createProxy(Object target) {
        Class<?> targetClass = target.getClass();
        
        // 判断是否实现了接口
        if (targetClass.getInterfaces().length > 0) {
            // 使用 JDK 动态代理
            return JdkDynamicProxy.createProxy(target);
        } else {
            // 使用 CGLIB 代理
            return CglibProxy.createProxy(target);
        }
    }
}
```

**强制使用 CGLIB 代理**：

```java
// 方式一：通过 @EnableAspectJAutoProxy 注解
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)  // 强制使用 CGLIB
public class AppConfig { }

// 方式二：通过 XML 配置
<aop:aspectj-autoproxy proxy-target-class="true" />
```

**proxyTargetClass 属性说明**：

| 值 | 说明 |
|----|------|
| `false`（默认） | 优先使用 JDK 动态代理 |
| `true` | 强制使用 CGLIB 代理 |

---

#### 1.7.3 代理创建过程详解

**完整的代理创建流程**：

```
┌───────────────────────────────────────────────────────────────────┐
│                    Spring 容器启动流程                           │
├───────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 扫描 Bean 定义                                              │
│       │                                                         │
│       ▼                                                         │
│  2. 实例化目标对象                                               │
│       │                                                         │
│       ▼                                                         │
│  3. 检测切面（@Aspect 注解的类）                                 │
│       │                                                         │
│       ▼                                                         │
│  4. 解析切点表达式                                               │
│       │                                                         │
│       ▼                                                         │
│  5. 判断是否有匹配的切面                                         │
│       │                                                         │
│       ├─ 有匹配 ──► 创建代理对象                                 │
│       │                                                         │
│       └─ 无匹配 ──► 使用原始对象                                 │
│                                                                 │
└───────────────────────────────────────────────────────────────────┘
```

**Spring AOP 内部实现**：

```java
// Spring AOP 代理创建核心逻辑
public class DefaultAopProxyFactory implements AopProxyFactory {
    
    @Override
    public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
        // 判断是否强制使用 CGLIB，或者目标类没有实现接口
        if (config.isProxyTargetClass() || 
            !hasNoUserSuppliedProxyInterfaces(config)) {
            return new ObjenesisCglibAopProxy(config);
        } else {
            return new JdkDynamicAopProxy(config);
        }
    }
    
    private boolean hasNoUserSuppliedProxyInterfaces(AdvisedSupport config) {
        Class<?>[] interfaces = config.getProxiedInterfaces();
        return interfaces.length == 0 || 
               (interfaces.length == 1 && SpringProxy.class.isAssignableFrom(interfaces[0]));
    }
}
```

**代理对象的结构**：

```
代理对象 (Proxy)
    │
    ├── AdvisedSupport （代理配置）
    │       ├── TargetSource （目标对象来源）
    │       ├── Advisor[] （切面通知器列表）
    │       └── ProxyConfig （代理配置参数）
    │
    └── 拦截链 (Interceptor Chain)
            ├── MethodBeforeAdviceInterceptor (@Before)
            ├── AspectJAfterAdvice (@After)
            ├── AfterReturningAdviceInterceptor (@AfterReturning)
            ├── AspectJAfterThrowingAdvice (@AfterThrowing)
            └── MethodInvocationProceedingJoinPoint (@Around)
```

---

#### 1.7.4 方法调用拦截过程

**完整的方法调用流程**：

```
客户端 ──► 代理对象.invoke()
                │
                ▼
    ┌───────────────────────────────┐
    │   创建 MethodInvocation       │
    │   (方法调用上下文)             │
    └───────────────────────────────┘
                │
                ▼
    ┌───────────────────────────────┐
    │   遍历拦截器链               │
    │   Interceptor Chain          │
    └───────────────────────────────┘
                │
                ├──► @Before 通知
                │
                ├──► @Around.proceed() 前
                │
                ├──► 目标方法执行
                │
                ├──► @Around.proceed() 后
                │
                ├──► @AfterReturning 通知
                │
                ├──► @AfterThrowing 通知（如果有异常）
                │
                └──► @After 通知
                │
                ▼
           返回结果给客户端
```

**拦截器链执行顺序**：

```java
// Spring AOP 拦截器链执行逻辑
public Object proceed() throws Throwable {
    // 如果所有拦截器都执行完毕，调用目标方法
    if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
        return invokeJoinpoint();
    }
    
    // 获取下一个拦截器
    InterceptorAndDynamicMethodMatcher interceptor = 
        this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
    
    // 执行拦截器
    if (interceptor.interceptor instanceof MethodInterceptor) {
        MethodInterceptor mi = (MethodInterceptor) interceptor.interceptor;
        return mi.invoke(this);  // 递归调用，形成链式执行
    } else {
        return proceed();  // 继续执行下一个拦截器
    }
}
```

---

#### 1.7.5 代理对象的生命周期

```
1. Bean 定义注册
       │
       ▼
2. Bean 实例化（createBeanInstance）
       │
       ▼
3. 属性填充（populateBean）
       │
       ▼
4. 初始化前（postProcessBeforeInitialization）
       │
       ▼
5. 初始化（initializeBean）
       │
       ▼
6. AOP 代理创建（wrapIfNecessary）
       │
       ├─ 检测是否有匹配的切面
       ├─ 选择代理方式（JDK/CGLIB）
       └─ 创建代理对象
       │
       ▼
7. 初始化后（postProcessAfterInitialization）
       │
       ▼
8. Bean 放入容器（singletonObjects）
       │
       ▼
9. 方法调用（通过代理对象）
```

---

#### 1.7.6 重要注意事项

##### 1. 自调用问题

```java
@Service
public class UserService {
    
    public void methodA() {
        // 自调用，不会经过代理！
        this.methodB();  // 直接调用，切面不会生效
    }
    
    @Transactional
    public void methodB() { ... }
}
```

**解决方案**：

```java
@Service
public class UserService {
    
    @Autowired
    private UserService self;  // 注入代理对象
    
    public void methodA() {
        // 通过代理对象调用，切面生效
        self.methodB();
    }
    
    @Transactional
    public void methodB() { ... }
}
```

##### 2. 内部类方法调用

```java
@Service
public class UserService {
    
    public void outerMethod() {
        InnerClass inner = new InnerClass();
        inner.innerMethod();  // 不会经过代理
    }
    
    public class InnerClass {
        @Transactional
        public void innerMethod() { ... }
    }
}
```

##### 3. 静态方法和 final 方法

```java
@Service
public class UserService {
    
    @Transactional
    public static void staticMethod() { 
        // 静态方法不会被代理！
    }
    
    @Transactional
    public final void finalMethod() { 
        // final 方法不会被 CGLIB 代理！
    }
}
```

##### 4. 性能考虑

```java
// 高频调用的方法，避免使用复杂的 @Around 切面
@Around("execution(* com.moodnote.high.frequency..*.*(..))")
public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    // 尽量减少切面逻辑的复杂度
    return joinPoint.proceed();
}
```

---

## 二、注解（Annotation）

注解是 Java 5 引入的一种元数据机制，允许在代码中添加元信息。它是一种强大的工具，广泛应用于框架（如 Spring、JUnit）和代码分析工具中。

---

### 2.1 注解基本语法

#### 2.1.1 注解定义详解

**注解的本质**：注解本质上是一种特殊的接口，继承自 `java.lang.annotation.Annotation`。

```java
// 自定义注解的完整结构
public @interface MyAnnotation {
    
    // 1. 简单属性 - value 属性是特殊的，可以省略名称
    String value() default "";
    
    // 2. 带默认值的属性
    int count() default 0;
    
    // 3. 数组属性
    String[] tags() default {};
    
    // 4. 枚举属性
    OperationType operation() default OperationType.INSERT;
    
    // 5. Class 属性
    Class<?> targetClass() default Object.class;
    
    // 6. 嵌套注解属性
    InnerAnnotation inner() default @InnerAnnotation;
}

// 枚举定义
public enum OperationType {
    INSERT, UPDATE, DELETE, SELECT
}

// 嵌套注解
public @interface InnerAnnotation {
    String name() default "default";
}
```

**注解定义的语法规则**：

| 规则 | 说明 | 示例 |
|------|------|------|
| 修饰符 | 必须是 public | `public @interface` |
| 属性类型 | 有限制，见下表 | `String value()` |
| 属性命名 | 遵循 Java 标识符规则 | `count`, `operationType` |
| 默认值 | 使用 `default` 关键字 | `default ""` |
| 无参数 | 可以定义无参数属性 | `void enabled()` |

**注解属性类型限制**：

| 类型 | 说明 | 示例 |
|------|------|------|
| **基本类型** | int, boolean, char, byte, short, long, float, double | `int count()` |
| **String** | 字符串类型 | `String name()` |
| **Class** | 类类型 | `Class<?> type()` |
| **enum** | 枚举类型 | `OperationType op()` |
| **Annotation** | 注解类型 | `@InnerAnnotation inner()` |
| **数组类型** | 以上类型的数组 | `String[] tags()` |

**注意事项**：
- 注解属性不能有泛型参数
- 不能使用 `null` 作为默认值
- 数组属性的默认值使用 `{}`

#### 2.1.2 注解使用详解

**基本使用方式**：

```java
// 方式1：完整属性名
@MyAnnotation(value = "test", count = 5)
public class MyClass { }

// 方式2：省略 value 属性名（只有 value 属性时）
@MyAnnotation("hello")
public class MyClass { }

// 方式3：使用默认值
@MyAnnotation
public class MyClass { }

// 方式4：数组属性
@MyAnnotation(tags = {"tag1", "tag2", "tag3"})
public class MyClass { }

// 方式5：单个元素的数组可以省略大括号
@MyAnnotation(tags = "singleTag")
public class MyClass { }

// 方式6：嵌套注解
@MyAnnotation(
    inner = @InnerAnnotation(name = "custom")
)
public class MyClass { }
```

**注解的应用位置**：

```java
// 1. 类级别
@MyAnnotation
public class UserService { }

// 2. 方法级别
public class UserService {
    @MyAnnotation
    public User createUser(User user) { }
}

// 3. 字段级别
public class User {
    @MyAnnotation
    private String username;
}

// 4. 参数级别
public class UserService {
    public User getUserById(@MyAnnotation Long id) { }
}

// 5. 构造方法级别
public class User {
    @MyAnnotation
    public User(String username) { }
}

// 6. 局部变量级别
public void method() {
    @MyAnnotation
    String temp = "test";
}
```

#### 2.1.3 注解属性的高级用法

**1. 数组属性的处理**：

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface Tags {
    String[] value() default {};
}

// 使用
@Tags({"spring", "aop", "annotation"})
public class MyClass { }

// 反射获取
Tags tags = MyClass.class.getAnnotation(Tags.class);
String[] tagArray = tags.value();
```

**2. 枚举属性的处理**：

```java
public enum LogLevel {
    DEBUG, INFO, WARN, ERROR
}

@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    LogLevel level() default LogLevel.INFO;
}

// 使用
@Log(level = LogLevel.DEBUG)
public void debugMethod() { }

// 反射获取
Log log = MyClass.class.getMethod("debugMethod").getAnnotation(Log.class);
LogLevel level = log.level();
```

**3. Class 属性的处理**：

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {
    Class<? extends Validator> value();
}

public interface Validator {
    boolean validate(Object obj);
}

public class EmailValidator implements Validator {
    public boolean validate(Object obj) { return true; }
}

// 使用
@Validate(EmailValidator.class)
public String email;

// 反射获取并实例化
Validate validate = User.class.getDeclaredField("email").getAnnotation(Validate.class);
Validator validator = validate.value().newInstance();
```

**4. 嵌套注解的处理**：

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface Outer {
    Inner inner() default @Inner;
}

@Retention(RetentionPolicy.RUNTIME)
public @interface Inner {
    String name() default "default";
    int value() default 0;
}

// 使用
@Outer(inner = @Inner(name = "custom", value = 10))
public class MyClass { }

// 反射获取
Outer outer = MyClass.class.getAnnotation(Outer.class);
Inner inner = outer.inner();
String name = inner.name();  // "custom"
int value = inner.value();   // 10
```

---

#### 2.1.4 注解与反射的结合

**完整示例**：

```java
// 定义注解
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    String name() default "";
    int length() default 255;
    boolean nullable() default true;
}

// 使用注解
public class User {
    @Column(name = "user_id", nullable = false)
    private Long id;
    
    @Column(name = "user_name", length = 100)
    private String username;
    
    @Column(name = "email")
    private String email;
}

// 通过反射解析注解
public class ColumnParser {
    public static Map<String, Column> parse(Class<?> clazz) {
        Map<String, Column> columnMap = new HashMap<>();
        
        // 获取所有字段
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            // 检查字段是否有 @Column 注解
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                columnMap.put(field.getName(), column);
                
                // 打印注解信息
                System.out.println("字段: " + field.getName());
                System.out.println("  列名: " + column.name());
                System.out.println("  长度: " + column.length());
                System.out.println("  是否可空: " + column.nullable());
            }
        }
        
        return columnMap;
    }
}

// 使用解析器
public class Main {
    public static void main(String[] args) {
        ColumnParser.parse(User.class);
    }
}
```

**输出结果**：

```
字段: id
  列名: user_id
  长度: 255
  是否可空: false
字段: username
  列名: user_name
  长度: 100
  是否可空: true
字段: email
  列名: email
  长度: 255
  是否可空: true
```

---

#### 2.1.5 注解的实际应用场景

| 场景 | 说明 | 示例 |
|------|------|------|
| **配置元数据** | 为类/方法/字段添加配置信息 | `@Column`, `@Table` |
| **编译时检查** | 在编译阶段进行代码检查 | `@Override`, `@Deprecated` |
| **运行时处理** | 通过反射在运行时获取注解信息 | Spring 的 `@Autowired` |
| **代码生成** | 根据注解生成代码 | Lombok 的 `@Data` |
| **AOP 切点** | 作为 AOP 切点表达式的匹配条件 | `@LogOperation` |
| **测试框架** | 标记测试方法 | JUnit 的 `@Test` |

---

#### 2.1.6 注解与接口的区别

| 特性 | 注解 (@interface) | 接口 (interface) |
|------|------------------|-----------------|
| **定义方式** | `public @interface` | `public interface` |
| **继承关系** | 隐式继承 `Annotation` | 显式继承其他接口 |
| **方法体** | 不允许有方法体 | 可以有默认方法体 |
| **属性类型** | 有限制（基本类型、String、Class、enum、注解、数组） | 无限制 |
| **默认值** | 可以有默认值 | 默认方法可以有实现 |
| **使用方式** | `@AnnotationName` | 实现接口 |
| **反射处理** | 通过 `getAnnotation()` 获取 | 通过 `getInterfaces()` 获取 |

### 2.2 元注解详解

元注解是用于定义注解的注解，Java 提供了五个标准元注解。

#### 2.2.1 @Target

指定注解可以应用的目标元素类型。

```java
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface MyAnnotation {
    // ...
}
```

**ElementType 枚举值**：

| 值 | 说明 |
|----|------|
| `TYPE` | 类、接口、枚举 |
| `FIELD` | 字段 |
| `METHOD` | 方法 |
| `PARAMETER` | 参数 |
| `CONSTRUCTOR` | 构造方法 |
| `LOCAL_VARIABLE` | 局部变量 |
| `ANNOTATION_TYPE` | 注解类型 |
| `PACKAGE` | 包 |
| `TYPE_PARAMETER` | 类型参数（Java 8+） |
| `TYPE_USE` | 类型使用（Java 8+） |

#### 2.2.2 @Retention

指定注解的保留策略，即注解在什么阶段仍然有效。

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
    // ...
}
```

**RetentionPolicy 枚举值**：

| 值 | 说明 | 适用场景 |
|----|------|----------|
| `SOURCE` | 只在源码阶段保留 | 编译检查（如 @Override） |
| `CLASS` | 在编译后的字节码中保留，但运行时不可用 | 字节码增强 |
| `RUNTIME` | 在运行时保留，可以通过反射获取 | 运行时处理（如 Spring 注解） |

#### 2.2.3 @Documented

指定注解是否应该被 JavaDoc 工具记录。

```java
@Documented
public @interface MyAnnotation {
    // ...
}
```

#### 2.2.4 @Inherited

指定注解是否可以被子类继承。

```java
@Inherited
public @interface MyAnnotation {
    // ...
}

@MyAnnotation
public class Parent { }

public class Child extends Parent { 
    // 自动继承 @MyAnnotation
}
```

#### 2.2.5 @Repeatable

指定注解可以在同一个元素上重复使用（Java 8+）。

```java
// 容器注解
public @interface MyAnnotations {
    MyAnnotation[] value();
}

// 可重复注解
@Repeatable(MyAnnotations.class)
public @interface MyAnnotation {
    String value();
}

// 使用
@MyAnnotation("first")
@MyAnnotation("second")
public class MyClass { }
```

### 2.3 常用内置注解

#### 2.3.1 @Override

标记方法覆盖了父类的方法。

```java
@Override
public String toString() {
    return super.toString();
}
```

#### 2.3.2 @Deprecated

标记方法或类已过时。

```java
@Deprecated
public void oldMethod() {
    // 已过时，请使用 newMethod()
}
```

#### 2.3.3 @SuppressWarnings

抑制编译器警告。

```java
@SuppressWarnings("unchecked")
public List<String> getList() {
    return new ArrayList();  // 无泛型警告
}
```

#### 2.3.4 @FunctionalInterface

标记函数式接口（只有一个抽象方法的接口）。

```java
@FunctionalInterface
public interface MyInterface {
    void doSomething();
}
```

---

## 三、反射（Reflection）

反射是 Java 提供的一种强大机制，允许程序在运行时动态获取类的信息并操作类的成员。它是许多框架（如 Spring、Hibernate）的核心基础。

---

### 3.1 反射概念详解

**定义**：反射是指程序能够在运行时获取类的完整结构信息，并能动态操作这些信息的能力。

**反射的核心能力**：

| 能力 | 说明 | 示例 |
|------|------|------|
| **获取类信息** | 获取类的名称、父类、接口、注解等 | `clazz.getName()` |
| **动态创建对象** | 在运行时创建任意类的实例 | `clazz.newInstance()` |
| **动态调用方法** | 调用任意方法，包括私有方法 | `method.invoke()` |
| **动态访问字段** | 读取和修改任意字段，包括私有字段 | `field.get()`, `field.set()` |
| **获取注解信息** | 获取类/方法/字段上的注解 | `clazz.getAnnotation()` |

**反射的工作原理**：

```
┌─────────────────────────────────────────────────────────────┐
│                      JVM 运行时                            │
│                                                           │
│  ┌─────────────────┐    ┌─────────────────────────────┐   │
│  │   字节码文件     │───►│        Class 对象          │   │
│  │   (.class)      │    │  (包含类的完整元数据)        │   │
│  └─────────────────┘    └─────────────────────────────┘   │
│                                     │                     │
│                                     ▼                     │
│                    ┌─────────────────────────────┐        │
│                    │        反射 API              │        │
│                    │  Class / Method / Field      │        │
│                    │  Constructor / Annotation    │        │
│                    └─────────────────────────────┘        │
│                                     │                     │
│                                     ▼                     │
│                    ┌─────────────────────────────┐        │
│                    │     动态操作                   │        │
│                    │  创建对象 / 调用方法 / 访问字段  │        │
│                    └─────────────────────────────┘        │
└─────────────────────────────────────────────────────────────┘
```

**反射的优缺点**：

| 维度 | 优点 | 缺点 |
|------|------|------|
| **灵活性** | 可以处理编译时未知的类型 | - |
| **通用性** | 支持框架的通用设计 | - |
| **性能** | - | 开销较大，比直接调用慢 10-100 倍 |
| **封装性** | - | 可以突破封装，访问私有成员 |
| **可读性** | - | 代码较为复杂，可读性差 |
| **安全性** | - | 可能绕过安全检查 |

---

### 3.2 Class 对象获取方式详解

Class 对象是反射的入口，代表类或接口在 JVM 中的运行时表示。

#### 3.2.1 方式一：对象.getClass()

**适用场景**：已经有对象实例时

```java
String str = "Hello";
Class<?> clazz = str.getClass();
System.out.println(clazz.getName());  // 输出: java.lang.String

User user = new User();
Class<?> userClass = user.getClass();
System.out.println(userClass.getSimpleName());  // 输出: User
```

**特点**：
- 需要先创建对象实例
- 返回的是对象实际类型的 Class 对象（考虑多态）

```java
// 多态示例
Animal animal = new Dog();
Class<?> clazz = animal.getClass();
System.out.println(clazz.getSimpleName());  // 输出: Dog（实际类型）
```

#### 3.2.2 方式二：类名.class

**适用场景**：已知类名，不需要创建对象

```java
Class<?> stringClass = String.class;
Class<?> userClass = User.class;
Class<?> intClass = int.class;  // 基本类型
Class<?> voidClass = void.class;  // void 类型
```

**特点**：
- 不需要创建对象，性能更好
- 可以获取基本类型的 Class 对象

#### 3.2.3 方式三：Class.forName()

**适用场景**：运行时动态加载类，类名在编译时未知

```java
// 完整类名
Class<?> stringClass = Class.forName("java.lang.String");

// 加载自定义类
Class<?> userClass = Class.forName("com.moodnote.pojo.User");
```

**特点**：
- 支持动态加载，类名可以来自配置文件
- 需要处理 `ClassNotFoundException` 异常
- 会触发类的静态初始化块

```java
public class MyClass {
    static {
        System.out.println("静态初始化块执行");
    }
}

// 使用 forName 会触发静态初始化
Class<?> clazz = Class.forName("com.moodnote.MyClass");
// 输出: 静态初始化块执行
```

#### 3.2.4 方式四：ClassLoader.loadClass()

**适用场景**：只加载类，不触发初始化

```java
ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
Class<?> clazz = classLoader.loadClass("com.moodnote.MyClass");
// 不会触发静态初始化块
```

**与 forName 的区别**：

| 方法 | 是否触发静态初始化 | 是否链接类 |
|------|------------------|----------|
| `Class.forName()` | 是 | 是 |
| `ClassLoader.loadClass()` | 否 | 否 |

#### 3.2.5 方式五：包装类.TYPE

**适用场景**：获取基本类型的 Class 对象

```java
Class<?> intClass = Integer.TYPE;    // 等同于 int.class
Class<?> boolClass = Boolean.TYPE;   // 等同于 boolean.class
Class<?> voidClass = Void.TYPE;      // 等同于 void.class
```

---

### 3.3 反射 API 链路详解

#### 3.3.1 类继承关系

```
Object
  ├── AccessibleObject (抽象类)
  │     ├── Constructor<T> (构造方法)
  │     ├── Field (字段)
  │     └── Method (方法)
  └── Class<T> (类的元数据)
```

**AccessibleObject**：
- 是 Constructor、Field、Method 的父类
- 提供 `setAccessible(boolean)` 方法，用于突破访问控制

**Class**：
- 代表类或接口
- 提供获取构造方法、方法、字段、注解等的 API

#### 3.3.2 思维链路详解

```
┌─────────────────────────────────────────────────────────────┐
│                    反射操作流程                             │
├─────────────────────────────────────────────────────────────┤
│                                                           │
│  阶段1: 获取 Class 对象                                    │
│         │                                                  │
│         ├─ obj.getClass()                                  │
│         ├─ ClassName.class                                 │
│         └─ Class.forName("className")                     │
│                                                           │
│         ▼                                                  │
│                                                           │
│  阶段2: 获取构造方法                                        │
│         │                                                  │
│         ├─ getConstructor(Class<?>...)  // 公共构造方法    │
│         └─ getDeclaredConstructor(Class<?>...)  // 所有    │
│                                                           │
│         ▼                                                  │
│                                                           │
│  阶段3: 创建对象实例                                        │
│         │                                                  │
│         ├─ constructor.newInstance(Object...)             │
│         └─ clazz.newInstance()  // 无参构造               │
│                                                           │
│         ▼                                                  │
│                                                           │
│  阶段4: 获取方法或字段                                      │
│         │                                                  │
│         ├─ getMethod(String, Class<?>...)  // 公共方法    │
│         ├─ getDeclaredMethod(String, Class<?>...)  // 所有 │
│         ├─ getField(String)  // 公共字段                   │
│         └─ getDeclaredField(String)  // 所有字段          │
│                                                           │
│         ▼                                                  │
│                                                           │
│  阶段5: 执行操作                                            │
│         │                                                  │
│         ├─ method.invoke(Object, Object...)  // 调用方法   │
│         ├─ field.get(Object)  // 读取字段                  │
│         └─ field.set(Object, Object)  // 设置字段          │
│                                                           │
└─────────────────────────────────────────────────────────────┘
```

#### 3.3.3 完整示例：动态创建对象并调用方法

```java
public class ReflectDemo {
    public static void main(String[] args) throws Exception {
        // 阶段1: 获取 Class 对象
        Class<?> userClass = Class.forName("com.moodnote.pojo.User");
        
        // 阶段2: 获取构造方法
        Constructor<?> constructor = userClass.getConstructor(String.class, Integer.class);
        
        // 阶段3: 创建对象实例
        Object user = constructor.newInstance("张三", 25);
        
        // 阶段4: 获取方法
        Method setNameMethod = userClass.getMethod("setName", String.class);
        Method getNameMethod = userClass.getMethod("getName");
        
        // 阶段5: 调用方法
        setNameMethod.invoke(user, "李四");
        String name = (String) getNameMethod.invoke(user);
        System.out.println(name);  // 输出: 李四
        
        // 获取并设置私有字段
        Field ageField = userClass.getDeclaredField("age");
        ageField.setAccessible(true);  // 突破封装
        ageField.set(user, 30);
        int age = (int) ageField.get(user);
        System.out.println(age);  // 输出: 30
    }
}
```

---

#### 3.3.4 反射 API 分类汇总

**Class 类的核心方法**：

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `getName()` | 获取全限定类名 | `String` |
| `getSimpleName()` | 获取简单类名 | `String` |
| `getSuperclass()` | 获取父类 | `Class<?>` |
| `getInterfaces()` | 获取实现的接口 | `Class<?>[]` |
| `getConstructors()` | 获取所有公共构造方法 | `Constructor<?>[]` |
| `getDeclaredConstructors()` | 获取所有构造方法 | `Constructor<?>[]` |
| `getConstructor(Class<?>...)` | 获取指定公共构造方法 | `Constructor<T>` |
| `getDeclaredConstructor(Class<?>...)` | 获取指定构造方法 | `Constructor<T>` |
| `getMethods()` | 获取所有公共方法（含继承） | `Method[]` |
| `getDeclaredMethods()` | 获取所有方法（不含继承） | `Method[]` |
| `getMethod(String, Class<?>...)` | 获取指定公共方法 | `Method` |
| `getDeclaredMethod(String, Class<?>...)` | 获取指定方法 | `Method` |
| `getFields()` | 获取所有公共字段（含继承） | `Field[]` |
| `getDeclaredFields()` | 获取所有字段（不含继承） | `Field[]` |
| `getField(String)` | 获取指定公共字段 | `Field` |
| `getDeclaredField(String)` | 获取指定字段 | `Field` |
| `getAnnotations()` | 获取所有注解 | `Annotation[]` |
| `getAnnotation(Class<A>)` | 获取指定注解 | `A` |
| `newInstance()` | 使用无参构造创建对象 | `Object` |
| `isInterface()` | 是否是接口 | `boolean` |
| `isPrimitive()` | 是否是基本类型 | `boolean` |
| `isArray()` | 是否是数组 | `boolean` |

**Constructor 类的核心方法**：

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `newInstance(Object...)` | 创建对象实例 | `Object` |
| `getParameterTypes()` | 获取参数类型 | `Class<?>[]` |
| `getExceptionTypes()` | 获取异常类型 | `Class<?>[]` |
| `setAccessible(boolean)` | 设置是否可访问 | `void` |

**Method 类的核心方法**：

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `invoke(Object, Object...)` | 调用方法 | `Object` |
| `getName()` | 获取方法名 | `String` |
| `getReturnType()` | 获取返回类型 | `Class<?>` |
| `getParameterTypes()` | 获取参数类型 | `Class<?>[]` |
| `getExceptionTypes()` | 获取异常类型 | `Class<?>[]` |
| `setAccessible(boolean)` | 设置是否可访问 | `void` |

**Field 类的核心方法**：

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `get(Object)` | 获取字段值 | `Object` |
| `set(Object, Object)` | 设置字段值 | `void` |
| `getName()` | 获取字段名 | `String` |
| `getType()` | 获取字段类型 | `Class<?>` |
| `setAccessible(boolean)` | 设置是否可访问 | `void` |

---

#### 3.3.5 反射的实际应用场景

| 场景 | 说明 | 示例 |
|------|------|------|
| **依赖注入** | Spring 通过反射注入依赖 | `@Autowired` |
| **ORM 框架** | Hibernate 通过反射映射数据库 | `@Entity`, `@Column` |
| **测试框架** | JUnit 通过反射调用测试方法 | `@Test` |
| **序列化** | JSON 序列化/反序列化 | Jackson, Gson |
| **动态代理** | AOP 通过反射创建代理 | JDK 动态代理 |
| **代码生成** | 根据配置动态生成代码 | Lombok |
| **插件系统** | 运行时动态加载插件 | OSGi |

---

#### 3.3.6 反射的性能优化

**性能问题的原因**：

1. **类型解析开销**：需要在运行时解析类型信息
2. **安全检查开销**：每次调用都要进行安全检查
3. **方法调用开销**：反射调用比直接调用慢

**优化策略**：

```java
// 1. 缓存 Class 对象
private static final Class<User> USER_CLASS = User.class;

// 2. 缓存 Method 对象
private static Method setNameMethod;

static {
    try {
        setNameMethod = USER_CLASS.getMethod("setName", String.class);
        setNameMethod.setAccessible(true);  // 一次性设置，避免重复检查
    } catch (NoSuchMethodException e) {
        e.printStackTrace();
    }
}

// 3. 使用批量操作
public void batchSetName(List<User> users, String name) throws Exception {
    for (User user : users) {
        setNameMethod.invoke(user, name);  // 使用缓存的方法
    }
}
```

**性能对比**：

| 操作 | 直接调用 | 反射调用 | 差距 |
|------|---------|---------|------|
| 方法调用 | ~1 ns | ~100 ns | 100x |
| 对象创建 | ~10 ns | ~1000 ns | 100x |
| 字段访问 | ~1 ns | ~50 ns | 50x |

### 3.4 Class 类常用 API

#### 3.4.1 获取类的基本信息

```java
Class<?> clazz = User.class;

// 获取类名
String className = clazz.getName();           // 全限定名
String simpleName = clazz.getSimpleName();   // 简单类名

// 获取包信息
Package pkg = clazz.getPackage();

// 获取父类
Class<?> superClass = clazz.getSuperclass();

// 获取实现的接口
Class<?>[] interfaces = clazz.getInterfaces();
```

#### 3.4.2 获取构造方法

```java
Class<?> clazz = User.class;

// 获取所有构造方法
Constructor<?>[] constructors = clazz.getConstructors();

// 获取指定参数类型的构造方法
Constructor<?> constructor = clazz.getConstructor(String.class, int.class);

// 使用构造方法创建对象
Object obj = constructor.newInstance("test", 25);
```

#### 3.4.3 获取字段

```java
Class<?> clazz = User.class;

// 获取所有公共字段
Field[] fields = clazz.getFields();

// 获取所有字段（包括私有）
Field[] allFields = clazz.getDeclaredFields();

// 获取指定名称的字段
Field field = clazz.getDeclaredField("username");

// 设置字段可访问（突破封装）
field.setAccessible(true);

// 获取字段值
Object value = field.get(obj);

// 设置字段值
field.set(obj, "newValue");
```

#### 3.4.4 获取方法

```java
Class<?> clazz = User.class;

// 获取所有公共方法（包括继承的）
Method[] methods = clazz.getMethods();

// 获取所有方法（包括私有，不包括继承的）
Method[] allMethods = clazz.getDeclaredMethods();

// 获取指定方法
Method method = clazz.getDeclaredMethod("setUsername", String.class);

// 设置方法可访问
method.setAccessible(true);

// 调用方法
Object result = method.invoke(obj, "newUsername");
```

### 3.5 实战示例

#### 3.5.1 通过反射创建对象

```java
// 获取 Class 对象
Class<?> clazz = User.class;

// 获取无参构造方法
Constructor<?> constructor = clazz.getConstructor();

// 创建对象
User user = (User) constructor.newInstance();
```

#### 3.5.2 通过反射调用方法

```java
// 获取 Class 对象
Class<?> clazz = User.class;

// 创建对象
User user = (User) clazz.getConstructor().newInstance();

// 获取方法
Method setNameMethod = clazz.getMethod("setName", String.class);

// 调用方法
setNameMethod.invoke(user, "张三");

// 获取方法返回值
Method getNameMethod = clazz.getMethod("getName");
String name = (String) getNameMethod.invoke(user);
```

#### 3.5.3 通过反射访问字段

```java
// 获取 Class 对象
Class<?> clazz = User.class;

// 创建对象
User user = (User) clazz.getConstructor().newInstance();

// 获取私有字段
Field privateField = clazz.getDeclaredField("password");

// 设置可访问
privateField.setAccessible(true);

// 设置字段值
privateField.set(user, "123456");

// 获取字段值
String password = (String) privateField.get(user);
```

---

## 四、综合案例：自定义注解 + AOP + 反射

### 4.1 定义自定义注解

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    OperationType value();
}
```

### 4.2 定义操作类型枚举

```java
public enum OperationType {
    INSERT,
    UPDATE
}
```

### 4.3 实现切面

```java
@Aspect
@Component
public class AutoFillAspect {
    
    @Before("@annotation(autoFill)")
    public void autoFill(JoinPoint joinPoint, AutoFill autoFill) throws Exception {
        // 获取操作类型
        OperationType operationType = autoFill.value();
        
        // 获取目标对象
        Object target = joinPoint.getArgs()[0];
        
        // 获取 Class 对象
        Class<?> clazz = target.getClass();
        
        // 获取方法
        Method setUpdateTimeMethod = clazz.getDeclaredMethod("setUpdateTime", LocalDateTime.class);
        
        // 设置可访问
        setUpdateTimeMethod.setAccessible(true);
        
        // 调用方法设置值
        setUpdateTimeMethod.invoke(target, LocalDateTime.now());
        
        // 如果是插入操作，还需要设置创建时间
        if (operationType == OperationType.INSERT) {
            Method setCreateTimeMethod = clazz.getDeclaredMethod("setCreateTime", LocalDateTime.class);
            setCreateTimeMethod.setAccessible(true);
            setCreateTimeMethod.invoke(target, LocalDateTime.now());
        }
    }
}
```

### 4.4 使用注解

```java
@Mapper
public interface UserMapper {
    
    @AutoFill(OperationType.INSERT)
    void insert(User user);
    
    @AutoFill(OperationType.UPDATE)
    void update(User user);
}
```

---

## 五、总结

### 5.1 AOP 要点

| 概念 | 说明 |
|------|------|
| 切面（Aspect） | 横切关注点的模块化 |
| 通知（Advice） | 在特定连接点执行的代码 |
| 切点（Pointcut） | 匹配连接点的表达式 |
| 连接点（JoinPoint） | 程序执行的某个特定点 |
| 织入（Weaving） | 将切面应用到目标对象的过程 |

### 5.2 注解要点

| 元注解 | 作用 |
|--------|------|
| @Target | 指定注解应用位置 |
| @Retention | 指定注解保留策略 |
| @Documented | 生成 JavaDoc 文档 |
| @Inherited | 允许子类继承 |
| @Repeatable | 允许重复使用 |

### 5.3 反射要点

| 步骤 | API | 说明 |
|------|-----|------|
| 1 | `Class.forName()` / `obj.getClass()` / `类.class` | 获取 Class 对象 |
| 2 | `getConstructor()` / `getDeclaredConstructor()` | 获取构造方法 |
| 3 | `newInstance()` | 创建对象 |
| 4 | `getMethod()` / `getDeclaredMethod()` | 获取方法 |
| 5 | `invoke()` | 调用方法 |
| 6 | `getField()` / `getDeclaredField()` | 获取字段 |
| 7 | `get()` / `set()` | 访问字段 |

通过掌握 AOP、注解和反射这三个核心技术，你可以构建更加灵活、可扩展的 Java 应用程序。