package CalendarService;


import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPutils {

	public int getresult(String url, String customhead)
	{
		  try{
			  //  String url = "http://localhost:8081/eds_webapp/webapp/Event?eventname=newevent99";
			  
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		 
				// optional default is GET
				con.setRequestMethod("GET");
		 
				//add request header
				con.setRequestProperty("User-Agent", "Mozilla/5.0");
		 
				int responseCode = con.getResponseCode();
				int out = Integer.parseInt(con.getHeaderField(customhead));
				//int out = con.getHeaderFieldInt(customhead, -1);
				System.out.println("output from GET is " + out);
				System.out.println("\nSending 'GET' request to URL : " + url);
				System.out.println("Response Code : " + responseCode);
				return out;

				}catch(Exception e) {
					//e.printStackTrace();
				}
				
			  
		return -2; // change this to -2
	}
}

// Code to read a webpage or something 
/*	BufferedReader in = new BufferedReader(
new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();

while ((inputLine = in.readLine()) != null) {
response.append(inputLine);	
}
in.close();

System.out.println(response.toString());
*/
