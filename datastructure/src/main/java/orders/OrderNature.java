package orders;

/**
 * 订单性质枚举，定义订单的类型。
 */
public enum OrderNature {
    /** 租赁订单，直接开始租赁 */
    RENT,
    /** 预订订单，提前预订车辆 */
    BOOKING
}
