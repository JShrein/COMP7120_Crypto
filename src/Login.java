/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.Timer;

public class Login
{
	public static void main(String args[])
                 {
		new LoginUI().start();
	}	
}
class LoginUI extends JFrame implements ActionListener
{
	private JTextField txtUserId;
	JPasswordField txtPassword;
	private JButton btnExit,
	btnSubmit;
	private JTabbedPane tab;
	private JPanel pnlLog, pnlAbt, pnlHlp, pnlLogRow1, pnlLogRow2, pnlLogRow3;
	private String strAbout[] = {"Authors: Swati, Renu,Tisha(31/03/11) ",
								 "Email: swati@gmail.com",
								 "Reference: http://www.builderau.com.au/",
								 "program/java/0,39024620,20279922-7,00.htm"};
	private String strHelp[] = {"This software is based on the AES",
								 "Reference: http://www.builderau.com.au/",
								 "program/java/0,39024620,20279922-7,00.htm"};
	String str1,str;
	public LoginUI()
	{
		txtUserId = new JTextField("", 10);
		txtPassword = new JPasswordField("", 10);
		btnExit = new JButton("Exit");
		btnExit.setPreferredSize(new Dimension(80, 20));
		btnExit.addActionListener(this);
		btnExit.setMnemonic(KeyEvent.VK_X);
		btnSubmit = new JButton("Submit");
		btnSubmit.setPreferredSize(new Dimension(80, 20));
		btnSubmit.addActionListener(this);
		btnSubmit.setMnemonic(KeyEvent.VK_E);
		pnlLogRow1 = new JPanel(new BorderLayout());
		pnlLogRow1.setPreferredSize(new Dimension(300, 20));
		pnlLogRow1.setBackground(new Color(0, 0, 0, 0));
		pnlLogRow1.add(new JLabel("   UserId: "), "Center");
		pnlLogRow1.add(txtUserId, "East");

		pnlLogRow2 = new JPanel(new BorderLayout());
		pnlLogRow2.setPreferredSize(new Dimension(300, 20));
		pnlLogRow2.setBackground(new Color(0, 0, 0, 0));
		pnlLogRow2.add(new JLabel("Password: "), "Center");
		pnlLogRow2.add(txtPassword, "East");
		pnlLog = new JPanel(new FlowLayout());
		pnlLog.setBackground(new Color(0, 0, 0, 0));
		pnlLog.add(new JLabel("Enter UserId & Password"), "Center");
		pnlLog.add(pnlLogRow1);
		pnlLog.add(pnlLogRow2);
		pnlLog.add(btnSubmit);
		pnlLog.add(btnExit);
		pnlAbt = new JPanel(new FlowLayout());
		pnlAbt.setBackground(new Color(0, 0, 0, 0));
		for (int i = 0; i < strAbout.length; i++)
			pnlAbt.add(new JLabel(strAbout[i]));
		pnlHlp = new JPanel(new FlowLayout());
		pnlHlp.setBackground(new Color(0, 0, 0, 0));
		for (int i = 0; i < strHelp.length; i++)
			pnlHlp.add(new JLabel(strHelp[i]));
		tab = new JTabbedPane();
		tab.setPreferredSize(new Dimension(310, 150));
		tab.add("Login", pnlLog);
		tab.add("About", pnlAbt);
		tab.add("Help", pnlHlp);

		setSize(360, 160);
		setLocation(100, 100);
		getContentPane().add(tab, "Center");

		setTitle("AES File Encryption/Decryption");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
	}

	public void start()
	{
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e)
                         {
		JButton btn = (JButton)e.getSource();
                                   if (btn == btnExit)
                                      {
			System.exit(0);
		   }
if (btn == btnSubmit)
                                      {
String s1 = txtUserId.getText();
       String s2 = txtPassword.getText();//.getPassword().toString();

	int ctr=0;
		
		try
		{
			
			//int i=0;
RandomAccessFile RAS=new RandomAccessFile("UsrPwd.txt", "rw");
			 str1=s1+":"+s2;
			while((RAS.getFilePointer())!=(RAS.length()))
			 {
				str=RAS.readLine();
				if(str.equals(str1))
				{
					ctr=1;
					break;
				}
			}
			RAS.close();
		}catch(Exception ea)
		 {}
		
//end of verify 	
	


			  
				if(ctr==1)
				{
		   		JOptionPane.showMessageDialog(this, new String("you login sucessfully !" + "   Welcome."));
                 this.setVisible(false);
              TestAES obj = new TestAES();
			  new FileEncryptUI().start(); 
					
				}
				else
            {
              JOptionPane.showMessageDialog(this,new String("Invalid UserName Or Password !"+"   Please try again."));
            }
			txtUserId.setText("");
	   txtPassword.setText("");
      }

	        }}
