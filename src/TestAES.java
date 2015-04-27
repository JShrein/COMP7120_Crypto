/**
 *
 * @author Marc
 * @author John
 * @author Mo
 */

import java.io.*;
import java.security.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import java.security.Key;

public class TestAES{
	
	public static void main(String args[]) throws IOException, NoSuchAlgorithmException{
		new Area51UI("default").start();
	}	
}
 
/**
 *	User Interface for AES File Encryption/Decryption
 *	@author Wong Yat Seng
 */
class Area51UI extends JFrame implements ActionListener{

	//declare form UI controls
	private JTextField txtSearch;		// Field for user to input a file name (this will need to change to implement the file content checking)
	
	//private JList<File> fileList;		// List of all a user's files
	private DefaultListModel<File> fileListModel;	// List model provides functionality to the file list
	
	private A51FileTree fileTree;
	//private DefaultMutableTreeNode treeNode;
	//private DefaultTreeModel treeModel;
	//private JTree fileTree;
	
	private JButton btnAdd,				// Encrypt and add file to system
					btnDisplay,			// Decrypt and display file contents
					btnLogout,			// Logout user and launch login GUI
                    btnDelete,			// Delete selected file
					btnCheck,			// List user's files
					btnSearch,			// Check contents of file against stored files
					btnReg;				// Register new user

	private JScrollPane listScrollPane;		// Container for file list, enables scrolling

	private JTabbedPane tab;			// High level container to hold each major section (Main, Register, About)
	private JPanel pnlMain,				// IN: JFrame; USE: Main panel container
				   pnlMainRow1,			// IN: pnlMain; USE: top button row
				   pnlMainRow2,			// IN: pnlMain; USE: lower button row
				   pnlFileList,			// IN: pnlMain; USE: List sub-panel
				   pnlAbt,				// IN: pnlMain; USE: List sub-panel
	           	   pnlReg,
	           	   pnlRegUser,
	           	   pnlRegPassword,
	           	   pnlCheck;

	private AES AES;					// AES object; provides encryption/decryption functionality
	
	private String strAbout[] = {"Authors: ", "Marc Badrian, Mohammad Shamim, and John Shrein",
								 "Email: ", "area51@cryptoproject.com", 
								 "Reference: COMP7120 Project", 
								 "Area 51 Secure Filesystem Manager"};
	
	private String defaultCheckMessage = "You can search for a file here";
	
	
	
	// Registration labels
	private JLabel regUsernameLabel;
	private JLabel regPasswordLabel;
	private JLabel regPrompt;
	
	// Registration fields
	private JTextField registerUsernameText;
	private JPasswordField registerPasswordText;

	// Hash digest object for password/file hashing
	private MessageDigest digest;

	// User parameters
	boolean isAdmin = false;		// Is current user administrator?
	boolean isValidUser = false;	// Is current user valid user? (Is this needed??)
	String currentUser;				// User name of current user
	private String userPath;		// String form of user's home path
	private File userPathFile;
	
	 // create a JTextArea
    JTextArea textArea = new JTextArea(30, 50);
    // wrap a scrollpane around it
    JScrollPane scrollPane = new JScrollPane(textArea);
	
