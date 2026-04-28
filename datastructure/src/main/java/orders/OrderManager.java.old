package orders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import ManagerBasic.AbstractManager;
import account.AccountLevel;
import cars.CarManager;
import common.saver.SaveManager;
import common.saver.exception.LoadException;

/**
 * 订单管理类，负责管理订单的添加、查询等操作
 */
public class OrderManager extends AbstractManager<Order> {
    /** 单例模式的实例（复用父类 _instance，此处仅为兼容原有命名） */
    private static OrderManager _instance;

    /** 私有构造函数，注册 JVM 关闭钩子以确保在程序退出时保存订单数据 */
    private OrderManager() {
        super();
        Runtime.getRuntime().addShutdownHook(new Thread(this::save));
        // 初始化时加载数据
        load();
    }

    /**
     * 实现父类的抽象方法：创建实例（供父类 getInstance 调用）
     */
    @Override
    protected AbstractManager<?> createInstance() {
        if (_instance == null) {
            _instance = new OrderManager();
        }
        return _instance;
    }

    /**
     * 兼容原有调用方式的单例获取方法（也可直接用父类 getInstance()）
     */
    public static OrderManager getInstance() {
        if (_instance == null) {
            _instance = new OrderManager();
        }
        return _instance;
    }

    /** 销毁单例实例，释放资源 */
    public static void dispose() {
        _instance = null;
    }

    // -------------------------- 复用父类通用方法，覆写/扩展特有逻辑 --------------------------
    /**
     * 覆写父类 add 方法，增加数据保存（可选），同时继承权限校验逻辑
     * @param  order 要添加的订单对象
     * @return 返回 true 如果订单成功添加，否则返回 false（如订单对象为 null
     */
    @Override
    public boolean add(Order order) {
        if (order == null) {
            return false;
        }
        if (getByID(order.getOrderID()) != null) {
            return false;
        }
        boolean result = super.add(order);
        if (result) {
            save();
        }
        return result;
    }

    /**
     * 覆写父类 remove 方法，继承权限校验逻辑
     */
    @Override
    public boolean remove(Order order) {
        if (order == null) {
            return false;
        }
        boolean result = super.remove(order);
        var car = CarManager.getInstance().getByID(order.getCarID());
        if (car != null) {
            car.setState(cars.CarState.AVAILABLE);
        }
        if (result) {
            save();
        }
        return result;
    }

    /**
     * 实现父类抽象方法：定义订单管理操作所需的权限等级
     */
    @Override
    public int getRequiredPermissionLevel() {
        return AccountLevel.ADMIN.getLevel(); // 仅管理员可操作订单数据
    }

    public int getRequiredPermissionLevelForQuery() {
        return AccountLevel.USER.getLevel(); // 普通用户也可查询订单数据
    }

    // -------------------------- 订单管理特有业务逻辑 --------------------------
    /**
     * 获取所有订单信息，返回一个不可修改的 Map
     * 
     * @return 返回一个不可修改的 Map，包含所有订单信息，键为订单 ID，值为订单对象
     */
    public Map<Long, Order> getOrders() {
        return Collections.unmodifiableMap(items); // 复用父类的 items 容器
    }

    /**
     * 兼容原有方法名：添加订单（内部调用父类 add 方法，继承权限校验）
     * 
     * @param order 要添加的订单对象
     */
    public void addOrder(Order order) {
        add(order); // 复用父类带权限校验的 add 方法
    }

    /**
     * 兼容原有方法名：根据 ID 获取订单（父类已有 getByID，此处为兼容）
     * 
     * @param ID 订单 ID
     * @return 返回订单对象，如果 ID 不存在则返回 null
     */
    public Order getOrderByID(long ID) {
        return getByID(ID); // 复用父类的 getByID 方法
    }

