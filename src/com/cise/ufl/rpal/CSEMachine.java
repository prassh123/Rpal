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
			item = item.trim();
			item = item.replaceAll(";", "");    // hack for tuples
			controlStructure.remove(controlStructureLength-1);
			
		    System.out.println ("ITEM:  " + item);
		    
		    if (item.equals("")) {
		    	continue;
		    }
		    else if (item.equals("neg")) {
		    	String rand = (String) stack.pop();
		    	rand = getValueofToken (rand);
		    	rand = "-" + rand;
		    	stack.push(rand);
		    	continue;
		    }
		    else if (item.equals("eq")) {
		    	String rand1 = (String) stack.pop();
		    	String rand2 = (String) stack.pop();
		    	
		    	Integer i1 = new Integer (getValueofToken(rand1));
		    	Integer i2 = new Integer (getValueofToken(rand2));
		    	
		    	if (i1 < i2) {
		    		stack.push("1");
		    	}
		    	else {
		    		stack.push("0");
		    	}
		    	continue;
		    }
		    else if (item.equals("ls")) {
    			String rator = (String) stack.pop();
    			String rand = (String) stack.pop();
    			
    			Integer i1 = new Integer( getValueofToken(rator) );
    			Integer i2 = new Integer( getValueofToken(rand) );
    			
    			if (i1 < i2 ) {
    				stack.push("1");
    			}
    			else {
    				stack.push("0");
    			}
    			continue;
    		}
		    else if (item.equals ("or")) {
		    	Integer i1 = (Integer) stack.pop();
		    	Integer i2 = (Integer) stack.pop();
		    	
		    	int result = i1 | i2;
		    	stack.push(""+result);
		    	continue;
		    }
		    else if (item.startsWith("Beta:")) {
		    	item = item.replaceAll("Beta:", "Beta ");   // note the space after beta...
		    	if (item.indexOf(" ") >=0) {
		    	    StringTokenizer st = new StringTokenizer(item, " ");
		    	    while (st.hasMoreTokens()) {
		    	       controlStructure.add(st.nextToken());
		    	    }
		    	}
		    	else {
		    		String result = (String) stack.pop();   // at this point, the stack should only have a 1 or 0
		    		if (result.equals("1")) {
		    			removeDelta(controlStructure, "deltaelse:");
		    		}
		    		else {
		    			removeDelta(controlStructure, "deltathen:");
		    		}
		    	}
		    	/*else {
		    		item = item.replaceAll("Beta:", "");
		    		if (item.equals("eq")) {
		    			String rator = (String) stack.pop();
		    			String rand = (String) stack.pop();
		    			
		    			if (getValueofToken(rator).equals(getValueofToken(rand))) {
		    				removeDelta(controlStructure, "deltaelse:");
		    			}
		    			else {
		    				removeDelta(controlStructure, "deltathen:");
		    			}
		    			
		    		}
		    		else if (item.equals("ls")) {
		    			String rator = (String) stack.pop();
		    			String rand = (String) stack.pop();
		    			
		    			Integer i1 = new Integer( getValueofToken(rator) );
		    			Integer i2 = new Integer( getValueofToken(rand) );
		    			
		    			if (i1 < i2 ) {
		    				removeDelta(controlStructure, "deltaelse:");
		    			}
		    			else {
		    				removeDelta(controlStructure, "deltathen:");
		    			}
		    		}
		    		else if (item.equals("or")) {
		    			String rator = (String) stack.pop();
		    			String rand = (String) stack.pop();
		    			
		    			
		    		}
		    	}*/
		    	continue;
		    }
		    else if (item.startsWith("deltaelse:")) {
		    	item = item.replaceAll("deltaelse:", "");
		    	StringTokenizer st = new StringTokenizer (item, " ");
		    	while (st.hasMoreTokens()) {
		    		controlStructure.add(st.nextToken());
		    	}
		    	continue;
		    }
		    else if (item.startsWith("deltathen:")) {
		    	item = item.replaceAll("deltathen:", "");
		    	StringTokenizer st = new StringTokenizer (item, " ");
		    	while (st.hasMoreTokens()) {
		    		controlStructure.add(st.nextToken());
		    	}
		    	continue;
		    }
		    else if (item.indexOf(" ")>=0 && !isString(getValueofToken(item)) ) {
		    	System.out.println ("Maybe i caught a tuple" );
		    	StringTokenizer st = new StringTokenizer (item, " ");
		    	while (st.hasMoreTokens()) {
			    	controlStructure.add(st.nextToken());
		    	}
		    	continue;
		    }
		    
		    else if (item.startsWith("PE_")) {   // CSE Rule 5
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
	    		String result = apply(item, rator, rand);
	    		stack.push(result);
		    }
		    
		    else if (item.startsWith("tau")) {
		    	if (item.indexOf(' ') >= 0) {  
    	    	    StringTokenizer st = new StringTokenizer (item, " ");
		    	    ArrayList temp = new ArrayList ();
		    	    
		    	
		    	    while (st.hasMoreElements()) {
		    		    String s = st.nextToken();
		    		        s= s.replace(';', ',');
		    		  //  if (s.indexOf(' ')>=0) {
		    		    	s=s.replaceAll("\\s", ",");    // in case the tuples are separated by spaces.
		    		  //  }
		    		    temp.add(s);
		    	    }
		    	   // bug: letz remove the last element.
		    	   if (temp.get(temp.size()-1) == "")
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
			    		tempStr.append(stack.pop() + ",");
			    		numberofElements--;
			    	}
			    	// remove the last comma
			    	tempStr.setCharAt(tempStr.length()-1, '\0');
			    	
			    	tempStr.append(")");
			    	stack.push(tempStr.toString());
			    }
		    	
		    }
		    
		    else if (item.startsWith("<ID:")) {
		    	item = getValueofToken (item);
		    	HashMap currenvMapping = null;
		    	if (item.equalsIgnoreCase("Print")) {
		    		stack.push("Print");       // continue in case its just a print.
		    		continue;
		    	}
		    	else if (item.equals("Conc")) {
		    		stack.push("Conc");
		    		continue;
		    	}
		    	else if (item.equals("Stern")) {
		    		stack.push("Stern");
		    		continue;
		    	}
		    	// lookup in the current environment, get the value and push it on to the stack.
		    	int tempEnvMarker = envMarker;
		    	while (tempEnvMarker >=0 ) {
		    	    System.out.println ("Looking up " + item + " in env " + (tempEnvMarker-1));
		    	    currenvMapping = (HashMap) environmentHash.get("PE_" + (tempEnvMarker-1));
		    	    if (currenvMapping != null &&  currenvMapping.get(item) != null) {
		    	    	break;
		    	    }
		    	    tempEnvMarker--;
		    	}
		    	String value = (String) currenvMapping.get(item);
		    	System.out.println ("Identifer " + item + " value pushed to stack " + value);
		    	stack.push(value);
		    }
		
		    else if (item.startsWith(STTransformer.LAMBDA)) {
		    	item = item + ":" + envMarker;
		    	System.out.println ("Pushing lambda into stack " + item);
		    	stack.push(item);
		    }
		    
		    else if (item.equals(STTransformer.GAMMA) || item.contains("gamma")) {
		    	item = STTransformer.GAMMA; // hack for tuples
		    	String itemAtTopOfStack = (String) stack.peek();
		    	if ( ! itemAtTopOfStack.startsWith (STTransformer.LAMBDA) ) {    // if its not lambda, then apply rator to rand and push it back.
		    		String rator = (String) stack.pop();
		    		String rand = (String) stack.pop();
		    		String result = apply(item, rator, rand);
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
		    			//StringTokenizer randStr = new StringTokenizer (rand);
		    			StringTokenizer randStr = new StringTokenizer (rand, ",");
		    			ArrayList randArray = new ArrayList ();
		    			while (randStr.hasMoreElements()) {
		    				String str = randStr.nextToken().trim();
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
		    			System.out.println ("env hash " + "PE_"+envMarker + environmentHash);
		    		}
		    		
		    		else {
		    		    currEnvMapping.put(X, rand);
		    		    environmentHash.put("PE_"+envMarker, currEnvMapping);
		    		    System.out.println ("env hash " +"PE_"+envMarker+ environmentHash);
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
	
	public void removeDelta (ArrayList controlStructure, String s) {
			for(int i=0; i<controlStructure.size(); i++) {
				String st =(String) controlStructure.get(i);
				if (st.startsWith (s)) {
					controlStructure.remove(i);
				}
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
	 
	 private boolean isString (String token) {
     	Pattern p = Pattern.compile("^'[\t\n\\\\\\();,a-zA-Z0-9-+*/<>&.@/:=~|$!#%^_\\[\\]{}\"'?\\s]*'$");  // Strings.. Note the \s space at the end...
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
	 
	public String apply (String item, String rator, String rand) {
		
		rator = getValueofToken(rator);
		rand = getValueofToken (rand);
		
		if (rator.equals("Print")) {
			return rand;
		}
		
		else if (rator.equals("Stern")) {
			
    		rand = rand.replaceAll("'", "");   // replace all single quotes.
    		rand = rand.substring(1);
    		return rand;
		}
		
		else if (rator.equals("Conc")) {
			rand = rand.replaceAll("'", "");
			return "Conc" + rand;
		}
		
		else if (rator.startsWith("Conc")) {
			rand = rand.replaceAll("'", "");
			rand = rator.replaceAll("Conc", "") + rand;
			return rand;
		}
				
		else if (isString(rator)) {
			return rator;
		}
		
		else if (isOperatorSymbol (rator)) {
			String result = "(" +rator + rand + ")";
			return result;
		}
		else  if (isOperatorSymbol (item)) {
 		    char sign=item.charAt(0);
		    rator = getExprValue (rator);
		    rand = getExprValue (rand);
		    
		    //extract the sign from the rator
		    /*if (isOperatorPresent (rator)) {
		    	sign = rator.charAt(0);
		    	rator = rator.substring(1, rator.length());
		    }*/
		    
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
		    	result = (new Integer (rator)).intValue() / (new Integer (rand)).intValue();
		    	return ""+result;
		    default:
		    	return "";
		    }
		   
		}
		else
			return "";
	}
}