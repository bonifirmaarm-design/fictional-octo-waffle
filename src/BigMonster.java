import java.util.Random;
import java.util.Scanner;

/**
 * Большой монстр — наследник Monster.
 * Тема: наследование (extends), super, переопределение (@Override) и перегрузка (Встреча 7).
 * Его задание сложнее и зависит от выбранного уровня сложности игры.
 */
public class BigMonster extends Monster {

    /**
     * Конструктор: вызываем конструктор родителя через super,
     * а затем задаём большому монстру собственный внешний вид.
     */
    public BigMonster(int sizeBoard) {
        super(sizeBoard);
        setImage("\uD83D\uDC79"); // большой монстр
    }

    /**
     * Переопределённое задание большого монстра.
     * Чем выше уровень сложности, тем больше числа в примере.
     */
    @Override
    public boolean taskMonster(int difficultGame) {
        if (difficultGame == 1) {
            // На самом простом уровне используем перегруженную версию без параметров.
            return taskMonster();
        } else {
            System.out.println("Решите задачу:");
            Random random = new Random();

            int x = random.nextInt(10 * (difficultGame - 1), 10 * difficultGame);
            int y = random.nextInt(10 * (difficultGame - 1), 10 * difficultGame);
            int z = random.nextInt(100 * (difficultGame - 1), 100 * difficultGame);
            int trueAnswer = x * y - z;

            System.out.println("Реши пример: " + x + " * " + y + " - " + z + " = ?");
            Scanner scanner = new Scanner(System.in);
            int answer = scanner.nextInt();

            if (trueAnswer == answer) {
                System.out.println("Верно! Ты победил монстра");
                return true;
            } else {
                System.out.println("Ты проиграл эту битву!");
                return false;
            }
        }
    }

    /**
     * Перегруженная версия без параметров — для самого лёгкого уровня.
     * Обращается к реализации родительского класса через super.
     */
    public boolean taskMonster() {
        return super.taskMonster(1);
    }
}
