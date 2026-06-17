package arithmetic.gui;

import arithmetic.user.Leaderboard;
import arithmetic.user.User;
import arithmetic.user.UserManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * 积分榜面板：TOP 10 排行，高亮当前用户，奖牌样式。
 */
public class LeaderboardPanel extends JPanel {

    private final MainFrame mainFrame;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public LeaderboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UIConstants.CONTENT_BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(UIConstants.PAD_LARGE, UIConstants.PAD_LARGE,
                UIConstants.PAD_LARGE, UIConstants.PAD_LARGE));

        // 顶部
        JLabel title = ComponentFactory.titleLabel("积分排行榜");
        title.setBorder(new EmptyBorder(0, 0, UIConstants.PAD_MEDIUM, 0));
        add(title, BorderLayout.NORTH);

        // 表格
        String[] columns = {"名次", "账号", "昵称", "积分", "挑战次数", "正确题数"};
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

        // 表头
        JTableHeader th = table.getTableHeader();
        th.setFont(UIConstants.FONT_BODY_BOLD);
        th.setBackground(UIConstants.CONTENT_BG);
        th.setForeground(UIConstants.TEXT_SECONDARY);
        th.setPreferredSize(new Dimension(0, 42));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.DIVIDER));

        // 列宽
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);

        // 名次列特殊渲染（奖牌颜色）
        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                lbl.setHorizontalAlignment(CENTER);
                lbl.setFont(UIConstants.FONT_BODY_BOLD);
                String rank = value.toString();
                switch (rank) {
                    case "1": lbl.setForeground(new Color(255, 193, 7)); break;
                    case "2": lbl.setForeground(new Color(158, 158, 158)); break;
                    case "3": lbl.setForeground(new Color(205, 127, 50)); break;
                    default:  lbl.setForeground(UIConstants.TEXT_SECONDARY); break;
                }
                return lbl;
            }
        });

        // 整行高亮当前用户 + 交替色
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                User current = UserManager.getInstance().getCurrentUser();
                boolean isMe = false;
                if (current != null && tableModel.getRowCount() > row) {
                    String rowUserId = tableModel.getValueAt(row, 1).toString();
                    isMe = current.getUserId().equals(rowUserId);
                }
                if (isMe) {
                    lbl.setBackground(new Color(239, 246, 255));
                    lbl.setFont(UIConstants.FONT_BODY_BOLD);
                } else if (sel) {
                    lbl.setBackground(new Color(219, 234, 254));
                    lbl.setFont(UIConstants.FONT_BODY);
                } else {
                    lbl.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    lbl.setFont(UIConstants.FONT_BODY);
                }
                lbl.setForeground(UIConstants.TEXT_PRIMARY);
                lbl.setOpaque(true);
                return lbl;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(UIConstants.CARD_BORDER));
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);
    }

    /** 刷新积分榜 */
    public void refresh() {
        tableModel.setRowCount(0);
        List<User> top = Leaderboard.getInstance().topN(10);
        for (int i = 0; i < top.size(); i++) {
            User u = top.get(i);
            String rank = String.valueOf(i + 1);
            tableModel.addRow(new Object[]{
                    rank,
                    u.getUserId(),
                    u.getNickname(),
                    u.getScore(),
                    u.getTotalChallenges(),
                    u.getTotalCorrect()
            });
        }
    }
}