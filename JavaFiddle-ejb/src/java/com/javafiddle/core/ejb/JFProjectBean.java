/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javafiddle.core.ejb;

import com.javafiddle.core.jpa.JFClass;
import com.javafiddle.core.jpa.JFPackage;
import com.javafiddle.core.jpa.JFProject;
import com.javafiddle.core.jpa.User;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Provides an interface for interaction with entity JFProject
 * @author roman
 */
@Named(value = "projectBean")
@Stateless
@LocalBean
public class JFProjectBean {
    @PersistenceContext
    EntityManager em;
    
    public void addNewProject(String projectName, Long userId) {
        User user = em.find(User.class, userId);
        JFProject newProj = new JFProject();
        newProj.setUser(user);
        newProj.setProjectName(projectName);
        em.persist(newProj);
    }
    public JFProject getProjectById(Long projectId) {
        return em.find(JFProject.class, projectId);
    }
    public JFProject getProjectByName(Long userId, String projectName) {
        User user = em.find(User.class, userId);
        return (JFProject) em.createQuery("select p from JFProject p "
                + "where p.user =:user and p.projectName =:name")
                .setParameter("pack", user)
                .setParameter("name", projectName)
                .getSingleResult();
    }
    
    public String getProjectName(Long projectId) {
        return this.getProjectById(projectId).getProjectName();
    }
    public void renameProject(Long projectId, String newName) {
        JFProject jpr = this.getProjectById(projectId);
        jpr.setProjectName(newName);
        em.persist(newName);
    }
    public void deleteProject(Long projectId) {
        em.remove(this.getProjectById(projectId));
    }
    public List<Long> getPackagesOfProject(Long projectId) {
        JFProject proj = this.getProjectById(projectId);
        return em.createQuery("select p.id from JFPackage p where p.jfproject =:project")
                .setParameter("project", proj)
                .getResultList();
    }
}
