package arithmetic.gui;

import arithmetic.Binaryoperation;
import arithmetic.Difficulty;
import arithmetic.challenge.Challenge;
import arithmetic.challenge.ChallengeResult;
import arithmetic.user.UserManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * 挑战答题面板：选择模式 → 选择难度 → 逐题作答 → 提交结算。
 * 两步骤选择 + 大号算式居中答题界面。
 */
public class ChallengePanel extends JPanel {

    private final MainFrame mainFrame;
    private final CardLayout innerCard = new CardLayout();
    private final JPanel innerPanel = new JPanel(innerCard);

    // 模式选择页
    private JPanel modePage;
    // 难度选择页
    private JPanel difficultyPage;
    // 答题界面
    private JPanel problemPage;
    private JLabel progressLabel;
    private JProgressBar progressBar;
    private JLabel problemLabel;
    private JTextField answerField;
    private JLabel feedbackLabel;
    private JButton submitBtn;

    private Challenge currentChallenge;
    private Difficulty.Mode selectedMode;
    private int currentIndex;

    public ChallengePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UIConstants.CONTENT_BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(UIConstants.PAD_LARGE, UIConstants.PAD_LARGE,
                UIConstants.PAD_LARGE, UIConstants.PAD_LARGE));

        innerPanel.setOpaque(false);
        innerPanel.add(buildModePage(), "mode");
        innerPanel.add(buildDifficultyPage(), "difficulty");
        innerPanel.add(buildProblemPage(), "problem");

        add(ComponentFactory.titleLabel("开始挑战"), BorderLayout.NORTH);
        add(innerPanel, BorderLayout.CENTER);
    }

    // ========================
    //  模式选择页（第一步）
    // ========================
    private JPanel buildModePage() {
        modePage = new JPanel(new GridBagLayout());
        modePage.setOpaque(false);

        JPanel grid = new JPanel(new GridLayout(1, 5, 10, 0));
        grid.setOpaque(false);

        grid.add(createModeCard(Difficulty.Mode.MIXED, "混合运算", "±×÷", "加减乘除随机混合", UIConstants.PRIMARY));
        grid.add(createModeCard(Difficulty.Mode.ADD_ONLY, "仅加法", "A + B", "全是加法题", UIConstants.SUCCESS));
        grid.add(createModeCard(Difficulty.Mode.SUB_ONLY, "仅减法", "A − B", "全是减法题", new Color(99, 102, 241)));
        grid.add(createModeCard(Difficulty.Mode.MUL_DIV, "仅乘除", "× or ÷", "乘除各半", new Color(245, 120, 50)));
//        grid.add(createModeCard(Difficulty.Mode.LONG_MIXED, "长混合", "翻倍", "题量翻倍混合", UIConstants.DANGER));

        modePage.add(grid);
        return modePage;
    }

    private JPanel createModeCard(Difficulty.Mode mode, String title, String badge, String desc, Color accent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fill(new RoundRectangle2D.Double(3, 3, getWidth() - 3, getHeight() - 3, 12, 12));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 4, getHeight() - 4, 12, 12));
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth() - 4, 4, 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 10, 20, 10));
        card.setPreferredSize(new Dimension(145, 190));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UIConstants.FONT_SUBTITLE);
        titleLbl.setForeground(accent);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel badgeLbl = new JLabel(badge);
        badgeLbl.setFont(new Font("Consolas", Font.BOLD, 22));
        badgeLbl.setForeground(accent);
        badgeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        badgeLbl.setBorder(new EmptyBorder(8, 0, 4, 0));

        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(UIConstants.FONT_SMALL);
        descLbl.setForeground(UIConstants.TEXT_SECONDARY);
        descLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton goBtn = ComponentFactory.primaryButton("选择");
        goBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        goBtn.addActionListener(e -> selectMode(mode));

        card.add(Box.createVerticalGlue());
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(badgeLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(descLbl);
        card.add(Box.createVerticalStrut(12));
        card.add(goBtn);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private void selectMode(Difficulty.Mode mode) {
        selectedMode = mode;
        // 重新构建难度页面以更新题数显示
        innerPanel.remove(difficultyPage);
        difficultyPage = buildDifficultyPage();
        innerPanel.add(difficultyPage, "difficulty");
        innerCard.show(innerPanel, "difficulty");
    }

    // ========================
    //  难度选择页（第二步）
    // ========================
    private JPanel buildDifficultyPage() {
        difficultyPage = new JPanel(new GridBagLayout());
        difficultyPage.setOpaque(false);

        JPanel grid = new JPanel(new GridLayout(1, 3, 20, 0));
        grid.setOpaque(false);

        grid.add(createDiffCard("L1 入门", "加减 [0,20]  乘除 [1,5]", "5 题 ×1分", UIConstants.SUCCESS, Difficulty.L1));
        grid.add(createDiffCard("L2 进阶", "加减 [0,50]  乘除 [1,8]", "10 题 ×2分", UIConstants.WARNING, Difficulty.L2));
        grid.add(createDiffCard("L3 满级", "加减 [0,100] 乘除 [1,10]", "20 题 ×3分", UIConstants.DANGER, Difficulty.L3));

        // 返回按钮
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(grid, BorderLayout.CENTER);

        JButton backBtn = new JButton("← 返回选择模式");
        backBtn.setFont(UIConstants.FONT_BODY);
        backBtn.setForeground(UIConstants.TEXT_SECONDARY);
        backBtn.setBorder(new EmptyBorder(8, 0, 0, 0));
        backBtn.setContentAreaFilled(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> innerCard.show(innerPanel, "mode"));
        wrapper.add(backBtn, BorderLayout.SOUTH);

        difficultyPage.add(wrapper);
        return difficultyPage;
    }

    private JPanel createDiffCard(String title, String desc, String info, Color accent, Difficulty diff) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fill(new RoundRectangle2D.Double(3, 3, getWidth() - 3, getHeight() - 3, 12, 12));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 4, getHeight() - 4, 12, 12));
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth() - 4, 4, 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(22, 18, 22, 18));
        card.setPreferredSize(new Dimension(220, 220));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UIConstants.FONT_SUBTITLE);
        titleLbl.setForeground(accent);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(UIConstants.FONT_BODY);
        descLbl.setForeground(UIConstants.TEXT_SECONDARY);
        descLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel infoLbl = new JLabel(info);
        infoLbl.setFont(UIConstants.FONT_SMALL);
        infoLbl.setForeground(UIConstants.TEXT_LIGHT);
        infoLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 计算实际题数，selectedMode 为 null 时显示预估值
        int actualCount = (selectedMode != null) ? (diff.problemCount * selectedMode.problemMultiplier) : diff.problemCount;
        JLabel countLbl = new JLabel("实做 " + actualCount + " 题");
        countLbl.setFont(UIConstants.FONT_SMALL);
        countLbl.setForeground(UIConstants.WARNING);
        countLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton goBtn = ComponentFactory.primaryButton("开始");
        goBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        goBtn.addActionListener(e -> startChallenge(diff));

        card.add(Box.createVerticalGlue());
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(descLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(infoLbl);
        card.add(Box.createVerticalStrut(2));
        card.add(countLbl);
        card.add(Box.createVerticalStrut(12));
        card.add(goBtn);
        card.add(Box.createVerticalGlue());

        return card;
    }

    // ========================
    //  答题页
    // ========================
    private JPanel buildProblemPage() {
        problemPage = new JPanel(new BorderLayout(0, 16));
        problemPage.setOpaque(false);
        problemPage.setBorder(new EmptyBorder(20, 0, 0, 0));

        // 进度条区域
        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setOpaque(false);
        progressLabel = new JLabel("第 1 / 5 题");
        progressLabel.setFont(UIConstants.FONT_BODY_BOLD);
        progressLabel.setForeground(UIConstants.TEXT_PRIMARY);
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(0, 10));
        progressBar.setBorderPainted(false);
        progressBar.setForeground(UIConstants.PRIMARY);
        progressBar.setBackground(UIConstants.CARD_BORDER);
        topBar.add(progressLabel, BorderLayout.WEST);
        topBar.add(progressBar, BorderLayout.CENTER);
        problemPage.add(topBar, BorderLayout.NORTH);

        // 算式居中
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JPanel problemCard = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fill(new RoundRectangle2D.Double(3, 3, getWidth() - 3, getHeight() - 3, 16, 16));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 4, getHeight() - 4, 16, 16));
                g2.dispose();
            }
        };
        problemCard.setOpaque(false);
        problemCard.setLayout(new BoxLayout(problemCard, BoxLayout.Y_AXIS));
        problemCard.setBorder(new EmptyBorder(36, 50, 36, 50));
        problemCard.setPreferredSize(new Dimension(480, 250));

        problemLabel = new JLabel("3 + 5 = ?");
        problemLabel.setFont(UIConstants.FONT_PROBLEM);
        problemLabel.setForeground(UIConstants.TEXT_PRIMARY);
        problemLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        answerField = new JTextField();
        answerField.setFont(new Font("Consolas", Font.PLAIN, 28));
        answerField.setHorizontalAlignment(JTextField.CENTER);
        answerField.setMaximumSize(new Dimension(200, 48));
        answerField.setPreferredSize(new Dimension(200, 48));
        answerField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.INPUT_BORDER, 2, true),
                new EmptyBorder(6, 14, 6, 14)));
        answerField.setAlignmentX(Component.CENTER_ALIGNMENT);
        answerField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) submitAnswer();
            }
        });

        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(UIConstants.FONT_BODY);
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        submitBtn = ComponentFactory.primaryButton("提交答案");
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitBtn.addActionListener(e -> submitAnswer());

        problemCard.add(problemLabel);
        problemCard.add(Box.createVerticalStrut(20));
        problemCard.add(answerField);
        problemCard.add(Box.createVerticalStrut(10));
        problemCard.add(feedbackLabel);
        problemCard.add(Box.createVerticalStrut(14));
        problemCard.add(submitBtn);

        center.add(problemCard);
        problemPage.add(center, BorderLayout.CENTER);

        return problemPage;
    }

    // ========================
    //  逻辑
    // ========================

    /** 从侧边栏/仪表盘进入时重置到模式选择页 */
    public void resetForNewChallenge() {
        innerCard.show(innerPanel, "mode");
        selectedMode = null;
        currentChallenge = null;
        currentIndex = 0;
    }

    private void startChallenge(Difficulty d) {
        currentChallenge = new Challenge(UserManager.getInstance().getCurrentUser(), d, selectedMode);
        currentChallenge.generate();
        currentIndex = 0;
        showCurrentProblem();
        innerCard.show(innerPanel, "problem");
        answerField.requestFocusInWindow();
    }

    private void showCurrentProblem() {
        int total = currentChallenge.getProblemCount();
        progressLabel.setText(String.format("第 %d / %d 题", currentIndex + 1, total));
        progressBar.setValue((int) ((currentIndex) * 100.0 / total));
        Binaryoperation p = currentChallenge.getProblems().get(currentIndex);
        problemLabel.setText(p.getLeftOperand() + " " + p.getOperator() + " " + p.getRightOperand() + " = ?");
        answerField.setText("");
        feedbackLabel.setText(" ");
        submitBtn.setEnabled(true);
    }

    private void submitAnswer() {
        String text = answerField.getText().trim();
        if (text.isEmpty()) {
            feedbackLabel.setText("请输入答案");
            feedbackLabel.setForeground(UIConstants.WARNING);
            return;
        }
        try {
            int ans = Integer.parseInt(text);
            currentChallenge.submitAnswer(currentIndex, ans);

            Binaryoperation p = currentChallenge.getProblems().get(currentIndex);
            if (ans == p.getResult()) {
                feedbackLabel.setText("正确！");
                feedbackLabel.setForeground(UIConstants.SUCCESS);
            } else {
                feedbackLabel.setText("错误，正确答案是 " + p.getResult());
                feedbackLabel.setForeground(UIConstants.DANGER);
            }
            submitBtn.setEnabled(false);

            Timer timer = new Timer(800, evt -> {
                currentIndex++;
                if (currentIndex < currentChallenge.getProblemCount()) {
                    showCurrentProblem();
                    answerField.requestFocusInWindow();
                } else {
                    finishChallenge();
                }
            });
            timer.setRepeats(false);
            timer.start();

        } catch (NumberFormatException ex) {
            feedbackLabel.setText("请输入有效数字");
            feedbackLabel.setForeground(UIConstants.WARNING);
        }
    }

    private void finishChallenge() {
        ChallengeResult result = currentChallenge.finish();
        mainFrame.getResultPanel().showResult(result);
        mainFrame.showPage(MainFrame.PAGE_RESULT);
    }
}
