package account;

/**
 * 账户权限级别枚举，定义系统中用户的三种权限等级。
 */
public enum AccountLevel {
    /** 游客权限，最低权限级别 */
    GUEST(0),
    /** 普通用户权限 */
    USER(1),
    /** 管理员权限，最高权限级别 */
    ADMIN(25565);

    private final int level;

    /**
     * 构造函数，初始化权限级别的数值。
     * @param level 权限级别数值
     */
    AccountLevel(int level) {
        this.level = level;
    }

    /**
     * 获取权限级别的数值，数值越大权限越高。
     * @return 权限级别数值
     */
    public int getLevel() {
        return level;
    }
}
