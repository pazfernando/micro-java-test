package com.example.helloworld.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.helloworld.service.DatabaseService;

@RestController
@RequestMapping("/api")
public class HelloController {
    private final DatabaseService databaseService;

    public HelloController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping("/db-test")
    public String testDatabaseConnections(@RequestParam(defaultValue = "all") String dbType) throws Exception {
        switch (dbType.toLowerCase()) {
            case "none":
                return "No se ejecuto nada.";
            case "postgres":
                return String.format("dbType: %s, Resultado de PostgreSQL: %s", 
                    dbType, databaseService.testPostgresConnectivity());
            case "postgres-proxy":
                return String.format("dbType: %s, Resultado de PostgreSQL-proxy: %s", 
                    dbType, databaseService.testPostgresProxyConnectivity()); 
            case "documentdb":
                return String.format("dbType: %s, Resultado de DocumentDB: %s", 
                    dbType, databaseService.testDocumentDBConnectivity());
            case "all":
                String postgresResult = databaseService.testPostgresConnectivity();
                String postgresProxyResult = databaseService.testPostgresProxyConnectivity();
                String docdbResult = databaseService.testDocumentDBConnectivity();
                return String.format(
                    "dbType: %s, Resultado de PostgreSQL: %s, Resultado de PostgreSQL-proxy: %s, Resultado de DocumentDB: %s",
                    dbType, postgresResult, postgresProxyResult, docdbResult);
            default:
                return "dbType not supported.";
        }
    }
}
