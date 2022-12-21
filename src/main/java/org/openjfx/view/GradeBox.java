package org.openjfx.view;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import org.openjfx.model.Course;
import org.openjfx.model.Dept;
import org.openjfx.model.Grade;
import org.openjfx.model.Student;
import org.openjfx.service.GradeService;

import java.util.List;
import java.util.function.Consumer;

public class GradeBox extends ViewBox {

    public GradeBox() {
        super();

        TableColumn<Grade, Long> idColumn = new TableColumn<>("id");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Grade, String> courseColumn = new TableColumn<>("course");
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
        courseColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Grade, String> studentColumn = new TableColumn<>("student");
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("student"));
        studentColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Grade, Double> gradeColumn = new TableColumn<>("grade");
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        gradeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        tableView.getColumns().addAll(idColumn, courseColumn, studentColumn, gradeColumn);

        predicate = getPredicate(Grade.class, List.of());

        addAction = () -> {
            var grade = new Grade();
            tableView.getItems().add(grade);
            tableView.getSelectionModel().selectLast();
            int i = tableView.getSelectionModel().getSelectedIndex();
            tableView.edit(i, courseColumn);
        };

        courseColumn.setOnEditCommit(event -> {
            var grade = event.getRowValue();
            grade.setCourse(event.getNewValue());
            tryCommit(grade);
        });

        studentColumn.setOnEditCommit(event -> {
            var grade = event.getRowValue();
            grade.setStudent(event.getNewValue());
            tryCommit(grade);
        });

        Consumer<TableColumn.CellEditEvent<Grade, Double>> gradeHandler = event -> {
            var grade = event.getRowValue();
            grade.setGrade(Double.valueOf(event.getNewValue()));
            tryCommit(grade);
        };
        gradeColumn.setOnEditCommit(event -> gradeHandler.accept(event));
        search.setOnKeyTyped(keyEvent -> {
            predicate = getPredicate(Grade.class, List.of(search.getText().split(" +")));
            update();
        });
    }

    @Override
    public void update() {
        tableView.getItems().clear();
        var grades = db.from(Grade.class).list();
        grades.forEach(grade -> {
            grade.setStudent(db.select("name")
                    .from(Student.class)
                    .where("id = ?", grade.getSid())
                    .unique().getName());
            grade.setCourse(db.select("name")
                    .from(Course.class)
                    .where("id = ?", grade.getCid())
                    .unique().getName());
        });
        grades = grades.stream().filter(predicate).toList();
        tableView.getItems().addAll(grades);
    }
}
