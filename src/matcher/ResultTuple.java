package matcher;

import java.util.ArrayList;

public class ResultTuple {
	private String summary,score,accessComplexity;
	private ArrayList<String> reference, hostname;
	
	public ResultTuple(){
		this.reference = new ArrayList<String>();
		this.hostname = new ArrayList<String>();
	}
	
	public void setHostname(String hostname){
		this.hostname.add(hostname);
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
	
	public ArrayList<String> getHostname(){
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
