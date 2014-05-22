package GUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class HttpUpload extends SwingWorker<Void, Integer> {

private HttpURLConnection httpConn;
private OutputStream outputStream;
private PrintWriter writer;
private File filename;
private String boundary;
String LINE_FEED = "\r\n";

public HttpUpload(File fname, String requestURL, String charset) throws IOException {
	
	URL url = new URL(requestURL);
	boundary = "----darsh----";
	httpConn = (HttpURLConnection) url.openConnection();
	httpConn.setDoOutput(true); // indicates POST method
	httpConn.setRequestMethod("POST");
	httpConn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + boundary);
	outputStream = httpConn.getOutputStream();
	filename = fname;
	writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),true);
	
}

@Override
protected Void doInBackground() throws Exception {
	// TODO Auto-generated method stub
	try{
	String fileName = filename.getName();
	System.out.println(fileName);
    writer.append("--" + boundary).append(LINE_FEED);
    writer.append("Content-Disposition: form-data; name=\"" + "uploadFile" + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
    writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
    writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
    writer.append(LINE_FEED);
    System.out.println(writer);
    writer.flush();
	
	FileInputStream inputStream = new FileInputStream(filename);
	byte[] buffer = new byte[4096];
	int bytesRead = -1;
		
	while ((bytesRead = inputStream.read(buffer)) != -1) {
		outputStream.write(buffer, 0, bytesRead);
	}
	
	inputStream.close();
	outputStream.flush();
    writer.append(LINE_FEED);
    writer.flush();
    	
    writer.append(LINE_FEED).flush();
    writer.append("--" + boundary + "--").append(LINE_FEED);
    writer.close();

    // check server's status code first
    int status = httpConn.getResponseCode();
   
    
    httpConn.getContentType();

    System.out.println("status "+status);
    
    if (status == HttpURLConnection.HTTP_OK) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
        String abc = "abc";
        while ((abc = reader.readLine()) != null) {
        	System.out.println(abc);
            // do nothing, but necessary to consume response from the server
        }
        reader.close();
        httpConn.disconnect();
    } else {
        throw new IOException("Server returned non-OK status: " + status);
    }
    
	} catch (IOException ex) {
	JOptionPane.showMessageDialog(null, "Error uploading file: " + ex.getMessage(),
	        "Error", JOptionPane.ERROR_MESSAGE);           
	ex.printStackTrace();
	cancel(true);
	}
	
	return null;
}
	
	 @Override
	 protected void done() {
	        if (!isCancelled()) {
	            JOptionPane.showMessageDialog(null, "File has been uploaded successfully!", "Message",
	                    JOptionPane.INFORMATION_MESSAGE);
	            
	        }
	   }

}
	 