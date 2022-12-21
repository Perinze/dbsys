package org.openjfx.view;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import org.openjfx.model.AbstractModel;
import org.openjfx.orm.DbTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.function.Consumer;
//var userField = new TextField("user");
//passwordField.setText("password");

public class LoginBox extends VBox {
    DbTemplate db;
    Consumer exitHandler;
    public LoginBox() {
        var userLabel = new Label("username:");
        var userField = new TextField();
        var passwordLabel = new Label("password:");
        var passwordField = new PasswordField();
        var loginButton = new Button("login");
        getChildren().addAll(userLabel, userField, passwordLabel, passwordField, loginButton);

        Runnable login = () -> {
            db = createDb(
                    "jdbc:mariadb://localhost/dbsys?useSSL=false&characterEncoding=utf8",
                    userField.getText(),
                    passwordField.getText());
            exitHandler.accept(db);
        };

        setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                login.run();
            }
        });

        loginButton.setOnAction(event -> {
            login.run();
        });
    }

    public void setOnExit(Consumer<DbTemplate> handler) {
        this.exitHandler = handler;
    }

    private DbTemplate createDb(String url, String user, String password) {
        var config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        var dataSource = new HikariDataSource(config);
        var jdbcTemplate = new JdbcTemplate(dataSource);
        return new DbTemplate(jdbcTemplate, AbstractModel.class.getPackageName());
    }

    public DbTemplate getDb() {
        return db;
    }
}
