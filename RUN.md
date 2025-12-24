如何在本地运行项目

先决条件
- JDK 17 已安装并设置 `JAVA_HOME`。
- 如果要使用 MySQL (`dev` profile)，请确保 MySQL 已启动并已创建数据库。

运行单元测试（使用内存 H2）
```powershell
Push-Location "d:\软工\OOP\java大作业\ZhongPingYouXuan\ZhongPingYouXuan"
.\mvnw.cmd test
Pop-Location
```

运行应用（使用 MySQL 的 `dev` profile）
1. 在 MySQL 中创建数据库并授权（示例使用 `root/123456`）：
```sql
CREATE DATABASE zhongpingyouxuan CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'root'@'localhost' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON zhongpingyouxuan.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```
2. 运行：
```powershell
Push-Location "d:\软工\OOP\java大作业\ZhongPingYouXuan\ZhongPingYouXuan"
.\mvnw.cmd spring-boot:run
Pop-Location
```

使用内存 H2 运行（不依赖 MySQL）
```powershell
Push-Location "d:\软工\OOP\java大作业\ZhongPingYouXuan\ZhongPingYouXuan"
# 使用我们添加的 local profile
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
Pop-Location
```

访问 H2 控制台（local profile）
- 打开浏览器访问 `http://localhost:8080/h2-console`
- JDBC URL 填写：`jdbc:h2:mem:localdb`，用户名 `sa`，密码留空

常见问题
- 如果运行 `spring-boot:run` 时仍尝试连接 MySQL，请确认激活的 profile 是 `local` 或 `test`（测试时）。
