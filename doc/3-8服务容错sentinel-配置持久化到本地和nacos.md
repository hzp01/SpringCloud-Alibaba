[TOC]
## 1 sentinel流控规则持久化到nacos示例
参考网址1：`http://www.itmuch.com/spring-cloud-alibaba/sentinel-rules-persistence-push-mode-using-nacos/`
参考网址2：`https://www.sonake.com/2019/12/16/Sentinel-Nacos%E5%AE%9E%E7%8E%B0%E8%A7%84%E5%88%99%E6%8C%81%E4%B9%85%E5%8C%96/`
### 1.1 order服务的pom文件加入依赖
```
        <!--添加sentinel持久化到nacos的依赖-->
        <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentinel-datasource-nacos</artifactId>
        </dependency>
```
### 1.2 order服务的yml文件添加配置
```
spring:
  cloud:
    sentinel:
      datasource:
        # 名称随意
        flow:
          nacos:
            server-addr: localhost:8848
            dataId: ${spring.application.name}-flow-rules
            groupId: SENTINEL_GROUP
            # 规则类型，取值见：
            # org.springframework.cloud.alibaba.sentinel.datasource.RuleType
            rule-type: flow
        degrade:
          nacos:
            server-addr: localhost:8848
            dataId: ${spring.application.name}-degrade-rules
            groupId: SENTINEL_GROUP
            rule-type: degrade
        system:
          nacos:
            server-addr: localhost:8848
            dataId: ${spring.application.name}-system-rules
            groupId: SENTINEL_GROUP
            rule-type: system
        authority:
          nacos:
            server-addr: localhost:8848
            dataId: ${spring.application.name}-authority-rules
            groupId: SENTINEL_GROUP
            rule-type: authority
        param-flow:
          nacos:
            server-addr: localhost:8848
            dataId: ${spring.application.name}-param-flow-rules
            groupId: SENTINEL_GROUP
            rule-type: param-flow
```
### 1.3 整理nacos
#### 1.3.1 数据库mysql导入nacos的配置文件
找到/nacos/conf下面的nacos-mysql.sql文件,在mysql导入运行，建立nacos对应的库表结构
#### 1.3.2 修改文件`nacos-server-1.1.4\nacos\conf\application.properties`
```
# db mysql
spring.datasource.platform=mysql
db.num=1
db.url.0=jdbc:mysql://localhost:3306/nacos?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true
db.user=root
db.password=root
```

### 1.4 改造sentinel-dashboard
#### 1.4.1 修改pom文件,删除scope属性
```
<!-- for Nacos rule publisher sample -->
  <dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-datasource-nacos</artifactId>
    <scope>test</scope>
  </dependency>
```
####　1.4.2 复制文件
复制目录`sentinel-dashboard/src/test/java/com/alibaba/csp/sentinel/dashboard/rule/nacos`
到目录`sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/nacos`

####　1.4.3 修改文件`com.alibaba.csp.sentinel.dashboard.controller.v2.FlowControllerV2`
将下面2个注解中的`flowRuleDefault`修改为`flowRuleNacos`
```
@Autowired
@Qualifier("flowRuleDefaultProvider")
private DynamicRuleProvider<List<FlowRuleEntity>> ruleProvider;
@Autowired
@Qualifier("flowRuleDefaultPublisher")
private DynamicRulePublisher<List<FlowRuleEntity>> rulePublisher;
```
#### 1.4.4 修改properties文件，增加nacos配置，修改默认启动端口
```
#配置nacos服务的地址
nacos.url=127.0.0.1:8848
#修改服务启动端口
server.port=8858
```

#### 1.4.5 修改文件`src\main\java\com\alibaba\csp\sentinel\dashboard\rule\nacos\NacosConfig.java`
将nacos的地址更新，修改前
```
    @Bean
    public ConfigService nacosConfigService() throws Exception {
        return ConfigFactory.createConfigService("localhost");
    }
```
修改后
```
    @Value("${nacos.url}")
    private String serverAddr;

    @Bean
    public ConfigService nacosConfigService() throws Exception {
        return ConfigFactory.createConfigService(serverAddr);
    }
```

#### 1.4.6 修改文件`sentinel-dashboard/src/main/webapp/resources/app/scripts/directives/sidebar/sidebar.html`
放开下面注释
```
<!--<li ui-sref-active="active">-->
  <!--<a ui-sref="dashboard.flow({app: entry.app})">-->
    <!--<i class="glyphicon glyphicon-filter"></i>&nbsp;&nbsp;流控规则 V1</a>-->
<!--</li>-->
```

#### 1.4.7 编译打包`mvn clean package -Dmaven.test.skip=true`
在target目录找到sentinel-dashboard.jar启动控制台进行测试

注：测试要用“流控规则V1”配置,tools文件夹下有编译好的jar包可以直接测试


