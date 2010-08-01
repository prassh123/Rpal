package com.cise.ufl.rpal;

import java.util.HashMap;
import java.util.Stack;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Enviroment {
	
	public int envMarker;
	private HashMap envMapping = new HashMap ();
	
	public void setEnvMapping (HashMap envMapping) {
		this.envMapping = envMapping;
	}
	
	public HashMap getEnvMapping () {
		return this.envMapping;
	}
	
}

public class CSEMachine {
	
	private static int envMarker = 0;
	private Stack stack = new Stack();
	Enviroment CE;
	
	private HashMap environmentHash = new HashMap();
	private ArrayList controlStructuresArray = new ArrayList ();
	

	public CSEMachine (ArrayList controlStructuresArray) {
		this.controlStructuresArray = controlStructuresArray;
	}
	
	public ArrayList getControlStructure (int index) {
		ArrayList controlStructure = (ArrayList) this.controlStructuresArray.get(index);
		return controlStructure;
	}
	
	private String getValueofToken (String token) {
		int beginIndex = token.indexOf(':')+1;
		if (beginIndex <= 0) 
			return token;
		return token.substring(beginIndex, token.length()-1);
	}
	
	@SuppressWarnings("unchecked")
	public void runCSEMachine () {
		try {
		int environment = 0;
		ArrayList controlStructure = getControlStructure(environment);
		controlStructure.add(0, "PE_" + environment);       //Making the start of the processing environment as PE_0
		int controlStructureLength = controlStructure.size();
		
		stack.push("PE_0");
		
		System.out.println ("About to process " + controlStructure);
		
		// get the last element from the control structure and examine if its jus an identifier or gamma
		
		while (controlStructure.size() > 0) {     // the last left out value will be the PE_0 env 
			System.out.println (controlStructure);
			System.out.println ("Stack content " + stack.toString());
			
			controlStructureLength = controlStructure.size();
			String item = (String) controlStructure.get(controlStructureLength-1);	
			controlStructure.remove(controlStructureLength-1);
			
		    System.out.println ("ITEM:  " + item);
		    
		    if (item.equals("")) {
		    	continue;
		    }
		    
		    if (item.startsWith("PE_")) {   // CSE Rule 5
		    	String value = (String) stack.pop();
		    	String envEnd = (String) stack.pop();
		    	
		    	if (item.equals(envEnd)) {
		    		System.out.println ("Lambda Closure achieved on environment " + item);
		    	}
		    	// put the value back in the stack
		    	if (item.equals("PE_0")) {
		    		System.out.println ("Execution complete. Final Value " + value);
		    	}
		    	else {
		    	    stack.push(value);
		    	}
		    	
		    }
		    
		    else if (isOperatorSymbol(item)) {
		    	String rator = (String) stack.pop();
	    		String rand = (String) stack.pop();
	    		String result = apply(rator, rand);
	    		stack.push(result);
		    }
		    
		    else if (item.startsWith("tau")) {
		    	if (item.indexOf(';') >= 0) {  
    	    	    StringTokenizer st = new StringTokenizer (item, ";");
		    	    ArrayList temp = new ArrayList ();
		    	    int numberofElements=0;
		    	
		    	    while (st.hasMoreElements()) {
		    		    String s = st.nextToken();
		    		    temp.add(s);
		    	    }
		    	   // bug: letz remove the last element.
		    	    temp.remove(temp.size()-1);
		    	    if (temp.size() > 0) {
		    	        controlStructure.addAll(temp);   // add the parsed tau values
		    	    }
		    	}
		    	else {    // RULE 9 this means there is only a single tau operator. Evaluate the _ arg and see how many elements are there. pop that many values frm the stack		
			    	int pos = item.indexOf('_')+1;
			    	int numberofElements = new Integer(item.substring(pos));
			    	StringBuffer tempStr = new StringBuffer("(");
			    	while (numberofElements > 0) {
			    		tempStr.append(stack.pop());
			    		numberofElements--;
			    	}
			    	tempStr.append(")");
			    	stack.push(tempStr.toString());
			    }
		    	
		    }
		    
		    else if (item.startsWith("<ID:")) {
		    	item = getValueofToken (item);
		    	// lookup in the current environment, get the value and push it on to the stack.
		    	System.out.println ("Looking up " + item + " in env " + (envMarker-1));
		    	HashMap currenvMapping = (HashMap) environmentHash.get("PE_" + (envMarker-1));
		    	String value = (String) currenvMapping.get(item);
		    	System.out.println ("Identifer " + item + " value pushed to stack " + value);
		    	stack.push(value);
		    }
		
		    else if (item.startsWith(STTransformer.LAMBDA)) {
		    	item = item + ":" + envMarker;
		    	System.out.println ("Pushing lambda into stack " + item);
		    	stack.push(item);
		    }
		    
		    else if (item.equals(STTransformer.GAMMA)) {
		    	String itemAtTopOfStack = (String) stack.peek();
		    	if ( ! itemAtTopOfStack.startsWith (STTransformer.LAMBDA) ) {    // if its not lambda, then apply rator to rand and push it back.
		    		String rator = (String) stack.pop();
		    		String rand = (String) stack.pop();
		    		String result = apply(rator, rand);
		    		stack.push(result);
		    	}
		    	else {
		    		// need to handle tuples...
		    		String lambdaNode = (String) stack.pop();
		    		String rand = (String) stack.pop();
		    		System.out.println ("lambda node " + lambdaNode);
		    		System.out.println ("rand node " + rand);
		    		
		    		String X = getX(lambdaNode);
		    		HashMap currEnvMapping = new HashMap ();
		    		
		    		if (X.indexOf(',') >= 0) {
		    			X = X.substring(1, X.length()-1);
		    			rand = rand.substring(1, rand.length()-1);
		    			StringTokenizer randStr = new StringTokenizer (rand);
		    			ArrayList randArray = new ArrayList ();
		    			while (randStr.hasMoreElements()) {
		    				String str = randStr.nextToken();
		    				randArray.add(getValueofToken(str));
		    			}
		    			
		    			StringTokenizer st = new StringTokenizer(X, ",");
		    			int i=0;
		    			while (st.hasMoreTokens()) {
		    				 // add the mapping
		    			    String tempStr = st.nextToken();
		    				currEnvMapping.put(tempStr, randArray.get(i));
		    				i++;
		    			}
		    			environmentHash.put("PE_"+envMarker, currEnvMapping);
		    			System.out.println ("env hash " + environmentHash);
		    		}
		    		
		    		else {
		    		    currEnvMapping.put(X, rand);
		    		    environmentHash.put("PE_"+envMarker, currEnvMapping);
		    		    System.out.println ("env hash " + environmentHash);
		    		}
		    		
		    		envMarker++;
		    		String newPE = "PE_"+envMarker;
		    		
		    		int k = getK(lambdaNode);
		    		System.out.println ("K value " + k);
		    		controlStructure.add(newPE);
		    		stack.push(newPE);
		    		
		    		ArrayList deltaK = getControlStructure (k);
		    		//System.out.println ("delta K " + deltaK);
		    		controlStructure.addAll(deltaK);    // append the ouput of deltaK
		    		//System.out.println ("after appending " + controlStructure);
		    	}
		    }
		    else {
		    	stack.push(item);
		    }
	        	    
	     }
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getK(String lambdaNode) {
		int startIndex= lambdaNode.indexOf("_")+1;
		int endIndex = lambdaNode.lastIndexOf('_');
		 
		String k = lambdaNode.substring(startIndex, endIndex);
		return new Integer(k).intValue();
	    
	}
	
	public String getX(String lambdaNode) {
		System.out.println ("got x " + lambdaNode);
		if (lambdaNode.startsWith("{")) {
			String st = lambdaNode.substring(1, lambdaNode.length()-1);
			return st;
		}
		
		int startIndex = lambdaNode.lastIndexOf('_')+1;
		int endIndex = lambdaNode.indexOf(":");
		String X = lambdaNode.substring(startIndex, endIndex);
		
		return X;
	    
	}
	
	 public boolean isOperatorSymbol(String token) {
     	Pattern p = Pattern.compile("^[-+*/<>&.@/:=~|$!#%^\\[\\]{}\"'?]$");  //  ^[-+*/<>&.@/:=~|$!#%^_\\[\\]{}\"'?]$
     	Matcher match = p.matcher(token);
     	boolean result = match.find();
		return result;
	}
	 
	 private boolean isOperatorPresent (String token) {
     	Pattern p = Pattern.compile("^[-+*%\\/][0-9]*$");  // pattern to detect only the integers
     	Matcher match = p.matcher(token);
     	boolean result = match.find();
     	return result;  	
     }
	 
	 private String getExprValue (String token) {
			int beginIndex = token.indexOf('(')+1;
			if (beginIndex <= 0) 
				return token;
			return token.substring(beginIndex, token.length()-1);
		}
	 
	public String apply (String rator, String rand) {
		if (isOperatorSymbol (rator)) {
			String result = "(" +rator + rand + ")";
			return result;
		}
		else {
			char sign='+';
		    rator = getExprValue (rator);
		    rand = getExprValue (rand);
		    
		    //extract the sign from the rator
		    if (isOperatorPresent (rator)) {
		    	sign = rator.charAt(0);
		    	rator = rator.substring(1, rator.length());
		    }
		    
		    switch (sign) {
		    case '+':
		    	int result = (new Integer (rand)).intValue() + (new Integer (rator)).intValue();
		    	return ""+result;
		    	
		    case '-':
		    	result = (new Integer (rand)).intValue() - (new Integer (rator)).intValue();
		    	return ""+result;
		    case '*':
		    	result = (new Integer (rand)).intValue() * (new Integer (rator)).intValue();
		    	return ""+result;
		    case '/':
		    	result = (new Integer (rand)).intValue() / (new Integer (rator)).intValue();
		    	return ""+result;
		    default:
		    	return "";
		    }
		   
			
		}
	}
}