package orders;

import java.util.List;

import ManagerBasic.AbstractManager;

public class OrderManager extends AbstractManager<Order> {
    
    private static OrderManager instance;

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
    
}
