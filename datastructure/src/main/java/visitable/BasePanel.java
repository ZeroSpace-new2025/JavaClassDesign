package visitable;

import javax.swing.*;
import java.awt.*;

public abstract class BasePanel<T> extends JPanel {
    protected static final int WIDTH = 1024;
    protected static final int HEIGHT = 768;

    // 通用组件
    protected JComboBox<String> filterComboBox; // 下拉筛选
    protected JTextField searchTextField; // 输入框
    protected JButton filterButton; // 筛选按钮
    protected JList<JPanel> dataList; // 数据列表（每行用JPanel封装详情）
    protected JScrollPane listScrollPane; // 列表滚动面板

    public BasePanel() {
        // 面板基础设置
        setSize(WIDTH, HEIGHT);
        setLayout(new BorderLayout());
    }


    protected void init() {
        // 初始化筛选栏（顶部）
        initFilterBar();

        // 初始化数据列表（中间）
        initDataList();

        // 初始化列表项点击事件（弹窗展示详情）
        initListClickListener();
    }
    /**
     * 初始化筛选栏：下拉框 + 输入框 + 筛选按钮
     */
    private void initFilterBar() {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        // 初始化下拉筛选框（子类可重写getFilterOptions自定义选项）
        filterComboBox = new JComboBox<>(getFilterOptions());
        filterPanel.add(new JLabel("筛选条件："));
        filterPanel.add(filterComboBox);

        // 初始化搜索输入框
        searchTextField = new JTextField(20);
        filterPanel.add(new JLabel("关键词："));
        filterPanel.add(searchTextField);

        // 初始化筛选按钮（子类可重写onFilterClick自定义筛选逻辑）
        filterButton = new JButton("筛选");
        filterButton.addActionListener(e -> onFilterClick());
        filterPanel.add(filterButton);

        // 将筛选栏添加到面板顶部
        add(filterPanel, BorderLayout.NORTH);
    }

    /**
     * 初始化数据列表（带滚动条）
     */
    private void initDataList() {
        // 初始化列表（子类可重写getListData填充自定义数据）
        dataList = childInitDataList();
        dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 单选
        dataList.setFixedCellHeight(60); // 每行高度

        // 滚动面板包裹列表，避免数据过多溢出
        listScrollPane = new JScrollPane(dataList);
        add(listScrollPane, BorderLayout.CENTER);
    }

    /**
     * 初始化列表项点击事件：点击后弹窗展示详情
     */
    private void initListClickListener() {
        dataList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // 避免重复触发
                var selectedItem = dataList.getSelectedValue();
                if (selectedItem != null) {
                    showDetailDialog(selectedItem);
                }
            }
        });
    }

    /**
     * 弹窗展示列表项详情（通用弹窗逻辑）
     * @param detailPanel 封装详情的面板
     */
    protected void showDetailDialog(JPanel detailPanel) {
        showDetailDialog(detailPanel, null);
    }

    /**
     * 弹窗展示列表项详情（通用弹窗逻辑，支持底部操作按钮）
     * @param detailPanel 封装详情的面板
     * @param actionPanel 底部操作按钮面板，可为null
     */
    protected void showDetailDialog(JPanel detailPanel, JPanel actionPanel) {
        JDialog dialog = new JDialog();
        dialog.setTitle("详情");
        dialog.setSize(800, 400);
        dialog.setModal(true); // 模态弹窗
        dialog.setLocationRelativeTo(this); // 相对当前面板居中

        // 主容器使用BorderLayout
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.add(new JScrollPane(detailPanel), BorderLayout.CENTER);
        
        // 添加底部操作按钮面板
        if (actionPanel != null) {
            mainContainer.add(actionPanel, BorderLayout.SOUTH);
        }
        
        dialog.add(mainContainer);
        dialog.setVisible(true);
    }

    protected void refreshListPanel(JPanel[] newData) {
        dataList.setListData(newData);
    }

    // --------------------- 抽象方法：由子类实现自定义逻辑 ---------------------
    /**
     * 获取下拉筛选的选项（子类自定义）
     */
    protected abstract String[] getFilterOptions();

    /**
     * 获取列表数据（子类自定义每行的JPanel内容）
     */
    protected abstract JPanel[] getListData();

    /**
     * 筛选按钮点击事件（子类自定义筛选逻辑）
     */
    protected abstract void onFilterClick();

    /**
     * 子类实现：基于数据源构建每行的JPanel（封装展示内容）
     * @param dataItem 数据项对象（如Car）
    */
    protected abstract JList<JPanel> childInitDataList();

    protected JPanel createDetailPanel(T dataItem) {
        // 默认实现：简单展示toString内容，子类可重写提供更丰富的详情展示
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JTextArea textArea = new JTextArea(dataItem.toString());
        textArea.setEditable(false);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }
}