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
@Table (name = "jfpackages", schema = "javafiddle",
        uniqueConstraints = @UniqueConstraint(columnNames = {"package_name", "jfproject"}))
public class JFPackage implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column (name = "package_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column (name = "package_name")
    private String packageName;

    @ManyToOne
    @JoinColumn(name = "jfproject")
    private JFProject jfproject;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public JFProject getJFProject() {
        return jfproject;
    }

    public void setJFProject(JFProject jfproject) {
        this.jfproject = jfproject;
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
        if (!(object instanceof JFPackage)) {
            return false;
        }
        JFPackage other = (JFPackage) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.javafiddle.core.jpa.JFPackage[ id=" + id + " ]";
    }
    
}
