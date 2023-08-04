package org.example;

import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Aggregates.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.*;

public class MongoDBStorage
{
    MongoClient mongoClient;
    MongoDatabase database;
    MongoCollection<Document> productList;
    MongoCollection<Document> marketList;

    //Инициализация базы данных
    void init() {
            mongoClient = MongoClients.create();
            database = mongoClient.getDatabase("testDB");
            database.drop();
            productList = database.getCollection("products");
            marketList = database.getCollection("markets");
    }

    //Добавляем продукт в базу данных(на склад ;) )
    void addProduct(Product product){
        Document doc = new Document("name", product.getName())
                .append("price", product.getPrice());
        productList.insertOne(doc);
        //System.out.println(doc.toJson());
    }

    //Запрашиваем продукт из базы данных
    Product getProduct(String name){
        Document doc = productList.find(eq("name", name)).first();
        if (doc != null)return new Product(doc.getString("name"), doc.getInteger("price"));
        return null;
    }
    //Проверка наличия магазина
    boolean existMarket(String name){
        Document doc = marketList.find(eq("name", name)).first();
        if(doc == null) return false;
        return true;
    }

    //Добавление товара в магазин
    void addMarket(Market market){
        Document doc = new Document("name", market.getName());
        List<Product> products = market.getProductList();
        for(Product product : products){
            doc.append("product_list", new Document()
                    .append("name", product.getName())
                    .append("price", product.getPrice()));
        }
        marketList.insertOne(doc);
        //System.out.println(doc.toJson());
    }

    //Выставление товара в магазине
    void exposeProduct(Product product, String market){
        Document doc = marketList.find(eq("name", market)).first();
        Bson update = Updates.addToSet("product_list", new Document()
                .append("name", product.getName())
                .append("price", product.getPrice()));
        marketList.updateOne(doc,update);
        //System.out.println(marketList.find(eq("name", market)).first().toJson());
    }
    //Вывод статистики по магазинам
    void totalStatistic(){
        AggregateIterable<Document> aggregateProduct = marketList.aggregate(Arrays.asList(
                unwind("$product_list"),
                group("$name", sum("count",1),
                        avg("averagePrice","$product_list.price"),
                        min("minPrice", "$product_list.price"),
                        max("max_price", "$product_list.price"))
                //Альтернативный вариант
    //            Document.parse("{ $group: { _id: {name: '$name'}, " +
    //                    "count: { $sum: 1}" +
    //                    "averagePrice: { $avg: '$product_list.price'}" +
    //                    "minPrice: { $min: '$product_list.price'}" +
    //                    "maxPrice: { $max: '$product_list.price'}}}")
        ));
        for(Document document : aggregateProduct){
            System.out.println((document.toJson()));
        }
    }
}
