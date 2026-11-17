import java.util.Random;
import java.util.Scanner;

// ================== ГЛАВНЫЙ КЛАСС (запуск игры) ==================
public class Main {

    static final String EMPTY = "  ";
    static String castle = "🏰"; // 🏰
    static int step = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        // ---------- Начало игры ----------
        System.out.println("=== Добро пожаловать в игру \"Путь к замку\"! ===");
        System.out.println("Цель: дойти до замка " + castle + ", побеждая монстров и собирая аптечки ❤");

        System.out.print("Введите размер игрового поля (например 5): ");
        int sizeBoard = scanner.nextInt();
        if (sizeBoard < 2) {
            sizeBoard = 2;
        }

        // ---------- Выбор уровня сложности ----------
        System.out.print("Выберите уровень сложности (1 - легко ... 5 - сложно): ");
        int difficultGame = scanner.nextInt();
        if (difficultGame < 1) difficultGame = 1;
        if (difficultGame > 5) difficultGame = 5;

        // ---------- Создание поля ----------
        String[][] board = new String[sizeBoard][sizeBoard];
        for (int i = 0; i < sizeBoard; i++) {
            for (int j = 0; j < sizeBoard; j++) {
                board[i][j] = EMPTY;
            }
        }

        // ---------- Замок (верхняя строка) ----------
        int castleX = 1 + random.nextInt(sizeBoard);
        int castleY = 1;
        board[castleY - 1][castleX - 1] = castle;

        // ---------- Герой (нижняя строка) ----------
        Person person = new Person(sizeBoard);
        board[person.getY() - 1][person.getX() - 1] = person.getImage();

        // ---------- Монстры ----------
        int countMonster = Math.max(1, (sizeBoard * sizeBoard) / 3);
        Monster[] monsters = new Monster[countMonster];
        int count = 0;
        int attempts = 0;
        while (count < countMonster && attempts < 1000) {
            attempts++;
            Monster monster;
            if (random.nextBoolean()) {
                monster = new Monster(sizeBoard);
            } else {
                monster = new BigMonster(sizeBoard);
            }
            int row = monster.getY() - 1;
            int column = monster.getX() - 1;
            if (board[row][column].equals(EMPTY)) {
                board[row][column] = monster.getImage();
                monsters[count] = monster;
                count++;
            }
        }

        // ---------- Аптечки (НОВОЕ) ----------
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

        // ---------- Игровой цикл ----------
        outputBoard(board, person.getLive());

        while (person.getLive() > 0) {
            System.out.println("Введите координаты хода через пробел (x y):");
            if (!scanner.hasNextInt()) break;
            int x = scanner.nextInt();
            if (!scanner.hasNextInt()) break;
            int y = scanner.nextInt();

            if (x < 1 || x > sizeBoard || y < 1 || y > sizeBoard) {
                System.out.println("Выход за пределы поля! Попробуй ещё раз.");
                continue;
            }

            if (person.isMoveCorrect(x, y)) {
                String next = board[y - 1][x - 1];

                if (next.equals(EMPTY)) {
                    board[person.getY() - 1][person.getX() - 1] = EMPTY;
                    person.move(x, y);
                    board[person.getY() - 1][person.getX() - 1] = person.getImage();
                    step++;
                    System.out.println("Ход корректный; Новые координаты: " + person.getX()
                            + ", " + person.getY() + "\nХод номер: " + step);

                } else if (next.equals(castle)) {
                    System.out.println("Вы прошли игру! 🏆 Победа за " + (step + 1) + " ходов!");
                    break;

                } else {
                    boolean handled = false;

                    // сначала аптечка
                    for (MedKit kit : medKits) {
                        if (kit != null && kit.conflictPerson(x, y)) {
                            kit.use();
                            person.upLive();
                            System.out.println("Вы подобрали аптечку! +1 жизнь ❤ (теперь жизней: "
                                    + person.getLive() + ")");
                            board[person.getY() - 1][person.getX() - 1] = EMPTY;
                            person.move(x, y);
                            board[person.getY() - 1][person.getX() - 1] = person.getImage();
                            step++;
                            handled = true;
                            break;
                        }
                    }

                    // иначе монстр
                    if (!handled) {
                        for (Monster monster : monsters) {
                            if (monster != null && monster.conflictPerson(x, y)) {
                                if (monster.taskMonster(difficultGame)) {
                                    board[person.getY() - 1][person.getX() - 1] = EMPTY;
                                    person.move(x, y);
                                    board[person.getY() - 1][person.getX() - 1] = person.getImage();
                                    step++;
                                } else {
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

        if (person.getLive() <= 0) {
            System.out.println("Game over! 💀 У вас закончились жизни.");
        }
    }

    static void outputBoard(String[][] board, int live) {
        String leftBlock = "| ";
        String rightBlock = "|";

        // Граница под размер поля.
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

// ================== ГЛАВНЫЙ ГЕРОЙ ==================
class Person {
    private int x;
    private int y;
    private String image = "🧙"; // 🧙
    private int live = 3;

    public Person(int sizeBoard) {
        Random random = new Random();
        x = 1 + random.nextInt(sizeBoard);
        y = sizeBoard;
    }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isMoveCorrect(int x, int y) {
        return this.x == x && Math.abs(this.y - y) == 1
                || this.y == y && Math.abs(this.x - x) == 1;
    }

    public void downLive() {
        if (live <= 0) {
            live = 0;
        } else {
            live--;
        }
    }

    public void upLive() {
        live++;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getLive() { return live; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}

// ================== МАЛЕНЬКИЙ МОНСТР ==================
class Monster {
    protected int x;
    protected int y;
    protected String image = "👾"; // 👾

    public Monster(int sizeBoard) {
        Random random = new Random();
        x = 1 + random.nextInt(sizeBoard);
        y = 1 + random.nextInt(sizeBoard);
    }

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

    public boolean conflictPerson(int x, int y) {
        return this.x == x && this.y == y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}

// ================== БОЛЬШОЙ МОНСТР (сложность) ==================
class BigMonster extends Monster {

    public BigMonster(int sizeBoard) {
        super(sizeBoard);
        setImage("👹"); // 👹
    }

    @Override
    public boolean taskMonster(int difficultGame) {
        if (difficultGame == 1) {
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

    public boolean taskMonster() {
        return super.taskMonster(1);
    }
}

// ================== АПТЕЧКА (новая логика) ==================
class MedKit {
    private int x;
    private int y;
    private String image = "❤"; // ❤
    private boolean active = true;

    public MedKit(int sizeBoard) {
        Random random = new Random();
        x = 1 + random.nextInt(sizeBoard);
        y = 1 + random.nextInt(sizeBoard);
    }

    public boolean conflictPerson(int x, int y) {
        return active && this.x == x && this.y == y;
    }

    public void use() { active = false; }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getImage() { return image; }
    public boolean isActive() { return active; }
}
