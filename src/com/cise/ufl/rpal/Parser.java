package com.cise.ufl.rpal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

class TreeNode {
	String token;
	private  TreeNode leftChild = null;
	private TreeNode rightChild = null;
	
	TreeNode (String token) {
		this.token = token;
	}
	
	public void setLeftChild (TreeNode t) {
		this.leftChild = t;
	}
	
	public void setRightChild (TreeNode t) {
		this.rightChild = t;
	}
	
	public TreeNode getLeftChild () {
		return this.leftChild;
	}
	
	public TreeNode getRightChild () {
		return this.rightChild;
	}
	
	public String getTokenValue () {
		return this.token;
	}
	
}

public class Parser {
    Lexer lexer = new Lexer ();
    HashMap lexTable;
    private static String nextToken;
    private static int index = -1;
    Stack stack = new Stack ();
    ArrayList tokenList;
   
    /**
     * Constructor for the Parser
     * 
     * @param fileName
     */
    public Parser (String fileName) {
    	 lexer.readFile (fileName);
    	 lexer.constructTokens();
    	 tokenList = lexer.getTokens();
    	 lexTable = lexer.getLexTable();
    	 
    	 nextToken = getNextToken();
    }
    
   public Parser () {
	   System.out.println ("Default Constructor");
   }
    
    
    /**
     * Utility to read the tokens and to identify if it's the correct one.
     * 
     * @param token
     * @throws Exception
     */
    public void readToken (String token) throws Exception {
    	if ( ! token.equals (nextToken)) {
    		throw new Exception ("Error: Expected "+ token + "but found "+ nextToken);
    	}
    	String type = lexer.getTypeOfToken(token);
    	if (type.equalsIgnoreCase("Identifier") ||  type.equalsIgnoreCase("Integer") || type.equalsIgnoreCase("String")) {
    		Build_tree (token, 0);
    	}
    	nextToken = getNextToken ();
    }

    /**
     * Utility to build the tree. It uses a first child - next sibling approach
     * 
     * @param token
     * @param n
     */
	private void Build_tree(String token, int n) {
		TreeNode treeNode = new TreeNode (token);
		ArrayList <TreeNode>treeNodesList = new ArrayList<TreeNode>  ();
		TreeNode tempNode, lastNode, lastButOneNode;
		if (n == 0) {  // Just push the tree to the stack
			stack.push(treeNode);
		}
		else {   // We will pop 'i' trees from the stack, connect it to-gether like a first child next sibling and then push the resulting one to the stack again...
			for (int i=0; i<n ; i++) {
				//lastNode = (TreeNode) stack.pop();
				//System.out.println (lastNode.getTokenValue());
				//lastButOneNode = (TreeNode) stack.pop();
				//System.out.println (lastButOneNode.getTokenValue());
				//lastButOneNode.setRightChild(lastNode);
				//lastNode.setRightChild(treeNode);
				//stack.push(lastButOneNode);
				//stack.push(lastNode);
				
				treeNodesList .add((TreeNode) stack.pop());
				
				
			}
			Collections.reverse(treeNodesList);
			System.out.println (treeNodesList);
			for (int i=0; i<treeNodesList.size()-1; i++) {
				    treeNodesList.get(i).setRightChild(treeNodesList.get(i+1));
			}
			treeNode.setLeftChild(treeNodesList.get(0));
			stack.push(treeNode);
		}
		
	}
	
	/**\
	 * Tester for Build tree
	 * 
	 */
	public void testBuild_tree () {
		Build_tree ("let", 0);
		Build_tree("where", 0);
		Build_tree("then", 2);
		preOrderTraversal();
	}
	
	private void preOrderTraversal () {
		if (stack.empty()) {
			System.out.println ("No trees in the stack!");
		}
		TreeNode root = (TreeNode) stack.pop();
		TreeNode temp = root;
		preOrder (root);
		//preOrder (root.getRightChild());
		
		/*while ( temp.getLeftChild() != null) {
			System.out.println ("Left Value: " + temp.getTokenValue());
			temp = temp.getLeftChild();
		}*/
		// Reset temp to root
		
		
		
	}
	
	private void preOrder (TreeNode t) {
		System.out.println ("Value: " + t.getTokenValue());
		if  (t.getLeftChild() != null ) {
		    preOrder (t.getLeftChild());
		}
		else if (t.getRightChild() != null)
		    preOrder (t.getRightChild());
	}
	

	/**
	 * 
	 * Utility to get the next token from the input. Or rather the lexer table. This updates the nextToken, which is always one token ahead
	 * @return
	 */
	private String getNextToken() {
		index++;
		return (String) tokenList.get(index);
	}
	
	
	/************************************************************
	 * Procedures for the various Non terminals...
	 * 
	 ************************************************************/
	
