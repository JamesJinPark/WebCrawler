package edu.upenn.cis455.xpathengine;

import java.util.ArrayList;
import java.util.List;

/**
 * Tokenizer for XPath engine 
 * @author James Park
 */
public class Tokenizer {
	public Tree<Token> tree;
	
	/**
	 * Constructor for tokenizer
	 * @param name
	 */
	public Tokenizer(String name){
		this.tree = new Tree<>(name);
	}
	
	/**
	 * Checks whether a XPath is valid and constructs a tree 
	 * @param xpath
	 * @return whether a XPath is valid
	 */
	public boolean isValid(String xpath){
		List<Character> list = new ArrayList<Character>();
		char[] array = xpath.toCharArray();
		for(char x : array){
			list.add(x);
		}
		if(list.isEmpty()){
			return false;
		}
		try{
			list = checkXPath(list);
		}catch(SyntaxException e){
			tree.addChild(new Token(Token.Type.ERROR, e.toString()));
			return false;
		}
		return (list.isEmpty());
	}
	
	/**
	 * XPath ::= axis step
	 * @param list
	 */
	public List<Character> checkXPath(List<Character> list){
		list = checkAxis(list);
		list = checkStep(list);
		return list;
	}
	
	/**
	 * axis ::= /
	 * @param list
	 */
	public List<Character> checkAxis(List<Character> list){
		if(list.get(0) != '/'){
			error("Missing axis in XPath!");
		}
		Token token = new Token(Token.Type.AXIS, "/");
		tree.addChild(token);
		list.remove(0);
		if(list.get(0) == '/'){
			error("Can't have two simluataneous axes in XPath!");
		}
		while(list.get(0) == ' '){
			list.remove(0);
		}
		return list;
	}
	
	/**
	 * step ::= nodename ([test])* (XPath)?
	 * @param list
	 */
	public List<Character> checkStep(List<Character> list){
		list = checkNodename(list);		
		while(!list.isEmpty() && list.get(0) == ' '){
			list.remove(0);
		}
		if(!list.isEmpty()){
			if(list.get(0) == '/'){
				list = checkXPath(list);
			} else if(list.get(0) == '['){
				list = checkTest(list);
			} else{
				error("Invalid XPath!: " + list);
			}
		}
		return list;
	}
	
	/**
	 * nodename ::= valid characters
	 * @param list
	 */
	public List<Character> checkNodename(List<Character> list){
		List<Character> charBuffer = new ArrayList<Character>();
		while(!list.isEmpty() && list.get(0) != '['){
			if(!isValidCharacter(list.get(0))){
				Token token = new Token(Token.Type.ELEMENT, buildString(charBuffer));
				tree.addChild(token);
				return list;
			}
			charBuffer.add(list.get(0));
			list.remove(0);
		}
		Token token = new Token(Token.Type.ELEMENT, buildString(charBuffer));
		tree.addChild(token);
		return list;
	}
	
	/**
	 * valid characters ::= letters | digits | periods | hyphens | underscores
	 * @param ch
	 * cannot contain spaces 
	 */
	public boolean isValidCharacter(char ch){
		if(ch == '/' || ch == '[' || ch == ']' || ch == ' ' ||ch == ':' || ch == ';' || ch == '*'){
			return false;
		}
		return true; 
	}
	
	/**
	 * test ::= step | text() = "..." | contains(text(), "...") | @attname = "..."
	 * @param list
	 */
	public List<Character> checkTest(List<Character> list){
		list.remove(0); //remove '['
		while(!list.isEmpty() && list.get(0) == ' '){
			list.remove(0);
		}
		List<Character> charBuffer = new ArrayList<Character>();					
		boolean inQuotes = false;
		
		while(inQuotes || list.get(0) != ']'){
			if(!inQuotes && list.get(0) == '['){ //TEST
				checkTest(list);  	//TEST
				break;
			}						//TEST
			charBuffer.add(list.get(0));
			if(list.get(0) == '\"'){
				if(inQuotes){
					inQuotes = false;
				} else {
					inQuotes = true;
				}
			}
			list.remove(0); 
		}
		if(!list.isEmpty()) list.remove(0); //remove ']'
		while(!list.isEmpty() && list.get(0) == ' '){
			list.remove(0); //remove whitespaces			
		}
		if(!list.isEmpty() && list.get(0) == '['){ //account for multiple tests
			checkTest(list);
		}
		if(!list.isEmpty() && list.get(0) == '/'){ //account for multiple steps
			checkXPath(list);
		}

		if(charBuffer.get(0) == '@'){//found attribute
			List<Character> stepList = checkAttribute(charBuffer);
			if(!stepList.isEmpty()){
				error("Invalid attribute in XPath: " + stepList);
			}
		} else if(charBuffer.size() > 8 && 
				(charBuffer.get(0).toString().toLowerCase() +
					charBuffer.get(1).toString().toLowerCase() +
					charBuffer.get(2).toString().toLowerCase() +
					charBuffer.get(3).toString().toLowerCase() +
					charBuffer.get(4).toString().toLowerCase() +
					charBuffer.get(5).toString().toLowerCase() +
					charBuffer.get(6).toString().toLowerCase() +
					charBuffer.get(7).toString().toLowerCase()).equals("contains")){ //found "contains(text(), "...")"			
			List<Character> stepList = checkContainsText(charBuffer);
			if(!stepList.isEmpty()){
				error("Invalid contains(text(),...) in XPath: " + stepList);
			}			
		} else if(charBuffer.size() > 6 && 
				(charBuffer.get(0).toString().toLowerCase() +
					charBuffer.get(1).toString().toLowerCase() +
					charBuffer.get(2).toString().toLowerCase() +
					charBuffer.get(3).toString().toLowerCase() +
					charBuffer.get(4).toString().toLowerCase() +
					charBuffer.get(5).toString().toLowerCase()).equals("text()")){ //found "text() = ..."
			List<Character> stepList = checkText(charBuffer);
			if(!stepList.isEmpty()){
				error("Invalid text() in XPath: " + stepList);
			}
		} else {
			List<Character> stepList = checkStep(charBuffer);
			if(!stepList.isEmpty()){
				error("Invalid step in XPath: " + stepList);
			}
		}
		return list;
	}

