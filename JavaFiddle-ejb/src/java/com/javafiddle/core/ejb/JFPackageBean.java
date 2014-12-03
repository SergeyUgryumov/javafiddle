/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javafiddle.core.ejb;

import com.javafiddle.core.jpa.JFPackage;
import com.javafiddle.core.jpa.JFProject;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Provides an interface for interaction with entity JFPackage
 * @author roman
 */
@Named (value = "packageBean")
@Stateless
public class JFPackageBean {

    @PersistenceContext
    EntityManager em;
    
    public JFPackage getPackageById(Long id) {
        return em.find(JFPackage.class, id);
    }
    public JFPackage getPackageByName(Long projectId, String packageName) {
        JFProject proj = em.find(JFProject.class, projectId);
        return (JFPackage) em.createQuery("select p from JFPackage p "
                + "where p.jfproject =:proj and p.packageName =:name")
                .setParameter("proj", proj)
                .setParameter("name", packageName)
                .getSingleResult();
    }
    
    public String getPackageName(Long id) {
        return this.getPackageById(id).getPackageName();
    }

    public void addPackage(Long projectId, String packageName) {
        JFPackage newPack = new JFPackage();
        newPack.setPackageName(packageName);
        JFProject pr = em.find(JFProject.class, projectId);
        newPack.setJFProject(pr);
        em.persist(newPack);
    }
    
    public void renamePackage(Long packageId, String newPackName) {
        JFPackage pack = this.getPackageById(packageId);
        pack.setPackageName(newPackName);
        em.persist(pack);
    }
    
    public void deletePackage(Long packageId) {
        JFPackage pack = this.getPackageById(packageId);
        em.remove(pack);
    }
    public List<Long> getClassesOfPackage(Long packageId) {
        JFPackage pack = this.getPackageById(packageId);
        return em.createQuery("select c.id from JFClass c where c.jfpackage =:package")
                .setParameter("package", pack)
                .getResultList();
    }
}
