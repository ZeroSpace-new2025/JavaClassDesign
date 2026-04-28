package business;

import account.AccountLevel;
import cars.Car;
import cars.CarManager;
import orders.OrderManager;

public class CarsBusiness {
    private LoginAccount _account = new LoginAccount();

    public CarsBusiness(LoginAccount account) {
        _account = account;
    }

    /**
     * 尝试添加一辆新车，只有当当前登录账户的权限级别高于或等于管理员权限级别时才允许添加。
     * @param car 要添加的汽车对象
     * @return 如果添加成功返回 true，否则返回 false
     */
    public boolean addCar(Car car) {
        if(car == null) {
            return false;
        }
        if(_account.isAllow(AccountLevel.ADMIN)){// 只有管理员权限才能添加汽车
            return CarManager.getInstance().add(car); 
        }
        return false;
    }
    
    /**
     * 尝试删除一辆汽车，只有当当前登录账户的权限级别高于或等于管理员权限级别时才允许删除。
     * @param carID 要删除的汽车的 ID
     * @return 如果删除成功返回 true，否则返回 false
     */
    public boolean removeCar(long carID) {
        if(_account.isAllow(AccountLevel.ADMIN)){ // 只有管理员权限才能删除汽车
            var car = CarManager.getInstance().getByID(carID);
            if(car == null) {
                return false;
            }

            if(OrderManager.getInstance().getOrdersByCarID(carID).size()>0){
                return false; // 不能删除有订单的汽车
            }

            return CarManager.getInstance().remove(car);
        }
        return false;
    }

    /**
     * 强制删除一辆汽车，删除汽车的同时删除所有与该汽车相关的订单，只有当当前登录账户的权限级别高于或等于管理员权限级别时才允许删除。
     * @param carID 要删除的汽车的 ID
     * @return 如果删除成功返回 true，否则返回 false
     */
    public boolean removeCarForse(long carID) {
        if(_account.isAllow(AccountLevel.ADMIN)){ // 只有管理员权限才能删除汽车
            var car = CarManager.getInstance().getByID(carID);
            if(car == null) {
                return false;
            }

            OrderManager.getInstance().getOrdersByCarID(carID)
                .forEach(order -> OrderManager.getInstance().remove(order));

            return CarManager.getInstance().remove(car);
        }
        return false;
    }

    /**
     * 尝试更新一辆汽车的信息，只有当当前登录账户的权限级别高于或等于管理员权限级别时才允许更新。
     * @param car 要更新的汽车对象，必须包含有效的 ID
     * @return 如果更新成功返回 true，否则返回 false
     */
    public boolean updateCar(Car car) {
        if(car == null) {
            return false; 
        }
        if(_account.isAllow(AccountLevel.ADMIN)){// 只有管理员权限才能更新汽车
            return CarManager.getInstance().update(car); 
        }
        return false;
    }

    /**
     * 尝试根据 ID 获取一辆汽车的信息，只有当当前登录账户的权限级别高于或等于游客权限级别时才允许查看。
     * @param carID 要获取的汽车的 ID
     * @return 如果当前登录账户的权限级别高于或等于游客权限级别返回汽车对象，否则返回 null
     */
    public Car getCarByID(long carID) {
        if(_account.isAllow(AccountLevel.GUEST)){
            return CarManager.getInstance().getByID(carID);// 只有游客权限及以上才能查看汽车信息
        }
        return null; // 没有权限查看汽车信息
    }

    /**
     * 尝试获取所有汽车的信息，只有当当前登录账户的权限级别高于或等于游客权限级别时才允许查看。
     * @return 如果当前登录账户的权限级别高于或等于游客权限级别返回汽车对象数组，否则返回空数组
     */
    public Car[] listAllCars() {
        if(_account.isAllow(AccountLevel.GUEST)){// 只有游客权限及以上才能查看汽车信息
            Car[] cars = new Car[CarManager.getInstance().size()];
            return CarManager.getInstance().listAll().toArray(cars); 
        }
        return new Car[0];
    }
}
