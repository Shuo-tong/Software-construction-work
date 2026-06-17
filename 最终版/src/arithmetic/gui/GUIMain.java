package arithmetic.gui;

import javax.swing.*;

/**
 * GUI 启动入口。
 * 优先加载 FlatLaf 现代主题，若未引入则回退到系统原生外观。
 */
public class GUIMain {

    public static void main(String[] args) {
        // 尝试加载 FlatLaf 现代主题
        try {
            Class<?> flatLaf = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            flatLaf.getMethod("setup").invoke(null);
        } catch (Exception e) {
            // FlatLaf 未引入，使用系统外观
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }

        // 全局 UI 默认值优化
        UIManager.put("Button.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 10);

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}