package org.openjfx.view;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.openjfx.model.AbstractModel;
import org.openjfx.model.Course;
import org.openjfx.model.Grade;
import org.openjfx.model.Student;

import java.util.List;
import java.util.function.Consumer;


public class CourseBox extends ViewBox {

    public CourseBox() {
        super();

        TableColumn<Course, Long> idColumn = new TableColumn<>("id");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Course, String> nameColumn = new TableColumn<>("name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Course, Long> countColumn = new TableColumn<>("count");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        TableColumn<Course, Double> gradeColumn = new TableColumn<>("grade");
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));

        tableView.getColumns().addAll(idColumn, nameColumn, countColumn, gradeColumn);

        predicate = getPredicate(Course.class, List.of());

        addAction = () -> {
            var course = new Course();
            tableView.getItems().add(course);
            tableView.getSelectionModel().selectLast();
            int i = tableView.getSelectionModel().getSelectedIndex();
            tableView.edit(i, nameColumn);
        };

        Consumer<TableColumn.CellEditEvent<Course, String>> nameHandler = event -> {
            var course = event.getRowValue();
            course.setName(event.getNewValue());
            tryCommit(course);
        };
        nameColumn.setOnEditCommit(event -> nameHandler.accept(event));
        search.setOnKeyTyped(keyEvent -> {
            predicate = getPredicate(Course.class, List.of(search.getText().split(" +")));
            update();
        });
    }

    @Override
    public void update() {
        tableView.getItems().clear();
        var courses = db.from(Course.class).list();
        courses.forEach(course -> {
            var grades = db.select("grade")
                    .from(Grade.class)
                    .where("cid = ?", course.getId())
                    .list();
            course.setCount((long) grades.size());
            course.setGrade(grades.stream().mapToDouble(Grade::getGrade).average().orElse(0));
        });
        courses = courses.stream().filter(predicate).toList();
        tableView.getItems().addAll(courses);
    }
}
