package visitable;

import account.Account;
import account.AccountLevel;
import account.AccountManager;
import business.LoginAccount;
import university.App;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 账户列表面板，用于展示和管理账户信息
 * 继承自BasePanel，实现账户数据的展示、筛选和详情查看功能
 * 权限等级为Admin以下时直接显示本账号详情
 * 权限等级为Admin以上时显示所有账号，与CarPanel相似
 */
public class AccountPanel extends BasePanel<Account> {

    /** 账户数据源 */
    private List<Account> accountDataSource = new ArrayList<>();
    
    /** 当前登录账户 */
    private LoginAccount loginAccount;
    
    /** 是否为管理员模式 */
    private boolean isAdminMode;

    /**
     * 登录输入框组件
     */
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPanel loginPanel;

    /**
     * 构造函数，初始化账户面板
     */
    public AccountPanel() {
        loginAccount = new LoginAccount();
        var account = App.getInstance().getLogInAccount();
        if (account != null) {
            loginAccount.login(account.getUsername(), account.getPassword());
        }
        
        isAdminMode = account != null && account.getLevel().getLevel() >= AccountLevel.ADMIN.getLevel();
        
        if (isAdminMode) {
            accountDataSource = loadAccountDataSource();
        }
        
        init();
    }

    @Override
    protected void init() {
        Account currentAccount = loginAccount.getAccount();
        if (currentAccount == null) {
            setSize(WIDTH, HEIGHT);
            setLayout(new BorderLayout());
            add(createLoginPanel(), BorderLayout.CENTER);
        } else {
            super.init();
        }
    }

    /**
     * 初始化登录面板（未登录时显示）
     * @return 登录面板
     */
    private JPanel createLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(4, 2, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        
        loginPanel.add(new JLabel("用户名："));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("密码："));
        loginPanel.add(passwordField);
        
        JButton loginButton = new JButton("登录");
        loginButton.addActionListener(e -> handleLogin());
        loginPanel.add(loginButton);
        
        JButton registerButton = new JButton("注册新账号");
        registerButton.addActionListener(e -> handleRegister());
        loginPanel.add(registerButton);
        
