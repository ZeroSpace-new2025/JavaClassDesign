package visitable;

import account.Account;
import account.AccountLevel;
import business.LoginAccount;
import university.App;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 账户列表面板，用于展示和管理账户信息
 * 继承自BasePanel，实现账户数据的展示、筛选和详情查看功能
 * 仅允许调用 business.LoginAccount 提供的方法
 */
public class AccountPanel extends BasePanel<Account> {

    /** 账户数据源 */
    private List<Account> accountDataSource = new ArrayList<>();
    
    /** 当前登录账户业务对象 */
    private LoginAccount loginAccount;
    
    /** 是否为管理员模式 */
    private boolean isAdminMode;

    /**
     * 登录输入框组件
     */
    private JTextField usernameField;
    private JPasswordField passwordField;

    /**
     * 构造函数，初始化账户面板
     */
    public AccountPanel() {
        loginAccount = LoginAccount.getInstance();
        LoginAccount appLogin = App.getInstance().getLogInAccount();
        
        // 如果 App 已经登录且与当前 loginAccount 是不同实例，则尝试同步登录状态
        if (appLogin != null && appLogin != loginAccount && appLogin.getAccount() != null) {
            boolean loginSuccess = loginAccount.login(appLogin.getAccount().getUsername(), appLogin.getAccount().getPassword());
            if (!loginSuccess) {
                App.getInstance().setLogInAccount(null);
            }
        }
        // 如果 appLogin 就是 loginAccount（同一单例），则无需重复登录
        
        updateAdminMode();
        init();
    }

    /**
     * 更新管理员模式状态并加载数据
     */
    private void updateAdminMode() {
        Account current = loginAccount.getAccount();
        isAdminMode = current != null && current.getLevel().getLevel() >= AccountLevel.ADMIN.getLevel();
        if (isAdminMode) {
            Account[] accounts = loginAccount.listAllAccounts();
            accountDataSource.clear();
            if (accounts != null) {
                for (Account acc : accounts) accountDataSource.add(acc);
            }
        }
    }

    @Override
    protected void init() {
        if (loginAccount.getAccount() == null) {
            setSize(WIDTH, HEIGHT);
            setLayout(new BorderLayout());
            add(createLoginPanel(), BorderLayout.CENTER);
            revalidate();
            repaint();
        } else {
            super.init();
        }
    }

    /**
     * 初始化登录面板（未登录时显示）
     */
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        
        panel.add(new JLabel("用户名："));
        panel.add(usernameField);
        panel.add(new JLabel("密码："));
        panel.add(passwordField);
        
        JButton loginButton = new JButton("登录");
        loginButton.addActionListener(e -> handleLogin());
        panel.add(loginButton);
        
        JButton registerButton = new JButton("注册新账号");
        registerButton.addActionListener(e -> handleRegister());
        panel.add(registerButton);
        
