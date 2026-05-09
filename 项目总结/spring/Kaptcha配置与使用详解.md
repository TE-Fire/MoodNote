# Kaptcha 配置与使用详解

## 一、概述

Kaptcha 是 Google 开源的高度可配置的验证码生成工具，能够生成各种样式的图形验证码，有效防止恶意注册、暴力破解等行为。

---

## 二、验证码处理流程详解（核心）

### 2.1 完整流程图

```
用户请求验证码
    ↓
后端生成验证码文本 (createText)
    ↓
生成验证码图片 (createImage)
    ↓
生成唯一 captchaKey (UUID)
    ↓
存储到 Redis: captchaKey → 验证码文本
    ↓
图片转为 Base64 编码
    ↓
返回给前端: {captchaKey, captchaImg}
    ↓
用户在表单输入验证码
    ↓
提交表单: 携带 captchaKey 和用户输入的 captcha
    ↓
后端验证: 用 captchaKey 从 Redis 取出真实值比对
    ↓
验证成功 → 删除 Redis 中的验证码 → 继续业务
```

### 2.2 核心代码解析（AuthServiceImpl）

```java
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private DefaultKaptcha defaultKaptcha;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public CaptchaVO getCaptcha() {
        // 步骤1：生成验证码文本（如 "4d4s"）
        String captchaText = defaultKaptcha.createText();

        // 步骤2：根据文本生成图片（BufferedImage）
        BufferedImage image = defaultKaptcha.createImage(captchaText);

        // 步骤3：生成唯一的验证码 key（如 "abc123def456"）
        String captchaKey = UUID.randomUUID().toString();

        // 步骤4：将验证码文本存储到 Redis，5分钟过期
        redisUtil.set(RedisKeyConstant.getCaptchaKey(captchaKey), 
                     captchaText, 
                     DataConstant.VERIFICATION_CODE_TIMEOUT, 
                     TimeUnit.MINUTES);

        // 步骤5：将图片转换为 Base64 编码字符串
        String captchaImg = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            byte[] bytes = baos.toByteArray();
            
            // Base64 编码 + 添加 Data URL 前缀
            captchaImg = "data:image/jpeg;base64," + 
                        Base64.getEncoder().encodeToString(bytes);
                        
            log.info("验证码图片转换成功");
        } catch (Exception e) {
            log.error("验证码图片转换失败", e);
            throw new BusinessException(ErrorCode.CODE_TO_IMAGE_ERROR, 
                                      MessageConstant.CODE_TO_IMAGE_ERROR);
        }

        // 步骤6：构建返回对象
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaKey(captchaKey);
        captchaVO.setCaptchaImg(captchaImg);
        
        return captchaVO;
    }

    @Override
    public boolean verifyCaptcha(String captchaKey, String inputCode) {
        // 1. 用 captchaKey 从 Redis 取出存储的验证码文本
        String storedCode = redisUtil.get(
            RedisKeyConstant.getCaptchaKey(captchaKey), 
            String.class
        );

        // 2. 检查是否过期
        if (storedCode == null) {
            throw new BusinessException(ErrorCode.CODE_EXPIRE, 
                                      MessageConstant.CODE_EXPIRE);
        }

        // 3. 比对用户输入和存储值
        if (storedCode.equals(inputCode)) {
            // 验证成功，删除 Redis 中的验证码（防止重复使用）
            redisUtil.delete(RedisKeyConstant.getCaptchaKey(captchaKey));
            return true;
        }

        log.warn("验证码错误，key: {}, 输入: {}, 存储: {}", 
                 captchaKey, inputCode, storedCode);
        
        return false;
    }
}
```

### 2.3 Base64 图片编码详解

#### 什么是 Base64？

Base64 是一种将二进制数据转换为文本字符串的编码方式，使用 64 个可打印字符来表示任意二进制数据。

#### 图片 → Base64 转换过程

