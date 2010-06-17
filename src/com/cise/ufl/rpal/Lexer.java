package com.cise.ufl.rpal;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;

class Lexer {
        private StringBuilder contents = new StringBuilder();
        private ArrayList tokenList = new ArrayList ();
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
        		String token = st.nextToken();
        		if (token.equals("<eol>") || token.equals ("\t")) 
        			continue;
        		else if (token.startsWith("//")) {
               			while (st.nextToken().equals("<eol>") != true)
        				continue;
        		}
        		if (! token.startsWith("//"))
        			tokenList.add(token);
        	}
        	logger.info("Token List :" + tokenList );
        }

        
        public static void main(String args[]) {
                Lexer lexer = new Lexer ();
                lexer.readFile (args[0]);
                lexer.constructTokens();
        }
}