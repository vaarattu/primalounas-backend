package primalounas.backend.primalounasbackend.util;
import java.io.*;
import java.net.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.Document;
import org.xml.sax.*;

public class MicrosoftLogin {
    private final String sts = "https://login.microsoftonline.com/extSTS.srf";
    private final String loginContextPath = "/_forms/default.aspx?wa=wsignin1.0";
    private final String sharepointContext = "primacorporate";
    private final String sharepointContext2 = "https://primacorporate.sharepoint.com";
    private final String reqXML = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\" xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"><s:Header><a:Action s:mustUnderstand=\"1\">http://schemas.xmlsoap.org/ws/2005/02/trust/RST/Issue</a:Action><a:ReplyTo><a:Address>http://www.w3.org/2005/08/addressing/anonymous</a:Address></a:ReplyTo><a:To s:mustUnderstand=\"1\">https://login.microsoftonline.com/extSTS.srf</a:To><o:Security s:mustUnderstand=\"1\" xmlns:o=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\"><o:UsernameToken><o:Username>[username]</o:Username><o:Password>[password]</o:Password></o:UsernameToken></o:Security></s:Header><s:Body><t:RequestSecurityToken xmlns:t=\"http://schemas.xmlsoap.org/ws/2005/02/trust\"><wsp:AppliesTo xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\"><a:EndpointReference><a:Address>[endpoint]</a:Address></a:EndpointReference></wsp:AppliesTo><t:KeyType>http://schemas.xmlsoap.org/ws/2005/05/identity/NoProofKey</t:KeyType><t:RequestType>http://schemas.xmlsoap.org/ws/2005/02/trust/Issue</t:RequestType><t:TokenType>urn:oasis:names:tc:SAML:1.0:assertion</t:TokenType></t:RequestSecurityToken></s:Body></s:Envelope>";
    private String generateSAML() {
        String saml = reqXML
                .replace("[username]", "juha.ala-rantala@primapower.com");
        saml = saml.replace("[password]", "Esasippola19");
        saml = saml.replace("[endpoint]", String.format("https://%s.sharepoint.com/_forms/default.aspx?wa=wsignin1.0", sharepointContext));
        return saml;
    }
    public String login() {
        String token;
        try {
            token = requestToken();
            String cookie = submitToken(token);
            return cookie;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    private String requestToken() throws XPathExpressionException, SAXException,
            ParserConfigurationException, IOException {

        String saml = generateSAML();

        URL u = new URL(sts);
        URLConnection uc = u.openConnection();
        HttpURLConnection connection = (HttpURLConnection) uc;

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        // http://stackoverflow.com/questions/12294274/mobile-app-for-sharepoint/12295224#12295224
        // connection.addRequestProperty("SOAPAction", sts);
        connection.addRequestProperty("Content-Type",
                "text/xml; charset=utf-8");
        // connection.addRequestProperty("Expect", "100-continue");
        // connection.addRequestProperty("Connection", "Keep-Alive");
        // connection.addRequestProperty("Content-Length", saml.length() +
        // "");
        // connection.setRequestProperty("SOAPAction", SOAP_ACTION);

        OutputStream out = connection.getOutputStream();
        Writer wout = new OutputStreamWriter(out);
        wout.write(saml);

        wout.flush();
        wout.close();

        InputStream in = connection.getInputStream();
        int c;
        StringBuilder sb = new StringBuilder("");
        while ((c = in.read()) != -1)
            sb.append((char) (c));
        in.close();
        String result = sb.toString();
        String token = extractToken(result);
        System.out.println(token);
        return token;
    }
    private String extractToken(String result) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
        //http://stackoverflow.com/questions/773012/getting-xml-node-text-value-with-java-dom
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document document = db.parse(new InputSource(new StringReader(result)));

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xp = xpf.newXPath();
        String token = xp.evaluate("//BinarySecurityToken/text()", document.getDocumentElement());
        //handle error  S:Fault:
        //http://social.microsoft.com/Forums/en-US/crmdevelopment/thread/df862099-d9a1-40a4-b92e-a107af5d4ca2
        System.out.println(token);
        return token;
    }
    private String submitToken(String token) throws IOException {
        //String token = "t=EwBwAk6hBwAUIQT64YiMbkZQLHdw6peopUrQ0O8AAYkt43mh328r0OTpTqSVMQEWGlzlpE906mSyOfU2JgkHQCBz0VBLPKyFEYeCUUqLQ0FmodljevOEceo5L1r+aj207XYvgGl+QBOMxSuNtdbPprICB/+NhRxEynCQe2l1U84a3S20At+OsGorLHKpp1RIfjR6FGGW3ahltWwDvvkcLY5mMtvOHoQx+citNFIvXGY4zzosNgum0OXMlIz26QfODI705ICMV9wmLfbJ4xQjeRAHFrPQxdeQ3mA9tepV9zPKyeAsAmFrMb0/3GUh9GK0jk9O1+N5PZYtL4cKsOrMbGN3Z++IhoTrwLR6/8PJrZNtyKJhv/W35N66THKsKH0DZgAACDKSCSEEFKnaQAEQ+c2vlhFUJ1WBjs9puwnuOFye+J6AvcpFrCaefpBozSYZTQAwJDuHu51xUyrUhrPetgTekrM04m7q6IpqccJBFxTzd3UAkJLgFJQpcerLOFKgYMrVNWOyqEPzn9Zdjv3Xa73HGa36kOUqZeDPcBcxOtMy0I5LmV8tQ4a3Cc302hDax208/eL1fi5xqEiUE89DLEJ8w9KyIWfVUFwvs3r374t/7KJmQH55yZk3p874gNFyToHA4s+0ZuMikRyDTXeYPQ/Jz8rgIYGA+dCwDNb6x+2y26TRX9QiWYvuhcJ8V1xola+Wo6tjHJwon+8QHXLjCiOXkLUvZbjnR2X+UoAnAYNYb5YVeTBqQSO2l19VhK4o5tnHvOhnwVBM8DeGFJSeMChqS7SlPzq/39ntZtPmv9HuvFrP8801pW9KmxgXdoEB&p=";
        // http://cafeconleche.org/books/xmljava/chapters/ch03s05.html
        String url = String.format("https://%s.sharepoint.com%s", sharepointContext, loginContextPath);
        URL u = new URL(url);
        URLConnection uc = u.openConnection();
        HttpURLConnection connection = (HttpURLConnection) uc;

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Accept", "application/x-www-form-urlencoded");
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0)");
        // http://stackoverflow.com/questions/12294274/mobile-app-for-sharepoint/12295224#12295224
        // connection.addRequestProperty("SOAPAction", sts);
        connection.addRequestProperty("Content-Type",
                "text/xml; charset=utf-8");
        // connection.addRequestProperty("Expect", "100-continue");
        // connection.addRequestProperty("Connection", "Keep-Alive");
        // connection.addRequestProperty("Content-Length", saml.length() +
        // "");
        connection.setInstanceFollowRedirects(false);

        OutputStream out = connection.getOutputStream();
        Writer wout = new OutputStreamWriter(out);

        wout.write(token);

        wout.flush();
        wout.close();

        InputStream in = connection.getInputStream();

        //http://www.exampledepot.com/egs/java.net/GetHeaders.html

        for (int i=0; ; i++) {
            String headerName = connection.getHeaderFieldKey(i);
            String headerValue = connection.getHeaderField(i);
            System.out.println("header: " + headerName + " : " + headerValue);
            if (headerName == null && headerValue == null) {
                // No more headers
                break;
            }
            if (headerName == null) {
                // The header value contains the server's HTTP version
            }
        }
        String headerName = connection.getHeaderField("set-cookie");
        System.out.println("headerName");
        System.out.println(headerName);
        int c;
        StringBuilder sb = new StringBuilder("");
        while ((c = in.read()) != -1)
            sb.append((char) (c));
        in.close();
        String result = sb.toString();
        System.out.println(result);

        return headerName;
    }

}
