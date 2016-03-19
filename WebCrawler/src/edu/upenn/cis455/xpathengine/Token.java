package edu.upenn.cis455.xpathengine;

/**
 * Token class for tokenizer and XPath Engine
 * @author James Park
 */
public class Token {
	static enum Type{AXIS, ELEMENT, ATTRIBUTE, CONTAINSTEXT, FULLTEXT, ERROR}
	
	final Type type; 
	final String value;
	
	/**
	 * Constructor for tokens
	 * @param type
	 * @param value
	 */
	public Token(Type type, String value){
		this.type = type;
		this.value = value;
	}
	
	public Token.Type getType(){
		return type;
	}
	
	public String getValue(){
		return value; 
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Token){
			Token that = (Token) o;
			return this.type == that.type && this.value.equals(that.value);
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return value.hashCode();
	}
	
	@Override
	public String toString(){
		return type + ": " + value;
	}
}
