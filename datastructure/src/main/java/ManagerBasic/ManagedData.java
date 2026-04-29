package ManagerBasic;

/**
 * 可管理数据接口，所有需要被 Manager 管理的数据类都必须实现此接口。
 * 实现该接口的类必须提供一个唯一的 long 类型 ID。
 */
public interface ManagedData {
    /**
     * 获取数据项的唯一 ID。
     * @return 数据项的唯一 ID
     */
    long getID();
}