## 2 sentinel持久化到本地
参考网址`https://www.cnblogs.com/fx-blog/p/11726189.html`
### 2.1 order服务pom文件引入sentinel持久依赖
```
        <!--添加sentinel持久化到本地的依赖-->
        <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentinel-datasource-extension</artifactId>
        </dependency>
```
### 2.2 新增持久化到本地的类
```
package cn.hzp.config;

import com.alibaba.csp.sentinel.command.handler.ModifyParamFlowRulesCommandHandler;
import com.alibaba.csp.sentinel.datasource.*;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * PULL规则持久化
 */
public class SentinelPerFile implements InitFunc {
    @Override
    public void init() throws Exception {
        String ruleDir = System.getProperty("user.dir").substring(0, 2) + "/sentinel/rules";
        String flowRulePath = ruleDir + "/flow-rule.json";
        String degradeRulePath = ruleDir + "/degrade-rule.json";
        String systemRulePath = ruleDir + "/system-rule.json";
        String authorityRulePath = ruleDir + "/authority-rule.json";
        String paramFlowRulePath = ruleDir + "/param-flow-rule.json";

        this.mkdirIfNotExits(ruleDir);
        this.createFileIfNotExits(flowRulePath);
        this.createFileIfNotExits(degradeRulePath);
        this.createFileIfNotExits(systemRulePath);
        this.createFileIfNotExits(authorityRulePath);
        this.createFileIfNotExits(paramFlowRulePath);

        // 注册一个可读数据源，用来定时读取本地的json文件，更新到规则缓存中
        // 流控规则
        ReadableDataSource<String, List<FlowRule>> flowRuleRDS =
                new FileRefreshableDataSource<>(flowRulePath, flowRuleListParser);
        // 将可读数据源注册至FlowRuleManager
        // 这样当规则文件发生变化时，就会更新规则到内存
        FlowRuleManager.register2Property(flowRuleRDS.getProperty());
        WritableDataSource<List<FlowRule>> flowRuleWDS = new FileWritableDataSource<>(
                flowRulePath,
                this::encodeJson
        );
        // 将可写数据源注册至transport模块的WritableDataSourceRegistry中
        // 这样收到控制台推送的规则时，Sentinel会先更新到内存，然后将规则写入到文件中
        WritableDataSourceRegistry.registerFlowDataSource(flowRuleWDS);

        // 降级规则
        ReadableDataSource<String, List<DegradeRule>> degradeRuleRDS = new FileRefreshableDataSource<>(
                degradeRulePath,
                degradeRuleListParser
        );
        DegradeRuleManager.register2Property(degradeRuleRDS.getProperty());
        WritableDataSource<List<DegradeRule>> degradeRuleWDS = new FileWritableDataSource<>(
                degradeRulePath,
                this::encodeJson
        );
        WritableDataSourceRegistry.registerDegradeDataSource(degradeRuleWDS);

        // 系统规则
        ReadableDataSource<String, List<SystemRule>> systemRuleRDS = new FileRefreshableDataSource<>(
                systemRulePath,
                systemRuleListParser
        );
        SystemRuleManager.register2Property(systemRuleRDS.getProperty());
        WritableDataSource<List<SystemRule>> systemRuleWDS = new FileWritableDataSource<>(
                systemRulePath,
                this::encodeJson
        );
        WritableDataSourceRegistry.registerSystemDataSource(systemRuleWDS);

        // 授权规则
        ReadableDataSource<String, List<AuthorityRule>> authorityRuleRDS = new FileRefreshableDataSource<>(
                authorityRulePath,
                authorityRuleListParser
        );
        AuthorityRuleManager.register2Property(authorityRuleRDS.getProperty());
        WritableDataSource<List<AuthorityRule>> authorityRuleWDS = new FileWritableDataSource<>(
                authorityRulePath,
                this::encodeJson
        );
        WritableDataSourceRegistry.registerAuthorityDataSource(authorityRuleWDS);

        // 热点参数规则
        ReadableDataSource<String, List<ParamFlowRule>> paramFlowRuleRDS = new FileRefreshableDataSource<>(
                paramFlowRulePath,
                paramFlowRuleListParser
        );
        ParamFlowRuleManager.register2Property(paramFlowRuleRDS.getProperty());
        WritableDataSource<List<ParamFlowRule>> paramFlowRuleWDS = new FileWritableDataSource<>(
                paramFlowRulePath,
                this::encodeJson
        );
        ModifyParamFlowRulesCommandHandler.setWritableDataSource(paramFlowRuleWDS);
    }

    private Converter<String, List<FlowRule>> flowRuleListParser = source -> JSON.parseObject(
            source,
            new TypeReference<List<FlowRule>>() {
            }
    );
    private Converter<String, List<DegradeRule>> degradeRuleListParser = source -> JSON.parseObject(
            source,
            new TypeReference<List<DegradeRule>>() {
            }
    );
    private Converter<String, List<SystemRule>> systemRuleListParser = source -> JSON.parseObject(
            source,
            new TypeReference<List<SystemRule>>() {
            }
    );

    private Converter<String, List<AuthorityRule>> authorityRuleListParser = source -> JSON.parseObject(
            source,
            new TypeReference<List<AuthorityRule>>() {
            }
    );

    private Converter<String, List<ParamFlowRule>> paramFlowRuleListParser = source -> JSON.parseObject(
            source,
            new TypeReference<List<ParamFlowRule>>() {
            }
    );

    private void mkdirIfNotExits(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private void createFileIfNotExits(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    private <T> String encodeJson(T t) {
        return JSON.toJSONString(t);
    }
}
```

### 2.3 标明持久化类的来源
在项目的 resources/META-INF/services 目录下创建文件，名为 com.alibaba.csp.sentinel.init.InitFunc ，内容为：
上面FileDataSourceInit的包名类名全路径，这里为`cn.hzp.config.SentinelPerFile`

### 2.4 测试验证
- 1 启动order服务和sentinel服务端
- 2 浏览器请求`http://localhost:8091/sentinel/highConcurrency?servName=app`
- 3 在sentinel控制台增加各种规则，查看本地文件是否生成，由于该项目在h盘下，可以在目录下`H:\sentinel\rules`找到5个json文件，表示成功