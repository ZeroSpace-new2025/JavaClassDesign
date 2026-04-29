package visitable;

import business.CarsBusiness;
import business.LoginAccount;
import business.Ordersbusinesss;
import cars.Car;
import cars.CarState;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 车辆列表面板，用于展示和管理车辆信息
 * 继承自BasePanel，实现车辆数据的展示、筛选和详情查看功能
 * 仅允许调用 business.CarsBusiness 和 business.Ordersbusinesss 提供的方法
 */
public class CarPanel extends BasePanel<Car> {

    /** 车辆数据源 */
    private List<Car> carDataSource = new ArrayList<>();
    
    /** 车辆业务对象 */
    private CarsBusiness carsBusiness;
    
    /** 订单业务对象 */
    private Ordersbusinesss ordersBusiness;
    
    /** 订单刷新回调 */
    private Runnable orderRefreshCallback;

    /**
     * 设置订单刷新回调，当创建订单成功后调用
     * @param callback 刷新回调
     */
    public void setOrderRefreshCallback(Runnable callback) {
        this.orderRefreshCallback = callback;
    }

    /**
     * 构造函数，初始化车辆面板
     */
    public CarPanel() {
        LoginAccount loginAccount = LoginAccount.getInstance();
        LoginAccount appLogin = university.App.getInstance().getLogInAccount();
        if (appLogin != null && appLogin.getAccount() != null) {
            loginAccount.login(appLogin.getAccount().getUsername(), appLogin.getAccount().getPassword());
        }
        carsBusiness = new CarsBusiness(loginAccount);
        ordersBusiness = new Ordersbusinesss(loginAccount);
        
        loadCarDataSource();
        init();
    }

    /**
     * 从CarsBusiness加载车辆数据
     */
    private void loadCarDataSource() {
        carDataSource.clear();
        Car[] cars = carsBusiness.listAllCars();
        if (cars != null) {
            for (Car c : cars) carDataSource.add(c);
        }
    }

    @Override
    protected String[] getFilterOptions() {
        return new String[]{"名称", "价格", "型号", "颜色", "状态"};
    }

    @Override
    protected JPanel[] getListData() {
        return getListDataByFilter(null, null);
    }

    private JPanel[] getListDataByFilter(String filterType, String keyword) {
        List<JPanel> list = new ArrayList<>();
        for (Car car : carDataSource) {
            boolean match = true;
            if (filterType != null && keyword != null && !keyword.trim().isEmpty()) {
                match = switch (filterType) {
                    case "名称" -> car.getName().contains(keyword);
                    case "价格" -> String.valueOf(car.getPrice()).contains(keyword);
                    case "型号" -> car.getModel().contains(keyword);
                    case "颜色" -> car.getColor().contains(keyword);
                    case "状态" -> car.getState().toString().contains(keyword);
                    default -> true;
                };
            }
            if (match) list.add(createCarItem(car));
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

    private JPanel createCarItem(Car car) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 3, 8, 4));
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        panel.setPreferredSize(new Dimension(600, 80));
        panel.putClientProperty("car", car);
        
        panel.add(new JLabel("ID：" + car.getID()));
        panel.add(new JLabel("名称：" + car.getName()));
        panel.add(new JLabel("型号：" + car.getModel()));
        panel.add(new JLabel("颜色：" + car.getColor()));
        panel.add(new JLabel("日租价格：¥" + car.getPrice()));
        panel.add(new JLabel("状态：" + getCarStateDesc(car.getState())));
        return panel;
    }

    private String getCarStateDesc(CarState state) {
        return switch (state) {
            case AVAILABLE -> "可租";
            case ON_RENT -> "已出租";
            case ON_REPAIR -> "维护中";
            case ON_BOOKING -> "预订中";
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
        Car car = (Car) detailPanel.getClientProperty("car");
        super.showDetailDialog(detailPanel, createActionPanel(car));
    }

    private JPanel createActionPanel(Car car) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        
        if (carsBusiness.getCarByID(car.getID()) != null) {
            JButton orderBtn = new JButton("创建订单");
            orderBtn.addActionListener(e -> handleCreateOrder(car));
            panel.add(orderBtn);
        }
        return panel;
    }

    private void handleCreateOrder(Car car) {
        java.time.LocalDate now = java.time.LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        int currentDay = now.getDayOfMonth();
        
        JPanel panel = new JPanel(new GridLayout(3, 4, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JComboBox<Integer> startYearBox = new JComboBox<>();
        JComboBox<Integer> startMonthBox = new JComboBox<>();
        JComboBox<Integer> startDayBox = new JComboBox<>();
        JComboBox<Integer> endYearBox = new JComboBox<>();
        JComboBox<Integer> endMonthBox = new JComboBox<>();
        JComboBox<Integer> endDayBox = new JComboBox<>();
        
        for (int y = currentYear; y <= currentYear + 5; y++) {
            startYearBox.addItem(y);
            endYearBox.addItem(y);
        }
        for (int m = 1; m <= 12; m++) {
            startMonthBox.addItem(m);
            endMonthBox.addItem(m);
        }
        for (int d = 1; d <= 31; d++) {
            startDayBox.addItem(d);
            endDayBox.addItem(d);
        }
        
        startYearBox.setSelectedItem(currentYear);
        startMonthBox.setSelectedItem(currentMonth);
        startDayBox.setSelectedItem(currentDay);
        
        endYearBox.setSelectedItem(currentYear);
        endMonthBox.setSelectedItem(currentMonth);
        endDayBox.setSelectedItem(Math.min(currentDay + 1, 31));
        
        panel.add(new JLabel("开始年："));
        panel.add(startYearBox);
        panel.add(new JLabel("结束年："));
        panel.add(endYearBox);
        panel.add(new JLabel("开始月："));
        panel.add(startMonthBox);
        panel.add(new JLabel("结束月："));
        panel.add(endMonthBox);
        panel.add(new JLabel("开始日："));
        panel.add(startDayBox);
        panel.add(new JLabel("结束日："));
        panel.add(endDayBox);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "选择订单时间", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (result != JOptionPane.OK_OPTION) return;
        
        int startYear = (int) startYearBox.getSelectedItem();
        int startMonth = (int) startMonthBox.getSelectedItem();
        int startDay = (int) startDayBox.getSelectedItem();
        int endYear = (int) endYearBox.getSelectedItem();
        int endMonth = (int) endMonthBox.getSelectedItem();
        int endDay = (int) endDayBox.getSelectedItem();
        
        String startTime = String.format("%04d%02d%02d", startYear, startMonth, startDay);
        String endTime = String.format("%04d%02d%02d", endYear, endMonth, endDay);
        
        java.time.LocalDate startDate = java.time.LocalDate.of(startYear, startMonth, startDay);
        java.time.LocalDate endDate = java.time.LocalDate.of(endYear, endMonth, endDay);
        
        if (startDate.isBefore(now)) {
            JOptionPane.showMessageDialog(this, "开始时间不能早于今天", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!endDate.isAfter(startDate)) {
            JOptionPane.showMessageDialog(this, "结束时间必须晚于开始时间", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (ordersBusiness.fileOrder(car.getID(), startTime, endTime)) {
            JOptionPane.showMessageDialog(this, "订单创建成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            if (orderRefreshCallback != null) {
                orderRefreshCallback.run();
            }
        } else {
            JOptionPane.showMessageDialog(this, "订单创建失败，请检查时间格式或权限。", "失败", JOptionPane.ERROR_MESSAGE);
        }
    }
}
