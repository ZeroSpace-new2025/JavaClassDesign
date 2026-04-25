package ManagerBasic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import university.App;

public abstract class AbstractManager<T extends ManagedData> implements BaseManager<T>
{

    protected Map<Long, T> items = new HashMap<>(); 

    /** 私有构造函数，防止外部实例化 */
    protected AbstractManager() {
    }

    public static AbstractManager<?> getInstance() {
        throw new UnsupportedOperationException("请使用具体的子类实现 getInstance 方法");
    }
    /**
     * 创建管理器的实例
     * 
     * @return 管理器的实例
     */
    protected abstract AbstractManager<?> createInstance();

    @Override
    public boolean add(T item) {
        if (!isEnabled(getRequiredPermissionLevel(),getAccountLevel())) {
            return false;
        }
        items.put(item.getID(), item);
        return true;
    }

    @Override
    public boolean remove(T item) {
        if (!isEnabled(getRequiredPermissionLevel(),getAccountLevel())) {
            return false;
        }
        items.remove(item.getID());
        return true;
    }

    @Override
    public T getByID(long ID) {
        return items.get(ID);
    }

    @Override
    public boolean update(T item) {
        if (!isEnabled(getRequiredPermissionLevel(),getAccountLevel())) {
            return false;
        }
        items.put(item.getID(), item);
        return true;
    }

    @Override
    public boolean containsID(long ID) {
        return items.containsKey(ID);
    }

    @Override
    public boolean contains(T item) {
        return items.containsKey(item.getID());
    }

    @Override
    public void clear() {
        if (!isEnabled(getRequiredPermissionLevel(),getAccountLevel())) {
            return;
        }
        items.clear();
    }

    @Override
    public List<T> ListAll() {
        return new ArrayList<>(items.values());
    }


    /**
     * 检查当前账户是否有权限执行某个操作
     * 
     * @param operationlevel 操作所需的权限等级
     * @param accountlevel 当前账户的权限等级
     * @return 如果账户权限等级大于或等于操作所需的权限等级返回 true，否则返回 false
     */
    public boolean isEnabled(int operationlevel ,int accountlevel) {
        return accountlevel >= operationlevel;
    }

    /**
     * 获取当前账户的权限等级
     * 
     * @return 当前账户的权限等级
     */
    public int getAccountLevel() {
        return App.getInstance().getLogInAccount().getLevel().getLevel();
    }

    /**
     * 获取执行某个操作所需的权限等级
      * @return 执行某个操作所需的权限等级
      */
    public abstract int getRequiredPermissionLevel();
}
