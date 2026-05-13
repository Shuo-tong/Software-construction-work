import os
import jpype
import pytest

# 项目根目录（存放 MathPractice.class 的位置）
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# JVM 路径：优先使用 JAVA_HOME，若无效则使用已知的 JDK-19 安装路径
JVM_PATH = None
_java_home = os.environ.get("JAVA_HOME", "")
_candidate = os.path.join(_java_home, "bin", "server", "jvm.dll")
if os.path.isfile(_candidate):
    JVM_PATH = _candidate
else:
    # 回退到系统中已知的 JDK-19 路径
    _fallback = r"C:\Program Files\Java\jdk-19\bin\server\jvm.dll"
    if os.path.isfile(_fallback):
        JVM_PATH = _fallback

# ---------------------- 1. JVM fixture（整个测试会话启动一次） ----------------------
@pytest.fixture(scope="session", autouse=True)
def jvm():
    """在所有测试开始前启动 JVM，结束后关闭"""
    if JVM_PATH is None:
        pytest.skip("未找到 jvm.dll，请检查 JAVA_HOME 环境变量或 JDK 安装")
    jpype.startJVM(JVM_PATH, f"-Djava.class.path={BASE_DIR}")
    yield
    jpype.shutdownJVM()

# ---------------------- 2. MathPractice fixture（在 JVM 启动后才加载 Java 类） ----------------------
@pytest.fixture(scope="session")
def math_practice(jvm):
    """返回 MathPractice 实例（显式依赖 jvm fixture 确保 JVM 已启动）"""
    MathPractice = jpype.JClass("MathPractice")
    return MathPractice()

# ---------------------- 3. 测试用例（使用 math_practice fixture） ----------------------
def test_generate_addition_question(math_practice):
    """测试生成 100 以内加法题目"""
    question = math_practice.generateAddition(100)
    assert "+" in question
    parts = question.split('+')
    assert len(parts) == 2
    assert 0 <= int(parts[0]) <= 100
    assert 0 <= int(parts[1]) <= 100

def test_calculate_correct_answer(math_practice):
    """测试计算正确答案"""
    question = "10+20"
    correct_answer = math_practice.calculateAnswer(question)
    assert correct_answer == 30

@pytest.mark.parametrize("question, user_answer, expected", [
    ("5+5", 10, True),
    ("3+2", 6, False),
    ("0+0", 0, True),
])
def test_check_user_answer(math_practice, question, user_answer, expected):
    """测试校验用户答案"""
    result = math_practice.checkAnswer(question, user_answer)
    assert result == expected