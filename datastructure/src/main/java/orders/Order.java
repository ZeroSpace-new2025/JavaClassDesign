package orders;

import ManagerBasic.ManagedData;
import lombok.Data;
import orders.exception.OrderTimeException;
import university.App;
import common.basic.*;;

@Data
public class Order implements ManagedData{
    private long orderID;
    private long carID;
    private long accountID;
    private String startTime;
    private String endTime;
    private double price;
    private OrderState state;
    private OrderNature nature;

    /** 构造函数，初始化订单信息
     * @param orderID 订单 ID
     * @param carID 车辆 ID
     * @param startTime 租赁开始时间，格式为 "yyyyMMdd"
     * @param endTime 租赁结束时间，格式为 "yyyyMMdd"
     * @throws OrderTimeException 
     */
    public Order(long orderID, long carID, String startTime, String endTime, OrderNature nature,double price) throws OrderTimeException {
        this.orderID = orderID;
        this.carID = carID;
        this.accountID = App.getInstance().getLogInAccount().getID();
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

    public Order() {
    }

    public long getID() {
        return orderID;
    }
}
