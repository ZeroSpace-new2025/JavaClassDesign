package visitable;

import javax.swing.*;
import java.awt.*;

/**
 * 主窗口类，提供汽车租赁系统的图形用户界面。
 * 窗口尺寸为 1024x768，包含顶部导航栏和内容区域。
 * 支持 Car、Order、Account 三个页面的切换。
 */
public class MainFrame extends JFrame {

    private static MainFrame instance;

    /**
     * 获取 MainFrame 的单例实例。
     * @return MainFrame 单例实例
     */
    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
        }
        return instance;
    }

    /**
     * 初始化主窗口并在 EDT 线程中显示。
     */
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

    /**
     * 构造函数，初始化主窗口的布局、导航按钮和内容面板。
     * 创建 CarPanel、OrderPanel、AccountPanel 三个页面，并设置跨面板刷新回调。
     */
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

        // 创建面板实例
        CarPanel carPanel = new CarPanel();
        OrderPanel orderPanel = new OrderPanel();
        AccountPanel accountPanel = new AccountPanel();
        
        // 设置订单刷新回调：CarPanel创建订单后刷新OrderPanel
        carPanel.setOrderRefreshCallback(() -> orderPanel.refreshData());

        // 添加不同页面的面板
        contentPanel.add(carPanel, "Car");
        contentPanel.add(orderPanel, "Order");
        contentPanel.add(accountPanel, "Account");

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