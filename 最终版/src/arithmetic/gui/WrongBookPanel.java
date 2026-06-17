package arithmetic.gui;

import arithmetic.user.User;
import arithmetic.user.UserManager;
import arithmetic.user.WrongEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * 错题本面板：现代无网格表格，支持"标记掌握"操作。
 */
public class WrongBookPanel extends JPanel {

    private final MainFrame mainFrame;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel summaryLabel;

    public WrongBookPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UIConstants.CONTENT_BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(UIConstants.PAD_LARGE, UIConstants.PAD_LARGE,
                UIConstants.PAD_LARGE, UIConstants.PAD_LARGE));

        // 顶部标题 + 摘要
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, UIConstants.PAD_MEDIUM, 0));
        header.add(ComponentFactory.titleLabel("错题本"), BorderLayout.WEST);

        summaryLabel = ComponentFactory.bodyLabel("共 0 题，未掌握 0 题");
        header.add(summaryLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // 表格
        String[] columns = {"#", "题目", "你的答案", "正确答案", "难度", "错误次数", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(UIConstants.FONT_BODY);
        table.setRowHeight(42);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(UIConstants.TEXT_PRIMARY);
        table.setBackground(Color.WHITE);

        // 表头样式
        JTableHeader th = table.getTableHeader();
        th.setFont(UIConstants.FONT_BODY_BOLD);
        th.setBackground(UIConstants.CONTENT_BG);
        th.setForeground(UIConstants.TEXT_SECONDARY);
        th.setPreferredSize(new Dimension(0, 42));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.DIVIDER));

        // 列宽
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(60);
        table.getColumnModel().getColumn(5).setPreferredWidth(70);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);

        // 状态列着色
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                lbl.setHorizontalAlignment(CENTER);
                if ("已掌握".equals(value)) {
                    lbl.setForeground(UIConstants.SUCCESS);
                } else {
                    lbl.setForeground(UIConstants.DANGER);
                }
                return lbl;
            }
        });

        // 整行交替色
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                lbl.setFont(UIConstants.FONT_BODY);
                lbl.setForeground(UIConstants.TEXT_PRIMARY);
                lbl.setOpaque(true);
                if (sel) {
                    lbl.setBackground(new Color(219, 234, 254));
                } else {
                    lbl.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                return lbl;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // 底部按钮
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton markBtn = ComponentFactory.successButton("标记掌握");
        markBtn.addActionListener(e -> markSelectedMastered());

        bottom.add(markBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    /** 刷新错题本数据 */
    public void refresh() {
        tableModel.setRowCount(0);
        User u = UserManager.getInstance().getCurrentUser();
        if (u == null) return;

        List<WrongEntry> entries = u.getWrongBook().getAll();
        int activeCount = 0;
        for (int i = 0; i < entries.size(); i++) {
            WrongEntry e = entries.get(i);
            if (!e.isMastered()) activeCount++;
            tableModel.addRow(new Object[]{
                    i + 1,
                    e.getProblem().toString() + " = ?",
                    e.getUserAnswer() == Integer.MIN_VALUE ? "未作答" : String.valueOf(e.getUserAnswer()),
                    String.valueOf(e.getProblem().getResult()),
                    e.getDifficulty().name(),
                    e.getRetryCount(),
                    e.isMastered() ? "已掌握" : "未掌握"
            });
        }
        summaryLabel.setText(String.format("共 %d 题，未掌握 %d 题", entries.size(), activeCount));
    }

    private void markSelectedMastered() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "请先选择要标记的错题", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        User u = UserManager.getInstance().getCurrentUser();
        if (u == null) return;
        for (int row : rows) {
            u.getWrongBook().markMastered(row);
        }
        UserManager.getInstance().saveCurrentUser();
        refresh();
    }
}