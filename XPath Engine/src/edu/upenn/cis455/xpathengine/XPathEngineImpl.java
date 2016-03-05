package edu.upenn.cis455.xpathengine;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of the XPath Engine Interface
 * @author James Park
 *
 */
public class XPathEngineImpl implements XPathEngine {
  String[] xpaths = null;
  Tokenizer[] tokenizers = null;
  boolean[] xpathsExistInXML = null;
  
  /**
   * Constructor for XPathEngine Implementation 
   */
  public XPathEngineImpl() {// Do NOT add arguments to the constructor!!
  }
	
  public void setXPaths(String[] s) {
	  this.xpaths = s;	  //Stores the XPath expressions that are given to this method 
	  this.tokenizers = new Tokenizer[xpaths.length];
	  this.xpathsExistInXML = new boolean[xpaths.length];
	  for(int i = 0; i< xpaths.length; i++){
		  this.tokenizers[i] = new Tokenizer(xpaths[i]);
		  this.xpathsExistInXML[i] = false;
	  }
  }

  public boolean isValid(int i) {
	if(this.xpaths != null && i < xpaths.length){
		return this.tokenizers[i].isValid(xpaths[i]);
	}
    return false;
  }
	
  public boolean[] evaluate(Document d) { 
	for(int i = 0; i < xpathsExistInXML.length; i++){
		if(this.isValid(i)){
			//traverse d using xpaths[i]
			int isSuccess = traverse(d, tokenizers[i]);
			if(tokenizers[i].tree.getNumberOfChildren() == 0 && isSuccess == 0){ //end node is found
				xpathsExistInXML[i] = true;	
			}
		}
	}
    return this.xpathsExistInXML; 
  }       

  /**
   * Traverses the DOM document using the tokenized XPath as a guide.  
   * @param root of DOM document
   * @param tokenizer
   * @return int that gives the status of the traversal: 0 if successful, -1 if failed
   */
public int traverse(Node d, Tokenizer tokenizer){;
  	  int status = 0;
  	  if(tokenizer.tree.getNumberOfChildren() == 0){
  		  return status;
  	  }
      NodeList children = d.getChildNodes();
      Token currentToken = tokenizer.tree.getChild(0);
      System.out.println("TEST current token: " + currentToken.toString());
      System.out.println("TEST current node: " + d.getNodeName());
      switch(currentToken.getType()){//static enum Type{AXIS, ELEMENT, ATTRIBUTE, CONTAINSTEXT, FULLTEXT, ERROR}
	      case AXIS:			tokenizer.tree.removeChild(0);
	      						status = status + traverse(d, tokenizer);
	    	  					break;
	      case ELEMENT:			tokenizer.tree.removeChild(0);
	    	  					String targetName = currentToken.getValue();
	    	  					boolean foundTarget = false;
	      						for (int iChild = 0; iChild < children.getLength(); iChild++ ) {
	      							Node node = children.item(iChild);
      								if(targetName.equals(node.getNodeName())){
	      								status = status + traverse(node, tokenizer);
	      								foundTarget = true;
	      								break;
	      							}
	      						}
	      						if(foundTarget){
		    	  					break;	      							
	      						}else{
	      							return -1;
	      						}
	      case ATTRIBUTE:		String targetAttributeName = currentToken.getValue();
	    	  					tokenizer.tree.removeChild(0);
	    	  					Token nextToken = null;
    	  						String targetAttributeValue = null;
	    	  					if(tokenizer.tree.getNumberOfChildren() != 0){
	    	  						nextToken = tokenizer.tree.getChild(0);
	    	  						targetAttributeValue = nextToken.getValue();
		    	  					tokenizer.tree.removeChild(0);
	    	  					}
								boolean foundTargetAttributeName = false;
								boolean foundTargetAttributeValue = false;
						        if(d.getAttributes().getLength() != 0){
						        	for(int j = 0; j < d.getAttributes().getLength(); j++ ){
						        		if(d.getAttributes().item(j).getNodeName().equals(targetAttributeName) &&
						        			d.getAttributes().item(j).getNodeValue().equals(targetAttributeValue)){
						        			status = status + traverse(d, tokenizer);
						        			foundTargetAttributeName = true;
						        			foundTargetAttributeValue = true;
						        			break;
						        		}
						        	}
						        }
								if(foundTargetAttributeName && foundTargetAttributeValue){
									break;	      							
								}else {
									return -1;
								}
	      case CONTAINSTEXT:	String targetContainsText = currentToken.getValue();
								tokenizer.tree.removeChild(0);
								boolean foundTargetContainsText = false;
	      						System.out.println("TEST target text: "+ targetContainsText);
								for (int iChild = 0; iChild < children.getLength(); iChild++ ) {
									Node node = children.item(iChild);
									if(node.getNodeName().equals("#text")){
										System.out.println("Found #text!");
										System.out.println(node.getNodeValue());
										if(node.getNodeValue().contains(targetContainsText)){
	      									status = status + traverse(node.getParentNode(), tokenizer);
											foundTargetContainsText = true;
											break;	      										
										}
									}
								}
								if(foundTargetContainsText){
									break;	      							
								}else {
									return -1;
								}
	      case FULLTEXT:		String targetText = currentToken.getValue();
	    	  					tokenizer.tree.removeChild(0);
	    	  					boolean foundTargetText = false;
	      						System.out.println("TEST target text: "+ targetText);
	    	  					for (int iChild = 0; iChild < children.getLength(); iChild++ ) {
	      							Node node = children.item(iChild);
	      							if(node.getNodeName().equals("#text")){
	      								System.out.println("Found #text");
	      								System.out.println(node.getNodeValue());
	      								if(node.getNodeValue().equals(targetText)){
	      									status = status + traverse(node.getParentNode(), tokenizer);
	    	      							foundTargetText = true;
	    	      							break;
	      								}
	      							}	      							
	      						}
								if(foundTargetText){
									break;	      							
								}else {
									return -1;
								}
	      case ERROR:			//should not be possible to get here
	    	  					return -1;
	      default: 				//should not be possible to get here
	    	  					return -1;
      }
      System.out.print(status);
	  return status;
  }
}