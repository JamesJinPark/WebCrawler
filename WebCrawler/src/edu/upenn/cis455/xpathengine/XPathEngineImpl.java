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
			boolean isSuccess = traverse(d, tokenizers[i]);
			System.out.println("This is success: " + isSuccess);
			if(tokenizers[i].tree.getNumberOfChildren() == 0 && isSuccess){ //end node is found
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
public boolean traverse(Node d, Tokenizer tokenizer){;
  	  if(tokenizer.tree.getNumberOfChildren() == 0){
  		  return true;
  	  }
      NodeList children = d.getChildNodes();
  	  Token currentToken = tokenizer.tree.getChild(0);
      switch(currentToken.getType()){//static enum Type{AXIS, ELEMENT, ATTRIBUTE, CONTAINSTEXT, FULLTEXT, ERROR}
	      case AXIS:			tokenizer.tree.removeChild(0);
	      						return traverse(d, tokenizer);
	      case ELEMENT:			String targetName = currentToken.getValue();
	      						for (int iChild = 0; iChild < children.getLength(); iChild++ ) {
	      							Node node = children.item(iChild);
	      							System.out.println(node.getNodeName());
	      							if(targetName.equals(node.getNodeName())){
      									
      									Token token = tokenizer.tree.getChild(0);
      		    	  					tokenizer.tree.removeChild(0);

      		    	  					if(traverse(node, tokenizer)){
		      								return true;	      									
	      								}
      		    	  					tokenizer.tree.addChild(0, token);	      							
      								}
      	  						}
	      						break;
	      case ATTRIBUTE:		String targetAttributeName = currentToken.getValue();
	    	  					Token nextToken = null;
    	  						String targetAttributeValue = null;
	    	  					tokenizer.tree.removeChild(0);
	    	  					if(tokenizer.tree.getNumberOfChildren() != 0){
	    	  						nextToken = tokenizer.tree.getChild(0);
	    	  						targetAttributeValue = nextToken.getValue();	    	  						
		    	  					tokenizer.tree.removeChild(0);
	    	  					}
	    	  						    	  					
						        if(d.getAttributes().getLength() != 0){
						        	for(int j = 0; j < d.getAttributes().getLength(); j++ ){
						        		if(d.getAttributes().item(j).getNodeName().equals(targetAttributeName) &&
						        			d.getAttributes().item(j).getNodeValue().equals(targetAttributeValue)){						        			
						        			if(traverse(d, tokenizer)){
						        				return true;
						        			}
						        		}
						        	}
						        }
						        break;
	      case CONTAINSTEXT:	String targetContainsText = currentToken.getValue();
								for (int jChild = 0; jChild < children.getLength(); jChild++ ) {
									Node node = children.item(jChild);
									if(node.getNodeName().equals("#text")){
										if(node.getNodeValue().contains(targetContainsText)){
											Token token = tokenizer.tree.getChild(0);
											tokenizer.tree.removeChild(0);

	      									if(traverse(node.getParentNode(), tokenizer)){
												return true;	
	      									}
	      		    	  					tokenizer.tree.addChild(0, token);	      							
										}
									}
								}
								break;
	      case FULLTEXT:		String targetText = currentToken.getValue();
	    	  					for (int iChild = 0; iChild < children.getLength(); iChild++ ) {
	      							Node node = children.item(iChild);
	      							if(node.getNodeName().equals("#text")){
	      								if(node.getNodeValue().equals(targetText)){
	      									
	      									Token token = tokenizer.tree.getChild(0);
	      									tokenizer.tree.removeChild(0);
	      		    	  					
	      									if(traverse(node.getParentNode(), tokenizer)){
		    	      							return true;
	      									}
	      									
	      		    	  					tokenizer.tree.addChild(0, token);	      							
	      								}
	      							}	      							
	      						}
	    	  					break;
	      case ERROR:			//should not be possible to get here
	    	  					return false;
	      default: 				//should not be possible to get here
	    	  					return false;
      }
	  return false;
  }
}