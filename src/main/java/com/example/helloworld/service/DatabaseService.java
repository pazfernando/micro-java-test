package com.example.helloworld.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

@Service
public class DatabaseService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    @Value("${db.postgres.host}")
    private String pgHost;

    @Value("${db.postgres.port}")
    private String pgPort;

    @Value("${db.postgres.dbname}")
    private String pgDbName;

    @Value("${db.postgres.user}")
    private String pgUser;

    @Value("${db.postgres.password}")
    private String pgPassword;

    @Value("${db.postgres.proxy}")
    private String pgProxyEndpoint;

    @Value("${db.docdb.host}")
    private String docdbHost;

    @Value("${db.docdb.port}")
    private String docdbPort;

    @Value("${db.docdb.dbname}")
    private String docdbDatabase;

    @Value("${db.docdb.user}")
    private String docdbUser;

    @Value("${db.docdb.password}")
    private String docdbPassword;

    public String testPostgresConnectivity() {
        return testPostgresConnection(pgHost);
    }

    public String testPostgresProxyConnectivity() {
        return testPostgresConnection(pgProxyEndpoint);
    }

    private String testPostgresConnection(String host) {
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, pgPort, pgDbName);
        logger.info("Conectando a PostgreSQL: {}", jdbcUrl);

        String resultado;
        try (Connection conn = DriverManager.getConnection(jdbcUrl, pgUser, pgPassword);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT version();")) {

            logger.info("Conexión a PostgreSQL exitosa.");

            if (rs.next()) {
                resultado = "Versión de PostgreSQL: " + rs.getString(1);
            } else {
                resultado = "No se pudo obtener la versión de PostgreSQL.";
            }

            logger.info("Resultado de query: {}", resultado);

        } catch (SQLException e) {
            logger.error("Error de SQL: {}", e.getMessage());
            return "Error al conectar: " + e.getMessage();
        }

        return resultado;
    }

    public String testDocumentDBConnectivity() {
        String docdbUri = String.format(
                "mongodb://%s:%s@%s:%s/%s?ssl=true&replicaSet=rs0&retryWrites=false",
                docdbUser, docdbPassword, docdbHost, docdbPort, docdbDatabase);

        logger.info("Conectando a DocumentDB con URI: {}", docdbUri);

        try {
            ConnectionString connectionString = new ConnectionString(docdbUri);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .build();

            try (MongoClient mongoClient = MongoClients.create(settings)) {
                MongoDatabase database = mongoClient.getDatabase(docdbDatabase);

                Document pingResult = database.runCommand(new Document("ping", 1));
                logger.info("Ping a DocumentDB -> {}", pingResult.toJson());

                Document buildInfo = database.runCommand(new Document("buildInfo", 1));
                return "DocumentDB buildInfo: " + buildInfo.toJson();
            }
        } catch (Exception e) {
            logger.error("Error conectando a DocumentDB: {}", e.getMessage());
            return "Error conectando a DocumentDB: " + e.getMessage();
        }
    }
}
