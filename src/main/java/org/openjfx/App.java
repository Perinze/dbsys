package org.openjfx;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.openjfx.model.Course;
import org.openjfx.model.Student;
import org.openjfx.orm.DbTemplate;
import org.openjfx.service.*;
import org.openjfx.view.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * JavaFX App
 */
public class App extends Application {
    DbTemplate db;

    @Override
    public void start(Stage stage) {
        var loginBox = new LoginBox();
        List<String> names = List.of("course", "dept", "grade", "student");
        List<ViewBox> boxes = List.of(
                new CourseBox(),
                new DeptBox(),
                new GradeBox(),
                new StudentBox());
        List<Service> services = List.of(
                new CourseService(),
                new DeptService(),
                new GradeService(),
                new StudentService());

        Iterator<String> nameIter = names.iterator();
        List<Tab> tabs = boxes.stream().map(box -> {
            var tab = new Tab(nameIter.next(), box);
            tab.setClosable(false);
            return tab;
        }).toList();
        TabPane tabPane = new TabPane(tabs.toArray(Tab[]::new));
        tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);

        Scene scene = new Scene(tabPane);
        loginBox.setOnExit(db -> {
            this.db = db;
            assert db != null;
            services.forEach(service -> service.setDb(db));
            Iterator<Service> iter = services.listIterator();
            boxes.forEach(box -> {
                box.setDb(db);
                box.setService(iter.next());
                box.update();
            });
            stage.setScene(scene);
            stage.show();
        });

        stage.setScene(new Scene(loginBox));
        stage.show();
    }

    void setDb(DbTemplate db) {
        this.db = db;
    }

    public static void main(String[] args) {
        launch();
    }

}