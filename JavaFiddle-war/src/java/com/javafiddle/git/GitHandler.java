package com.javafiddle.git;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.RevertCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * Class for handling all operations with git.<br/>
 * Replaces the revisions system which was used in the project initially.
 * @author roman
 */
public class GitHandler {
    private String name, email;
    private Repository rep;
    private final String sep = File.separator;
    private final String prefix = System.getProperty("user.home") + File.separator + "javafiddle_data"
            + File.separator;
    private Git git;
    public GitHandler(String name, String email, String pathToFolder) { //pathToFolder - relative
        this.name = name;
        this.email = email;
        String fullPath = this.prefix + pathToFolder;
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try { 
            this.rep = builder.setGitDir(new File(fullPath+sep+".git"))
                    .readEnvironment()
                    .findGitDir()
                    .build();
            if (!GitHandler.repoIsInitialized(fullPath)) {
                try{
                    rep.create();
                }
                catch(Exception e) {
                    System.out.println("Repo exists.");
                }
            }
            this.git = new Git(this.rep);
        } catch (IOException ex) {
            Logger.getLogger(GitHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Adds the specified file or folder to the repository.
     * @param pathToObject MUST BE A RELATIVE PATH!
     */
    public void addFileToRepo(String pathToObject) {
        AddCommand add = this.git.add();
        System.out.println("GitHandler(62): adding file to repo: " + pathToObject);
        add.addFilepattern("src");
        add.addFilepattern(pathToObject);
        try {
            DirCache cache = add.call();
        } catch (GitAPIException ex) {
            Logger.getLogger(GitHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Commits current state of the project.
     * @param message
     * @return 
     */
    public String commit(String message) {
        CommitCommand commit = git.commit();
        commit.setMessage(message);
        commit.setCommitter(this.name, this.email);
        try {
            RevCommit commitRes = commit.call();
            System.out.println("GitHandler: commit(72): " + commitRes.getFullMessage());
            System.out.println("GitHandler: commit(73): " + commitRes.abbreviate(10).name());
            return commitRes.abbreviate(10).name(); //So that is the hash.
        } catch (GitAPIException ex) {
            System.out.println("GitHandler: commit: 74: " + ex);
        }
        return null;
    }
    /**
     * Map of times and SHA-1 hashes.
     * @return 
     */
    public Map<Integer, String> getCommitsMap() throws IOException, GitAPIException {
        HashMap<Integer,String> commitsMap = new HashMap();
        LogCommand log = this.git.log();
        for (RevCommit rev: log.all().call()) {
            commitsMap.put(rev.getCommitTime(), rev.abbreviate(10).name());
        }
        return commitsMap;
    }
    public static boolean repoIsInitialized(String pathToProject) {
        System.out.println("Repo \"" + pathToProject + File.separator + ".git" + "\" exists:" +  
                new File(pathToProject + File.separator + ".git").exists());
        return (new File(pathToProject + File.separator + ".git")).exists();
    }
}
