# 部署文档

## 生产环境部署指南

### 一、服务器要求

#### 硬件配置（最低要求）
- CPU：2核
- 内存：4GB
- 硬盘：50GB

#### 软件环境
- 操作系统：Linux (CentOS 7+ / Ubuntu 18.04+)
- JDK：17+
- MySQL：8.0+
- Redis：5.0+
- Nginx：1.18+

### 二、后端部署

#### 1. 安装JDK
```bash
# 安装OpenJDK 17
yum install java-17-openjdk java-17-openjdk-devel

# 验证安装
java -version
```

#### 2. 安装MySQL
```bash
# 下载MySQL YUM源
wget https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm

# 安装MySQL YUM源
rpm -ivh mysql80-community-release-el7-3.noarch.rpm

# 安装MySQL
yum install mysql-server

# 启动MySQL
systemctl start mysqld
systemctl enable mysqld

# 获取临时密码
grep 'temporary password' /var/log/mysqld.log

# 登录并修改密码
mysql -u root -p
ALTER USER 'root'@'localhost' IDENTIFIED BY 'YourNewPassword123!';
```

#### 3. 创建数据库
```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
source /path/to/database/schema.sql
source /path/to/database/init-data.sql
```

#### 4. 安装Redis
```bash
# 安装Redis
yum install redis

# 启动Redis
systemctl start redis
systemctl enable redis

# 设置Redis密码（可选）
vi /etc/redis.conf
# 取消注释并设置: requirepass your_password

# 重启Redis
systemctl restart redis
```

#### 5. 打包后端项目
```bash
# 在本地开发环境打包
cd backend
mvn clean package -DskipTests

# 打包完成后，在 target 目录下会生成 admin-system-1.0.0.jar
```

#### 6. 上传并运行
```bash
# 创建应用目录
mkdir -p /opt/admin-system
cd /opt/admin-system

# 上传jar包到服务器
# 使用scp或其他工具上传 admin-system-1.0.0.jar

# 创建启动脚本
vi start.sh
```

**start.sh 内容：**
```bash
#!/bin/bash
nohup java -jar -Xms512m -Xmx1024m admin-system-1.0.0.jar --spring.profiles.active=prod > logs/app.log 2>&1 &
echo $! > app.pid
```

```bash
# 赋予执行权限
chmod +x start.sh

# 启动应用
./start.sh

# 查看日志
tail -f logs/app.log
```

#### 7. 配置为系统服务（推荐）
```bash
# 创建systemd服务文件
vi /etc/systemd/system/admin-system.service
```

**admin-system.service 内容：**
```ini
[Unit]
Description=Admin System Service
After=syslog.target network.target

[Service]
Type=simple
User=root
ExecStart=/usr/bin/java -jar -Xms512m -Xmx1024m /opt/admin-system/admin-system-1.0.0.jar --spring.profiles.active=prod
ExecStop=/bin/kill -15 $MAINPID
Restart=on-failure
RestartSec=10s

[Install]
WantedBy=multi-user.target
```

```bash
# 重新加载systemd
systemctl daemon-reload

# 启动服务
systemctl start admin-system

# 设置开机自启
systemctl enable admin-system

# 查看状态
systemctl status admin-system
```

### 三、前端部署

#### 1. 安装Nginx
```bash
# 安装Nginx
yum install nginx

# 启动Nginx
systemctl start nginx
systemctl enable nginx
```

#### 2. 打包前端项目
```bash
# 在本地开发环境打包
cd frontend
npm run build

# 打包完成后，在 dist 目录下会生成静态文件
```

#### 3. 部署静态文件
```bash
# 创建网站目录
mkdir -p /var/www/admin-system

# 上传dist目录下的所有文件到 /var/www/admin-system
# 使用scp或其他工具上传
```

#### 4. 配置Nginx
```bash
# 编辑Nginx配置
vi /etc/nginx/conf.d/admin-system.conf
```

**admin-system.conf 内容：**
```nginx
server {
    listen 80;
    server_name your-domain.com;  # 修改为你的域名

    # 前端静态文件
    location / {
        root /var/www/admin-system;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # 后端API代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 300;
        proxy_send_timeout 300;
        proxy_read_timeout 300;
    }

    # 日志配置
    access_log /var/log/nginx/admin-system-access.log;
    error_log /var/log/nginx/admin-system-error.log;
}
```

```bash
# 测试Nginx配置
nginx -t

# 重新加载Nginx
systemctl reload nginx
```

#### 5. 配置HTTPS（可选但推荐）
```bash
# 安装certbot
yum install certbot python2-certbot-nginx

# 获取SSL证书
certbot --nginx -d your-domain.com

# 自动续期
certbot renew --dry-run
```