```java
// 1. BufferedImage（内存中的图片对象）
BufferedImage image = defaultKaptcha.createImage(captchaText);

// 2. 写入 ByteArrayOutputStream（字节数组输出流）
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ImageIO.write(image, "jpg", baos);

// 3. 得到字节数组（二进制数据）
byte[] bytes = baos.toByteArray();

// 4. Base64 编码为字符串
String base64String = Base64.getEncoder().encodeToString(bytes);

// 5. 加上 Data URL 前缀（浏览器可识别）
String dataUrl = "data:image/jpeg;base64," + base64String;
```

#### Data URL 格式说明

```
data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD...
│    │       │       └── Base64 编码的图片数据
│    │       └────────── 编码方式 (base64)
│    └────────────────── 媒体类型 (image/jpeg)
└─────────────────────── 协议标识 (data:)
```

#### Base64 字符串示例

```
data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAASABQDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCdAAAAAAAAAAAAAAAAAAAAAB9/9k=
```

#### 前端如何使用 Base64 图片

```html
<!-- 直接赋值给 img 标签的 src 属性 -->
<img src="data:image/jpeg;base64,/9j/4AAQSkZJRg..." alt="验证码">
```

```javascript
// Vue 示例
const captchaImg = ref('');

// 获取验证码
const getCaptcha = async () => {
    const res = await axios.get('/api/auth/captcha');
    captchaImg.value = res.data.data.captchaImg; // 直接使用
};
```

### 2.4 captchaKey 和 captcha 的角色说明

| 字段 | 含义 | 生成位置 | 传输方向 | 作用 |
|------|------|----------|----------|------|
| **captchaKey** | 验证码的唯一标识（钥匙） | 后端生成 | 后端→前端→后端 | 用于从 Redis 查找对应的验证码 |
| **captcha** | 用户输入的验证码 | 用户输入 | 前端→后端 | 与 Redis 中存储的真实值比对 |

#### 类比理解

想象一个储物柜系统：
- **captchaKey** = 储物柜编号（如 "A123"）
- **captcha** = 用户输入的密码（如 "4d4s"）
- **Redis** = 储物柜

**流程：**
1. 你拿到一个储物柜编号（captchaKey）和一张写着密码的纸条（图片）
2. 你记住密码，扔掉纸条
3. 你用编号找到储物柜，输入密码，验证成功就打开了

---

## 三、两种配置方式详解

### 3.1 方式一：application.yml 配置方式

#### 优点
- 配置灵活，修改无需重新编译
- 适合多环境配置（dev/test/prod）
- 配置与代码分离

#### 缺点
- 需要额外的配置读取逻辑
- 不如 Java 配置直观

#### 配置示例

```yaml
# application.yml
kaptcha:
  # 边框设置
  border: "no"                              # 是否有边框：yes/no
  border-color: "105,179,90"               # 边框颜色：RGB
  
  # 图片大小
  image:
    width: "120"                           # 图片宽度
    height: "40"                           # 图片高度
    
  # 文本设置
  textproducer:
    char:
      length: "4"                          # 验证码长度
      string: "0123456789abcdefghijklmnopqrstuvwxyz"  # 字符范围
    font:
      color: "blue"                        # 字体颜色
      size: "40"                           # 字体大小
      names: "Arial,Courier,微软雅黑"      # 字体列表
    
  # 噪声（干扰线）
  noise:
    impl: "com.google.code.kaptcha.impl.DefaultNoise"
    color: "black"
    
  # 样式（水纹/鱼眼/阴影）
  obscurificator:
    impl: "com.google.code.kaptcha.impl.WaterRipple"
    
  # 背景
  background:
    impl: "com.google.code.kaptcha.impl.DefaultBackground"
    clear:
      from: "lightGray"                    # 背景渐变开始
      to: "white"                          # 背景渐变结束
```

#### 对应的 Java 配置类

