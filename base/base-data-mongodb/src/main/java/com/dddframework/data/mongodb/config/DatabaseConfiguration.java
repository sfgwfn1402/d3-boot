//
//
//import com.mongodb.reactivestreams.client.MongoClient;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.mongodb.core.MongoTemplate;
//
//@Configuration
//public class  DatabaseConfiguration {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);
//
//    @Value("${spring.data.mongodb.uri}")
//    private String mongoUri;
//
//    @Value("${spring.data.mongodb.database}")
//    private String mongoDbName;
//
//    @Primary
//    @Bean
//    public MongoTemplate mongoTemplate() {
//
//        LOGGER.debug(" instantiating MongoDbFactory ");
//
//        SimpleMongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongoClient(), mongoDbName);
//
//        return new MongoTemplate(mongoDbFactory);
//
//    }
//
//    @Primary
//    @Bean
//    public MongoClient mongoClient() {
//        return new MongoClient(mongoClientURI());
//    }
//
//    @SuppressWarnings("deprecation")
//	@Primary
//    @Bean
//    public MongoClientURI mongoClientURI() {
//        LOGGER.debug(" creating connection with mongodb with uri [{}] ", mongoUri);
//        //return new MongoClientURI(mongoUri);
//
//        MongoClientOptions.Builder options = MongoClientOptions.builder();
//        options.socketKeepAlive(true);
//        return new MongoClientURI(mongoUri, options);
//
//    }
//
//}
//
//
//
//@Configuration
//@EnableReactiveMongoRepositories(basePackageClasses =
//        OrderRepository.class)
//public class MongoConfig extends AbstractReactiveMongoConfiguration {
//
//    @Bean
//    @Override
//    public MongoClient reactiveMongoClient() {
//        return MongoClients.create();
//    }
//
//    @Override
//    protected String getDatabaseName() {
//        return "order_test";
//    }
//}

