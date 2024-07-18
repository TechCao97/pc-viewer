package cn.billycao.pcviewer.frame;

import cn.billycao.pcviewer.config.DirectoryConfig;
import cn.billycao.pcviewer.entity.ConfigPathItem;
import lombok.Data;
import org.thymeleaf.util.StringUtils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ConfigTable {
    private IndexFrame indexFrame;
    private final String[] columnNames = {"路径", "锁定"};
    private List<ConfigPathItem> data;
    private List<ConfigPathItem> sourceData;
    private JScrollPane scrollPane;
    private JTable table;
    private JButton btnAdd;
    private JButton btnDelete;
    private JButton btnReset;
    private JButton btnSave;

    public ConfigTable(IndexFrame frame) {
        this.indexFrame = frame;
        List<ConfigPathItem> paths = frame.getConfig().getPaths();
        List<ConfigPathItem> data = DirectoryConfig.copyData(paths);
        this.sourceData = DirectoryConfig.copyData(paths);

        table = new JTable();
        this.setDataAndModel(data);
        scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setPreferredSize(new Dimension(IndexFrame.contentWith, IndexFrame.contentHeight * 3));
        btnAdd = new JButton("+");
        btnDelete = new JButton("-");
        btnReset = new JButton("重置");
        btnSave = new JButton("保存");
        int btnWidth = ((IndexFrame.contentWith - IndexFrame.contentGap) / 2 - IndexFrame.contentGap) / 2;
        btnAdd.setPreferredSize(new Dimension(btnWidth, IndexFrame.contentHeight));
        btnDelete.setPreferredSize(new Dimension(btnWidth, IndexFrame.contentHeight));
        btnReset.setPreferredSize(new Dimension(btnWidth, IndexFrame.contentHeight));
        btnSave.setPreferredSize(new Dimension(btnWidth, IndexFrame.contentHeight));

        btnAdd.addActionListener(e -> {
            this.data.add(new ConfigPathItem("", false));
            this.refreshModel();
            scrollPane.repaint();
            SwingUtilities.invokeLater(() -> {
                // 滚动到最下方
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            });
        });
        btnDelete.addActionListener(e -> {
            int[] selectedRows = this.table.getSelectedRows();
            Integer[] boxedArray = Arrays.stream(selectedRows).boxed().sorted(Comparator.reverseOrder()).toArray(Integer[]::new);
            for (int index : boxedArray) {
                this.data.remove(index);
            }
            this.refreshModel();
        });
        btnReset.addActionListener(e -> this.setDataAndModel(DirectoryConfig.copyData(this.sourceData)));
        btnSave.addActionListener(e -> {
            try {
                if (this.table.getCellEditor() != null) {
                    this.table.getCellEditor().stopCellEditing();
                }
                this.data = this.data.stream().filter(item -> !StringUtils.isEmpty(item.getPath())).collect(Collectors.toList());
                this.refreshModel();
                //更新视图数据
                this.sourceData = DirectoryConfig.copyData(this.data);
                //更新内存中的配置项
                this.indexFrame.setConfig(new DirectoryConfig(this.data));
                //更新持久化配置文件
                DirectoryConfig.write(this.data);

                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "保存成功", null, JOptionPane.INFORMATION_MESSAGE));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "保存失败", null, JOptionPane.ERROR_MESSAGE));
                ex.printStackTrace();
            }
        });
    }

    public void setDataAndModel(List<ConfigPathItem> data) {
        this.data = data;
        this.setModel(new TableModel(columnNames, convert(data)));
    }

    public void refreshModel() {
        this.setModel(new TableModel(columnNames, convert(this.data)));
    }

    public void setModel(TableModel model) {
        model.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            Object value = model.getValueAt(row, column);
            ConfigPathItem item = this.data.get(row);
            if (column == 0) {
                String s = (String) value;
                if (s.contains("\\")) {
                    s = s.replaceAll("\\\\+", "/");
                    model.setValueAt(s, row, column);
                }
                item.setPath(s);
            } else {
                item.setLock((Boolean) value);
            }
        });
        SwingUtilities.invokeLater(() -> {
            table.setModel(model);
            table.setRowHeight(IndexFrame.contentHeight);
            table.getTableHeader().setPreferredSize(new Dimension(0, IndexFrame.contentHeight));
            TableColumn column = table.getColumnModel().getColumn(0);
            column.setPreferredWidth(400);
        });
    }

    public void addTo(JPanel panel) {
        panel.add(scrollPane);
        panel.add(btnAdd);
        panel.add(btnDelete);
        panel.add(btnReset);
        panel.add(btnSave);
    }

    private static Object[][] convert(List<ConfigPathItem> list) {
        Object[][] result = new Object[list.size()][2];
        for (int i = 0; i < list.size(); i++) {
            ConfigPathItem item = list.get(i);
            result[i][0] = item.getPath();
            result[i][1] = item.getLock();
        }
        return result;
    }

    private static class TableModel extends AbstractTableModel {
        /*
         * 这里和刚才一样，定义列名和每个数据的值
         */
        String[] columnNames;
        Object[][] data;

        /**
         * 构造方法，初始化二维数组data对应的数据
         */
        public TableModel(String[] columnNames, Object[][] data) {
            this.columnNames = columnNames;
            this.data = data;
        }

        // 以下为继承自AbstractTableModle的方法，可以自定义

        /**
         * 得到列名
         */
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        /**
         * 重写方法，得到表格列数
         */
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * 得到表格行数
         */
        @Override
        public int getRowCount() {
            return data.length;
        }

        /**
         * 得到数据所对应对象
         */
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }

        /**
         * 得到指定列的数据类型
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return data[0][columnIndex].getClass();
        }

        /**
         * 指定设置数据单元是否可编辑.这里设置"姓名","学号"不可编辑
         */
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        /**
         * 如果数据单元为可编辑，则将编辑后的值替换原来的值
         */
        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            data[rowIndex][columnIndex] = value;
            /*通知监听器数据单元数据已经改变*/
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }
}
