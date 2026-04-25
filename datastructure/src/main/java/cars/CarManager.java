
package cars;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import ManagerBasic.AbstractManager;
import account.AccountLevel;
import common.saver.SaveManager;
import common.saver.exception.LoadException;

/**
 * 车辆管理类，负责管理车辆的添加、查询等操作
 */
public class CarManager extends AbstractManager<Car> {
    /** 单例模式的实例（复用父类 _instance，此处仅为兼容原有命名） */
    private static CarManager _instance;

    /** 私有构造函数，注册 JVM 关闭钩子以确保在程序退出时保存车辆数据 */
    private CarManager() {
        super();
        Runtime.getRuntime().addShutdownHook(new Thread(this::save));
        // 初始化时加载数据
        load();
    }

    /**
     * 实现父类的抽象方法：创建实例（供父类 getInstance 调用）
     */
    @Override
    protected AbstractManager<?> createInstance() {
        if (_instance == null) {
            _instance = new CarManager();
        }
        return _instance;
    }

    /**
     * 兼容原有调用方式的单例获取方法（也可直接用父类 getInstance()）
     */
    public static CarManager getInstance() {
        if (_instance == null) {
           _instance = new CarManager();
        }
        return _instance;
    }

    /** 销毁单例实例，释放资源 */
    public static void dispose() { // 修正原拼写错误 despose -> dispose
        _instance = null;
    }

    // -------------------------- 复用父类通用方法，覆写/扩展特有逻辑 --------------------------
    /**
     * 覆写父类 add 方法，增加数据保存（可选），同时继承权限校验逻辑
     */
    @Override
    public boolean add(Car car) {
        boolean result = super.add(car);
        if (result) {
            save(); // 添加后自动保存（可选）
        }
        return result;
    }

    /**
     * 覆写父类 remove 方法，继承权限校验逻辑
     */
    @Override
    public boolean remove(Car car) {
        boolean result = super.remove(car);
        if (result) {
            save(); // 删除后自动保存（可选）
        }
        return result;
    }

    /**
     * 实现父类抽象方法：定义车辆管理操作所需的权限等级
     */
    @Override
    public int getRequiredPermissionLevel() {
        return AccountLevel.ADMIN.getLevel(); // 仅管理员可操作车辆数据
    }

    // -------------------------- 车辆管理特有业务逻辑 --------------------------
    /** 
     * 获取所有车辆信息，返回一个不可修改的 Map
     * @return 返回一个不可修改的 Map，包含所有车辆信息，键为车辆 ID，值为车辆对象
     *  */
    public Map<Long, Car> getCars() {
        return Collections.unmodifiableMap(items); // 复用父类的 items 容器
    }

    /** 
     * 兼容原有方法名：添加车辆（内部调用父类 add 方法，继承权限校验）
     * @param car 要添加的车辆对象
     *  */
    public void addCar(Car car) {
        add(car); // 复用父类带权限校验的 add 方法
    }

    /** 
     * 兼容原有方法名：根据 ID 获取车辆（父类已有 getByID，此处为兼容）
     * @param ID 车辆 ID
     * @return 返回车辆对象，如果 ID 不存在则返回 null
     *  */
    public Car getCarByID(long ID) {
        return getByID(ID); // 复用父类的 getByID 方法
    }

    /**
     * 根据车辆名称获取车辆信息
     * @param name 车辆名称
     * @return 包含所有名称匹配的车辆 Map
     */
    public Map<Long, Car> getCarByName(String name) {
        Map<Long, Car> result = new HashMap<>();
        for (Map.Entry<Long, Car> entry : items.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * 根据车辆型号获取车辆信息
     * @param model 车辆型号
     * @return 包含所有型号匹配的车辆 Map
     */
    public Map<Long, Car> getCarByModel(String model) {
        Map<Long, Car> result = new HashMap<>();
        for (Map.Entry<Long, Car> entry : items.entrySet()) {
            if (entry.getValue().getModel().equals(model)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * 根据车辆 ID 删除车辆信息（带权限隐含校验：需通过 getAccountLevel 验证）
     * @param ID 车辆 ID
     */
    public void removeCarByID(long ID) {
        Car car = getByID(ID);
        if (car != null) {
            remove(car); // 复用父类带权限校验的 remove 方法
        }
    }

    /** 
     * 更新车辆状态（带权限隐含校验：需通过 getAccountLevel 验证）
     * @param ID 车辆 ID
     * @param newState 新的车辆状态
     */
    public void updateCarState(long ID, CarState newState) {
        // 权限校验（复用父类的 isEnabled 方法）
        if (!isEnabled(getRequiredPermissionLevel(), getAccountLevel())) {
            return;
        }
        Car car = getByID(ID);
        if (car != null) {
            car.setState(newState);
            update(car); // 复用父类的 update 方法更新数据
            save(); // 更新后保存
        }
    }

    /** 
     * 更新车辆价格（带权限隐含校验）
     * @param ID 车辆 ID
     * @param newPrice 新的车辆价格
     *  */
    public void updateCarPrice(long ID, int newPrice) {
        // 权限校验
        if (!isEnabled(getRequiredPermissionLevel(), getAccountLevel())) {
            return;
        }
        Car car = getByID(ID);
        if (car != null) {
            car.setPrice(newPrice);
            update(car); // 复用父类的 update 方法更新数据
            save(); // 更新后保存
        }
    }

    // -------------------------- 数据持久化逻辑（加载/保存） --------------------------
    /** 从文件加载车辆数据 */
    @SuppressWarnings("unchecked") 
    void load() {
        items.clear();
        List<Car> carList;
        Type carListType = new TypeToken<List<Car>>() {}.getType();
        Object loadedData = null;
        try {
            loadedData = SaveManager.getInstance().load("car", 0, carListType);
        } catch (LoadException e) {
            carList = new ArrayList<>();
        }
        if (loadedData instanceof List<?>) {
            carList = (List<Car>) loadedData;
        } else {
            carList = new ArrayList<>();
        }
        for (Car car : carList) {
            items.put(car.getID(), car);
        }
    }

    /** 将车辆数据保存到文件 */
    public void save() {
        List<Car> carList = new ArrayList<>(items.values());
        SaveManager.getInstance().save("car", carList, 0);
    }
}