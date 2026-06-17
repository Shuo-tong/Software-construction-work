package arithmetic.gui;

import java.awt.*;

/**
 * UI 全局常量：统一配色、字体与尺寸。
 * 设计语言：现代扁平 + 活力蓝主色调 + 深色侧边栏 + 柔和阴影 + 充足留白。
 */
public final class UIConstants {

    private UIConstants() {}

    // ========================
    //  配色方案（高饱和度）
    // ========================

    /** 主色：高饱和活力蓝 */
    public static final Color PRIMARY        = new Color(35, 92, 235);
    /** 主色悬浮态 */
    public static final Color PRIMARY_HOVER  = new Color(25, 78, 210);
    /** 主色按下态 */
    public static final Color PRIMARY_PRESS  = new Color(18, 60, 180);

    /** 成功色：高饱和翠绿 */
    public static final Color SUCCESS        = new Color(16, 185, 70);
    /** 警告色：高饱和琥珀橙 */
    public static final Color WARNING        = new Color(245, 148, 0);
    /** 危险色：高饱和正红 */
    public static final Color DANGER         = new Color(225, 45, 45);

    /** 侧边栏背景：深海军蓝 */
    public static final Color SIDEBAR_BG     = new Color(20, 28, 45);
    /** 侧边栏选中项 */
    public static final Color SIDEBAR_ACTIVE = new Color(40, 54, 82);
    /** 侧边栏悬浮项 */
    public static final Color SIDEBAR_HOVER  = new Color(30, 42, 66);
    /** 侧边栏文字 */
    public static final Color SIDEBAR_TEXT   = new Color(200, 210, 222);

    /** 内容区背景：暖白 */
    public static final Color CONTENT_BG     = new Color(242, 245, 250);
    /** 卡片背景：纯白 */
    public static final Color CARD_BG        = Color.WHITE;
    /** 卡片边框 */
    public static final Color CARD_BORDER    = new Color(210, 218, 228);

    /** 主文字：深黑 */
    public static final Color TEXT_PRIMARY   = new Color(20, 28, 45);
    /** 次文字：中深灰 */
    public static final Color TEXT_SECONDARY = new Color(80, 95, 120);
    /** 浅文字/占位符 */
    public static final Color TEXT_LIGHT     = new Color(140, 155, 175);

    /** 输入框边框 */
    public static final Color INPUT_BORDER   = new Color(190, 200, 212);
    /** 输入框聚焦边框 */
    public static final Color INPUT_FOCUS    = PRIMARY;

    /** 分割线 */
    public static final Color DIVIDER        = new Color(218, 225, 235);

    // ========================
    //  字体
    // ========================

    /** 大标题 */
    public static final Font FONT_TITLE      = new Font("Microsoft YaHei UI", Font.BOLD, 24);
    /** 中标题 */
    public static final Font FONT_SUBTITLE   = new Font("Microsoft YaHei UI", Font.BOLD, 18);
    /** 正文 */
    public static final Font FONT_BODY       = new Font("Microsoft YaHei UI", Font.PLAIN, 14);
    /** 正文加粗 */
    public static final Font FONT_BODY_BOLD  = new Font("Microsoft YaHei UI", Font.BOLD, 14);
    /** 小字 */
    public static final Font FONT_SMALL      = new Font("Microsoft YaHei UI", Font.PLAIN, 12);
    /** 算式大字 */
    public static final Font FONT_PROBLEM    = new Font("Consolas", Font.BOLD, 36);
    /** 按钮字体 */
    public static final Font FONT_BUTTON     = new Font("Microsoft YaHei UI", Font.BOLD, 14);

    // ========================
    //  尺寸
    // ========================

    /** 窗口默认尺寸 */
    public static final int WINDOW_WIDTH  = 1100;
    public static final int WINDOW_HEIGHT = 680;

    /** 侧边栏宽度 */
    public static final int SIDEBAR_WIDTH = 210;

    /** 卡片圆角 */
    public static final int CARD_ARC = 12;
    /** 按钮圆角 */
    public static final int BUTTON_ARC = 8;

    /** 通用内边距 */
    public static final int PAD_XL     = 32;
    public static final int PAD_LARGE  = 24;
    public static final int PAD_MEDIUM = 16;
    public static final int PAD_SMALL  = 8;
}