```java
@Configuration
public class KaptchaConfig {
    
    @Bean
    public DefaultKaptcha producer() {
        Properties properties = new Properties();
        
        // 读取 application.yml 中的配置
        properties.setProperty("kaptcha.border", "no");
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        properties.setProperty("kaptcha.image.width", "120");
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        
        return defaultKaptcha;
    }
}
```

### 3.2 方式二：Java 配置类（@Bean 注册方式）⭐ 推荐

#### 优点
- 代码直观，一目了然
- 便于代码审查和维护
- 可以添加更多自定义逻辑
- 支持复杂的动态配置

#### 缺点
- 修改配置需要重新编译

#### 配置示例（本项目使用）

```java
@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha producer() {
        Properties properties = new Properties();
        
        // ==================== 基础配置 ====================
        properties.setProperty("kaptcha.border", "no");           // 无边框
        properties.setProperty("kaptcha.image.width", "125");     // 图片宽度 125px
        properties.setProperty("kaptcha.image.height", "45");     // 图片高度 45px
        
        // ==================== 文本配置 ====================
        properties.setProperty("kaptcha.textproducer.char.length", "4");                    // 4个字符
        properties.setProperty("kaptcha.textproducer.font.color", "blue");                   // 蓝色字体
        properties.setProperty("kaptcha.textproducer.font.size", "40");                     // 字体大小 40px
        properties.setProperty("kaptcha.textproducer.font.names", "Arial,Courier,微软雅黑"); // 字体列表
        properties.setProperty("kaptcha.textproducer.char.space", "2");                     // 字符间距
        
        // ==================== 噪声配置 ====================
        properties.setProperty("kaptcha.noise.impl", 
            "com.google.code.kaptcha.impl.DefaultNoise");  // 默认噪声（干扰线）
        properties.setProperty("kaptcha.noise.color", "black");  // 噪声颜色
        
        // ==================== 样式配置 ====================
        properties.setProperty("kaptcha.obscurificator.impl", 
            "com.google.code.kaptcha.impl.WaterRipple");  // 水纹样式
        // 可选: FishEyeGimpy（鱼眼）、ShadowGimpy（阴影）
        
        // ==================== 背景配置 ====================
        properties.setProperty("kaptcha.background.impl", 
            "com.google.code.kaptcha.impl.DefaultBackground");
        properties.setProperty("kaptcha.background.clear.from", "lightGray");
        properties.setProperty("kaptcha.background.clear.to", "white");
        
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        
        return defaultKaptcha;
    }
}
```

---

## 四、Maven 依赖配置

### 4.1 父 POM 依赖管理

```xml
<!-- pom.xml -->
<properties>
    <kaptcha.version>2.3.2</kaptcha.version>
</properties>

<dependencyManagement>
    <dependencies>
        <!-- Kaptcha 验证码 -->
        <dependency>
            <groupId>com.github.penggle</groupId>
            <artifactId>kaptcha</artifactId>
            <version>${kaptcha.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 4.2 子模块引入

```xml
<!-- moodnote-service/pom.xml -->
<dependencies>
    <dependency>
        <groupId>com.github.penggle</groupId>
        <artifactId>kaptcha</artifactId>
    </dependency>
