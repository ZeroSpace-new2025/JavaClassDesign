# AssemblyInfo.md - 程序集信息

---

## 1. 程序集概述

- **编写环境**: Java 21 + Maven + Swing（GUI 框架）
- **层级定位**: 汽车租赁管理系统，采用 common → datamanager → business → visitable 四层架构
- **输入规范**: 接收用户操作指令（注册/登录/添加车辆/创建订单/审核订单等），通过 Business 层进行权限校验与业务处理
- **输出规范**: 提供单例管理器实例、JSON 格式数据持久化、权限控制结果（boolean）、领域实体对象、Swing GUI 界面
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
| `AbstractManager<T>` | 抽象基类，实现 BaseManager 接口，使用 `HashMap<Long, T>` 存储数据，提供数据加载/保存/默认列表等持久化骨架方法，子类需实现 defaultList/getTypeName/getManagerType 抽象方法 |
| `ManagedData` | 标记接口，要求实现类提供 `getID()` 方法，作为 Manager 管理的实体约束 |

### 2.2 账户管理模块（account 包）

| 类名 | 功能描述 |
|------|---------|
| `Account` | 账户实体类，包含 username/password/level/ID 字段，ID 通过 MD5(username) 前16位生成，实现 ManagedData 接口 |
| `AccountManager` | 账户管理单例，独立实现 CRUD（未继承 AbstractManager），提供 registerAccount/deleteAccount/getAccount/getByUsername/tryLogin/isAllowed 方法，支持 JSON 持久化与 JVM 关闭自动保存 |
| `AccountLevel` | 权限级别枚举：GUEST(0)、USER(1)、ADMIN(25565) |

### 2.3 车辆管理模块（cars 包）

| 类名 | 功能描述 |
|------|---------|
| `Car` | 车辆实体类，包含 name/color/price/state/ID/model 字段，构造函数校验非空与非负价格，实现 ManagedData 接口 |
| `CarManager` | 车辆管理单例，继承 AbstractManager<Car>，提供 addCar/getCarByID/getCarByName/getCarByModel/removeCarByID/updateCarState/updateCarPrice/getOrdersByCarID 方法，删除车辆时检查关联订单，支持 JSON 持久化 |
| `CarState` | 车辆状态枚举：ON_RENT（租出）、ON_REPAIR（维修）、AVAILABLE（可用）、ON_BOOKING（预定中） |

### 2.4 订单管理模块（orders 包）

| 类名 | 功能描述 |
|------|---------|
| `Order` | 订单实体类，包含 orderID/carID/accountID/startTime/endTime/price/state/nature 字段，构造函数自动计算价格（车辆日租金 × 租赁天数），实现 ManagedData 接口 |
| `OrderManager` | 订单管理单例，继承 AbstractManager<Order>，提供 addOrder/getOrderByID/getOrderByAccountID/getOrderByCarID/getOrderByState/removeOrderByID/updateOrderByState/updateOrderByPrice/finishOrder/cancelOrder/RentByBooking/fileOrder 方法，订单状态流转联动车辆状态变更，支持 JSON 持久化 |
| `OrderState` | 订单状态枚举：PENDING（待确认）、RENTING（租赁中）、FINISHED（已完成）、CANCELED（已取消）、REVIEWING（审核中）、REJECTED（审核未通过）、REVIEWED（已审核） |
| `OrderNature` | 订单性质枚举：RENT（直接租赁）、BOOKING（预定） |

### 2.5 业务逻辑层（business 包）

| 类名 | 功能描述 |
|------|---------|
| `LoginAccount` | 登录账户业务单例，负责账户登录/登出/注册/权限验证/密码修改/权限级别变更，提供 isAllow 权限检查方法，支持未登录状态下默认 GUEST 权限，提供 listAllAccounts/getAccountByID/getAccountByUsername 查询方法 |
| `CarsBusiness` | 车辆业务类，封装车辆 CRUD 操作，所有操作均基于 LoginAccount 权限验证：添加/删除/更新需 ADMIN 权限，查看需 GUEST 权限，删除时检查关联订单并提供强制删除选项 |
| `Ordersbusinesss` | 订单业务类，封装订单 CRUD 与审核操作，所有操作均基于 LoginAccount 权限验证：提交/取消/完成订单需 USER 权限，删除/审核通过/拒绝订单需 ADMIN 权限，提供 listAllOrders/getOrderByID 查询方法 |

