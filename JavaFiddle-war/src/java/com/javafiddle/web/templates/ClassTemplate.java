package com.javafiddle.web.templates;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClassTemplate {
    private String value;
    private String name;
    private String type;
    //private String projectName;
    
    //projectName здесь несущественнен
    public ClassTemplate(String name, String type, String packname
    		//, String projectName
    		) {
        this.name = name;
        this.type = type;
        //this.projectName = projectName;
        
        value = "";
        if (!packname.startsWith("!"))
            value += "package " + packname + ";\n\n";
        if (type.equals("JUnit")) {
            value += getJUnitImport();
        }
        value += getCommentsBlock();
        value += getClassDefinition();
    }
    
    private String getJUnitImport(){
        String def = "";
        def += "import junit.framework.Test;\n";
        def += "import junit.framework.TestCase;\n";
        def += "import junit.framework.TestSuite;\n\n";
        return def;
    }
    
    private String getClassDefinition() {
        String def = "public ";
        
        switch (type) {
            case "class": case "exception": case "runnable": case "JUnit":
                def += "class";
                break;
            case "enum":
                def += "enum";
                break;
            case "interface":
                def += "interface";
                break;
            case "annotation":
                def += "@interface";
                break;           
            default:
                break;
        }
        
        def += " " + name + " ";
        
        if(type.equals("exception")) {
            def += "extends Exception ";
        } else if (type.equals("JUnit")) {
            def += "extends TestCase ";
        }
        
        def += "{\n\t\n";
        
        if(type.equals("exception")) {
            def += getExceptionContent();
        } else if(type.equals("runnable")) {
            def += getRunnableContent();
        } else if(type.equals("JUnit")) {
            def += getTestContent();
        }
        
        def += "}";
        
        return def;
    }
    
    private String getExceptionContent() {
        String exccontent = "\t/**\n";
        exccontent += "\t * Creates a new instance of\n";
        exccontent += "\t * <code>NewException</code> without detail message.\n";
        exccontent += "\t */\n";
        
        exccontent += "\tpublic " + name + " {\n";
        exccontent += "\n";
        exccontent += "\t}\n";
        exccontent += "\t/**\n";
        exccontent += "\t * Creates a new instance of\n";
        exccontent += "\t * <code>NewException</code> with the specified detail message.\n";
        exccontent += "\t *\n";
        exccontent += "\t * @param msg the detail message\n";
        exccontent += "\t */\n";
        exccontent += "\tpublic " + name + "(String msg) {\n";
        exccontent += "\t\tsuper(msg);\n";
        exccontent += "\t}\n";
                
        return exccontent;
    }
    
     private String getRunnableContent() {
        String exccontent = "\t/**\n";
        exccontent += "\t * @param args the command line arguments\n";
        exccontent += "\t */\n";
        
        exccontent += "\tpublic static void main(String[] args) {\n";
        exccontent += "\t\tSystem.out.println(\"Hello, World!\");\n";
        exccontent += "\t}\n";
                
        return exccontent;
    }
     
     private String getTestContent(){
         String testcontent = "\t/**\n";
         testcontent += "\t * Create the test case\n"
                 + "\t *\n"
                 + "\t * @param testName name of the test case\n"
                 + "\t */\n"
                 + "\tpublic AppTest( String testName ) {\n"
                 + "\t\tsuper( testName );\n"
                 + "\t}\n\n";
         testcontent += "\t/**\n"
                 + "\t * @return the suite of tests being tested\n"
                 + "\t */\n"
                 + "\tpublic static Test suite() {\n"
                 + "\t\treturn new TestSuite( AppTest.class );\n"
                 + "\t}\n\n";
         testcontent += "\t/**\n"
                 + "\t * Simple test\n"
                 + "\t */\n"
                 + "\tpublic void testApp() {\n"
                 + "\t\tassertTrue( true );\n"
                 + "\t}\n";         
         return testcontent;
     }
    
    private String getCommentsBlock() {
        String comments;
        
        comments = "/**\n";
        comments += " * Author: guest\n";
        //comments += " * Project: " + projectName + "\n";
        comments += " * Time: " + newDateTime() + "\n";
        comments += " */\n";
        
        return comments;
    }
    
    public String getValue() {
        return value;
    }
    private String newDateTime() {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date datetime = new Date();
        return df.format(datetime);
    }
}
