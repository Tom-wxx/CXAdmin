@echo off
chcp 65001 >nul
echo ========================================
echo     后台管理系统 - 环境检查工具
echo ========================================
echo.

echo [1/6] 检查 Java...
java -version 2>&1 | findstr "version"
if %errorlevel% equ 0 (
    echo ✅ Java 已安装
) else (
    echo ❌ Java 未安装
)
echo.

echo [2/6] 检查 Maven...
mvn -version 2>nul
if %errorlevel% equ 0 (
    echo ✅ Maven 已安装（命令行可用）
) else (
    echo ⚠️  Maven 命令未找到（可能只在 IDEA 中配置）
    echo    建议：在 IDEA 中启动后端
)
echo.

echo [3/6] 检查 MySQL 服务...
netstat -ano | findstr ":3306" >nul
if %errorlevel% equ 0 (
    echo ✅ MySQL 正在运行（端口 3306）
) else (
    echo ❌ MySQL 未运行
    echo    启动命令：net start MySQL80
)
echo.

echo [4/6] 检查 Redis 服务...
netstat -ano | findstr ":6379" >nul
if %errorlevel% equ 0 (
    echo ✅ Redis 正在运行（端口 6379）
) else (
    echo ❌ Redis 未运行
    echo    启动命令：redis-server.exe
)
echo.

echo [5/6] 检查后端端口（8080）...
netstat -ano | findstr ":8080" >nul
if %errorlevel% equ 0 (
    echo ⚠️  端口 8080 已被占用
    echo    后端可能正在运行，或需要关闭占用进程
) else (
    echo ✅ 端口 8080 可用
)
echo.

echo [6/6] 检查前端端口（8082）...
netstat -ano | findstr ":8082" >nul
if %errorlevel% equ 0 (
    echo ✅ 前端正在运行（端口 8082）
) else (
    echo ⚠️  前端未运行
    echo    启动命令：cd frontend && npm run dev
)
echo.

echo ========================================
echo              检查完成
echo ========================================
echo.
echo 下一步操作：
echo 1. 确保 MySQL 和 Redis 都在运行
echo 2. 在 IDEA 中打开 backend 项目
echo 3. 运行 AdminSystemApplication.java
echo 4. 访问 http://localhost:8082 测试系统
echo.
echo 详细启动指南请查看：START_BACKEND_IN_IDEA.md
echo 问题排查请查看：BACKEND_TROUBLESHOOTING.md
echo.
pause
