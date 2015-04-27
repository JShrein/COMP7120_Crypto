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

import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import java.security.Key;

public class TestAES{
	
	public static void main(String args[]) throws IOException, NoSuchAlgorithmException{
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Area51UI("default").start();
	}	
}
 
/**
 *	User Interface for AES File Encryption/Decryption
 *	@author Wong Yat Seng
 */
class Area51UI extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	
	private static final String ADD_FOLDER = "ADD_FOLDER";
	private static final String DELETE_FILE = "DELETE_FILE";
	private static final String DISPLAY = "DISPLAY";
	private static final String ADD_FILE = "ADD_FILE";
	private static final String SEARCH = "SEARCH";
	private static final String CHECK = "CHECK";
	private static final String REGISTER = "REGISTER";
	private static final String LOGOUT = "LOGOUT";
	
	private Area51UI frameReference;

	//declare form UI controls
	private JTextField txtSearch;		// Field for user to input a file name (this will need to change to implement the file content checking)
		
	private A51FileTree fileTree;
	
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
    // wrap a scroll pane around it
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
		btnLogout.setActionCommand(LOGOUT);
		btnLogout.setMnemonic(KeyEvent.VK_X);
		
		// ITEM: Register Button
		// PURPOSE: Allows admin to register a new user
		// Contained in pnlReg
		btnReg = new JButton("Register");
		btnReg.setPreferredSize(new Dimension(100,20));
		btnReg.addActionListener(this);
		btnReg.setActionCommand(REGISTER);
		btnReg.setMnemonic(KeyEvent.VK_R);
		
		// ITEM: Add Button
		// PURPOSE: Encrypts and adds new file to system
		// Contained in pnlMainRow1
		btnAdd = new JButton("Add");
		btnAdd.setPreferredSize(new Dimension(80,20));
		btnAdd.addActionListener(this);
		btnAdd.setActionCommand(ADD_FILE);
		btnAdd.setMnemonic(KeyEvent.VK_A);
		
		// ITEM: Display Button
		// PURPOSE: Decrypts selected file and displays text
		// Contained in pnlMainRow1
		btnDisplay = new JButton("Display");
		btnDisplay.setPreferredSize(new Dimension(100,20));
		btnDisplay.addActionListener(this);
		btnDisplay.setActionCommand(DISPLAY);
		btnDisplay.setMnemonic(KeyEvent.VK_D);
		
		// ITEM: Check Button
		// PURPOSE: Extracts file from associated text field and checks contents against stored files 
		// Contained in pnlMain
		btnCheck = new JButton("Check");
		btnCheck.setPreferredSize(new Dimension(80,20));
		btnCheck.addActionListener(this);
		btnCheck.setActionCommand(CHECK);
		btnCheck.setMnemonic(KeyEvent.VK_L);
		
		// ITEM: Search Button
		// PURPOSE: Extracts filename from associated text field and checks against stored filename 
		// Contained in pnlMain
		btnSearch = new JButton("Search");
		btnSearch.setPreferredSize(new Dimension(80,20));
		btnSearch.addActionListener(this);
		btnSearch.setActionCommand(SEARCH);
		btnSearch.setMnemonic(KeyEvent.VK_C);
		
		// Configure Delete button for removing encrypted files (and their key)
		// Contained in pnlMainRow2
		btnDelete = new JButton("Delete");
		btnDelete.setPreferredSize(new Dimension(80,20));
		btnDelete.addActionListener(this);
		btnDelete.setActionCommand(DELETE_FILE);
		btnDelete.setMnemonic(KeyEvent.VK_E);
		
		
		// Configure file list tree
		fileTree = new A51FileTree(userPathFile);
		populateTree(fileTree);
		fileTree.tree.addMouseListener(new FileTreeMouseListener());
		
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

		// Instantiate SHA-256 hash digest object
		digest = MessageDigest.getInstance("SHA-256");
		
		// Set main JFrame (this) options
		setSize(360,600);
		setLocation(100,100);
		getContentPane().add(tab,"Center");
		
		setTitle("AREA 51 - Secure File System");		
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		setResizable(false);		
		
		frameReference = this;
	}
	
	
	/**
	 *	Starts FileEncryptUI
	 */
	public void start(){
		setVisible(true);	
	}

	/**
	 *	Receive and process user interactions
	 *  Note: source may be JButton OR JMenuItem
	 *  (May be able to avoid cast to source type by using action commands exclusively)
	 *	@param e: Sender of the event
	 */
	public void actionPerformed(ActionEvent e){
		
		String actionCommand = e.getActionCommand();
		
		JButton btn = null;
		JMenuItem mItem = null;
		if(e.getSource() instanceof JButton)
		{
			btn = (JButton)e.getSource();
		}
		if(e.getSource() instanceof JMenuItem)
		{
			mItem = (JMenuItem)e.getSource();
		}
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// PURPOSE: Add a file into the system. 
		// 			If a file with the same file already exists in the system, give an error.
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (actionCommand.equals(ADD_FOLDER)){
			
			TreePath selectedPath = fileTree.tree.getSelectionPath();
			File selectedFile = extractSelectedFileFromTree();
			
			if(selectedFile.isDirectory())
			{
				File newFolder = new File(selectedFile + "/New Folder");

				boolean isCreated = newFolder.mkdir();
				
				if(isCreated)
				{
					fileTree.addObject(newFolder);
				}
			}
			
			System.out.println("Sorry, this doesn't fully work just yet");
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// PURPOSE: Add a file into the system. 
		// 			If a file with the same file already exists in the system, give an error.
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (actionCommand.equals(ADD_FILE)){
			
			// Get the file to encrypt
			String desktop = System.getProperty("user.home") + "/Desktop";
			File file = getFileDialogOpen("*.*", desktop);
			System.out.println(desktop);
			
			if (file==null)	
				return;
			
			String fileName = file.getName();
			
			// Update file tree
			TreePath selectedPath = fileTree.tree.getSelectionPath();
        	DefaultMutableTreeNode node = (DefaultMutableTreeNode)selectedPath.getLastPathComponent();
        	File selectedFile = (File)node.getUserObject();
        	System.out.println(selectedFile.toString());
      
        	
        	// MUST make sure file tree model matches files on disk
        	// If selected path is not a directory then get the file's parent path to add at same hierarchy 
        	// 	   level as selected file
        	if(!selectedFile.isDirectory())
        	{
        		// Set path to path's parent (should be folder or null if root path)
        		selectedPath = selectedPath.getParentPath();
        		node = (DefaultMutableTreeNode)selectedPath.getLastPathComponent();
        		selectedFile = (File)node.getUserObject();
            	System.out.println(selectedFile.toString());
            	// If selectedPath is null or still not a directory, just set to users root folder
            	if(selectedFile == null || !(selectedFile.isDirectory()))
            	{
            		selectedFile = new File(userPath);
            	}
        	}
        	
        	File encryptedFilePath = new File(selectedFile + "/" + fileName);
			
			if(!encryptedFilePath.exists())
        	{
				// Add new file to tree model
				fileTree.addObject(encryptedFilePath);
			
				//open file and read data
				byte data[] = readByteFile(file);
				digest.reset();
				
				for(int i = 0; i < data.length; i++)
				{
					digest.update(data[i]);
				}
				
				byte[] fileDigestBytes = digest.digest();
				String fileDigest = toHashString(fileDigestBytes);
				
				//encrypt and save as new data and key as new files						
				data = AES.encrypt(data);
				Key key = AES.getKey();
				
				System.out.println(toHashString(key.getEncoded()));
				digest.reset();
				
				for(int i = 0; i < data.length; i++)
				{
					digest.update(data[i]);
				}
				
				byte[] encFileDigestBytes = digest.digest();
				String encFileDigest = toHashString(encFileDigestBytes);
				
				byte[] keyBytes = key.getEncoded();
				String keyHexString = toHashString(keyBytes);
				
				String hashAndKey = fileDigest + ":" + encFileDigest + ":" + keyHexString + "\n";
				
				if (writeByteFile(encryptedFilePath + "", data)){
			
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
		if (actionCommand.equals(DELETE_FILE)) {
			
			// Get the selected file from the tree model
			/*TreePath selectedPath = fileTree.tree.getSelectionPath();
        	DefaultMutableTreeNode node = (DefaultMutableTreeNode)selectedPath.getLastPathComponent();
        	File selectedFile = (File)node.getUserObject();*/
			File selectedFile = extractSelectedFileFromTree();
        	
        	// Don't try to delete unless we're sure it exists
        	if(selectedFile.exists())
        	{
        		// File exists, need to check several possibilities
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
		}
		
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// PURPOSE: Displays the plaintext contents of a file
		// NOTES: If the file does not exist, the system should give an error.
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (actionCommand.equals(DISPLAY)){			

			File decFile = extractSelectedFileFromTree();
			
						
			// Hash the selected file to find the key in keyfile.txt
			digest.reset();
			byte[] keyBytes = null;
			
			byte[] encryptedFileBytes = readByteFile(decFile);
			
			for(int i = 0; i < encryptedFileBytes.length; i++)
			{
				digest.update(encryptedFileBytes[i]);
			}
			
			String encryptedFileHash = toHashString(digest.digest());
			
			try {
				RandomAccessFile keyFile = new RandomAccessFile("keyfile.txt", "rw");
				
				while ((keyFile.getFilePointer()) != (keyFile.length())) {
					
					String storedFileHash = keyFile.readLine();
					String hash[] = storedFileHash.split(":");
					
					if (hash[1].equals(encryptedFileHash)) {
						keyBytes = toByteArray(hash[2]);
						break;
					}
				}
				keyFile.close();
				
			} catch (FileNotFoundException e0) {
				System.out.println("ERROR: File not found.");
				e0.printStackTrace();
			} catch (IOException e1) {
				System.out.println("ERROR: Unable to access file");
				e1.printStackTrace();
			}
			
			
			String path = decFile.getPath();
			
			// Get key back from bytes
			Key key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
			
			if (!isAdmin && !path.contains(currentUser)) {
				JOptionPane.showMessageDialog(null, "ERROR: You do not have access to this file!");
			} else {			
			//get encrypted file and key
			if (!decFile.exists()){
				JOptionPane.showMessageDialog(null,
					"Encrypted file not found or cannot be accessed.",
					"Error",JOptionPane.ERROR_MESSAGE);
					return;
			}
			
			//use key to decrypt data
			byte data[] = readByteFile(decFile);
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
		if (actionCommand.equals(SEARCH)){
			
			String filename = txtSearch.getText();
			System.out.println(filename);
			txtSearch.setText("");
			//File userFolder = new File(userPath);
			//ArrayList<String> files = new ArrayList<String>(Arrays.asList(userFolder.list()));

        	DefaultMutableTreeNode node = fileTree.rootNode;
        	Enumeration<DefaultMutableTreeNode> filesInTree = node.breadthFirstEnumeration();
			
        	
			// Set text box back to default message
			txtSearch.setText("You can search for a file here");
			txtSearch.setForeground(Color.gray);
			
			boolean foundFile = false;
			
			while(filesInTree.hasMoreElements())
			{
				//String currentFile = files.get(i);
				DefaultMutableTreeNode currentNode = filesInTree.nextElement();
				File currentFile = (File)currentNode.getUserObject();
				if(currentFile.getName().equals(filename))
				{	
					JOptionPane.showMessageDialog(null, "This file is in the system");
					foundFile = true;
					//fileTree.tree.setSelectionPath(currentNode.getParent());
					break;
				}
			}
			if(!foundFile)
			{
				JOptionPane.showMessageDialog(null, "ERROR: This file does not exist!");
			}
		}
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// PURPOSE: Register new users
		// NOTES: Only available to administrator
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (actionCommand.equals(REGISTER)) {
			
	    	String username = registerUsernameText.getText();
			char[] password = registerPasswordText.getPassword();			
			digest.reset();
			
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
		if (actionCommand.equals(CHECK)) {			
			boolean isSame = false;

			// Get the file to check
			String desktop = System.getProperty("user.home") + "/Desktop";
			File file = getFileDialogOpen("*.*", desktop);
			System.out.println(desktop);
						
			if (file==null)	
				return;
		
			//open file and read data
			byte data[] = readByteFile(file);
			digest.reset();
			
			for(int i = 0; i < data.length; i++)
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
		}
		
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// PURPOSE: Logs out the current user and launches login gui
		// NOTES: 
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (actionCommand.equals(LOGOUT)) {	
				backToLogin();
		}
	}
	
	/**
	 *	Populates file tree
	 *	@param treePanel - Panel container for JTree tree model
	 *	@return void
	 */
	public void populateTree(A51FileTree treePanel) {
    	
        DefaultMutableTreeNode parent = null;
 
        // root is users home folder (e.g. "./users/username/")
        File root = userPathFile;
        addFiles(parent, root);
    }
    
	/**
	 *	Recursively adds files to tree as nodes
	 *	@param parent node, if file.isDirectory, then do recursive call with parent = file
	 *	@param file represented by parent node
	 *	@return void
	 */
    public void addFiles(DefaultMutableTreeNode parent, File file) {
    	
    	DefaultMutableTreeNode currentNode;
    	File[] subFiles = file.listFiles();
    	for(int i = 0; i < subFiles.length; i++)
    	{
    		String fileName = subFiles[i].getName();
    		if(!(fileName.charAt(0) == '.')) {

    			System.out.println(fileName);
	    		if(subFiles[i].isDirectory())
	    		{
	    			currentNode = fileTree.addObject(parent, subFiles[i]);
	    			addFiles(currentNode, subFiles[i]);
	    		}
	    		else {
	    			fileTree.addObject(parent,subFiles[i]);
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
	
	/**
	 *	Performs cleanup actions before returning to LoginUI
	 *	@param void
	 *	@return void
	 */
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
	
	/**
	 *	Converts a hex string to a byte array
	 *	@param string containing hexadecimal characters
	 *	@return array of bytes represented by hex chars
	 */
	protected byte[] toByteArray(String hexString)
	{
		int len = hexString.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
	                             + Character.digit(hexString.charAt(i+1), 16));
	    }
	    return data;
	}
	
	protected class FileTreeMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent event) {
			if (SwingUtilities.isRightMouseButton(event)) {
				
				JPopupMenu contextMenu = new JPopupMenu();
				
				JMenuItem addFolder = new JMenuItem();
				addFolder.setText("Create New Folder");
				addFolder.setMnemonic(KeyEvent.VK_C);
				addFolder.setActionCommand(ADD_FOLDER);
				addFolder.addActionListener(frameReference);
				
				JMenuItem display = new JMenuItem();
				display.setText("Display Contents");
				display.setMnemonic(KeyEvent.VK_S);
				display.setActionCommand(DISPLAY);
				display.addActionListener(frameReference);
				
				JMenuItem delete = new JMenuItem();
				delete.setText("Delete File");
				delete.setMnemonic(KeyEvent.VK_D);
				delete.setActionCommand(DELETE_FILE);
				delete.addActionListener(frameReference);
				
				contextMenu.add(addFolder);
				contextMenu.addSeparator();
				contextMenu.add(display);
				contextMenu.addSeparator();
				contextMenu.add(delete);

		        int row = fileTree.tree.getClosestRowForLocation(event.getX(), event.getY());
		        fileTree.tree.setSelectionRow(row);
		        contextMenu.show(event.getComponent(), event.getX(), event.getY());
		        
		        
		    }
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
	
	/**
	 *	PURPOSE: Use user's selection to get the file from tree model
	 *	@param void
	 *	@return Selected file
	 */
	private File extractSelectedFileFromTree()
	{
		TreePath selectedPath = fileTree.tree.getSelectionPath();
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selectedPath.getLastPathComponent();
		File selectedFile = (File)selectedNode.getUserObject();
		return selectedFile;
	}
}