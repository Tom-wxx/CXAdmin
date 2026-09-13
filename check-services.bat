@echo off
chcp 65001 >nul
echo ========================================
echo   后台管理系统 - 环境检查工具
echo ========================================
echo.

echo [1/4] 检查 MySQL 服务...
sc query mysql >nul 2>&1
if %errorlevel% == 0 (
    echo ✓ MySQL 服务已安装
    sc query mysql | findstr "RUNNING" >nul 2>&1
    if %errorlevel% == 0 (
        echo ✓ MySQL 服务正在运行
    ) else (
        echo ✗ MySQL 服务未运行
        echo   请运行: net start mysql
    )
) else (
    echo ✗ MySQL 服务未安装或服务名不是 'mysql'
    echo   请检查 MySQL 是否已安装
)
echo.

echo [2/4] 检查 MySQL 连接...
mysql -u root -proot -e "SELECT 1;" >nul 2>&1
if %errorlevel% == 0 (
    echo ✓ MySQL 连接成功（密码: root）
) else (
    echo ✗ MySQL 连接失败
    echo   请检查用户名和密码是否正确
    echo   或手动测试: mysql -u root -p
)
echo.

echo [3/4] 检查 Redis 服务...
sc query Redis >nul 2>&1
if %errorlevel% == 0 (
    echo ✓ Redis 服务已安装
    sc query Redis | findstr "RUNNING" >nul 2>&1
    if %errorlevel% == 0 (
        echo ✓ Redis 服务正在运行
    ) else (
        echo ✗ Redis 服务未运行
        echo   请运行: net start Redis
    )
) else (
    echo ⚠ Redis 服务未安装或服务名不是 'Redis'
    echo   如果使用独立 Redis，请手动启动: redis-server
)
echo.

echo [4/4] 检查数据库...
mysql -u root -proot -e "USE admin_system; SELECT COUNT(*) FROM flyway_schema_history;" >nul 2>&1
if %errorlevel% == 0 (
    echo ✓ 数据库 admin_system 已由 Flyway 初始化
    mysql -u root -proot -e "SELECT CONCAT('  当前版本: ', MAX(version)) FROM admin_system.flyway_schema_history WHERE success=1;" -N 2>nul
) else (
    echo ⚠ 数据库尚未初始化 —— 这是正常的，无需手工建库
    echo   直接启动后端即可：Flyway 会自动建库建表并灌入种子数据
    echo   cd backend ^&^& mvn -pl admin-boot -am spring-boot:run
)
echo.

echo ========================================
echo   检查完成！
echo ========================================
echo.
echo 如果所有检查都通过，可以启动应用：
echo   1. 启动后端: cd backend ^&^& mvn spring-boot:run
echo   2. 启动前端: cd frontend ^&^& npm run dev
echo.

pause
