package arithmetic.gui;

import arithmetic.user.User;
import arithmetic.user.UserManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 主窗口：左侧深色侧边栏 + 右侧 CardLayout 内容区。
 * 登录前只显示登录页（侧边栏隐藏），登录后侧边栏展开。
 */
public class MainFrame extends JFrame {

    // CardLayout 面板名
    public static final String PAGE_LOGIN       = "login";
    public static final String PAGE_DASHBOARD   = "dashboard";
    public static final String PAGE_CHALLENGE   = "challenge";
    public static final String PAGE_RESULT      = "result";
    public static final String PAGE_WRONGBOOK   = "wrongbook";
    public static final String PAGE_LEADERBOARD = "leaderboard";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    private final JPanel sidebarPanel;
    private JLabel sidebarUserLabel;
    private JLabel sidebarScoreLabel;
    private final Map<String, JPanel> menuItems = new LinkedHashMap<>();
    private String currentPage = "";

    private final LoginPanel loginPanel;
    private final DashboardPanel dashboardPanel;
    private final ChallengePanel challengePanel;
    private final ResultPanel resultPanel;
    private final WrongBookPanel wrongBookPanel;
    private final LeaderboardPanel leaderboardPanel;

    public MainFrame() {
        setTitle("小学口算挑战系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);

        // 初始化面板
        loginPanel       = new LoginPanel(this);
        dashboardPanel   = new DashboardPanel(this);
        challengePanel   = new ChallengePanel(this);
        resultPanel      = new ResultPanel(this);
        wrongBookPanel   = new WrongBookPanel(this);
        leaderboardPanel = new LeaderboardPanel(this);

        // 侧边栏
        sidebarPanel = buildSidebar();
        sidebarPanel.setVisible(false); // 登录前隐藏

        // 内容区
        contentPanel.setBackground(UIConstants.CONTENT_BG);
        contentPanel.add(loginPanel,       PAGE_LOGIN);
        contentPanel.add(dashboardPanel,   PAGE_DASHBOARD);
        contentPanel.add(challengePanel,   PAGE_CHALLENGE);
        contentPanel.add(resultPanel,      PAGE_RESULT);
        contentPanel.add(wrongBookPanel,   PAGE_WRONGBOOK);
        contentPanel.add(leaderboardPanel, PAGE_LEADERBOARD);

        // 主布局
        JPanel root = new JPanel(new BorderLayout());
        root.add(sidebarPanel, BorderLayout.WEST);
        root.add(contentPanel, BorderLayout.CENTER);
        setContentPane(root);

        showPage(PAGE_LOGIN);
    }

    // ========================
    //  侧边栏构建
    // ========================

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        sidebar.setBackground(UIConstants.SIDEBAR_BG);
        sidebar.setLayout(new BorderLayout());

        // ---- 顶部用户信息区 ----
        JPanel userSection = new JPanel();
        userSection.setOpaque(false);
        userSection.setLayout(new BoxLayout(userSection, BoxLayout.Y_AXIS));
        userSection.setBorder(new EmptyBorder(UIConstants.PAD_LARGE, UIConstants.PAD_MEDIUM, UIConstants.PAD_MEDIUM, UIConstants.PAD_MEDIUM));

        // 头像占位（圆形色块 + 首字母）
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.PRIMARY);
                g2.fillOval(0, 0, 48, 48);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 20));
                User u = UserManager.getInstance().getCurrentUser();
                String ch = (u != null) ? u.getNickname().substring(0, 1).toUpperCase() : "?";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(ch, (48 - fm.stringWidth(ch)) / 2, (48 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(48, 48); }
            @Override public Dimension getMaximumSize()   { return getPreferredSize(); }
        };
        avatar.setOpaque(false);
        avatar.setAlignmentX(Component.LEFT_ALIGNMENT);
        userSection.add(avatar);
        userSection.add(Box.createVerticalStrut(10));

        sidebarUserLabel = new JLabel("未登录");
        sidebarUserLabel.setFont(UIConstants.FONT_BODY_BOLD);
        sidebarUserLabel.setForeground(Color.WHITE);
        sidebarUserLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userSection.add(sidebarUserLabel);

        sidebarScoreLabel = new JLabel("积分: 0");
        sidebarScoreLabel.setFont(UIConstants.FONT_SMALL);
        sidebarScoreLabel.setForeground(UIConstants.SIDEBAR_TEXT);
        sidebarScoreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userSection.add(sidebarScoreLabel);

        sidebar.add(userSection, BorderLayout.NORTH);

        // ---- 导航菜单区 ----
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(UIConstants.PAD_SMALL, 0, UIConstants.PAD_SMALL, 0));

        addNavItem(nav, "首页", PAGE_DASHBOARD);
        addNavItem(nav, "开始挑战", PAGE_CHALLENGE);
        addNavItem(nav, "错题本", PAGE_WRONGBOOK);
        addNavItem(nav, "积分榜", PAGE_LEADERBOARD);

        nav.add(Box.createVerticalGlue());

        // 登出按钮
        JPanel logoutItem = createNavItem("退出登录", "logout");
        logoutItem.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { onLogout(); }
        });
        nav.add(logoutItem);
        nav.add(Box.createVerticalStrut(UIConstants.PAD_MEDIUM));

        sidebar.add(nav, BorderLayout.CENTER);
        return sidebar;
    }

    private void addNavItem(JPanel nav, String text, String pageName) {
        JPanel item = createNavItem(text, pageName);
        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (pageName.equals(PAGE_CHALLENGE)) {
                    challengePanel.resetForNewChallenge();
                } else if (pageName.equals(PAGE_WRONGBOOK)) {
                    wrongBookPanel.refresh();
                } else if (pageName.equals(PAGE_LEADERBOARD)) {
                    leaderboardPanel.refresh();
                } else if (pageName.equals(PAGE_DASHBOARD)) {
                    dashboardPanel.refresh();
                }
                showPage(pageName);
            }
            @Override public void mouseEntered(MouseEvent e) {
                if (!pageName.equals(currentPage)) item.setBackground(UIConstants.SIDEBAR_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!pageName.equals(currentPage)) item.setBackground(UIConstants.SIDEBAR_BG);
            }
        });
        menuItems.put(pageName, item);
        nav.add(item);
    }

    private JPanel createNavItem(String text, String name) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(UIConstants.SIDEBAR_BG);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        item.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 44));
        item.setBorder(new EmptyBorder(0, 20, 0, 12));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.FONT_BODY);
        lbl.setForeground(UIConstants.SIDEBAR_TEXT);
        item.add(lbl, BorderLayout.CENTER);
        return item;
    }

    // ========================
    //  页面切换
    // ========================

    /** 切换到指定页面并高亮侧边栏 */
    public void showPage(String pageName) {
        cardLayout.show(contentPanel, pageName);
        highlightMenuItem(pageName);
    }

    /** 高亮侧边栏当前选中项 */
    private void highlightMenuItem(String pageName) {
        currentPage = pageName;
        for (Map.Entry<String, JPanel> entry : menuItems.entrySet()) {
            if (entry.getKey().equals(pageName)) {
                entry.getValue().setBackground(UIConstants.SIDEBAR_ACTIVE);
            } else {
                entry.getValue().setBackground(UIConstants.SIDEBAR_BG);
            }
        }
    }

    // ========================
    //  登录/登出
    // ========================

    /** 登录成功后：展开侧边栏，刷新用户信息，跳转仪表盘 */
    public void onLoginSuccess() {
        sidebarPanel.setVisible(true);
        updateSidebarUserInfo();
        dashboardPanel.refresh();
        showPage(PAGE_DASHBOARD);
    }

    /** 登出：隐藏侧边栏，回到登录页 */
    public void onLogout() {
        UserManager.getInstance().logout();
        sidebarPanel.setVisible(false);
        showPage(PAGE_LOGIN);
    }

    /** 刷新侧边栏用户信息 */
    public void updateSidebarUserInfo() {
        User u = UserManager.getInstance().getCurrentUser();
        if (u != null) {
            sidebarUserLabel.setText(u.getNickname());
            sidebarScoreLabel.setText("积分: " + u.getScore());
        }
        sidebarPanel.repaint();
    }

    // ========================
    //  面板访问器
    // ========================

    public DashboardPanel getDashboardPanel()     { return dashboardPanel; }
    public ChallengePanel getChallengePanel()     { return challengePanel; }
    public ResultPanel getResultPanel()           { return resultPanel; }
    public WrongBookPanel getWrongBookPanel()     { return wrongBookPanel; }
    public LeaderboardPanel getLeaderboardPanel() { return leaderboardPanel; }
}
