/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//mo
//package Encryptor;

/**
 *
 * @author renu
 */
import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.StandardCopyOption;
import java.security.*;
//import java.nio.file.Path;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;

import java.security.Key;

public class TestAES{
	
	public static void main(String args[]) throws IOException, NoSuchAlgorithmException{
		new FileEncryptUI("default").start();
	}	
}
 
/**
 *	User Interface for AES File Encryption/Decryption
 *	@author Wong Yat Seng
 *	@version 1.0
 */
class FileEncryptUI extends JFrame implements ActionListener{

	//declare form UI controls
	private JTextField txtCheck;		//decrypt source key
	
	private JButton //btnEncBrw,			//browse encrypt source file
					//btnDecBrw,			//browse decrypt source file
					//btnKeyBrw,			//browse encrypt source key
					btnAdd,			//run encryption
					btnDisplay,
					//btnDecRun,			//run decryption
					btnExit,
                    //btnSend,
                    btnDelete,
					btnList,
					btnCheck,
					btnReg;			// List all files in user's folder
	//private JScrollPane outputScrollPane;
	private JScrollPane listScrollPane;
	//private JTextArea output;
	private JTextArea list;
	private JTabbedPane tab;
	private JPanel pnlEnc,				//main encryption panel
				   //pnlDec,				//main decryption panel
				   pnlEncRow1,
				   //pnlDecRow1,
				   //pnlDecRow2,
				   //pnlDecText,
				   pnlListText,
				   pnlAbt,
	               pnlHlp,
	           	   pnlReg,
	           	   pnlRegUser,
	           	   pnlRegPassword,
	           	   pnlCheck;
                   //pnlSend;
                       	private AES AES;					//AES object
	private String strAbout[] = {"Authors: ", "Marc Badrian, Mohammad Shamim, and John Shrein",
								 "Email: ", "area51@cryptoproject.com", "Reference: COMP7120 Project", 
								 "Area 51 Secure Filesystem Manager"};

	private String strHelp[] = {"This software is based on the AES",
								 "Reference: http://www.abc.com/",
								 "program/java,20279922"};
	
	private String userName;
	private String userPath;
	
	JFrame windowRef = this;
	
	// Create swing components required for this JFrame
	JLabel regUsernameLabel;
	JLabel regPasswordLabel;
	JLabel regPrompt;
	
	JTextField registerUsernameText;
	JPasswordField registerPasswordText;

	MessageDigest digest;

	boolean isAdmin = false;
	boolean isValidUser = false;
	String currentUser;
	
	/**
	 *	Default constructor to launch program
	 */
	public FileEncryptUI(String user) throws NoSuchAlgorithmException{
		
		AES = new AES();
		
		// encryption panel		
		//txtEncFile = new JTextField("",30);
		txtCheck = new JTextField("You can search for a file here",30);
		txtCheck.setForeground(Color.gray);
		txtCheck.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				txtCheck.setText("");
			}

