package orders;

import ManagerBasic.ManagedData;
import lombok.Data;
import orders.exception.OrderTimeException;
import common.basic.*;;

/**
 * 订单类，表示系统中的一个租赁订单。
 * 包含订单 ID、车辆 ID、账户 ID、时间、价格、状态和性质。
 */
@Data
public class Order implements ManagedData{
    /** 订单的唯一 ID */
    private long orderID;
    /** 关联的车辆 ID */
    private long carID;
    /** 创建订单的账户 ID */
    private long accountID;
    /** 租赁开始时间，格式为 "yyyyMMdd" */
    private String startTime;
    /** 租赁结束时间，格式为 "yyyyMMdd" */
    private String endTime;
    /** 订单总价格 */
    private double price;
    /** 订单状态 */
    private OrderState state;
    /** 订单性质（租赁或预订） */
    private OrderNature nature;

    /**
     * 构造函数，初始化订单信息。
     * @param orderID 订单 ID
     * @param carID 车辆 ID
     * @param accountID 创建订单的账户 ID
     * @param startTime 租赁开始时间，格式为 "yyyyMMdd"
     * @param endTime 租赁结束时间，格式为 "yyyyMMdd"
     * @param nature 订单性质
     * @param price 日租价格
     * @throws OrderTimeException 当结束时间早于开始时间时抛出
     */
    public Order(long orderID, long carID, long accountID, String startTime, String endTime, OrderNature nature,double price) throws OrderTimeException {
        this.orderID = orderID;
        this.carID = carID;
        this.accountID = accountID;
        this.startTime = startTime;
        this.endTime = endTime;
        double _price = price * TimeTransport.getDaysBetween(startTime, endTime);
        if (_price < 0) {
            throw new OrderTimeException("结束时间必须晚于开始时间");
        }
        this.price = _price;
        this.state = OrderState.PENDING;
        this.nature = nature;
    }

    /**
     * 默认构造函数，供 JSON 反序列化使用。
     */
    public Order() {
    }

    /**
     * 获取订单的唯一 ID。
     * @return 订单 ID
     */
    public long getID() {
        return orderID;
    }
}
