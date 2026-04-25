package orders;

public enum OrderState {
    PENDING,   // 待确认
    RENTING,   // 租赁中
    FINISHED,  // 已完成
    CANCELED,   // 已取消
    REVIEWING,   //审核中
    REJECTED,   //审核未通过
}
