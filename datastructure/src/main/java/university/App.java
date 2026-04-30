package university;

import account.Account;
import account.AccountManager;
import business.LoginAccount;
import common.saver.SaveManager;

/**
 * 应用程序入口类，负责初始化系统组件并启动主界面。
 * 管理全局登录状态，提供单例访问点。
 */
public class App 
{
    LoginAccount account;

    private static App _instance;

    /**
     * 获取 App 的单例实例。
     * @return App 单例实例
     */
    public static App getInstance() {
        if (_instance == null) {
            _instance = new App();
        }
        return _instance;
    }

    private App() {
        account = LoginAccount.getInstance();
        // 尝试自动登录默认管理员账号
        

        var list = AccountManager.getInstance().listAll();
        if(list.isEmpty()) {
            System.out.println("No accounts found.");
        }
        for (Account acc : list) {
            System.out.println("Username: " + acc.getUsername() + ", Level: " + acc.getLevel()+", Password: " + acc.getPassword());
        }
    }

    /**
     * 释放 App 实例，用于重置单例状态。
     */
    public void dispose() {
        _instance = null;
    }

    /**
     * 获取当前登录的账户业务对象。
     * @return 当前登录的 LoginAccount 实例，未登录时返回空实例
     */
    public LoginAccount getLogInAccount() {
        return account;
    }
    
    /**
     * 设置当前登录的账户业务对象。
     * @param account 要设置的 LoginAccount 实例，传 null 表示登出
     */
    public void setLogInAccount(LoginAccount account) {
        this.account = account;
    }
    
    /**
     * 程序入口方法，初始化保存管理器、账户管理器并启动主界面。
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SaveManager.getInstance();
        AccountManager.getInstance();

        // 启动主界面
        visitable.MainFrame.getInstance().init();
        //to be continued
        
    }
}
