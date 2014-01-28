package matcher;

import java.util.ArrayList;

public class ResultTuple {
	String hostname,summary,score,accessComplexity;
	ArrayList<String> reference = new ArrayList<String>();
	
	public ResultTuple(String hostname){
		this.hostname=hostname;
	}
	
	public void setHostname(String hostname){
		this.hostname=hostname;
	}
	
	public void setSummary(String summary){
		this.summary=summary;
	}
	
	public void setScore(String score){
		this.score=score;
	}
	
	public void setAccessComplexity(String accessComplexity){
		this.accessComplexity=accessComplexity;
	}
	
	public void setReference(String reference){
		this.reference.add(reference);
	}
	
	public String getHostname(){
		return hostname;
	}
	
	public String getSummary(){
		return summary;
	}
	
	public String getScore(){
		return score;
	}
	
	public String getAccessComplexity(){
		return accessComplexity;
	}
	
	public ArrayList<String> getReference(){
		return reference;
	}
}
