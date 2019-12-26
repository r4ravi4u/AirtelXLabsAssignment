/*
 * Assumptions : 
 * 1. Assuming "," as Tokenizer for each Address to convert into individual words
 * 2. Sector C6 = C-6/ -> Implies if a word contains both "-" & "/" than it can be treated as Sector + combine letters from left & right of "-" and remove /
 * 3. Whatever coming after "/" will be a +ve integer only
 * 4. If nothing matches, return Blank "" String
 * 5. Will try to convert each address into exact 3 words like below : 
 * 		a) C-6/6280, Vasant Kunj [2 words] = 6280, Sector C6, Vasant Kunj [3 Words]
 * 		b) Plot 18, National Highway  8, Udyog Vihar Phase -4, Gurgaon [4 words] = Plot 18 National Highway  8, Udyog Vihar Phase -4, Gurgaon [3 words]
 * Implies that :
 * 		Word 1 = Exact Plot / Flat No. ( Or Building Name, Nearby Point Name)
 * 		Word 2 = Sector Name 'OR' Area / Locality Name
 *  	Word 3 = City Name
 *  6. Assuming that City Name will surely be given in each Address 
 *  7. If "N" (new Address i.e. to be matched) is :
 *  	a) Case A : N contains final 1 word only. Will be treated as City. Now if list "M" contains exact 1 record with city equals to N's city, will return that record, else return Blank
 *  	b) Case B : N contains final 2 words. Implies Area Name + City. So if exact 1 record matching, then return record, else return Blank
 *  8. If M contains Duplicate Address, we will discard 1 address
 *  9. Spell Checks not taken care by code. Sector, Sec, Sectr, Sctr will be treated as different
 *  10. Treating lowercase and uppercase as same
 *  11. Assuming Plot nos. (final comparing part) is +ve Integer only.
 *  
 *  Creating a Lookup table (HashMap) again containing a HashMap as value which in turn take a TreeSet as its value
 *  This will result into a O(1) time complexity for searching and hence our main LookUp Table will be build directly in O(n) where n = No. of Strings in M
 *  Creating a Map Lookup table as it helps in faster searching for continuous N queries (for new Address Match)
 *  Build table only once and search query speed optimised
 */	
package airtel.ClosestAddressMatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

interface Constants
{
	String BLANK = "";
	String SECTOR = "sector";
	String HYPHEN = "-";
	String REGEX = ",";
	String ALL_SPACE_REMOVE = "\\s";
	//String SINGLE_SPACE = " ";
}

public class ClosestAddressMatch 
{
	private static class PlotIndex implements Comparable<PlotIndex>{
		int plot;
		int index;
		
		PlotIndex(int plot, int index)	{
			this.plot = plot;
			this.index = index;
		}

		@Override
		public int compareTo(PlotIndex o) {
			return this.plot - o.plot;
		}
	}
	
	private static void mapUpdate(String city, String area, int plot, int index,
			Map<String, Map<String, Set<PlotIndex>>> map) {

		Map<String, Set<PlotIndex>> value = null;
		Set<PlotIndex> set = null;

		if (map.containsKey(city)) {
			value = map.get(city);
			if (value.containsKey(area)) {
				set = value.get(area);
				set.add(new PlotIndex(plot, index));
				return;
			}
			set = new TreeSet<>();
			set.add(new PlotIndex(plot, index));
			value.put(area, set);
			return;
		}
		value = new HashMap<>();
		set = new TreeSet<>();
		set.add(new PlotIndex(plot, index));
		value.put(area, set);
		map.put(city, value);
	}
	
	private static void getAddressUtil(String[] str, int index, Map<String, Map<String, Set<PlotIndex>>> map)	{
		str[0] = str[0].replaceAll("[^\\d]", "");	//remove all alphabets
		int plot = Integer.parseInt(str[0]);
		
		str[str.length - 2] = str[str.length - 2].replaceAll(Constants.SECTOR, Constants.BLANK);
		str[str.length - 2] = str[str.length - 2].replaceAll(Constants.HYPHEN, Constants.BLANK);
		
		mapUpdate(str[str.length - 1], str[str.length - 2], plot, index, map);
	}
	
