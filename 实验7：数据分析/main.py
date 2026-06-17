
"""
数据读取与相关性计算模块
功能：
1. load_data(path): 读取原始数据文件（行=基因名，列=时间戳，值=表达量）
2. compute_corr(df, method): 计算属性两两之间的相关系数矩阵
3. top_correlated_pairs(corr, threshold, top_n): 找出高相关性的属性对
4. corr_of(corr, genes): 取若干指定属性之间的相关性子矩阵

"""

from __future__ import annotations

import os
from typing import Iterable, List, Tuple

import numpy as np
import pandas as pd
# noinspection PyUnresolvedReferences
from matplotlib import pyplot as plt

# matplotlib 中字设置
plt.rcParams["font.sans-serif"] = ["SimHei", "Microsoft YaHei", "Arial Unicode MS"]
plt.rcParams["axes.unicode_minus"] = False


#  读取数据

def load_data(path: str) -> pd.DataFrame:
    """
    读取基因表达量文件。

    原始文件格式：
      - 每行一个属性（基因），第一列是基因名
      - 后续若干列是各时间戳上的表达量
      - 分隔符：Tab（即使扩展名是 .csv）
      - 缺失值：空字符串、空格、"NA"

    返回的数据结构：
      DataFrame
        index = 基因名 (gene)
        columns = t0, t1, ..., tN-1
        dtype  = float64，缺失值为 NaN
    """
    if not os.path.exists(path):
        raise FileNotFoundError(f"数据文件不存在: {path}")

    df = pd.read_csv(
        path,
        sep="\t",
        header=None,
        index_col=0,
        skip_blank_lines=True,
        na_values=[" ", "", "NA", "NaN", "nan"],
        engine="python",
    )

    # 去掉完全空的行/列
    df = df.dropna(how="all")
    df = df.dropna(axis=1, how="all")

    # 强制转为数值类型，无法解析的设为 NaN
    df = df.apply(pd.to_numeric, errors="coerce")

    # 重命名列与行索引
    df.columns = [f"t{i}" for i in range(df.shape[1])]
    df.index = df.index.astype(str).str.strip()
    df.index.name = "gene"

    return df



# 相关性计算

def compute_corr(df: pd.DataFrame, method: str = "pearson") -> pd.DataFrame:
    """
    计算属性（行）之间两两的相关系数。

    参数:
      df: load_data 返回的 DataFrame（行=属性，列=时间戳）
      method: 'pearson'
    返回:
      n×n 相关系数矩阵，行列均为属性名
    """
    if method not in ("pearson"):
        raise ValueError(f"不支持的相关系数方法: {method}")
    # corr() 默认计算列与列之间，所以先转置
    return df.T.corr(method=method)


def corr_of(corr: pd.DataFrame, genes: Iterable[str]) -> pd.DataFrame:
    """从总相关性矩阵中取出指定属性的子矩阵。"""
    genes = list(genes)
    missing = [g for g in genes if g not in corr.index]
    if missing:
        raise KeyError(f"以下属性不存在: {missing}")
    return corr.loc[genes, genes]



# 高相关性属性对

def top_correlated_pairs(
    corr: pd.DataFrame,
    threshold: float = 0.8,
    top_n: int | None = None,
    abs_value: bool = True,
) -> List[Tuple[str, str, float]]:
    """
    找出高相关性属性对。

    参数:
      corr: 相关系数矩阵
      threshold: 阈值（默认 0.8）
      top_n: 仅返回相关性最高的前 N 对，None 表示全部
      abs_value: True 时按 |r| 比较，False 时按 r 本身比较
    返回:
      [(属性A, 属性B, 相关系数 r), ...]，按相关性由高到低排序
    """
    pairs: List[Tuple[str, str, float]] = []
    names = corr.columns.tolist()
    n = len(names)
    for i in range(n):
        for j in range(i + 1, n):
            r = corr.iat[i, j]
            if pd.isna(r):
                continue
            cmp_v = abs(r) if abs_value else r
            if cmp_v >= threshold:
                pairs.append((names[i], names[j], float(r)))

    pairs.sort(key=lambda x: (abs(x[2]) if abs_value else x[2]), reverse=True)
    if top_n is not None:
        pairs = pairs[:top_n]
    return pairs


