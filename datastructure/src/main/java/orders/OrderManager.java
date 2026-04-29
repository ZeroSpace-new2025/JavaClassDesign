package orders;

import java.lang.reflect.Type;
import java.util.List;

import ManagerBasic.AbstractManager;

/**
 * 订单管理器类，负责管理订单的增删改查操作。
 * 继承自 AbstractManager，采用单例模式。
 */
public class OrderManager extends AbstractManager<Order> {
    
    private static OrderManager instance;

    /**
     * 获取 OrderManager 的单例实例。
     * @return OrderManager 单例实例
     */
    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    private OrderManager() {
        super();
        init(0);
    }

    @Override
    public List<Order> defaultList() {
        return new java.util.ArrayList<>();
    }

    @Override
    public String getTypeName() {
        return "Order";
    }

    /**
     * 根据订单 ID 获取订单信息
     * @param orderID 订单 ID
     * @return 返回订单对象，如果订单不存在则返回 null
     */
    public Order getOrderByID(long orderID) {
        return getByID(orderID);
    }

    /**
     * 根据账户 ID 获取订单信息
     * @param accountID 账户 ID
     * @return 返回订单列表，如果没有订单则返回空列表
     */
    public List<Order> getOrdersByAccountID(long accountID) {
        return listAll().stream()
                .filter(order -> order.getAccountID() == accountID)
                .toList();
    }

    /**
     * 根据车辆 ID 获取订单信息
     * @param carID 车辆 ID
     * @return 返回订单列表，如果没有订单则返回空列表
     */
    public List<Order> getOrdersByCarID(long carID) {
        return listAll().stream()
                .filter(order -> order.getCarID() == carID)
                .toList();
    }

    /**
     * 根据订单状态获取订单信息
     * @param state 订单状态
     * @return 返回订单列表，如果没有订单则返回空列表
     */
    public List<Order> getOrdersByState(OrderState state) {
        return listAll().stream()
                .filter(order -> order.getState() == state)
                .toList();
    }

    /**
     * 根据订单性质获取订单信息
     * @param nature 订单性质
     * @return 返回订单列表，如果没有订单则返回空列表
     */
    public List<Order> getOrdersByNature(OrderNature nature) {
        return listAll().stream()
                .filter(order -> order.getNature() == nature)
                .toList();
    }
    
    /**
     * 获取 Gson 反序列化时使用的类型信息。
     * @return Order 类型的 Type 对象
     */
    @Override
    public Type getManagerType() {
        return new com.google.gson.reflect.TypeToken<Order>(){}.getType();
    }
}
