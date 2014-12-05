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
    private String sep;
    private Git git;
    public GitHandler(String name, String email, String pathToFolder) {
        this.name = name;
        this.email = email;
        this.sep = System.getProperty("file.separator");
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try {
            this.rep = builder.setGitDir(new File(pathToFolder+sep+".git"))
                    .readEnvironment()
                    .findGitDir()
                    .build();
            this.git = new Git(this.rep);
        } catch (IOException ex) {
            Logger.getLogger(GitHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void initializeRepo(String pathToFolder) throws IOException {
        rep.create();
    }
    /**
     * Adds the specified file to the repo.
     * @param pathToFile MUST BE A RELATIVE PATH!
     */
    public void addFileToRepo(String pathToFile) {
        AddCommand add = this.git.add();
        add.addFilepattern(pathToFile);
        try {
            add.call();
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
            return commitRes.abbreviate(10).name(); //So that is the hash.
        } catch (GitAPIException ex) {
            Logger.getLogger(GitHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            return null;
        }
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
}
