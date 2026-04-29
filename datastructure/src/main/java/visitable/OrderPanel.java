package visitable;

import account.AccountLevel;
import cars.Car;
import cars.CarManager;
import orders.Order;
import orders.OrderManager;
import orders.OrderState;
import university.App;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单列表面板，用于展示和管理订单信息
 * 继承自BasePanel，实现订单数据的展示、筛选和详情查看功能
 */
public class OrderPanel extends BasePanel<Order> {

    /** 订单数据源 */
    private List<Order> orderDataSource = new ArrayList<>();

    /**
     * 构造函数，初始化订单面板
     */
    public OrderPanel() {
        orderDataSource = loadOrderDataSource();
        init();
    }

    /**
     * 从OrderManager加载订单数据
     * @return 订单列表
     */
    private List<Order> loadOrderDataSource() {
        var allOrders = OrderManager.getInstance().listAll();
        if(allOrders == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(allOrders); // 创建副本以防止外部修改原数据
    }

    /**
     * 获取下拉筛选选项
     * @return 筛选选项数组
     */
    @Override
    protected String[] getFilterOptions() {
        return new String[]{"订单ID", "车辆ID", "开始时间", "结束时间", "状态", "类型"};
    }

    /**
     * 获取列表数据（基于Order对象构建）
     * @return 订单列表项面板数组
     */
    @Override
    protected JPanel[] getListData() {
        return getListDataByFilter(null, null);
    }

    /**
     * 带筛选条件的列表数据构建
     * @param filterType 筛选类型
     * @param keyword 筛选关键词
     * @return 筛选后的订单列表项面板数组
     */
    private JPanel[] getListDataByFilter(String filterType, String keyword) {
        List<JPanel> orderItemList = new ArrayList<>();
        
        // 筛选逻辑
        for (Order order : orderDataSource) {
            boolean isMatch = true;
            
            if (filterType != null && keyword != null && !keyword.trim().isEmpty()) {
                isMatch = switch (filterType) {
                    case "订单ID" -> String.valueOf(order.getOrderID()).contains(keyword);
                    case "车辆ID" -> String.valueOf(order.getCarID()).contains(keyword);
                    case "开始时间" -> order.getStartTime().contains(keyword);
                    case "结束时间" -> order.getEndTime().contains(keyword);
                    case "状态" -> order.getState().toString().contains(keyword);
                    case "类型" -> order.getNature().toString().contains(keyword);
                    default -> true;
                };
            }
            
            if (isMatch) {
                orderItemList.add(createOrderItem(order));
            }
        }
        
        return orderItemList.toArray(new JPanel[0]);
    }

    /**
     * 筛选按钮点击事件处理
     * 根据选择的筛选类型和关键词过滤订单数据
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
     * 基于Order对象构建订单列表项
     * @param order 订单对象
     * @return 封装后的JPanel列表项
     */
    private JPanel createOrderItem(Order order) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new GridLayout(2, 3, 8, 4)); // 网格布局更规整
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        itemPanel.setPreferredSize(new Dimension(600, 80)); // 固定项高度
        
        // 将Order对象存储在clientProperty中，以便详情弹窗时使用
        itemPanel.putClientProperty("order", order);
        
        // 获取关联车辆名称
        Car car = CarManager.getInstance().getByID(order.getCarID());
        String carName = (car != null) ? car.getName() : "未知车辆";
        
        // 展示Order类的核心属性
        itemPanel.add(new JLabel("订单ID：" + order.getOrderID()));
        itemPanel.add(new JLabel("车辆：" + carName));
        itemPanel.add(new JLabel("开始时间：" + order.getStartTime()));
        itemPanel.add(new JLabel("结束时间：" + order.getEndTime()));
        itemPanel.add(new JLabel("价格：¥" + order.getPrice()));
        itemPanel.add(new JLabel("状态：" + getOrderStateDesc(order.getState())));
        