	/**
	 * finds attribute name 
	 * @param list
	 */
	public List<Character> checkAttribute(List<Character> list){
		list.remove(0); //remove @
		List<Character> attributeNameBuffer = new ArrayList<Character>();
		while(!list.isEmpty() && list.get(0) != ' ' && list.get(0) != '='){ //get the name of the attribute
			attributeNameBuffer.add(list.get(0));
			list.remove(0); 
		}
		if(attributeNameBuffer.size() == 0){
			error("Invalid attribute provided! " + list);
		}
		String attributeName = buildString(attributeNameBuffer); //store the attribute name		
		while(!list.isEmpty() && list.get(0) == ' '){
			list.remove(0);
		}		
		if(list.isEmpty() || list.get(0) != '='){			
			error("Missing '=' operator in attribute! " + list);			
		} 
		list.remove(0);//remove '=' operator
		while(!list.isEmpty() && list.get(0) == ' '){
			list.remove(0);
		}
		if(list.isEmpty() || list.get(0) != '\"'){			
			error("Missing '\"' operator in attribute! " + list);
		}
		list.remove(0);//remove \"
		List<Character> attributeContentBuffer = new ArrayList<Character>();
		while(!list.isEmpty() && list.get(0) != '\"'){
			attributeContentBuffer.add(list.get(0));
			list.remove(0);
		}
		if(!list.isEmpty()){
			list.remove(0); //remove \" 
		}
		String attributeContent = buildString(attributeContentBuffer); //store the attribute content
		tree.addChild(new Token(Token.Type.ATTRIBUTE, attributeName));
		tree.addChild(new Token(Token.Type.FULLTEXT, attributeContent));
		return list;
	}
	
	/**
	 * finds the contains(text(), "...")
	 * @param list
	 */
	public List<Character> checkContainsText(List<Character> list){
		for(int i = 0; i < 8; i++){
			list.remove(0);//remove contains
		}
		while(!list.isEmpty() && list.get(0) == ' '){
			list.remove(0);
		}
		if(!list.isEmpty() && list.get(0) != '(') error("Invalid format for contains.  '(' missing: " + list);
		list.remove(0);//remove (,
		if(list.size() > 6){
			if((list.get(0).toString().toLowerCase() + 
			list.get(1).toString().toLowerCase() +
			list.get(2).toString().toLowerCase() +
			list.get(3).toString().toLowerCase() +
			list.get(4).toString().toLowerCase() +
			list.get(5).toString().toLowerCase() +
			list.get(6).toString().toLowerCase()).equals("text(),")){
				for(int i = 0; i < 7; i++){
					list.remove(0);//remove text(),
				}				
			} else {
				error("Incorrect format for \"text()\" for contains(text(), ...) ");
			}
		} else {
			error("Missing \"text()\" for contains(text(), ...) ");
		}
		while(!list.isEmpty() && list.get(0) == ' '){
			list.remove(0);
		}				
		if(list.isEmpty() || list.get(0) != '\"'){			
			error("Missing '\"' operator in contains(text(), ...)! " + list);
		}
		list.remove(0);//remove \"
		List<Character> containsTextBuffer = new ArrayList<Character>();
		while(!list.isEmpty() && list.get(0) != '\"'){
			containsTextBuffer.add(list.get(0));
			list.remove(0);
		}
		if(!list.isEmpty()){
			list.remove(0); //remove \" 
		}
		if(!list.isEmpty() && list.get(0) != ')') error("Invalid format for contains.  ')' missing: " + list);
		list.remove(0);//remove ),
		tree.addChild(new Token(Token.Type.CONTAINSTEXT, buildString(containsTextBuffer)));
		return list;
	}
	
	/**
	 * finds the text(), "..."
	 * @param list
	 */
	public List<Character> checkText(List<Character> list){
		for(int i = 0; i < 6; i++){
			list.remove(0);//remove text()
		}
		while(!list.isEmpty() && list.get(0) == ' '){
			list.remove(0);
		}
		if(!list.isEmpty() && list.get(0) != '=') error("Invalid format for text().  '=' missing: " + list);
		list.remove(0);//remove '='
		while(!list.isEmpty() && list.get(0) == ' '){
			list.remove(0);
		}				
		if(list.isEmpty() || list.get(0) != '\"'){			
			error("Missing '\"' operator in text()! " + list);
		}
		list.remove(0);//remove \"
		List<Character> textBuffer = new ArrayList<Character>();
		while(!list.isEmpty() && list.get(0) != '\"'){
			textBuffer.add(list.get(0));
			list.remove(0);
		}
		if(!list.isEmpty()){
			list.remove(0); //remove \" 
		}
		tree.addChild(new Token(Token.Type.FULLTEXT, buildString(textBuffer)));
		return list;
	}
	
		
    /**
     * Utility routine to throw a <code>SyntaxException</code> with the
     * given message.
     * @param message The text to put in the <code>SyntaxException</code>.
     */
    private void error(String message) {
        throw new SyntaxException("XPath Error: " + message);
    }
    
    
    /**
     * Builds a string from the arraylist of characters
     * @param list
     * @return String
     */
    private String buildString(List<Character> list) {
    	StringBuilder string = new StringBuilder(list.size());
    	for(Character ch : list){
    		string.append(ch);
    	}
    	return string.toString();
    }

}
