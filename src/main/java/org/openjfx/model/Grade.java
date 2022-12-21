package org.openjfx.model;

import javax.persistence.*;

@Entity
@Table(name = "grade")
public class Grade extends AbstractModel {
    private Long sid;
    private Long cid;
    private String student;
    private String course;
    private Double grade;

    @Column(nullable = false, updatable = false)
    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    @Column(nullable = false, updatable = false)
    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    @Transient
    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    @Transient
    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    @Column(nullable = false)
    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }
}