### 四、防火墙配置
```bash
# 开放端口
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --permanent --add-port=443/tcp
firewall-cmd --permanent --add-port=8080/tcp  # 如果需要直接访问后端
firewall-cmd --reload
```

### 五、性能优化

#### 1. MySQL优化
```bash
# 编辑MySQL配置
vi /etc/my.cnf
```

**添加以下配置：**
```ini
[mysqld]
max_connections = 500
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
innodb_flush_log_at_trx_commit = 2
```

```bash
# 重启MySQL
systemctl restart mysqld
```

#### 2. Redis优化
```bash
# 编辑Redis配置
vi /etc/redis.conf
```

**优化配置：**
```ini
maxmemory 512mb
maxmemory-policy allkeys-lru
save ""  # 禁用RDB持久化（根据需要）
```

```bash
# 重启Redis
systemctl restart redis
```

#### 3. JVM优化
```bash
# 根据服务器内存调整JVM参数
# 4GB内存服务器推荐配置：
-Xms1024m -Xmx2048m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m
```

### 六、监控和日志

#### 1. 应用日志
```bash
# 查看应用日志
tail -f /opt/admin-system/logs/admin-system.log

# 查看Nginx访问日志
tail -f /var/log/nginx/admin-system-access.log

# 查看Nginx错误日志
tail -f /var/log/nginx/admin-system-error.log
```

#### 2. 日志轮转
```bash
# 创建日志轮转配置
vi /etc/logrotate.d/admin-system
```

**内容：**
```
/opt/admin-system/logs/*.log {
    daily
    rotate 30
    missingok
    notifempty
    compress
    delaycompress
    copytruncate
}
```

### 七、备份策略

#### 1. 数据库备份
```bash
# 创建备份脚本
vi /opt/backup/mysql-backup.sh
```

**mysql-backup.sh：**
```bash
#!/bin/bash
BACKUP_DIR="/opt/backup/mysql"
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p $BACKUP_DIR

mysqldump -u root -p'YourPassword' admin_system > $BACKUP_DIR/admin_system_$DATE.sql
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete
```

```bash
# 设置定时任务
crontab -e
# 每天凌晨2点备份
0 2 * * * /opt/backup/mysql-backup.sh
```

#### 2. 应用备份
```bash
# 备份应用和配置文件
tar -czf admin-system-backup-$(date +%Y%m%d).tar.gz /opt/admin-system
```

### 八、常见问题排查

#### 1. 后端无法启动
```bash
# 查看端口占用
netstat -tunlp | grep 8080

# 查看应用日志
tail -n 100 logs/admin-system.log

# 检查Java进程
ps aux | grep java
```

#### 2. 数据库连接失败
```bash
# 测试MySQL连接
mysql -u root -p -h localhost

# 检查防火墙
systemctl status firewalld
```

#### 3. 前端页面无法访问
```bash
# 检查Nginx状态
systemctl status nginx

# 测试Nginx配置
nginx -t

# 查看Nginx错误日志
tail -f /var/log/nginx/error.log
```

### 九、安全加固

#### 1. 修改默认密码
- 修改数据库root密码
- 修改Redis密码
- 修改应用管理员密码

#### 2. 配置防火墙
```bash
# 只开放必要端口
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --permanent --add-port=443/tcp
firewall-cmd --reload
```

#### 3. 定期更新
```bash
# 定期更新系统和软件包
yum update
```

### 十、升级部署

#### 1. 备份当前版本
```bash
# 备份数据库
mysqldump -u root -p admin_system > backup.sql

# 备份应用
cp admin-system-1.0.0.jar admin-system-1.0.0.jar.bak
```

#### 2. 部署新版本
```bash
# 停止服务
systemctl stop admin-system

# 上传新版本jar包
# 替换旧版本

# 执行数据库升级脚本（如果有）
mysql -u root -p admin_system < upgrade.sql

# 启动服务
systemctl start admin-system

# 查看日志确认启动成功
tail -f logs/admin-system.log
```

#### 3. 回滚（如果需要）
```bash
# 停止服务
systemctl stop admin-system

# 恢复旧版本
mv admin-system-1.0.0.jar.bak admin-system-1.0.0.jar

# 恢复数据库
mysql -u root -p admin_system < backup.sql

# 启动服务
systemctl start admin-system
```

---

**注意事项：**
1. 生产环境部署前请做好充分测试
2. 定期备份数据库和重要配置文件
3. 监控系统运行状态，及时处理异常
4. 保持系统和依赖软件的及时更新
