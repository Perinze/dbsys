package org.openjfx.service;

import org.openjfx.model.*;
import org.openjfx.orm.DbTemplate;

import javax.persistence.NoResultException;

import static org.openjfx.service.Service.*;

public class GradeService extends Service {

    @Override
    public <T extends AbstractModel> void complete(T object) {
        var grade = (Grade) object;
        if (grade.getSid() == null && grade.getStudent() != null) {
            try {
                grade.setSid(db.select("id")
                        .from(Student.class)
                        .where("name = ?", grade.getStudent())
                        .unique().getId());
            } catch (NoResultException e) {
                throw new RuntimeException(String.format("Student '%s' not found", grade.getStudent()));
            }
        }
        if (grade.getCid() == null && grade.getCourse() != null) {
            try {
                grade.setCid(db.select("id")
                        .from(Course.class)
                        .where("name = ?", grade.getCourse())
                        .unique().getId());
            } catch (NoResultException e) {
                throw new RuntimeException(String.format("Course '%s' not found", grade.getCourse()));
            }
        }
    }

    @Override
    public <T extends AbstractModel> boolean check(T object) {
        var grade = (Grade) object;
        return checkDouble(grade.getGrade())
                && (checkLong(grade.getSid()) || checkString(grade.getStudent()))
                && (checkLong(grade.getCid()) || checkString(grade.getCourse()));
    }
}
