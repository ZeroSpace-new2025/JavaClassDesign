package orders.exception;

/**
 * 订单异常基类，用于表示订单操作中的通用异常。
 */
public class OrderException extends Exception {
    /**
     * 构造函数，使用错误消息创建异常。
     * @param message 错误消息
     */
    public OrderException(String message) {
        super(message);
    }
}
