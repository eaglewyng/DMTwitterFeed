package Feed;

import java.util.ArrayList;
import java.util.List;

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
			if(!str.toLowerCase().contains(inclTerms.get(i).toLowerCase())){
				return false;
			}
		}
		for(int i = 0; i < exclTerms.size(); i++){
			if(str.toLowerCase().contains(exclTerms.get(i).toLowerCase())){
				return false;
			}
		}
		return true;
	}
}
