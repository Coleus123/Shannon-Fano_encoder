# Shannon-Fano Архиватор

**Автор:** Огородников Александр

**Алгоритм кодирования:** Шеннона-Фано

## Возможности
- Кодирование и декодирование файлов и директорий
- Поддержка текстовых и бинарных файлов
- Сохранение структуры директорий и имен файлов
- Контроль целостности через SHA-256
- Защита архива паролем

## Примеры использования

### Кодирование
```bash
# Один файл
java -jar Shannon-Fano_encoder.jar encode file.txt archive.sf

# С паролем
java -jar Shannon-Fano_encoder.jar encode file.txt archive.sf mypassword

# Несколько файлов и директорий
java -jar Shannon-Fano_encoder.jar encode file1.txt,dir1,file2.txt archive.sf

# Без пароля
java -jar Shannon-Fano_encoder.jar decode archive.sf output_dir

# С паролем
java -jar Shannon-Fano_encoder.jar decode archive.sf output_dir mypassword