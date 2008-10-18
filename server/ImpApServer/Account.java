package ImpApServer;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;
import java.util.LinkedList;

public class Account {
    private boolean logged;
    private String username;
    private String password;
    private Document profile;
    private XPath xpath= XPathFactory.newInstance().newXPath();
    Account()   {
        logged=false;
    }
    public boolean login(String _name, String _pass) {
        username=_name;
        password=_pass;
        File configFile = new File(Server.PATH + "\\" + _name + ".txt");
        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            try {
                BufferedReader temp = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(configFile))));
                logged=temp.readLine().equals(_pass);
                loadProfile();
            } catch (Exception e) {
                return false;
            }
        }
        return logged;
    }
    public void logout()    {
        username="";
        password="";
        saveProfile();
    }
    private boolean loadProfile()   {
        File file = new File(Server.PATH + "\\" + username + "\\.profile.xml");
        if (file.exists() && file.isFile() && file.canRead()) {
            try {
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                profile = builder.parse(file);
                return true;
            } catch(Exception e)    {
                return false;
            }
        }
        return false;
    }
    private boolean saveProfile()   {
        File file = new File(Server.PATH + "\\" + username + "\\.!profile.xml");
        try {
            StreamResult result = new StreamResult(new FileOutputStream(file));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(profile);
            transformer.transform(source, result);
        } catch(Exception e)    {
            return false;
        }
        return true;
    }
    public String evaluate(String _path) {
        try {
            return xpath.evaluate(_path, profile);
        } catch (XPathExpressionException e) {
            return null;
        }
    }
    public List<String> LSUB(String base, String mask)  {
        boolean recursive = mask.endsWith("*");
        List<String> result;
        mask = mask.substring(0,mask.length()-1);
        String path = Server.PATH + "\\" + username + "\\" + base + "\\"+mask;
        path = path.replace("/","\\");
        File file = new File(path);
        result = recursiveLSUB(file,recursive);
        return result;
    }
    private List<String> recursiveLSUB(File file, boolean recursive)    {
        List<String> result = new LinkedList<String>();
        if(file.exists() && file.isDirectory())   {
            File[] files = file.listFiles(
                new FilenameFilter() {
                    public boolean accept(File theFile, String fileName) {
                        File file = new File(theFile.getPath()+"\\"+fileName);
                        return file.isDirectory();
                    }
                }
            );
            for(File f : files) {
                if(recursive)   {
                    result.addAll(recursiveLSUB(f,recursive));
                }
                result.add(f.getPath().substring((Server.PATH + "\\" + username + "\\").length(),f.getPath().length()).replace("\\","/"));
            }
        }
        return result;
    }
}