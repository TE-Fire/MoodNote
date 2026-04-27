# Skill: 创建子模块结构

## 触发条件
当 Maven 父工程创建完成后，需要创建各个子模块的目录结构和基础 POM 文件。

## 前置依赖
- Skill-001: 创建Maven父工程

## 执行规范

### 文件位置
- 子模块目录：`d:\moodnote-parent\{模块名}`
- 每个子模块包含独立的 `pom.xml` 文件

### 命名规范
- 子模块名称：
  - 公共模块：`moodnote-common`
  - 实体模块：`moodnote-pojo`
  - 数据访问模块：`moodnote-mapper`
  - 业务逻辑模块：`moodnote-service`
  - 启动模块：`moodnote-server`

### 代码规范
- 每个子模块的 POM 文件继承父工程
- 正确配置模块间的依赖关系
- 保持模块职责清晰，避免循环依赖

### 依赖引入
- 子模块间的依赖关系：
  - moodnote-server 依赖 moodnote-service
  - moodnote-service 依赖 moodnote-mapper
  - moodnote-mapper 依赖 moodnote-pojo
  - 所有模块依赖 moodnote-common

## 代码模板

### 模板说明
为每个子模块创建基础目录结构和 POM 文件。

### 代码示例

#### 1. moodnote-common 模块
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>com.moodnote</groupId>
        <artifactId>moodnote-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>moodnote-common</artifactId>
    <name>moodnote-common</name>
    <description>MoodNote 公共模块</description>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>
    </dependencies>

</project>
```

#### 2. moodnote-pojo 模块
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>com.moodnote</groupId>
        <artifactId>moodnote-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>moodnote-pojo</artifactId>
    <name>moodnote-pojo</name>
    <description>MoodNote 实体与 DTO 模块</description>

    <dependencies>
        <dependency>
            <groupId>com.moodnote</groupId>
            <artifactId>moodnote-common</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
        </dependency>
    </dependencies>

</project>
```

#### 3. moodnote-mapper 模块
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>com.moodnote</groupId>
        <artifactId>moodnote-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>moodnote-mapper</artifactId>
    <name>moodnote-mapper</name>
    <description>MoodNote 数据访问模块</description>

    <dependencies>
        <dependency>
            <groupId>com.moodnote</groupId>
            <artifactId>moodnote-pojo</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>

</project>
```

#### 4. moodnote-service 模块
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>com.moodnote</groupId>
        <artifactId>moodnote-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>moodnote-service</artifactId>
    <name>moodnote-service</name>
    <description>MoodNote 业务逻辑模块</description>

    <dependencies>
        <dependency>
            <groupId>com.moodnote</groupId>
            <artifactId>moodnote-mapper</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct-processor</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>

</project>
```

#### 5. moodnote-server 模块
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>com.moodnote</groupId>
        <artifactId>moodnote-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>moodnote-server</artifactId>
    <name>moodnote-server</name>
    <description>MoodNote 启动与 Web 模块</description>

    <dependencies>
        <dependency>
            <groupId>com.moodnote</groupId>
            <artifactId>moodnote-service</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

## 验收标准
1. 所有子模块目录结构创建成功
2. 每个子模块的 POM 文件配置正确
3. 模块间依赖关系配置正确
4. 所有模块都继承自父工程
5. 目录结构符合 Maven 规范

## 关联 Skill
前置：Skill-001-创建Maven父工程

后置：Skill-003-Vue3项目初始化