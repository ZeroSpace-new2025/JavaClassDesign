# AssemblyInfo.md - 程序集信息

---

## 1. 程序集概述

- **编写环境**: Java 21 + Maven + Swing（GUI 框架预留）
- **层级定位**: 汽车租赁管理系统核心业务逻辑层，负责账户、车辆、订单的 CRUD 操作与数据持久化
- **输入规范**: 接收用户操作指令（注册/登录/添加车辆/创建订单等），通过 Manager 类进行权限校验与业务处理
- **输出规范**: 提供单例管理器实例、JSON 格式数据持久化、权限控制结果（boolean）、领域实体对象
- **依赖项**: 
  - `com.google.code.gson:gson 2.10.1` — JSON 序列化/反序列化
  - `org.projectlombok:lombok 1.18.36` — 代码简化（@Data, @Getter）
  - `junit:junit 3.8.1` — 单元测试（测试域）

---

## 2. 程序集封装功能

### 2.1 核心管理器框架（ManagerBasic 包）

| 类名 | 功能描述 |
|------|---------|
| `BaseManager<T>` | 泛型 CRUD 接口，定义 add/remove/getByID/update/containsID/contains/clear/listAll 八个标准操作 |
| `AbstractManager<T>` | 抽象基类，实现 BaseManager 接口，使用 `HashMap<Long, T>` 存储数据，集成基于 AccountLevel 的权限校验逻辑（isEnabled 方法），提供 getAccountLevel/getRequiredPermissionLevel 抽象方法供子类实现 |
| `ManagedData` | 标记接口，要求实现类提供 `getID()` 方法，作为 Manager 管理的实体约束 |

### 2.2 账户管理模块（account 包）

| 类名 | 功能描述 |
|------|---------|
| `Account` | 账户实体类，包含 username/password/level/ID 字段，ID 通过 MD5(username) 前16位生成，实现 ManagedData 接口 |
| `AccountManager` | 账户管理单例，独立实现 CRUD（未继承 AbstractManager），提供 registerAccount/deleteAccount/getAccount/tryLogin/isAllowed 方法，支持 JSON 持久化与 JVM 关闭自动保存 |
| `AccountLevel` | 权限级别枚举：GUEST(0)、USER(1)、ADMIN(25565) |

### 2.3 车辆管理模块（cars 包）

| 类名 | 功能描述 |
|------|---------|
| `Car` | 车辆实体类，包含 name/color/price/state/ID/model 字段，构造函数校验非空与非负价格，实现 ManagedData 接口 |
| `CarManager` | 车辆管理单例，继承 AbstractManager<Car>，提供 addCar/getCarByID/getCarByName/getCarByModel/removeCarByID/updateCarState/updateCarPrice 方法，删除车辆时检查关联订单，支持 JSON 持久化 |
| `CarState` | 车辆状态枚举：ON_RENT（租出）、ON_REPAIR（维修）、AVAILABLE（可用）、ON_BOOKING（预定中） |

### 2.4 订单管理模块（orders 包）

| 类名 | 功能描述 |
|------|---------|
| `Order` | 订单实体类，包含 orderID/carID/accountID/startTime/endTime/price/state/nature 字段，构造函数自动计算价格（车辆日租金 × 租赁天数），实现 ManagedData 接口 |
| `OrderManager` | 订单管理单例，继承 AbstractManager<Order>，提供 addOrder/getOrderByID/getOrderByAccountID/getOrderByCarID/getOrderByState/removeOrderByID/updateOrderByState/updateOrderByPrice/finishOrder/cancelOrder/RentByBooking/fileOrder 方法，订单状态流转联动车辆状态变更，支持 JSON 持久化 |
| `OrderState` | 订单状态枚举：PENDING（待确认）、RENTING（租赁中）、FINISHED（已完成）、CANCELED（已取消）、REVIEWING（审核中）、REJECTED（审核未通过）、REVIEWED（已审核） |
| `OrderNature` | 订单性质枚举：RENT（直接租赁）、BOOKING（预定） |

### 2.5 异常处理模块

| 类名 | 路径 | 功能描述 |
|------|------|---------|
| `OrderException` | `orders.exception.OrderException` | 订单基础异常类 |
| `OrderTimeException` | `orders.exception.OrderTimeException` | 订单时间校验异常，继承 OrderException |
| `SaveException` | `common.saver.exception.SaveException` | 数据保存异常类 |
| `LoadException` | `common.saver.exception.LoadException` | 数据加载异常类 |
| `NotFoundFileException` | `common.saver.exception.NotFoundFileException` | 文件未找到异常，继承 LoadException |

