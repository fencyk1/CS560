import java.util.ArrayList;
import java.util.List;


	
	public class InfoHolder implements InfoHolderInterface {
	
		private List<Integer> lineNums = new ArrayList<Integer>();
		private List<Integer> LCcounts = new ArrayList<Integer>();
		private List<String> binReps = new ArrayList<String>();
		
		
		/*
		 * There are four element-location methods:
		 * 	-finding a line number by an LC
		 * 	-finding an LC by line number
		 * 	-finding a binary representation by a line # and an LC
		 * 	 
		 * 	an addLine method has also been implemented. At this time
		 * [10-9-10] a Remove method was not deemed necessary.
		 */
		public int findLineByLC (int finder, int start) {
			
			//Added a checking component, in the event that LCcounts.size is 
			//zero at teh start of this call.
			if (start == LCcounts.size()) 
			{
				return -1;
			}
			//iterative searching for the LC counter,s [next, if multiple] location
			while (!(LCcounts.get(start) == finder)) {
				start++;
				//if, at any point, 'start' goes beyond the bounds of the ArrayLists ...
				if (start == LCcounts.size()) {
					// a -1 integer value is returned, stating that there is no other
					// LC in this symbol table.
					return -1;
				}
			}
			//otherwise, just return the line number where the LC is located. 
			return lineNums.get(start);
		}
		
		public int findLCByLine (int loc){
			//gives the programmer the LC value that is located at the line.
			return LCcounts.get(loc);
		}
		
		
		public String findBinaryByLC( int finder, int start) {
		
			// first, find the line the binary rep desired is located in conjunction with the LC.
			int location = findLineByLC(finder, start);
			
			//if no collocation exists, there is nothing to return.
			if(location < 0)
			{
				return null;
			}
			
			//otherwise, give the programmer what he wants. :)
			return binReps.get(start);
		}
		
		// provides the binary encoding located at the given line
		public String findBinaryByLine (int loc) {
			return binReps.get(loc);
		}
		 
		// You give it anLC and a binary String to add to the table.
		// It does the adding so that you have new elements to use.
		public void AddLine(int LC, String bin){
			
			int newNum;
			
			if (lineNums.size() > 0)
			{
				newNum = (lineNums.get(lineNums.size()-1)) + 1;
			}
			else
			{
				newNum = 1;
			}
			lineNums.add(newNum);
			LCcounts.add(LC);
			binReps.add(bin);
			
		}
	}