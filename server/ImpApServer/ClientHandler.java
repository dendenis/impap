package ImpApServer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class ClientHandler implements Runnable {
    private Socket client = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private int state = 0;
    private String currentMailbox = "";
    private String username = "";
    private String password = "";
    private Account profile = new Account();
    private Server server = null;

    public static final int NOT_AUTHENTICATED_STATE = 0;
    public static final int AUTHENTICATED_STATE = 1;
    public static final int SELECTED_STATE = 2;
    public static final int LOGOUT_STATE = 3;
    public static int UIDVALIDITY = 1;

    private Map<String, List<Message> > messages = new HashMap<String, List<Message> >();

    public ClientHandler(Socket client, Server server) {
        this.client = client;
        this.server = server;
        //test purposes only
        List<Message> msg = new ArrayList<Message>();
        Message m = new Message();
        m.setUID(4827313);
        msg.add(m);
        m = new Message();
        m.setUID(4827315);
        msg.add(m);
        messages.put("INBOX",msg);
        Thread myThread = new Thread(this);
        myThread.start();
    }

    public void run() {
        if (this.client == null || !this.client.isConnected()) {
            System.out.println("Unable to start session, invalid connection token");
        } else {
            System.out.println("client handler thread started for client " + client.getInetAddress().getHostAddress());

            try {
                this.out = new PrintWriter(client.getOutputStream());
                this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                //greeting
                println("* OK [CAPABILTY IMAP4rev1] IMpossible APplication Copyright Unseeing_Eye");
                out.flush();
                String line = in.readLine();
                Pattern pattern = Pattern.compile("([a-zA-Z0-9]+) ([a-zA-Z0-9]+)(.*)");
                while (line != null) {
                    System.out.println(line);
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String tag = matcher.group(1);
                        String command = matcher.group(2);
                        String params = matcher.group(3);
                        handleCommand(tag, command, params);
                    } else {
                        this.client.close();
                        return;
                    }
                    line = in.readLine();
                }
                System.out.println("connection to " + client.getInetAddress().getHostAddress() + " closed by client");

            }
            catch (Exception err) {
                err.printStackTrace();
            }

        }
    }

    public void handleCommand(String tag, String command, String params) {
        command = command.toUpperCase();
        //-----------------------------------------------------------
        //--------------------ALL PURPOSE COMMANDS-------------------
        //-----------------------------------------------------------
        if (command.equals("CAPABILITY")) {
            println("* CAPABILITY IMAP4rev1 AUTH=PLAIN");
            println(tag + " OK CAPABILITY completed");
        } else if (command.equals("NOOP")) {
            println(tag + " OK NOOP completed (z-z-z...)");
        } else if (command.equals("LOGOUT")) {
            println("* BYE IMAP4rev1 Server logging out");
            println(tag + " OK LOGOUT completed");
            flush();
            try {
                client.close();
                return;
            }
            catch (Exception err) {
                err.printStackTrace();
            }
        }
        //-----------------------------------------------------------
        //------------------STATE SPECIFIC COMMANDS------------------
        //-----------------------------------------------------------
        else {
            //-----------------------------------------------------------
            //-----------------------------------------------------------
            if (this.state == NOT_AUTHENTICATED_STATE) {
                //-----------------------------------------------------------
                if (command.equals("LOGIN")) {
                    try {
                        String[] bits = params.split(" ");
                        this.username = bits[1]+Server.CONTEXT;
                        this.password = bits[2];
                    }
                    catch (Exception err) {
                        println(tag + " BAD parameters");
                        flush();
                        return;
                    }
                    if (profile.login(username,password)) {
                        this.state = AUTHENTICATED_STATE;
                        println(tag + " OK LOGIN completed");
                    }
                    else {
                        println(tag + " NO LOGIN failed.");
                    }
                }
                //-----------------------------------------------------------
                else if (command.equals("AUTHENTICATE")) {
                    String[] bits = UnpackParams(params);
                    if(bits.length>1 && bits[1].toUpperCase().equals("PLAIN")) {
                        println("+");
                        try {
                            params=in.readLine();
                            System.out.println(params);
                            params= Base64Coder.decodeString(params);
                            Pattern pattern = Pattern.compile("([a-zA-Z0-9.]+@[a-zA-Z0-9.]+) (.+)");
                            Matcher matcher = pattern.matcher(params);
                            if (matcher.find()) {
                                this.username = matcher.group(1);
                                this.password = matcher.group(2);
                            }
                            if (profile.login(username,password)) {
                                this.state = AUTHENTICATED_STATE;
                                println(tag + " OK AUTHENTICATE completed.");
                            }
                            else    {
                                println(tag + " NO AUTHENTICATE failed.");
                            }
                        } catch(Exception err)    {
                                println(tag + " BAD AUTHENTICATE failed");
                        }
                    } else {
                        println(tag + " BAD AUTHENTICATE unknown method.");
                    }
                }
                //-----------------------------------------------------------
                else {
                    unrecognizedCommand(tag, command);                                          
                }
            }
            //-----------------------------------------------------------
            //-----------------------------------------------------------
            else if (this.state == AUTHENTICATED_STATE || this.state == SELECTED_STATE) {
                //-----------------------------------------------------------
                if (command.equals("SELECT")) {
                    String[] bits = UnpackParams(params);
                    File mailBox = new File(Server.PATH + "\\" + username + "\\" + bits[1]);
                    if(mailBox.exists() && mailBox.isDirectory())   {
                        File[] files = mailBox.listFiles(
                            new FilenameFilter() {
                                public boolean accept(File theFile, String fileName) {
                                    int len = fileName.length();
                                    if (len < 5) {
                                        return false;
                                    }
                                    String suffix = fileName.substring(len - 4, len);
                                    return (suffix.equals(".msg"));
                                }
                            }                                    
                        );
                        println("* " + files.length + " EXISTS");
                        println("* 0 RECENT");
                        println("* OK [UIDVALIDITY " + UIDVALIDITY + "] UID validity status");
                        println(tag + " OK [READ-WRITE] " + command + " COMPLETED");//READ-ONLY maybe
                        this.state = SELECTED_STATE;
                        this.currentMailbox = bits[1];
                    }
                    else {
                        println(tag + " NO SELECT failed, no mailbox with that name");
                    }       
                }
                //-----------------------------------------------------------
                else if (command.equals("STATUS")) {
                    String[] bits = UnpackParams(params);
                    File mailBox = new File(Server.PATH + "\\" + username + "\\" + bits[1]);
                    if(mailBox.exists() && mailBox.isDirectory())   {
                        File[] files = mailBox.listFiles(
                            new FilenameFilter() {
                                public boolean accept(File theFile, String fileName) {
                                    int len = fileName.length();
                                    if (len < 5) {
                                        return false;
                                    }
                                    String suffix = fileName.substring(len - 4, len);
                                    return (suffix.equals(".msg"));
                                }
                            }
                        );
                        println("* " + files.length + " EXISTS");
                        println("* 0 RECENT");
                        println("* OK [UIDVALIDITY " + UIDVALIDITY + "] UID validity status");
                        println(tag + " OK [READ-WRITE] " + command + " COMPLETED");//READ-ONLY maybe
                        this.state = SELECTED_STATE;
                        this.currentMailbox = bits[1];

                    } else {
                        println(tag + " NO STATUS failed, no mailbox with that name");
                    }
                }
                //-----------------------------------------------------------
                else if (command.equals("EXAMINE")) {
                    String[] bits = UnpackParams(params);
                    File mailBox = new File(Server.PATH + "\\" + username + "\\" + bits[1]);
                    if(mailBox.exists() && mailBox.isDirectory())   {
                        File[] files = mailBox.listFiles(
                            new FilenameFilter() {
                                public boolean accept(File theFile, String fileName) {
                                    int len = fileName.length();
                                    if (len < 5) {
                                        return false;
                                    }
                                    String suffix = fileName.substring(len - 4, len);
                                    return (suffix.equals(".msg"));
                                }
                            }
                        );
                        println("* " + files.length + " EXISTS");
                        println("* 0 RECENT");
                        println("* OK [UIDVALIDITY " + UIDVALIDITY + "] UID validity status");
                        println(tag + " OK [READ-ONLY] " + command + " COMPLETED");//READ-ONLY always
                        this.state = SELECTED_STATE;
                        this.currentMailbox = bits[1];

                    } else {
                        println(tag + " NO EXAMINE failed, no mailbox with that name");
                    }
                }
                //-----------------------------------------------------------
                else if (command.equals("LSUB")) {
                    // get a listing of the folders...
                    String[] bits = UnpackParams(params);
                    if(bits.length!=3)  {
                        println(tag + " BAD LSUB failed");
                    }
                    else    {
                        List<String> dirlist = profile.LSUB(bits[1],bits[2]);
                        for(String s : dirlist) {
                            println("* LSUB () \"/\" " + s);
                        }
                        println(tag + " OK LSUB completed");
                    }
                }
                //-----------------------------------------------------------
                /*
                else if (command.equals("SUBSCRIBE")) {
                    System.out.println("----------");
                    System.out.println(params);
                    println(tag + " OK SUBSCRIBE completed");
                } else if (command.equals("UNSUBSCRIBE")) {
                    System.out.println("----------");
                    System.out.println(params);
                    println(tag + " OK UNSUBSCRIBE completed");
                }
                else if (command.equals("CREATE")) {
                    println(tag + " NO you like totally cannot create a mailbox here.");
                } else if (command.equals("DELETE")) {
                    println(tag + " NO ummm, cannot delete either.");
                } else if (command.equals("RENAME")) {
                    println(tag + " NO renaming is right out.");
                } else if (command.equals("LIST")) {
                    // okay, let's have a look at the params before we do anything...
                    if (params.equals(" \"\" \"\"")) {
                        // right, this means the client is after the delimeter
                        println("* LIST (\\Noselect) \"/\" \"\"");
                        println(tag + " OK LIST Completed");
                    } else if (params.equals(" \"\" \"INBOX\"")) {
                        // print out a general description for the inbox...
                        println("* LIST (\\Unmarked) \"/\" \"INBOX\"");
                        println(tag + " OK LIST Completed");
                    } else if (params.equals(" \"\" \"*\"") || params.equals(" \"\" \"INBOX*\"")) {
                        println("* LIST (\\Unmarked) \"/\" \"INBOX\"");
                        // get a listing of the folders...
                        Iterator folderIterator = this.messages.keySet().iterator();
                        while (folderIterator.hasNext()) {
                            println("* LIST () \"/\" \"INBOX/" + folderIterator.next() + "\"");
                        }
                        println(tag + " OK LIST completed");
                    } else {
                        println(tag + " OK LIST Completed");
                    }
                }                  */
                /*
                    else if (command.equals("STATUS"))
                    {
                        // status lets you query a mailbox that is not selected...
                    }
                    */          /*
                else if (command.equals("APPEND")) {
                    println(tag + " NO pfft don't even try it.");
                }                 */
                //-----------------------------------------------------------
                //-----------------------------------------------------------
                else if (this.state == SELECTED_STATE) {
                    //-----------------------------------------------------------
                    if (command.equals("CHECK")) {
                        println(tag + " OK CHECK Completed");
                    }/*
                    else if (command.equals("CLOSE")) {
                        // we don't need to delete anything, so we just go back to the authenticated state
                        println(tag + " OK CLOSE Completed");
                        this.state = AUTHENTICATED_STATE;
                    } else if (command.equals("EXPUNGE")) {
                        println(tag + " NO EXPUNGE i don't think so!");
                    } else if (command.equals("SEARCH")) {
                        println(tag + " NO SEARCH maybe in a later version");
                    } else if (command.equals("FETCH")) {
                        // this is where some fun stuff happens...
                        // get what we can out of the request...
                        Pattern pattern = Pattern.compile(" ([0-9*]+):([0-9*]+) \\((.+)\\)");
                        Matcher matcher = pattern.matcher(params);
                        if (matcher.find()) {
                            // cool, the inital stuff was valid...
                            String startMsg = matcher.group(1);
                            String endMsg = matcher.group(2);
                            String fetchParams = matcher.group(3);

                            int startMsgInt = -1;
                            int endMsgInt = -1;

                            if (!startMsg.equals("*")) {
                                startMsgInt = Integer.parseInt(startMsg);
                            }
                            if (!endMsg.equals("*")) {
                                endMsgInt = Integer.parseInt(endMsg);
                            }

                            List messages = this.getMessagesBetweenSEQs(startMsgInt, endMsgInt);
                            Iterator msgIterator = messages.iterator();
                            while (msgIterator.hasNext()) {
                                Message msg = (Message) msgIterator.next();
                                List commands = getFetchCommands(fetchParams);
                                Iterator iterator = commands.iterator();
                                String output = "* " + msg.getSEQ() + " FETCH (";
                                while (iterator.hasNext()) {
                                    String temp = (String) iterator.next();
                                    output += processFetchCommand(temp, msg) + " ";
                                }
                                output = output.substring(0, output.length() - 1);
                                output += ")";
                                println(output);
                            }

                            println(tag + " OK FETCH completed");
                        } else {
                            pattern = Pattern.compile(" ([0-9*]+) \\((.+)\\)");
                            matcher = pattern.matcher(params);
                            if (matcher.find()) {
                                // cool, the inital stuff was valid...
                                String startMsg = matcher.group(1);
                                int startMsgInt = Integer.parseInt(startMsg);
                                String fetchParams = matcher.group(2);

                                List commands = getFetchCommands(fetchParams);
                                Iterator iterator = commands.iterator();

                                Message msg = this.getMessageBySEQ(startMsgInt);

                                String output = "* " + msg.getSEQ() + " FETCH (";
                                while (iterator.hasNext()) {
                                    String temp = (String) iterator.next();
                                    output += processFetchCommand(temp, msg) + " ";
                                }
                                output = output.substring(0, output.length() - 1);
                                output += ")";
                                println(output);

                                println(tag + " OK UID completed");
                            }

                        }
                    } else if (command.equals("STORE")) {
                        println(tag + " NO STORE, talk to the hand");
                    } else if (command.equals("COPY")) {
                        println(tag + " NO COPY I think that once is enough for you");
                    }*/
                    //-----------------------------------------------------------
                    else if (command.equals("UID")) {
                        if (!params.trim().substring(0, 5).toUpperCase().equals("FETCH")) {
                            //println(tag+" NO COPY, STORE or SEARCH with this server, sorry");
                            println(tag + " OK UID completed");
                        } else {
                            params = params.substring(6).trim();
                            // get the UIDs...
                            Pattern pattern = Pattern.compile("([0-9*]+):([0-9*]+) \\((.+)\\)");
                            Matcher matcher = pattern.matcher(params);
                            if (matcher.find()) {
                                String startUID = matcher.group(1);
                                String endUID = matcher.group(2);
                                String fetchParams = matcher.group(3);
                                if (fetchParams.indexOf("uid") == -1 && fetchParams.indexOf("UID") == -1) {
                                    fetchParams = "UID " + fetchParams;
                                }
                                int startMsgInt = -1;
                                int endMsgInt = -1;
                                if (!startUID.equals("*")) {
                                    startMsgInt = Integer.parseInt(startUID);
                                }
                                if (!endUID.equals("*")) {
                                    endMsgInt = Integer.parseInt(endUID);
                                }
                                List<Message> messages = this.getMessagesBetweenUIDs(startMsgInt, endMsgInt);
                                for(Message msg : messages) {
                                    List<String> commands = getFetchCommands(fetchParams);
                                    String output = "* " + msg.getSEQ() + " FETCH (";
                                    for(String temp : commands) {
                                        output += processFetchCommand(temp, msg) + " ";
                                    }
                                    output = output.substring(0, output.length() - 1);
                                    output += ")";
                                    println(output);
                                }
                                println(tag + " OK UID completed");
                            } else {
                                pattern = Pattern.compile("([0-9*]+) \\((.+)\\)");
                                matcher = pattern.matcher(params);
                                if (matcher.find()) {
                                    String startUID = matcher.group(1);
                                    int startUIDInt = -1;
                                    if (!startUID.equals("*")) {
                                        startUIDInt = Integer.parseInt(startUID);
                                    }
                                    String fetchParams = matcher.group(2);
                                    if (fetchParams.indexOf("uid") == -1 && fetchParams.indexOf("UID") == -1) {
                                        fetchParams = "UID " + fetchParams;
                                    }
                                    List<String> commands = getFetchCommands(fetchParams);
                                    Message msg = this.getMessageByUID(startUIDInt);
                                    String output = "* " + msg.getSEQ() + " FETCH (";
                                    for(String temp : commands) {
                                        output += processFetchCommand(temp, msg) + " ";
                                    }
                                    output = output.substring(0, output.length() - 1);
                                    output += ")";
                                    println(output);
                                    println(tag + " OK UID completed");
                                }
                            }
                        }
                    }
                }
                //-----------------------------------------------------------
                else {
                    unrecognizedCommand(tag, command);
                }
            }
        }
        flush();
    }

    public void unrecognizedCommand(String tag, String command) {
        System.out.println("Could not: " + tag + " " + command);
        println(tag + " BAD " + command + " - i can't do that dave...");
    }

    public List<String> getFetchCommands(String fetchParams) {
        if (fetchParams.equals("ALL")) {
            fetchParams = "FLAGS INTERNALDATE RFC822.SIZE ENVELOPE";
        } else if (fetchParams.equals("FAST")) {
            fetchParams = "FLAGS INTERNALDATE RFC822.SIZE";
        } else if (fetchParams.equals("FULL")) {
            fetchParams = "FLAGS INTERNALDATE RFC822.SIZE ENVELOPE BODY";
        }
        Pattern fetchPattern = Pattern.compile("[a-zA-Z0-9\\.]+(\\[\\])?(\\[[a-zA-Z0-9\\.()\\- ]+\\])?");
        Matcher fetchMatcher = fetchPattern.matcher(fetchParams);
        List<String> fetchCommands = new ArrayList<String>();
        while (fetchMatcher.find()) {
            String newCommand = fetchMatcher.group(0);
            fetchCommands.add(newCommand);
            fetchParams = fetchParams.substring(newCommand.length()).trim();
            fetchMatcher = fetchPattern.matcher(fetchParams);
        }
        return fetchCommands;
    }

    public String processFetchCommand(String params, Message tempMessage) {
        String command = params;
        if (params.indexOf(" ") != -1) {
            command = params.substring(0, params.indexOf(" "));
            params = params.substring(params.indexOf(" ")).trim();
        }
        command = command.toUpperCase();
        if (command.equals("FLAGS")) {
            return "FLAGS (" + tempMessage.getFlags() + ")";
        } else if (command.equals("INTERNALDATE")) {
            return "INTERNALDATE \"" + tempMessage.getInternalDate() + "\"";
        } else if (command.equals("BODY")) {
            System.out.println(params);
            return "BODY(" + tempMessage.getBody() + ")";
        } else if (command.equals("RFC822")) {
            String temp = tempMessage.getHeaders() + "\r\n" + tempMessage.getText();
            return "RFC822 {" + temp.length() + "}\r\n" + temp + "\r\n";
        } else if (command.equals("RFC822.HEADER")) {
            String temp = tempMessage.getHeaders();
            return "RFC822 {" + (temp.length() + 2) + "}\r\n" + temp + "\r\n";
        } else if (command.equals("RFC822.SIZE")) {
            String temp = tempMessage.getHeaders() + "\r\n" + tempMessage.getText();
            return "RFC822.SIZE " + temp.length();
        } else if (command.equals("RFC822.TEXT")) {
            String temp = tempMessage.getText();
            return "RFC822.TEXT {" + (temp.length() + 2) + "}\r\n" + temp + "\r\n";
        } else if (command.equals("UID")) {
            return "UID " + tempMessage.getUID();
        } else if (command.equals("BODY[]") || command.equals("BODY.PEEK[]")) {
            String temp = tempMessage.getHeaders() + "\r\n" + tempMessage.getText();
            return "BODY[] {" + (temp.length() + 2) + "}\r\n" + temp + "\r\n";
        } else if (command.equals("BODY[HEADER]")) {
            String temp = tempMessage.getHeaders();
            return "BODY[HEADER] {" + (temp.length() + 2) + "}\r\n" + temp + "\r\n";
        } else if (command.equals("BODY[TEXT]")) {
            String temp = tempMessage.getText();
            return "BODY[TEXT] {" + (temp.length() + 2) + "}\r\n" + temp + "\r\n";
        } else if (command.equals("BODY[HEADER.FIELDS")) {
            params = params.replace("]", "");
            params = params.replace("(", "");
            params = params.replace(")", "");
            params = params.replace("\"", "");
            String output = "BODY[HEADER.FIELDS (";
            String[] bits = params.split(" ");
            for(String bit : bits)  {
                output += "\"" + bit.toUpperCase() + "\" ";
            }
            output = output.substring(0, output.length() - 1);
            output += ")] ";
            String temp = tempMessage.getHeaders(bits, false);
            output += "{" + (temp.length() + 2) + "}\r\n" + temp + "\r\n";
            return output;
        } else if (command.equals("BODY[HEADER.FIELDS.NOT")) {
            String output = command + params;
            params = params.replace("]", "");
            params = params.replace("(", "");
            params = params.replace(")", "");
            params = params.replace("\"", "");

            String[] bits = params.split(" ");
            String temp = tempMessage.getHeaders(bits, true);
            output += " {" + (temp.length() + 2) + "}\r\n" + temp + "\r\n";
            return output;
        } else if (command.equals("BODY.PEEK[TEXT]")) {
            String temp = tempMessage.getText();
            return "BODY[TEXT] {" + (temp.length() + 2) + "}\r\n" + temp;
        } else if (command.equals("BODY.PEEK[HEADER]")) {
            String temp = tempMessage.getHeaders();
            return "BODY[HEADER] {" + (temp.length() + 2) + "}\r\n" + temp + "\r\n";
        } else if (command.equals("BODY.PEEK[HEADER.FIELDS")) {
            params = params.replace("]", "");
            params = params.replace("(", "");
            params = params.replace(")", "");
            params = params.replace("\"", "");

            String output = "BODY[HEADER.FIELDS (";

            String[] bits = params.split(" ");
            for(String bit : bits)  {
                output += "\"" + bit.toUpperCase() + "\" ";
            }
            output = output.substring(0, output.length() - 1);
            output += ")] ";
            String temp = tempMessage.getHeaders(bits, false);
            output += "{" + (temp.length() + 2) + "}\r\n" + temp + "\r\n";
            return output;
        } else if (command.equals("BODY.PEEK[HEADER.FIELDS.NOT")) {
            // clean up the params...
            String output = "BODY[HEADER.FIELDS.NOT" + params;
            params = params.replace("]", "");
            params = params.replace("(", "");
            params = params.replace(")", "");
            params = params.replace("\"", "");

            String[] bits = params.split(" ");
            String temp = tempMessage.getHeaders(bits, true);
            output += " {" + (temp.length() + 2) + "}\r\n" + temp + "\r\n";
            return output;
        } else if (command.equals("BODYSTRUCTURE")) {
            return "BODYSTRUCTURE " + tempMessage.getBody();
        } else if (command.equals("ENVELOPE")) {
            return "ENVELOPE (" + tempMessage.getEnvelope() + ")";
        }
        return "";
    }
