package orders;

/**
 * 订单状态枚举，定义订单在生命周期中的各种状态。
 */
public enum OrderState {
    /** 待确认，订单已创建等待处理 */
    PENDING,
    /** 租赁中，订单已生效正在租赁 */
    RENTING,
    /** 已完成，订单已正常结束 */
    FINISHED,
    /** 已取消，订单已被取消 */
    CANCELED,
    /** 审核中，订单正在等待管理员审核 */
    REVIEWING,
    /** 审核未通过，订单被管理员拒绝 */
    REJECTED,
    /** 已审核，订单已通过管理员审核 */
    REVIEWED
}
