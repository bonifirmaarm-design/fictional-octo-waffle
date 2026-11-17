import java.util.Random;

/**
 * Аптечка — НОВАЯ логика, которую мы добавляем в игру.
 * Когда герой встаёт на клетку с аптечкой, он получает +1 жизнь.
 * Тема: ООП — отдельный класс с полями, конструктором и методами (Встреча 6-7).
 */
public class MedKit {
    private int x;
    private int y;
    private String image = "A"; // аптечка
    private boolean active = true;   // аптечку можно использовать только один раз

    /**
     * Конструктор: аптечка появляется в случайной клетке поля.
     */
    public MedKit(int sizeBoard) {
        Random random = new Random();
        x = 1 + random.nextInt(sizeBoard);
        y = 1 + random.nextInt(sizeBoard);
    }

    /**
     * Проверяет, встаёт ли герой на клетку с аптечкой
     * (и что аптечка ещё не использована).
     */
    public boolean conflictPerson(int x, int y) {
        return active && this.x == x && this.y == y;
    }

    /**
     * Использовать аптечку (после этого она пропадает с поля).
     */
    public void use() {
        active = false;
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

    public boolean isActive() {
        return active;
    }
}
