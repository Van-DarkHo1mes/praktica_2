import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class PasswordCracker {
    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyz";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите количество потоков для многопоточного режима:");
        int threadCount = scanner.nextInt();
        scanner.nextLine();

        while (true) {
            System.out.println("Введите хэш (MD5 или SHA-256) для подбора пароля. Введите 'exit' для завершения.");
            String hash = scanner.nextLine().trim();
            if (hash.equalsIgnoreCase("exit")) {
                break;
            }

            long startTime = System.nanoTime();
            String password = crackPassword(hash, threadCount);
            long endTime = System.nanoTime();

            if (password != null) {
                System.out.println("Найден пароль для хэша " + hash + ": " + password);
            } else {
                System.out.println("Пароль для хэша " + hash + " не найден.");
            }
            System.out.println("Время выполнения: " + (endTime - startTime) / 1_000_000 + " мс");
        }

        System.out.println("Программа завершена.");
    }

    private static String crackPassword(String hash, int threadCount) {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<String>> futures = new ArrayList<>();

        int rangeSize = CHARSET.length() / threadCount;
        for (int i = 0; i < threadCount; i++) {
            int start = i * rangeSize;
            int end = (i == threadCount - 1) ? CHARSET.length() : (i + 1) * rangeSize;
            String prefixRange = CHARSET.substring(start, end);

            Callable<String> task = new BruteForceTask(prefixRange, hash);
            futures.add(executor.submit(task));
        }

        executor.shutdown();

        for (Future<String> future : futures) {
            try {
                String result = future.get();
                if (result != null) {
                    executor.shutdownNow();
                    return result;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
