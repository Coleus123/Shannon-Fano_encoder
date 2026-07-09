package com.shannonfano.core;

import com.shannonfano.archive.ArchiveReader;
import com.shannonfano.archive.ArchiveWriter;
import com.shannonfano.exception.CorruptArchiveException;
import com.shannonfano.exception.InvalidArchiveException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Основной класс алгоритма Шеннона-Фано.
 * Координирует работу всех компонентов для кодирования и декодирования.
 */
public class ShannonFanoAlgorithm {

    private final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
    private final CodeTableBuilder codeTableBuilder = new CodeTableBuilder();
    private final ArchiveWriter archiveWriter = new ArchiveWriter();
    private final ArchiveReader archiveReader = new ArchiveReader();

    /**
     * Кодирует файл в архив
     *
     * @param inputPath Путь к исходному файлу
     * @param outputPath Путь к выходному архиву
     */
    public void encode(String inputPath, String outputPath) throws IOException {
        if (inputPath == null || inputPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Не указан файл для кодирования");
        }
        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            throw new FileNotFoundException("Файл не найден: " + inputPath);
        }
        if (!inputFile.isFile()) {
            throw new IllegalArgumentException("Путь должен указывать на файл: " + inputPath);
        }
        frequencyAnalyzer.clear();
        frequencyAnalyzer.analyzeFile(inputPath);
        codeTableBuilder.buildTables(frequencyAnalyzer.getCharacterFrequency());
        Map<Object, String> encodingTable = codeTableBuilder.getEncodingTable();
        archiveWriter.writeFile(inputPath, outputPath, encodingTable);
    }

    /**
     * Кодирует коллекцию файлов и директорий в архив
     *
     * @param inputPaths Список путей к файлам/директориям
     * @param outputPath Путь к выходному архиву
     */
    public void encode(List<String> inputPaths, String outputPath) throws IOException {
        if (inputPaths == null || inputPaths.isEmpty()) {
            throw new IllegalArgumentException("Не указаны файлы/каталоги для кодирования");
        }
        for (String path : inputPaths) {
            if (!new File(path).exists()) {
                throw new FileNotFoundException("Путь не существует: " + path);
            }
        }
        frequencyAnalyzer.clear();
        for (String path : inputPaths) {
            File file = new File(path);
            if (file.isFile()) {
                frequencyAnalyzer.analyzeFile(path);
            } else if (file.isDirectory()) {
                frequencyAnalyzer.analyzeDirectory(path);
            }
        }
        codeTableBuilder.buildTables(frequencyAnalyzer.getCharacterFrequency());
        Map<Object, String> encodingTable = codeTableBuilder.getEncodingTable();
        archiveWriter.writeArchive(inputPaths, outputPath, encodingTable);
    }

    /**
     * Декодирует архив и восстанавливает файл
     *
     * @param inputPath Путь к архиву
     * @param outputDir Директория для восстановления файла
     */
    public void decode(String inputPath, String outputDir) throws IOException,
            InvalidArchiveException,
            CorruptArchiveException {
        if (inputPath == null || inputPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Не указан архив для декодирования");
        }
        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            throw new FileNotFoundException("Архив не найден: " + inputPath);
        }
        archiveReader.readArchive(inputPath, outputDir);
    }
}