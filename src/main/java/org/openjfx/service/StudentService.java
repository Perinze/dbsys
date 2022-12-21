package org.openjfx.service;

import org.openjfx.model.AbstractModel;
import org.openjfx.model.Dept;
import org.openjfx.model.Student;
import org.openjfx.orm.DbTemplate;

public class StudentService extends Service {

    @Override
    public <T extends AbstractModel> void complete(T object) {
        var student = (Student) object;
        if (student.getDid() == null && student.getDept() != null) {
            student.setDid(db.select("id")
                    .from(Dept.class)
                    .where("name = ?", student.getDept())
                    .unique().getId());
        }
    }

    @Override
    public <T extends AbstractModel> boolean check(T object) {
        var student = (Student) object;
        System.out.println(student);
        return checkString(student.getName())
                && checkString(student.getDept()) || checkLong(student.getDid());
    }
}
