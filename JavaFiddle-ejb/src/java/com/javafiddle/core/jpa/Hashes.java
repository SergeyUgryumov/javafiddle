package com.javafiddle.core.jpa;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author roman
 */
@Entity
@Table(name = "hashes", schema = "javafiddle")
public class Hashes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column (name = "project_hash")
    private String projectHash;
    
    @ManyToOne
    @JoinColumn(name = "jfproject")
    JFProject jfproject;
    
    @Column (name = "commit_time")
    Long commitTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectHash() {
        return projectHash;
    }

    public void setProjectHash(String projectHash) {
        this.projectHash = projectHash;
    }

    public JFProject getJFProject() {
        return jfproject;
    }

    public void setJFProject(JFProject jfproject) {
        this.jfproject = jfproject;
    }

    public Long getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(Long commitTime) {
        this.commitTime = commitTime;
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
        if (!(object instanceof Hashes)) {
            return false;
        }
        Hashes other = (Hashes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.javafiddle.core.jpa.Hashes[ id=" + id + " ]";
    }
    
}
