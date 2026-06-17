package arithmetic.gui;

import arithmetic.user.UserManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * 登录/注册面板：居中圆角卡片，GridBagLayout 精确对齐。
 * 登录与注册通过顶部标签页切换，结构更清晰。
 */
public class LoginPanel extends JPanel {

    private final MainFrame mainFrame;

    // 登录表单
    private JTextField loginIdField;
    private JPasswordField loginPwField;

    // 注册表单
    private JTextField regIdField;
    private JPasswordField regPwField;
    private JTextField regNickField;

    // 标签页切换
    private final CardLayout formCard = new CardLayout();
    private final JPanel formPanel = new JPanel(formCard);

    private JLabel messageLabel;

    /** 表单列宽常量 */
    private static final int FIELD_WIDTH  = 300;
    private static final int FIELD_HEIGHT = 42;
    private static final int LABEL_WIDTH  = 60;
    private static final int ROW_GAP      = 16;
    private static final int SECTION_GAP  = 28;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(UIConstants.CONTENT_BG);

        // ============ 居中卡片 ============
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 16));
                g2.fill(new RoundRectangle2D.Double(4, 4, getWidth() - 4, getHeight() - 4, 16, 16));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 5, getHeight() - 5, 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(420, 440));
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(36, 44, 28, 44));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0;
        gc.gridwidth = 2;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        gc.insets = new Insets(0, 0, 4, 0);

        // ---- 标题 ----
        JLabel title = new JLabel("小学口算挑战", SwingConstants.CENTER);
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(UIConstants.TEXT_PRIMARY);
        card.add(title, gc);

        gc.gridy++;
        gc.insets = new Insets(0, 0, 0, 0);
        JLabel subtitle = new JLabel("登录账号开始练习", SwingConstants.CENTER);
        subtitle.setFont(UIConstants.FONT_SMALL);
        subtitle.setForeground(UIConstants.TEXT_LIGHT);
        card.add(subtitle, gc);

        gc.gridy++;
        gc.insets = new Insets(0, 0, SECTION_GAP, 0);
        card.add(ComponentFactory.divider(), gc);

        // ---- 表单区（CardLayout 切换） ----
        formPanel.setOpaque(false);
        formPanel.add(buildLoginForm(), "login");
        formPanel.add(buildRegisterForm(), "register");

        gc.gridy++;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.weighty = 1.0;
        gc.fill = GridBagConstraints.BOTH;
        card.add(formPanel, gc);
        gc.weighty = 0.0;
        gc.fill = GridBagConstraints.HORIZONTAL;

        // ---- 消息标签 ----
        gc.gridy++;
        gc.insets = new Insets(12, 0, 0, 0);
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(UIConstants.FONT_SMALL);
        card.add(messageLabel, gc);

        add(card);
    }

    // ========================
    //  登录表单
    // ========================
    private JPanel buildLoginForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;

        loginIdField = ComponentFactory.styledTextField("请输入用户名");
        loginPwField = ComponentFactory.styledPasswordField("请输入密码");
        setFieldSize(loginIdField);
        setFieldSize(loginPwField);

        // 用户名
        gc.gridx = 0; gc.gridy = 0;
        gc.gridwidth = 1;
        gc.weightx = 0.0;
        gc.insets = new Insets(0, 0, 0, 0);
        JLabel idLabel = createFormLabel("用户名");
        panel.add(idLabel, gc);

        gc.gridx = 1; gc.weightx = 1.0;
        gc.insets = new Insets(0, 0, 0, 0);
        panel.add(loginIdField, gc);

        // 密码
        gc.gridx = 0; gc.gridy = 1;
        gc.weightx = 0.0;
        gc.insets = new Insets(ROW_GAP, 0, 0, 0);
        JLabel pwLabel = createFormLabel("密  码");
        panel.add(pwLabel, gc);

        gc.gridx = 1; gc.weightx = 1.0;
        gc.insets = new Insets(ROW_GAP, 0, 0, 0);
        panel.add(loginPwField, gc);

        // 登录按钮
        gc.gridx = 0; gc.gridy = 2;
        gc.gridwidth = 2;
        gc.weightx = 1.0;
        gc.insets = new Insets(SECTION_GAP, 0, 0, 0);
        JButton loginBtn = ComponentFactory.primaryButton("登  录");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT));
        panel.add(loginBtn, gc);
        loginBtn.addActionListener(e -> doLogin());

        // 切换注册
        gc.gridy = 3;
        gc.insets = new Insets(12, 0, 0, 0);
        JButton toRegBtn = ComponentFactory.ghostButton("没有账号？去注册");
        toRegBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panel.add(toRegBtn, gc);
        toRegBtn.addActionListener(e -> {
            showMessage(" ", UIConstants.CONTENT_BG);
            formCard.show(formPanel, "register");
        });

        return panel;
    }

    // ========================
    //  注册表单
    // ========================
    private JPanel buildRegisterForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;

        regIdField   = ComponentFactory.styledTextField("请输入用户名");
        regPwField   = ComponentFactory.styledPasswordField("请输入密码");
        regNickField = ComponentFactory.styledTextField("可选，留空使用用户名");
        setFieldSize(regIdField);
        setFieldSize(regPwField);
        setFieldSize(regNickField);

        // 用户名
        gc.gridx = 0; gc.gridy = 0;
        gc.gridwidth = 1;
        gc.weightx = 0.0;
        gc.insets = new Insets(0, 0, 0, 0);
        panel.add(createFormLabel("用户名"), gc);

        gc.gridx = 1; gc.weightx = 1.0;
        panel.add(regIdField, gc);

        // 密码
        gc.gridx = 0; gc.gridy = 1;
        gc.weightx = 0.0;
        gc.insets = new Insets(ROW_GAP, 0, 0, 0);
        panel.add(createFormLabel("密  码"), gc);

        gc.gridx = 1; gc.weightx = 1.0;
        gc.insets = new Insets(ROW_GAP, 0, 0, 0);
        panel.add(regPwField, gc);

        // 昵称
        gc.gridx = 0; gc.gridy = 2;
        gc.weightx = 0.0;
        gc.insets = new Insets(ROW_GAP, 0, 0, 0);
        panel.add(createFormLabel("昵  称"), gc);

        gc.gridx = 1; gc.weightx = 1.0;
        gc.insets = new Insets(ROW_GAP, 0, 0, 0);
        panel.add(regNickField, gc);

        // 注册按钮
        gc.gridx = 0; gc.gridy = 3;
        gc.gridwidth = 2;
        gc.weightx = 1.0;
        gc.insets = new Insets(SECTION_GAP, 0, 0, 0);
        JButton regBtn = ComponentFactory.successButton("注  册");
        regBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT));
        panel.add(regBtn, gc);
        regBtn.addActionListener(e -> doRegister());

        // 切换登录
        gc.gridy = 4;
        gc.insets = new Insets(12, 0, 0, 0);
        JButton toLoginBtn = ComponentFactory.ghostButton("已有账号？去登录");
        toLoginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panel.add(toLoginBtn, gc);
        toLoginBtn.addActionListener(e -> {
            showMessage(" ", UIConstants.CONTENT_BG);
            formCard.show(formPanel, "login");
        });

        return panel;
    }

    // ========================
    //  辅助方法
    // ========================

    /** 创建表单行标签（右对齐，固定宽度） */
    private JLabel createFormLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setFont(UIConstants.FONT_BODY);
        lbl.setForeground(UIConstants.TEXT_SECONDARY);
        lbl.setPreferredSize(new Dimension(LABEL_WIDTH, FIELD_HEIGHT));
        lbl.setMinimumSize(new Dimension(LABEL_WIDTH, FIELD_HEIGHT));
        return lbl;
    }

    /** 统一输入框尺寸 */
    private void setFieldSize(JComponent field) {
        field.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        field.setMinimumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
    }

    private void doLogin() {
        String id = loginIdField.getText().trim();
        String pw = new String(loginPwField.getPassword()).trim();
        if (id.isEmpty() || pw.isEmpty()) {
            showMessage("请填写用户名和密码", UIConstants.WARNING);
            return;
        }
        boolean ok = UserManager.getInstance().login(id, pw);
        if (ok) {
            showMessage(" ", UIConstants.CONTENT_BG);
            loginIdField.setText("");
            loginPwField.setText("");
            mainFrame.onLoginSuccess();
        } else {
            showMessage("用户名或密码错误", UIConstants.DANGER);
        }
    }

    private void doRegister() {
        String id   = regIdField.getText().trim();
        String pw   = new String(regPwField.getPassword()).trim();
        String nick = regNickField.getText().trim();
        if (id.isEmpty() || pw.isEmpty()) {
            showMessage("用户名和密码不能为空", UIConstants.WARNING);
            return;
        }
        boolean ok = UserManager.getInstance().register(id, pw, nick);
        if (ok) {
            showMessage("注册成功，请登录！", UIConstants.SUCCESS);
            regIdField.setText("");
            regPwField.setText("");
            regNickField.setText("");
            formCard.show(formPanel, "login");
        } else {
            showMessage("用户名已存在", UIConstants.DANGER);
        }
    }

    private void showMessage(String msg, Color color) {
        messageLabel.setText(msg);
        messageLabel.setForeground(color);
    }
}