package ManagerBasic;

import java.util.List;

/**
 * 基础管理接口，定义了基本的增删改查操作
 * 
 * @param <T> 管理的对象类型
 */
public interface BaseManager<T> {

    /**
     * 添加一个对象到管理器中
     * 
     * @param item 要添加的对象
     * @return 如果添加成功返回 true，否则返回 false
     */
    public boolean add(T item);

    /**
     * 从管理器中删除一个对象
     * 
     * @param item 要删除的对象
     * @return 如果删除成功返回 true，否则返回 false
     */
    public boolean remove(T item);

    /**
     * 根据 ID 从管理器中获取一个对象
     * 
     * @param ID 要获取的对象的 ID
     * @return 如果找到对象返回该对象，否则返回 null
     */
    public T getByID(long ID);

    /**
     * 更新管理器中的一个对象
     * 
     * @param item 要更新的对象
     * @return 如果更新成功返回 true，否则返回 false
     */
    public boolean update(T item);

    /**
     * 检查管理器中是否包含一个对象
     * 
     * @param ID 要检查的对象的 ID
     * @return 如果管理器中包含该对象返回 true，否则返回 false
     */
    public boolean containsID(long ID);

    /**
     * 检查管理器中是否包含一个对象
     * 
     * @param item 要检查的对象
     * @return 如果管理器中包含该对象返回 true，否则返回 false
     */
    public boolean contains(T item);

    /**
     * 清空管理器中的所有对象
     */
    public void clear();

    /**
     * 获取管理器中所有对象的列表
     * 
     * @return 管理器中所有对象的列表
     */
    public List<T> listAll();

}