	public void func_E () {
		System.out.println ("Next Token "+ nextToken);
		if (nextToken.equalsIgnoreCase("let")) {
			try {
				readToken("let");
				func_D ();
				readToken("in");
				func_E ();
				Build_tree("let", 2);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (nextToken.equalsIgnoreCase("fn")) {
			try {
				readToken ("fn");
				int n = 0;
				do {
					func_Vb();
					n++;
					} while (lexer.getTypeOfToken(nextToken).equals ("Identifier") || lexer.getTypeOfToken(nextToken).equals ("(") );
				readToken (".");
				func_E ();
				Build_tree ("lambda", n+1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		else 
			func_Ew ();
	}


	private void func_Ew() {
		func_T ();
		if (nextToken.equalsIgnoreCase ("where")) {
			try {
				readToken ("where");
			} catch (Exception e) {
				e.printStackTrace();
			}
			func_Dr();
			Build_tree("where", 2);
		}
		
	}


	private void func_Dr() {
		// TODO Auto-generated method stub
		if (nextToken.equalsIgnoreCase ("rec")) {
			try {
				readToken ("rec");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		func_Db();
		Build_tree("rec", 1);
	}


	private void func_Db() {
        
		if (lexer.getTypeOfToken(nextToken).equalsIgnoreCase("(")) {
			try {
				readToken ("(");
				func_D ();
				readToken (")");
				readToken (";");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		else if ( lexer.getTypeOfToken(nextToken).equalsIgnoreCase("Identifier")) {
        	int n = 0;
        	do {
				func_Vb();
				n++;
				} while (lexer.getTypeOfToken(nextToken).equals ("Identifier") || lexer.getTypeOfToken(nextToken).equals ("(") );
        	try {
				readToken ("=");
			} catch (Exception e) {
				e.printStackTrace();
			}
			func_E ();
			Build_tree("fcn_form", n+1);
        }
   	}


	private void func_T() {
		func_Ta ();
		if (lexer.getTypeOfToken(nextToken).equals ("(")) {
			try {
				int n = 0;
			do {
				readToken ("(") ;
				readToken (",") ;
				func_Ta();
				n++;
			} while (lexer.getTypeOfToken(nextToken).equals ("(") );
			Build_tree("tau", n+1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}


	private void func_Ta() {
		func_Tc ();
		
		while (nextToken.equalsIgnoreCase ("aug")) {
			try {
				readToken ("aug");
			} catch (Exception e) {
				e.printStackTrace();
			}
			func_Tc();
			Build_tree ("aug", 2);
		}  
		
	}


	private void func_Tc() {
		func_B ();
		try {
		if (nextToken.equalsIgnoreCase("->")) {
			
			readToken ("->");
			fn_Tc();
			readToken ("|");
		    fn_Tc();
		    Build_tree ("->", 3);
		}
		else 
			readToken (";");
		} catch (Exception e) {
			e.printStackTrace();
		}
 	}


	private void func_B() {
		try {
        func_Bt ();
        if (nextToken.equalsIgnoreCase("or")) {
        	readToken ("or");
        	func_Bt ();
        	Build_tree ("or", 2);
        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void func_Bt() {
		try {
	        func_Bs ();
	        if (nextToken.equalsIgnoreCase("&")) {
	        	readToken ("&");
	        	func_Bs ();
	        	Build_tree ("&", 2);
	        }
			} catch (Exception e) {
				e.printStackTrace();
			}
	}


	private void func_Bs() {
		if (nextToken.equalsIgnoreCase("not")) {
			try {
				readToken ("not");
				func_Bp ();
				Build_tree ("not", 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		else
			func_Bp();
		}


	private void func_Bp() {
		
		fn_A ();
		try {
		if (nextToken.equalsIgnoreCase("eq")) {
			readToken ("eq");
			fn_A ();
			Build_tree ("eq", 2);
		}
		else if (nextToken.equalsIgnoreCase("ne")) {
			readToken ("ne");
			fn_A ();
			Build_tree ("ne", 2);		
		}
		
		else if (nextToken.equalsIgnoreCase("(")) {
			readToken ("(");
			String temp = nextToken;
			if (temp.equalsIgnoreCase("gr") || temp.equalsIgnoreCase(">")) {
				 readToken (temp);
				 readToken (")");
				 fn_A ();
				 Build_tree("gr", 2);
			}
			else if (temp.equalsIgnoreCase("ge") || temp.equalsIgnoreCase(">=")) {
				 readToken (temp);
				 readToken (")");
				 fn_A ();
				 Build_tree("ge", 2);
			}
			else if (temp.equalsIgnoreCase("ls") || temp.equalsIgnoreCase("<")) {
				 readToken (temp);
				 readToken (")");
				 fn_A ();
				 Build_tree("ls", 2);
			}
			else if (temp.equalsIgnoreCase("le") || temp.equalsIgnoreCase(">")) {
				 readToken (temp);
				 readToken (")");
				 fn_A ();
				 Build_tree("le", 2);
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void fn_A() {
		// TODO Auto-generated method stub
		
	}


	


	private void fn_Tc() {
		// TODO Auto-generated method stub
		
	}


	private void func_Vb() {
		// TODO Auto-generated method stub
		
	}


	private void func_D() {
		
		
	}
	
	public static void main (String args[]) {
		Parser p = new Parser ();
		p.testBuild_tree();
		
	}
	
     
}