        return itemPanel;
    }

    // 订单状态中文描述
    private String getOrderStateDesc(OrderState state) {
        return switch (state) {
            case PENDING -> "待确认";
            case RENTING -> "租赁中";
            case FINISHED -> "已完成";
            case CANCELED -> "已取消";
            case REVIEWING -> "审核中";
            case REJECTED -> "审核未通过";
            case REVIEWED -> "已审核";
            default -> "未知状态";
        };
    }

    @Override
    protected JList<JPanel> childInitDataList() {
        JList<JPanel> result = new JList<>(getListData()); // 这里是 JPanel 数组！
        result.setFixedCellHeight(80);

        result.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                // 重点：value 是 JPanel，不是 Order！
                if (value instanceof JPanel itemPanel) {
                    // 设置选中背景色
                    itemPanel.setBackground(isSelected ? new Color(200, 230, 255) : list.getBackground());
                    return itemPanel; // 直接返回你已经做好的订单面板
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        return result;
    }

    @Override
    protected void showDetailDialog(JPanel detailPanel) {
        // 从detailPanel中获取Order对象
        Order order = (Order) detailPanel.getClientProperty("order");
        
        // 创建底部操作按钮面板
        JPanel actionPanel = createActionPanel(order);
        
        // 调用父类的showDetailDialog，传入actionPanel
        super.showDetailDialog(detailPanel, actionPanel);
    }

    /**
     * 创建详情窗口底部操作按钮面板
     * @param order 当前查看的订单对象
     * @return 操作按钮面板
     */
    private JPanel createActionPanel(Order order) {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        
        // 获取当前登录账户
        var account = App.getInstance().getLogInAccount();
        boolean isUser = account != null && account.getLevel().getLevel() >= AccountLevel.USER.getLevel();
        boolean isAdmin = account != null && account.getLevel().getLevel() >= AccountLevel.ADMIN.getLevel();
        
        // "取消订单"按钮：User以上权限显示
        if (isUser) {
            JButton cancelButton = new JButton("取消订单");
            cancelButton.addActionListener(e -> handleCancelOrder(order));
            actionPanel.add(cancelButton);
        }
        
        // "通过审核"按钮：Admin以上权限显示
        if (isAdmin) {
            JButton approveButton = new JButton("通过审核");
            approveButton.addActionListener(e -> handleApproveOrder(order));
            actionPanel.add(approveButton);
        }
        
        // "拒绝通过"按钮：Admin以上权限显示
        if (isAdmin) {
            JButton rejectButton = new JButton("拒绝通过");
            rejectButton.addActionListener(e -> handleRejectOrder(order));
            actionPanel.add(rejectButton);
        }
        
        // "完成"按钮：User以上权限显示
        if (isUser) {
            JButton finishButton = new JButton("完成");
            finishButton.addActionListener(e -> handleFinishOrder(order));
            actionPanel.add(finishButton);
        }
        
        return actionPanel;
    }

    /**
     * 处理取消订单逻辑
     * @param order 订单对象
     */
    private void handleCancelOrder(Order order) {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "确定要取消该订单吗？",
                "确认取消", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            order.setState(OrderState.CANCELED);
            OrderManager.getInstance().update(order);
            JOptionPane.showMessageDialog(this, 
                    "订单已取消",
                    "成功", JOptionPane.INFORMATION_MESSAGE);
            refreshListPanel(getListData());
        }
    }

    /**
     * 处理通过审核逻辑
     * @param order 订单对象
     */
    private void handleApproveOrder(Order order) {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "确定要通过该订单的审核吗？",
                "确认通过", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            order.setState(OrderState.REVIEWED);
            OrderManager.getInstance().update(order);
            JOptionPane.showMessageDialog(this, 
                    "订单已通过审核",
                    "成功", JOptionPane.INFORMATION_MESSAGE);
            refreshListPanel(getListData());
        }
    }

    /**
     * 处理拒绝通过逻辑
     * @param order 订单对象
     */
    private void handleRejectOrder(Order order) {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "确定要拒绝该订单的审核吗？",
                "确认拒绝", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            order.setState(OrderState.REJECTED);
            OrderManager.getInstance().update(order);
            JOptionPane.showMessageDialog(this, 
                    "订单已拒绝审核",
                    "成功", JOptionPane.INFORMATION_MESSAGE);
            refreshListPanel(getListData());
        }
    }

    /**
     * 处理完成订单逻辑
     * @param order 订单对象
     */
    private void handleFinishOrder(Order order) {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "确定要完成该订单吗？",
                "确认完成", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            order.setState(OrderState.FINISHED);
            OrderManager.getInstance().update(order);
            JOptionPane.showMessageDialog(this, 
                    "订单已完成",
                    "成功", JOptionPane.INFORMATION_MESSAGE);
            refreshListPanel(getListData());
        }
    }
}
