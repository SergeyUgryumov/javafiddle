package com.javafiddle.maven;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PomCreator {
	private DocumentBuilder docBuilder;
	private Document document;
	private Transformer transformer;
	File file;
	
    private Element modelVersion;
    private Element groupId;
    private Element artifactId;
    private Element packaging;
    private Element version;
    private Element name;
    private Element url;
    
    private Dependency dep;            
    
    public PomCreator(String projectPath) {
    	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    	file = new File(projectPath + File.separator + "pom.xml");
    	file.getParentFile().mkdirs();
		try {
			docBuilder = docFactory.newDocumentBuilder();
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
			file.createNewFile();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		document = docBuilder.newDocument();
		
        modelVersion = document.createElement("modelVersion");
        modelVersion.setTextContent("4.0.0");
        
        groupId = document.createElement("groupId");
        groupId.setTextContent("com.myfirstproject.web");
        
        artifactId = document.createElement("artifactId");
        artifactId.setTextContent("MyFirstProject");
        
        packaging = document.createElement("packaging");
        packaging.setTextContent("jar");
        
        version = document.createElement("version");
        version.setTextContent("1.0-SNAPSHOT");
        
        name = document.createElement("name");
        name.setTextContent("MyFirstProject");
        
        url = document.createElement("url");
        url.setTextContent("http://maven.apache.org");
        
        dep = new Dependency(document);
    }
    
    
	public File createFile() {    	
    	String NS_URL = "http://maven.apache.org/POM/4.0.0";

    	Element root = document.createElementNS(NS_URL, "project");
    	root.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", 
    	    "xsi:schemaLocation", NS_URL + " http://maven.apache.org/maven-v4_0_0.xsd");
    	document.appendChild(root);
    	
		if (!modelVersion.getTextContent().equals("")) {
        	root.appendChild(modelVersion);
        }
        
        if (!groupId.getTextContent().equals("")) {
        	root.appendChild(groupId);
        }
        
        if (!artifactId.getTextContent().equals("")) {
        	root.appendChild(artifactId);
        }
        
        if (!packaging.getTextContent().equals("")) {
        	root.appendChild(packaging);
        }
        
        if (!version.getTextContent().equals("")) {
        	root.appendChild(version);
        }
        
        if (!name.getTextContent().equals("")) {
        	root.appendChild(name);
        }
        
        if (!url.getTextContent().equals("")) {
        	root.appendChild(url);
        }
		
        Element dependencies = document.createElement("dependencies");
        root.appendChild(dependencies);
        
        dep.setGroupId("junit");
        dep.setArtifactId("junit");
        dep.setVersion("3.8.1");
        dep.setScope("test");
        
        dependencies.appendChild(dep.getNode());
				
		DOMSource source = new DOMSource(document);
		Result fileResult = new StreamResult(file);
		
		try {
			transformer.transform(source, fileResult);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
		return file;		
	}
	
	public void setModelVersion(String modelVersion) {
		this.modelVersion.setTextContent(modelVersion);
	}

	public void setGroupId(String groupId) {
		this.groupId.setTextContent(groupId);
	}

	public void setArtifactId(String artifactId) {
		this.artifactId.setTextContent(artifactId);
	}

	public void setPackaging(String packaging) {
		this.packaging.setTextContent(packaging);
	}

	public void setVersion(String version) {
		this.version.setTextContent(version);
	}

	public void setName(String name) {
		this.name.setTextContent(name);
	}

	public void setUrl(String url) {
		this.url.setTextContent(url);
	}      

}