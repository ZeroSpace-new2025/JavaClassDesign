package cars;

/**
 * 车辆状态枚举，定义车辆在系统中的各种状态。
 */
public enum CarState {
    /** 已出租，车辆正在被租赁 */
    ON_RENT,
    /** 维护中，车辆正在维修或保养 */
    ON_REPAIR,
    /** 可租，车辆处于空闲状态可供租赁 */
    AVAILABLE,
    /** 预订中，车辆已被预订但尚未开始租赁 */
    ON_BOOKING
}
