Основные моменты:
1) REST Restaurant Service с использованием JDBC и Servlet;
2) Без использования Spring, Hibernate;
3) Использованы Mapstruckt, Lombok;
4) Unit тесты, с использованием Mockito и Junit;
5) Интеграционное тестирование с помощью testcontainers;
6) БД Postgres;
7) War и  БД развернуты при помощи Tomcat9 по адресу:<br>http://31.128.40.167:8085/aston_rest-1.0-SNAPSHOT/

Сущности:

1) OrderDetail (детали заказа)
Поля:
id
OrderStatus orderStatus;
List<Product> products; // (OneToMany)
BigDecimal totalAmount;

2) Product (сущность продукта)
Поля:
id
String name;
BigDecimal price;
int quantity;
boolean available;
List<ProductCategory> productCategories; // (ManyToMany)

3) ProductCategory
Поля:
id
String name;
CategoryType type;
List<Product> products; // (ManyToMany)

Реализованы CRUD операции для OrderDetail и Product.

4) OrderApproval (подтверждение заказа) создана для будущего развития.
Поля:
id
OrderDetail orderDetail; // (OneToOne)

[Не большая постман коллекция для основных запросов.](https://github.com/kirillkormilcev/aston_rest/blob/master/Aston%20restauraunt.postman_collection.json)

Схема таблиц базы данных:
![Схема таблиц базы данных](./src/main/resources/BD_diagram.png)
