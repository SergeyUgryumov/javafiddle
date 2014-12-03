/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javafiddle.core.jpa;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author roman
 */
@Entity 
@Table (name = "jfclasses", schema = "javafiddle",
        uniqueConstraints = @UniqueConstraint(columnNames = {"class_name", "jfpackage"}))
public class JFClass implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    //@Column (name = "class_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column (name = "class_name")
    private String className;

    @Column (name = "content")
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "jfpackage")
    private JFPackage jfpackage;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public JFPackage getJFPackage() {
        return jfpackage;
    }

    public void setJFPackage(JFPackage jfpackage) {
        this.jfpackage = jfpackage;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof JFClass)) {
            return false;
        }
        JFClass other = (JFClass) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.javafiddle.core.jpa.JFClass[ id=" + id + " ]";
    }
    
}
