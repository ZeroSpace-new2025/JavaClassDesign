package orders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import common.saver.SaveManager;
import common.saver.exception.LoadException;

/**
 * 订单管理类，负责管理订单的添加、查询等操作
 */
public class OrderManager {
    /** 存储订单信息的 Map，键为订单 ID，值为订单对象 */
    private Map<Long, Order> orders = new HashMap<>();
    /** 单例模式的实例 */
    private static OrderManager _intrnce;

    /** 私有构造函数，防止外部实例化 */
    public static OrderManager getInstance() {
        if (_intrnce == null) {
            _intrnce = new OrderManager();
            _intrnce.load();
        }
        return _intrnce;
    }

    /** 销毁单例实例，释放资源 */
    public static void despose() {
        _intrnce = null;
    }

    /** 私有构造函数，注册 JVM 关闭钩子以确保在程序退出时保存订单数据 */
    private OrderManager() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            save();
        }));
    }


    /** 获取所有订单信息，返回一个不可修改的 Map */
    public Map<Long, Order> getOrders() {
        return Collections.unmodifiableMap(orders);
    }

    /** 根据订单 ID 获取订单信息 */
    public void addOrderByID(Order order) {
        orders.put(order.getOrderID(), order);
    }

    /** 根据订单 ID 获取订单信息 */
    public Order getOrderByID(long ID) {
        return orders.get(ID);
    }

    /**
     * 根据账户ID获取订单信息，返回一个 Map，键为订单 ID，值为订单对象
     * @param accountID 账户ID
     * @return 返回一个 Map，包含所有账户ID匹配的订单信息，键为订单 ID，值为订单对象
      *
     */
    public Map<Long, Order> getOrderByAccountID(long accountID) {
        Map<Long, Order> result = new HashMap<>();
        for (Map.Entry<Long, Order> entry : orders.entrySet()) {
            if (entry.getValue().getAccountID() == accountID) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /** 根据订单状态获取订单信息，返回一个 Map，键为订单 ID，值为订单对象
     * @param state 订单状态
     * @return 返回一个 Map，包含所有订单状态匹配的订单信息，键为订单 ID，值为订单对象
     */
    public Map<Long, Order> getOrderByState(OrderState state) {
        Map<Long, Order> result = new HashMap<>();
        for (Map.Entry<Long, Order> entry : orders.entrySet()) {
            if (entry.getValue().getState() == state) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }


    /** 根据订单 ID 删除订单信息 */
    public void removeOrderByID(long ID) {
        orders.remove(ID);
    }

    /** 更新订单状态，根据订单 ID 更新订单的状态
     * @param ID 订单 ID
     * @param newState 新的订单状态
     */
    public void updateOrderByState(long ID, OrderState newState) {
        Order order = orders.get(ID);
        if (order != null) {
            order.setState(newState);
        }
    }

    /** 更新订单价格，根据订单 ID 更新订单的价格
     * @param ID 订单 ID
     * @param newPrice 新的订单价格
     */
    public void updateOrderByPrice(long ID, double newPrice) {
        Order order = orders.get(ID);
        if (order != null) {
            order.setPrice(newPrice);
        }
    }

    /** 
     * 从保存的数据中加载订单信息，清空当前的订单 Map，并将加载的数据添加到 Map 中。
     */
    @SuppressWarnings("unchecked")
    private void load() {
        orders.clear();
        List<Order> carList;
        Type carListType = new TypeToken<List<Order>>() {}.getType();
        Object loadedData = null;
        try{
            loadedData = SaveManager.getInstance().load("order", 0, carListType);
        }
        catch(LoadException e)
        {
            carList = new ArrayList<>();
        }
        if (loadedData instanceof List<?>) {
            carList = (List<Order>) loadedData;
        }else{
            carList = new ArrayList<>();
        }
        for (Order car : carList) {
            orders.put(car.getOrderID(), car);
        }
    }

    /** 
     * 将当前订单信息保存到文件中，将订单 Map 中的订单列表保存到文件中。
     */
    public void save() {
        List<Order> carList = new ArrayList<>(orders.values());
        SaveManager.getInstance().save("order", carList, 0);
    }
}