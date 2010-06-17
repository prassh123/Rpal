package com.cise.ufl.rpal;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Lexer<E> {
        private StringBuilder contents = new StringBuilder();
        private ArrayList<E> tokenList = new ArrayList<E> ();
       
        Logger logger = Logger.getLogger("rpalLogger");
        

        public void readFile (String fileName) {
        		logger.info ("---------------------------\n");
                logger.info ("Input file: " + fileName);
                logger.info ("---------------------------\n");
                try {
                        BufferedReader br = new BufferedReader (new FileReader(fileName));
                        String line = null;
                        while (( line = br.readLine()) != null){
                                 contents.append(line);
                                 contents.append(" <eol> "); // the space in front of <eol> is crucial for the lexer
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                logger.info ("File Contents: " + contents);
        }
        
        public void constructTokens () {
        	logger.info("Initiating token construction...");
        	if (contents == null || contents.equals("")) {
        		logger.severe("content is empty");
        		return;
        	}
        	
        	StringTokenizer st = new StringTokenizer (contents.toString(), " ");
        	while (st.hasMoreTokens()) {
        		E token = (E) st.nextToken();
        		if (token.equals("<eol>") || token.equals ("\t")) 
        			continue;
        		else if (((String) token).startsWith("//")) {
               			while (st.nextToken().equals("<eol>") != true)
        				continue;
        		}
        		if (! ((String) token).startsWith("//"))
        			tokenList.add(token);
        	}
        	logger.info("Token List :" + tokenList );
        }
        
        private boolean isInteger (String token) {
        	Pattern p = Pattern.compile("^[0-9]*$");  // pattern to detect only the integers
        	Matcher match = p.matcher(token);
        	boolean result = match.find();
        	return result;  	
        }
        private boolean isIdentifier (String token) {
        	Pattern p = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");  // pattern to detect identifiers valid are the ones that start with _, letters and digits followed only by those. 
        	Matcher match = p.matcher(token);
        	boolean result = match.find();
        	return result;  	
        }
        
        public void getTypeOfToken (String token) {
        	if ( isInteger (token))
        		System.out.println( token + "\t" + "Integer");
        	else if (isIdentifier(token))
        		System.out.println( token + "\t" + "Identifier");
        	else 
        		System.out.println( token + "\t" + "Unknown");
        }
        
        public void printLexTable () {
        	for (E token: tokenList ) {
        		this.getTypeOfToken((String) token);
        	}
        }
        
        public static void main(String args[]) {
                Lexer lexer = new Lexer ();
                lexer.readFile (args[0]);
                lexer.constructTokens();
              //System.out.println("The result  " + lexer.isInteger("12345"));
                lexer.printLexTable();
        }
}