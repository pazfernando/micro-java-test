package com.example.helloworld.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoDatabase;

@Service
public class DatabaseService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    @Value("${spring.datasource.url}")
    private String pgEndpoint;

    @Value("${db.postgres.proxy}")
    private String pgProxyEndpoint;

    @Value("${db.postgres.proxy.port}")
    private String pgProxyPort;

    @Value("${db.postgres.proxy.dbname}")
    private String pgProxyDbName;

    @Value("${db.postgres.proxy.user}")
    private String pgProxyUser;

    @Value("${db.postgres.proxy.password}")
    private String pgProxyPassword;

    @Value("${spring.data.mongodb.host}")
    private String docdbHost;

    @Value("${spring.data.mongodb.port}")
    private String docdbPort;

    @Value("${spring.data.mongodb.database}")
    private String docdbDatabase;

    @Value("${spring.data.mongodb.username}")
    private String docdbUser;

    @Value("${spring.data.mongodb.password}")
    private String docdbPassword;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    public String testPostgresConnectivity() throws Exception {
        String resultado;

        try {
            logger.info("pgEndpoint: " + pgEndpoint);
            resultado = jdbcTemplate.queryForObject("SELECT version()", String.class);
        } catch (Exception e) {
            logger.error("Error de SQL: {}", e.getMessage());
            throw e;
        }

        return resultado;
    }

    public String testPostgresProxyConnectivity() throws Exception {
        String resultado;
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", pgProxyEndpoint, pgProxyPort, pgProxyDbName);
        logger.info("pgProxyEndpoint: " + jdbcUrl);

        try (Connection conn = DriverManager.getConnection(jdbcUrl, pgProxyUser, pgProxyPassword);
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
            throw e;
        }

        return resultado;
    }

    public String testDocumentDBConnectivity() {
        String docdbUri = String.format(
            "mongodb://%s:%s@%s:%s/%s?replicaSet=rs0&readPreference=secondaryPreferred&retryWrites=false",
            docdbUser, docdbPassword, docdbHost, docdbPort, docdbDatabase); // Sin TLS o con TLS funciona para conectarse
    
        logger.info("Conectando a DocumentDB con URI: {}", docdbUri);

        try {
            MongoDatabase database = mongoTemplate.getDb();
            Document pingResult = database.runCommand(new Document("ping", 1));
            logger.info("Ping a DocumentDB -> {}", pingResult.toJson());

            Document buildInfo = database.runCommand(new Document("buildInfo", 1));
            return "DocumentDB buildInfo: " + buildInfo.toJson();
        } catch (Exception e) {
            logger.error("Error conectando a DocumentDB: {}", e.getMessage());
            throw e;
        }
    }
}
