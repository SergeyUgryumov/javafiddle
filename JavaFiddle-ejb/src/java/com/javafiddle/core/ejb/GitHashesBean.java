package com.javafiddle.core.ejb;

import com.javafiddle.core.jpa.Hashes;
import com.javafiddle.core.jpa.JFProject;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Interface for interaction with database hashes.
 * @author roman
 */
@Stateless
@LocalBean
public class GitHashesBean {
    @PersistenceContext
    EntityManager em;

    @EJB
    JFProjectBean projBean;
    
    public void addHash(Long projectId, Long time, String hash) {
        Hashes newHash = new Hashes();
        newHash.setCommitTime(time);
        newHash.setJFProject(projBean.getProjectById(projectId));
        newHash.setProjectHash(hash);
        em.persist(newHash);
        
    }
    public String getLastHash(Long projectId) {
        List<String> list = em.createQuery("select h.projectHash from Hash h "
                + "where h.jfproject =:project order by h.commitTime desc")
                .setParameter("project",projectId)
                .getResultList();
        return list.get(0);
    }
    
    /**
     * Returns the project ID with the specified hash.
     * @param hash
     * @return 
     */
    public Long getProjectIdByHash(String hash) {
        return (Long) em.createQuery("select h.jfproject "
                + "from Hash h where h.projectHash =:hash")
                .setParameter("hash", hash)
                .getResultList()
                .get(0);
    }
}