    /**
     * 根据账户ID获取订单信息，返回一个 Map，键为订单 ID，值为订单对象
     * 
     * @param accountID 账户ID
     * @return 返回一个 Map，包含所有账户ID匹配的订单信息，键为订单 ID，值为订单对象
     */
    public Map<Long, Order> getOrderByAccountID(long accountID) {
        Map<Long, Order> result = new HashMap<>();
        for (Map.Entry<Long, Order> entry : items.entrySet()) {
            if (entry.getValue().getAccountID() == accountID) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * 根据车辆ID获取订单信息，返回一个 Map，键为订单 ID，值为订单对象
     * 
     * @param carID 车辆ID
     * @return 返回一个 Map，包含所有车辆ID匹配的订单信息，键为订单 ID，值为订单对象
     */
    public Map<Long, Order> getOrderByCarID(long carID) {
        Map<Long, Order> result = new HashMap<>();
        for (Map.Entry<Long, Order> entry : items.entrySet()) {
            if (entry.getValue().getCarID() == carID) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * 根据订单状态获取订单信息，返回一个 Map，键为订单 ID，值为订单对象
     * 
     * @param state 订单状态
     * @return 返回一个 Map，包含所有订单状态匹配的订单信息，键为订单 ID，值为订单对象
     */
    public Map<Long, Order> getOrderByState(OrderState state) {
        Map<Long, Order> result = new HashMap<>();
        for (Map.Entry<Long, Order> entry : items.entrySet()) {
            if (entry.getValue().getState() == state) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * 根据订单 ID 删除订单信息（带权限隐含校验：需通过 getAccountLevel 验证）
     * 
     * @param ID 订单 ID
     */
    public void removeOrderByID(long ID) {
        Order order = getByID(ID);
        if (order != null) {
            remove(order); // 复用父类带权限校验的 remove 方法
        }
    }

    /**
     * 更新订单状态（带权限隐含校验：需通过 getAccountLevel 验证）
     * 
     * @param ID       订单 ID
     * @param newState 新的订单状态
     */
    public void updateOrderByState(long ID, OrderState newState) {
        // 权限校验（复用父类的 isEnabled 方法）
        if (!isEnabled(getRequiredPermissionLevel(), getAccountLevel())) {
            return;
        }
        Order order = getByID(ID);
        if (order != null) {
            order.setState(newState);
            update(order);
        }
    }

    /**
     * 更新订单价格（带权限隐含校验）
     * 
     * @param ID       订单 ID
     * @param newPrice 新的订单价格
     */
    public void updateOrderByPrice(long ID, double newPrice) {
        // 权限校验
        if (!isEnabled(getRequiredPermissionLevel(), getAccountLevel())) {
            return;
        }
        Order order = getByID(ID);
        if (order != null) {
            order.setPrice(newPrice);
            update(order);
        }
    }

    // -------------------------- 数据持久化逻辑（加载/保存） --------------------------
    /** 从文件加载订单数据 */
    @SuppressWarnings("unchecked")
    private void load() {
        items.clear();
        List<Order> orderList;
        Type orderListType = new TypeToken<List<Order>>() {
        }.getType();
        Object loadedData = null;
        try {
            loadedData = SaveManager.getInstance().load("order", 0, orderListType);
        } catch (LoadException e) {
            orderList = new ArrayList<>();
        }
        if (loadedData instanceof List<?>) {
            orderList = (List<Order>) loadedData;
        } else {
            orderList = new ArrayList<>();
        }
        for (Order order : orderList) {
            items.put(order.getOrderID(), order);
        }
    }

    /** 将订单数据保存到文件 */
    public void save() {
        List<Order> orderList = new ArrayList<>(items.values());
        SaveManager.getInstance().save("order", orderList, 0);
    }

    /**
     * 订单完成，更新订单状态并释放车辆
     * 
     * @param ID 订单 ID
     * @return 返回 true 如果订单成功完成并释放车辆，否则返回 false（如订单不存在）
     */

    public boolean finishOrder(long ID) {
        if (!isEnabled(getRequiredPermissionLevel(), getAccountLevel())) {
            return false;
        }
        Order order = getByID(ID);
        if (order != null) {
            var car = CarManager.getInstance().getByID(order.getCarID());
            if (car == null) {
                return false;
            }
            car.setState(cars.CarState.AVAILABLE);
            order.setState(OrderState.FINISHED);
            update(order);
            return true;
        }
        return false;
    }

    /**
     * 订单取消，更新订单状态并释放车辆
     * 
     * @param ID 订单 ID
     * @return 返回 true 如果订单成功取消并释放车辆，否则返回 false（如订单不存在）
     */
    public boolean cancelOrder(long ID) {
        if (!isEnabled(getRequiredPermissionLevelForQuery(), getAccountLevel())) {
            return false;
        }
        Order order = getByID(ID);
        if (order != null) {
            if (order.getState() != OrderState.PENDING && order.getState() != OrderState.REVIEWING
                    && order.getState() != OrderState.REJECTED && order.getState() != OrderState.REVIEWED) {
                return false; // 已完成的订单不能取消
            }
            order.setState(OrderState.CANCELED);
            var car = CarManager.getInstance().getByID(order.getCarID());
            if (car != null)
                car.setState(cars.CarState.AVAILABLE);
            return true;
        }
        return false;
    }

    /**
     * 订单审核通过，更新订单状态并设置车辆为租出状态
     * 
     * @param ID 订单 ID
     * @return 返回 true 如果订单成功审核通过并设置车辆状态，否则返回 false（如订单不存在或订单状态不为 REVIEWING 或订单性质不为
     *         BOOKING）
     */
    public boolean RentByBooking(long ID) {
        if (!isEnabled(getRequiredPermissionLevel(), getAccountLevel())) {
            return false;
        }
        Order order = getByID(ID);
        if (order != null && order.getState() == OrderState.REVIEWED && order.getNature() == OrderNature.BOOKING) {
            order.setState(OrderState.RENTING);
            update(order);

            var car = CarManager.getInstance().getByID(order.getCarID());
            if (car != null) {
                car.setState(cars.CarState.ON_RENT);
                CarManager.getInstance().update(car);
            }
            return true;
        }
        return false;
    }

    /**
     * 提交订单审核，更新订单状态并设置车辆为预定中状态
     * 
     * @param order 订单对象
     * @return 返回 true 如果订单成功审核通过并设置车辆状态，否则返回 false（如订单对象为 null 或订单 ID 已存在或订单状态不为
     *         PENDING）
     */
    public boolean fileOrder(Order order) {
        if (!isEnabled(getRequiredPermissionLevelForQuery(), getAccountLevel())) {
            return false;
        }
        if (order == null) {
            return false;
        }
        if (getByID(order.getOrderID()) != null || order.getState() != OrderState.PENDING) {
            return false;
        }
        boolean result = super.add(order);
        if (result) {
            var car = CarManager.getInstance().getByID(order.getCarID());
            if (car != null) {
                car.setState(cars.CarState.ON_BOOKING);
            }
            save();
        }
        return result;
    }
}