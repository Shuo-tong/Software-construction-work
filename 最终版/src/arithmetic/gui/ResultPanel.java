package arithmetic.gui;

import arithmetic.Binaryoperation;
import arithmetic.challenge.ChallengeResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * 挑战结果面板：环形正确率图 + 详细统计 + 错题清单 + 操作按钮。
 */
public class ResultPanel extends JPanel {

    private final MainFrame mainFrame;
    private JLabel titleLabel;
    private JLabel scoreLabel, correctLabel, wrongLabel, timeLabel, bonusLabel;
    private JPanel wrongListPanel;
    private JPanel ringPanel;
    private int correctCount, totalCount;

    public ResultPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UIConstants.CONTENT_BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(UIConstants.PAD_LARGE, UIConstants.PAD_LARGE,
                UIConstants.PAD_LARGE, UIConstants.PAD_LARGE));

        titleLabel = ComponentFactory.titleLabel("挑战结果");
        titleLabel.setBorder(new EmptyBorder(0, 0, UIConstants.PAD_MEDIUM, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 中央区域：左侧环形图 + 右侧统计
        JPanel center = new JPanel(new BorderLayout(UIConstants.PAD_LARGE, 0));
        center.setOpaque(false);

        // 左：环形正确率
        ringPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // 卡片背景
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                // 灰色底环
                int cx = getWidth() / 2, cy = getHeight() / 2 - 10;
                int r = 70;
                g2.setStroke(new BasicStroke(14, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(UIConstants.CARD_BORDER);
                g2.drawArc(cx - r, cy - r, r * 2, r * 2, 0, 360);
                // 正确率弧
                if (totalCount > 0) {
                    float ratio = (float) correctCount / totalCount;
                    g2.setColor(ratio >= 0.8f ? UIConstants.SUCCESS : ratio >= 0.5f ? UIConstants.WARNING : UIConstants.DANGER);
                    g2.drawArc(cx - r, cy - r, r * 2, r * 2, 90, -(int)(360 * ratio));
                    // 中心百分比文字
                    g2.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 28));
                    String pct = (int)(ratio * 100) + "%";
                    FontMetrics fm = g2.getFontMetrics();
                    g2.setColor(UIConstants.TEXT_PRIMARY);
                    g2.drawString(pct, cx - fm.stringWidth(pct) / 2, cy + fm.getAscent() / 3);
                }
                // 副标签
                g2.setFont(UIConstants.FONT_SMALL);
                g2.setColor(UIConstants.TEXT_SECONDARY);
                String sub = "正确率";
                FontMetrics fm2 = g2.getFontMetrics();
                g2.drawString(sub, cx - fm2.stringWidth(sub) / 2, cy + r + 30);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(220, 260); }
        };
        ringPanel.setOpaque(false);

        // 右：详细统计卡片
        JPanel statsPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // 阴影
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fill(new RoundRectangle2D.Double(2, 2, getWidth() - 2, getHeight() - 2, 16, 16));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 3, getHeight() - 3, 16, 16));
                g2.dispose();
            }
        };
        statsPanel.setOpaque(false);
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(new EmptyBorder(UIConstants.PAD_LARGE, UIConstants.PAD_LARGE, UIConstants.PAD_LARGE, UIConstants.PAD_LARGE));

        scoreLabel   = statRow(statsPanel, "获得积分");
        correctLabel = statRow(statsPanel, "正确题数");
        wrongLabel   = statRow(statsPanel, "错误题数");
        timeLabel    = statRow(statsPanel, "用时");
        bonusLabel   = statRow(statsPanel, "额外奖励");

        center.add(ringPanel, BorderLayout.WEST);
        center.add(statsPanel, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // 底部：错题列表 + 按钮
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(UIConstants.PAD_MEDIUM, 0, 0, 0));

        wrongListPanel = new JPanel();
        wrongListPanel.setOpaque(false);
        wrongListPanel.setLayout(new BoxLayout(wrongListPanel, BoxLayout.Y_AXIS));
        JScrollPane sp = new JScrollPane(wrongListPanel);
        sp.setPreferredSize(new Dimension(0, 120));
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(UIConstants.CONTENT_BG);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, UIConstants.PAD_MEDIUM, 8));
        btnRow.setOpaque(false);
        JButton againBtn = ComponentFactory.primaryButton("再来一局");
        againBtn.addActionListener(e -> {
            mainFrame.getChallengePanel().resetForNewChallenge();
            mainFrame.showPage(MainFrame.PAGE_CHALLENGE);
        });
        JButton homeBtn = ComponentFactory.ghostButton("返回仪表盘");
        homeBtn.addActionListener(e -> {
            mainFrame.getDashboardPanel().refresh();
            mainFrame.showPage(MainFrame.PAGE_DASHBOARD);
        });
        btnRow.add(againBtn);
        btnRow.add(homeBtn);

        bottom.add(sp, BorderLayout.CENTER);
        bottom.add(btnRow, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
    }

    /** 展示挑战结果 */
    public void showResult(ChallengeResult result) {
        this.correctCount = result.getCorrect();
        this.totalCount   = result.getTotal();

        scoreLabel.setText(String.valueOf(result.getScoreEarned()));
        correctLabel.setText(result.getCorrect() + " / " + result.getTotal());
        wrongLabel.setText(String.valueOf(result.getWrong()));
        timeLabel.setText(String.format("%.1f 秒", result.getTimeUsedMs() / 1000.0));
        boolean allRight = result.getCorrect() == result.getTotal() && result.getTotal() > 0;
        bonusLabel.setText(allRight ? "+" + result.getDifficulty().bonusAllRight + " 分 (全对奖励！)" : "无");
        bonusLabel.setForeground(allRight ? UIConstants.SUCCESS : UIConstants.TEXT_SECONDARY);

        // 错题列表
        wrongListPanel.removeAll();
        List<Binaryoperation> wrongs = result.getWrongProblems();
        if (wrongs.isEmpty()) {
            JLabel noWrong = new JLabel("全部正确，太棒了！");
            noWrong.setFont(UIConstants.FONT_BODY_BOLD);
            noWrong.setForeground(UIConstants.SUCCESS);
            noWrong.setAlignmentX(Component.LEFT_ALIGNMENT);
            wrongListPanel.add(noWrong);
        } else {
            JLabel hdr = new JLabel("错题清单（已记入错题本）：");
            hdr.setFont(UIConstants.FONT_BODY_BOLD);
            hdr.setForeground(UIConstants.TEXT_PRIMARY);
            hdr.setAlignmentX(Component.LEFT_ALIGNMENT);
            wrongListPanel.add(hdr);
            wrongListPanel.add(Box.createVerticalStrut(4));
            for (Binaryoperation p : wrongs) {
                JLabel item = new JLabel("  - " + p.fullString());
                item.setFont(UIConstants.FONT_BODY);
                item.setForeground(UIConstants.DANGER);
                item.setAlignmentX(Component.LEFT_ALIGNMENT);
                wrongListPanel.add(item);
            }
        }
        wrongListPanel.revalidate();
        wrongListPanel.repaint();
        // 刷新环形图
        ringPanel.repaint();
        repaint();
    }

    private JLabel statRow(JPanel parent, String label) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row.setBorder(new EmptyBorder(4, 0, 4, 0));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_BODY);
        lbl.setForeground(UIConstants.TEXT_SECONDARY);

        JLabel val = new JLabel("-");
        val.setFont(UIConstants.FONT_BODY_BOLD);
        val.setForeground(UIConstants.TEXT_PRIMARY);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        parent.add(row);
        return val;
    }
}