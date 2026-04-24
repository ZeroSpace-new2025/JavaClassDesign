package account;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import common.saver.SaveManager;
import common.saver.exception.LoadException;

/**
 * AccountSaveManager 是一个单例类，负责管理账户数据的保存和加载。
 * 它使用 SaveManager 来处理具体的数据保存和加载操作。
 */
public class AccountManager {

    /** 初始化账户保存管理器，并注册 JVM 关闭钩子以确保在程序退出时保存账户数据。 */
    private AccountManager() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveAccounts();
        }));
    }

    /** AccountSaveManager 的单例实例。 */
    private static AccountManager _instance;

    /** 返回 AccountSaveManager 的单例实例。 */
    public static AccountManager getInstance() {
        if (_instance == null) {
            _instance = new AccountManager();
            _instance.loadAccounts();
        }
        return _instance;
    }

    public static void dispose() {
        _instance = null;
    }

    /** 存储账户数据的映射，键为账户ID，值为 Account 对象。 */
    private final  Map<Long, Account> accounts =new HashMap<>();

    /** 加载账户数据，从文件中读取账户列表并将其存储在 accounts 映射中。 */
    @SuppressWarnings("unchecked")
    public void loadAccounts() {
        accounts.clear();
        List<Account> accountList;
        Type accountListType = new TypeToken<List<Account>>() {}.getType();
        Object loadedData = null;
        try{
            loadedData = SaveManager.getInstance().load("account", 0, accountListType);
        }
        catch(LoadException e)
        {
            accountList = new ArrayList<>();
            accountList.add(new Account("admin", "admin", AccountLevel.ADMIN));
        }
        if (loadedData instanceof List<?>) {
            accountList = (List<Account>) loadedData;
        }else{
            accountList = new ArrayList<>();
            accountList.add(new Account("admin", "admin", AccountLevel.ADMIN));
        }
        for (Account account : accountList) {
            accounts.put(account.getID(), account);
        }
    }
    
    /** 保存账户数据，将 accounts 映射中的账户列表保存到文件中。 */
    public void saveAccounts() {
        List<Account> accountList = new ArrayList<>(accounts.values());
        SaveManager.getInstance().save("account", accountList, 0);
    }

    /** 
     * 注册新账户，如果用户名已存在则返回 false，否则创建新账户并保存数据。 
     * @param username 用户名
     * @param password 密码
     * @param level 账户级别
     * @return 如果注册成功返回 true，否则返回 false
     */
    public boolean registerAccount(String username, String password, AccountLevel level) {
        if (accounts.containsKey(Account.getRawID(username))) {
            return false; // 用户名已存在
        }
        Account newAccount = new Account(username, password, level);
        accounts.put(newAccount.getID(), newAccount);
        saveAccounts(); // 保存账户数据
        return true;
    }

    /** 
     * 删除账户，根据用户名删除对应的账户信息
     * @param username 用户名
     */
    public void  deleteAccount(String username) {
        if (!accounts.containsKey(Account.getRawID(username))) {
            return ;
        }
        accounts.remove(Account.getRawID(username));
        saveAccounts(); 
    }

    /**
     * 根据用户名获取账户信息，如果账户不存在则返回 null
     * @param username 用户名
     * @return 返回对应的账户信息，如果账户不存在则返回 null
     */
    public Account getAccount(String username) {
        return accounts.get(Account.getRawID(username));
    }

    /**
     * 尝试登录，根据用户名和密码验证账户信息，如果验证成功返回 true，否则返回 false
     * @param username 用户名
     * @param password 密码
     * @return 如果登录成功返回 true，否则返回 false
     */
    public boolean tryLogin(String username, String password) {
        Account account = accounts.get(Account.getRawID(username));
        return (account != null && account.getPassword().equals(password)); 
    }

    /**
     * 检查账户是否具有足够的权限，根据账户 ID 和所需的权限级别进行验证，如果账户存在且权限级别满足要求则返回 true，否则返回 false
     * @param ID 账户 ID
     * @param requiredLevel 所需的权限级别
     * @return 如果账户存在且权限级别满足要求则返回 true，否则返回 false
     */
    public boolean IsAllow(long ID, AccountLevel requiredLevel) {
        Account account = accounts.get(ID);
        return (account != null && account.getLevel().getLevel() >= requiredLevel.getLevel());
    }
    
}