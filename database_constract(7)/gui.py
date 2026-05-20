# -*- coding: utf-8 -*-
"""
基因表达量相关性分析 —— Tkinter GUI
==================================================
直接运行: python gui.py

界面布局:
  顶部工具栏  : 打开文件 / 高相关性阈值 / 刷新
  左侧        : 属性多选列表 + 操作按钮
  右上        : 高相关性属性对表格（双击 -> 立即画图）
  中下        : 嵌入的 matplotlib 折线图
  底部状态栏  : 提示信息
"""

from __future__ import annotations

import os
import tkinter as tk
from tkinter import ttk, filedialog, messagebox
from typing import Optional

# noinspection PyUnresolvedReferences
import matplotlib
# noinspection PyUnresolvedReferences
matplotlib.use("TkAgg")  # 必须放在导入 pyplot 之前
# noinspection PyUnresolvedReferences
from matplotlib import pyplot as plt
# noinspection PyUnresolvedReferences
from matplotlib.figure import Figure
# noinspection PyUnresolvedReferences
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg

# 复用核心模块
from main import (
    load_data,
    compute_corr,
    top_correlated_pairs,
    plot_lines,
    DEFAULT_FILE,
)


class App(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("基因表达量相关性分析")
        self.geometry("1200x760")
        self.minsize(900, 600)

        # 数据状态
        self.df = None
        self.corr = None
        self.current_path: Optional[str] = None

        self._build_ui()

        # 自动加载默认数据
        if os.path.exists(DEFAULT_FILE):
            self._load_file(DEFAULT_FILE)

    # ----------------------------- UI 构建 -----------------------------
    def _build_ui(self):
        # ---------- 顶部工具栏 ----------
        top = ttk.Frame(self, padding=(8, 6))
        top.pack(side=tk.TOP, fill=tk.X)

        ttk.Button(top, text="打开数据文件…", command=self.on_open).pack(side=tk.LEFT)

        ttk.Label(top, text="  高相关性阈值 |r| ≥").pack(side=tk.LEFT, padx=(12, 4))
        self.threshold_var = tk.DoubleVar(value=0.8)
        spin = ttk.Spinbox(
            top, from_=0.0, to=1.0, increment=0.05, width=6,
            textvariable=self.threshold_var, command=self.on_refresh_top,
        )
        spin.pack(side=tk.LEFT)
        # 回车 / 失焦时也刷新
        spin.bind("<Return>", lambda _e: self.on_refresh_top())
        spin.bind("<FocusOut>", lambda _e: self.on_refresh_top())

        self.path_var = tk.StringVar(value="未加载文件")
        ttk.Label(top, textvariable=self.path_var, foreground="#666").pack(side=tk.RIGHT)

        # ---------- 主体：左右分栏 ----------
        main_pane = ttk.Panedwindow(self, orient=tk.HORIZONTAL)
        main_pane.pack(fill=tk.BOTH, expand=True, padx=8, pady=4)

        # ---- 左：属性多选 ----
        left = ttk.LabelFrame(main_pane, text="属性（基因）— 按住 Ctrl/Shift 多选")
        list_box = ttk.Frame(left)
        list_box.pack(side=tk.TOP, fill=tk.BOTH, expand=True, padx=4, pady=4)

        self.listbox = tk.Listbox(list_box, selectmode=tk.EXTENDED, exportselection=False)
        self.listbox.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        sb = ttk.Scrollbar(list_box, orient=tk.VERTICAL, command=self.listbox.yview)
        sb.pack(side=tk.LEFT, fill=tk.Y)
        self.listbox.config(yscrollcommand=sb.set)

        # noinspection SpellCheckingInspection
        btns = ttk.Frame(left)
        btns.pack(side=tk.BOTTOM, fill=tk.X, padx=4, pady=(0, 6))
        ttk.Button(btns, text="全选",
                   command=lambda: self.listbox.select_set(0, tk.END)
                   ).pack(side=tk.LEFT, padx=(0, 4))
        ttk.Button(btns, text="绘制折线图", command=self.on_plot_selected)\
            .pack(side=tk.LEFT, fill=tk.X, expand=True)

        main_pane.add(left, weight=1)

        # ---- 右：上下分栏（高相关性表 + 折线图） ----
        right_pane = ttk.Panedwindow(main_pane, orient=tk.VERTICAL)

        # 右上：高相关性表
        top_frame = ttk.LabelFrame(right_pane, text="高相关性属性对（双击 → 立即绘图）")
        cols = ("a", "b", "r")
        self.tree = ttk.Treeview(top_frame, columns=cols, show="headings", height=8)
        self.tree.heading("a", text="属性 A")
        self.tree.heading("b", text="属性 B")
        self.tree.heading("r", text="相关系数 r")
        self.tree.column("a", width=120, anchor=tk.CENTER)
        self.tree.column("b", width=120, anchor=tk.CENTER)
        self.tree.column("r", width=120, anchor=tk.CENTER)
        self.tree.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=4, pady=4)
        sb2 = ttk.Scrollbar(top_frame, orient=tk.VERTICAL, command=self.tree.yview)
        sb2.pack(side=tk.LEFT, fill=tk.Y)
        self.tree.config(yscrollcommand=sb2.set)
        self.tree.bind("<Double-1>", self.on_tree_double)
        right_pane.add(top_frame, weight=1)

        # 右下：嵌入折线图
        plot_frame = ttk.LabelFrame(right_pane, text="折线图")
        self.figure = Figure(figsize=(7, 4.2), dpi=100)
        self.ax = self.figure.add_subplot(111)
        self.canvas = FigureCanvasTkAgg(self.figure, master=plot_frame)
        self.canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)
        right_pane.add(plot_frame, weight=2)

        main_pane.add(right_pane, weight=3)

        # ---------- 底部状态栏 ----------
        self.status_var = tk.StringVar(value="就绪")
        ttk.Label(self, textvariable=self.status_var, anchor=tk.W,
                  relief=tk.SUNKEN, padding=(8, 2)).pack(side=tk.BOTTOM, fill=tk.X)

        self._draw_placeholder()

    # ----------------------------- 数据加载 -----------------------------
    def on_open(self):
        path = filedialog.askopenfilename(
            title="选择数据文件",
            filetypes=[("数据文件", "*.csv *.tsv *.txt"), ("所有文件", "*.*")],
            initialdir=os.path.dirname(DEFAULT_FILE),
        )
        if path:
            self._load_file(path)

    def _load_file(self, path: str):
        try:
            self.df = load_data(path)
            self.corr = compute_corr(self.df, method="pearson")
        except Exception as e:
            messagebox.showerror("读取失败", f"{type(e).__name__}: {e}")
            return

        self.current_path = path
        self.path_var.set(path)

        # 填充属性列表
        self.listbox.delete(0, tk.END)
        for name in self.df.index:
            self.listbox.insert(tk.END, name)

        n_missing = int(self.df.isna().sum().sum())
        self.status_var.set(
            f"已加载 {self.df.shape[0]} 个属性 × {self.df.shape[1]} 个时间点 "
            f"（缺失值 {n_missing} 个）"
        )
        self.on_refresh_top()
        self._draw_placeholder()

    # ----------------------------- 高相关性表 -----------------------------
    def on_refresh_top(self):
        if self.corr is None:
            return
        for i in self.tree.get_children():
            self.tree.delete(i)
        try:
            thr = float(self.threshold_var.get())
        except (tk.TclError, ValueError):
            thr = 0.8
        pairs = top_correlated_pairs(self.corr, threshold=thr)
        for a, b, r in pairs:
            self.tree.insert("", tk.END, values=(a, b, f"{r:+.3f}"))
        self.status_var.set(f"高相关性属性对：{len(pairs)} 组（阈值 {thr:.2f}）")

    def on_tree_double(self, _event):
        sel = self.tree.selection()
        if not sel:
            return
        a, b, _ = self.tree.item(sel[0], "values")
        # 同步到左侧选中
        self._select_in_listbox([a, b])
        self._plot([a, b])

    # ----------------------------- 绘图 -----------------------------
    def _selected_genes(self):
        return [self.listbox.get(i) for i in self.listbox.curselection()]

    def on_plot_selected(self):
        genes = self._selected_genes()
        if not genes:
            messagebox.showinfo("提示", "请先在左侧选择至少 1 个属性")
            return
        self._plot(genes)

    def _plot(self, genes):
        if self.df is None:
            return
        self.ax.clear()
        plot_lines(self.df, genes, corr=self.corr, show=False, ax=self.ax)
        self.canvas.draw_idle()
        self.status_var.set(f"已绘制 {len(genes)} 个属性：{', '.join(genes)}")

    def _draw_placeholder(self):
        self.ax.clear()
        self.ax.text(
            0.5, 0.5,
            "← 在左侧选择属性后点击「绘制折线图」\n或双击右上方的高相关性属性对",
            ha="center", va="center", fontsize=11, color="#888",
            transform=self.ax.transAxes,
        )
        self.ax.set_xticks([])
        self.ax.set_yticks([])
        self.canvas.draw_idle()

    # ----------------------------- 工具 -----------------------------
    def _select_in_listbox(self, genes):
        self.listbox.select_clear(0, tk.END)
        all_items = list(self.listbox.get(0, tk.END))
        for g in genes:
            if g in all_items:
                idx = all_items.index(g)
                self.listbox.select_set(idx)
                self.listbox.see(idx)


def main():
    app = App()
    app.mainloop()


if __name__ == "__main__":
    main()
