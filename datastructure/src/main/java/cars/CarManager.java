package cars;

import java.lang.reflect.Type;
import java.util.List;

/**
 * CarManager 是一个单例类，负责管理车辆信息。它继承自 AbstractManager<Car>，提供了车辆的增删改查功能。
 * 该类使用一个 HashMap 来存储车辆对象，键为车辆的唯一 ID，值为车辆对象。
 * 通过 getInstance() 方法获取单例实例，确保全局只有一个 CarManager 对象。
 */
public class CarManager extends ManagerBasic.AbstractManager<Car> {

    /** 单例实例。 */
    private static CarManager instance = null;

    /** 私有构造函数，防止外部实例化。 */
    private CarManager() {
        super();
        try{
            init(0);
        }catch(Exception e){
            System.out.println("CarManager 初始化失败。");
            e.printStackTrace();
        }
    }

    @Override
    public List<Car> defaultList() {
        List<Car> defaultCars = new java.util.ArrayList<>();
        defaultCars.add(new Car("Toyota Camry","Red" ,300 ,CarState.AVAILABLE , 20000, "Sedan"));
        defaultCars.add(new Car("Honda Accord","Blue" ,280 ,CarState.AVAILABLE , 20001, "SUV"));
        defaultCars.add(new Car("Ford Mustang","Black" ,500 ,CarState.AVAILABLE , 20002, "Coupe"));
        return defaultCars;
    }

    @Override
    public String getTypeName() {
        return "car";
    }

    /** 获取单例实例。 */
    public static CarManager getInstance() {
        if (instance == null) {
            instance = new CarManager();
        }
        return instance;
    }
    
    /**
     * 根据车辆名称获取车辆对象。
     * @param name 车辆名称
     * @return 返回对应的车辆对象，如果不存在则返回 null。
     */
    public Car getByName(String name) {
        for (Car car : items.values()) {
            if (car.getName().equals(name)) {
                return car;
            }
        }
        return null;
    }

    /**
     * 根据车辆型号获取车辆对象列表。
     * @param model 车辆型号
     * @return 返回对应的车辆对象列表，如果不存在则返回空列表。
     */
    public List<Car> getByModel(String model) {
        List<Car> result = new java.util.ArrayList<>();
        for (Car car : items.values()) {
            if (car.getModel().equals(model)) {
                result.add(car);
            }
        }
        return result;
    }

    /**
     * 根据车辆状态获取车辆对象列表。
     * @param state 车辆状态
     * @return 返回对应的车辆对象列表，如果不存在则返回空列表。
     */
    public List<Car> getByState(CarState state) {
        List<Car> result = new java.util.ArrayList<>();
        for (Car car : items.values()) {
            if (car.getState() == state) {
                result.add(car);
            }
        }
        return result;
    }

    /**
     * 根据价格范围获取车辆对象列表。
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return  返回对应的车辆对象列表，如果不存在则返回空列表。
     */
    public List<Car> getByPriceRange(int minPrice, int maxPrice) {
        List<Car> result = new java.util.ArrayList<>();
        for (Car car : items.values()) {
            if (car.getPrice() >= minPrice && car.getPrice() <= maxPrice) {
                result.add(car);
            }
        }
        return result;
    }

    /**
     * 根据最低价格获取车辆对象列表。
     * @param minPrice 最低价格
     * @return  返回对应的车辆对象列表，如果不存在则返回空列表。
     */
    public List<Car> getByMinPrice(int minPrice) {
        List<Car> result = new java.util.ArrayList<>();
        for (Car car : items.values()) {
            if (car.getPrice() >= minPrice) {
                result.add(car);
            }
        }
        return result;
    }

    /**
     * 根据最高价格获取车辆对象列表。
     * @param maxPrice 最高价格
     * @return 返回对应的车辆对象列表，如果不存在则返回空列表。
     */
    public List<Car> getByMaxPrice(int maxPrice) {
        List<Car> result = new java.util.ArrayList<>();
        for (Car car : items.values()) {
            if (car.getPrice() <= maxPrice) {
                result.add(car);
            }
        }
        return result;
    }

    /**
     * 根据车辆颜色获取车辆对象列表。
     * @param color 车辆颜色
     * @return 返回对应的车辆对象列表，如果不存在则返回空列表。
     */
    public List<Car> getByColor(String color) {
        List<Car> result = new java.util.ArrayList<>();
        for (Car car : items.values()) {
            if (car.getColor().equals(color)) {
                result.add(car);
            }
        }
        return result;
    }

    /**
     * 获取 Gson 反序列化时使用的类型信息。
     * @return Car 类型的 Type 对象
     */
    @Override
    public Type getManagerType() {
        return new com.google.gson.reflect.TypeToken<Car>(){}.getType();
    }   
}