/*
    private List getMessagesBetweenSEQs(int startSeq, int endSeq) {
        List<Message> output = new ArrayList<Message>();
        Iterator iterator = this.messages.get(this.currentMailbox).iterator();
        while (iterator.hasNext()) {
            Message msg = (Message) iterator.next();
            if (startSeq > 0 && endSeq > 0) {
                // between
                if (startSeq >= msg.getSEQ() && endSeq <= msg.getSEQ()) {
                    output.add(msg);
                }
            } else if (startSeq > 0) {
                // gt startUid
                if (msg.getSEQ() >= startSeq) {
                    output.add(msg);
                }
            } else if (endSeq > 0) {
                // lt endUid
                if (msg.getSEQ() <= endSeq) {
                    output.add(msg);
                }
            } else {
                // all uids!
                output.add(msg);
            }
        }
        return output;
    }                                       */
    /*
    private Message getMessageBySEQ(int seq) {
        Iterator iterator = this.messages.get(this.currentMailbox).iterator();
        while (iterator.hasNext()) {
            Message temp = (Message) iterator.next();
            if (temp.getSEQ() == seq) {
                return temp;
            }
        }
        return null;
    }*/

    private List<Message> getMessagesBetweenUIDs(int startUid, int endUid) {
        List<Message> output = new ArrayList<Message>();
        for(Message msg : this.messages.get(this.currentMailbox))   {
            if (startUid > 0 && endUid > 0) {
                if (startUid <= msg.getUID() && endUid >= msg.getUID()) {
                    output.add(msg);
                }
            } else if (startUid > 0) {
                if (msg.getUID() >= startUid) {
                    output.add(msg);
                }
            } else if (endUid > 0) {
                if (msg.getUID() <= endUid) {
                    output.add(msg);
                }
            } else {
                output.add(msg);
            }
        }
        return output;
    }
    
    private Message getMessageByUID(int uid) {
        for(Message temp: this.messages.get(this.currentMailbox))   {
            if (temp.getUID() == uid) {
                return temp;
            }
        }
        return null;
    }

    private synchronized void println(String theString) {
        try {
            System.out.println(theString);
            this.out.print(theString + "\r\n");
            this.out.flush();
        }
        catch (Exception err) {
            if (theString.indexOf("LOGOUT") == -1 && theString.indexOf("BYE") == -1) {
                System.err.println("Tried to write: " + theString);
                err.printStackTrace();
            }
        }
    }
    private void flush() {
        try {
            this.out.flush();
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }
    public boolean isConnected() {
        return (this.client.isConnected());
    }
    public String getUsername() {
        return this.username;
	}
    private String [] UnpackParams(String params)  {
        params = params.replace("\"", "");
        params = params.replace("(", "");
        params = params.replace(")", "");
        return params.split(" ");
    }
}
