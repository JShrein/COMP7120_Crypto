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
		new LoginUI("User Login and Registration", 355, 175);
	}
}

// UI for Login and Registration
class LoginUI extends JFrame {
	
	JFrame windowRef = this;
	
	String[] aboutMessage = {
			"Area 51 Secure Filesystem Manager",
			"Marc Badrian, Mohammad Shamim, John Shrein",
			"COMP7120 - Cryptography"
	};
	
	// Create swing components required for this JFrame
	JLabel loginUsernameLabel;
	JLabel loginUpasswordLabel;
	JLabel registerUsernameLabel;
	JLabel registerPasswordLabel;
	JLabel loginPrompt;
	JLabel registerPrompt;
	
	JTextField loginUusernameText;
	JTextField registerUusernameText;
	JPasswordField loginPasswordText;
	JPasswordField registerPasswordText;
	
	JButton loginButton;
	JButton registerButton;
	JButton loginExit;
	JButton registerExit;
	JButton loadSystem;
	
	// Tab container for different sections
	JTabbedPane mainTab;
	
	JPanel loginPanel;
	JPanel loginPanelUser;
	JPanel loginPanelPassword;
	
	JPanel registerPanel;
	JPanel registerPanelUser;
	JPanel registerPanelPassword;
	
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
		registerPrompt = new JLabel("Register new account");
		
		loginUsernameLabel = new JLabel("Username");
		loginUpasswordLabel = new JLabel("Password");
		loginUusernameText = new JTextField(15);
		loginPasswordText = new JPasswordField(15);
		
		registerUsernameLabel = new JLabel("Username");
		registerPasswordLabel = new JLabel("Password");
		registerUusernameText = new JTextField(15);
		registerPasswordText = new JPasswordField(15);
		
		loginButton = new JButton("Login");
		registerButton = new JButton("Register");
		loginExit = new JButton("Exit");
		registerExit = new JButton("Exit");
		loadSystem = new JButton("Start");
		
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
		loginPanel.add(loginExit);
		loginPanel.add(loadSystem);
		
		registerPanelUser = new JPanel(new BorderLayout());
		registerPanelUser.setPreferredSize(new Dimension(300, 20));
		registerPanelUser.add(registerUsernameLabel, "Center");
		registerPanelUser.add(registerUusernameText, "East");
		
		registerPanelPassword = new JPanel(new BorderLayout());
		registerPanelPassword.setPreferredSize(new Dimension(300, 20));
		registerPanelPassword.add(registerPasswordLabel, "Center");
		registerPanelPassword.add(registerPasswordText, "East");
		
		registerPanel = new JPanel(new FlowLayout());
		registerPanel.setSize(350, 180);
		registerPanel.add(registerPrompt);
		registerPanel.add(registerPanelUser);
		registerPanel.add(registerPanelPassword);
		registerPanel.add(registerButton);
		registerPanel.add(registerExit);
		
		
		
		aboutPanel = new JPanel(new FlowLayout());
		for(int i = 0; i < aboutMessage.length; i++) {
			aboutPanel.add(new JLabel(aboutMessage[i]));
		}
		
		mainTab = new JTabbedPane();
		mainTab.setSize(350, 150);
		mainTab.add("Login", loginPanel);
		mainTab.add("Register", registerPanel);
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
				
				String passwordHashString = toHashString(passwordHashBytes);
				
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
					
				}
				else
				{
					// Display failure message
					JOptionPane.showMessageDialog(null, "Invalid username/password combination");
					currentUser = "";
				}
			}
		});
		
		// Handle register button clicks
		registerButton.addActionListener(new ActionListener() {
			
		    @Override
			public void actionPerformed(ActionEvent e) {
		    	
		    	if(isAdmin)
		    	{
			    	String username = registerUusernameText.getText();
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
						registerUusernameText.setText("");
						registerPasswordText.setText("");
						
				    	try {
							RandomAccessFile passwords = new RandomAccessFile("passwd.txt", "rw");
							
							passwords.seek(passwords.length());
							passwords.writeBytes(credentials);
							passwords.close();
							
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
		    	else
		    	{
		    		JOptionPane.showMessageDialog(null, "ERROR: Only Administrator can create accounts!");
		    	}
		    }
		});
	
		loadSystem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(isValidUser)
				{
					hideWindow();
				}
				
			}
		});
		
		exitButtonListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		};
		
		// Handle exit button clicks
		loginExit.addActionListener(exitButtonListener);
		registerExit.addActionListener(exitButtonListener);
	}
	
	private String toHashString(byte[] characters)
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
	
	public void hideWindow()
	{
		this.setVisible(false);
		TestAES obj = new TestAES();
		new FileEncryptUI(currentUser).start(); 
	}
}