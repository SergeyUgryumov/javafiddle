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
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
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
public class JFProjectBean{
    @EJB
    private JFPackageBean packBean;
    @EJB
    private JFClassBean classBean;
    @EJB
    private ProjectManager pm;
    @PersistenceContext
    EntityManager em;
    
    public JFProject addNewProject(String projectName, Long userId) {
        User user = em.find(User.class, userId);
        JFProject newProj = new JFProject();
        newProj.setUser(user);
        newProj.setProjectName(projectName);
        em.persist(newProj);
        return this.getProjectByName(userId, projectName);
    }
    public JFProject getProjectById(Long projectId) {
        return em.find(JFProject.class, projectId);
    }
    public JFProject getProjectByName(Long userId, String projectName) {
        User user = em.find(User.class, userId);
        return (JFProject) em.createQuery("select p from JFProject p "
                + "where p.user =:user and p.projectName =:name")
                .setParameter("user", user)
                .setParameter("name", projectName)
                .getSingleResult();
    }
    
    public String getProjectName(Long projectId) {
        return this.getProjectById(projectId).getProjectName();
    }
    public void rename(Long projectId, String newName) {
        JFProject jpr = this.getProjectById(projectId);
        jpr.setProjectName(newName);
        em.persist(jpr);
    }
    public void delete(Long projectId) {
        em.remove(this.getProjectById(projectId));
    }
    public List<Long> getPackagesOfProject(Long projectId) {
        JFProject proj = this.getProjectById(projectId);
        return em.createQuery("select p.id from JFPackage p where p.jfproject =:project")
                .setParameter("project", proj)
                .getResultList();
    }
    public List<String> getNamesOfPackagesOfProject(Long projectId) {
        JFProject proj = this.getProjectById(projectId);
        return em.createQuery("select p.packageName from JFPackage p where p.jfproject =:project")
                .setParameter("project", proj)
                .getResultList();
    }
    /**
     * Creates new project with empty example class and adds it to the database.
     * @param userId
     * @return 
     */
    public Long addNewDefaultProject(Long userId) {
        JFProject newProj = this.addNewProject("MyFirstProject", userId);
        JFPackage newPack = packBean.addPackage(this.getProjectByName(userId, 
                "MyFirstProject").getId(), "com.javafiddle.main");
        
        JFClass newClass = classBean.addClass("Main.java", "", 
                packBean.getPackageByName(newProj.getId(), "com.javafiddle.main").getId(), 
                (new Date()).getTime());
        return newClass.getId();
    }
}
