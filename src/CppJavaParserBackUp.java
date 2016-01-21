import java.io.*;
import java.util.Stack;
/**
 * Created by Soneer Sainion
 * A simple parser to find errors in code such as unclosed brackets ect.
 */
public class CppJavaParserBackUp {
	public static void main(String arg[]) throws IOException{

		String path = arg[0];
		Stack<StackItem> curlyBrackets = new Stack<StackItem>();
		Stack<StackItem> parathensis = new Stack<StackItem>();
		Stack<StackItem> squareBrackets = new Stack<StackItem>();
		Stack<StackItem> ifStack = new Stack<StackItem>();
		FileReader fr = new FileReader(path); 
		BufferedReader br = new BufferedReader(fr); 
		String s; 


		int numberOfErrors = 0;
		int line = 1;
		int multiLineCommentFLag = 0;
		int singleQouteFLag = 0;
		int doubleQouteFLag = 0;
		StackItem tempItem = null;
		StackItem multLineItem = null;
		StackItem ifItem = null;

		while((s = br.readLine()) != null) 
		{
			if(s.contains("#ifdef") && !s.contains("\"#ifdef\""))
			{
				ifItem = new StackItem(line,s.indexOf("#ifdef"),"#ifdef",s);
				ifStack.push(ifItem);
			}
			if(s.contains("#ifndef") && !s.contains("\"#ifdef\""))
			{
				ifItem = new StackItem(line,s.indexOf("#ifndef"),"#ifndef",s);
				ifStack.push(ifItem);
			}
			if(s.contains("#if defined") && !s.contains("\"#if defined\""))
			{
				ifItem = new StackItem(line,s.indexOf("#if defined"),"#if defined",s);
				ifStack.push(ifItem);
			}
			if(s.contains("#if ! defined") && !s.contains("\"#if ! defined\""))
			{
				ifItem = new StackItem(line,s.indexOf("#if ! defined"),"#if ! defined",s);
				ifStack.push(ifItem);
			}
			if(s.contains("#else") &&  ifStack.isEmpty())
			{
				numberOfErrors++;
				System.out.println(line +":"+ s.indexOf("#else") + ":" + s + ":"+"#else"+"#else without if statement");
			}
			if(s.contains("#endif")  && !ifStack.isEmpty())
			{
				ifStack.pop();
			}
			else if(s.contains("#endif") && ifStack.isEmpty())
			{
				numberOfErrors++; 
				System.out.println(line +":"+ s.indexOf("#endif") + ":" + s + ":"+"#endif"+"#endif without if statement");
			}


			for(int i=0; i<s.length();i++)
			{	
				//Current Plus One Char After
				char currentSecondChar;
				//Current Plus On Char Before
				char currentFirstChar;
				if(i>1)
				{
					currentFirstChar = s.charAt(i - 1);
				}
				else
				{
					currentFirstChar = '\0';
				}
				char currentChar = s.charAt(i);
				if(i<(s.length()-1))
				{
					currentSecondChar = s.charAt(i + 1);
				}
				else
				{
					currentSecondChar = '\0';
				}
				String currentString = currentChar+"";
				String currentSecondString =  currentChar+""+currentSecondChar+"";
				String currentFirstString = currentFirstChar+""+currentChar+"";
				tempItem = new StackItem(line,i,currentString, s);

				if(multiLineCommentFLag == 0 && singleQouteFLag ==0 && doubleQouteFLag == 0)
				{

					//Switch statement to add [{,(,),},ect] to correct stacks
					switch (currentString)
					{
					case "{": curlyBrackets.push(tempItem); break;
					case "}": 
						if(!curlyBrackets.isEmpty())
						{
							curlyBrackets.pop(); 
						}
						else
						{
							numberOfErrors++;
							System.out.println(line +":"+ tempItem.getColomn() + ":" + s + ":"+tempItem.getSymbol()+"Curly bracket without start bracket");
						}
						break;
					case "[": squareBrackets.push(tempItem); break;
					case "]": 
						if(!squareBrackets.isEmpty())
						{
							squareBrackets.pop(); 
						}
						else
						{
							numberOfErrors++;
							System.out.println(line +":"+ tempItem.getColomn() + ":" + s + ":"+tempItem.getSymbol()+"Closed bracket without start bracket");
						}
						break;
					case "(": parathensis.push(tempItem);   break;
					case ")": 
						if(!parathensis.isEmpty())
						{
							parathensis.pop(); 
						}
						else
						{
							numberOfErrors++;
							System.out.println(line +":"+ tempItem.getColomn() + ":" + s + ":"+tempItem.getSymbol()+" Close parathesis without start parathesis");
						}
						break;
					case "\"": 
						if(doubleQouteFLag == 0)
						{
							doubleQouteFLag = 1 ;
						}
						else if(doubleQouteFLag == 1)
						{
							doubleQouteFLag = 0 ;
						}
						break;

					case "\'": 
						if(singleQouteFLag == 0)
						{
							singleQouteFLag = 1 ;
						}
						else if(singleQouteFLag == 1)
						{
							singleQouteFLag = 0 ;
						}
						break;
					default:break;
					}

					//Switch statement to add 2 char strings to correct stacks
					switch (currentSecondString)
					{
					case "//": break;
					case "/*": 
						multLineItem = new StackItem(line,i,currentSecondString, s);
						multiLineCommentFLag = 1 ;
						break;
					case "*/": 
						if(multiLineCommentFLag == 0)
						{
							numberOfErrors++;
							System.out.println(tempItem.getLine() +":"+ tempItem.getColomn() + ":" + s + ":"+ currentSecondString+"*/ without start /*");
						}
						break;
					default:break;
					}

				}
				else if(currentSecondString.equals("*/") && currentFirstString.equals("\"*/"))
				{
					multiLineCommentFLag = 0;
				}
				else if(currentString.equals("\'") && !currentFirstString.equals("\\\'"))
				{
					singleQouteFLag = 0;
				}
				else if(currentString.equals("\"") && !currentFirstString.equals("\\\""))
				{
					doubleQouteFLag = 0;
				}
				else{
					// add a current error to list of errors
				}
			}
			if(singleQouteFLag ==1)
			{
				singleQouteFLag = 0;
				numberOfErrors++;
				System.out.println(tempItem.getLine() +":"+ tempItem.getColomn() + ":" + s + ":"+ tempItem.getSymbol()+"Unclosed single quote");
			}
			if(doubleQouteFLag ==1)
			{
				doubleQouteFLag = 0;
				numberOfErrors++;
				System.out.println(tempItem.getLine() +":"+ tempItem.getColomn() + ":" + s + ":"+ tempItem.getSymbol()+"Unclosed double quote");
			}
			line++;
		} 
		fr.close(); 
		if(multiLineCommentFLag==1)
		{
			numberOfErrors++;
			System.out.println(multLineItem.getLine() +":"+ multLineItem.getColomn() + ":" + multLineItem.getLineString() + ":"+ multLineItem.getSymbol()+"Unclosed multi comment");
		}

		numberOfErrors = numberOfErrors + traverseStack(curlyBrackets);
		numberOfErrors = numberOfErrors + traverseStack(parathensis);
		numberOfErrors = numberOfErrors + traverseStack(squareBrackets);
		numberOfErrors = numberOfErrors + traverseStack(ifStack);

		if(numberOfErrors > 0)
		{
			System.exit(-1);    // if there are errors

		}
		else
		{
			System.exit(0);  
		}
	}
	public static int traverseStack(Stack<StackItem> currentStack)
	{ 	int errors = 0;
	if(!currentStack.isEmpty()){

		for(StackItem obj : currentStack)
		{
			errors++;
			System.out.println(obj.getLine() +":"+ obj.getColomn() + ":" + obj.getLineString() + ":"+ obj.getSymbol()+" Unclosed" + obj.getSymbol() );
		}
	}
	return errors;
	}

	static class StackItem {
		public int x = 0;
		public int y = 0;
		public String z = "";
		public String k = "";
		//constructor
		public StackItem(int line, int colomn, String currentChar, String lineString) {
			x = line;
			y = colomn;
			z = currentChar;
			k = lineString;
		}
		public int getColomn(){
			return y+1;
		}
		public int getLine(){
			return x;
		}
		public String getSymbol(){
			return z;
		}
		public String getLineString(){
			return k;
		}
	}
}


