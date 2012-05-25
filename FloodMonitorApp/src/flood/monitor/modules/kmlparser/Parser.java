package flood.monitor.modules.kmlparser;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class Parser {

	public void Parse(String file, InputStream stream) {
/*
 <?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://www.opengis.net/kml/2.2">
  <Document>
    <Placemark>
      <name>CDATA example</name>
      <description>
        <![CDATA[
          <h1>CDATA Tags are useful!</h1>
          <p><font color="red">Text is <i>more readable</i> and 
          <b>easier to write</b> when you can avoid using entity 
          references.</font></p>
        ]]>
      </description>
      <Point>
        <coordinates>102.595626,14.996729</coordinates>
      </Point>
    </Placemark>
  </Document>
</kml> 
 */
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = new DefaultHandler() {

				boolean bfname = false;
				boolean blname = false;
				boolean bnname = false;
				boolean bsalary = false;

				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {
					Log.i(Parser.class.toString(), "Start Element :" + qName);
				}

				public void endElement(String uri, String localName,
						String qName) throws SAXException {
					Log.i(Parser.class.toString(), "End Element :" + qName);
				}

				public void characters(char ch[], int start, int length)
						throws SAXException {
				}

			};

			saxParser.parse(stream, handler);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}