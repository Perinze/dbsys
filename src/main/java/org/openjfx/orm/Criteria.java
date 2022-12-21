package org.openjfx.orm;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.util.ArrayList;
import java.util.List;

/**
 * Hold criteria query information.
 *
 * @author liaoxuefeng
 *
 * @param <T> Entity type.
 */
final class Criteria<T> {

    DbTemplate db;
    Mapper<T> mapper;
    Class<T> clazz;
    List<String> select = null;
    boolean distinct = false;
    String table = null;
    String where = "";
    List<Object> whereParams = new ArrayList<>();
    List<String> orderBy = null;
    int offset = 0;
    int maxResults = 0;

    Criteria(DbTemplate db) {
        this.db = db;
    }

    String sql() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("SELECT ");
        sb.append((select == null ? "*" : String.join(", ", select)));
        sb.append(" FROM ").append(mapper.tableName);
        if (where != null && where != "") {
            sb.append(" WHERE ").append(String.join(" ", where));
        }
        if (orderBy != null) {
            sb.append(" ORDER BY ").append(String.join(", ", orderBy));
        }
        if (offset >= 0 && maxResults > 0) {
            sb.append(" LIMIT ?, ?");
        }
        String s = sb.toString();
        //System.out.println(s);
        return s;
    }

    Object[] params() {
        List<Object> params = new ArrayList<>();
        if (where != null && where != "") {
            for (Object obj : whereParams) {
                if (obj == null) {
                    params.add(null);
                } else {
                    params.add(obj);
                }
            }
        }
        if (offset >= 0 && maxResults > 0) {
            params.add(offset);
            params.add(maxResults);
        }
        return params.toArray();
    }

    List<T> list() {
        String selectSql = sql();
        Object[] selectParams = params();
        return db.jdbcTemplate.query(selectSql, mapper.rowMapper, params());
    }

    T first() {
        this.offset = 0;
        this.maxResults = 1;
        String selectSql = sql();
        Object[] selectParams = params();
        List<T> list = db.jdbcTemplate.query(selectSql, mapper.rowMapper, params());
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    T unique() {
        this.offset = 0;
        this.maxResults = 2;
        String selectSql = sql();
        Object[] selectParams = params();
        List<T> list = db.jdbcTemplate.query(selectSql, mapper.rowMapper, selectParams);
        if (list.isEmpty()) {
            throw new NoResultException("Expected unique row but nothing found.");
        }
        if (list.size() > 1) {
            throw new NonUniqueResultException("Expected unique row but more than 1 rows found.");
        }
        return list.get(0);
    }
}
