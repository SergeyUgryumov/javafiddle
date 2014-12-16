package com.javafiddle.runner;

import com.javafiddle.core.ejb.ProjectManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.apache.maven.shared.invoker.PrintStreamHandler;
/**
 * Compilation is a class responsible for handling compilation process. This 
 * includes the compilation itself, all exceptional situations and all output
 * of the compiler. <br/>
 * 
 */
public class Compilation implements Launcher, Serializable {

    private String pathtoProject = "";
    private Process process;
    private Boolean killed = false;
    private Queue<String> stream = null;
    private Integer pid = null;
    private int exitValue;
    private InputStream is;

    public Compilation(String pathtoProject) {
        this.stream = new LinkedList<>();
        this.pathtoProject = pathtoProject;
        exitValue = -1;
    }
    
    public class Execute extends Thread {
        private Invoker invoker;
        private InvocationRequest request;        
                
        public Execute(Invoker invoker, InvocationRequest request){
            this.invoker = invoker;
            this.request = request;
            is = request.getInputStream(null);
        }
        
        @Override
        public void run(){
            InvocationResult result;
            try {
                result = invoker.execute( request );
                exitValue = result.getExitCode(); 
            } catch (MavenInvocationException ex) {
                Logger.getLogger(Compilation.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
    }

    @Override
    public void run() {
        try {
            InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(new File(pathtoProject + File.separator + "pom.xml"));
                
		//Вместо compile можно указать:
		//	test
		//	Тестирование с помощью JUnit тестов 
		//	package
		//	Создание .jar файла или war, ear в зависимости от типа проекта 
		//	integration-test
		//	Запуск интеграционных тестов 
		//	install
		//	Копирование .jar (war , ear) в локальный репозиторий 
		//	deploy
		//	публикация файла в удалённый репозиторий
                             
		request.setGoals(Arrays.asList( "clean", "compile" ));

                PipedOutputStream out = new PipedOutputStream();
                PrintStream ps = new PrintStream(out);
                InvocationOutputHandler oh = new PrintStreamHandler(ps, true);
                PipedInputStream in = new PipedInputStream();
                out.connect(in);
                                
		Invoker invoker = new DefaultInvoker();                
		invoker.setMavenHome(new File("C:\\apache-maven-3.2.3"));//???
                invoker.setOutputHandler(oh);
                
                Execute execute = new Execute(invoker, request); 
                execute.start();
                printLines("", in);     
                
                waitFor();
                out.close();
                in.close();
                
        } catch (IOException ex) {
                Logger.getLogger(Compilation.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    @Override
    public void destroy() {
        if(process != null) {
            process.destroy();
            killed = true;
        }
    }

    @Override
    public void printLines(String name, InputStream ins) throws IOException {
        String line = null;
        BufferedReader in = new BufferedReader(
            new InputStreamReader(ins));            
        while (((line = in.readLine()) != null) && (exitValue == -1)) {
            stream.add(name + " " + line);
        }
    }

    @Override
    public String getOutputStream() {
        return stream.poll();
    }
    
    @Override
    public OutputStream getInputStream() {
        return null;
    }
    
    @Override
    public InputStream getErrorStream() {
        return null;
    }

    @Override
    public Boolean streamIsEmpty() {
        return stream.isEmpty();
    }

    @Override
    public int getExitCode() {
        return exitValue;
    }

    @Override
    public int waitFor() {
            while(exitValue == -1){};
            return 0;
    }

    @Override
    public Integer getPid() {
        return pid;
    }
    

    @Override
    public void addToOutput(String line) {
        stream.add(line);
    }
}
