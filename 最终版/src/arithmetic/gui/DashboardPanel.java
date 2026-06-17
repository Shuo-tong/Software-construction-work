package arithmetic.gui;

import arithmetic.Difficulty;
import arithmetic.dao.ChallengeRecordDAO;
import arithmetic.user.Leaderboard;
import arithmetic.user.User;
import arithmetic.user.UserManager;
import arithmetic.user.WrongEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * 仪表盘面板：欢迎语 + 统计卡片 + 最近挑战记录 + 待复习错题。
 */
public class DashboardPanel extends JPanel {

    private final MainFrame mainFrame;
    private JLabel welcomeLabel;
    private JLabel scoreValue, challengeValue, correctValue, wrongValue, rankValue;

    // 待刷新的子面板（卡片外容器 + 内部内容面板）
    private JPanel recentCard;
    private JPanel recentContent;
    private JPanel wrongCard;
    private JPanel wrongContent;

    private static final Color CARD_SCORE     = new Color(35, 92, 235);
    private static final Color CARD_CHALLENGE = new Color(110, 50, 225);
    private static final Color CARD_CORRECT   = new Color(16, 185, 70);
    private static final Color CARD_WRONG     = new Color(245, 148, 0);
    private static final Color CARD_RANK      = new Color(225, 45, 120);

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UIConstants.CONTENT_BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(UIConstants.PAD_LARGE, UIConstants.PAD_LARGE,
                UIConstants.PAD_LARGE, UIConstants.PAD_LARGE));

        // ---- 顶部欢迎 ----
        welcomeLabel = new JLabel("欢迎回来！");
        welcomeLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 28));
        welcomeLabel.setForeground(UIConstants.TEXT_PRIMARY);
        welcomeLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(welcomeLabel, BorderLayout.NORTH);

        // ---- 统计卡片区 ----
        JPanel statsGrid = new JPanel(new GridLayout(1, 5, 16, 0));
        statsGrid.setOpaque(false);

        scoreValue     = createStatCard(statsGrid, "总积分", "0", CARD_SCORE);
        challengeValue = createStatCard(statsGrid, "挑战次数", "0", CARD_CHALLENGE);
        correctValue   = createStatCard(statsGrid, "累计正确", "0", CARD_CORRECT);
        wrongValue     = createStatCard(statsGrid, "错题数", "0", CARD_WRONG);
        rankValue      = createStatCard(statsGrid, "当前排名", "-", CARD_RANK);

        // ---- 下方两栏：最近挑战 + 待复习错题 ----
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 16, 0));
        bottomRow.setOpaque(false);
        bottomRow.setBorder(new EmptyBorder(16, 0, 0, 0));

        // 左栏卡片（标题 + 独立内容面板）
        recentCard = sectionCard("最近挑战记录");
        recentContent = new JPanel();
        recentContent.setOpaque(false);
        recentContent.setLayout(new BoxLayout(recentContent, BoxLayout.Y_AXIS));
        recentCard.add(recentContent, BorderLayout.CENTER);

        // 右栏卡片（标题 + 独立内容面板）
        wrongCard = sectionCard("待复习错题");
        wrongContent = new JPanel();
        wrongContent.setOpaque(false);
        wrongContent.setLayout(new BoxLayout(wrongContent, BoxLayout.Y_AXIS));
        wrongCard.add(wrongContent, BorderLayout.CENTER);

        bottomRow.add(recentCard);
        bottomRow.add(wrongCard);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(statsGrid, BorderLayout.NORTH);
        center.add(bottomRow, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    // =========================================
    //  最近挑战记录
    // =========================================
    private void populateRecent() {
        recentContent.removeAll();

        User u = UserManager.getInstance().getCurrentUser();
        if (u == null) return;

        List<ChallengeRecordDAO.ChallengeRecord> records =
                new ChallengeRecordDAO().findRecent(u.getUserId(), 5);

        if (records.isEmpty()) {
            JLabel empty = new JLabel("还没有挑战记录，去试试吧！");
            empty.setFont(UIConstants.FONT_BODY);
            empty.setForeground(UIConstants.TEXT_LIGHT);
            empty.setBorder(new EmptyBorder(20, 12, 20, 12));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            recentContent.add(empty);
        } else {
            // 表头行
            JPanel header = recordRow("难度", "模式", "结果", "得分", "用时", true, UIConstants.TEXT_SECONDARY);
            recentContent.add(header);

            for (ChallengeRecordDAO.ChallengeRecord r : records) {
                String diffStr = r.difficulty.name();
                String modeStr = r.mode != null ? r.mode.displayName : "混合";
                String result  = r.correct + "/" + r.total;
                String score   = "+" + r.scoreEarned;
                String time    = String.format("%.1fs", r.timeUsedMs / 1000.0);
                JPanel row = recordRow(diffStr, modeStr, result, score, time, false, UIConstants.TEXT_PRIMARY);
                recentContent.add(row);
            }
        }

        recentContent.revalidate();
        recentContent.repaint();
    }

    private JPanel recordRow(String a, String b, String c, String d, String e,
                             boolean isHeader, Color fg) {
        JPanel row = new JPanel(new GridLayout(1, 5, 6, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row.setBorder(new EmptyBorder(4, 8, 4, 8));

        Font font = isHeader
                ? new Font("Microsoft YaHei UI", Font.BOLD, 12)
                : UIConstants.FONT_BODY;

        row.add(cellLabel(a, font, fg));
        row.add(cellLabel(b, font, fg));
        row.add(cellLabel(c, font, fg));
        row.add(cellLabel(d, font, fg));
        row.add(cellLabel(e, font, fg));

        return row;
    }

    private JLabel cellLabel(String text, Font font, Color fg) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(fg);
        return lbl;
    }

    // =========================================
    //  待复习错题
    // =========================================
    private void populateWrong() {
        wrongContent.removeAll();

        User u = UserManager.getInstance().getCurrentUser();
        if (u == null) return;

        List<WrongEntry> active = u.getWrongBook().getActive();
        int showCount = Math.min(active.size(), 4);

        if (active.isEmpty()) {
            JLabel empty = new JLabel("暂无错题，继续保持！");
            empty.setFont(UIConstants.FONT_BODY);
            empty.setForeground(UIConstants.TEXT_LIGHT);
            empty.setBorder(new EmptyBorder(20, 12, 20, 12));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            wrongContent.add(empty);
        } else {
            // 顶上提示：还有 X 道未掌握
            JLabel hint = new JLabel("还有 " + active.size() + " 道未掌握");
            hint.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
            hint.setForeground(UIConstants.WARNING);
            hint.setBorder(new EmptyBorder(0, 10, 6, 0));
            hint.setAlignmentX(Component.LEFT_ALIGNMENT);
            wrongContent.add(hint);

            for (int i = 0; i < showCount; i++) {
                WrongEntry we = active.get(i);
                JPanel entryPanel = createWrongEntryPanel(we);
                wrongContent.add(entryPanel);
                if (i < showCount - 1) {
                    wrongContent.add(Box.createVerticalStrut(4));
                }
            }

            if (active.size() > showCount) {
                JLabel more = new JLabel("还有 " + (active.size() - showCount) + " 道 → 去错题本");
                more.setFont(UIConstants.FONT_SMALL);
                more.setForeground(UIConstants.PRIMARY);
                more.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                more.setBorder(new EmptyBorder(4, 10, 0, 0));
                more.setAlignmentX(Component.LEFT_ALIGNMENT);
                more.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                        mainFrame.getWrongBookPanel().refresh();
                        mainFrame.showPage(MainFrame.PAGE_WRONGBOOK);
                    }
                });
                wrongContent.add(more);
            }
        }

        wrongContent.revalidate();
        wrongContent.repaint();
    }

    private JPanel createWrongEntryPanel(WrongEntry we) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        p.setBorder(new EmptyBorder(4, 8, 4, 8));

