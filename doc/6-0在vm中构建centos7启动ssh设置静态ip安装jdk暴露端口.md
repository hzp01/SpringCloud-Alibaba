[TOC]
# 1 vm中安装centos
参考网址：`https://blog.csdn.net/vevenlcf/article/details/78297008`
- 1 选择稍后安装
- 2 自定义硬件中选择iso
- 3 开启，选择第一个install，选English，选硬盘，设置root密码要求5位，重启

# 2 开启网络连接，设置静态ip
默认没有启动网络连接，需要手动开启
- 1 vi /etc/sysconfig/network-scripts/ifcfg-enoxxxxxxx
- 2 修改ONBOOT=yes，设置静态ip
```
# 开启网络连接，第一步已经实现
ONBOOT=yes
# 修改dhcp为static，动态分配改为静态
BOOTPROTO=static
# 新增静态ip，我dos中vm的ip4为192.168.149.1
IPADDR=192.168.149.11   
GATEWAY=192.168.149.2
NETMASK=255.255.255.0
DNS1=114.114.114.114
DNS2=8.8.8.8
```
- 3 保存退出 :wq!
- 4 重启网络 service network restart
- 5 查看网络地址 ip addr
- 6 测试 ping www.baidu.com

# 3 开启ssh
- 1 vi /etc/ssh/sshd_config
- 2 取消端口、地址、开启远程、密码连接共5行注释
```
Port
ListenAddress
ListenAddress
PermitRootLogin
PasswordAuthentication
```
- 3 重启sshd服务：service sshd start
- 4 设置开机启动：systemctl enable ssh
- 5 测试ping，在主机dos窗口ping虚拟机ip，在虚拟机vm中ping主机ip
注意：这里主机网络配置中心不能禁用vm，会导致vm可以ping通主机，主机不通vm

# 4 安装jdk8
- 1 安装`sudo yum install java-1.8.0-openjdk-devel`，安装后的目录为`/usr/lib/jvm/java-1.8.0`
- 2 配置环境变量: `vi /etc/profile`
```
export JAVA_HOME=/usr/lib/jvm/java-1.8.0                
export JRE_HOME=${JAVA_HOME}/jre                        
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib     
export PATH=${JAVA_HOME}/bin:$PATH
```
- 3 启动生效：`source /etc/profile`
- 4 测试 `java -version`

# 5 暴漏服务端口，关闭防火墙
- 1 查看防火墙状态`firewall-cmd --state`,确保running
- 2 开启80端口`firewall-cmd --zone=public --add-port=80/tcp --permanent`,其中zone表示作用域，permanent表示永久生效，否则重启失效
- 3 重启防火墙`systemctl restart firewalld.service`
- 4 测试
    - 主机测试vm`telnet 192.168.149.11 9876`
    - 查询暴漏端口，略
注：这里直接关闭防火墙：`systemctl disable firewalld.service` 