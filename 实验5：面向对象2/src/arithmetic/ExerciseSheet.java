package arithmetic;

public class ExerciseSheet extends Exercise {

    /** 依次产生并显示：50道加法、50道减法、50道加减法混合练习题 */
    public void displayAll() {
        // (1) 产生 50 道加法的练习题，格式化显示
        System.out.println("===== 加法练习 =====");
        generateAdditionExercise(50);
        formattedDisplay();

        // (2) 产生 50 道减法的练习题，格式化显示
        System.out.println("===== 减法练习 =====");
        generateSubstractExercise(50);
        formattedDisplay();

        // (3) 产生 50 道加减法混合的练习题，格式化显示
        System.out.println("===== 混合运算练习 =====");
        generateBinaryExercise(50);
        formattedDisplay();
    }
}