			@Override
			public void focusLost(FocusEvent e) {
				//txtCheck.setText("You can search for a file here");
			}
		});

		
		btnExit = new JButton("Logout");
		btnExit.setPreferredSize(new Dimension(80,20));
		btnExit.addActionListener(this);
		btnExit.setMnemonic(KeyEvent.VK_X);
		
		btnReg = new JButton("Register");
		btnReg.setPreferredSize(new Dimension(80,20));
		btnReg.addActionListener(this);
		btnReg.setMnemonic(KeyEvent.VK_E);
		
		btnAdd = new JButton("Add");
		btnAdd.setPreferredSize(new Dimension(80,20));
		btnAdd.addActionListener(this);
		btnAdd.setMnemonic(KeyEvent.VK_E);
		
		btnDisplay = new JButton("Display");
		btnDisplay.setPreferredSize(new Dimension(80,20));
		btnDisplay.addActionListener(this);
		btnDisplay.setMnemonic(KeyEvent.VK_D);
		
		btnDelete = new JButton("Delete");
		btnDelete.setPreferredSize(new Dimension(80,20));
		btnDelete.addActionListener(this);
		btnDelete.setMnemonic(KeyEvent.VK_E);
		
		btnList = new JButton("List");
		btnList.setPreferredSize(new Dimension(80,20));
		btnList.addActionListener(this);
		btnList.setMnemonic(KeyEvent.VK_L);
		
		btnCheck = new JButton("Check");
		btnCheck.setPreferredSize(new Dimension(80,20));
		btnCheck.addActionListener(this);
		btnCheck.setMnemonic(KeyEvent.VK_L);
		
		pnlEncRow1 = new JPanel(new BorderLayout());
		pnlEncRow1.setPreferredSize(new Dimension(300,20));
		pnlEncRow1.setBackground(new Color(0,0,0,0));
				
		pnlCheck = new JPanel(new BorderLayout());
		pnlCheck.setPreferredSize(new Dimension(300,25));
		pnlCheck.setBackground(new Color(0,0,0,0));
		//pnlCheck.add(new JLabel("You can search for a file here:"), "North");
		//pnlCheck.add(Box.createVerticalStrut(20));
		pnlCheck.add(txtCheck, "Center");
		pnlCheck.add(btnCheck, "East");
		
		// File list panel
		list = new JTextArea(2,3);
		list.setEditable(false);
		listScrollPane = new JScrollPane(list);
		listScrollPane.setPreferredSize(new Dimension(300, 300));
		
		pnlListText = new JPanel(new BorderLayout());
		pnlListText.setPreferredSize(new Dimension(300,300));
		pnlListText.setBackground(new Color(0,0,0,0));
		pnlListText.add(new JLabel("Output: "),"North");
		pnlListText.add(listScrollPane, "Center");
				
		pnlEnc = new JPanel(new FlowLayout());		
		pnlEnc.setBackground(new Color(0,0,0,0));
		pnlEnc.add(new JLabel("Welcome to AREA 51."));
		pnlEnc.add(pnlEncRow1);
		pnlEnc.add(btnAdd);
		pnlEnc.add(btnList);
		pnlEnc.add(btnDisplay);
		pnlEnc.add(btnDelete);
		pnlEnc.add(btnExit);
		pnlEnc.add(pnlListText);
		pnlEnc.add(pnlCheck);
		
		//about panel
		pnlAbt = new JPanel(new FlowLayout());
		pnlAbt.setBackground(new Color(0,0,0,0));
		for (int i=0; i<strAbout.length; i++)
			pnlAbt.add(new JLabel(strAbout[i]));
		
		// Setup components
		regPrompt = new JLabel("Register new account");
				
		regUsernameLabel = new JLabel("Username");
		regPasswordLabel = new JLabel("Password");
		registerUsernameText = new JTextField(15);
		registerPasswordText = new JPasswordField(15);
		//btnReg = new JButton("Register");

		
		//register users panels
		pnlRegUser = new JPanel(new BorderLayout());
		pnlRegUser.setPreferredSize(new Dimension(300, 20));
		pnlRegUser.add(regUsernameLabel, "Center");
		pnlRegUser.add(registerUsernameText, "East");
		
		pnlRegPassword = new JPanel(new BorderLayout());
		pnlRegPassword.setPreferredSize(new Dimension(300, 20));
		pnlRegPassword.add(regPasswordLabel, "Center");
		pnlRegPassword.add(registerPasswordText, "East");
		
		pnlReg = new JPanel(new FlowLayout());
		pnlReg.setBackground(new Color(0,0,0,0));
		pnlReg.setSize(350, 180);
		pnlReg.add(regPrompt);
		pnlReg.add(pnlRegUser);
		pnlReg.add(pnlRegPassword);
		pnlReg.add(btnReg);
		
		// Instantiate SHA-256 hash digest obj
		digest = MessageDigest.getInstance("SHA-256");
		
		//help panel
		pnlHlp = new JPanel(new FlowLayout());
		pnlHlp.setBackground(new Color(0,0,0,0));
		for (int i=0; i<strHelp.length; i++)
			pnlHlp.add(new JLabel(strHelp[i]));
                
		/*//Send panel
            pnlSend = new JPanel(new FlowLayout());
            pnlSend.setPreferredSize(new Dimension(300, 20));
            pnlSend.setBackground(new Color(0, 0, 0, 0));

            btnSend = new JButton("SEND");
            btnSend.setPreferredSize(new Dimension(80, 20));
            btnSend.addActionListener(this);
            btnSend.setMnemonic(KeyEvent.VK_S);
            pnlSend.add(new JLabel("For Send a file"));
            pnlSend.add(btnSend);
		 */
		
         //main tab
		tab = new JTabbedPane();
		tab.setPreferredSize(new Dimension(310,150));
		tab.add("Main",pnlEnc);
		//tab.add("Decrypt",pnlDec);
		isAdmin = user.equals("admin");
		if(user.equals("admin"))
		{
			tab.add("Register Users", pnlReg);
		}
		tab.add("About",pnlAbt);
		//tab.add("Help",pnlHlp);
              //  tab.add("Send",pnlSend);
		
		// Setting user from login information
		// Will allow for home directory and limiting user to their own folder
		userName = user;
		
		//main frame
		setSize(360,600);
		setLocation(100,100);
		getContentPane().add(tab,"Center");
		
		setTitle("AREA 51 - Secure File System");		
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		setResizable(false);		
		
	}
	
	
	/**
	 *	Starts FileEncryptUI
	 */
	public void start(){
		setVisible(true);
		
		// Create user folder
		userPath = "./users/" + userName + "/";
		boolean success = new File(userPath).mkdirs();
		System.out.println(success);
		
	}

	/**
	 *	Receive and process user interactions
	 *	@param e	Sender of the event
	 */
	public void actionPerformed(ActionEvent e){
		JButton btn = (JButton)e.getSource();
		
		/*
		//browse for source file
		//if (btn == btnEncBrw){
		//	File file = getFileDialogOpen("*.*", userName);
		//	if (file==null)	return;
			//txtEncFile.setText(file.getAbsolutePath());
		//}

		//browse for encrypted files
		if (btn == btnDecBrw){
			File file = getFileDialogOpen("*.enc", userName);
			if (file==null)	return;
			txtDecFile.setText(file.getAbsolutePath());
		}

		//browse for encryption key
		if (btn == btnKeyBrw){
			File file = getFileDialogOpen("*.key", userName);
			if (file==null)	return;
			txtDecKey.setText(file.getAbsolutePath());
		}
*/
		
		
		//Add a file into the system. 
		//If a file with the same file already exists in the system, give an error.
		if (btn == btnAdd){
			
			//clear output text
			list.setText(null);
			
			//browse for source file
			String desktop = System.getProperty("user.home") + "/Desktop";
			File file = getFileDialogOpen("*.*", desktop);
			System.out.println(desktop);
			if (file==null)	return;
			//txtEncFile.setText(file.getAbsolutePath());
			
			//open file and read data
			//File file = new File(txtEncFile.getText());
			byte data[] = readByteFile(file);
			
			String fileName = file.getName();
			String encryptedFilePath = userPath + fileName;
			//File dest = new File(userPath + fileName);	
			
			
			//encrypt and save as new data and key as new files						
			data = AES.encrypt(data);
			if (writeByteFile(encryptedFilePath,data) &&
				writeObjectFile(encryptedFilePath + ".key",AES.getKey())){
				/*try{
					copyFile(file, dest);
				} catch (IOException i) {
					JOptionPane.showMessageDialog(null, "IOException",
							"Error",JOptionPane.ERROR_MESSAGE);
					}
				*/
				//remove old file
				/*int reply = JOptionPane.showConfirmDialog(null, "Would you like to Delete the source file? ", 
						"Delete source file", JOptionPane.YES_NO_OPTION);
				
				if(reply == JOptionPane.YES_OPTION)
				{
					file.delete();
				}
				*/
				JOptionPane.showMessageDialog(null,
					//"File encrypted as: " + file.getName() + ".enc\n" +
					//"Encryption key: " + file.getName() + ".key\n",
					"File successfully added to the system!",	
					"Done",JOptionPane.INFORMATION_MESSAGE);				
			}			
		}

		// delete a file
		if (btn == btnDelete) {
			
			//clear output text
			list.setText(null);
			
			//browse for file
			File file = getFileDialogOpen("*.*");
			if (file==null)	return;
			
			//remove file
			int reply = JOptionPane.showConfirmDialog(null, "Are you sure you would like to permanently delete this file? ", 
					"Delete source file", JOptionPane.YES_NO_OPTION);
			
			if(reply == JOptionPane.YES_OPTION)
			{
				file.delete();
			}
		}
		
		//if the system receives this command, it will display the content of the file 
		//with the filename on screen. If the file does not exist, the system should give an error.
		if (btn == btnDisplay){			
			
			//clear output text
			list.setText(null);
			
			//browse for file
			File file = getFileDialogOpen("*.*");
			if (file==null)	return;
			
			File encFile = new File((file).toString());
			String path = encFile.getAbsolutePath();
			if (!isAdmin && !path.contains(userName)) {
				JOptionPane.showMessageDialog(null, "ERROR: You do not have access to this file!");
			} else {
			File keyFile = new File(file + ".key");
			
			//get encrypted file and key
			if (!encFile.exists()){
				JOptionPane.showMessageDialog(null,
					"Encrypted file not found or cannot be accessed.",
					"Error",JOptionPane.ERROR_MESSAGE);
					return;
			}

			if (!keyFile.exists()){
				JOptionPane.showMessageDialog(null,				
					"Key file not found or cannot be accessed.",
					"Error",JOptionPane.ERROR_MESSAGE);
					return;
			}
			
			//use key to decrypt data
			byte data[] = readByteFile(encFile);
			Key key = (Key)readObjectFile(keyFile);
			data = AES.decrypt(data,key);

			//restore original file and remove encrypted file and key
			String filename = encFile.getAbsolutePath().
				substring(0,encFile.getAbsolutePath().length()-4);
			if (writeByteFile(filename,data)){
				JOptionPane.showMessageDialog(null,
					"File sucessfully decrypted.",
					"Done",JOptionPane.INFORMATION_MESSAGE);
				list.append(new String(encFile.getName() + " file contents: " + "\n" + "\n"));
				//for(int i = 0; i < data.length; i += 45)
				//{
					//output.append(new String(data).substring(i, i + 45) + "\n");
				//}
				int linePos = 0;
				for(int i = 0; i < data.length; i++)
				{
					if(linePos >= 50 && data[i] == 0x20)
					{
						list.append("\n");
						linePos = 0;
					}
					else
					{
						list.append((char)data[i] + "");
						linePos++;
					}
				}
			}
			}
		}
		
		// Check
		if (btn == btnCheck) {
			
			String filename = txtCheck.getText();
			System.out.println(filename);
			txtCheck.setText("");
			File userFolder = new File(userPath);
			ArrayList<String> files = new ArrayList<String>(Arrays.asList(userFolder.list()));
			
			//clear output text
			list.setText(null);
			
			// Set text box back to default message
			txtCheck.setText("You can search for a file here");
			
			for(int i = 0; i < files.size(); i++)
			{
				String currentFile = files.get(i);
				if(currentFile.equals(filename))
				{	
					list.append("This file exists in the system: " + "\n" + "\n");
					list.append(files.get(i) + "\n");
					break;
				} else if (!currentFile.equals(filename) && i == (files.size() - 1)) {
					JOptionPane.showMessageDialog(null, "ERROR: This file does not exist!");
				}
			}
		}
		
		// register users
		if (btn == btnReg) 
		{
	    	String username = registerUsernameText.getText();
			char[] password = registerPasswordText.getPassword();
			
			for(int i = 0; i < password.length; i++)
			{
				digest.update((byte)password[i]);
			}
			
			byte[] passwordHashBytes = digest.digest();
			String newPasswordHash = toHashString(passwordHashBytes);
			String credentials = username + ':' + newPasswordHash + "\n";

			
			if(password.length < 8 || username.length() < 1)
			{
				JOptionPane.showMessageDialog(null, "ERROR: username or password is invalid");
			}
			else
			{
				// Set text fields back to blank after processing input
				registerUsernameText.setText("");
				registerPasswordText.setText("");
				
		    	try {
					RandomAccessFile passwords = new RandomAccessFile("passwd.txt", "rw");
					
					passwords.seek(passwords.length());
					passwords.writeBytes(credentials);
					passwords.close();
					
					// Create user folder
					userPath = "./users/" + username + "/";
					boolean success = new File(userPath).mkdirs();
					System.out.println(success);
					
					JOptionPane.showMessageDialog(null, "Registration Successful");
					
				} catch (FileNotFoundException e0) {
					System.out.println("ERROR: File not found.");
					e0.printStackTrace();
				} catch (IOException e1) {
					System.out.println("ERROR: Unable to access file");
					e1.printStackTrace();
				}
			}
    	}
		
		/*
		//perform decryption
		if (btn == btnDecRun){			
			File file = new File(txtDecFile.getText());
			File keyFile = new File(txtDecKey.getText());
			
			//get encrypted file and key
			if (!file.exists()){
				JOptionPane.showMessageDialog(null,
					"Encrypted file not found or cannot be accessed.",
					"Error",JOptionPane.ERROR_MESSAGE);
					return;
			}

			if (!keyFile.exists()){
				JOptionPane.showMessageDialog(null,				
					"Key file not found or cannot be accessed.",
					"Error",JOptionPane.ERROR_MESSAGE);
					return;
			}
			
			//use key to decrypt data
			byte data[] = readByteFile(file);
			Key key = (Key)readObjectFile(keyFile);
			data = AES.decrypt(data,key);

			//restore original file and remove encrypted file and key
			String filename = file.getAbsolutePath().
				substring(0,file.getAbsolutePath().length()-4);
			if (writeByteFile(filename,data)){
				file.delete();
				keyFile.delete();
				JOptionPane.showMessageDialog(null,
					"File sucessfully decrypted.",
					"Done",JOptionPane.INFORMATION_MESSAGE);
				output.append(new String(filename + "\n" + "\n"));
				//for(int i = 0; i < data.length; i += 45)
				//{
					//output.append(new String(data).substring(i, i + 45) + "\n");
				//}
				int linePos = 0;
				for(int i = 0; i < data.length; i++)
				{
					if(linePos >= 50 && data[i] == 0x20)
					{
						output.append("\n");
						linePos = 0;
					}
					else
					{
						output.append((char)data[i] + "");
						linePos++;
					}
				}
			}			
			
		}
		*/
		if(btn == btnList) {
			
			//clear output text
			list.setText(null);
			
			// THIS LINE SHOULDN'T BE REQUIRED, BASICALLY RENAMING USERPATH TO USERFOLDER
			File userFolder = new File(userPath);
			ArrayList<File> filesList = new ArrayList<File>(Arrays.asList(userFolder.listFiles()));
			ArrayList<String> files = new ArrayList<String>(Arrays.asList(userFolder.list()));
			
			int folderLevel = 0;
			
			for(int i = 0; i < files.size(); i++)
			{
				// List only files that aren't .key files and that DONT begin with '.'
				String currentFile = files.get(i);
				if(!(currentFile.charAt(0) == '.') && !(currentFile.substring(currentFile.length() - 3, currentFile.length())).equals("key"))
				{
					list.append(files.get(i) + "\n");
				}
			}
			
			for(int i = 0; i < filesList.size(); i++)
			{
				File currentFile2 = filesList.get(i);
				String fileName = currentFile2.getName();
				if(!(fileName.charAt(0) == '.') && !(fileName.substring(fileName.length() - 3, fileName.length())).equals("key"))
				{
					if(currentFile2.isDirectory())
					{
						list.append(fileName);
						folderLevel++;
					}
				}
				
			}
		}
		
		//exit
		if (btn == btnExit){
				backToLogin();
		}
               // if(btn==btnSend)
               // {
               //     //SendMail.html;
               // }
	}
	
	/**
	 *	Allow user to select a file using an Open Dialog and return the file
	 *	@return A valid file that the user has selected, or null
	 */
	protected File getFileDialogOpen(String filter, String directory){
		FileDialog fd = new FileDialog(this,"Select File",FileDialog.LOAD);
		fd.setDirectory(directory);
		fd.setFile(filter);
		fd.setVisible(true);

		if (fd.getFile() == null) return null;
		File file = new File(fd.getDirectory()+fd.getFile());
		
		if (!file.canRead()){
			JOptionPane.showMessageDialog(null,
				"Selected file cannot be read.",
				"Error",JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return file;
	}
	
	protected File getFileDialogOpen(String filter){
		FileDialog fd = new FileDialog(this,"Select File",FileDialog.LOAD);
		fd.setDirectory(userPath);
		fd.setFile(filter);
		fd.setVisible(true);

		if (fd.getFile() == null) return null;
		File file = new File(fd.getDirectory()+fd.getFile());
		
		if (!file.canRead()){
			JOptionPane.showMessageDialog(null,
				"Selected file cannot be read.",
				"Error",JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return file;
	}
	
	/**
	 *	Reads a file and returns its contents as an array of bytes
	 *	@param	file	The file object to read from
	 *	@return The contents of the file as an array of bytes
	 */
	protected byte[] readByteFile(File file){		
		byte data[] = null;
		try{
			FileInputStream fis = new FileInputStream(file);			
			
			int c,i=0;
			data = new byte[(int)file.length()];
			while ((c = fis.read()) != -1)
				data[i++] = (byte)c;
			fis.close();
		}
		catch(IOException e){
			JOptionPane.showMessageDialog(null,
				file.getName() + " not found or cannot be read.",
				"Error",JOptionPane.ERROR_MESSAGE);
			return null;
		}				
		return data;
	}
	
	/**
	 *	Writes byte contents to a file
	 *	@param file	The file object to write to
	 *	@param data	The data (array of bytes) to write
	 *	@return Whether the process is successful
	 */
	protected boolean writeByteFile(String fileName, byte[] data){
		File file = new File(fileName);
		if (!file.canWrite()){
			try {file.createNewFile();}
			catch(IOException e){
				JOptionPane.showMessageDialog(null,
					"Unable to create file " + file.getName() + " for writing.",
					"Error",JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}		
				
		try{			
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.close();
			return true;
		}
		catch(IOException e){
			JOptionPane.showMessageDialog(null,
				"Unable to write to file " + file.getName(),
				"Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}	

	/**
	 *	Reads a file and returns its contents as an array of objects
	 *	@param	file	The file object to read from
	 *	@return The contents of the file as an array of objects
	 */
	protected Object readObjectFile(File file){		
		Object obj;
		try{
			ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(file));
		    obj = (Object)ois.readObject();
			ois.close();			
		}
		catch(IOException e){
			JOptionPane.showMessageDialog(null,
				file.getName() + " not found or cannot be read.",
				"Error",JOptionPane.ERROR_MESSAGE);
			return null;
		}
		catch(ClassNotFoundException e){
			JOptionPane.showMessageDialog(null,
				file.getName() + " does not contain a readable object.",
				"Error",JOptionPane.ERROR_MESSAGE);
			return null;
		}			
		return obj;
	}
	
	/**
	 *	Writes an object to a file
	 *	@param file	The file object to write to
	 *	@param data	The data (single instance of an object) to write
	 *	@return Whether the process is successful
	 */
	protected boolean writeObjectFile(String fileName, Object data){
		File file = new File(fileName);
		if (!file.canWrite()){
			try {file.createNewFile();}
			catch(IOException e){
				JOptionPane.showMessageDialog(null,
					"Unable to create file " + file.getName() + " for writing.",
					"Error",JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}		
				
		try{
			ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(file));
		    oos.writeObject(data);
			oos.close();
			return true;
		}
		catch(IOException e){
			JOptionPane.showMessageDialog(null,
				"Unable to write to file " + file.getName(),
				"Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	protected void backToLogin(){
		try {
		this.setVisible(false);
		//Login obj = new Login();
		new LoginUI("User Login and Registration", 355, 175);
		}
		catch(NoSuchAlgorithmException e){
			JOptionPane.showMessageDialog(null,
					"NoSuchAlgorithmException",
					"Error",JOptionPane.ERROR_MESSAGE);
			}
	}
	/*
	protected void copyFile(File source, File dest)
			throws IOException {
		Files.copy(source.toPath(), dest.toPath());
	}
	*/
	protected String toHashString(byte[] characters)
	{
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < characters.length; i++) {
    		String hex = Integer.toHexString(0xff & characters[i]);
   	     	if(hex.length() == 1) 
   	     		sb.append('0');
   	     	sb.append(hex);
    	}
		
		return sb.toString();
	}
}