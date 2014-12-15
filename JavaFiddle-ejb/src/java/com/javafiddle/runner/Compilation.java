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
 * Currently javac is used for compilation. I suppose we will replace it with maven.
 * Also I'm not sure if the code is cross-platform, because there is some text 
 * requiring the program being run on a Unix-system. It is commented, but anyway 
 * it is better to be double-checked.
 * 
 */
public class Compilation implements Launcher, Serializable {

    private String pathtoProject = "";
    private Process process;
    private Boolean killed = false;
    private Queue<String> stream = null;
    private Integer pid = null;
    private int exitValue;

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
                PrintStream ps = new PrintStream(out);//PrintStream(out);
                InvocationOutputHandler oh = new PrintStreamHandler(ps, true);
                PipedInputStream in = new PipedInputStream();
                out.connect(in);
                                
		Invoker invoker = new DefaultInvoker();                
		invoker.setMavenHome(new File("C:\\apache-maven-3.2.3"));//???
                invoker.setOutputHandler(oh);
                
                Execute execute = new Execute(invoker, request); 
                execute.start();
                printLines(" stdout:", in);     
                
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
        while ((line = in.readLine()) != null) {
            stream.add(name + " " + line);
        }
    }

    @Override
    public String getOutputStream() {
        return stream.poll();
    }
    
    @Override
    public OutputStream getInputStream() {
        return process.getOutputStream();
    }
    
    @Override
    public InputStream getErrorStream() {
        return process.getErrorStream();
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
    
    private void setPid(Process process) {
 //       if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
            try {
                Class cl = process.getClass();
                Field field = cl.getDeclaredField("pid");
                field.setAccessible(true);
                Object pidObject = field.get(process);
                pid = (Integer) pidObject;
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
                Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
            }
   /*     } else {
            throw new IllegalArgumentException("Needs to be a UNIXProcess");
        }
  */  }

    @Override
    public void addToOutput(String line) {
        stream.add(line);
    }
}
