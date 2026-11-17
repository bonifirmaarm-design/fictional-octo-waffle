import java.util.Random;

/**
 * Главный герой игры.
 * Тема: ООП (Встреча 6) — поля, конструктор, методы, инкапсуляция (private + геттеры/сеттеры).
 */
public class Person {
    // Поля класса скрыты (инкапсуляция). Доступ — через геттеры и специальные методы.
    private int x;              // координата по горизонтали
    private int y;              // координата по вертикали
    private String image = "G"; // как выглядит герой на поле (G - geroy)
    private int live = 3;       // количество жизней

    /**
     * Конструктор. Герой появляется в случайной клетке нижней строки поля.
     */
    public Person(int sizeBoard) {
        Random random = new Random();
        x = 1 + random.nextInt(sizeBoard); // случайная колонка
        y = sizeBoard;                     // нижняя строка
    }

    /**
     * Перемещение: получаем новые координаты и присваиваем их полям.
     * Тема: ключевое слово this (Встреча 6).
     */
    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Проверка корректности хода: можно двигаться только на одну клетку
     * по горизонтали или по вертикали (без диагоналей).
     * Тема: логические операции &&, || и Math.abs (Встреча 3).
     */
    public boolean isMoveCorrect(int x, int y) {
        return this.x == x && Math.abs(this.y - y) == 1
                || this.y == y && Math.abs(this.x - x) == 1;
    }

    /**
     * Уменьшение жизней. Контролируем, чтобы жизни не стали отрицательными.
     * Тема: сеттер-подобный метод (Встреча 6).
     */
    public void downLive() {
        if (live <= 0) {
            live = 0;
        } else {
            live--;
        }
    }

    /**
     * Увеличение жизней — используется при подборе аптечки (НОВАЯ логика).
     */
    public void upLive() {
        live++;
    }

    // Геттеры — позволяют читать скрытые поля, не открывая к ним прямой доступ.
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLive() {
        return live;
    }

    public String getImage() {
        return image;
    }

    // Сеттер для внешнего вида героя.
    public void setImage(String image) {
        this.image = image;
    }
}
