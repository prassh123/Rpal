package com.cise.ufl.rpal;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import java.util.ArrayList;

public class STTransformer extends Parser {
	
	private String rpalFileName = "";
	private static final String GAMMA = "gamma";
	private static final String LAMBDA = "lambda";
	
	Parser p = null;
	
	
	public void convertLet (TreeNode node) {
		TreeNode X = null;
		TreeNode E = null;
		TreeNode P = null;
		
		//TreeNode gammaNode = new TreeNode(GAMMA);
		TreeNode lambdaNode = new TreeNode (LAMBDA);
		
		System.out.println ("\n\nIncoming node to convertLet " + node.getTokenValue());
		if ( ! node.getTokenValue().equals("let") ) {
			System.out.println ("Expected Let statement");
			return;
		}
		try {
		node.setTokenValue(GAMMA);
	
		if ( node.getLeftChild().getTokenValue().equals("=")) {
			
			P = node.getLeftChild().getRightChild();	
			//System.out.println ("P Value "+ P.getTokenValue());
			X = node.getLeftChild().getLeftChild();		
			//System.out.println ("X Value "+ X.getTokenValue());
			E = node.getLeftChild().getLeftChild().getRightChild();
			//System.out.println ("E Value "+ E.getTokenValue());
		}
		node.setLeftChild(lambdaNode);
		lambdaNode.setRightChild(E);   // This would be the transformed lambda node.
		lambdaNode.setLeftChild(X);
		X.setRightChild(P);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		//preOrder(gammaNode, 0);
	}
	
	public void convertWhere (TreeNode node) {
		TreeNode X = null;
		TreeNode E = null;
		TreeNode P = null;
		
		//TreeNode gammaNode = new TreeNode(GAMMA);  //Transforming the input Where node to gamma node.
		TreeNode lambdaNode = new TreeNode (LAMBDA);
		System.out.println ("\n\nIncoming node to convertWhere " + node.getTokenValue());
		if ( ! node.getTokenValue().equals("where") ) {
			System.out.println ("Expected where statement");
			return;
		}
			node.setTokenValue(GAMMA);
			
			
		if ( node.getLeftChild().getRightChild().getTokenValue().equals("=")) {
			//System.out.println ("= node in where detected");
			//System.out.println ("Setting P node to be " + node.getLeftChild().getTokenValue());
			P = node.getLeftChild();
        	//System.out.println ("setting X to be "  + node.getLeftChild().getRightChild().getLeftChild().getTokenValue());
			X = node.getLeftChild().getRightChild().getLeftChild();
			//System.out.println ("setting E to be "  + node.getLeftChild().getRightChild().getLeftChild().getRightChild().getTokenValue());
			E =  node.getLeftChild().getRightChild().getLeftChild().getRightChild();
		
		
		
	     
		
		node.setLeftChild(lambdaNode);	
		
    	node.setRightChild  (null);  // null the right child of the incoming where node
    	
    	P.setLeftChild(null);
    	P.setRightChild(null);
    	X.setLeftChild(null);
    	X.setRightChild(P);
    	
    	lambdaNode.setLeftChild(X);
    	
    	lambdaNode.setRightChild(E);
    	
    	System.out.println ("----------------\n");
    	System.out.println ("----where------\n");
    	preOrder(node, 0);
   
		}
		
	}
	
	public void convertWithin (TreeNode node) {
		TreeNode X1 = null;	
		TreeNode E1 = null;
		TreeNode X2 = null;
		TreeNode E2 = null;
		//TreeNode equalsNode = new TreeNode("=");
		
		TreeNode gammaNode = new TreeNode(GAMMA);
		TreeNode lambdaNode = new TreeNode (LAMBDA);
		
		
		
		
		System.out.println ("\n\nIncoming node to convertWithin  " + node.getTokenValue());
		if ( ! node.getTokenValue().equals("within") ) {
			System.out.println ("Expected within statement");
			return;
		}
		
		node.setTokenValue("=");
		//node.setRightChild(null);   // This might be a blunder
		//System.out.println ("Setting P Node to be " + node.getRightChild().getTokenValue());
		 System.out.println ("Left child " + node.getLeftChild().getTokenValue() + "\n");
		 System.out.println ("Right child " + node.getLeftChild().getRightChild().getTokenValue());
		 
		if ( node.getLeftChild().getTokenValue().equals("=") && node.getLeftChild().getRightChild().getTokenValue().equals("=")) {
			System.out.println ("came here" );
			System.out.println ("Setting X1 node to be " + node.getLeftChild().getLeftChild().getTokenValue());
			X1 = node.getLeftChild().getLeftChild();
			
			System.out.println ("Setting E1 node to be " + node.getLeftChild().getLeftChild().getRightChild().getTokenValue());
			E1 = node.getLeftChild().getLeftChild().getRightChild();
			
			System.out.println ("Setting X2 node to be " + node.getLeftChild().getRightChild().getLeftChild().getTokenValue());
			X2 = node.getLeftChild().getRightChild().getLeftChild();
			
			System.out.println ("Setting E2 node to be " + node.getLeftChild().getRightChild().getLeftChild().getRightChild().getTokenValue());
			E2 =  node.getLeftChild().getRightChild().getLeftChild().getRightChild();
			
		
			node.setLeftChild(X2);
		    X2.setRightChild(gammaNode);
		    X2.setLeftChild(null);
		    gammaNode.setLeftChild(lambdaNode);
		    gammaNode.setRightChild(null);
		    lambdaNode.setRightChild(E1);
		    lambdaNode.setLeftChild(X1);
		    X1.setRightChild(E2);
		    X1.setLeftChild(null);
		
		   // preOrder(node, 0);
		}
	}
	
	public void convertRec (TreeNode node) {
		TreeNode X = null;
		TreeNode E = null;
	
		//TreeNode equalsNode = new TreeNode("=");
		TreeNode ystar = new TreeNode("Y*");
		
		
		TreeNode gammaNode = new TreeNode(GAMMA);
		TreeNode lambdaNode = new TreeNode (LAMBDA);
		
	
		System.out.println ("\n\nIncoming node to convertRec  " + node.getTokenValue());
		if ( ! node.getTokenValue().equals("rec") ) {
			System.out.println ("Expected rec statement");
			return;
		}
		node.setTokenValue("=");
		System.out.println ("Node rec" + node.getLeftChild().getTokenValue());
		if ( node.getLeftChild().getTokenValue().equals("=")) {
			
			System.out.println ("Setting X node to be " + node.getLeftChild().getLeftChild().getTokenValue());
			X = node.getLeftChild().getLeftChild();
			TreeNode newX = new TreeNode(X);
			
			System.out.println ("Setting E node to be " + node.getLeftChild().getLeftChild().getRightChild().getTokenValue());
			E = node.getLeftChild().getLeftChild().getRightChild();
			
			node.setLeftChild(X);
		    X.setRightChild(gammaNode);
		    X.setLeftChild(null);
		    gammaNode.setLeftChild(ystar);
		    
		    ystar.setRightChild(lambdaNode);
		    lambdaNode.setLeftChild(X);
		    lambdaNode.setLeftChild(newX);
		    newX.setRightChild(E);
		
		   // preOrder(node, 0);
		}
	}
	
	public void convertFcnForm (TreeNode node) {
		
        TreeNode P = null;
        ArrayList<TreeNode> V = new ArrayList<TreeNode> (); 
		TreeNode E = null;
	
		TreeNode equalsNode = new TreeNode("=");
		TreeNode gammaNode = new TreeNode(GAMMA);
		TreeNode lambdaNode = new TreeNode (LAMBDA) ;
	
		System.out.println ("\n\nIncoming node to convertFcn_Form  " + node.getTokenValue());
		if ( ! node.getTokenValue().equals("fcn_form") ) {
			System.out.println ("Expected fcn_form statement");
			return;
		}
		
			System.out.println ("Setting P node to be " + node.getLeftChild().getTokenValue());
			P = node.getLeftChild();
			TreeNode PCopy = P;
			
			while (PCopy.getRightChild() != null) {
				V.add(PCopy.getRightChild());
				PCopy = PCopy.getRightChild();      
			}
			E = PCopy; // The last node should be E
			
			equalsNode.setLeftChild(P);
			P.setRightChild(lambdaNode);
			lambdaNode = new TreeNode (LAMBDA);
			
		
			for (int i=0; i < V.size(); i++) {
				lambdaNode.setLeftChild(V.get(i));         // I belive we only set up to the number of V's. However lets double check.
				lambdaNode = new TreeNode (LAMBDA);
				V.get(i).setRightChild(lambdaNode);
				
			}
			
			V.get(V.size()-1).setRightChild(E);
		   // preOrder(node, 0);
		
	}
	
	public void convertAnd (TreeNode node) {
		
		ArrayList <TreeNode> X = new ArrayList<TreeNode> ();
		ArrayList <TreeNode> E = new ArrayList<TreeNode> ();
	
		TreeNode equalsNode = new TreeNode("=");
		
		TreeNode gammaNode = new TreeNode(GAMMA);
		TreeNode lambdaNode = new TreeNode (LAMBDA);
		TreeNode commaNode = new TreeNode (",");
		TreeNode tauNode = new TreeNode("tau");
	
		System.out.println ("\n\nIncoming node to convertAnd  " + node.getTokenValue());
		if ( ! node.getTokenValue().equals("and") ) {
			System.out.println ("Expected and statement");
			return;
		}
		
		if ( node.getLeftChild().equals("=")) {
			
			TreeNode equalNode = node.getLeftChild();
			TreeNode equalNodeCopy = equalNode;
			while (equalNodeCopy.getRightChild() != null) {
				X.add(equalNodeCopy.getLeftChild());
				E.add(equalNodeCopy.getRightChild());
				equalNodeCopy = equalNodeCopy.getRightChild();
			}
			
			equalsNode.setLeftChild(commaNode);
			commaNode.setRightChild(tauNode);
			
			commaNode.setLeftChild(X.get(0));
			tauNode.setLeftChild(E.get(0));
			
			for (int i=0; i<X.size()-1; i++) {
				X.get(i).setRightChild(X.get(i+1));
			}
			
			for (int i=0; i<E.size()-1; i++) {
				E.get(i).setRightChild(E.get(i+1));
			}
		   
		  //  preOrder(node, 0);
		}
	}
	
	public void convertAtTheRateOf (TreeNode node) {
		TreeNode E1 = null;
		TreeNode N = null;
		TreeNode E2 = null;
		
		TreeNode gammaNode1 = new TreeNode(GAMMA);
		TreeNode gammaNode2 = new TreeNode(GAMMA);
		
		System.out.println ("\n\nIncoming node to convertAtTheRateOf " + node.getTokenValue());
		if ( ! node.getTokenValue().equals("@") ) {
			System.out.println ("Expected @ statement");
			return;
		}
		E1 = node.getLeftChild();
		N = E1.getRightChild();
		E2 = N.getRightChild();
		
		gammaNode1.setLeftChild(gammaNode2);
		gammaNode2.setRightChild(E2);
		gammaNode2.setLeftChild(N);
		N.setRightChild(E1);
		
		//preOrder(gammaNode1, 0);
	}
	
    private void postOrder (TreeNode t) {	
    	if  (t.getLeftChild() != null ) {
		    postOrder (t.getLeftChild());
		}
	    if (t.getRightChild() != null) {  
		    postOrder (t.getRightChild());
	    }
	    System.out.println(t.getTokenValue());
	    reduceConstruct (t);
	}
    
    private void reduceConstruct (TreeNode t) {
    	String nodeValue = t.getTokenValue();
    	
    	if (nodeValue.equals("let")) {
    		convertLet(t);
    	}
    	else if (nodeValue.equals("where")) {
    		convertWhere (t);
    	}
    	else if (nodeValue.equals("within")) {
    		convertWithin (t);
    	}
    	else if (nodeValue.equals("rec")) {
    		convertRec(t);
    	}
    	else if (nodeValue.equals("fcn_form")) {
    		convertFcnForm(t);
    	}
    	else if (nodeValue.equals ("and")) {
    		convertAnd(t);
    	}
    	else if (nodeValue.equals ("@")) {
    		convertAtTheRateOf(t);
    	}
    	else 
    		return;
    }
	
	public void constructAST (String rpalFileName) {
		this.rpalFileName = rpalFileName;
		p = new Parser (rpalFileName);
		try {
			p.fn_E();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (nextToken.equalsIgnoreCase("PARSE_COMPLETE")) {
			System.out.println ("*******PARSING COMPLETE*****");
			System.out.println ("-------TREE VALUES----------");
			p.preOrderTraversal ();
	    }
		
	}
	
	public void constructST () {
        System.out.println ("------POST ORDER TRAVERSAL-------\n");   
		
		/* IMP. We will need to invoke getRootTreeNode using the instance of the parent class. 
		 * The below call is going to traverse the tree recursively in a post order fashion and reduce it to a 
		 * partial standard tree.*/
		
		this.postOrder(p.getRootTreeNode());     
		
		//Print it out in a pre-order way
		p.preOrder(p.getRootTreeNode(), 0);
	}
	
	public static void main (String args[]) {
		// create Options object
		Options options = new Options();
		// add t option
		options.addOption("ast", true, "rpal test file name");
		options.addOption("noout", false, "No output computation");
	
		CommandLineParser parser = new PosixParser();
		STTransformer subtreeTransformer = null;
		try {
			CommandLine cmd = parser.parse( options, args);
			 if (cmd.hasOption("l")) {
                 System.out.println ("USAGE: ./p1 [-ast][-noout] <testfile>");
                 return;
                 }
			
			if (cmd.hasOption("ast")) {
				String rpalFileName = cmd.getOptionValue("ast");
				subtreeTransformer = new STTransformer();
				subtreeTransformer.constructAST(rpalFileName);
				
			}
            if (! cmd.hasOption("noout")) {
            	
            	subtreeTransformer.constructST();
			}
		} catch (Exception e1) {
			System.out.println (e1.getMessage());
		}
	}
	
}