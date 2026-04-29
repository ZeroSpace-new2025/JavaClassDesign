package account;

import common.basic.MD5Util;
import lombok.Data;

@Data
public class Account implements ManagerBasic.ManagedData {

    /** 账户的用户名。 */
    private String username;

    /** 账户的密码，存储为明文（不安全，实际应用中应使用哈希,现作为演示）。 */
    private String password;
    /** 账户的权限级别，使用枚举表示。 */
    private AccountLevel level;

    /** 账户的唯一 ID，根据用户名生成。 */
    private long ID;

    /** 构造函数，初始化账户信息。 */
    public Account(String username, String password ,AccountLevel level) {
        this.username = username;
        this.password = password;
        this.level = level;
        this.ID = getRawID(username);
    }

    /** 默认构造函数，供 JSON 反序列化使用。 */
    public Account() {
    }

    /** 
     * 获取账户的唯一 ID，根据用户名生成。 
     * @param username 用户名
    */
    public static long getRawID(String username) {
        var hash = MD5Util.md5(username);
        return Long.parseUnsignedLong(hash.substring(0, 16), 16);
    }

    @Override
    public long getID() {
        return ID;
    }
}