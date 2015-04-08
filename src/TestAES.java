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
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.security.Key;

public class TestAES{
	public static void main(String args[]){
		new FileEncryptUI().start();
	}	
}
 
/**
 *	User Interface for AES File Encryption/Decryption
 *	@author Wong Yat Seng
 *	@version 1.0
 */
class FileEncryptUI extends JFrame implements ActionListener{

	//declare form UI controls
	private JTextField txtEncFile,		//encrypt source file
					   txtDecFile,		//decrypt source file
					   txtDecKey;		//decrypt source key
	private JButton btnEncBrw,			//browse encrypt source file
					btnDecBrw,			//browse decrypt source file
					btnKeyBrw,			//browse encrypt source key
					btnEncRun,			//run encryption
					btnDecRun,			//run decryption
					btnExit,
                                        btnSend;
	private JTextArea output;
	private JTabbedPane tab;
	private JPanel pnlEnc,				//main encryption panel
				   pnlDec,				//main decryption panel
				   pnlEncRow1,
				   pnlDecRow1,
				   pnlDecRow2,
				   pnlDecText,
				   pnlAbt,
	               pnlHlp,
                       pnlSend,
                       pnlOutput;
                       	private AES AES;					//AES object
	private String strAbout[] = {"Author: ",
								 "Email: ",
								 "Reference: "};

	private String strHelp[] = {"This software is based on the AES",
								 "Reference: http://www.abc.com/",
								 "program/java,20279922"};
	/**
	 *	Default constructor to launch program
	 */
	public FileEncryptUI(){
		
		AES = new AES();
		
		// encryption panel		
		txtEncFile = new JTextField("",30);
		
		btnExit = new JButton("Exit");
		btnExit.setPreferredSize(new Dimension(80,20));
		btnExit.addActionListener(this);
		btnExit.setMnemonic(KeyEvent.VK_X);
		
		btnEncBrw = new JButton("...");
		btnEncBrw.addActionListener(this);
		
		btnEncRun = new JButton("Encrypt");
		btnEncRun.setPreferredSize(new Dimension(80,20));
		btnEncRun.addActionListener(this);
		btnEncRun.setMnemonic(KeyEvent.VK_E);
		
		pnlEncRow1 = new JPanel(new BorderLayout());
		pnlEncRow1.setPreferredSize(new Dimension(300,20));
		pnlEncRow1.setBackground(new Color(0,0,0,0));
		pnlEncRow1.add(new JLabel("File: "),"West");
		pnlEncRow1.add(txtEncFile,"Center");
		pnlEncRow1.add(btnEncBrw,"East");
		
		pnlEnc = new JPanel(new FlowLayout());		
		pnlEnc.setBackground(new Color(0,0,0,0));
		pnlEnc.add(new JLabel("Select a file to be encrypted"));
		pnlEnc.add(pnlEncRow1);
		pnlEnc.add(btnEncRun);
		pnlEnc.add(btnExit);

		// decryption panel
		txtDecFile = new JTextField("",30);
		txtDecKey = new JTextField("",30);
		output = new JTextArea(2,3);
		
		btnDecBrw = new JButton("...");
		btnDecBrw.addActionListener(this);
				
		btnKeyBrw = new JButton("...");
		btnKeyBrw.addActionListener(this);
		
		btnDecRun = new JButton("Decrypt");
		btnDecRun.setPreferredSize(new Dimension(80,20));
		btnDecRun.addActionListener(this);
		btnDecRun.setMnemonic(KeyEvent.VK_D);
		
		pnlDecRow1 = new JPanel(new BorderLayout());
		pnlDecRow1.setPreferredSize(new Dimension(300,20));
		pnlDecRow1.setBackground(new Color(0,0,0,0));
		pnlDecRow1.add(new JLabel("File: "),"West");
		pnlDecRow1.add(txtDecFile,"Center");
		pnlDecRow1.add(btnDecBrw,"East");

		pnlDecRow2 = new JPanel(new BorderLayout());
		pnlDecRow2.setPreferredSize(new Dimension(300,20));
		pnlDecRow2.setBackground(new Color(0,0,0,0));
		pnlDecRow2.add(new JLabel("Key: "),"West");
		pnlDecRow2.add(txtDecKey,"Center");
		pnlDecRow2.add(btnKeyBrw,"East");
		
		pnlDecText = new JPanel(new BorderLayout());
		pnlDecText.setPreferredSize(new Dimension(300,300));
		pnlDecText.setBackground(new Color(0,0,0,0));
		pnlDecText.add(new JLabel("Decrypted Output: "),"South");
		pnlDecText.add(output,"Center");
		//pnlDecText.add(btnKeyBrw,"East");
		
		pnlDec = new JPanel(new FlowLayout());		
		pnlDec.setBackground(new Color(0,0,0,0));
		pnlDec.add(new JLabel("Select file to decrypt and decryption key"));
		pnlDec.add(pnlDecRow1);
		pnlDec.add(pnlDecRow2);
		pnlDec.add(btnDecRun);
		pnlDec.add(pnlDecText);
		
		//about panel
		pnlAbt = new JPanel(new FlowLayout());
		pnlAbt.setBackground(new Color(0,0,0,0));
		for (int i=0; i<strAbout.length; i++)
			pnlAbt.add(new JLabel(strAbout[i]));
		
		//help panel
		pnlHlp = new JPanel(new FlowLayout());
		pnlHlp.setBackground(new Color(0,0,0,0));
		for (int i=0; i<strHelp.length; i++)
			pnlHlp.add(new JLabel(strHelp[i]));
                //Send panel
            pnlSend = new JPanel(new FlowLayout());
            pnlSend.setPreferredSize(new Dimension(300, 20));
            pnlSend.setBackground(new Color(0, 0, 0, 0));

            btnSend = new JButton("SEND");
            btnSend.setPreferredSize(new Dimension(80, 20));
            btnSend.addActionListener(this);
            btnSend.setMnemonic(KeyEvent.VK_S);
            pnlSend.add(new JLabel("For Send a file"));
            pnlSend.add(btnSend);

		
                //main tab
		tab = new JTabbedPane();
		tab.setPreferredSize(new Dimension(310,150));
		tab.add("Encrypt",pnlEnc);
		tab.add("Decrypt",pnlDec);
		tab.add("About",pnlAbt);
		tab.add("Help",pnlHlp);
              //  tab.add("Send",pnlSend);
		
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
	}

