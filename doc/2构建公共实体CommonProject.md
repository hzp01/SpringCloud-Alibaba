[TOC]
## 基础模块构建步骤
### 1 在父工程上新建module，类型为maven
### 2 修改pom.xml
```
<!--增加jpa依赖、lombok组件、fastjson组件、数据库连接插件-->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.56</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.6</version>
        </dependency>
    </dependencies>
```
### 3 在src/main/java下创建cn.hzp.domain包，在该包下创建实体类User、Projduct、Order