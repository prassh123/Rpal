package com.cise.ufl.rpal;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class STTransformer extends Parser {
	
	private String rpalFileName = "";
	private static final String GAMMA = "gamma";
	private static final String LAMBDA = "lambda";
	
	Parser p = null;
	
	
	public void convertLet (TreeNode node) {
		TreeNode X = null;
		TreeNode E = null;
		TreeNode P = null;
		
		TreeNode gammaNode = new TreeNode(GAMMA);
		TreeNode lambdaNode = new TreeNode (LAMBDA);
		
		
		
		
		System.out.println ("\n\nIncoming node to convertLet " + node.getTokenValue());
		if ( ! node.getTokenValue().equals("let") ) {
			System.out.println ("Expected Let statement");
			return;
		}
		//System.out.println ("Setting P Node to be " + node.getRightChild().getTokenValue());
		
		if ( node.getLeftChild().getTokenValue().equals("=")) {
			System.out.println ("= node detected");
			System.out.println ("Setting P node to be " + node.getLeftChild().getRightChild().getTokenValue());
			P = node.getLeftChild().getRightChild();
			
			System.out.println ("setting X to be "  + node.getLeftChild().getLeftChild().getTokenValue());
			X = node.getLeftChild().getLeftChild();
			System.out.println ("setting E to be "  + node.getLeftChild().getLeftChild().getRightChild().getTokenValue());
			E = node.getLeftChild().getLeftChild().getRightChild();
		}
		
		
		lambdaNode.setLeftChild(X);
		X.setRightChild(P);
		lambdaNode.setRightChild(E);
		gammaNode.setLeftChild(lambdaNode);
		
		preOrder(gammaNode, 0);
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
				//System.exit(0);
	         }
		
	}
	
	public void constructST () {
		TreeNode rootTreeNode = p.getRootTreeNode();
		convertLet (rootTreeNode);
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