# 可视化：折线图

def plot_lines(
    df: pd.DataFrame,
    genes: Iterable[str],
    corr: pd.DataFrame | None = None,
    title: str | None = None,
    show: bool = True,
    ax: "plt.Axes | None" = None,
):
    """
    绘制若干属性（基因）随时间变化的折线图。

    参数:
      df    : load_data 返回的 DataFrame（行=属性，列=时间戳）
      genes : 要绘制的属性名列表
      corr  : 可选，相关系数矩阵；提供后图例会附带两两相关系数（仅当 genes 数量为 2 时显示在标题）
      title : 图标题；为 None 时自动生成
      show  : 是否立即 plt.show()；嵌入 GUI 时设为 False
      ax    : 已有的 Axes（用于嵌入 GUI），为 None 时新建 Figure
    返回:
      (fig, ax) 元组
    """
    genes = list(genes)
    if not genes:
        raise ValueError("genes 不能为空")
    missing = [g for g in genes if g not in df.index]
    if missing:
        raise KeyError(f"以下属性不存在: {missing}")

    if ax is None:
        fig, ax = plt.subplots(figsize=(10, 5))
    else:
        fig = ax.figure

    x = np.arange(df.shape[1])
    for g in genes:
        ax.plot(x, df.loc[g].values, marker="o", markersize=3, linewidth=1.4, label=g)

    ax.set_xlabel("时间戳")
    ax.set_ylabel("表达量")

    if title is None:
        if len(genes) == 2 and corr is not None \
                and genes[0] in corr.index and genes[1] in corr.index:
            r = corr.loc[genes[0], genes[1]]
            title = f"{genes[0]} vs {genes[1]}   (Pearson r = {r:+.3f})"
        else:
            title = f"属性表达量随时间变化（{len(genes)} 个属性）"
    ax.set_title(title)

    ax.axhline(0, color="#999", linewidth=0.8, linestyle="--", alpha=0.6)
    ax.grid(alpha=0.3)
    ax.legend(loc="best", fontsize=9, ncol=min(len(genes), 4))
    fig.tight_layout()

    if show:
        plt.show()
    return fig, ax


# 控制台快速演示

DEFAULT_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "实验7-数据.csv")


def _preview(path: str = DEFAULT_FILE) -> None:
    print(f"[1] 读取文件: {path}")
    df = load_data(path)
    print(f"    形状: {df.shape}  (属性数 × 时间点数)")
    print(f"    属性列表: {list(df.index)}")
    print(f"    缺失值数量: {int(df.isna().sum().sum())}")
    print()

    print("[2] 数据预览（前 5 个属性 × 前 8 个时间点）：")
    print(df.iloc[:5, :8].to_string())
    print()

    print("[3] 计算 Pearson 相关性矩阵 ...")
    corr = compute_corr(df, method="pearson")
    print(f"    矩阵形状: {corr.shape}")
    print()

    print("[4] 高相关性属性对（|r| >= 0.8）：")
    pairs = top_correlated_pairs(corr, threshold=0.8)
    if not pairs:
        print("    无")
    else:
        for a, b, r in pairs:
            print(f"    {a:<8} <-> {b:<8}  r = {r:+.3f}")
    print()

    print("[5] Top-5 相关性最高的属性对：")
    for a, b, r in top_correlated_pairs(corr, threshold=0.0, top_n=5):
        print(f"    {a:<8} <-> {b:<8}  r = {r:+.3f}")
    print()

    # [6] 可视化演示：自动画出相关性最高的一对 + 一组多属性折线图
    print("[6] 折线图演示：")
    top1 = top_correlated_pairs(corr, threshold=0.0, top_n=1)
    if top1:
        a, b, _ = top1[0]
        plot_lines(df, [a, b], corr=corr)
    plot_lines(df, ["clb1", "clb2", "clb5", "clb6"], corr=corr,
               title="所有clb表达量随时间变化")


if __name__ == "__main__":
    _preview()
