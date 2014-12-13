package com.javafiddle.core.ejb;

import com.javafiddle.core.ejb.util.PackageNameUtility;
import com.javafiddle.core.jpa.JFClass;
import com.javafiddle.core.jpa.JFPackage;
import com.javafiddle.core.jpa.JFProject;
import com.javafiddle.core.jpa.User;
import java.io.File;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Provides us with global methods for handling projects.
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
    
    @EJB
    private UserBean userBean;
    
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
        String className;
        String classType;
        if (clazz.getClassName().lastIndexOf(".") > 0) {
            className = clazz.getClassName().substring(0, clazz.getClassName().lastIndexOf("."));
            classType = clazz.getClassName().substring(clazz.getClassName().lastIndexOf(".")+1);
        }
        else {
            className = clazz.getClassName();
            classType = "";
        }
        sb.append(className).append("\", \"type\": \"").append(classType).append("\"");
        return sb.toString();
    }
    /**
     * Returns the relative path for the class mentioned in arguments.
     * @param classId
     * @return 
     */
    public String getPathForClass(Long classId) {
        JFClass clazz = jfclassBean.getClassById(classId);
        JFPackage pack = clazz.getJFPackage();
        JFProject proj = pack.getJFProject();
        User user = proj.getUser();
        String SEP = File.separator;
        String path = user.getNickname() + SEP + proj.getProjectName() + SEP 
                +"src" + SEP + "main" + SEP;
        path += PackageNameUtility.getPathFromPackage(pack.getPackageName());
        path += SEP;
        path += clazz.getClassName();
        return path;
    }
    
    public String getPathForProject(Long projectId) {
        JFProject project = jfprojectBean.getProjectById(projectId);
        User user = project.getUser();
        return user.getNickname() + File.separator + project.getProjectName();
    }
    
    public List<Long> getProjectsOfUser(Long userId) {
        return em.createQuery("select p.id from JFProject p where p.user =:user")
                .setParameter("user", em.find(User.class, userId))
                .getResultList();
    }
    public List<String> getNamesOfProjectsOfUser(Long userId) {
        return em.createQuery("select p.projectName from JFProject p where p.user =:user")
                .setParameter("user", em.find(User.class, userId))
                .getResultList();
    }
    
    public void generateData() {
        String className, content;
        for (Object packageId: em.createQuery("select p.id from JFPackage p")
                .getResultList()) {
            for (int i = 1; i <= 3; i++) {
                className = "my_class_" + Integer.toString(i) + ".java";
                content = "that is the content of class " + i + " in package " + packageId;
                jfclassBean.addClass(className,content,(Long)packageId,(new Date()).getTime());
            }
        }
    }
    public String getExampleTree() {
        return this.projectsOfUserToJSON(1L);
    }
}
