package org.openjfx.view;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.openjfx.model.Course;
import org.openjfx.model.Dept;
import org.openjfx.model.Grade;
import org.openjfx.model.Student;

import javax.persistence.Table;
import java.util.List;
import java.util.function.Consumer;

public class DeptBox extends ViewBox {

    public DeptBox() {
        super();

        TableColumn<Dept, Long> idColumn = new TableColumn<>("id");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Dept, String> nameColumn = new TableColumn<>("name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Dept, Long> countColumn = new TableColumn<>("count");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        TableColumn<Dept, Double> gradeColumn = new TableColumn<>("grade");
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));

        tableView.getColumns().addAll(idColumn, nameColumn, countColumn, gradeColumn);

        predicate = getPredicate(Dept.class, List.of());

        addAction = () -> {
            var dept = new Dept();
            tableView.getItems().add(dept);
            tableView.getSelectionModel().selectLast();
            int i = tableView.getSelectionModel().getSelectedIndex();
            tableView.edit(i, nameColumn);
        };

        Consumer<TableColumn.CellEditEvent<Dept, String>> nameHandler = event -> {
            var dept = event.getRowValue();
            dept.setName(event.getNewValue());
            tryCommit(dept);
        };
        nameColumn.setOnEditCommit(event -> nameHandler.accept(event));
        search.setOnKeyTyped(keyEvent -> {
            predicate = getPredicate(Dept.class, List.of(search.getText().split(" +")));
            update();
        });
    }

    @Override
    public void update() {
        tableView.getItems().clear();
        var depts = db.from(Dept.class).list();
        depts.forEach(dept -> {
            var students = db.from(Student.class)
                    .where("did = ?", dept.getId())
                    .list();
            dept.setCount(Long.valueOf(students.size()));
            var grade = students.stream().flatMapToDouble(student -> db.select("grade")
                            .from(Grade.class)
                            .where("sid = ?", student.getId())
                            .list().stream().mapToDouble(Grade::getGrade))
                            .average().orElse(0);
            dept.setGrade(grade);
        });
        depts = depts.stream().filter(predicate).toList();
        tableView.getItems().addAll(depts);
    }
}
