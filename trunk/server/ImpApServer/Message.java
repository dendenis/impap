package ImpApServer;

import java.security.MessageDigest;
import java.util.*;

public class Message
{
	private int UID = 0;
	private int SEQ = 0;
	private String flags = "\\Seen";
	private String internalDate = "";
	private String body = "";
	private Map<String, String> headers=null;
	private String text = "";
	private String envelope = "";
	private String msgId = "";
	
	public Message()
	{
		this.UID = 1;
		this.SEQ = 1;
		this.flags = "\\Seen";
		this.internalDate = "18-OCT-2008 00:12:12 +1200";
        this.headers = new LinkedHashMap<String, String>();
		this.headers.put("From", "IMpAPp <IMpAPp@localhost.localdomain>");
		this.headers.put("To", "You <localuser@localhost.localdomain>");
		this.headers.put("Subject", "Test");
		this.headers.put("Date", "Sun, 1 Apr 1901 12:09:56 +1000");
		this.headers.put("MIME-Version", "1.0");
		this.headers.put("Content-Type", "text/html; charset=\"us-ascii\"");
		this.headers.put("Content-Transfer-Encoding", "7bit");
        //TODO
		this.text = "No Content!";
		this.body = "RFC822 (\"TEXT\" \"HTML\" (\"CHARSET\" \"us-ascii\") NIL NIL \"7BIT\" "+(this.text.length())+" 1)";
		this.envelope = "Sun, 1 Apr 1901 12:09:56 +1000\" \"Test\" ((\"Test User\" NIL \"test\" \"user.id.su\")) ((\"Richard Johnson\" NIL \"richard\" \"rjohnson.id.au\")) ((\"Richard Johnson\" NIL \"richard\" \"rjohnson.id.au\")) ((NIL NIL \"richard\" \"rjohnson.id.au\")) NIL NIL NIL \"<200611050209.kA529kHd013940@mail8.tpgi.com.au>\"";
	}
	
	public void updateEnvelope()
	{
		this.envelope = 
			"\""+this.headers.get("Date")+"\" \""+
			this.headers.get("Subject")+"\" ((\""+
			this.headers.get("From")+"\" NIL))";
		this.envelope = "\""+this.headers.get("Date")+"\" \""+this.headers.get("Subject")+"\" ((NIL NIL \""+this.headers.get("From")+"\" \"localhost\")) ((NIL NIL \""+this.headers.get("From")+"\" \"localhost\")) ((NIL NIL \""+this.headers.get("From")+"\" \"localhost\")) ((NIL NIL \"your name\" \"localhost\")) NIL NIL NIL \"<"+this.msgId+">\"";
	}
	
	public String getInternalDate()	{ return this.internalDate; }
    public void setInternalDate(String date)	{ this.internalDate = date; }
    
    public String getText()	{ return this.text; }
    public void setText(String text)	{ this.text = text; }

    public int getUID()	{ return this.UID; }
    public void setUID(int uid) {this.UID = uid; }

    public int getSEQ()	{ return this.SEQ; }
    public void setSEQ(int seq) {this.SEQ = seq; }

    public String getBody()	{
        return this.body;
    }
    public String getFlags()	{
        return this.flags;
    }
	public String getEnvelope()	{
		return this.envelope;
	}
    public void setHeader(String header, String value)	{
        this.headers.put(header, value);
    }
    public void setMsgId(String msgId)	{
        this.msgId = msgId;
    }


    public String getHeaders(String[] params, boolean exclude)	{
        String output = "";
		if (exclude)		{
			Map<String, String> temp = this.headers;
			for (String  x : params)    {
				if (this.headers.containsKey(x))    {
					temp.remove(x);
				}
			}
            for(String key : temp.keySet())   {
                output += key+": "+temp.get(key)+"\r\n";
            }
			return output;
		}
		else    {
			for (String x : params) {
				if (this.headers.containsKey(x))    {
					output += x + ": " + this.headers.get(x) + "\r\n";
				}
			}
			return output;
		}
	}
	
	public String getHeaders()  {
		String output = "";
        for(String key : this.headers.keySet()) {
            output += key+": "+this.headers.get(key)+"\r\n";
        }
		return output;
	}
	
	public int getSize()	{
		String temp = this.getHeaders()+"\r\n"+this.getBody();
		return temp.length();
	}

	public void clearFlags()    { this.flags = ""; }
	
	public String getHash() {
		String hashStr = "";
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(this.body.getBytes());
            for(String key : this.headers.keySet()) {
                md5.update((key+": "+this.headers.get(key)).getBytes("UTF-8"));
            }
            md5.update((this.internalDate).getBytes());
			byte[] hash = md5.digest();
			for (byte i : hash) {
				hashStr += Integer.toHexString(i & 0xff);
			}
			System.out.println(hashStr);
		}
		catch (Exception err)   {
			err.printStackTrace();
		}
		return hashStr;
	}
}
