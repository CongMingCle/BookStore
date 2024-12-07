package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class Main {

	public Main() {
	}
	public static void main(String[] args) {
		final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
		final String DB_URL = "jdbc:mysql://localhost:3306/bookstore";
		final String USER = "root";
		final String PASS;
		
		JPasswordField JPF = new JPasswordField();
		
		Object[] PW = {"Please Enter the Password of DB:\n", JPF};
		JOptionPane.showConfirmDialog(null, PW, "Bookstore System" , JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		char[] Password = JPF.getPassword();
		StringBuffer temp = new StringBuffer();
		for(int i = 0; i < Password.length; i++) {
			
			temp.append(Password[i]);
		}
		PASS = temp.toString();
		
		try{
            Class.forName(JDBC_DRIVER);
            Connection Conn	 = DriverManager.getConnection(DB_URL,USER,PASS);
            Statement Stmt = Conn.createStatement();
    		Conn.close();
    		Stmt.close();
        }catch(SQLException SE){  
        	JOptionPane.showMessageDialog(null, "Password Error", "Bookstore System" ,JOptionPane.ERROR_MESSAGE);
			//System.out.println();
            SE.printStackTrace();
			System.exit(0);
        }catch(Exception e){
            e.printStackTrace();
        }
		Object[] ID = {"I am saler", "I am manager"};
		Object idInput = JOptionPane.showInputDialog(null, "Please Choose Who You Are:\n", "Bookstore System", JOptionPane.PLAIN_MESSAGE, null ,ID, null);
		if(idInput == ID[0]) {
			new UIForSale(PASS);
		}
		if(idInput == ID[1]) {
			new UIForManager(PASS);
		}
		
	}

}
