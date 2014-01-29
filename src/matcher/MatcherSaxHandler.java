package matcher;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class MatcherSaxHandler extends DefaultHandler{

	private HashSet<ResultTuple> vuls = new HashSet<ResultTuple>();
	Set<Match> matches = null;
	private ResultTuple temp = null;
	boolean flag = false, scoreFlag=false, summaryFlag=false, accessComplFlag=false, referenceFlag=false;
	
	public MatcherSaxHandler(Set<Match> matches){
		this.matches = matches;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(qName.equalsIgnoreCase("entry"))
		{
			for(Match m: matches){
				if(m.getCve().equals(attributes.getValue("id"))){
					flag = true;
					temp = new ResultTuple(m.getHostname());
				}
			}
		}

		if(flag==true && qName.equalsIgnoreCase("cvss:score"))
			scoreFlag=true;
		
		if(flag==true && qName.equalsIgnoreCase("cvss:access-complexity"))
			accessComplFlag=true;
		
		if(flag==true && qName.equalsIgnoreCase("vuln:summary"))
			summaryFlag=true;
		
		if(flag==true && qName.equalsIgnoreCase("vuln:reference"))
			referenceFlag=true;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(qName.equalsIgnoreCase("entry") && flag==true){
			flag=false;
			vuls.add(temp);
		}
	}

	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		if(scoreFlag){
			temp.setScore(new String(ch, start, length));
			scoreFlag=false;
		}
		if(accessComplFlag){
			temp.setAccessComplexity(new String(ch, start, length));
			accessComplFlag=false;
		}
		if(summaryFlag){
			temp.setSummary(new String(ch, start, length));
			summaryFlag=false;
		}
		if(referenceFlag){
			temp.setReference(new String(ch, start, length));
			referenceFlag=false;
		}
	}
	
	public HashSet<ResultTuple> getTuple(){
		return vuls;
	}
	
}