	/**
	 *	Receive and process user interactions
	 *	@param e	Sender of the event
	 */
	public void actionPerformed(ActionEvent e){
		JButton btn = (JButton)e.getSource();
		
		//browse for source file
		if (btn == btnEncBrw){
			File file = getFileDialogOpen("*.*");
			if (file==null)	return;
			txtEncFile.setText(file.getAbsolutePath());
		}

		//browse for encrypted files
		if (btn == btnDecBrw){
			File file = getFileDialogOpen("*.enc");
			if (file==null)	return;
			txtDecFile.setText(file.getAbsolutePath());
		}

		//browse for encryption key
		if (btn == btnKeyBrw){
			File file = getFileDialogOpen("*.key");
			if (file==null)	return;
			txtDecKey.setText(file.getAbsolutePath());
		}

		//perform encryption
		if (btn == btnEncRun){
			
			//open file and read data
			File file = new File(txtEncFile.getText());
			byte data[] = readByteFile(file);

			//encrypt and save as new data and key as new files						
			data = AES.encrypt(data);
			if (writeByteFile(file.getAbsolutePath() + ".enc",data) &&
				writeObjectFile(file.getAbsolutePath() + ".key",AES.getKey())){
				
				//remove old file
				file.delete();
				
				JOptionPane.showMessageDialog(null,
					"File encrypted as: " + file.getName() + ".enc\n" +
					"Encryption key: " + file.getName() + ".key\n",
					"Done",JOptionPane.INFORMATION_MESSAGE);				
			}			
		}

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
				output.append(new String(data));
			}			
			
		}
		
		//exit
		if (btn == btnExit){
			System.exit(0);
		}
                if(btn==btnSend)
                {
                    //SendMail.html;
                }
	}
	
	/**
	 *	Allow user to select a file using an Open Dialog and return the file
	 *	@return A valid file that the user has selected, or null
	 */
	protected File getFileDialogOpen(String filter){
		FileDialog fd = new FileDialog(this,"Select File",FileDialog.LOAD);
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
}