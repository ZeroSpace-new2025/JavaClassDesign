package cars;

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
 * 车辆管理类，负责管理车辆的添加、查询等操作
 */
public class CarManager {
    /** 存储车辆信息的 Map，键为车辆 ID，值为车辆对象 */
    private Map<Long, Car> cars = new HashMap<>();
    /** 单例模式的实例 */
    private static CarManager _interence;

    /** 获取车辆管理器的实例，采用单例模式 */
    public static CarManager getInstance() {
        if (_interence == null) {
            _interence = new CarManager();
            _interence.load();
        }
        return _interence;
    }

    /** 销毁单例实例，释放资源 */
    public static void dispose() {
        _interence = null;
    }

    /** 私有构造函数，注册 JVM 关闭钩子以确保在程序退出时保存车辆数据 */
    private CarManager() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            save();
        }));
    }


    /** 获取所有车辆信息，返回一个不可修改的 Map */
    public Map<Long, Car> getCars() {
        return Collections.unmodifiableMap(cars);
    }

    /** 根据车辆 ID 获取车辆信息 */
    public void addCar(Car car) {
        cars.put(car.getID(), car);
    }

    /** 根据车辆 ID 获取车辆信息 */
    public Car getCarByID(long ID) {
        return cars.get(ID);
    }

     /** 
      * 根据车辆名称获取车辆信息，返回一个 Map，键为车辆 ID，值为车辆对象
      * @param name 车辆名称
      * @return 返回一个 Map，包含所有名称匹配的车辆信息，键为车辆 ID，值为车辆对象
      *  */
    public Map<Long, Car> getCarByName(String name) {
        Map<Long, Car> result = new HashMap<>();
        for (Map.Entry<Long, Car> entry : cars.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /** 
     * 根据车辆型号获取车辆信息，返回一个 Map，键为车辆 ID，值为车辆对象 
     * @param model 车辆型号
     * @return 返回一个 Map，包含所有型号匹配的车辆信息，键为车辆 ID，值为车辆对象
     * */
    public Map<Long, Car> getCarByModel(String model) {
        Map<Long, Car> result = new HashMap<>();
        for (Map.Entry<Long, Car> entry : cars.entrySet()) {
            if (entry.getValue().getModel().equals(model)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
    
    public void removeCarByID(long ID) {
        cars.remove(ID);
    }

    public void updateCarState(long ID, CarState newState) {
        Car car = cars.get(ID);
        if (car != null) {
            car.setState(newState);
        }
    }

    public void updateCarPrice(long ID, int newPrice) {
        Car car = cars.get(ID);
        if (car != null) {
            car.setPrice(newPrice);
        }
    }

    /** 
     * 从保存的数据中加载车辆信息，清空当前的车辆 Map，并将加载的数据添加到 Map 中。
     */
    @SuppressWarnings("unchecked")
    private void load() {
        cars.clear();
        List<Car> carList;
        Type carListType = new TypeToken<List<Car>>() {}.getType();
        Object loadedData = null;
        try{
            loadedData = SaveManager.getInstance().load("car", 0, carListType);
        }
        catch(LoadException e)
        {
            carList = new ArrayList<>();
        }
        if (loadedData instanceof List<?>) {
            carList = (List<Car>) loadedData;
        }else{
            carList = new ArrayList<>();
        }
        for (Car car : carList) {
            cars.put(car.getID(), car);
        }
    }

    /** 
     * 将当前车辆信息保存到文件中，将车辆 Map 中的车辆列表保存到文件中。
     */
    private void save() {
        List<Car> carList = new ArrayList<>(cars.values());
        SaveManager.getInstance().save("car", carList, 0);
    }
}