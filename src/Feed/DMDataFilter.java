package Feed;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DMDataFilter {
	private List<String> inclTerms;
	private List<String> exclTerms;
	
	public DMDataFilter(){
		inclTerms = new ArrayList<String>();
		exclTerms = new ArrayList<String>();
	}
	
	public DMDataFilter(List<String> inclTerms, List<String> exclTerms){
		this.inclTerms = inclTerms;
		this.exclTerms = exclTerms;
	}

	public List<String> getInclTerms() {
		return inclTerms;
	}

	public void setInclTerms(List<String> inclTerms) {
		this.inclTerms = inclTerms;
	}

	public List<String> getExclTerms() {
		return exclTerms;
	}

	public void setExclTerms(List<String> exclTerms) {
		this.exclTerms = exclTerms;
	}
	
	public boolean isMatch(String str){
		for(int i = 0; i < inclTerms.size(); i++){
			if(!specContains(str, inclTerms.get(i))){
				return false;
			}
		}
		for(int i = 0; i < exclTerms.size(); i++){
			if(specContains(str, exclTerms.get(i))){
				return false;
			}
		}
		return true;
	}
	
	public static boolean specContains(String inputStr, String critStr){
		Scanner inputScanner = new Scanner(inputStr);
		Scanner critScanner = new Scanner(critStr);
		boolean matchFound = false;
		while(inputScanner.hasNext()){
			if(!critScanner.hasNext()){
				matchFound = true;
				break;
			}
			String nextIW = inputScanner.next();
			String nextCW = critScanner.next();
			
			//reset the criteria scanner  if the string doesn't meet the criteria
			if(!nextIW.equalsIgnoreCase(nextCW)){
				critScanner = new Scanner(critStr);
			}
		}
		inputScanner.close();
		critScanner.close();
		return matchFound;
		
	}
}
