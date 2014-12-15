package com.javafiddle.maven;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Dependency {
    private Element groupId;
    private Element artifactId;
    private Element packaging;
    private Element version;
    private Element name;
    private Element url;
    private Element type;
    private Element scope;
    
    Document document;
    
    public Dependency(Document document) {
    	this.document = document;
    	 
    	groupId = document.createElement("groupId");
        artifactId = document.createElement("artifactId");
        packaging = document.createElement("packaging");
        version = document.createElement("version");
        name = document.createElement("name");
        url = document.createElement("url");
        type = document.createElement("type");
        scope = document.createElement("scope");       
    }
    
    public Element getNode() {
    	Element root = document.createElement("dependency");
    	
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
        
        if (!type.getTextContent().equals("")) {
        	root.appendChild(type);
        }
        
        if (!scope.getTextContent().equals("")) {
        	root.appendChild(scope);
        }
    	
        return root;
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
	
	public void setType(String type) {
		this.type.setTextContent(type);
	}
	
	public void setScope(String scope) {
		this.scope.setTextContent(scope);
	}
	
	
}