### 2.6 公共工具模块（common 包）

| 类名 | 路径 | 功能描述 |
|------|------|---------|
| `MD5Util` | `common.basic.MD5Util` | 静态工具类，提供字符串 MD5 哈希计算，用于生成 Account ID |
| `TimeTransport` | `common.basic.TimeTransport` | 静态工具类，提供时间解析/格式化/天数计算功能，支持 "yyyyMMdd" 格式，提供 getDaysBetween/parseTime/formatTime/toDays 等方法 |
| `SaveManager` | `common.saver.SaveManager` | 数据持久化单例外观类，管理 saves/ 目录，提供 save/load/saveByFullPath/loadByFullPath 方法，文件命名格式 `{name}_{id}.json` |
| `DataSaveManager` | `common.saver.DataSaveManager` | 包级私有工具类，使用 Gson 实现 JSON 序列化/反序列化与文件读写 |

### 2.7 异常处理模块

| 类名 | 路径 | 功能描述 |
|------|------|---------|
| `OrderException` | `orders.exception.OrderException` | 订单基础异常类 |
| `OrderTimeException` | `orders.exception.OrderTimeException` | 订单时间校验异常，继承 OrderException |
| `SaveException` | `common.saver.exception.SaveException` | 数据保存异常类 |
| `LoadException` | `common.saver.exception.LoadException` | 数据加载异常类 |
| `NotFoundFileException` | `common.saver.exception.NotFoundFileException` | 文件未找到异常，继承 LoadException |

### 2.8 应用入口（university 包）

| 类名 | 功能描述 |
|------|---------|
| `App` | 应用单例入口，持有当前登录账户（LoginAccount 实例），main 方法初始化 SaveManager 和 AccountManager，启动 MainFrame，提供 getLogInAccount/setLogInAccount/dispose 方法 |

### 2.9 图形界面层（visitable 包）

| 类名 | 功能描述 |
|------|---------|
| `MainFrame` | 主窗口单例，1024x768 固定尺寸，包含 Car/Order/Account 三个导航按钮，使用 CardLayout 切换页面，设置 CarPanel 到 OrderPanel 的跨面板刷新回调 |
| `BasePanel<T>` | 抽象基础面板，提供通用列表展示/筛选/详情查看功能，包含筛选栏（下拉框+输入框+按钮）、数据列表（JList）、详情弹窗（JDialog），子类需实现 getFilterOptions/getListData/onFilterClick/childInitDataList 抽象方法 |
| `CarPanel` | 车辆列表面板，继承 BasePanel<Car>，展示车辆 ID/名称/型号/颜色/价格/状态，支持按名称/价格/型号/颜色/状态筛选，详情弹窗显示"创建订单"按钮（USER 权限可见），创建订单时弹出时间选择器并校验时间合法性 |
| `OrderPanel` | 订单列表面板，继承 BasePanel<Order>，展示订单 ID/车辆名称/开始时间/结束时间/价格/状态，支持按订单ID/车辆ID/开始时间/结束时间/状态/类型筛选，详情弹窗显示"取消订单"/"完成"/"通过审核"/"拒绝通过"按钮（按权限显示），提供 refreshData 公开刷新方法供外部调用 |
| `AccountPanel` | 账户列表面板，继承 BasePanel<Account>，未登录时显示登录/注册界面（用户名/密码输入框+登录按钮+注册按钮），已登录时：Admin 以下显示本人账户详情+底部"注销本账号"/"修改密码"按钮，Admin 及以上显示所有账户列表+底部"删除"/"改变访问等级"/"更改密码"按钮，支持按用户名/权限级别/账户ID筛选 |

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
| TimeTransport | `datastructure/src/main/java/common/basic/TimeTransport.java` |
| SaveManager | `datastructure/src/main/java/common/saver/SaveManager.java` |
| DataSaveManager | `datastructure/src/main/java/common/saver/DataSaveManager.java` |
| SaveException | `datastructure/src/main/java/common/saver/exception/SaveException.java` |
| LoadException | `datastructure/src/main/java/common/saver/exception/LoadException.java` |
| NotFoundFileException | `datastructure/src/main/java/common/saver/exception/NotFoundFileException.java` |
| LoginAccount | `datastructure/src/main/java/business/LoginAccount.java` |
| CarsBusiness | `datastructure/src/main/java/business/CarsBusiness.java` |
| Ordersbusinesss | `datastructure/src/main/java/business/Ordersbusinesss.java` |
| MainFrame | `datastructure/src/main/java/visitable/MainFrame.java` |
| BasePanel | `datastructure/src/main/java/visitable/BasePanel.java` |
| CarPanel | `datastructure/src/main/java/visitable/CarPanel.java` |
| OrderPanel | `datastructure/src/main/java/visitable/OrderPanel.java` |
| AccountPanel | `datastructure/src/main/java/visitable/AccountPanel.java` |
| AppTest | `datastructure/src/test/java/university/AppTest.java` |

