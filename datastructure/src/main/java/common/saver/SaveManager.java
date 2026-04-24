package common.saver;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;

import common.saver.exception.LoadException;


/**
 * SaveManager 是一个单例类，负责管理游戏数据的保存和加载。
 * 它使用 DataSaveManager 来处理具体的数据保存和加载操作。
 */
public class SaveManager {

    private static final String SAVE_DIR = "saves";

    private static SaveManager _instance;

    private String savePath = "";

    /**
     * 返回 SaveManager 的单例实例。
     * 
     * @return SaveManager 的单例实例
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static SaveManager getInstance() {
        SaveManager instance = SaveManager._instance;
        if (instance == null) {
            try
            {
                instance = new SaveManager();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        return instance;
    }

    
    /** 初始化保存管理器。 */
    private SaveManager() throws IOException {
        File rootDir = new File("");
        File saveDir = new File(rootDir, SAVE_DIR);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        this.savePath = saveDir.getAbsolutePath();
    }

    /** 保存数据到文件。
     * 
     * @param name 数据名称
     * @param data 数据对象
     * @param id   数据ID
     * @return 是否保存成功
     */
    public <T> boolean save(String name, T data,int id) 
    {
        return DataSaveManager.save(name, data, id, getSavePath(name, id));
    }

    /**
     * 从文件加载数据。 
     * @param <T> 数据对象的类型
     * @param name 数据名称
     * @param id 数据ID
     * @param clazz 数据对象的类类型
     * @return 加载的数据对象，如果加载失败则返回 null
     */
    public Object load(String name,int id, Type clazz) throws LoadException
    {
        return DataSaveManager.load(getSavePath(name, id), clazz);
    }

    /**
     * 获取保存文件的路径。
     * 
     * @param name 数据名称
     * @param id   数据ID
     * @return 保存文件的路径
     */
    private Path getSavePath(String name, int id) {
        String fileName = String.format("%s_%d.json", name, id);
        return new File(savePath, fileName).toPath();
    }

    /** 
     * 使用完整路径保存数据。 
     * @param <T> 数据对象的类型
     * @param name 数据名称
     * @param data 数据对象
     * @param id 数据ID
     * @param savePath 保存文件的完整路径
     * @return 是否保存成功
     */
    public <T> boolean saveByFullPath(String name, T data,int id,Path savePath) {
        return DataSaveManager.save(name, data, id, savePath);
    }

    /** 
     * 使用完整路径加载数据。 
     * @param name 数据名称
     * @param id 数据ID
     * @param clazz 数据对象的类类型
     * @param savePath 保存文件的完整路径
     * @return 加载的数据对象，如果加载失败则返回 null
     */
    public Object loadByFullPath(String name,int id, Type clazz,Path savePath) throws LoadException {
        return DataSaveManager.load(savePath, clazz);
    }
}