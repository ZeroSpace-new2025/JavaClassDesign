package business;

import account.*;

public class LoginAccount {





    private Account _account = null;
    /**
     * 用户登录方法，验证用户名和密码是否正确。
     * @param username 用户输入的用户名
     * @param password 用户输入的密码
     * @return 如果登录成功返回 true，否则返回 false
     */
    public boolean login(String username, String password) {
        var account = AccountManager.getInstance().getByID(Account.getRawID(username));
        if (account == null) {
            return false; // 用户不存在
        }
        if( account.getPassword().equals(password)){
            _account = account; // 登录成功，保存账户信息
            return true; // 登录成功
        } // 验证密码
        return false; // 密码错误
    }

    /**
     * 用户登出方法，清除当前登录账户的信息。
     */
    public void logout() {
        _account = null; // 清除当前登录账户信息
    }

    /**
     * 获取当前登录账户的信息。
     * @return 当前登录账户的 Account 对象，如果没有登录则返回 null
     */
    public Account getAccount() {
        return _account;
    }

    /**
     * 尝试添加一个新账户，只有当当前登录账户的权限级别高于或等于新账户的权限级别时才允许添加。
     * @param username 新账户的用户名
     * @param password 新账户的密码
     * @param level 新账户的权限级别
     * @return 如果添加成功返回 true，否则返回 false
     */
    public boolean tryAddAccount(String username, String password, AccountLevel level) {
        var account = AccountManager.getInstance().getByID(Account.getRawID(username));
        if (account != null) {
            return false; // 用户已存在
        }
        
        if(_account == null && level.getLevel() >= AccountLevel.USER.getLevel()) {
            return false; // 没有登录账户，且新账户权限级别不低于普通用户，不允许添加
        }
        else if(_account != null && _account.getLevel().getLevel() < level.getLevel()) {
            return false; // 当前登录账户权限级别低于新账户权限级别，不允许添加
        }

        var newAccount = new Account(username, password, level);
        return AccountManager.getInstance().add(newAccount); // 添加新账户
    }
    
    /**
     * 尝试删除一个账户，只有当当前登录账户的权限级别等于高于管理员权限级别时才允许删除。
     * @param username 要删除的账户的用户名
     * @return 如果删除成功返回 true，否则返回 false
     */
    public boolean tryDeleteAccount(String username) {
        if(_account == null || _account.getLevel().getLevel() < AccountLevel.ADMIN.getLevel()) {
            return false; // 没有登录账户，或当前登录账户权限级别低于管理员，不允许删除账户
        }
        var account = AccountManager.getInstance().getByUsername(username);
        if (account == null) {
            return false; // 用户不存在
        }
        return AccountManager.getInstance().remove(account); // 删除账户
    }

    /**
     * 尝试删除当前登录账户。
     * @return 如果删除成功返回 true，否则返回 false
     */
    public boolean tryDeleteThisAccount(){
        if(_account == null) {
            return false; // 没有登录账户，不允许删除账户
        }
        return AccountManager.getInstance().remove(_account); // 删除当前登录账户
    }

    /**
     * 尝试更新一个账户的密码，只有当当前登录账户的权限级别等于高于管理员权限级别时才允许更新。
     * @param username 
     * @param newPassword
     * @return
     */
    public boolean tryUpdatePassword(String username, String newPassword) {
        if(_account == null || _account.getLevel().getLevel() < AccountLevel.ADMIN.getLevel()) {
            return false; // 没有登录账户，或当前登录账户权限级别低于管理员，不允许更新密码
        }
        var account = AccountManager.getInstance().getByUsername(username);
        if (account == null) {
            return false; // 用户不存在
        }
        account.setPassword(newPassword); // 更新密码
        return AccountManager.getInstance().update(account); // 保存更新后的账户信息
    }

    /**
     * 尝试更新当前登录账户的密码。
     * @param newPassword 新密码
     * @return 如果更新成功返回 true，否则返回 false
     */
    public boolean tryUpdateThisPassword(String newPassword) {
        if(_account == null) {
            return false; // 没有登录账户，不允许更新密码
        }
        _account.setPassword(newPassword); // 更新密码
        return AccountManager.getInstance().update(_account); // 保存更新后的账户信息
    }

    /**
     * 尝试更新一个账户的权限级别，只有当当前登录账户的权限级别等于高于管理员权限级别时才允许更新。
     * @param username 要更新权限级别的账户的用户名
     * @param newLevel 新的权限级别
     * @return 如果更新成功返回 true，否则返回 false
     */
    public boolean tryUpdateAccountLevel(String username, AccountLevel newLevel) {
        if(_account == null || _account.getLevel().getLevel() < AccountLevel.ADMIN.getLevel()) {
            return false; // 没有登录账户，或当前登录账户权限级别低于管理员，不允许更新账户权限级别
        }
        var account = AccountManager.getInstance().getByUsername(username);
        if (account == null) {
            return false; // 用户不存在
        }
        account.setLevel(newLevel); // 更新账户权限级别
        return AccountManager.getInstance().update(account); // 保存更新后的账户信息
    }

    /**
     * 检查当前登录账户是否具有访问指定权限级别的权限。
     * @param level 要检查的权限级别
     * @return 如果当前登录账户的权限级别高于或等于指定权限级别返回 true，否则返回 false
     */
    public boolean isAllow(AccountLevel level) {
        if(_account == null) {
            return AccountLevel.GUEST.getLevel() >= level.getLevel(); // 没有登录账户，默认权限级别为 GUEST
        }
        return _account.getLevel().getLevel() >= level.getLevel(); // 当前登录账户权限级别高于或等于指定权限级别，允许访问
    }

}
