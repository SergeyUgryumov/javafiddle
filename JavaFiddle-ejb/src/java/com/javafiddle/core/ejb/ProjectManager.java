package com.javafiddle.core.ejb;

import com.javafiddle.core.jpa.JFClass;
import com.javafiddle.core.jpa.JFPackage;
import com.javafiddle.core.jpa.JFProject;
import com.javafiddle.core.jpa.User;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Provides us with global methods for handling project tree.
 * @author roman
 */

@Named(value = "projectManager")
@Stateless
public class ProjectManager {
    @EJB
    private JFPackageBean jfpackageBean;

    @EJB
    private JFProjectBean jfprojectBean;
    
    @EJB
    private JFClassBean jfclassBean;
    
    @PersistenceContext
    private EntityManager em;

    public String projectsOfUserToJSON(Long userId) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"projects\":[");
        for (Long projId: this.getProjectsOfUser(userId)) {
            sb.append("{");
            sb.append(this.projectTreeToJSON(projId));
            sb.append("},");
        }
        if (!this.getProjectsOfUser(userId).isEmpty()) {
            sb.delete(sb.length()-1, sb.length());
        }
        sb.append("]}");
        return sb.toString();
    }
    /**
     * Returns project's tree in JSON format.
     * @param projectId
     * @return 
     */
    public String projectTreeToJSON(Long projectId) {
        StringBuilder sb = new StringBuilder();
        JFProject proj = jfprojectBean.getProjectById(projectId);
        if (proj == null) return null;
        sb.append("\"id\": ").append(proj.getId()).append(", \"name\": \"");
        sb.append(proj.getProjectName()).append("\", \"packages\": [");
        for (Long packId: jfprojectBean.getPackagesOfProject(projectId)) {
            sb.append("{");
            sb.append(this.packageToJSON(packId));
            sb.append("},");
        }
        if (!jfprojectBean.getPackagesOfProject(projectId).isEmpty()) {
            sb.delete(sb.length() - 1, sb.length());
        }
        sb.append("]");
        return sb.toString();
    }
    public String packageToJSON(Long packageId) {
        JFPackage pack = jfpackageBean.getPackageById(packageId);
        if (pack == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append("\"id\":").append(pack.getId()).append(", \"name\": \"");
        sb.append(pack.getPackageName()).append("\", \"parentId\": \"");
        sb.append(pack.getJFProject().getId()).append("\", \"files\": [");
        for (Long classId: jfpackageBean.getClassesOfPackage(packageId)) {
            sb.append("{");
            sb.append(this.classToJSON(classId));
            sb.append("},");
        }
        if (!jfpackageBean.getClassesOfPackage(packageId).isEmpty()) {
            sb.delete(sb.length() - 1, sb.length());
        }
        sb.append("]");
        return sb.toString();
    }
    public String classToJSON(Long classId) {
        JFClass clazz = jfclassBean.getClassById(classId);
        if (clazz == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append("\"id\": ").append(clazz.getId()).append(", \"name\": \"");
        String className = clazz.getClassName().substring(0, clazz.getClassName().lastIndexOf("."));
        String classType = clazz.getClassName().substring(clazz.getClassName().lastIndexOf(".")+1);
        sb.append(className).append("\", \"type\": \"").append(classType).append("\"");
        return sb.toString();
    }
    
    public String getPathForClass(Long classId) {
        return null;
    }
    
    public List<Long> getProjectsOfUser(Long userId) {
        return em.createQuery("select p.id from JFProject p where p.user =:user")
                .setParameter("user", em.find(User.class, userId))
                .getResultList();
    }
}
