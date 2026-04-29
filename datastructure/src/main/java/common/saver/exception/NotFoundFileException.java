package common.saver.exception;

/**
 * 文件未找到异常，用于表示尝试加载的文件不存在。
 */
public class NotFoundFileException extends LoadException {
    /**
     * 构造函数，使用错误消息创建异常。
     * @param message 错误消息
     */
    public NotFoundFileException(String message) {
        super(message);
    }

    /**
     * 构造函数，使用错误消息和原因创建异常。
     * @param message 错误消息
     * @param cause 异常原因
     */
    public NotFoundFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
