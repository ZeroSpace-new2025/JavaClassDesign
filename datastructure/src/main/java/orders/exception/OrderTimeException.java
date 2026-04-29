package orders.exception;

/**
 * 订单时间异常，用于表示订单时间相关的错误（如结束时间早于开始时间）。
 */
public class OrderTimeException extends OrderException {
    /**
     * 构造函数，使用错误消息创建异常。
     * @param message 错误消息
     */
    public OrderTimeException(String message) {
        super(message);
    }
}