---

## 4. 设计模式与架构

| 模式 | 应用位置 | 说明 |
|------|---------|------|
| 单例模式 | App, SaveManager, AccountManager, CarManager, OrderManager, LoginAccount, MainFrame | 全局唯一实例，通过 getInstance() 获取 |
| 模板方法模式 | AbstractManager → CarManager/OrderManager | 父类定义 CRUD 骨架，子类覆写特定方法 |
| 泛型仓储模式 | BaseManager<T> / AbstractManager<T> | 通用数据访问接口，类型安全 |
| 外观模式 | SaveManager → DataSaveManager | 简化持久化 API，隐藏底层实现细节 |
| 基于角色的访问控制（RBAC） | AccountLevel + LoginAccount.isAllow() + Business 层权限校验 | 通过权限等级数值比较实现操作授权 |
| 模板方法模式（UI） | BasePanel<T> → CarPanel/OrderPanel/AccountPanel | 父类定义列表/筛选/详情弹窗骨架，子类实现数据展示与业务逻辑 |
| 回调模式 | MainFrame → CarPanel.setOrderRefreshCallback() → OrderPanel.refreshData() | 跨面板数据同步，CarPanel 创建订单后触发 OrderPanel 刷新 |
| 策略模式（降级） | LoginAccount.login() | 先通过 computed ID 查找账户，失败后降级为用户名直接匹配 |

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

## 6. 权限控制矩阵

| 操作 | GUEST | USER | ADMIN |
|------|-------|------|-------|
| 查看车辆列表 | ✓ | ✓ | ✓ |
| 查看订单列表 | ✗ | ✓ | ✓ |
| 查看账户列表 | ✗ | ✗ | ✓ |
| 创建订单 | ✗ | ✓ | ✓ |
| 取消订单 | ✗ | ✓ | ✓ |
| 完成订单 | ✗ | ✓ | ✓ |
| 添加车辆 | ✗ | ✗ | ✓ |
| 删除车辆 | ✗ | ✗ | ✓ |
| 删除订单 | ✗ | ✗ | ✓ |
| 审核通过订单 | ✗ | ✗ | ✓ |
| 拒绝订单 | ✗ | ✗ | ✓ |
| 删除账户 | ✗ | ✗ | ✓ |
| 修改账户权限 | ✗ | ✗ | ✓ |
| 修改他人密码 | ✗ | ✗ | ✓ |
| 修改本人密码 | ✗ | ✓ | ✓ |
| 注销本人账户 | ✗ | ✓ | ✓ |
| 注册新账户 | ✓(USER) | ✓(USER) | ✓(任意级别) |

---