	/**
	 *	Default constructor to launch program
	 */
	public Area51UI(String user) throws NoSuchAlgorithmException{
		
		// Instantiate AES object for encryption/decryption functionality
		AES = new AES();
		
		// Setting current user from login information
		// Will allow for home directory and limiting user to their own folder
		// If user is admin, will enable "Register" panel
		currentUser = user;
		isAdmin = user.equals("admin");
		
		// Create user folder
		if(isAdmin)
		{
			userPath = "./users/";
			userPathFile = new File(userPath);
		}
		else
		{
			userPath = "./users/" + currentUser + "/";
			userPathFile = new File(userPath);
		}
		
		System.out.println(userPathFile.getName());
		boolean success = userPathFile.mkdir();
		if(success) {
			System.out.println("New folder created for " + currentUser);
		} else {
			System.out.println("No new folder created for " + currentUser);
		}
		
		// Field for user input of file for content checking
		txtSearch = new JTextField(defaultCheckMessage,30);
		txtSearch.setForeground(Color.gray);
		txtSearch.addFocusListener(new FocusListener() {
			
			// Define what actions to take if field is selected
			@Override
			public void focusGained(FocusEvent e) {
				
				txtSearch.setForeground(Color.black);
				
				// If selected and text == default message, clear text (Will not erase partial input)
				if(txtSearch.getText().equals(defaultCheckMessage)) {
					txtSearch.setText("");					
				}
			}

			// Define what actions to take if filed is de-selected
			// ***NOTE*** Must be careful because changes here may preceed desired changes
			@Override
			public void focusLost(FocusEvent e) {
				// If de-selected and field is empty, reset to default message
				if(txtSearch.getText().length() == 0)
				{
					txtSearch.setText(defaultCheckMessage);
					txtSearch.setForeground(Color.gray);
				}
			}
		});

		// ITEM: Logout Button
		// PURPOSE: Logs out user and returns to Login GUI
		// Contained in btnMainRow2
		btnLogout = new JButton("Logout");
		btnLogout.setPreferredSize(new Dimension(80,20));
		btnLogout.addActionListener(this);
		btnLogout.setMnemonic(KeyEvent.VK_X);
		
		// ITEM: Register Button
		// PURPOSE: Allows admin to register a new user
		// Contained in pnlReg
		btnReg = new JButton("Register");
		btnReg.setPreferredSize(new Dimension(80,20));
		btnReg.addActionListener(this);
		btnReg.setMnemonic(KeyEvent.VK_R);
		
		// ITEM: Add Button
		// PURPOSE: Encrypts and adds new file to system
		// Contained in pnlMainRow1
		btnAdd = new JButton("Add");
		btnAdd.setPreferredSize(new Dimension(80,20));
		btnAdd.addActionListener(this);
		btnAdd.setMnemonic(KeyEvent.VK_A);
		
		// ITEM: Display Button
		// PURPOSE: Decrypts selected file and displays text
		// Contained in pnlMainRow1
		btnDisplay = new JButton("Display");
		btnDisplay.setPreferredSize(new Dimension(80,20));
		btnDisplay.addActionListener(this);
		btnDisplay.setMnemonic(KeyEvent.VK_D);
		
		// ITEM: List Button
		// PURPOSE: Displays a list of user's files
		// Contained in pnlMain
		btnCheck = new JButton("Check");
		btnCheck.setPreferredSize(new Dimension(80,20));
		btnCheck.addActionListener(this);
		btnCheck.setMnemonic(KeyEvent.VK_L);
		
		// ITEM: Check Button
		// PURPOSE: Extracts file from associated text field and checks contents against stored files 
		// Contained in pnlMain
		btnSearch = new JButton("Search");
		btnSearch.setPreferredSize(new Dimension(80,20));
		btnSearch.addActionListener(this);
		btnSearch.setMnemonic(KeyEvent.VK_C);
		
		// Configure Delete button for removing encrypted files (and their key)
		// Contained in pnlMainRow2
		btnDelete = new JButton("Delete");
		btnDelete.setPreferredSize(new Dimension(80,20));
		btnDelete.addActionListener(this);
		btnDelete.setMnemonic(KeyEvent.VK_E);
		
		
		// Configure file list tree
		fileTree = new A51FileTree(userPathFile);
		populateTree(fileTree);
		
		// ITEM: Main Panel Row 1
		// PURPOSE: Contains Top Row of Buttons (Add, List, Display)
		pnlMainRow1 = new JPanel(new FlowLayout());
		pnlMainRow1.setPreferredSize(new Dimension(300,25));
		pnlMainRow1.setBackground(new Color(0,0,0,0));
		pnlMainRow1.add(btnAdd);
		pnlMainRow1.add(btnCheck);
		pnlMainRow1.add(btnDisplay);
		
		// ITEM: Main Panel Row 2
		// PURPOSE: Contains Bottom Row of Buttons (Delete, Logout)
		pnlMainRow2 = new JPanel(new FlowLayout());
		pnlMainRow2.setPreferredSize(new Dimension(300,25));
		pnlMainRow2.setBackground(new Color(0,0,0,0));
		pnlMainRow2.add(btnDelete);
		pnlMainRow2.add(btnLogout);
				
		pnlCheck = new JPanel(new BorderLayout());
		pnlCheck.setPreferredSize(new Dimension(300,25));
		pnlCheck.setBackground(new Color(0,0,0,0));
		pnlCheck.add(txtSearch, "Center");
		pnlCheck.add(btnSearch, "East");
		
		// ITEM: Scroll pane container
		// PURPOSE: Contains other JComponents to allow vertical and horizontal scrolling
		//listScrollPane = new JScrollPane(fileList);
		listScrollPane = new JScrollPane(fileTree);
		listScrollPane.setPreferredSize(new Dimension(300, 300));
		
		pnlFileList = new JPanel(new BorderLayout());
		pnlFileList.setPreferredSize(new Dimension(300,300));
		pnlFileList.setBackground(new Color(0,0,0,0));
		pnlFileList.add(new JLabel("Output: "),"North");
		pnlFileList.add(listScrollPane, "Center");
		
		// ITEM: Main Panel
		// PURPOSE: Contains all main system components
		pnlMain = new JPanel(new FlowLayout());		
		pnlMain.setBackground(new Color(0,0,0,0));
		pnlMain.add(new JLabel("Welcome to AREA 51."));
		pnlMain.add(pnlMainRow1);
		pnlMain.add(pnlMainRow2);
		pnlMain.add(pnlFileList);
		pnlMain.add(pnlCheck);
		
		// ITEM: About Panel
		// PURPOSE: Contains information about program and developers
		pnlAbt = new JPanel(new FlowLayout());
		pnlAbt.setBackground(new Color(0,0,0,0));
		for (int i=0; i<strAbout.length; i++)
			pnlAbt.add(new JLabel(strAbout[i]));
		
		// Setup registration components
		regPrompt = new JLabel("Register new account");	
		regUsernameLabel = new JLabel("Username");
		regPasswordLabel = new JLabel("Password");
		registerUsernameText = new JTextField(15);
		registerPasswordText = new JPasswordField(15);

		// User registration panels
		pnlRegUser = new JPanel(new BorderLayout());
		pnlRegUser.setPreferredSize(new Dimension(300, 20));
		pnlRegUser.add(regUsernameLabel, "Center");
		pnlRegUser.add(registerUsernameText, "East");
		
		pnlRegPassword = new JPanel(new BorderLayout());
		pnlRegPassword.setPreferredSize(new Dimension(300, 20));
		pnlRegPassword.add(regPasswordLabel, "Center");
		pnlRegPassword.add(registerPasswordText, "East");
		
		// ITEM: Registration Panel
		// CONTAINS: User registration functionality
		pnlReg = new JPanel(new FlowLayout());
		pnlReg.setBackground(new Color(0,0,0,0));
		pnlReg.setSize(350, 180);
		pnlReg.add(regPrompt);
		pnlReg.add(pnlRegUser);
		pnlReg.add(pnlRegPassword);
		pnlReg.add(btnReg);
		
        // ITEM: Main tab pane
		// CONTAINS: All panels (Main, ?Register?, About)
		tab = new JTabbedPane();
		tab.setPreferredSize(new Dimension(310,150));
		tab.add("Main",pnlMain);
		
		if(user.equals("admin"))
		{
			tab.add("Register Users", pnlReg);
		}
		
		tab.add("About",pnlAbt);

		// Instantiate SHA-256 hash digest obj
		digest = MessageDigest.getInstance("SHA-256");
		
		// Set main JFrame (this) options
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
	}

