package flood.monitor.modules;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.widget.Toast;

public class Connector {

	private Connection conn;
	private Statement stmt;
	
	public boolean connect()
    {
            try
            {
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                    String username = "ramirezs";
                    String password = "ceC6CH";
                    conn = DriverManager.getConnection("jdbc:oracle:thin:@asuka.cs.ndsu.nodak.edu:1521:asuka",username,password);
                    if (conn == null)
                    {
                            System.out.println("Failed to set up connection");
                    }
                    else
                    {
                            System.out.println("Connection created");
                            stmt = conn.createStatement();
                            return true;
                    }
            }
            catch (ClassNotFoundException e)
            {
                    System.out.println("Driver not found!\n");
                    e.printStackTrace(System.err);
            }
            catch (SQLException e)
            {
                    e.printStackTrace(System.err);
            } 
            return false;
    }
	
	public void disconnect()
    {
            try
            {
                    stmt.close();
                    conn.close();
                    System.out.println("Connection closed");
            }
            catch (SQLException e)
            {
                    e.printStackTrace(System.err);
            }
    }

	public String executeQuery(String query)
    {
            String resultString = "";
            try
            {
                    ResultSet result = stmt.executeQuery(query);
                    ResultSetMetaData rmeta = result.getMetaData();

                    int colCount = rmeta.getColumnCount();
                    for (int i = 1; i <= colCount; i++)
                    {
                            int size = rmeta.getColumnDisplaySize(i);
                            String name = rmeta.getColumnName(i);
                            resultString += name;
                            for (int j = name.length(); j < size; j++)
                                    resultString += " ";
                    }
                    resultString += "\n";
                    while (result.next())
                    {
                            for (int i = 1; i <= colCount; i++)
                            {
                                    int size = rmeta.getColumnDisplaySize(i);
                                    String value = result.getString(i);
                                    resultString += value;
                                    for (int j = value.length(); j < size; j++)
                                            resultString += " ";
                            }
                            resultString += "\n";
                    }
                    result.close();
            }
            catch (SQLException e)
            {
                    //e.printStackTrace(System.err);
            }
            return resultString;
    }

    public void UploadData(Context context, String latitude, String longitude, String hoursAgo, String minutesAgo, String runoff, String coverDepth, String coverType, String comment, String email){
    	try{
       		URL siteUrl = new URL("http://192.168.0.100/plogger/plog-admin/plog-mobupload.php");
    		HttpURLConnection conn = (HttpURLConnection) siteUrl.openConnection();
    		conn.setRequestMethod("POST");
    		conn.setDoOutput(true);
    		conn.setDoInput(true);
    		
    		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
    		
    		HashMap<String, String> data = new HashMap<String, String>();
			//Name&Value
			data.put("latitude", latitude);
			data.put("longitude", longitude);
			data.put("hours", hoursAgo);
			data.put("min", minutesAgo);
			data.put("runoff", runoff);
			data.put("depth", coverDepth);
			data.put("cover", coverType);
			data.put("comment", comment);
			data.put("contact", email);
    		
    		Set keys = data.keySet();
    		Iterator keyIter = keys.iterator();
    		String content = "";
    		for(int i=0; keyIter.hasNext(); i++) {
    			Object key = keyIter.next();
    			if(i!=0) {
    				content += "&";
    			}
    			content += key + "=" + URLEncoder.encode(data.get(key), "UTF-8");
    		}
    		System.out.println(content);
    		out.writeBytes(content);
    		out.flush();
    		out.close();
    		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    		String line = "";
    		while((line=in.readLine())!=null) {
    			System.out.println(line);
    		}
    		in.close();
    	}catch(Exception ex){
    		Toast.makeText(context, "Error Message: " + ex.getMessage(), 5000).show();
    	}
    }

    public void UploadPicture(Context context, String file){
    	HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		DataInputStream inputStream = null;

		String pathToOurFile = file;
		String urlServer = "http://192.168.0.100/plogger/plog-admin/plog-picture.php";
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary =  "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1*1024*1024;

		try
		{
		FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );

		URL url = new URL(urlServer);
		connection = (HttpURLConnection) url.openConnection();

		// Allow Inputs & Outputs
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);

		// Enable POST method
		connection.setRequestMethod("POST");

		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

		outputStream = new DataOutputStream( connection.getOutputStream() );
		outputStream.writeBytes(twoHyphens + boundary + lineEnd);
		outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
		outputStream.writeBytes(lineEnd);

		bytesAvailable = fileInputStream.available();
		bufferSize = Math.min(bytesAvailable, maxBufferSize);
		buffer = new byte[bufferSize];

		// Read file
		bytesRead = fileInputStream.read(buffer, 0, bufferSize);

		while (bytesRead > 0)
		{
		outputStream.write(buffer, 0, bufferSize);
		bytesAvailable = fileInputStream.available();
		bufferSize = Math.min(bytesAvailable, maxBufferSize);
		bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		}

		outputStream.writeBytes(lineEnd);
		outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

		// Responses from the server (code and message)
		int serverResponseCode = connection.getResponseCode();
		String serverResponseMessage = connection.getResponseMessage();

		fileInputStream.close();
		outputStream.flush();
		outputStream.close();
		}
		catch (Exception ex)
		{
			Toast.makeText(context, "Error Message: " + ex.getMessage(), 5000).show();
		}
    }

	
}
