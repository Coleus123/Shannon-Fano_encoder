package com.shannonfano.core;

import java.util.*;

/**
 * Строит таблицы кодирования и декодирования по алгоритму Шеннона-Фано
 */
public class CodeTableBuilder {

    private Map<Object, String> encodingTable = new HashMap<>();
    private Map<String, Object> decodingTable = new HashMap<>();

    /**
     * Строит таблицы кодирования на основе частот символов
     *
     * @param frequency Карта частот символов
     */
    public void buildTables(Map<Object, Integer> frequency) {
        List<Map.Entry<Object, Integer>> sortedSymbols = new ArrayList<>(frequency.entrySet());
        sortedSymbols.sort((a, b)
                -> Integer.compare(b.getValue(), a.getValue()));
        encodingTable = new HashMap<>();
        buildCodeRecursive(sortedSymbols, "");
        decodingTable = new HashMap<>();
        for (Map.Entry<Object, String> entry : encodingTable.entrySet()) {
            decodingTable.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * Рекурсивно строит коды для символов методом Шеннона-Фано
     *
     * @param symbols Список символов с их частотами, отсортированный по убыванию
     * @param prefix Префикс кода для текущего уровня рекурсии
     */
    private void buildCodeRecursive(List<Map.Entry<Object, Integer>> symbols, String prefix) {
        if (symbols.isEmpty()) {
            return;
        }
        if (symbols.size() == 1) {
            Object character = symbols.get(0).getKey();
            encodingTable.put(character, prefix.isEmpty() ? "0" : prefix);
            return;
        }
        int total = symbols.stream().mapToInt(Map.Entry::getValue).sum();
        int half = total / 2;
        int cumulative = 0;
        int splitIndex = 0;
        int minDiff = Integer.MAX_VALUE;
        for (int i = 0; i < symbols.size(); i++) {
            cumulative += symbols.get(i).getValue();
            int currentDiff = Math.abs(cumulative - half);
            if (currentDiff < minDiff) {
                minDiff = currentDiff;
                splitIndex = i;
            } else {
                break;
            }
        }
        buildCodeRecursive(symbols.subList(0, splitIndex + 1), prefix + "0");
        buildCodeRecursive(symbols.subList(splitIndex + 1, symbols.size()), prefix + "1");
    }

    /**
     * Возвращает таблицу кодирования
     *
     * @return Таблица кодирования
     */
    public Map<Object, String> getEncodingTable() {
        return new HashMap<>(encodingTable);
    }

    /**
     * Возвращает таблицу декодирования.
     *
     * @return Таблица декодирования
     */
    public Map<String, Object> getDecodingTable() {
        return new HashMap<>(decodingTable);
    }
}