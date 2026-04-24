package university;

import account.AccountManager;
import common.saver.SaveManager;

public class App 
{
    public static void main(String[] args) {
        SaveManager.getInstance();
        AccountManager.getInstance();

        //to be continued
        
    }
}
