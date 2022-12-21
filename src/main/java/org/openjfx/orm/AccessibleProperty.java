package org.openjfx.orm;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;



/**
 * Represent a bean property.
 *
 * @author liaoxuefeng
 */
public class AccessibleProperty {

    // method
    final Method getter;
    final Method setter;

    // java type
    final Class<?> propertyType;

    // java bean property name
    final String propertyName;

    // table column name
    final String columnName;

    boolean isId() {
        return this.getter.isAnnotationPresent(Id.class);
    }

    // is id && marked as @GeneratedValue(strategy=GenerationType.IDENTITY)
    boolean isIdentityId() {
        if (!isId()) {
            return false;
        }
        GeneratedValue gv = this.getter.getAnnotation(GeneratedValue.class);
        if (gv == null) {
            return false;
        }
        GenerationType gt = gv.strategy();
        return gt == GenerationType.IDENTITY;
    }

    boolean isInsertable() {
        if (isIdentityId()) {
            return false;
        }
        Column col = this.getter.getAnnotation(Column.class);
        return col == null || col.insertable();
    }

    boolean isUpdatable() {
        if (isId()) {
            return false;
        }
        Column col = this.getter.getAnnotation(Column.class);
        return col == null || col.updatable();
    }

    public AccessibleProperty(PropertyDescriptor pd) {
        this.getter = pd.getReadMethod();
        this.setter = pd.getWriteMethod();
        this.propertyType = pd.getReadMethod().getReturnType();
        this.propertyName = pd.getName();
        this.columnName = getColumnName(pd.getReadMethod(), propertyName);
    }

    private static String getColumnName(Method m, String defaultName) {
        Column col = m.getAnnotation(Column.class);
        if (col == null || col.name().isEmpty()) {
            return defaultName;
        }
        return col.name();
    }
}
