module org.openjfx {
    requires javafx.controls;
    requires java.persistence;
    requires java.desktop;
    requires org.slf4j;
    requires spring.beans;
    requires spring.core;
    requires spring.jdbc;
    requires spring.context;
    requires java.sql;
    requires com.zaxxer.hikari;
    exports org.openjfx;
    exports org.openjfx.model;
    exports org.openjfx.service;
}