	/**
	 *	Receive and process user interactions
	 *	@param e	Sender of the event
	 */
	public void actionPerformed(ActionEvent e){
		JButton btn = (JButton)e.getSource();
		

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// PURPOSE: Add a file into the system. 
		// 			If a file with the same file already exists in the system, give an error.
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (btn == btnAdd){
			
			// Get the file to encrypt
			String desktop = System.getProperty("user.home") + "/Desktop";
			File file = getFileDialogOpen("*.*", desktop);
			System.out.println(desktop);
			
			if (file==null)	
				return;
			
			String fileName = file.getName();
			//String encryptedFilePath = userPath + fileName;
			
			// Update file tree
			TreePath selectedPath = fileTree.tree.getSelectionPath();
        	DefaultMutableTreeNode node = (DefaultMutableTreeNode)selectedPath.getLastPathComponent();
        	File selectedFilePath = (File)node.getUserObject();
        	
        	// MUST make sure file tree model matches files on disk
        	// If selected path is not a directory then get the file's parent path to add at same hierarchy 
        	// 	   level as selected file
        	if(!selectedFilePath.isDirectory())
        	{
        		// Set path to path's parent (should be folder or null if root path)
        		selectedPath = selectedPath.getParentPath();
        		node = (DefaultMutableTreeNode)selectedPath.getLastPathComponent();
            	selectedFilePath = (File)node.getUserObject();
            	
            	// If selectedPath is null or still not a directory, just set to users root folder
            	if(selectedFilePath == null || !(selectedFilePath.isDirectory()))
            	{
            		selectedFilePath = new File(userPath);
            	}
        	} else {
        		// Set path to path's parent (should be folder or null if root path)
        		node = (DefaultMutableTreeNode)selectedPath.getLastPathComponent();
            	selectedFilePath = (File)node.getUserObject();
        	}
        	System.out.println(selectedFilePath);
        	System.out.println(selectedFilePath.getName());
        	
        	File encryptedFilePath = new File(selectedFilePath + "/" + fileName);
        	
        	System.out.println(encryptedFilePath);
			
			//if(!new File(encryptedFilePath).exists())
			if(!encryptedFilePath.exists())
        	{
				// Add new file to tree model
				fileTree.addObject(encryptedFilePath);
			
				//open file and read data
				//File file = new File(txtEncFile.getText());
				byte data[] = readByteFile(file);
				// Instantiate SHA-256 hash digest obj
				try {
					digest = MessageDigest.getInstance("SHA-256");
				} catch (NoSuchAlgorithmException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				for(int i = 0; i < data.length; i++)
				{
					digest.update(data[i]);
				}
				
				byte[] fileDigestBytes = digest.digest();
				String fileDigest = toHashString(fileDigestBytes);
				
				
				//encrypt and save as new data and key as new files						
				data = AES.encrypt(data);
				Key key = AES.getKey();				
				byte[] keyBytes = key.getEncoded();
				String keyHexString = toHashString(keyBytes);
				
				String hashAndKey = fileDigest + ":" + keyHexString + "\n";
				
				if (writeByteFile(encryptedFilePath + "", data) &&
					writeObjectFile(encryptedFilePath + ".key", key)){
			
					JOptionPane.showMessageDialog(null,
						"File successfully added to the system!",	
						"Done",JOptionPane.INFORMATION_MESSAGE);				
				}

				try {
					RandomAccessFile keyfile = new RandomAccessFile("keyfile.txt", "rw");
					
					keyfile.seek(keyfile.length());
					keyfile.writeBytes(hashAndKey);
					keyfile.close();

				} catch (FileNotFoundException e0) {
					System.out.println("ERROR: File not found.");
					e0.printStackTrace();
				} catch (IOException e1) {
					System.out.println("ERROR: Unable to access file");
					e1.printStackTrace();
				}
			} 
			else {
				JOptionPane.showMessageDialog(null,
					"ERROR: File already exists!",	
					"FILE ENCRYPTION WARNING",JOptionPane.WARNING_MESSAGE);
			}
			
			
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// PURPOSE: Remove a file from the system
		// NOTES: Key file is inferred from user selection and also deleted
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (btn == btnDelete) {
			
			// Get the selected file from the tree model
			TreePath selectedPath = fileTree.tree.getSelectionPath();
        	DefaultMutableTreeNode node = (DefaultMutableTreeNode)selectedPath.getLastPathComponent();
        	File selectedFile = (File)node.getUserObject();
        	
        	// Don't try to delete unless we're sure it exists
        	if(selectedFile.exists())
        	{
        		// File exists, need to check several possibilities
        		// Need to delete the associated key file implicitly
        		File associatedKeyFile = new File(selectedFile + ".key");
        		
        		if(!selectedFile.isDirectory())
            	{
        			// TYPICAL CASE: File exists and is NOT a directory,
        			// Confirm deletion and proceed
        			int reply = JOptionPane.showConfirmDialog(
        					null, 
        					"Are you sure you would like to permanently delete this file? ", 
        					"Delete file", JOptionPane.YES_NO_OPTION
        			);
        			
        			if(reply == JOptionPane.YES_OPTION)
        			{
        				selectedFile.delete();
        				fileTree.removeCurrentNode();
        				if(associatedKeyFile.exists())
        				{
        					associatedKeyFile.delete();
        				}
        			}
            	}
            	else if(selectedFile.isDirectory() && (selectedFile.listFiles()).length > 1)
            	{
            		System.out.println(selectedFile.listFiles().length);
            		for(int i = 0; i < selectedFile.listFiles().length; i++)
            		{
            			System.out.println(selectedFile.listFiles()[i]);
            		}
            		// Error: Directory is not empty, do not delete
            		JOptionPane.showMessageDialog(
            				null,
        					"ERROR: Directory is not empty!",	
        					"FILE DELETION WARNING",JOptionPane.WARNING_MESSAGE
        			);
            	}
            	else if(selectedFile.isDirectory())
            	{
            		// OK: Is directory && is empty
        			// Confirm deletion and proceed
        			int reply = JOptionPane.showConfirmDialog(
        					null, 
        					"Are you sure you would like to permanently delete this folder? ", 
        					"Delete folder", JOptionPane.YES_NO_OPTION
        			);
        			
        			if(reply == JOptionPane.YES_OPTION)
        			{
        				selectedFile.delete();
        				fileTree.removeCurrentNode();
        			}
            	}
            	else
            	{
            		// Error: Unknown error: If path was a file, a folder with files, or empty folder, then it 
            		// 		would have been handled, so give general error
            		JOptionPane.showMessageDialog(
            				null,
        					"ERROR: Unable to delete file!",	
        					"FILE DELETION WARNING",JOptionPane.WARNING_MESSAGE
        			);
            	}
        	}
        	
        	
			//clear output text
			//list.setText(null);
			// USE fileListModel.clear(); to clear new list
			
			// If selected index is -1 then nothing is selected, so return
			//if(fileList.getSelectedIndex() == -1)
			//	return;
			
			//File selectedFile = (File)fileList.getSelectedValue();
			
			//if(selectedFile == null) 
			//{
			//	System.out.println("ERROR: File is null");
			//	return;
			//}
			
			//File keyFile = new File(selectedFile.getAbsolutePath() + ".key");
			
			//remove file
			
		}
		
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// PURPOSE: Displays the plaintext contents of a file
		// NOTES: If the file does not exist, the system should give an error.
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (btn == btnDisplay){			
			
			// Get the selected file from the tree model
			TreePath selectedPath = fileTree.tree.getSelectionPath();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)selectedPath.getLastPathComponent();
			File decFile = (File)node.getUserObject();
			System.out.println(decFile);
						
			String path = decFile.getPath();
			String keyPath = path + ".key";
			System.out.println(path);
			System.out.println(keyPath);
			if (!isAdmin && !path.contains(currentUser)) {
				JOptionPane.showMessageDialog(null, "ERROR: You do not have access to this file!");
			} else {
			File keyFile = new File(keyPath);
			
			//get encrypted file and key
			if (!decFile.exists()){
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
			byte data[] = readByteFile(decFile);
			Key key = (Key)readObjectFile(keyFile);
			data = AES.decrypt(data,key);

			//restore original file and remove encrypted file and key
				JOptionPane.showMessageDialog(null,
					"File sucessfully decrypted.",
					"Done",JOptionPane.INFORMATION_MESSAGE);
				String text = "";
				for (int i=0;i < data.length; i++) {
					char a = (char)data[i];
					text += a;
				}
				
			    textArea.setEditable(false);
			    textArea.setText(text);
			    // display them in a message dialog
			    JOptionPane.showMessageDialog(null, scrollPane);
			}
		}
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// PURPOSE: Check the contents of the input file against stored files
		// NOTES: 
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (btn == btnSearch) {
			
			String filename = txtSearch.getText();
			System.out.println(filename);
			txtSearch.setText("");
			File userFolder = new File(userPath);
			ArrayList<String> files = new ArrayList<String>(Arrays.asList(userFolder.list()));
			
			//clear output text
			//list.setText(null);
			// USE fileListModel.clear(); to clear new list
			
			// Set text box back to default message
			txtSearch.setText("You can search for a file here");
			txtSearch.setForeground(Color.gray);
			
			for(int i = 0; i < files.size(); i++)
			{
				String currentFile = files.get(i);
				if(currentFile.equals(filename))
				{	
					//list.append("This file exists in the system: " + "\n" + "\n");
					//list.append(files.get(i) + "\n");
					break;
				} else if (!currentFile.equals(filename) && i == (files.size() - 1)) {
					JOptionPane.showMessageDialog(null, "ERROR: This file does not exist!");
				}
			}
		}
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// PURPOSE: Register new users
		// NOTES: Only available to administrator
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (btn == btnReg) 
		{
	    	String username = registerUsernameText.getText();
			char[] password = registerPasswordText.getPassword();
			// Instantiate SHA-256 hash digest obj
			try {
				digest = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
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
		
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// PURPOSE: List all of the files in the users home folder
		// NOTES: List sub-folders also, which can be expanded, listing those files, etc.,
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if(btn == btnCheck) {			
			boolean isSame = false;

			// Get the file to check
			String desktop = System.getProperty("user.home") + "/Desktop";
			File file = getFileDialogOpen("*.*", desktop);
			System.out.println(desktop);
						
			if (file==null)	
				return;
		
			//open file and read data
			byte data[] = readByteFile(file);
			// Instantiate SHA-256 hash digest obj
			try {
				digest = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}			for(int i = 0; i < data.length; i++)
			{
				digest.update(data[i]);
			}
			
			byte[] dataHashBytes = digest.digest();
			String dataHash = toHashString(dataHashBytes);
			System.out.println(dataHash);
			
			try {
				RandomAccessFile checkFile = new RandomAccessFile("keyfile.txt", "rw");
				
				while ((checkFile.getFilePointer()) != (checkFile.length())) {
					String storedFileHash = checkFile.readLine();
					String hash[] = storedFileHash.split(":");
					System.out.println(hash[0]);
					if (hash[0].equals(dataHash)) {
						isSame = true;
						break;
					}
				}
				checkFile.close();
			} catch (FileNotFoundException e0) {
				System.out.println("ERROR: File not found.");
				e0.printStackTrace();
			} catch (IOException e1) {
				System.out.println("ERROR: Unable to access file");
				e1.printStackTrace();
			}
			
			
			if (isSame == true) {
				JOptionPane.showMessageDialog(null, "There is a file that exists in the system with the same content!");

			} else {
				JOptionPane.showMessageDialog(null, "No file exists in the system with the same content!");

			}
			/*
			//clear output text
			//list.setText(null);
			fileListModel.clear();
			
			File userFolder = new File(userPath);
			ArrayList<File> files = new ArrayList<File>(Arrays.asList(userFolder.listFiles()));
			//ArrayList<String> files = new ArrayList<String>(Arrays.asList(userFolder.list()));
			

			for(int i = 0; i < files.size(); i++) {
				File currentFile = files.get(i);
				String currentFileName = currentFile.getName();
				if(!(currentFileName.charAt(0) == '.') && !(currentFileName.substring(currentFileName.length() - 3, currentFileName.length())).equals("key"))
				{
					fileListModel.addElement(currentFile);
				}
			}
			*/
		}
		
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// PURPOSE: Logs out the current user and launches login gui
		// NOTES: 
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (btn == btnLogout){
				backToLogin();
		}
	}
	
	/*
	private void createTreeNodes(DefaultMutableTreeNode top) {
		DefaultMutableTreeNode folder = null;
		DefaultMutableTreeNode file = null;
		
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(userPathFile.listFiles()));
		
		for(int i = 0; i < files.size(); i++){
			if(files.get(i).isDirectory()) {
				folder = new DefaultMutableTreeNode(files.get(i));
				top.add(folder);
			}
			else if(files.get(i).isFile()) {
				file = new DefaultMutableTreeNode(files.get(i));
				
			}
		}
		
		folder =
	}
	*/
	
	public void populateTree(A51FileTree treePanel) {
    	
        DefaultMutableTreeNode parent = null;
 
        // root is users home folder (e.g. "./users/username/")
        File root = userPathFile;
        addFiles(parent, root);
    }
    
    // Recursively adds files
    public void addFiles(DefaultMutableTreeNode parent, File file) {
    	
    	DefaultMutableTreeNode currentNode;
    	File[] subFiles = file.listFiles();
    	for(int i = 0; i < subFiles.length; i++)
    	{
    		String fileName = subFiles[i].getName();
    		if(!(fileName.charAt(0) == '.')) {
    			try
    			{
		    		if (!fileName.substring(fileName.length() - 4, fileName.length()).equals(".key"))
					{
		    			System.out.println(fileName);
		    			System.out.println("is Key File? " + fileName.substring(fileName.length() - 4, fileName.length()).equals(".key"));
			    		if(subFiles[i].isDirectory())
			    		{
			    			currentNode = fileTree.addObject(parent, subFiles[i]);
			    			addFiles(currentNode, subFiles[i]);
			    		}
			    		else {
			    			fileTree.addObject(parent,subFiles[i]);
			    		}
					}
    			} catch(StringIndexOutOfBoundsException e) {
    				System.out.println("ERROR: String index out of bounds at: " + (fileName.length() - 4) + ", but must not be key file so adding it anyway");
    				if(subFiles[i].isDirectory())
		    		{
		    			currentNode = fileTree.addObject(parent, subFiles[i]);
		    			addFiles(currentNode, subFiles[i]);
		    		}
		    		else {
		    			fileTree.addObject(parent,subFiles[i]);
		    		}
    				e.printStackTrace();
    			}
    		}
    	}
    }
	
	/**
	 *	Allow user to select a file using an Open Dialog
	 *	@param Filter is used to limit displayed file types
	 *	@param directory is the starting directory in which the Open Dialog will start
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
	
	/**
	 *	Allow user to select a file using an Open Dialog
	 *	@param Filter is used to limit displayed file types
	 *	@return A valid file that the user has selected, or null
	 */
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
		isValidUser = false;
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
	
	/**
	 *	Converts a byte array to a hex string
	 *	@param data The bytes to be encrypted
	 *	@return A hexadecimal representation of the data
	 */
	protected String toHashString(byte[] data)
	{
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < data.length; i++) {
    		String hex = Integer.toHexString(0xff & data[i]);
   	     	if(hex.length() == 1) 
   	     		sb.append('0');
   	     	sb.append(hex);
    	}
		
		return sb.toString();
	}
}