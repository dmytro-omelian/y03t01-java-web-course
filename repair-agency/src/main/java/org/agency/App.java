package org.agency;

import org.agency.controller.ActionController;
import org.agency.delegator.RepositoryDelegator;
import org.agency.delegator.ServiceDelegator;
import org.agency.service.operation.delegator.PerformerDelegator;
import org.agency.service.operation.performer.action.Action;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("[App] is started successfully.");
        // TODO add tests coverage
        // TODO add DTOs
        // TODO cached information for the password
        // TODO to cache everything that is possible
        // TODO create class with getting fields (for password ask about the same password twice)

        final String DB_URL = System.getenv("PG_DB_URL");
        final String USERNAME = System.getenv("PG_USERNAME");
        final String PASSWORD = System.getenv("PG_PASSWORD");

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            System.out.println("Connection was successful!");

            RepositoryDelegator repositoryDelegator = new RepositoryDelegator(connection);
            ServiceDelegator serviceDelegator = new ServiceDelegator(repositoryDelegator);
            PerformerDelegator performerDelegator = new PerformerDelegator(serviceDelegator);

            ActionController actionController = new ActionController(performerDelegator);
            boolean stop = false;
            while (!stop) {
                actionController.showActionsList();
                Action action = actionController.chooseAction();
                stop = actionController.performAction(action);
            }
            logger.info("Action performing is finished.");
        } catch (SQLException e) {
            logger.error("ticket was not created! See: " + e); // FIXME
            throw new RuntimeException(e);
        }
        // TODO there is default login and password for admin
        logger.info("[App] is finished successfully.");
    }

}
