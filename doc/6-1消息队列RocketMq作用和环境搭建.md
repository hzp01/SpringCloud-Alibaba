[TOC]
# 1 RocketMq消息队列作用
```
1 异步解耦：不需要即时同步，如用户注册后的短信邮件通知，通知消息异步处理
2 流量削峰：如秒杀业务的消息通知，通知消息异步处理
```
其它队列：activeMq、kafka略；

# 2 在vm的centos下rmq简单环境搭建
要求：linux系统x64，jdk8+
## 2.1 下载安装包`http://rocketmq.apache.org/release_notes/release-notes-4.4.0/`

## 2.2 安装，上传到/usr/local/src中，
- 1 进入压缩文件目录：cd /usr/lcoal/src
- 2 解压文件：unzip xxx.zip
    - 如果没有解压命令，请先安装：yum install zip unzip
- 3 移动解压的文件夹：mv rocketmq-xxxx /usr/local/rocketmq

## 2.3 启动nameServer和broker
- 1 进入启动目录：`cd /usr/local/rocketmq/bin`
- 2 启动nameServer: `nohup ./mqnamesrv &`
- 3 测试:`tail -f ~/logs/rokectmqlogs/namesrv.log`
    - 显示`The Name Server boot success`表示成功
- 4 测试端口：netstat -an | grep 9876 
    - 如果没有netstat命令，请先安装:yum install net-tools
    - 端口存在，表示成功

## 2.4 启动broker
- 1 编辑bin/runbroker.sh，bin/runserver.sh,修改参数JAVA_OPT的值 -Xms256m -Xmx256m -Xmn128m
- 2 启动`nohup ./mqbroker -n localhost:9876 &`
- 3 测试`tail -f ~/logs/rocketmqlogs/broker.log`
    - 显示boot success表示成功
## 2.4 测试
- 1 开启两个窗口，一个发一个收，进入bin目录
- 2发送命令：
```
export NAMESRV_ADDR=localhost:9876 
./tools.sh org.apache.rocketmq.example.quickstart.Producer
```

- 3接收命令：
```
export NAMESRV_ADDR=localhost:9876 
./tools.sh org.apache.rocketmq.example.quickstart.Consumer
```
- 4在接受后一直监听状态，再次执行发送就可以发现接收窗口第二次接收

## 2.5 关闭mq
```
./mqshutdown broker 
./mqshutdown namesrv
```

# 3 rocketMq控制台安装
- 1 下载`https://github.com/apache/rocketmq-externals/releases`,选择console下载
- 2 修改启动类rocketmq-console\src\main\resources\application.properties
```
server.port=7777
rocketmq.config.namesrvAddr=192.168.149.11:9876
```
- 3 编译jar包，执行`mvn clean package -Dmaven.test.skip=true`
- 4 上传到vm的/usr/local/src下，启动`java -jar r-xx.jar`
- 5 测试，浏览器访问`http://192.168.149.11:7777`