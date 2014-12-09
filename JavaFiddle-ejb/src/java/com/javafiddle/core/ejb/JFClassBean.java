/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javafiddle.core.ejb;

import com.javafiddle.core.jpa.JFClass;
import com.javafiddle.core.jpa.JFPackage;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Provides an interface for interaction with entity JFClass
 * @author roman
 */
@Named (value = "classBean")
@Stateless
public class JFClassBean {
    @PersistenceContext
    EntityManager em;

    public JFClassBean() {}
    
    public JFClass getClassById(Long id) {
        return em.find(JFClass.class, id);
    }
    
    public JFClass getClassByName(Long packageId, String className){
        JFPackage pack = em.find(JFPackage.class, packageId);
        return (JFClass) em.createQuery("select c from JFClass c "
                + "where c.jfpackage =:pack and c.className =:name")
                .setParameter("pack", pack)
                .setParameter("name", className)
                .getSingleResult();
    }
    
    public JFClass addClass(String className, String content, Long packageId, Long time) {
        JFClass clazz = new JFClass();
        clazz.setClassName(className);
        clazz.setContent(content);
        JFPackage pr = em.find(JFPackage.class, packageId);
        clazz.setJFPackage(pr);
        clazz.setLastSaveTime(time);
        em.persist(clazz);
        return this.getClassByName(packageId, className); 
    }

    public String getClassName(Long id) {
        return this.getClassById(id).getClassName();
    }

    public String getClassContent(Long id) {
        return this.getClassById(id).getContent();
    }
    
    public Long getLastSaveTime(Long id) {
        return this.getClassById(id).getLastSaveTime();
    }
    
    public void updateContent(Long id, String content) {
        JFClass clazz = this.getClassById(id);
        clazz.setContent(content);
        em.persist(clazz);
    }

    public void rename(Long id, String newName) {
        JFClass clazz = this.getClassById(id);
        clazz.setClassName(newName);
        em.persist(clazz);
    }

    public void delete(Long id) {
        JFClass clazz = this.getClassById(id);
        em.remove(clazz);
    }
}
