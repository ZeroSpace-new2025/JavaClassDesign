package business;

import account.AccountLevel;
import cars.CarManager;
import common.basic.TimeTransport;
import orders.OrderManager;
import orders.exception.OrderTimeException;

public class Ordersbusinesss {
    private LoginAccount _account;


    public Ordersbusinesss(LoginAccount account) {
        _account = account;
    }

    /**
     * 尝试提交一个订单，只有当当前登录账户的权限级别高于或等于普通用户权限级别时才允许提交订单。
     * @param carID 要预订的汽车的 ID
     * @param startTime 订单的开始时间，格式为 "yyyyMMdd"
     * @param endTime 订单的结束时间，格式为 "yyyyMMdd"
     */
    public boolean fileOrder(long carID,String startTime,String endTime) {
        if(!_account.isAllow(AccountLevel.USER)){// 只有普通用户权限及以上才能提交订单
            return false; // 没有权限提交订单
        }
        var car = CarManager.getInstance().getByID(carID);
        if(car == null) {
            return false; 
        }
        try{
            if(TimeTransport.getDaysBetween(startTime, endTime) <= 0) {
                return false; // 时间不合法
            }
            var order = new orders.Order(System.currentTimeMillis(), carID, startTime, endTime, orders.OrderNature.RENT, car.getPrice());
            return OrderManager.getInstance().add(order);
        }
        catch(OrderTimeException e){
            return false;
        }
    }
}
