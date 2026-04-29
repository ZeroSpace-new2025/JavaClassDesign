package common.saver;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import common.saver.exception.LoadException;
import common.saver.exception.NotFoundFileException;


/**
 * DataSaveManager 是一个工具类，负责处理具体的数据保存和加载操作。
 * 它使用 Gson 库将数据对象转换为 JSON 格式，并将其写入文件，或从文件中读取 JSON 并转换回数据对象。
 */
 class DataSaveManager {

    /** Gson 实例，用于 JSON 序列化和反序列化。 */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 保存数据到文件。
     * @param name 数据名称
     * @param data 数据对象
     * @param id   数据ID
     * @param saveFile 保存文件对象
     * @return 是否保存成功
     */
    public static <T> boolean save(String name, T data,int id,Path savePath) 
    {
        System.out.println("最终保存文件路径：" + savePath.toFile().getAbsolutePath());
        String json = GSON.toJson(data);
        try
        {
        Files.writeString(savePath, json, java.nio.charset.StandardCharsets.UTF_8);
        }
        catch(IOException e)
        {
            return false;
        }
        return true;
    }

    /**
     * 从文件加载数据。
     * 
     * @param savePath 保存文件路径
     * @param type 数据对象的类类型
     * @return 加载的数据对象
     */
    public static Object load(Path savePath, Type type) throws LoadException
    {
        if (!Files.exists(savePath)) {
            throw new NotFoundFileException("Path " + savePath + " not found.");
        }
        try
        {
            String json = Files.readString(savePath, java.nio.charset.StandardCharsets.UTF_8);
            if (json == null || json.trim().isEmpty()) {
                return null;
            }
            return GSON.fromJson(json, type);
        }
        catch(com.google.gson.JsonSyntaxException e)
        {
            throw new LoadException("JSON 格式错误: " + savePath, e);
        }
        catch(IOException e)
        {
            throw new LoadException("Failed to load data from " + savePath, e);
        }
    }
}