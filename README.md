#### [Lesson from Java QA Engineer (by OTUS)][link]:
#### Запуск тестов: mvn clean test

---
- Сайт www.220-volt.ru
- Электроэнструменты -> Перфораторы
- Выбрать марки Makita и Зубр
- Отсортировать по цене (min->max)
- Добавить в сравнение первый перфоратор "Зубр" и первый перфоратор "Makita"
- Перейти на страницу сравнения. Убедиться, что в сравнении корректные перфораторы.
---
#### Критерии оценки:
- +1 балл - код компилируется и запускается
- +1 балл - код запускается без дополнительных действий со стороны проверяющего (не нужно скачивать WebDriver, решать конфликты зависимостей и т.п.) 
- +1 балл - задействован WebDriverManager 
- +1 балл - логи пишутся в консоль и файл 
- +1 балл - тест проходит без падений (допускается падение теса только при некорректной работе SUT)
- +2 балла - тест проходит при разной скорости интернет-соединения
- -1 балл - за каждый Thread.sleep()
---
#### Максимум 8 баллов

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

[link]: <https://otus.ru/learning/102096/>
