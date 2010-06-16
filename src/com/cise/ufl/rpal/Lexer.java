package com.cise.ufl.rpal;

import java.io.*;

class Lexer {
        private StringBuilder contents = new StringBuilder();

        public void readFile (String fileName) {
                System.out.println ("---------------------------\n");
                System.out.println ("Input file: " + fileName);
                System.out.println ("---------------------------\n");
                try {
                        BufferedReader br = new BufferedReader (new FileReader(fileName));
                        String line = null;
                        while (( line = br.readLine()) != null){
                                 contents.append(line);
                                 contents.append("\n");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                System.out.println ("File Contents: " + contents);
        }


        public static void main(String args[]) {
                Lexer lexer = new Lexer ();
                lexer.readFile (args[0]);
        }
}