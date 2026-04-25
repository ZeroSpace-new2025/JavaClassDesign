package university;

import account.Account;
import account.AccountLevel;
import account.AccountManager;
import common.saver.SaveManager;

public class App 
{
    Account account;

    private static App _instance;

    public static App getInstance() {
        if (_instance == null) {
            _instance = new App();
        }
        return _instance;
    }

    private App() {
        account = new Account("basic", "basic", AccountLevel.GUEST);
    }

    public void dispose() {
        _instance = null;
    }

    public Account getLogInAccount() {
        return account;
    }
    
    public static void main(String[] args) {
        SaveManager.getInstance();
        AccountManager.getInstance();

        //to be continued
        
    }
}