        return loginPanel;
    }

    /**
     * 处理登录逻辑
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "用户名和密码不能为空",
                    "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean success = loginAccount.login(username, password);
        if (success) {
            App.getInstance().setLogInAccount(loginAccount.getAccount());
            JOptionPane.showMessageDialog(this, 
                    "登录成功",
                    "成功", JOptionPane.INFORMATION_MESSAGE);
            refreshAccountPanel();
        } else {
            JOptionPane.showMessageDialog(this, 
                    "用户名或密码错误",
                    "失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 处理注册新账号逻辑
     */
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "用户名和密码不能为空",
                    "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean success = loginAccount.tryAddAccount(username, password, AccountLevel.USER);
        if (success) {
            JOptionPane.showMessageDialog(this, 
                    "注册成功，请登录",
                    "成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                    "注册失败，用户名可能已存在",
                    "失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 从AccountManager加载账户数据
     * @return 账户列表
     */
    private List<Account> loadAccountDataSource() {
        var allAccounts = AccountManager.getInstance().listAll();
        if(allAccounts == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(allAccounts); // 创建副本以防止外部修改原数据
    }

    /**
     * 获取下拉筛选选项
     * @return 筛选选项数组
     */
    @Override
    protected String[] getFilterOptions() {
        return new String[]{"用户名", "权限级别", "账户ID"};
    }

    /**
     * 获取列表数据（基于Account对象构建）
     * @return 账户列表项面板数组
     */
    @Override
    protected JPanel[] getListData() {
        Account currentAccount = loginAccount.getAccount();
        if (currentAccount == null) {
            return new JPanel[0];
        }
        
        if (!isAdminMode) {
            return new JPanel[]{createAccountItem(currentAccount)};
        }
        return getListDataByFilter(null, null);
    }

    /**
     * 带筛选条件的列表数据构建
     * @param filterType 筛选类型
     * @param keyword 筛选关键词
     * @return 筛选后的账户列表项面板数组
     */
    private JPanel[] getListDataByFilter(String filterType, String keyword) {
        List<JPanel> accountItemList = new ArrayList<>();
        
        // 筛选逻辑
        for (Account account : accountDataSource) {
            boolean isMatch = true;
            
            if (filterType != null && keyword != null && !keyword.trim().isEmpty()) {
                isMatch = switch (filterType) {
                    case "用户名" -> account.getUsername().contains(keyword);
                    case "权限级别" -> account.getLevel().toString().contains(keyword);
                    case "账户ID" -> String.valueOf(account.getID()).contains(keyword);
                    default -> true;
                };
            }
            
            if (isMatch) {
                accountItemList.add(createAccountItem(account));
            }
        }
        
        return accountItemList.toArray(new JPanel[0]);
    }

    /**
     * 筛选按钮点击事件处理
     * 根据选择的筛选类型和关键词过滤账户数据
     */
    @Override
    protected void onFilterClick() {
        String filterType = (String) filterComboBox.getSelectedItem();
        String keyword = searchTextField.getText().trim();
        
        // 空筛选条件处理
        if (filterType == null || filterType.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "请选择筛选类型",
                    "筛选提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 重新加载筛选后的列表数据
        JPanel[] filteredData = getListDataByFilter(filterType, keyword);
        refreshListPanel(filteredData); // 刷新列表面板
        
        JOptionPane.showMessageDialog(this, 
                "筛选条件：" + filterType + "，关键词：" + keyword + "\n匹配结果：" + filteredData.length + "条",
                "筛选完成", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 基于Account对象构建账户列表项
     * @param account 账户对象
     * @return 封装后的JPanel列表项
     */
    private JPanel createAccountItem(Account account) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new GridLayout(2, 3, 8, 4)); // 网格布局更规整
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        itemPanel.setPreferredSize(new Dimension(600, 80)); // 固定项高度
        
        // 将Account对象存储在clientProperty中，以便详情弹窗时使用
        itemPanel.putClientProperty("account", account);
        
        // 展示Account类的核心属性
        itemPanel.add(new JLabel("ID：" + account.getID()));
        itemPanel.add(new JLabel("用户名：" + account.getUsername()));
        itemPanel.add(new JLabel("权限级别：" + getAccountLevelDesc(account.getLevel())));
        itemPanel.add(new JLabel("密码：" + maskPassword(account.getPassword())));
        
        return itemPanel;
    }

    /**
     * 获取账户权限级别的中文描述
     * @param level 权限级别枚举
     * @return 权限级别中文描述
     */
    private String getAccountLevelDesc(AccountLevel level) {
        return switch (level) {
            case GUEST -> "游客";
            case USER -> "普通用户";
            case ADMIN -> "管理员";
            default -> "未知";
        };
    }

    /**
     * 密码脱敏显示
     * @param password 原始密码
     * @return 脱敏后的密码字符串
     */
    private String maskPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }
        return "*".repeat(password.length());
    }

    @Override
    protected JList<JPanel> childInitDataList() {
        JList<JPanel> result = new JList<>(getListData()); // 这里是 JPanel 数组！
        result.setFixedCellHeight(80);

        result.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                // 重点：value 是 JPanel，不是 Account！
                if (value instanceof JPanel itemPanel) {
                    // 设置选中背景色
                    itemPanel.setBackground(isSelected ? new Color(200, 230, 255) : list.getBackground());
                    return itemPanel; // 直接返回你已经做好的账户面板
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        return result;
    }

    @Override
    protected void showDetailDialog(JPanel detailPanel) {
        // 从detailPanel中获取Account对象
        Account account = (Account) detailPanel.getClientProperty("account");
        
        // 创建底部操作按钮面板
        JPanel actionPanel = createActionPanel(account);
        
        // 调用父类的showDetailDialog，传入actionPanel
        super.showDetailDialog(detailPanel, actionPanel);
    }

    /**
     * 创建详情窗口底部操作按钮面板
     * @param account 当前查看的账户对象
     * @return 操作按钮面板
     */
    private JPanel createActionPanel(Account account) {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        
        if (isAdminMode) {
            JButton deleteButton = new JButton("删除");
            deleteButton.addActionListener(e -> handleDeleteAccount(account));
            actionPanel.add(deleteButton);
            
            JButton updateLevelButton = new JButton("改变访问等级");
            updateLevelButton.addActionListener(e -> handleUpdateLevel(account));
            actionPanel.add(updateLevelButton);
            
            JButton resetPasswordButton = new JButton("更改密码");
            resetPasswordButton.addActionListener(e -> handleResetPassword(account));
            actionPanel.add(resetPasswordButton);
        } else {
            if (account.getID() == loginAccount.getAccount().getID()) {
                JButton logoutButton = new JButton("注销本账号");
                logoutButton.addActionListener(e -> handleLogout());
                actionPanel.add(logoutButton);
                
                JButton changePasswordButton = new JButton("修改密码");
                changePasswordButton.addActionListener(e -> handleChangePassword());
                actionPanel.add(changePasswordButton);
            }
        }
        
        return actionPanel;
    }

    /**
     * 处理删除账户逻辑
     * @param account 账户对象
     */
    private void handleDeleteAccount(Account account) {
        // 不能删除当前登录账户
        if (account.getID() == loginAccount.getAccount().getID()) {
            JOptionPane.showMessageDialog(this, 
                    "不能删除当前登录账户",
                    "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "确定要删除账户：" + account.getUsername() + " 吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            AccountManager.getInstance().remove(account);
            JOptionPane.showMessageDialog(this, 
                    "账户已删除",
                    "成功", JOptionPane.INFORMATION_MESSAGE);
            // 刷新列表
            accountDataSource = loadAccountDataSource();
            refreshListPanel(getListData());
        }
    }

    /**
     * 处理修改权限逻辑
     * @param account 账户对象
     */
    private void handleUpdateLevel(Account account) {
        String[] levels = {"GUEST", "USER", "ADMIN"};
        String selectedLevel = (String) JOptionPane.showInputDialog(this,
                "选择新的权限级别：",
                "修改权限",
                JOptionPane.QUESTION_MESSAGE,
                null,
                levels,
                account.getLevel().toString());
        
        if (selectedLevel != null) {
            AccountLevel newLevel = switch (selectedLevel) {
                case "GUEST" -> AccountLevel.GUEST;
                case "USER" -> AccountLevel.USER;
                case "ADMIN" -> AccountLevel.ADMIN;
                default -> null;
            };
            
            if (newLevel != null) {
                loginAccount.tryUpdateAccountLevel(account.getUsername(), newLevel);
                JOptionPane.showMessageDialog(this, 
                        "权限已更新",
                        "成功", JOptionPane.INFORMATION_MESSAGE);
                // 刷新列表
                accountDataSource = loadAccountDataSource();
                refreshListPanel(getListData());
            }
        }
    }

    /**
     * 处理重置密码逻辑
     * @param account 账户对象
     */
    private void handleResetPassword(Account account) {
        String newPassword = JOptionPane.showInputDialog(this,
                "请输入新密码：",
                "更改密码",
                JOptionPane.QUESTION_MESSAGE);
        
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            boolean success = loginAccount.tryUpdatePassword(account.getUsername(), newPassword.trim());
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "密码已更改",
                        "成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "密码更改失败",
                        "失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 处理注销当前登录账号逻辑
     */
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "确定要注销当前账号吗？",
                "确认注销", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            loginAccount.logout();
            App.getInstance().setLogInAccount(null);
            JOptionPane.showMessageDialog(this, 
                    "已注销",
                    "成功", JOptionPane.INFORMATION_MESSAGE);
            refreshAccountPanel();
        }
    }

    /**
     * 处理修改当前登录账号密码逻辑
     */
    private void handleChangePassword() {
        String newPassword = JOptionPane.showInputDialog(this,
                "请输入新密码：",
                "修改密码",
                JOptionPane.QUESTION_MESSAGE);
        
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            boolean success = loginAccount.tryUpdateThisPassword(newPassword.trim());
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "密码已修改",
                        "成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "密码修改失败",
                        "失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 刷新账户面板
     */
    private void refreshAccountPanel() {
        var account = App.getInstance().getLogInAccount();
        isAdminMode = account != null && account.getLevel().getLevel() >= AccountLevel.ADMIN.getLevel();
        
        if (isAdminMode) {
            accountDataSource = loadAccountDataSource();
        }
        
        refreshListPanel(getListData());
    }
}
