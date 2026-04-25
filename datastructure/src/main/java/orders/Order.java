package orders;

import ManagerBasic.ManagedData;
import cars.CarManager;
import lombok.Data;
import orders.exception.OrderTimeException;
import university.App;

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
     * @param accountID 账户 ID
     * @param startTime 租赁开始时间，格式为 "yyyyMMdd"
     * @param endTime 租赁结束时间，格式为 "yyyyMMdd"
     * @throws OrderTimeException 
     */
    public Order(long orderID, long carID, String startTime, String endTime, OrderNature nature) throws OrderTimeException {
        this.orderID = orderID;
        this.carID = carID;
        this.accountID = App.getInstance().getLogInAccount().getID();
        this.startTime = startTime;
        this.endTime = endTime;
        var car = CarManager.getInstance().getCarByID(carID);
        double price = car.getPrice() * calculateDays(startTime, endTime);
        if (price < 0) {
            throw new OrderTimeException("结束时间必须晚于开始时间");
        }
        this.price = price;
        this.state = OrderState.PENDING;
        this.nature = nature;

        switch (nature) {
            case RENT:
                car.setState(cars.CarState.ON_RENT);
                break;
            case BOOKING:
                car.setState(cars.CarState.On_BOOKING);
                break;
            default:
                break;
        }
    }
    
    /** 计算租赁天数，简单计算方法，假设每个月30天，每年360天 
     * @param startTime 租赁开始时间，格式为 "yyyyMMdd"
     * @param endTime 租赁结束时间，格式为 "yyyyMMdd"
     * @return 返回租赁的天数
     *
    */
    public static int calculateDays(String startTime, String endTime) {

        int startYear = Integer.parseInt(startTime.substring(0,4));
        int startMonth = Integer.parseInt(startTime.substring(4,6));
        int startDay = Integer.parseInt(startTime.substring(6,8));
        int endYear = Integer.parseInt(endTime.substring(0,4));
        int endMonth = Integer.parseInt(endTime.substring(4,6));
        int endDay = Integer.parseInt(endTime.substring(6,8));
        
        // 简单计算天数，假设每个月30天，每年360天
        return (endYear - startYear) * 360 + (endMonth - startMonth) * 30 + (endDay - startDay);
    }

    public Order() {
    }

    public long getID() {
        return orderID;
    }
}
