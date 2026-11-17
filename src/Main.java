import java.util.Random;
import java.util.Scanner;

/**
 * Игра "Путь к замку".
 *
 * Темы (из репозитория с уроками):
 *  - Встреча 3: условия if/else, equals, логические операции, Math.abs
 *  - Встреча 4: массивы и циклы (двумерный массив — игровое поле)
 *  - Встреча 5: методы (outputBoard, taskMonster, возвращаемые значения, параметры)
 *  - Встреча 6: ООП — классы Person, инкапсуляция, геттеры/сеттеры
 *  - Встреча 7: наследование, переопределение, перегрузка, полиморфизм (Monster/BigMonster)
 *
 * НОВОЕ в этой версии: логика аптечек (класс MedKit) — восстанавливают жизни.
 */
public class Main {

    // Символ пустой клетки.
    static final String EMPTY = ".";

    // Замок — цель игрока.
    static String castle = "Z"; // Z - замок (zamok)

    // Счётчик ходов.
    static int step = 0;

    public static void main(String[] args) {
        // Принудительно выводим в UTF-8, чтобы русский не ломался в консоли.
        System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));

        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        // ---------- Обработка начала игры ----------
        System.out.println("=== Добро пожаловать в игру \"Путь к замку\"! ===");
        System.out.println("Цель: дойти до замка (" + castle + "), побеждая монстров (m, M) и собирая аптечки (+)");

        System.out.print("Введите размер игрового поля (например 5): ");
        int sizeBoard = scanner.nextInt();
        if (sizeBoard < 2) {
            sizeBoard = 2; // поле меньше 2 бессмысленно
        }

        // ---------- Выбор уровня сложности ----------
        System.out.print("Выберите уровень сложности (1 - легко ... 5 - сложно): ");
        int difficultGame = scanner.nextInt();
        if (difficultGame < 1) difficultGame = 1;
        if (difficultGame > 5) difficultGame = 5;

        // ---------- Создание игрового поля ----------
        String[][] board = new String[sizeBoard][sizeBoard];
        for (int i = 0; i < sizeBoard; i++) {
            for (int j = 0; j < sizeBoard; j++) {
                board[i][j] = EMPTY;
            }
        }

        // ---------- Генерация замка на первой (верхней) строке ----------
        int castleX = 1 + random.nextInt(sizeBoard);
        int castleY = 1;
        board[castleY - 1][castleX - 1] = castle;

        // ---------- Генерация главного героя на последней (нижней) строке ----------
        Person person = new Person(sizeBoard);
        board[person.getY() - 1][person.getX() - 1] = person.getImage();

        // ---------- Генерация монстров ----------
        // Монстров создаём примерно на треть поля, чтобы игра оставалась проходимой.
        int countMonster = Math.max(1, (sizeBoard * sizeBoard) / 3);
        Monster[] monsters = new Monster[countMonster];
        int count = 0;
        int attempts = 0;
        while (count < countMonster && attempts < 1000) {
            attempts++;

            // Полиморфизм: случайно создаём обычного или большого монстра,
            // но храним их в одном массиве типа Monster.
            Monster monster;
            if (random.nextBoolean()) {
                monster = new Monster(sizeBoard);       // маленький монстр
            } else {
                monster = new BigMonster(sizeBoard);     // большой монстр
            }

            int row = monster.getY() - 1;
            int column = monster.getX() - 1;

            // Ставим монстра только на пустую клетку (не на героя и не на замок).
            if (board[row][column].equals(EMPTY)) {
                board[row][column] = monster.getImage();
                monsters[count] = monster;
                count++;
            }
        }

        // ---------- Генерация аптечек (НОВОЕ) ----------
        int countMedKit = Math.max(1, sizeBoard / 2);
        MedKit[] medKits = new MedKit[countMedKit];
        int countKit = 0;
        attempts = 0;
        while (countKit < countMedKit && attempts < 1000) {
            attempts++;
            MedKit kit = new MedKit(sizeBoard);
            int row = kit.getY() - 1;
            int column = kit.getX() - 1;
            if (board[row][column].equals(EMPTY)) {
                board[row][column] = kit.getImage();
                medKits[countKit] = kit;
                countKit++;
            }
        }

        // ---------- Основной игровой цикл ----------
        outputBoard(board, person.getLive());

        // Игра продолжается, пока у героя есть жизни и он не дошёл до замка.
        while (person.getLive() > 0) {
            System.out.println("Введите координаты хода через пробел (x y):");
            if (!scanner.hasNextInt()) {
                break; // ввод закончился — выходим без ошибки
            }
            int x = scanner.nextInt();
            if (!scanner.hasNextInt()) {
                break;
            }
            int y = scanner.nextInt();

            // Проверка выхода за пределы поля.
            if (x < 1 || x > sizeBoard || y < 1 || y > sizeBoard) {
                System.out.println("Выход за пределы поля! Попробуй ещё раз.");
                continue;
            }

            // Проверка корректности перемещения (на 1 клетку, без диагоналей).
            if (person.isMoveCorrect(x, y)) {
                String next = board[y - 1][x - 1];

                if (next.equals(EMPTY)) {
                    // Клетка пустая — просто переходим.
                    board[person.getY() - 1][person.getX() - 1] = EMPTY;
                    person.move(x, y);
                    board[person.getY() - 1][person.getX() - 1] = person.getImage();
                    step++;
                    System.out.println("Ход корректный; Новые координаты: " + person.getX()
                            + ", " + person.getY() + "\nХод номер: " + step);

                } else if (next.equals(castle)) {
                    // Дошли до замка — победа.
                    System.out.println("Вы прошли игру! ПОБЕДА за " + (step + 1) + " ходов!");
                    break;

                } else {
                    // Клетка занята — это аптечка или монстр.
                    boolean handled = false;

                    // Сначала проверяем аптечку (НОВАЯ логика).
                    for (MedKit kit : medKits) {
                        if (kit != null && kit.conflictPerson(x, y)) {
                            kit.use();
                            person.upLive();
                            System.out.println("Вы подобрали аптечку! +1 жизнь (теперь жизней: "
                                    + person.getLive() + ")");
                            board[person.getY() - 1][person.getX() - 1] = EMPTY;
                            person.move(x, y);
                            board[person.getY() - 1][person.getX() - 1] = person.getImage();
                            step++;
                            handled = true;
                            break;
                        }
                    }

                    // Если не аптечка — значит монстр. Решаем его задание.
                    if (!handled) {
                        for (Monster monster : monsters) {
                            if (monster != null && monster.conflictPerson(x, y)) {
                                // Полиморфизм: вызовется задание того монстра,
                                // который реально стоит на клетке (обычного или большого).
                                if (monster.taskMonster(difficultGame)) {
                                    // Победили монстра — проходим на его клетку.
                                    board[person.getY() - 1][person.getX() - 1] = EMPTY;
                                    person.move(x, y);
                                    board[person.getY() - 1][person.getX() - 1] = person.getImage();
                                    step++;
                                } else {
                                    // Проиграли бой — теряем жизнь.
                                    person.downLive();
                                    System.out.println("Осталось жизней: " + person.getLive());
                                }
                                handled = true;
                                break;
                            }
                        }
                    }
                }
            } else {
                System.out.println("Некорректный ход! Двигаться можно только на соседнюю клетку.");
            }

            outputBoard(board, person.getLive());
        }

        // ---------- Обработка завершения игры при исчерпании жизней ----------
        if (person.getLive() <= 0) {
            System.out.println("Game over! У вас закончились жизни.");
        }
    }

    /**
     * Вывод игрового поля.
     * Тема: метод, вынесенный из основной программы (Встреча 5), циклы по массиву.
     */
    static void outputBoard(String[][] board, int live) {
        String leftBlock = "| ";
        String rightBlock = "|";

        // Верхняя/нижняя граница строится под ширину поля.
        StringBuilder wall = new StringBuilder("+");
        for (int i = 0; i < board.length; i++) {
            wall.append("----+");
        }

        for (String[] row : board) {
            System.out.println(wall);
            for (String col : row) {
                System.out.print(leftBlock + col + " ");
            }
            System.out.println(rightBlock);
        }
        System.out.println(wall);

        System.out.println("Количество жизней:\t" + live + "\n");
    }
}
