package org.openjfx.service;

import org.openjfx.model.AbstractModel;
import org.openjfx.orm.DbTemplate;

public abstract class Service {
    DbTemplate db;

    public abstract <T extends AbstractModel> void complete(T object);
    public abstract <T extends AbstractModel> boolean check(T object);

    public void setDb(DbTemplate db) {
        this.db = db;
    }
    static boolean checkString(String s) {
        return s != null && !s.equals("");
    }

    static boolean checkLong(Long x) {
        return x != null && x != 0;
    }

    static boolean checkDouble(Double x) {
        return x != null && x != 0;
    }
}
