/**
 *
 * @author Marc Badrian
 * @author John Shrein
 * @author Mohammad Shamim
 * 
 * COMP 7120 - Final Project
 * Area 51 - A Secure File Management System
 * Due: 4-28-2015
 * 
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.*;

// Main Class
public class Login {
	public static void main(String[] args) throws NoSuchAlgorithmException {
		// Creates new JFrame with size = (300, 150
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new LoginUI("User Login and Registration", 355, 175);
	}
}

// UI for Login and Registration
class LoginUI extends JFrame {

	private static final long serialVersionUID = 1L;

	JFrame windowRef = this;
	
	String[] aboutMessage = {
			"Area 51 Secure Filesystem Manager",
			"Marc Badrian, Mohammad Shamim, John Shrein",
			"COMP7120 - Cryptography"
	};
	
	// Create swing components required for this JFrame
	JLabel loginUsernameLabel;
	JLabel loginUpasswordLabel;
	JLabel loginPrompt;
	
	JTextField loginUusernameText;
	JPasswordField loginPasswordText;
	
	JButton loginButton;
	JButton loginExit;
	JButton loadSystem;
	
	// Tab container for different sections
	JTabbedPane mainTab;
	
	JPanel loginPanel;
	JPanel loginPanelUser;
	JPanel loginPanelPassword;
	
	JPanel aboutPanel;
	
	MessageDigest digest;
	
	boolean isAdmin = false;
	boolean isValidUser = false;
	String currentUser;
	
	// Declare actionlisteners for login and register buttons
	ActionListener loginButtonListener;
	ActionListener registerButtonListener;
	ActionListener exitButtonListener;
	
	public LoginUI(String title, int width, int height) throws NoSuchAlgorithmException
	{
		// Set JFrame options
		this.setLayout(null);
		setTitle(title);
		setSize(width, height);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// Setup components
		loginPrompt = new JLabel("Enter username and password");
		
		loginUsernameLabel = new JLabel("Username");
		loginUpasswordLabel = new JLabel("Password");
		loginUusernameText = new JTextField(15);
		loginPasswordText = new JPasswordField(15);
		
		loginButton = new JButton("Login");
		
		// Setup each panel
		loginPanelUser = new JPanel(new BorderLayout());
		loginPanelUser.setPreferredSize(new Dimension(300, 20));
		loginPanelUser.add(loginUsernameLabel, "Center");
		loginPanelUser.add(loginUusernameText, "East");
		
		loginPanelPassword = new JPanel(new BorderLayout());
		loginPanelPassword.setPreferredSize(new Dimension(300, 20));
		loginPanelPassword.add(loginUpasswordLabel, "Center");
		loginPanelPassword.add(loginPasswordText, "East");
		
		loginPanel = new JPanel(new FlowLayout());
		loginPanel.setSize(300, 180);
		loginPanel.add(loginPrompt);
		loginPanel.add(loginPanelUser);
		loginPanel.add(loginPanelPassword);
		loginPanel.add(loginButton);

		
		this.getRootPane().setDefaultButton(loginButton);
		loginButton.requestFocus();
		
		aboutPanel = new JPanel(new FlowLayout());
		for(int i = 0; i < aboutMessage.length; i++) {
			aboutPanel.add(new JLabel(aboutMessage[i]));
		}
		
		mainTab = new JTabbedPane();
		mainTab.setSize(350, 150);
		mainTab.add("Login", loginPanel);
		mainTab.add("About", aboutPanel);
		
		// Add main tab to JFrame
		add(mainTab, "Center");
		
		// Instantiate SHA-256 hash digest obj
		digest = MessageDigest.getInstance("SHA-256");
		
		// Make the Login interface visible
		setVisible(true);

		// Handle login button clicks
		loginButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				String username = loginUusernameText.getText();
				char[] password = loginPasswordText.getPassword();
				
				currentUser = username;
				
				for(int i = 0; i < password.length; i++)
				{
					digest.update((byte)password[i]);
				}
				
				byte[] passwordHashBytes = digest.digest();
				
				String passwordHashString = toHexString(passwordHashBytes);
				
				String credentials = username + ':' + passwordHashString;
				
				// Set text fields back to blank after processing input
				loginUusernameText.setText("");
				loginPasswordText.setText("");

				try {
					RandomAccessFile passwords = new RandomAccessFile("passwd.txt", "rw");
					
					while ((passwords.getFilePointer()) != (passwords.length())) {
						String storedCredentials = passwords.readLine();
						if (storedCredentials.equals(credentials)) {
							isValidUser = true;
							break;
						}
					}
					passwords.close();
				} catch (FileNotFoundException e0) {
					System.out.println("ERROR: File not found.");
					e0.printStackTrace();
				} catch (IOException e1) {
					System.out.println("ERROR: Unable to access file");
					e1.printStackTrace();
				}
				
				if(isValidUser)
				{
					// Display success message and start the Filesystem app
					JOptionPane.showMessageDialog(null, "Login Successful!");
					
					if(currentUser.equals("admin"))
					{
						isAdmin = true;
					}
					
					hideWindow();
				}
				else
				{
					// Display failure message
					JOptionPane.showMessageDialog(null, "Invalid username/password combination");
					currentUser = "";
				}
			}
		});
	}
	
	/**
	 * Convert byte array to a hexadecimal string
	 * @param characters
	 * @return Hex string representation of input
	 */
	private String toHexString(byte[] characters)
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
	
	/**
	 * Hides window after user logs in
	 * @param void
	 * @return void
	 */
	public void hideWindow()
	{
		this.setVisible(false);
		try {
			new Area51UI(currentUser).start(); 
		} catch (NoSuchAlgorithmException a) {
			System.out.println("ERROR: NoSuchAlgorithmException.");
			a.printStackTrace();
		}
	}
}