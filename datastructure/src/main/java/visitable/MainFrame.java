package visitable;

import javax.swing.*;
import java.awt.*;

/**
 * 主窗口：1024*768
 * 顶部导航：car / order / account 切换按钮
 * 每个页面：下拉筛选 + 输入框 + 筛选按钮 + 数据列表 + 点击列表弹窗详情
 */
public class MainFrame extends JFrame {

    private static MainFrame instance;

    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
        }
        return instance;
    }

    public void init() {
        //  Swing窗口UI线程启动
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
    // 窗口固定尺寸
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    // 导航按钮
    private JPanel contentPanel; // 内容面板
    private JButton carButton;
    private JButton orderButton;
    private JButton accountButton;
    private CardLayout cardLayout; // 卡片布局

    public MainFrame() {
        setTitle("Visitable Pattern Demo");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 窗口居中

        // 初始化导航按钮
        carButton = new JButton("Car");
        orderButton = new JButton("Order");
        accountButton = new JButton("Account");

        // 导航面板
        JPanel navPanel = new JPanel();
        navPanel.add(carButton);
        navPanel.add(orderButton);
        navPanel.add(accountButton);

        // 内容面板使用CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // 添加不同页面的面板
        contentPanel.add(new CarPanel(), "Car");
        contentPanel.add(new OrderPanel(), "Order");
        contentPanel.add(new AccountPanel(), "Account");

        // 添加导航和内容面板到主窗口
        setLayout(new BorderLayout());
        add(navPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // 添加按钮事件监听器
        carButton.addActionListener(e -> cardLayout.show(contentPanel, "Car"));
        orderButton.addActionListener(e -> cardLayout.show(contentPanel, "Order"));
        accountButton.addActionListener(e -> cardLayout.show(contentPanel, "Account"));
    }

}