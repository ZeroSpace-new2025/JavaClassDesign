package cars;

import lombok.Data;

/**
 * 车辆类，包含车辆的基本信息和状态
 */
@Data
public class Car {
    /** 车辆的名称 */
    private String name;
    /** 车辆的颜色 */
    private String color;
    /** 车辆的价格,单位为元\天*/
    private int price;
    /** 车辆的状态 */
    private CarState state;
    /** 车辆的ID */
    private long ID;

    /** 车辆的型号  */
    private String model;

    /** 
     * 构造函数，初始化车辆信息
     * @param name 车辆名称
     * @param color 车辆颜色
     * @param price 车辆价格
     * @param state 车辆状态
     * @param ID 车辆ID
     * @param model 车辆型号
     */
    public Car(String name, String color, int price, CarState state, long ID, String model) {
        this.name = name;
        this.color = color;
        this.price = price;
        this.state = state;
        this.ID = ID;
        this.model = model;
    }
    
    /** 默认构造函数，供 JSON 反序列化使用 */
    public Car() {
    }
}
