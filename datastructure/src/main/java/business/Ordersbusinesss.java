package business;

import account.AccountLevel;
import cars.CarManager;
import common.basic.TimeTransport;
import orders.OrderManager;
import orders.exception.OrderTimeException;

/**
 * 订单业务类，负责订单的创建、取消、完成、审核等操作。
 * 所有操作均基于当前登录账户的权限进行验证。
 */
public class Ordersbusinesss {
    private LoginAccount _account;


    /**
     * 构造函数，注入登录账户对象。
     * @param account 登录账户业务对象
     */
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
        if(_account.getAccount() == null) {
            return false;
        }
        if(!_account.isAllow(AccountLevel.USER)){
            return false;
        }
        var car = CarManager.getInstance().getByID(carID);
        if(car == null) {
            return false; 
        }
        try{
            if(TimeTransport.getDaysBetween(startTime, endTime) <= 0) {
                return false;
            }
            var order = new orders.Order(System.currentTimeMillis(), carID, _account.getAccount().getID(), startTime, endTime, orders.OrderNature.RENT, car.getPrice());
            return OrderManager.getInstance().add(order);
        }
        catch(OrderTimeException e){
            return false;
        }
    }

    /**
     * 尝试取消一个订单，只有当当前登录账户的权限级别高于或等于普通用户权限级别时才允许取消订单。
     * @param orderID 要取消的订单的 ID
     * @return 如果订单取消成功返回 true，否则返回 false（如订单不存在、没有权限等情况）
     */
    public boolean cancelOrder(long orderID) {
        if(!_account.isAllow(AccountLevel.USER)){// 只有普通用户权限及以上才能取消订单
            return false; // 没有权限取消订单
        }
        var Order = OrderManager.getInstance().getByID(orderID);
        if(Order == null) {
            return false; 
        }
        Order.setState(orders.OrderState.CANCELED);
        OrderManager.getInstance().update(Order);
        return true;
    }

    /**
     * 尝试完成一个订单，只有当当前登录账户的权限级别高于或等于普通用户权限级别时才允许完成订单。
     * @param orderID 要完成的订单的 ID
     * @return 如果订单完成成功返回 true，否则返回 false（如订单不存在、没有
     */
    public boolean completeOrder(long orderID) {
        if(!_account.isAllow(AccountLevel.USER)){// 只有普通用户权限及以上才能完成订单
            return false; // 没有权限完成订单
        }
        var order = OrderManager.getInstance().getByID(orderID);
        if(order == null) {
            return false; 
        }
        order.setState(orders.OrderState.FINISHED);
        OrderManager.getInstance().update(order);
        return true;
    }

    /**
     * 尝试删除一个订单，只有当当前登录账户的权限级别高于或等于管理员权限级别时才允许删除订单。
     * @param orderID 要删除的订单的 ID
     * @return 如果订单删除成功返回 true，否则返回 false（如订单不存在、没有权限等情况）
     */
    public boolean deleteOrder(long orderID) {
        if(!_account.isAllow(AccountLevel.ADMIN)){// 只有管理员权限才能删除订单
            return false; // 没有权限删除订单
        }
        var order = OrderManager.getInstance().getByID(orderID);
        if(order == null) {
            return false; 
        }
        return OrderManager.getInstance().remove(order);
    }

    /**
     * 获取所有订单列表，仅当当前登录账户权限为 User 及以上时允许查看。
     * @return 订单列表，若无权限则返回空数组
     */
    public orders.Order[] listAllOrders() {
        if (!_account.isAllow(AccountLevel.USER)) {
            return new orders.Order[0];
        }
        return OrderManager.getInstance().listAll().toArray(new orders.Order[0]);
    }

    /**
     * 根据 ID 获取订单信息，仅当当前登录账户权限为 User 及以上时允许查看。
     * @param orderID 订单 ID
     * @return 订单对象，若无权限或不存在则返回 null
     */
    public orders.Order getOrderByID(long orderID) {
        if (_account.isAllow(AccountLevel.USER)) {
            return OrderManager.getInstance().getByID(orderID);
        }
        return null;
    }

    /**
     * 尝试审核通过一个订单，仅当当前登录账户权限为 Admin 时允许操作。
     * @param orderID 订单 ID
     * @return 如果审核成功返回 true，否则返回 false
     */
    public boolean approveOrder(long orderID) {
        if (!_account.isAllow(AccountLevel.ADMIN)) {
            return false;
        }
        var order = OrderManager.getInstance().getByID(orderID);
        if (order == null) {
            return false;
        }
        order.setState(orders.OrderState.REVIEWED);
        OrderManager.getInstance().update(order);
        return true;
    }

    /**
     * 尝试拒绝一个订单，仅当当前登录账户权限为 Admin 时允许操作。
     * @param orderID 订单 ID
     * @return 如果拒绝成功返回 true，否则返回 false
     */
    public boolean rejectOrder(long orderID) {
        if (!_account.isAllow(AccountLevel.ADMIN)) {
            return false;
        }
        var order = OrderManager.getInstance().getByID(orderID);
        if (order == null) {
            return false;
        }
        order.setState(orders.OrderState.REJECTED);
        OrderManager.getInstance().update(order);
        return true;
    }
}
