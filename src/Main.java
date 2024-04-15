import java.io.*;
import java.util.concurrent.SynchronousQueue;

public class Main {
    // Очереди для передачи чисел от источника к потребителям
    private static final SynchronousQueue<Integer> queueToConsumer1 = new SynchronousQueue<>();
    private static final SynchronousQueue<Integer> queueToConsumer2 = new SynchronousQueue<>();

    public static void main(String[] args) throws InterruptedException {
        Thread source = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader("source.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    int number = Integer.parseInt(line);
                    // Передаем число обоим потребителям
                    queueToConsumer1.put(number);
                    queueToConsumer2.put(number);
                }
                // Сигнализируем о конце файла специальным значением (например, -1)
                queueToConsumer1.put(-1);
                queueToConsumer2.put(-1);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread consumer1 = new Thread(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output1.txt"))) {
                while (true) {
                    int number = queueToConsumer1.take();
                    if (number == -1) break; // Конец файла
                    writer.write(Integer.toString(number * number) + "\n");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread consumer2 = new Thread(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output2.txt"))) {
                while (true) {
                    int number = queueToConsumer2.take();
                    if (number == -1) break; // Конец файла
                    writer.write(Integer.toString(number * number * number) + "\n");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Запускаем потоки
        source.start();
        consumer1.start();
        consumer2.start();

        // Ожидаем завершения работы потоков
        source.join();
        consumer1.join();
        consumer2.join();
    }
}