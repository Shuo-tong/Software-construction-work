package arithmetic.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * 现代风格组件工厂：圆角按钮、阴影卡片、样式化输入框、标签等。
 * 所有组件统一通过此工厂创建，确保全局视觉一致性。
 */
public final class ComponentFactory {

    private ComponentFactory() {}

    // ========================
    //  按钮
    // ========================

    /** 主色圆角按钮（蓝底白字） */
    public static JButton primaryButton(String text) {
        return styledButton(text, UIConstants.PRIMARY, UIConstants.PRIMARY_HOVER, Color.WHITE);
    }

    /** 成功色按钮（绿底白字） */
    public static JButton successButton(String text) {
        return styledButton(text, UIConstants.SUCCESS, UIConstants.SUCCESS.darker(), Color.WHITE);
    }

    /** 危险色按钮（红底白字） */
    public static JButton dangerButton(String text) {
        return styledButton(text, UIConstants.DANGER, UIConstants.DANGER.darker(), Color.WHITE);
    }

    /** 幽灵按钮（透明底 + 蓝色文字，悬浮变蓝底白字） */
    public static JButton ghostButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? UIConstants.PRIMARY : UIConstants.CONTENT_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(),
                        UIConstants.BUTTON_ARC, UIConstants.BUTTON_ARC));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UIConstants.FONT_BUTTON);
        btn.setForeground(UIConstants.PRIMARY);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 40));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setForeground(Color.WHITE);
                try { btn.getClass().getField("hovered").setBoolean(btn, true); } catch (Exception ignored) {}
                btn.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setForeground(UIConstants.PRIMARY);
                try { btn.getClass().getField("hovered").setBoolean(btn, false); } catch (Exception ignored) {}
                btn.repaint();
            }
        });
        return btn;
    }

    /** 通用圆角按钮 */
    private static JButton styledButton(String text, Color bg, Color hoverBg, Color fg) {
        JButton btn = new JButton(text) {
            private Color currentBg = bg;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(currentBg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(),
                        UIConstants.BUTTON_ARC, UIConstants.BUTTON_ARC));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UIConstants.FONT_BUTTON);
        btn.setForeground(fg);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 40));
        // 悬浮变色（通过匿名内部类字段反射）
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                try { btn.getClass().getDeclaredField("currentBg").set(btn, hoverBg); } catch (Exception ignored) {}
                btn.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                try { btn.getClass().getDeclaredField("currentBg").set(btn, bg); } catch (Exception ignored) {}
                btn.repaint();
            }
        });
        return btn;
    }

    // ========================
    //  卡片面板
    // ========================

    /** 圆角卡片（白底 + 柔和阴影） */
    public static JPanel card() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // 阴影
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Double(2, 2, getWidth() - 2, getHeight() - 2,
                        UIConstants.CARD_ARC, UIConstants.CARD_ARC));
                // 卡片本体
                g2.setColor(UIConstants.CARD_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 3, getHeight() - 3,
                        UIConstants.CARD_ARC, UIConstants.CARD_ARC));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(UIConstants.PAD_MEDIUM, UIConstants.PAD_MEDIUM,
                UIConstants.PAD_MEDIUM, UIConstants.PAD_MEDIUM));
        return panel;
    }

    /** 带顶部色条的卡片 */
    public static JPanel accentCard(Color accentColor) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // 阴影
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fill(new RoundRectangle2D.Double(2, 2, getWidth() - 2, getHeight() - 2,
                        UIConstants.CARD_ARC, UIConstants.CARD_ARC));
                // 白底
                g2.setColor(UIConstants.CARD_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 3, getHeight() - 3,
                        UIConstants.CARD_ARC, UIConstants.CARD_ARC));
                // 顶部色条
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth() - 3, 4, 4, 4);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, UIConstants.PAD_MEDIUM, UIConstants.PAD_MEDIUM, UIConstants.PAD_MEDIUM));
        return panel;
    }

    // ========================
    //  输入框
    // ========================

    /** 圆角文本输入框（带 placeholder） */
    public static JTextField styledTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(UIConstants.TEXT_LIGHT);
                    g2.setFont(UIConstants.FONT_BODY);
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left + 2, getHeight() / 2 + 5);
                    g2.dispose();
                }
            }
        };
        field.setFont(UIConstants.FONT_BODY);
        field.setPreferredSize(new Dimension(280, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.INPUT_BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    /** 圆角密码输入框（带 placeholder） */
    public static JPasswordField styledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(UIConstants.TEXT_LIGHT);
                    g2.setFont(UIConstants.FONT_BODY);
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left + 2, getHeight() / 2 + 5);
                    g2.dispose();
                }
            }
        };
        field.setFont(UIConstants.FONT_BODY);
        field.setPreferredSize(new Dimension(280, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.INPUT_BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    // ========================
    //  标签
    // ========================

    /** 标题标签 */
    public static JLabel titleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.FONT_TITLE);
        lbl.setForeground(UIConstants.TEXT_PRIMARY);
        return lbl;
    }

    /** 副标题标签 */
    public static JLabel subtitleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.FONT_SUBTITLE);
        lbl.setForeground(UIConstants.TEXT_PRIMARY);
        return lbl;
    }

    /** 正文标签 */
    public static JLabel bodyLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.FONT_BODY);
        lbl.setForeground(UIConstants.TEXT_SECONDARY);
        return lbl;
    }

    // ========================
    //  分割线
    // ========================

    /** 水平分割线 */
    public static JSeparator divider() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(UIConstants.DIVIDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
}
