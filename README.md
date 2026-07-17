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
encode file.txt archive.sf

# С паролем
encode file.txt archive.sf mypassword

# Несколько файлов и директорий
encode file1.txt,dir1,file2.txt archive.sf

# Без пароля
decode archive.sf output_dir

# С паролем
decode archive.sf output_dir mypassword