	private static String updateAddressUtil(String str, AtomicInteger plot)	{
		str = str.replaceAll(Constants.SECTOR, Constants.BLANK);
		str = str.replaceAll(Constants.HYPHEN, Constants.BLANK);
		
		int index = str.indexOf("/");
		
		//means we have case c) as per above example
		if(index != -1)	{
			/*
			 * We need to split str[0] into 2 parts : 
			 * part 1 : after / int
			 * part 2 : before / String without "-"
			 */
			String first = str.substring(index+1);	//plot
			str = str.substring(0, index);	//area
			//str[1] = city
			
			//convert first to int, it can throw exception in case input is not proper (must be numeric)
			first = first.replaceAll("[^\\d]", "");	//remove all alphabets 
			plot.set(Integer.parseInt(first));					
		}
		
		return str;
		
	}
	
	public String closestAddressMatch(List<String> M, String N)	{
		if(M == null || N == null)
			return Constants.BLANK;
		
		int lenM = M.size();
		int lenN = N.length();
		
		if(lenM < 1)
			return Constants.BLANK;
		
		if(lenN < 1)	//all address can match and empty N
			return M.get(0);	//returns 1st address (or can return any address)
		
		/*
		 * String 1 = City Name
		 * String 2 = Colony / Area / Locality Name
		 * Set's Plot = Plot number + Index of the address in list M to send final result back
		 */
		Map<String, Map<String, Set<PlotIndex>>> map = new HashMap<>();
		
		//build lookUp map Table
		for(int i = 0; i < lenM; i++)	{
			
			String curr = M.get(i).replaceAll(Constants.ALL_SPACE_REMOVE, Constants.BLANK).toLowerCase();
			
			String str[] = curr.split(Constants.REGEX);
			
			//str[0] = treat this as City Given Only
			Map<String, Set<PlotIndex>> value = null;
			Set<PlotIndex> set = null;
			if(str.length == 1)	{				
				set = new TreeSet<>();
				set.add(new PlotIndex(-1, i));
				if(map.containsKey(str[0]))	{
					value = map.get(str[0]);
					value.putIfAbsent(Constants.BLANK, set);
					continue;
				}
				value = new HashMap<>();
				value.put(Constants.BLANK, set);
				map.put(str[0], value);
				continue;
			}
			
			/*
			 * str[0] = Area / Colony / Locality
			 * str[1] = City
			 * Examples : 
			 * a) (Sector C6, Vasant Kunj) => C6, Vasant Kunj <= (C-6, Vasant Kunj)
			 * b) Udyog Vihar Phase -4, Gurgaon
			 * c) C-6/6280, Vasant Kunj -> Actually it contains full address but need to take care accordingly
			*/
			AtomicInteger plot = new AtomicInteger(-1);
			if(str.length == 2)	{
				
				str[0] = updateAddressUtil(str[0], plot);
				mapUpdate(str[1], str[0], plot.get(), i, map);
				continue;				
			}
			
			/*
			 * no. of words with "," as separator >= 3
			 * Hence we will take like this :
			 * str[n-1] = city
			 * str[n-2] = area
			 * str[0] = plot
			 */
			
			getAddressUtil(str, i, map);
						
		}
		
		//LookUp map has been built up, Now lets search for N (new Address)
		N = N.replaceAll(Constants.ALL_SPACE_REMOVE, Constants.BLANK).toLowerCase();
		String[] str = N.split(Constants.REGEX);
		int len = str.length;
		
		Map<String, Set<PlotIndex>> value = null;
		Set<PlotIndex> set = null;
		int index = -1;
		//city only
		Iterator itr = null;
		int size = -1;
		if(len == 1)	{
			if(!map.containsKey(str[0]))
				return Constants.BLANK;
			
			value = map.get(str[0]);
			/*
			 * if(value.size() > 1) //more than 1 areas so unable to match return
			 * Constants.BLANK;
			 */
			if((set = value.get(Constants.BLANK)) != null)	{
				itr = set.iterator();
				while(itr.hasNext())
					index = ((PlotIndex)itr.next()).index;
				
				return M.get(index);
			}
			
			if(value.size() > 1)
				return Constants.BLANK;
			
			for(String s : value.keySet())	{
				set = value.get(s);
			}				
			
			itr = set.iterator();
			while(itr.hasNext())
				index = ((PlotIndex)itr.next()).index;
			
			return M.get(index);
		}
		
		AtomicInteger plot = new AtomicInteger(-1);
		int last = Integer.MAX_VALUE;
		PlotIndex p = null;
		if(len == 2) {
			str[0] = updateAddressUtil(str[0], plot);
			
			if(!map.containsKey(str[0]))
				return Constants.BLANK;
			
			value = map.get(str[1]);
			if(!value.containsKey(str[0]))
				return Constants.BLANK;
			
			set = value.get(str[0]);
			if(plot.get() == -1)	{
				p = new PlotIndex(-1, -1);	//make dummy and check set
				if(!set.contains(p) && set.size() > 1)
					return Constants.BLANK;
				
				itr = set.iterator();
				while(itr.hasNext())
					index = ((PlotIndex)itr.next()).index;
				
				return M.get(index);				
			}
			
			itr = set.iterator();
			while(itr.hasNext())	{
				p = (PlotIndex)itr.next();
				int curr = Math.abs(plot.get() - p.plot);
				if(last > curr)	{
					index = p.index;
					last = curr;
				}
				else
					break;
			}
			return M.get(index);			
		}
		
		str[0] = str[0].replaceAll("[^\\d]", "");	//remove all alphabets
		plot.set(Integer.parseInt(str[0]));
		
		//area / locality
		str[str.length - 2] = str[str.length - 2].replaceAll(Constants.SECTOR, Constants.BLANK);
		str[str.length - 2] = str[str.length - 2].replaceAll(Constants.HYPHEN, Constants.BLANK);
		
		if(!map.containsKey(str[str.length - 1]))
			return Constants.BLANK;
		
		value = map.get(str[str.length - 1]);
		if(!value.containsKey(str[str.length - 2]))
			return Constants.BLANK;
		
		set = value.get(str[str.length - 2]);
		
		itr = set.iterator();
		while(itr.hasNext())	{
			p = (PlotIndex)itr.next();
			int curr = Math.abs(plot.get() - p.plot);
			if(last > curr)	{
				index = p.index;
				last = curr;
			}
			else
				break;
		}
		
		return M.get(index);		
	}
	
    public static void main( String[] args )
    {
    	ClosestAddressMatch cam = new ClosestAddressMatch();
    	
    	List<String> M = new ArrayList<>();
		M.add("6480, Sector C6, Vasant Kunj");
		M.add("Plot 16, Udyog Vihar Phase -4, Gurgaon");
		M.add("Plot 19, Udyog Vihar Phase -4, Gurgaon");
		M.add("8231, Sector C8, Vasant Kunj");
		M.add("C-6/6280, Vasant Kunj");
		M.add("Sector 5, Pitampura");
		
		String N1 = "6279, Sector C6, Vasant Kunj";
		String N2 = "Plot 18, National Highway  8, Udyog Vihar Phase -4, Gurgaon";
		String N3 = "Pitampura";
		
		System.out.println(cam.closestAddressMatch(M, N1));
		System.out.println(cam.closestAddressMatch(M, N2));
		System.out.println(cam.closestAddressMatch(M, N3));
    }
}