//        JLabel icon = new JLabel("✗");
//        icon.setFont(new Font("Arial", Font.BOLD, 16));
//        icon.setForeground(UIConstants.DANGER);

        String text = we.getProblem().toString()
                + " = " + we.getUserAnswer()
                + "  [正确:" + we.getProblem().getResult() + "]";
        JLabel desc = new JLabel(text);
        desc.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
        desc.setForeground(UIConstants.TEXT_PRIMARY);

////        JLabel retry = new JLabel("×" + we.getRetryCount());
//        retry.setFont(new Font("Arial", Font.BOLD, 12));
//        retry.setForeground(UIConstants.WARNING);

//        p.add(icon, BorderLayout.WEST);
        p.add(desc, BorderLayout.CENTER);
//        p.add(retry, BorderLayout.EAST);

        return p;
    }

    // =========================================
    //  公共卡片容器
    // =========================================
    private JPanel sectionCard(String title) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fill(new RoundRectangle2D.Double(2, 2, getWidth() - 2, getHeight() - 2, 12, 12));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 3, getHeight() - 3, 12, 12));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 15));
        titleLbl.setForeground(UIConstants.TEXT_PRIMARY);
        titleLbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        card.add(titleLbl, BorderLayout.NORTH);

        return card;
    }

    // =========================================
    //  刷新
    // =========================================

    /** 刷新仪表盘所有数据 */
    public void refresh() {
        User u = UserManager.getInstance().getCurrentUser();
        if (u == null) return;

        welcomeLabel.setText("欢迎回来，" + u.getNickname() + "！");
        scoreValue.setText(String.valueOf(u.getScore()));
        challengeValue.setText(String.valueOf(u.getTotalChallenges()));
        correctValue.setText(String.valueOf(u.getTotalCorrect()));
        wrongValue.setText(String.valueOf(u.getWrongBook().activeCount()));
        int rank = Leaderboard.getInstance().rankOf(u.getUserId());
        rankValue.setText(rank > 0 ? "第 " + rank + " 名" : "-");

        // 刷新下方两栏
        populateRecent();
        populateWrong();

        // 同步侧边栏
        mainFrame.updateSidebarUserInfo();
    }

    // =========================================
    //  统计卡片
    // =========================================
    private JLabel createStatCard(JPanel parent, String title, String value, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 14));
                g2.fill(new RoundRectangle2D.Double(2, 2, getWidth() - 2, getHeight() - 2, 12, 12));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 3, getHeight() - 3, 12, 12));
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth() - 3, 5, 5, 5);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 14, 14, 14));
        card.setPreferredSize(new Dimension(130, 118));
        card.setMinimumSize(new Dimension(100, 95));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 13));
        titleLbl.setForeground(UIConstants.TEXT_SECONDARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 32));
        valueLbl.setForeground(accentColor);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(valueLbl);

        parent.add(card);
        return valueLbl;
    }
}
