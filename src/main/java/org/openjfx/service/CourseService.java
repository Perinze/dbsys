package org.openjfx.service;

import org.openjfx.model.AbstractModel;
import org.openjfx.model.Course;
import org.openjfx.orm.DbTemplate;

public class CourseService extends Service {

    @Override
    public <T extends AbstractModel> void complete(T object) {
    }

    @Override
    public <T extends AbstractModel> boolean check(T object) {
        var course = (Course) object;
        System.out.println(course);
        return checkString(course.getName());
    }
}
