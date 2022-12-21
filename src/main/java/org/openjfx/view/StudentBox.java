package org.openjfx.view;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.openjfx.service.StudentService;
import org.openjfx.model.AbstractModel;
import org.openjfx.model.Dept;
import org.openjfx.model.Grade;
import org.openjfx.model.Student;

import java.util.List;
import java.util.function.Consumer;

public class StudentBox extends ViewBox {

    public StudentBox() {
        super();

        TableColumn<Student, Long> idColumn = new TableColumn<>("id");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Student, String> nameColumn = new TableColumn<>("name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Student, String> deptColumn = new TableColumn<>("dept");
        deptColumn.setCellValueFactory(new PropertyValueFactory<>("dept"));
        deptColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Student, Double> gradeColumn = new TableColumn<>("grade");
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));

        tableView.getColumns().addAll(idColumn, nameColumn, deptColumn, gradeColumn);

        predicate = getPredicate(Student.class, List.of());

        addAction = () -> {
            var student = new Student();
            tableView.getItems().add(student);
            tableView.getSelectionModel().selectLast();
            int i = tableView.getSelectionModel().getSelectedIndex();
            tableView.edit(i, nameColumn);
        };

        nameColumn.setOnEditCommit(event -> {
            var student = event.getRowValue();
            student.setName(event.getNewValue());
            tryCommit(student);
        });

        Consumer<CellEditEvent<Student, String>> deptHandler = event -> {
            var student = event.getRowValue();
            student.setDept(event.getNewValue());
            tryCommit(student);
        };
        deptColumn.setOnEditCommit(event -> deptHandler.accept(event));
        search.setOnKeyTyped(keyEvent -> {
            predicate = getPredicate(Student.class, List.of(search.getText().split(" +")));
            update();
        });
    }

    @Override
    public void update() {
        tableView.getItems().clear();
        var students = db.from(Student.class).list();
        students.forEach(student -> {
            student.setDept(db.select("name")
                    .from(Dept.class)
                    .where("id = ?", student.getDid())
                    .unique().getName());
            var grade = db.select("grade")
                    .from(Grade.class)
                    .where("sid = ?", student.getId())
                    .list().stream().mapToDouble(Grade::getGrade).average();
            student.setGrade(grade.orElse(0));
        });
        students = students.stream().filter(predicate).toList();
        tableView.getItems().addAll(students);
    }
}
