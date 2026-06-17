# 小学数学口算挑战系统 
一个基于 Java Swing + MySQL 的小学数学口算练习与挑战系统，支持命令行与图形界面双模式。

## 功能概览

- **登录 / 注册** — 用户体系，积分累计
- **难度分级** — L1（简单）、L2（中等）、L3（困难）
- **四种练习模式** — 混合、仅加法、仅减法、乘除
- **错题本** — 自动记录错题，支持重练与掌握标记
- **积分榜** — 按总积分排名的全局排行榜
- **结果统计** — 每次挑战后展示正确率、用时、得分

## 项目结构

```
最终版/
├── src/
│   ├── arithmetic/
│   │   ├── Main.java              # 控制台入口
│   │   ├── gui/
│   │   │   ├── GUIMain.java       # GUI 入口
│   │   │   ├── MainFrame.java     # 主窗口
│   │   │   ├── LoginPanel.java    # 登录面板
│   │   │   ├── DashboardPanel.java
│   │   │   ├── ChallengePanel.java
│   │   │   ├── ResultPanel.java
│   │   │   ├── WrongBookPanel.java
│   │   │   ├── LeaderboardPanel.java
│   │   │   ├── ComponentFactory.java
│   │   │   └── UIConstants.java
│   │   ├── challenge/
│   │   │   ├── Challenge.java
│   │   │   ├── ChallengeResult.java
│   │   │   └── ChallengeRunner.java
│   │   ├── dao/
│   │   │   ├── DBUtil.java        # 数据库连接工具
│   │   │   ├── UserDAO.java
│   │   │   ├── ChallengeRecordDAO.java
│   │   │   └── WrongEntryDAO.java
│   │   ├── user/
│   │   │   ├── User.java
│   │   │   ├── UserManager.java
│   │   │   ├── WrongBook.java
│   │   │   ├── WrongEntry.java
│   │   │   └── Leaderboard.java
│   │   ├── arithmetic/            # 四则运算实现
│   │   │   ├── Addition.java
│   │   │   ├── Substraction.java
│   │   │   ├── Multiplication.java
│   │   │   ├── Division.java
│   │   │   ├── Binaryoperation.java
│   │   │   ├── OperationFactory.java
│   │   │   ├── Exercise.java
│   │   │   └── Difficulty.java
│   │   └── TEST/                  # 单元测试
│   │       ├── AdditionTest.java
│   │       ├── BinaryoperationTest.java
│   │       └── ChallengeRunnerTest.java
├── create_tables.sql              # 数据库建表脚本
├── out/                           # 编译输出
└── 最终版.iml
```

## 环境要求

| 依赖 | 版本要求 |
|------|---------|
| JDK  | 8 或更高 |
| MySQL | 5.7+ / 8.0+ |
| 驱动程序 | `mysql-connector-java-x.x.x.jar` |

## 快速开始

### 1. 创建数据库

登录 MySQL 并执行建表脚本：

```bash
mysql -u root -p < create_tables.sql
```

默认数据库名 `swork`，如需修改请到 `src/arithmetic/dao/DBUtil.java` 更改连接参数。

### 2. 配置数据库连接

打开 `src/arithmetic/dao/DBUtil.java`，按实际情况修改：

```java
private static final String URL  = "jdbc:mysql://localhost:3306/swork?...";
private static final String USER = "root";
private static final String PASS = "你的密码";
```

### 3. 编译

```bash
# 创建 out 目录（如已存在则跳过）
mkdir -p out

# 编译所有源文件（请将 mysql-connector-java.jar 路径替换为实际路径）
javac -cp ".;mysql-connector-java-x.x.x.jar" -d out src/arithmetic/**/*.java
```

### 4. 运行

**图形界面模式（推荐）：**

```bash
java -cp "out;mysql-connector-java-x.x.x.jar" arithmetic.gui.GUIMain
```

**控制台模式：**

```bash
java -cp "out;mysql-connector-java-x.x.x.jar" arithmetic.Main
```

> **提示：** 如果 `mysql-connector-java.jar` 已放置在项目根目录或已加入 CLASSPATH，只需对应调整 `-cp` 参数路径即可。

### 5. 用 IntelliJ IDEA 打开（推荐）

本项目为 IntelliJ IDEA 项目，直接使用 IDEA 打开项目根目录即可：

1. **File → Open** → 选择项目根目录
2. 确认 Project SDK 为 JDK 8+
3. 将 `mysql-connector-java.jar` 添加到 **Project Structure → Libraries**
4. 运行 `arithmetic.gui.GUIMain` 或 `arithmetic.Main`

> 项目已包含编译配置（.idea/），开箱即用。

## 使用说明

1. 启动后进入登录页面，**注册**新账号或**登录**已有账号
2. 登录后进入**首页**，可查看个人统计概览
3. 点击 **开始挑战** 选择难度（L1/L2/L3）与模式（混合/加法/减法/乘除）
4. 完成挑战后查看**结果统计**（正确数、用时、得分）
5. 在 **错题本** 回顾并重练做错的题目
6. 在 **积分榜** 查看与其他用户的排名

## 常见问题

**Q: 运行时报错 "MySQL 驱动加载失败"**
A: 确保 `mysql-connector-java.jar` 已加入 classpath。

**Q: 连接数据库失败 "Access denied"**
A: 检查 `DBUtil.java` 中的用户名和密码是否正确。

**Q: 编译时 `src/arithmetic/**/*.java` 不生效**
A: Windows 命令行下可改用 `dir /s /B src\arithmetic\*.java > sources.txt && javac @sources.txt`。
