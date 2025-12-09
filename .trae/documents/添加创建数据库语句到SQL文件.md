1. 在SQL文件头部（注释之后，SET语句之前）添加创建数据库的语句
2. 添加的语句为：
   ```sql
   CREATE DATABASE IF NOT EXISTS school_club_management;
   USE school_club_management;
   ```
3. 这样确保在执行SQL文件时，会先创建数据库（如果不存在），然后使用该数据库