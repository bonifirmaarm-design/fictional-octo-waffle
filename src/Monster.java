import java.util.Random;
import java.util.Scanner;

/**
 * Обычный (маленький) монстр.
 * Задаёт игроку простую задачу на сложение.
 * Тема: ООП (Встреча 6-7), методы, Random, Scanner.
 */
public class Monster {
    // protected — чтобы классы-наследники (BigMonster) имели доступ к полям.
    protected int x;
    protected int y;
    protected String image = "\uD83D\uDC7E"; // монстр

    /**
     * Конструктор. Монстр появляется в случайной клетке поля.
     */
    public Monster(int sizeBoard) {
        Random random = new Random();
        x = 1 + random.nextInt(sizeBoard);
        y = 1 + random.nextInt(sizeBoard);
    }

    /**
     * Задание маленького монстра: решить пример на сложение.
     * Возвращает true, если игрок ответил верно, иначе false.
     */
    public boolean taskMonster(int difficultGame) {
        Random random = new Random();
        int a = random.nextInt(100);
        int b = random.nextInt(100);
        int trueAnswer = a + b;

        System.out.println("Реши пример: " + a + " + " + b + " = ?");
        Scanner scanner = new Scanner(System.in);
        int ans = scanner.nextInt();

        if (trueAnswer == ans) {
            System.out.println("Верно! Ты победил монстра");
            return true;
        } else {
            System.out.println("Ты проиграл эту битву!");
            return false;
        }
    }

    /**
     * Проверяет, идёт ли герой на клетку с этим монстром.
     */
    public boolean conflictPerson(int x, int y) {
        return this.x == x && this.y == y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
