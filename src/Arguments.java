import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;


public class Arguments {

	HashMap<String,String> opt;
	ArrayList<String> arg;

/* long arguments must begin with a -- (--long)
** short arguments can be combined. (-la)
** NOTE any non argument following an argument is assumed an option of that argument.  (-n 12)
** this could lead to potential issues with the remaining argument array.
** Any non arguments are added to an argument array 
** either the proceeding argument must have an option,  ( --long long_arg arg1 arg2 arg3)
** this will not work ( --long arg1 arg2 arg3) arg1 becomes the value of long
** or the non argument must come before the option ( arg1 arg2 arg3 --long)
** or they must be seperated by a -- (--long -- arg1 arg2 arg3)
*/


public Arguments (String a[]) {

	String old = null;
	boolean end = false;

	arg = new ArrayList<String>();
	opt = new HashMap<String,String>();
	ArrayList<String> a2 = new ArrayList<String>(Arrays.asList(a));

	a2.add("--");

	for (String argument: a2) 
	{
		//old = addArg(old, argument, end)

		//System.out.println("old: " + old + " carg: " + argument);
	
		if (old != null) {
			if ("--".equals(old)) 
				end = true;

			if (end) {
				if (!"--".equals(old)) 
					arg.add(old);
			} else if (old.startsWith("--")) {
			
				if (argument.startsWith("-")) {
					// old code says to add as bool True.
					//System.out.println("add (--,-) " + old.substring(2));
					opt.put(old.substring(2),new String());
				} else {
					// add 
					//System.out.println("add (--,)" + old.substring(2) + " = " + argument);
					opt.put(old.substring(2),argument);
					argument = null;
				}
			} else if (old.startsWith("-")) {
				if (argument.startsWith("-")) {
					for (int i=1; i<old.length(); i++)
					{
						String letter = old.substring(i,i+1);
						//System.out.println("add (-,-) " + letter);
						opt.put(letter,new String());
					}		
				} else {
					for (int i=1; i<old.length()-1; i++)
					{
						String letter = old.substring(i,i+1);
						//System.out.println("add (-,)" + letter);
						opt.put(letter,new String());
					}		
					//System.out.println ("add: (-,)" + old.substring(old.length()-1,old.length()) + " = " + argument);
					opt.put(old.substring(old.length()-1,old.length()),argument);
					argument = null;
				}	
			} else {
				arg.add(old);
			}
			
		}

		old = argument;
	}


}
public ArrayList getArguments() { return arg; }
public String getArgumentAt(int i) { return arg.get(i); }
public HashMap getOptions() { return opt; }
public boolean hasArgument(String s) { return arg.contains(s); }
public boolean hasOption(String s) { return opt.containsKey(s); }
public boolean hasOptionOrArgument(String s) { return opt.containsKey(s) || arg.contains(s); }
public String getOptionForKey(String s) { return opt.get(s); }
public String getOptionForKeys(String shortKey, String longKey) { 
	if (hasOption(shortKey)) return getOptionForKey(shortKey);
	return getOptionForKey(longKey);
}

public String toString() { return "opt: " + opt + " arg: " + arg; }

}
