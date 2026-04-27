# Skill: 创建Maven父工程

## 触发条件
当需要初始化 MoodNote 项目时，首先创建 Maven 父工程作为项目的基础结构。

## 前置依赖
- 无

## 执行规范

### 文件位置
- 项目根目录：`d:\moodnote-parent`

### 命名规范
- 父工程名称：`moodnote-parent`
- POM 文件：`pom.xml`

### 代码规范
- 使用 Maven 3.9+ 版本
- 继承 Spring Boot 3.2.x 父 POM
- 配置正确的 groupId、artifactId、version
- 聚合所有子模块

### 依赖引入
- Spring Boot 3.2.x 父 POM
- Maven 插件管理

## 代码模板

### 模板说明
创建 Maven 父工程的 POM 文件，配置基础依赖和模块聚合。

### 代码示例
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>

    <groupId>com.moodnote</groupId>
    <artifactId>moodnote-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <name>moodnote-parent</name>
    <description>MoodNote 项目父工程</description>

    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>${java.version}</maven.compiler.source>
        <maven.compiler.target>${java.version}</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <mybatis-plus.version>3.5.5</mybatis-plus.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
        <lombok.version>1.18.30</lombok.version>
        <knife4j.version>4.4.0</knife4j.version>
        <hutool.version>5.8.25</hutool.version>
    </properties>

    <modules>
        <module>moodnote-common</module>
        <module>moodnote-pojo</module>
        <module>moodnote-mapper</module>
        <module>moodnote-service</module>
        <module>moodnote-server</module>
    </modules>

    <dependencyManagement>
        <dependencies>
            <!-- Spring Boot 核心依赖 -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-test</artifactId>
                <scope>test</scope>
            </dependency>

            <!-- MyBatis-Plus -->
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-boot-starter</artifactId>
                <version>${mybatis-plus.version}</version>
            </dependency>

            <!-- MySQL 驱动 -->
            <dependency>
                <groupId>com.mysql</groupId>
                <artifactId>mysql-connector-j</artifactId>
                <scope>runtime</scope>
            </dependency>

            <!-- MapStruct -->
            <dependency>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct</artifactId>
                <version>${mapstruct.version}</version>
            </dependency>
            <dependency>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>${mapstruct.version}</version>
                <scope>provided</scope>
            </dependency>

            <!-- Lombok -->
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
                <scope>provided</scope>
            </dependency>

            <!-- Knife4j (Swagger 增强) -->
            <dependency>
                <groupId>com.github.xiaoymin</groupId>
                <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
                <version>${knife4j.version}</version>
            </dependency>

            <!-- Hutool 工具类库 -->
            <dependency>
                <groupId>cn.hutool</groupId>
                <artifactId>hutool-all</artifactId>
                <version>${hutool.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

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
1. Maven 父工程 POM 文件创建成功
2. 正确配置了 Spring Boot 3.2.x 父依赖
3. 聚合了所有子模块
4. 配置了正确的 Java 版本（21）
5. 依赖管理部分包含了所有必要的依赖

## 关联 Skill
前置：无

后置：Skill-002-创建子模块结构