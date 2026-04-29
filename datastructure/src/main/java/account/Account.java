package account;

import com.google.gson.annotations.SerializedName;

import common.basic.MD5Util;
import lombok.Data;

/**
 * 账户类，表示系统中的一个用户账户。
 * 包含用户名、密码、权限级别和唯一 ID。
 */
@Data
public class Account implements ManagerBasic.ManagedData {

    /** 账户的用户名。 */
    private String username;

    /** 账户的密码，存储为明文（不安全，实际应用中应使用哈希,现作为演示）。 */
    private String password;
    /** 账户的权限级别，使用枚举表示。 */
    private AccountLevel level;

    /** 账户的唯一 ID，根据用户名生成。 */
    @SerializedName("id")
    private long ID;

    /**
     * 构造函数，初始化账户信息。
     * @param username 用户名
     * @param password 密码
     * @param level 权限级别
     */
    public Account(String username, String password ,AccountLevel level) {
        this.username = username;
        this.password = password;
        this.level = level;
        this.ID = getRawID(username);
    }

    /**
     * 默认构造函数，供 JSON 反序列化使用。
     */
    public Account() {
    }

    /** 
     * 获取账户的唯一 ID，根据用户名生成。 
     * @param username 用户名
     * @return 根据用户名 MD5 哈希值生成的唯一 ID
    */
    public static long getRawID(String username) {
        var hash = MD5Util.md5(username);
        return Long.parseUnsignedLong(hash.substring(0, 16), 16);
    }

    /**
     * 获取账户的唯一 ID。
     * @return 账户的唯一 ID
     */
    @Override
    public long getID() {
        return ID;
    }
}