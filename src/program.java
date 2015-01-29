import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class program {

	public static void main(String[] args) throws Exception {

		CrmAuth auth = new CrmAuth();

		// CRM Online
		String url = "https://org.crm.dynamics.com/"; 
		String username ="username@org.onmicrosoft.com"; 
		String password = "password";
		CrmAuthenticationHeader authHeader = auth.GetHeaderOnline(username,
		password, url);
		// End CRM Online

		// CRM OnPremise - IFD
		//String url = "https://org.domain.com/";
		//// Username format could be domain\\username or username in the form of an email
		//String username = "username";
		//String password = "password";
		//CrmAuthenticationHeader authHeader = auth.GetHeaderOnPremise(username, password, url);
		// End CRM OnPremise - IFD

		String id = CrmWhoAmI(authHeader, url);
		if (id == null)
			return;

		String name = CrmGetUserName(authHeader, id, url);
		System.out.println(name);
	}

	public static String CrmWhoAmI(CrmAuthenticationHeader authHeader,
			String url) throws IOException, SAXException,
			ParserConfigurationException {
		StringBuilder xml = new StringBuilder();
		xml.append("<s:Body>");
		xml.append("<Execute xmlns=\"http://schemas.microsoft.com/xrm/2011/Contracts/Services\">");
		xml.append("<request i:type=\"c:WhoAmIRequest\" xmlns:b=\"http://schemas.microsoft.com/xrm/2011/Contracts\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:c=\"http://schemas.microsoft.com/crm/2011/Contracts\">");
		xml.append("<b:Parameters xmlns:d=\"http://schemas.datacontract.org/2004/07/System.Collections.Generic\"/>");
		xml.append("<b:RequestId i:nil=\"true\"/>");
		xml.append("<b:RequestName>WhoAmI</b:RequestName>");
		xml.append("</request>");
		xml.append("</Execute>");
		xml.append("</s:Body>");

		Document xDoc = CrmExecuteSoap.ExecuteSoapRequest(authHeader,
				xml.toString(), url);
		if (xDoc == null)
			return null;

		NodeList nodes = xDoc
				.getElementsByTagName("b:KeyValuePairOfstringanyType");
		for (int i = 0; i < nodes.getLength(); i++) {
			if ((nodes.item(i).getFirstChild().getTextContent())
					.equals("UserId")) {
				return nodes.item(i).getLastChild().getTextContent();
			}
		}

		return null;
	}

	public static String CrmGetUserName(CrmAuthenticationHeader authHeader,
			String id, String url) throws IOException, SAXException,
			ParserConfigurationException {
		StringBuilder xml = new StringBuilder();
		xml.append("<s:Body>");
		xml.append("<Execute xmlns=\"http://schemas.microsoft.com/xrm/2011/Contracts/Services\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">");
		xml.append("<request i:type=\"a:RetrieveRequest\" xmlns:a=\"http://schemas.microsoft.com/xrm/2011/Contracts\">");
		xml.append("<a:Parameters xmlns:b=\"http://schemas.datacontract.org/2004/07/System.Collections.Generic\">");
		xml.append("<a:KeyValuePairOfstringanyType>");
		xml.append("<b:key>Target</b:key>");
		xml.append("<b:value i:type=\"a:EntityReference\">");
		xml.append("<a:Id>" + id + "</a:Id>");
		xml.append("<a:LogicalName>systemuser</a:LogicalName>");
		xml.append("<a:Name i:nil=\"true\" />");
		xml.append("</b:value>");
		xml.append("</a:KeyValuePairOfstringanyType>");
		xml.append("<a:KeyValuePairOfstringanyType>");
		xml.append("<b:key>ColumnSet</b:key>");
		xml.append("<b:value i:type=\"a:ColumnSet\">");
		xml.append("<a:AllColumns>false</a:AllColumns>");
		xml.append("<a:Columns xmlns:c=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\">");
		xml.append("<c:string>firstname</c:string>");
		xml.append("<c:string>lastname</c:string>");
		xml.append("</a:Columns>");
		xml.append("</b:value>");
		xml.append("</a:KeyValuePairOfstringanyType>");
		xml.append("</a:Parameters>");
		xml.append("<a:RequestId i:nil=\"true\" />");
		xml.append("<a:RequestName>Retrieve</a:RequestName>");
		xml.append("</request>");
		xml.append("</Execute>");
		xml.append("</s:Body>");

		Document xDoc = CrmExecuteSoap.ExecuteSoapRequest(authHeader,
				xml.toString(), url);
		if (xDoc == null)
			return null;

		String firstname = "";
		String lastname = "";

		NodeList nodes = xDoc
				.getElementsByTagName("b:KeyValuePairOfstringanyType");
		for (int i = 0; i < nodes.getLength(); i++) {
			if ((nodes.item(i).getFirstChild().getTextContent())
					.equals("firstname")) {
				firstname = nodes.item(i).getLastChild().getTextContent();
			}
			if ((nodes.item(i).getFirstChild().getTextContent())
					.equals("lastname")) {
				lastname = nodes.item(i).getLastChild().getTextContent();
			}
		}

		return firstname + " " + lastname;
	}
}
