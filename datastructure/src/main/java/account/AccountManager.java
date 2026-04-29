package account;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;

/**
 * AccountManager 是一个单例类，负责管理账户信息。它继承自 AbstractManager<Account>，提供了账户的增删改查功能。
 * 该类使用一个 HashMap 来存储账户对象，键为账户的唯一 ID，值为账户对象。
 * 通过 getInstance() 方法获取单例实例，确保全局只有一个 AccountManager 对象。
 */
public class AccountManager extends ManagerBasic.AbstractManager<Account> {

    /** 单例实例。 */
    private static AccountManager instance = null;

    /** 私有构造函数，防止外部实例化。 */
    private AccountManager() {
        super();
        init(0);
    }

    /** 获取单例实例。 */
    public static AccountManager getInstance() {
        if (instance == null) {
            instance = new AccountManager();
        }
        return instance;
    }

    @Override
    public List<Account> defaultList() {
        List<Account> defaultAccounts = new java.util.ArrayList<>();
        defaultAccounts.add(new Account("admin", "admin123", AccountLevel.ADMIN));
        defaultAccounts.add(new Account("user1", "password1", AccountLevel.USER));
        defaultAccounts.add(new Account("user2", "password2", AccountLevel.USER));
        return defaultAccounts;
    }

    @Override
    public String getTypeName() {
        return "account";
    }

    /**
    * 根据用户名获取账户对象。
    * 策略1：通过 computed ID 查找
    * 策略2（降级）：遍历账户列表按用户名直接匹配
    * @param username 用户名
    * @return 返回对应的账户对象，如果不存在则返回 null。
    */
    public Account getByUsername(String username) {
        long id = Account.getRawID(username);
        Account account = getByID(id);
        
        if (account == null) {
            for (Account acc : listAll()) {
                if (acc.getUsername().equals(username)) {
                    return acc;
                }
            }
        }
        
        return account;
    }

    /**
     * 根据用户名删除账户对象。
     * @param username 用户名
     * @return 如果删除成功返回 true，如果账户不存在返回 false。
     */
    public boolean removeByUsername(String username) {
        long id = Account.getRawID(username);
        Account account = getByID(id);
        if (account == null) {
            return false;
        }
        return remove(account);
    }

    /**
     * 根据用户名更新账户对象。
     * @param username 用户名
     * @param newAccount 新的账户对象，必须与用户名对应的 ID 匹配。
     * @return 如果更新成功返回 true，如果账户不存在或 ID 不匹配返回 false。
     */
    public boolean updateByUsername(String username, Account newAccount) {
        long id = Account.getRawID(username);
        if (id != newAccount.getID()) {
            return false; // ID 不匹配，更新失败
        }
        return update(newAccount);
    }

    @Override
    public boolean add(Account account) {
        boolean success = super.add(account);
        if (success) {
            save(0);
        }
        return success;
    }

    @Override
    public boolean remove(Account account) {
        boolean success = super.remove(account);
        if (success) {
            save(0);
        }
        return success;
    }

    @Override
    public boolean update(Account account) {
        boolean success = super.update(account);
        if (success) {
            save(0);
        }
        return success;
    }

    /** 检查是否存在指定用户名的账户。
     * @param username 用户名
     * @return 如果存在返回 true，否则返回 false。
     */
    public boolean containsUsername(String username) {
        long id = Account.getRawID(username);
        return containsID(id);
    }

    /** 尝试登录，验证用户名和密码。
     * @param username 用户名
     * @param password 密码
     * @return 如果登录成功返回 0，如果用户不存在返回 1，如果密码错误返回 2。
     */
    public int trylogin(String username, String password) {
        Account account = getByUsername(username);
        if (account == null) {
            return 1; // 用户不存在
        }
        if(!account.getPassword().equals(password))return 2;
        return 0;
    }

    @Override
    public Type getManagerType() {
        return new TypeToken<Account>(){}.getType();
    }
}
