package com.shannonfano;

import com.shannonfano.core.ShannonFanoAlgorithm;
import com.shannonfano.exception.ArchiveException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Главный класс приложения для работы с кодировщиком Шеннона-Фано.
 * Предоставляет интерфейс командной строки для кодирования и декодирования.
 */
public class Main {

    /**
     * Точка входа в приложение
     *
     * @param args Аргументы командной строки:
     *             encode input_file output_archive
     *             decode input_archive output_dir
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            printUsage();
            System.exit(1);
        }
        String command = args[0];
        ShannonFanoAlgorithm algorithm = new ShannonFanoAlgorithm();
        try {
            if (command.equalsIgnoreCase("encode")) {
                handleEncode(args, algorithm);
            } else if (command.equalsIgnoreCase("decode")) {
                handleDecode(args, algorithm);
            } else {
                System.err.println("Ошибка: неизвестная команда: " + command);
                printUsage();
                System.exit(1);
            }
        } catch (IllegalArgumentException | IOException | ArchiveException e) {
            System.err.printf("Ошибка: %s", e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Обрабатывает команду кодирования
     *
     * @param args Аргументы командной строки
     * @param algorithm Экземпляр алгоритма
     */
    private static void handleEncode(String[] args, ShannonFanoAlgorithm algorithm) throws IOException {
        if (args.length < 3) {
            System.err.println("Ошибка: недостаточно аргументов для кодирования");
            printUsage();
            System.exit(1);
        }
        List<String> inputPaths = Arrays.asList(args[1].split(","));
        String outputPath = args[2];
        String password = args.length > 3 ? args[3] : null;
        System.out.printf("Кодирование: %s -> %s", inputPaths, outputPath);
        algorithm.encode(inputPaths, outputPath, password);
        System.out.println("Кодирование завершено успешно");
    }

    /**
     * Обрабатывает команду декодирования
     *
     * @param args Аргументы командной строки
     * @param algorithm Экземпляр алгоритма
     */
    private static void handleDecode(String[] args, ShannonFanoAlgorithm algorithm) throws IOException, ArchiveException {
        if (args.length < 3) {
            System.err.println("Ошибка: недостаточно аргументов для декодирования");
            printUsage();
            System.exit(1);
        }
        String inputPath = args[1];
        String outputDir = args[2];
        String password = args.length > 3 ? args[3] : null;
        System.out.printf("Декодирование архива: %s -> %s", inputPath, outputDir);
        algorithm.decode(inputPath, outputDir, password);
        System.out.println("Декодирование завершено успешно");
    }

    /**
     * Выводит справку по использованию программы
     */
    private static void printUsage() {
        System.out.println("Shannon-Fano Кодировщик/Декодировщик");
        System.out.println();
        System.out.println("Использование:");
        System.out.println("  encode <input1,input2,...> <output_archive> [password]");
        System.out.println("  decode <input_archive> <output_dir> [password]");
        System.out.println();
        System.out.println("Примеры:");
        System.out.println("  encode file.txt archive.sf");
        System.out.println("  encode file.txt archive.sf mypassword");
        System.out.println("  encode dir1,dir2 archive.sf");
        System.out.println("  decode archive.sf output_dir");
        System.out.println("  decode archive.sf output_dir mypassword");
    }
}