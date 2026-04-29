package ManagerBasic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.google.gson.*;

import common.saver.SaveManager;
import common.saver.exception.LoadException;

/**
 * AbstractManager 是一个抽象类，提供了基本的增删改查（CRUD）操作的实现。
 * 它使用一个 HashMap 来存储数据项，键为数据项的 ID，值为数据项对象。
 * 该类实现了 BaseManager 接口，并且要求泛型类型 T 必须实现 ManagedData 接口，以确保每个数据项都有一个唯一的 ID。
 */
public abstract class AbstractManager<T extends ManagedData> implements BaseManager<T>
{
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    /** 使用 HashMap 存储数据项，键为数据项的 ID，值为数据项对象。*/
    protected HashMap<Long, T> items = new HashMap<>(); 

    private volatile boolean isInited = false;

    /** 构造函数，初始化数据存储结构。 */
    protected AbstractManager() {
    }

    /**
     * 初始化方法，加载数据并注册 JVM 关闭钩子以保存数据。该方法确保只会被调用一次。
     * @param id 用于区分不同数据类型的标识符，例如账户 ID 或者订单 ID。
     */
    protected void init(int id){
        if(isInited) return;
        isInited = true;
        load(id);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            save(id);
        }));
    }

     /**
     * 添加一个数据项，如果数据项为 null 或者 ID 已经存在，则返回 false。
     * @param item 要添加的数据项
     * @return 如果添加成功返回 true，否则返回 false。
     */
    @Override
    public boolean add(T item){
        if(item == null || items.containsKey(item.getID())){
            return false;
        }
        items.put(item.getID(), item);
        return true;
    }

    @Override
    public boolean remove(T item){
        if(item == null || !items.containsKey(item.getID())){
            return false;
        }
        items.remove(item.getID());
        return true;
    }

    @Override
    public T getByID(long ID){
        return items.get(ID);
    }

    @Override
    public boolean update(T item){
        if(item == null || !items.containsKey(item.getID())){
            return false;
        }
        items.put(item.getID(), item);
        return true;
    }

    @Override
    public boolean containsID(long ID){
        return items.containsKey(ID);
    }
    
    @Override
    public boolean contains(T item){
        if(item == null){
            return false;
        }
        return items.containsKey(item.getID());
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public List<T> listAll() {
        return List.copyOf(items.values());
    }

    /**
     * 获取管理器中数据项的数量。
     * @return 数据项数量
     */
    public int size() {
        return items.size();
    }

    /**
     * 加载数据，从持久化存储中读取数据并填充到内存中。
     */
    @SuppressWarnings("unchecked")
    public void load(int id){
        clear();
        List<T> dataList;

        Class<T> actualType;
        try {
            actualType = (Class<T>) getManagerType();
        } catch (ClassCastException e) {
            System.out.println("无法获取泛型类型参数，使用默认数据列表。");
            dataList = defaultList();
            if (dataList == null) {
                dataList = new ArrayList<>();
            }
            for (T data : dataList) {
                if (data == null) continue;
                items.put(data.getID(), data);
            }
            return;
        }
        
        Type dataListType = new TypeToken<List<T>>() {}.getType();
        try {
            Object loadedData = SaveManager.getInstance().load(getTypeName(), id, dataListType);
        
            if (loadedData == null) {
                System.out.println("未加载到数据，使用默认数据列表。");
                dataList = defaultList();
            } else if (!(loadedData instanceof List<?>)) {
                System.out.println("加载的数据格式不正确，预期为 List<T>，使用默认数据。");
                dataList = defaultList();
            } else {
                List<?> rawList = (List<?>) loadedData;
                dataList = new ArrayList<>();
                for (Object obj : rawList) {
                    try {
                        T realData = gson.fromJson(gson.toJson(obj), actualType);
                        dataList.add(realData);
                    } catch (Exception e) {
                        System.out.println("反序列化数据项失败: " + e.getMessage());
                    }
                }
            }
        
        } catch (LoadException e) {
            System.out.println("加载数据失败: " + e.getMessage());
            System.out.println("使用默认数据列表。");
            dataList = defaultList();
        } catch (Exception e) {
            System.out.println("加载数据时发生未知错误: " + e.getMessage());
            dataList = defaultList();
        }
        
        if (dataList == null || dataList.isEmpty()) {
            dataList = defaultList();
        }
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        
        for (T data : dataList) {
            if (data == null) continue;
            items.put(data.getID(), data);
        }
    }

    /**
     * 保存数据，将内存中的数据持久化到存储中。
     */
    public void save(int id){
        List<T> accountList = new ArrayList<>(items.values());
        SaveManager.getInstance().save(getTypeName(), accountList, id);
    }

    /**
     * 提供一个默认数据列表的方法，子类需要实现该方法以提供初始数据。
     * @return 返回一个包含默认数据的列表，如果没有默认数据可以返回一个空列表。
     */
    public abstract List<T> defaultList();

    /**
     * 获取数据类型的名称，子类需要实现该方法以返回对应的数据类型名称，例如 "account"、"car" 或者 "order"。
     * @return 返回数据类型的名称。
     */
    public abstract String getTypeName();

    /**
     * 获取 Gson 反序列化时使用的类型信息。
     * 子类必须实现该方法以返回正确的泛型类型。
     * @return 泛型类型 T 的 Type 对象
     */
    public abstract Type getManagerType();
}
