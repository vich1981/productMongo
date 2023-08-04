package org.example;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static MongoDBStorage mongoStorage = new MongoDBStorage();
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        ArrayList<Market> markets = new ArrayList<>();
        ArrayList<Product> products = new ArrayList<>();

        mongoStorage.init();
        //Тестовая загрузка магазинов товарами
        initMarkets(markets);
        initProducts(products);
        initExpose(products, markets);


        while(true){
            System.out.print("Введите команду:");
            String input = in.nextLine();
            int answer = parseInput(input);
            if(answer == 0) System.out.println("ОК");
            else System.out.println("Команда не выполнена");
        }
    }
    //Создаем магазины
    static void initMarkets(ArrayList<Market> markets){
        markets.add(new Market("Пятерочка"));
        markets.add(new Market("Дикси"));
        markets.add(new Market("Перекресток"));
        markets.add(new Market("Да"));
        markets.add(new Market("Лента"));
        for(Market market : markets){
            mongoStorage.addMarket(market);
        }

    }
    //Создаем продукты
    static void initProducts(ArrayList<Product> products){
        products.add(new Product("Мясо", 560));
        products.add(new Product("Рыба", 780));
        products.add(new Product("Хлеб", 56));
        products.add(new Product("Вафли", 115));
        products.add(new Product("Сыр", 148));
        products.add(new Product("Пиво", 56));
        products.add(new Product("Конфеты", 156));
        products.add(new Product("Вода", 60));
        products.add(new Product("Молоко", 95));
        for(Product product : products){
            mongoStorage.addProduct(product);
        }
    }
    //Выставляем продукты в магазины
    static void initExpose(ArrayList<Product> products, ArrayList<Market> markets){
        for(Market market : markets){
            for(Product product : products){
                mongoStorage.exposeProduct(product, market.getName());
            }
        }
    }

    //Обработка входных данных от пользователя
    static int parseInput(String input){
        String[] fragments = input.split(" ");
        if (fragments[0].equals("ДОБАВИТЬ_МАГАЗИН")){
            if(fragments.length == 2) {
                mongoStorage.addMarket(new Market(fragments[1]));
                return 0;
            }
            else System.out.println("Неверные параметры команды");
            return -1;
        }
        else if (fragments[0].equals("ДОБАВИТЬ_ТОВАР")){
            if(fragments.length == 3){
                try {
                    int price = Integer.parseInt(fragments[2]);
                    mongoStorage.addProduct(new Product(fragments[1],price));
                    return 0;
                }
                catch(NumberFormatException ex){
                    System.out.println("Неверно указана цена товара");
                }

            }
            else System.out.println("Неверные параметры команды");
            return -1;
        }
        else if (fragments[0].equals("ВЫСТАВИТЬ_ТОВАР")){
            if(fragments.length == 3){
                //System.out.println("Кол-во параметров верно");
                Product product = mongoStorage.getProduct(fragments[1]);
                //System.out.println("Запросили продукт:" + product.getName());
                boolean isMarket = mongoStorage.existMarket(fragments[2]);
                //System.out.println("Магазин существует?" + isMarket);
                if(product != null && isMarket){
                    mongoStorage.exposeProduct(product, fragments[2]);
                    return 0;
                }
            }
            System.out.println("Неверные параметры команды");
            return -1;

        }
        else if (fragments[0].equals("СТАТИСТИКА_ТОВАРОВ") && fragments.length == 1){
            mongoStorage.totalStatistic();
            return 0;
        }
        System.out.println("Неверные параметры команды");
        return -1;
    }

}