</dependencies>
```

---

## 五、完整配置参数详解

### 5.1 基础配置

| 参数 | 说明 | 默认值 | 可选值 |
|------|------|--------|--------|
| kaptcha.border | 是否有边框 | yes | yes, no |
| kaptcha.border.color | 边框颜色 | black | r,g,b 或颜色名 |
| kaptcha.border.thickness | 边框粗细 | 1 | 正整数 |
| kaptcha.image.width | 图片宽度 | 200 | 像素值 |
| kaptcha.image.height | 图片高度 | 50 | 像素值 |

### 5.2 文本配置

| 参数 | 说明 | 默认值 |
|------|------|--------|
| kaptcha.textproducer.impl | 文本生成器 | DefaultTextCreator |
| kaptcha.textproducer.char.string | 字符范围 | abcde2345678gfynmnpwx |
| kaptcha.textproducer.char.length | 验证码长度 | 5 |
| kaptcha.textproducer.font.names | 字体 | Arial, Courier |
| kaptcha.textproducer.font.size | 字体大小 | 40px |
| kaptcha.textproducer.font.color | 字体颜色 | black |
| kaptcha.textproducer.char.space | 字符间距 | 2 |

### 5.3 噪声配置

| 参数 | 说明 | 默认值 |
|------|------|--------|
| kaptcha.noise.impl | 噪声实现类 | DefaultNoise |
| kaptcha.noise.color | 噪声颜色 | black |

### 5.4 样式配置

| 参数 | 说明 | 默认值 |
|------|------|--------|
| kaptcha.obscurificator.impl | 样式实现类 | WaterRipple |

**可选样式：**
- `WaterRipple`：水纹效果（推荐）
- `FishEyeGimpy`：鱼眼效果
- `ShadowGimpy`：阴影效果

### 5.5 背景配置

| 参数 | 说明 | 默认值 |
|------|------|--------|
| kaptcha.background.impl | 背景实现类 | DefaultBackground |
| kaptcha.background.clear.from | 背景渐变开始 | lightGray |
| kaptcha.background.clear.to | 背景渐变结束 | white |

---

## 六、核心 API 说明

### 6.1 DefaultKaptcha 主要方法

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `createText()` | 生成验证码文本 | String |
| `createImage(String text)` | 根据文本生成验证码图片 | BufferedImage |
| `setConfig(Config config)` | 设置配置 | void |

### 6.2 Config 配置类

通过 Properties 对象配置 Kaptcha 的各种参数。

```java
Properties properties = new Properties();
properties.setProperty("kaptcha.border", "no");
Config config = new Config(properties);
defaultKaptcha.setConfig(config);
```

---

## 七、基础使用方法

### 7.1 创建 VO 对象

```java
@Data
public class CaptchaVO {
    private String captchaKey;  // 验证码 key
    private String captchaImg;  // Base64 图片
}
```

### 7.2 实现 Controller 层

```java
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 获取图形验证码
     */
    @GetMapping("/captcha")
    public Result<CaptchaVO> getCaptcha() {
        log.info("获取图形验证码");
        return Result.success(authService.getCaptcha());
    }

    /**
     * 用户登录（需要验证码）
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        // 1. 验证图形验证码
        boolean valid = authService.verifyCaptcha(
            loginDTO.getCaptchaKey(), 
            loginDTO.getCaptcha()
        );
        
        if (!valid) {
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR, "验证码错误");
        }
        
        // 2. 继续登录逻辑...
        return Result.success(loginVO);
    }
}
```

### 7.3 DTO 示例

```java
@Data
public class LoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "验证码不能为空")
    private String captcha;        // 用户输入的验证码
    
    @NotBlank(message = "验证码key不能为空")
    private String captchaKey;     // 验证码 key
}
```

---

## 八、前端使用示例

### 8.1 Vue 3 + Axios 示例

```vue
<template>
    <div class="login-form">
        <div class="form-item">
            <label>用户名：</label>
            <input v-model="form.username" type="text">
        </div>
        
        <div class="form-item">
            <label>密码：</label>
            <input v-model="form.password" type="password">
        </div>
        
        <div class="form-item captcha-item">
            <label>验证码：</label>
            <input v-model="form.captcha" type="text" placeholder="请输入验证码">
            <img 
                :src="captchaImg" 
                alt="验证码" 
                @click="refreshCaptcha"
                class="captcha-img"
                title="点击刷新"
            >
        </div>
        
        <button @click="login">登录</button>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const form = ref({
    username: '',
    password: '',
    captcha: '',
    captchaKey: ''
});

const captchaImg = ref('');

// 刷新验证码
const refreshCaptcha = async () => {
    try {
        const res = await axios.get('/api/auth/captcha');
        captchaImg.value = res.data.data.captchaImg;
        form.value.captchaKey = res.data.data.captchaKey;
        form.value.captcha = '';  // 清空输入框
    } catch (error) {
        console.error('获取验证码失败', error);
    }
};

// 登录
const login = async () => {
    try {
        const res = await axios.post('/api/auth/login', form.value);
        console.log('登录成功', res.data);
    } catch (error) {
        console.error('登录失败', error);
    }
};

// 页面加载时获取验证码
onMounted(() => {
    refreshCaptcha();
});
</script>

<style scoped>
.captcha-item {
    display: flex;
    align-items: center;
    gap: 10px;
}

.captcha-img {
    cursor: pointer;
    border: 1px solid #ddd;
    border-radius: 4px;
    height: 45px;
}
</style>
```

---

## 九、安全建议

### 9.1 验证码过期时间

建议设置 3-5 分钟，太短影响用户体验，太长增加安全风险。

```java
redisUtil.set(key, code, 5, TimeUnit.MINUTES);
```

### 9.2 验证成功后立即删除

防止验证码被重复使用。

```java
if (storedCode.equals(inputCode)) {
    redisUtil.delete(key);  // 验证成功立即删除
    return true;
}
```

### 9.3 大小写不敏感比较（可选）

```java
if (storedCode.equalsIgnoreCase(inputCode)) {
    // 验证成功
}
```

### 9.4 限制获取频率

防止恶意刷验证码。

```java
// 限制同一 IP 每分钟最多获取 5 次
String limitKey = "captcha:limit:" + ip;
Long count = redisUtil.get(limitKey, Long.class);
if (count != null && count >= 5) {
    throw new BusinessException("验证码获取过于频繁");
}
redisUtil.set(limitKey, (count == null ? 1 : count + 1), 1, TimeUnit.MINUTES);
```

---

## 十、常见问题

### Q1：验证码图片乱码或显示为方框？

**A：** 检查服务器是否支持中文，可以尝试换成英文字体：

```java
properties.setProperty("kaptcha.textproducer.font.names", "Arial,Courier");
```

### Q2：验证码图片无法显示？

**A：** 检查以下几点：
1. Base64 格式是否正确
2. 是否添加了 `data:image/jpeg;base64,` 前缀
3. 图片格式是否支持（建议使用 jpg/png）

### Q3：如何添加更多干扰线？

**A：** 调整噪声实现类：

```java
properties.setProperty("kaptcha.noise.impl", 
    "com.google.code.kaptcha.impl.DefaultNoise");
```

### Q4：如何实现算术验证码（如 3+5=?）？

**A：** 需要自定义 TextProducer 实现类，生成数学表达式文本。

### Q5：验证码刷新不生效？

**A：** 检查是否有缓存问题，在前端添加时间戳：

```javascript
// 方式1：前端添加时间戳
const res = await axios.get('/api/auth/captcha?t=' + Date.now());

// 方式2：后端设置响应头
response.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
```

---

## 十一、总结

本项目采用 **Java 配置类方式** 配置 Kaptcha，使用 Redis 存储验证码，通过 Base64 格式返回图片，具有以下优势：

1. **代码直观**：配置清晰，便于维护
2. **存储安全**：Redis 分布式存储，支持水平扩展
3. **传输高效**：Base64 格式，无需额外请求
4. **使用简单**：接口标准化，前端集成方便

### 核心要点回顾

- **captchaKey**：验证码的唯一标识（钥匙）
- **captcha**：用户输入的验证码（密码）
- **Base64**：将二进制图片转为文本字符串的编码方式
- **Data URL**：`data:image/jpeg;base64,` 前缀让浏览器可直接识别

建议在生产环境中：
- 增加验证码获取频率限制
- 考虑添加验证码识别难度（如旋转、扭曲等）
- 定期更换验证码样式
