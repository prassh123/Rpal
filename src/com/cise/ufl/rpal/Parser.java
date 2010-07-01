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
   // HashMap lexTable;
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
    	// lexTable = lexer.getLexTable();
    	 
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
	
	public void fn_E () throws Exception {
		//System.out.println ("Next Token "+ nextToken);
		if (nextToken.equalsIgnoreCase("let")) {
			try {
				readToken("let");
				fn_D ();
				readToken("in");
				fn_E ();
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
					fn_Vb();
					n++;
					} while (lexer.getTypeOfToken(nextToken).equals ("Identifier") || lexer.getTypeOfToken(nextToken).equals ("(") );
				readToken (".");
				fn_E ();
				Build_tree ("lambda", n+1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		else 
			fn_Ew ();
	}


	private void fn_Ew() throws Exception {
		fn_T ();
		if (nextToken.equalsIgnoreCase ("where")) {
			readToken ("where");		
			fn_Dr();
			Build_tree("where", 2);
		}
		
	}


	private void fn_Dr() throws Exception {
		
		if (nextToken.equalsIgnoreCase ("rec")) {
				readToken ("rec");
		}
		Build_tree("rec", 1);
		fn_Db();
		
	}


	private void fn_Db() throws Exception {
        
		if (lexer.getTypeOfToken(nextToken).equalsIgnoreCase("(")) {
			try {
				readToken ("(");
				fn_D ();
				readToken (")");
				readToken (";");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		else if ( lexer.getTypeOfToken(nextToken).equalsIgnoreCase("Identifier")) {
        	int n = 0;
        	do {
				fn_Vb();
				n++;
				} while (lexer.getTypeOfToken(nextToken).equals ("Identifier") || lexer.getTypeOfToken(nextToken).equals ("(") );
        	try {
				readToken ("=");
			} catch (Exception e) {
				e.printStackTrace();
			}
			fn_E ();
			Build_tree("fcn_form", n+1);
        }
   	}


	private void fn_T() throws Exception {
		fn_Ta ();
		if (lexer.getTypeOfToken(nextToken).equals ("(")) {		
				int n = 0;
			do {
				readToken ("(") ;
				readToken (",") ;
				fn_Ta();
				readToken (")") ;
				n++;
			} while (lexer.getTypeOfToken(nextToken).equals ("(") );
			Build_tree("tau", n);
		}
		else 
			readToken (";");
	}


	private void fn_Ta() throws Exception {
		fn_Tc ();
		
		if (nextToken.equalsIgnoreCase (";")) {
			readToken (";");
		}
		else {
		    while (nextToken.equalsIgnoreCase ("aug")) {
			    readToken ("aug");
			    fn_Tc();
			    Build_tree ("aug", 2);
		    }  
		}
	}


	private void fn_Tc() throws Exception {
		fn_B ();
		if (nextToken.equalsIgnoreCase("->")) {		
			readToken ("->");
			fn_Tc();
			readToken ("|");
		    fn_Tc();
		    Build_tree ("->", 3);
		}
		else 
			readToken (";");
 	}


	private void fn_B() throws Exception {
        fn_Bt ();
        if (nextToken.equalsIgnoreCase("or")) {
        	while(nextToken.equalsIgnoreCase("or")) {
        	    readToken ("or");
        	    fn_Bt ();
        	    Build_tree ("or", 2);
        	}
        }
        else
        	readToken (";");
	}


	private void fn_Bt() throws Exception {
	        fn_Bs ();
	        if (nextToken.equalsIgnoreCase("&")) {
	        	while(nextToken.equalsIgnoreCase("&")) {
	        	    readToken ("&");
	        	    fn_Bs ();
	        	    Build_tree ("&", 2);
	        	}
	        }
	        else
	        	readToken (";");       
	}


	private void fn_Bs() throws Exception {
		if (nextToken.equalsIgnoreCase("not")) {
			readToken ("not");
			fn_Bp ();
			Build_tree ("not", 1);
		}
		else {
			fn_Bp();
			readToken (";");    
		}
		}


	private void fn_Bp() throws Exception {
		
		fn_A ();
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
		
		else  {
	
			String temp = nextToken;
			if (temp.equalsIgnoreCase("gr") || temp.equalsIgnoreCase(">")) {
				 readToken (temp);
				 fn_A ();
				 Build_tree("gr", 2);
			}
			else if (temp.equalsIgnoreCase("ge") || temp.equalsIgnoreCase(">=")) {
				 readToken (temp);
				 fn_A ();
				 Build_tree("ge", 2);
			}
			else if (temp.equalsIgnoreCase("ls") || temp.equalsIgnoreCase("<")) {
				 readToken (temp);
				 fn_A ();
				 Build_tree("ls", 2);
			}
			else if (temp.equalsIgnoreCase("le") || temp.equalsIgnoreCase(">")) {
				 readToken (temp);
				 fn_A ();
				 Build_tree("le", 2);
			}
		}
	}


	private void fn_A() throws Exception {
		if (nextToken.equalsIgnoreCase("+")) {
			readToken ("+");
			fn_At ();
		}
		else if (nextToken.equalsIgnoreCase("-")) {
			readToken ("+");
			fn_At ();
			Build_tree ("neg", 1);
		}
		else {
		    fn_At ();
		    readToken (";");
		}
		while (nextToken.equalsIgnoreCase("+") || nextToken.equalsIgnoreCase("-")  ) {
			if (nextToken.equalsIgnoreCase("+")) {
				readToken ("+");
				fn_At ();
				Build_tree ("+", 2);
			}
			else {
				readToken ("-");
				fn_At ();
				Build_tree ("-", 2);
			}
		 // nextToken = getNextToken(); I dont think this is necessary...
		}
	}


	


	private void fn_At() throws Exception {
		fn_Af();
		while (nextToken.equalsIgnoreCase("*") || nextToken.equalsIgnoreCase("/")  ) {
			if (nextToken.equalsIgnoreCase("*")) {
				readToken ("*");
				fn_Af();
				Build_tree ("*", 2);
			}
			else {
				readToken ("/");
				fn_Af ();
				Build_tree ("/", 2);
			}
		//	nextToken = getNextToken();
		}
		readToken (";");
		
	}

	private void fn_Af() throws Exception {
		fn_Ap();
		if (nextToken.equalsIgnoreCase("**")) {
			readToken ("**");
			fn_Af();
			Build_tree ("**", 2);
		}
		readToken (";");
	}

	private void fn_Ap() throws Exception {
	    fn_R();
	    if (nextToken.equalsIgnoreCase("**")) {
	        while (nextToken.equalsIgnoreCase("@")) {
	    	    readToken ("@");
	    	    readToken (nextToken);
	    	    fn_R();
	    	    Build_tree ("@", 3);
	        }
	    }
	    readToken (";");	
	}

	private void fn_R() throws Exception {
		fn_Rn ();
		int n=0;
		while (lexer.getTypeOfToken(nextToken).equalsIgnoreCase("Identifier")) {
			readToken (nextToken);
            fn_Rn ();	
            n++;
   		}
		Build_tree ("gamma", n);
	}

	private void fn_Rn() throws Exception {
		/*if (lexer.getTypeOfToken(nextToken).equalsIgnoreCase("Identifier") 
				|| lexer.getTypeOfToken(nextToken).equalsIgnoreCase("Integer") 
				|| lexer.getTypeOfToken(nextToken).equalsIgnoreCase("String")) */
					readToken (nextToken);        // should be Id or Int or String at this point..
	    if (nextToken.equalsIgnoreCase("True")) {
			readToken ("True");
			Build_tree ("True", 1);
		}
		else if (nextToken.equalsIgnoreCase("False")) {
			readToken ("False");
			Build_tree ("False", 1);
		}
		else if (nextToken.equalsIgnoreCase("nil")) {
			readToken ("nil");
			Build_tree ("nil", 1);
		}
		else if (nextToken.equalsIgnoreCase("(")) {
			readToken ("(");
			fn_E();
			readToken (")");
		}
		else {
			readToken ("dummy");
			Build_tree ("dummy",1);
		}
		
	}



	private void fn_Vb() throws Exception {
		/*if (lexer.getTypeOfToken(nextToken).equalsIgnoreCase("Identifier")) {
			readToken (nextToken);
		}*/
		
			if (nextToken.equalsIgnoreCase("(")) {
				readToken ("(");
				if (nextToken.equalsIgnoreCase(")")) {
					Build_tree ("()", 2);
				}
				else {
					fn_V1 ();
					readToken (")");
				}
			}
		
			else {
				readToken (nextToken);
			}
		
	}


	private void fn_V1() throws Exception {
		int n=0;
		while (lexer.getTypeOfToken(nextToken).equals("Identifier")) {
			readToken (nextToken);
			n++;
		}
		Build_tree (",",n);
	}

	private void fn_D() {
		
		
	}
	
	public static void main (String args[]) {
		Parser p = new Parser ();
		p.testBuild_tree();
		
	}
	
     
}
