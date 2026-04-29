package common.saver.exception;

/**
 * 加载异常，用于表示数据加载过程中发生的错误。
 */
public class LoadException extends Exception {
    /**
     * 构造函数，使用错误消息创建异常。
     * @param message 错误消息
     */
    public LoadException(String message) {
        super(message);
    }

    /**
     * 构造函数，使用错误消息和原因创建异常。
     * @param message 错误消息
     * @param cause 异常原因
     */
    public LoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
