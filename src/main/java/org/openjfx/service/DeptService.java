package org.openjfx.service;

import org.openjfx.model.AbstractModel;
import org.openjfx.model.Dept;
import org.openjfx.orm.DbTemplate;

public class DeptService extends Service {

    @Override
    public <T extends AbstractModel> void complete(T object) {

    }

    @Override
    public <T extends AbstractModel> boolean check(T object) {
        var dept = (Dept) object;
        System.out.println(dept);
        return checkString(dept.getName());
    }

}
