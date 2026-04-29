package visitable;

import business.CarsBusiness;
import business.LoginAccount;
import business.Ordersbusinesss;
import cars.Car;
import orders.Order;
import orders.OrderState;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单列表面板，用于展示和管理订单信息
 * 继承自BasePanel，实现订单数据的展示、筛选和详情查看功能
 * 仅允许调用 business.Ordersbusinesss 和 business.CarsBusiness 提供的方法
 */
public class OrderPanel extends BasePanel<Order> {

    /** 订单数据源 */
    private List<Order> orderDataSource = new ArrayList<>();
    
    /** 订单业务对象 */
    private Ordersbusinesss ordersBusiness;
    
    /** 车辆业务对象（用于获取车辆名称） */
    private CarsBusiness carsBusiness;

    /**
     * 构造函数，初始化订单面板
     */
    public OrderPanel() {
        LoginAccount loginAccount = LoginAccount.getInstance();
        LoginAccount appLogin = university.App.getInstance().getLogInAccount();
        if (appLogin != null && appLogin.getAccount() != null) {
            loginAccount.login(appLogin.getAccount().getUsername(), appLogin.getAccount().getPassword());
        }
        ordersBusiness = new Ordersbusinesss(loginAccount);
        carsBusiness = new CarsBusiness(loginAccount);
        
        loadOrderDataSource();
        init();
    }

    /**
     * 从Ordersbusinesss加载订单数据
     */
    private void loadOrderDataSource() {
        orderDataSource.clear();
        Order[] orders = ordersBusiness.listAllOrders();
        if (orders != null) {
            for (Order o : orders) orderDataSource.add(o);
        }
    }

    @Override
    protected String[] getFilterOptions() {
        return new String[]{"订单ID", "车辆ID", "开始时间", "结束时间", "状态", "类型"};
    }

    @Override
    protected JPanel[] getListData() {
        return getListDataByFilter(null, null);
    }

    private JPanel[] getListDataByFilter(String filterType, String keyword) {
        List<JPanel> list = new ArrayList<>();
        for (Order order : orderDataSource) {
            boolean match = true;
            if (filterType != null && keyword != null && !keyword.trim().isEmpty()) {
                match = switch (filterType) {
                    case "订单ID" -> String.valueOf(order.getOrderID()).contains(keyword);
                    case "车辆ID" -> String.valueOf(order.getCarID()).contains(keyword);
                    case "开始时间" -> order.getStartTime().contains(keyword);
                    case "结束时间" -> order.getEndTime().contains(keyword);
                    case "状态" -> order.getState().toString().contains(keyword);
                    case "类型" -> order.getNature().toString().contains(keyword);
                    default -> true;
                };
            }
            if (match) list.add(createOrderItem(order));
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

    private JPanel createOrderItem(Order order) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 3, 8, 4));
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        panel.setPreferredSize(new Dimension(600, 80));
        panel.putClientProperty("order", order);
        
        Car car = carsBusiness.getCarByID(order.getCarID());
        String carName = (car != null) ? car.getName() : "未知车辆";
        
        panel.add(new JLabel("订单ID：" + order.getOrderID()));
        panel.add(new JLabel("车辆：" + carName));
        panel.add(new JLabel("开始时间：" + order.getStartTime()));
        panel.add(new JLabel("结束时间：" + order.getEndTime()));
        panel.add(new JLabel("价格：¥" + order.getPrice()));
        panel.add(new JLabel("状态：" + getOrderStateDesc(order.getState())));
        return panel;
    }

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
        Order order = (Order) detailPanel.getClientProperty("order");
        super.showDetailDialog(detailPanel, createActionPanel(order));
    }

    private JPanel createActionPanel(Order order) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        
        if (ordersBusiness.getOrderByID(order.getOrderID()) != null) {
            JButton cancelBtn = new JButton("取消订单");
            cancelBtn.addActionListener(e -> handleCancelOrder(order));
            panel.add(cancelBtn);
            
            JButton finishBtn = new JButton("完成");
            finishBtn.addActionListener(e -> handleFinishOrder(order));
            panel.add(finishBtn);
            
            JButton approveBtn = new JButton("通过审核");
            approveBtn.addActionListener(e -> handleApproveOrder(order));
            panel.add(approveBtn);
            
            JButton rejectBtn = new JButton("拒绝通过");
            rejectBtn.addActionListener(e -> handleRejectOrder(order));
            panel.add(rejectBtn);
        }
        return panel;
    }

    private void handleCancelOrder(Order order) {
        if (JOptionPane.showConfirmDialog(this, "确定取消该订单？", "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (ordersBusiness.cancelOrder(order.getOrderID())) {
                JOptionPane.showMessageDialog(this, "订单已取消", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshPanel();
            } else {
                JOptionPane.showMessageDialog(this, "取消失败", "失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleApproveOrder(Order order) {
        if (JOptionPane.showConfirmDialog(this, "确定通过审核？", "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (ordersBusiness.approveOrder(order.getOrderID())) {
                JOptionPane.showMessageDialog(this, "已通过审核", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshPanel();
            } else {
                JOptionPane.showMessageDialog(this, "审核失败", "失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleRejectOrder(Order order) {
        if (JOptionPane.showConfirmDialog(this, "确定拒绝审核？", "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (ordersBusiness.rejectOrder(order.getOrderID())) {
                JOptionPane.showMessageDialog(this, "已拒绝审核", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshPanel();
            } else {
                JOptionPane.showMessageDialog(this, "拒绝失败", "失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleFinishOrder(Order order) {
        if (JOptionPane.showConfirmDialog(this, "确定完成该订单？", "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (ordersBusiness.completeOrder(order.getOrderID())) {
                JOptionPane.showMessageDialog(this, "订单已完成", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshPanel();
            } else {
                JOptionPane.showMessageDialog(this, "完成失败", "失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshPanel() {
        loadOrderDataSource();
        refreshListPanel(getListData());
    }

    /**
     * 公开刷新方法，供外部调用以同步订单数据
     */
    public void refreshData() {
        refreshPanel();
    }
}
