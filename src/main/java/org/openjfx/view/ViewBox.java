package org.openjfx.view;

import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openjfx.service.Service;
import org.openjfx.service.StudentService;
import org.openjfx.model.AbstractModel;
import org.openjfx.orm.DbTemplate;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public abstract class ViewBox extends VBox {
    DbTemplate db;
    Service service;
    TableView tableView;
    Button add;
    Button del;
    Button refresh;
    TextField search;
    MenuButton viewSelector;
    Runnable addAction;
    Runnable delAction;
    Predicate<AbstractModel> predicate;

    public ViewBox() {
        tableView = new TableView<>();
        tableView.setEditable(true);
        add = new Button("add");
        del = new Button("delete");
        refresh = new Button("refresh");
        search = new TextField();
        viewSelector = new MenuButton();
        getChildren().addAll(tableView, new HBox(add, del, refresh, search));

        delAction = () -> {
            try {
                db.delete(tableView.getSelectionModel().getSelectedItem());
            } catch (Exception e) {
                Dialog dialog = new Dialog();
                dialog.setContentText(e.getMessage());
                dialog.getDialogPane().getButtonTypes().add(new ButtonType("ok", ButtonBar.ButtonData.OK_DONE));
                dialog.showAndWait();
            }
            update();
        };

        setOnKeyReleased(keyEvent -> {
            switch (keyEvent.getCode()) {
                case INSERT -> addAction.run();
                case DELETE -> delAction.run();
                case ESCAPE -> update();
            }
        });
        add.setOnAction(event -> addAction.run());
        del.setOnAction(event -> delAction.run());
        refresh.setOnAction(event -> update());
    }

    <T extends AbstractModel> void tryCommit(T object) {
        if (!service.check(object)) return;
        try {
            service.complete(object);
        } catch (RuntimeException e) {
            Dialog dialog = new Dialog();
            dialog.setContentText(e.getMessage());
            dialog.getDialogPane().getButtonTypes().add(new ButtonType("ok", ButtonBar.ButtonData.OK_DONE));
            dialog.showAndWait();
            return;
        }

        if (object.getId() != null) {
            db.update(object);
        } else {
            db.insert(object);
        }
        update();
    }

    static <T extends AbstractModel> Predicate getPredicate(Class<T> cls, List<String> params) {
        Predicate<T> filter = object -> true;
        for (var item : params) {
            if (!item.contains("=")) continue;
            var pair = Arrays.stream(item.split("=")).iterator();
            String f = pair.hasNext() ? pair.next() : null;
            String v = pair.hasNext() ? pair.next() : null;
            if (f == null || v == null) continue;

            Field field;
            try {
                field = cls.getDeclaredField(f);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            Object value;
            switch (f) {
                case "id":
                case "count":
                case "sid":
                case "cid":
                case "did":
                    value = Long.valueOf(v);
                    break;
                case "grade":
                    value = Double.valueOf(v);
                    break;
                default:
                    value = v;
            }

            field.setAccessible(true);
            filter = filter.and(object -> {
                try {
                    return field.get(object).equals(value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return filter;
    }
    public abstract void update();

    public void setDb(DbTemplate db) {
        this.db = db;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