        return panel;
    }

    /**
     * 处理登录逻辑
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (loginAccount.login(username, password)) {
            App.getInstance().setLogInAccount(loginAccount);
            JOptionPane.showMessageDialog(this, "登录成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            refreshPanel();
        } else if (loginAccount.getAccount() != null && loginAccount.getAccount().getUsername().equals(username)) {
            // 已经登录过该账号，直接刷新
            JOptionPane.showMessageDialog(this, "已处于登录状态", "提示", JOptionPane.INFORMATION_MESSAGE);
            refreshPanel();
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误", "失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 处理注册新账号逻辑
     */
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (loginAccount.tryAddAccount(username, password, AccountLevel.USER)) {
            JOptionPane.showMessageDialog(this, "注册成功，请登录", "成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "注册失败，用户名可能已存在", "失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected String[] getFilterOptions() {
        return new String[]{"用户名", "权限级别", "账户ID"};
    }

    @Override
    protected JPanel[] getListData() {
        Account current = loginAccount.getAccount();
        if (current == null) return new JPanel[0];
        
        if (!isAdminMode) {
            return new JPanel[]{createAccountItem(current)};
        }
        return getListDataByFilter(null, null);
    }

    private JPanel[] getListDataByFilter(String filterType, String keyword) {
        List<JPanel> list = new ArrayList<>();
        for (Account acc : accountDataSource) {
            boolean match = true;
            if (filterType != null && keyword != null && !keyword.trim().isEmpty()) {
                match = switch (filterType) {
                    case "用户名" -> acc.getUsername().contains(keyword);
                    case "权限级别" -> acc.getLevel().toString().contains(keyword);
                    case "账户ID" -> String.valueOf(acc.getID()).contains(keyword);
                    default -> true;
                };
            }
            if (match) list.add(createAccountItem(acc));
        }
        return list.toArray(new JPanel[0]);
    }

    @Override
    protected void onFilterClick() {
        String filterType = (String) filterComboBox.getSelectedItem();
        String keyword = searchTextField.getText().trim();
        if (filterType == null || filterType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请选择筛选类型", "筛选提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        refreshListPanel(getListDataByFilter(filterType, keyword));
        JOptionPane.showMessageDialog(this, "筛选完成", "信息", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createAccountItem(Account account) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 3, 8, 4));
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        panel.setPreferredSize(new Dimension(600, 80));
        panel.putClientProperty("account", account);
        
        panel.add(new JLabel("ID：" + account.getID()));
        panel.add(new JLabel("用户名：" + account.getUsername()));
        panel.add(new JLabel("权限级别：" + getLevelDesc(account.getLevel())));
        panel.add(new JLabel("密码：" + maskPassword(account.getPassword())));
        return panel;
    }

    private String getLevelDesc(AccountLevel level) {
        return switch (level) {
            case GUEST -> "游客";
            case USER -> "普通用户";
            case ADMIN -> "管理员";
            default -> "未知";
        };
    }

    private String maskPassword(String pwd) {
        return (pwd == null || pwd.isEmpty()) ? "" : "*".repeat(pwd.length());
    }

    @Override
    protected JList<JPanel> childInitDataList() {
        JList<JPanel> list = new JList<>(getListData());
        list.setFixedCellHeight(80);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof JPanel p) {
                    p.setBackground(isSelected ? new Color(200, 230, 255) : list.getBackground());
                    return p;
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        return list;
    }

    @Override
    protected void showDetailDialog(JPanel detailPanel) {
        Account account = (Account) detailPanel.getClientProperty("account");
        super.showDetailDialog(detailPanel, createActionPanel(account));
    }

    private JPanel createActionPanel(Account account) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        
        if (isAdminMode) {
            JButton deleteBtn = new JButton("删除");
            deleteBtn.addActionListener(e -> handleDelete(account));
            panel.add(deleteBtn);
            
            JButton levelBtn = new JButton("改变访问等级");
            levelBtn.addActionListener(e -> handleUpdateLevel(account));
            panel.add(levelBtn);
            
            JButton pwdBtn = new JButton("更改密码");
            pwdBtn.addActionListener(e -> handleResetPassword(account));
            panel.add(pwdBtn);
        } else if (account.getID() == loginAccount.getAccount().getID()) {
            JButton logoutBtn = new JButton("注销本账号");
            logoutBtn.addActionListener(e -> handleLogout());
            panel.add(logoutBtn);
            
            JButton pwdBtn = new JButton("修改密码");
            pwdBtn.addActionListener(e -> handleChangePassword());
            panel.add(pwdBtn);
        }
        return panel;
    }

    private void handleDelete(Account account) {
        if (account.getID() == loginAccount.getAccount().getID()) {
            JOptionPane.showMessageDialog(this, "不能删除当前登录账户", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "确定删除 " + account.getUsername() + "？", "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (loginAccount.tryDeleteAccount(account.getUsername())) {
                JOptionPane.showMessageDialog(this, "已删除", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshPanel();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败", "失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleUpdateLevel(Account account) {
        String[] levels = {"GUEST", "USER", "ADMIN"};
        String sel = (String) JOptionPane.showInputDialog(this, "选择新权限级别：", "修改权限", JOptionPane.QUESTION_MESSAGE, null, levels, account.getLevel().toString());
        if (sel != null) {
            AccountLevel newLevel = switch (sel) {
                case "GUEST" -> AccountLevel.GUEST;
                case "USER" -> AccountLevel.USER;
                case "ADMIN" -> AccountLevel.ADMIN;
                default -> null;
            };
            if (newLevel != null && loginAccount.tryUpdateAccountLevel(account.getUsername(), newLevel)) {
                JOptionPane.showMessageDialog(this, "权限已更新", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshPanel();
            }
        }
    }

    private void handleResetPassword(Account account) {
        String pwd = JOptionPane.showInputDialog(this, "输入新密码：", "更改密码", JOptionPane.QUESTION_MESSAGE);
        if (pwd != null && !pwd.trim().isEmpty() && loginAccount.tryUpdatePassword(account.getUsername(), pwd.trim())) {
            JOptionPane.showMessageDialog(this, "密码已更改", "成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleLogout() {
        if (JOptionPane.showConfirmDialog(this, "确定注销当前账号？", "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            loginAccount.logout();
            App.getInstance().setLogInAccount(null);
            JOptionPane.showMessageDialog(this, "已注销", "成功", JOptionPane.INFORMATION_MESSAGE);
            refreshPanel();
        }
    }

    private void handleChangePassword() {
        String pwd = JOptionPane.showInputDialog(this, "输入新密码：", "修改密码", JOptionPane.QUESTION_MESSAGE);
        if (pwd != null && !pwd.trim().isEmpty()) {
            if (loginAccount.tryUpdateThisPassword(pwd.trim())) {
                JOptionPane.showMessageDialog(this, "密码已修改", "成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "修改失败", "失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshPanel() {
        updateAdminMode();
        removeAll();
        if (loginAccount.getAccount() == null) {
            setLayout(new BorderLayout());
            add(createLoginPanel(), BorderLayout.CENTER);
        } else {
            super.init();
        }
        revalidate();
        repaint();
    }
}
