
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static final int COUNT_TEXTS = 10000; // количество текстов
    public static final int LENGT_TEXTS = 100000; // длина каждого текста
    public static final int QUEUE_CAPACITY = 100; // // размер блокирующих очередей
    public static long countA = 0;
    public static long countB = 0;
    public static long countC = 0;

    // блокирующие очереди для каждого символа
    public static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    public static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    public static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    public static void main(String[] args) throws InterruptedException {

        // поток генерации текстов
        Thread generate = new Thread(() -> {
            for (int i = 0; i < COUNT_TEXTS; i++) {
                String texts = GenerateText.generateText("abc", LENGT_TEXTS);
                try {
                    queueA.put(texts);
                    queueB.put(texts);
                    queueC.put(texts);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread analyzerA = new Thread(() -> {
            char target = 'a';
            try {
                for (int i = 0; i < COUNT_TEXTS; i++) {
                    String text = queueA.take(); // извлекаем текст из очереди
                    countA += text.chars()       // cчитаем все вхождения 'a' за один проход
                            .filter(ch -> ch == target)
                            .count();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread analyzerB = new Thread(() -> {
            char target = 'b';
            try {
                for (int i = 0; i < COUNT_TEXTS; i++) {
                    String text = queueB.take(); // извлекаем текст из очереди
                    countB += text.chars()       // cчитаем все вхождения 'b' за один проход
                            .filter(ch -> ch == target)
                            .count();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread analyzerC = new Thread(() -> {
            char target = 'c';
            try {
                for (int i = 0; i < COUNT_TEXTS; i++) {
                    String text = queueC.take();  // извлекаем текст из очереди
                    countC += text.chars()        // cчитаем все вхождения 'c' за один проход
                            .filter(ch -> ch == target)
                            .count();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        generate.start();
        analyzerA.start();
        analyzerB.start();
        analyzerC.start();

        generate.join();
        analyzerA.join();
        analyzerB.join();
        analyzerC.join();

        System.out.printf("Общее количество символов: \n a = %d \n b = %d \n c = %d", countA, countB, countC);
    }
}
