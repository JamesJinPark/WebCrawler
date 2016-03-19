package edu.upenn.cis455.xpathengine;

import java.util.ArrayList;

/**
 * @author James Park
 *
 * @param <Token>
 */
@SuppressWarnings("hiding")
public class Tree<Token>{
	private String value;
	private ArrayList<Token> children;
	
	/**
	 * Constructs a Tree with given value in the root node,
	 * having the given children.
	 * @param value
	 * @param children
	 */
	public Tree(String value){
		this.value = value;
		this.children = new ArrayList<Token>();
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	/**
	 * Adds the child as the new index'th child of this Tree;
	 * subsequent nodes are "moved over" as necessary to make 
	 * room for the new child
	 * @param index
	 * @param child
	 */
	public void addChild(int index, Token child){
		children.add(index, child);
	}
	
	/**
	 * Adds the child as the new last child of this node
	 * @param index
	 * @param child
	 */
	public void addChild(Token child){
		children.add(child);
	}
	
	/**
	 * Returns the number of children that this node has
	 * @return
	 */
	public int getNumberOfChildren(){
		return children.size();
	}
	
	/**
	 * Returns the index'th child of this node
	 * @param index
	 * @return
	 */
	public Token getChild(int index){
		return children.get(index);
	}
	
	/**
	 * Removes the index'th child of this node
	 * @param index
	 */
	public void removeChild(int index){
		children.remove(index);
	}

		
	/**
	 * Searches this Tree for a node that is == to node, and
	 * returns true if found. 
	 * @return true if node is found
	 */
	boolean contains(Token node){
		for(Token child : children){
			if(child.equals(node)) return true;
		}
		return false;
	}
	
	/* 
	 * Returns a one-line string representing this tree
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		if(children.size() == 0){
			return value.toString();
		}
		String result = value + "(" + children.get(0);
		for(int i = 1; i< children.size(); i++){
			result += ", " + children.get(i);
		}
		return result + ")";
	}
	
	
	/**
    * Tests whether the input argument is a Tree having the same shape
    * and containing the same values as this Tree.
    * 
    * @param obj The object to be compared to this Tree.
    * @return <code>true</code> if the object is equals to this Tree,
    *         <code>false</code> otherwise.
    */
   @Override
   public boolean equals(Object obj) {
       if (!(obj instanceof Tree)) return false;
       Tree<?> that = (Tree<?>) obj;
       if (!equals(this.value, that.value)) return false;
       if (!equals(this.getNumberOfChildren(),
                   that.getNumberOfChildren())) return false;
       for (int i = 0; i < getNumberOfChildren(); i++) {
           if (!(this.getChild(i).equals(that.getChild(i)))) return false;
       }
       return true;
   }
   
   /**
    * Tests whether two values are equal (either == or <code>equals(obj)</code>),
    * when one or both values may be <code>null</code>.
    * 
    * @param object1 The first object to be tested.
    * @param object2 The second object to be tested.
    * @return <code>true</code> iff the objects are equal.
    */
   private boolean equals(Object object1, Object object2) {
       if (object1 == object2) return true;
       if (object1 == null) return false;
       return object1.equals(object2);
   }

}
