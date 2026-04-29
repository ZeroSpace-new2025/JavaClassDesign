package visitable;

import account.AccountLevel;
import business.LoginAccount;
import business.Ordersbusinesss;
import cars.Car;
import cars.CarManager;
import cars.CarState;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 车辆列表面板，用于展示和管理车辆信息
 * 继承自BasePanel，实现车辆数据的展示、筛选和详情查看功能
 */
public class CarPanel extends BasePanel<Car> {

    /** 车辆数据源 */
    private List<Car> carDataSource = new ArrayList<>();

    /**
     * 构造函数，初始化车辆面板
     */
    public CarPanel() {
        carDataSource = loadCarDataSource();
        init();
    }

    /**
     * 从CarManager加载车辆数据
     * @return 车辆列表
     */
    private List<Car> loadCarDataSource() {
        var allCars = CarManager.getInstance().listAll();
        if(allCars == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(allCars); // 创建副本以防止外部修改原数据
    }

    /**
     * 获取下拉筛选选项
     * @return 筛选选项数组
     */
    @Override
    protected String[] getFilterOptions() {
        return new String[]{"名称", "价格", "型号", "颜色", "状态"};
    }

    /**
     * 获取列表数据（基于Car对象构建）
     * @return 车辆列表项面板数组
     */
    @Override
    protected JPanel[] getListData() {
        return getListDataByFilter(null, null);
    }

    /**
     * 带筛选条件的列表数据构建
     * @param filterType 筛选类型
     * @param keyword 筛选关键词
     * @return 筛选后的车辆列表项面板数组
     */
    private JPanel[] getListDataByFilter(String filterType, String keyword) {
        List<JPanel> carItemList = new ArrayList<>();
        
        // 筛选逻辑
        for (Car car : carDataSource) {
            boolean isMatch = true;
            
            if (filterType != null && keyword != null && !keyword.trim().isEmpty()) {
                isMatch = switch (filterType) {
                    case "名称" -> car.getName().contains(keyword);
                    case "价格" -> String.valueOf(car.getPrice()).contains(keyword);
                    case "型号" -> car.getModel().contains(keyword);
                    case "颜色" -> car.getColor().contains(keyword);
                    case "状态" -> car.getState().toString().contains(keyword);
                    default -> true;
                };
            }
            
            if (isMatch) {
                carItemList.add(createCarItem(car));
            }
        }
        
        return carItemList.toArray(new JPanel[0]);
    }

    /**
     * 筛选按钮点击事件处理
     * 根据选择的筛选类型和关键词过滤车辆数据
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
        refreshListPanel(filteredData); // 刷新列表面板（需在BasePanel中实现）
        
        JOptionPane.showMessageDialog(this, 
                "筛选条件：" + filterType + "，关键词：" + keyword + "\n匹配结果：" + filteredData.length + "条",
                "筛选完成", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 基于Car对象构建车辆列表项
     * @param car 车辆对象
     * @return 封装后的JPanel列表项
     */
    private JPanel createCarItem(Car car) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new GridLayout(2, 3, 8, 4)); // 网格布局更规整
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        itemPanel.setPreferredSize(new Dimension(600, 80)); // 固定项高度
        
        // 将Car对象存储在clientProperty中，以便详情弹窗时使用
        itemPanel.putClientProperty("car", car);
        
        // 展示Car类的核心属性
        itemPanel.add(new JLabel("ID：" + car.getID()));
        itemPanel.add(new JLabel("名称：" + car.getName()));
        itemPanel.add(new JLabel("型号：" + car.getModel()));
        itemPanel.add(new JLabel("颜色：" + car.getColor()));
        itemPanel.add(new JLabel("日租价格：¥" + car.getPrice()));
        itemPanel.add(new JLabel("状态：" + getCarStateDesc(car.getState())));
        
        return itemPanel;
    }

    // 车辆状态中文描述
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
        JList<JPanel> result = new JList<>(getListData()); // 这里是 JPanel 数组！
        result.setFixedCellHeight(80);

        result.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                // 重点：value 是 JPanel，不是 Car！
                if (value instanceof JPanel itemPanel) {
                    // 设置选中背景色
                    itemPanel.setBackground(isSelected ? new Color(200, 230, 255) : list.getBackground());
                    return itemPanel; // 直接返回你已经做好的车辆面板
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        return result;
    }

    @Override
    protected void showDetailDialog(JPanel detailPanel) {
        // 从detailPanel中获取Car对象
        Car car = (Car) detailPanel.getClientProperty("car");
        
        // 创建底部操作按钮面板
        JPanel actionPanel = createActionPanel(car);
        
        // 调用父类的showDetailDialog，传入actionPanel
        super.showDetailDialog(detailPanel, actionPanel);
    }

    /**
     * 创建详情窗口底部操作按钮面板
     * @param car 当前查看的车辆对象
     * @return 操作按钮面板
     */
    private JPanel createActionPanel(Car car) {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        
        // 获取当前登录账户
        var account = university.App.getInstance().getLogInAccount();
        LoginAccount loginAccount = new LoginAccount();
        Ordersbusinesss ordersBusiness = new Ordersbusinesss(loginAccount);
        
        // "创建订单"按钮：User以上权限显示
        if (account != null && account.getLevel().getLevel() >= AccountLevel.USER.getLevel()) {
            JButton createOrderButton = new JButton("创建订单");
            createOrderButton.addActionListener(e -> handleCreateOrder(car, ordersBusiness));
            actionPanel.add(createOrderButton);
        }
        
        return actionPanel;
    }

    /**
     * 处理创建订单逻辑
     * @param car 车辆对象
     * @param ordersBusiness 订单业务对象
     */
    private void handleCreateOrder(Car car, Ordersbusinesss ordersBusiness) {
        // 弹出对话框输入开始时间和结束时间
        String startTime = JOptionPane.showInputDialog(this, "请输入开始时间（格式：yyyyMMdd）：");
        if (startTime == null || startTime.trim().isEmpty()) {
            return; // 用户取消输入
        }
        
        String endTime = JOptionPane.showInputDialog(this, "请输入结束时间（格式：yyyyMMdd）：");
        if (endTime == null || endTime.trim().isEmpty()) {
            return; // 用户取消输入
        }
        
        // 调用业务层创建订单
        boolean success = ordersBusiness.fileOrder(car.getID(), startTime.trim(), endTime.trim());
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                    "订单创建成功！",
                    "成功", JOptionPane.INFORMATION_MESSAGE);
            // 刷新订单列表
            refreshOrderList();
        } else {
            JOptionPane.showMessageDialog(this, 
                    "订单创建失败，请检查时间格式或车辆状态。",
                    "失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 刷新订单列表（通知OrderPanel刷新）
     */
    private void refreshOrderList() {
        // 这里可以通过事件监听或其他方式通知OrderPanel刷新
        // 简单实现：重新加载OrderPanel的数据
        // 由于OrderPanel是独立实例，这里暂不实现跨面板刷新
    }
}