### 2.6 公共工具模块（common 包）

| 类名 | 路径 | 功能描述 |
|------|------|---------|
| `MD5Util` | `common.basic.MD5Util` | 静态工具类，提供字符串 MD5 哈希计算，用于生成 Account ID |
| `SaveManager` | `common.saver.SaveManager` | 数据持久化单例外观类，管理 saves/ 目录，提供 save/load/saveByFullPath/loadByFullPath 方法，文件命名格式 `{name}_{id}.json` |
| `DataSaveManager` | `common.saver.DataSaveManager` | 包级私有工具类，使用 Gson 实现 JSON 序列化/反序列化与文件读写 |

### 2.7 应用入口（university 包）

| 类名 | 功能描述 |
|------|---------|
| `App` | 应用单例入口，持有当前登录账户（默认 GUEST），main 方法初始化 SaveManager 和 AccountManager，提供 getLogInAccount/dispose 方法 |

---

## 3. 文件结构

| 类名 | 路径 |
|------|------|
| App | `datastructure/src/main/java/university/App.java` |
| BaseManager | `datastructure/src/main/java/ManagerBasic/BaseManager.java` |
| AbstractManager | `datastructure/src/main/java/ManagerBasic/AbstractManager.java` |
| ManagedData | `datastructure/src/main/java/ManagerBasic/ManagedData.java` |
| Account | `datastructure/src/main/java/account/Account.java` |
| AccountManager | `datastructure/src/main/java/account/AccountManager.java` |
| AccountLevel | `datastructure/src/main/java/account/AccountLevel.java` |
| Car | `datastructure/src/main/java/cars/Car.java` |
| CarManager | `datastructure/src/main/java/cars/CarManager.java` |
| CarState | `datastructure/src/main/java/cars/CarState.java` |
| Order | `datastructure/src/main/java/orders/Order.java` |
| OrderManager | `datastructure/src/main/java/orders/OrderManager.java` |
| OrderState | `datastructure/src/main/java/orders/OrderState.java` |
| OrderNature | `datastructure/src/main/java/orders/OrderNature.java` |
| OrderException | `datastructure/src/main/java/orders/exception/OrderException.java` |
| OrderTimeException | `datastructure/src/main/java/orders/exception/OrderTimeException.java` |
| MD5Util | `datastructure/src/main/java/common/basic/MD5Util.java` |
| SaveManager | `datastructure/src/main/java/common/saver/SaveManager.java` |
| DataSaveManager | `datastructure/src/main/java/common/saver/DataSaveManager.java` |
| SaveException | `datastructure/src/main/java/common/saver/exception/SaveException.java` |
| LoadException | `datastructure/src/main/java/common/saver/exception/LoadException.java` |
| NotFoundFileException | `datastructure/src/main/java/common/saver/exception/NotFoundFileException.java` |
| AppTest | `datastructure/src/test/java/university/AppTest.java` |

---

## 4. 设计模式与架构

| 模式 | 应用位置 | 说明 |
|------|---------|------|
| 单例模式 | App, SaveManager, AccountManager, CarManager, OrderManager | 全局唯一实例，通过 getInstance() 获取 |
| 模板方法模式 | AbstractManager → CarManager/OrderManager | 父类定义 CRUD 骨架，子类覆写特定方法 |
| 泛型仓储模式 | BaseManager<T> / AbstractManager<T> | 通用数据访问接口，类型安全 |
| 外观模式 | SaveManager → DataSaveManager | 简化持久化 API，隐藏底层实现细节 |
| 基于角色的访问控制（RBAC） | AccountLevel + AbstractManager.isEnabled() | 通过权限等级数值比较实现操作授权 |

---

## 5. 数据持久化规范

- **存储目录**: `saves/`（相对于工作目录）
- **文件格式**: JSON（Gson 序列化，PrettyPrinting 格式化输出）
- **文件命名**: `{name}_{id}.json`（如 `account_0.json`, `car_0.json`, `order_0.json`）
- **触发时机**: 
  - 程序启动时加载（Manager 构造函数/getInstance 中调用 load）
  - 程序退出时保存（JVM ShutdownHook）
  - 关键操作后保存（OrderManager.add/fileOrder 等）

---
