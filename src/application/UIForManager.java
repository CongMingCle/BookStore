package application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class UIForManager {
	
	private int dataAmount = 0; //指针，指向Data数组尾部的下一个存储单元
	private int selectedRow = 0; //指向DataTable中被选中的行号
	String[] labelName = {"BookName", "Author", "Publisher", "Publish Date", "Price", "ISBN", "Stock"}; //DataTable表头
	String[][] Data = new String[1000][7]; //书目数据的二维数组
	
	JFrame frame = new JFrame("Book Manage");
	JPanel panel0 = new JPanel();
	JPanel panel1 = new JPanel();
	JPanel panel2 = new JPanel();
	JPanel panel3 = new JPanel();
	JLabel label1 = new JLabel("Book Information");
	JLabel label2 = new JLabel("Book InformationList");
	JButton button0 = new JButton("SEARCH");
	JButton button1 = new JButton("ADD");
	JButton button2 = new JButton("EXIT");
	JButton button3 = new JButton("CHANGE");
	JButton button4 = new JButton("DELETE");
	
	DefaultTableModel DataModel = new DefaultTableModel(Data, labelName);
	//改写JTable的方法，使得DataTable的单元格不可编辑
	JTable DataTable = new JTable(DataModel) {
		public boolean isCellEditable(int row, int column)
        {
			return false;
        }
	};
	JScrollPane ScPane = new JScrollPane();
	JLabel[] Labels = new JLabel[7];
	JTextField[] Editfields = new JTextField[7];
	JTextField editISBN = new JTextField(50);

	
	final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	final String DB_URL = "jdbc:mysql://localhost:3306/bookstore";
	final String USER = "root";
	final String PASS;
	boolean succeedExSql = true;
	
	public UIForManager(String password) {
		this.PASS = password;
		interfaceForManager();
	}
	
	public void interfaceForManager() {
		frame.setBounds(200, 200, 650, 400);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel0.setLayout(new FlowLayout());
		panel1.setLayout(new GridLayout(7, 2, 5, 2));
		panel2.setLayout(new GridLayout(4, 1, 15, 15));
		panel3.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		editISBN.setSize(new Dimension(50, 7));
		button0.addActionListener(new ButtonHander());
		
		panel0.add(editISBN);
		panel0.add(button0);
		
		//创建标签和文本编辑框
		for(int i = 0; i < 7; i++) {
			Labels[i] = new JLabel(labelName[i]);
			Editfields[i] = new JTextField();
			Editfields[i].setPreferredSize(new Dimension(40,5));
			panel1.add(Labels[i]);
			panel1.add(Editfields[i]);
		}
		
		
		button1.addActionListener(new ButtonHander());
		button2.addActionListener(new ButtonHander());
		button3.addActionListener(new ButtonHander());
		button4.addActionListener(new ButtonHander());
		
		panel2.add(button1);
		panel2.add(button2);
		panel2.add(button3);
		panel2.add(button4);
		panel2.setAlignmentX(1);
		panel2.setAlignmentY(1);
		
		DataTable.setPreferredScrollableViewportSize(new Dimension(600,150));//设置DataTable在视口中的大小
	
		ListSelectionModel SelectionModel = DataTable.getSelectionModel();//获取DataTable中的选择模型
		SelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//设置为只能选择一个单元格
		SelectionModel.addListSelectionListener(new SelectionListener());//为选择模型添加监听器
		
		DataTable.addMouseListener(new MouseHander());//为DataTable添加鼠标监听器
		
		ScPane.setViewportView(DataTable);
		
		panel3.add(label2);
		panel3.add(ScPane);
		
		frame.add(panel0, BorderLayout.NORTH);
		frame.add(panel1, BorderLayout.CENTER);
		frame.add(panel2, BorderLayout.EAST);
		frame.add(panel3, BorderLayout.SOUTH);
		
		frame.setVisible(true);
	}
	

		//返回数据数量（书的数量）
		public int getDataAmount() {
			return this.dataAmount;
		}
		//数据增加方法，读取TextFields中用户输入，并将其添加在Data中，同时更新DataTable和数据库
		public void addData() {
			
			for(int i = 0; i < 7; i++) {
				this.Data[dataAmount][i] = this.Editfields[i].getText();
			}
			this.dataAmount++;
			for(int i=0; i < 7; i++) {
				DataModel.setValueAt(Data[this.dataAmount - 1][i], this.dataAmount - 1 , i);
				DataModel.fireTableCellUpdated(this.dataAmount - 1 , i);
			}
			String str=this.generateInsertStat("book", Data[dataAmount - 1]);
	        System.out.println(str);
			
			try{
	            Class.forName(JDBC_DRIVER);
	            Connection Conn	 = DriverManager.getConnection(DB_URL,USER,PASS);
	            Statement Stmt = Conn.createStatement();
	            try {
	            	Stmt.executeUpdate(str);
	    		}catch(Exception e) {
	    			JOptionPane.showMessageDialog(null, "Illegal Entery", "System Notice" ,JOptionPane.WARNING_MESSAGE);
	    		}
	            
	    		Conn.close();
	    		Stmt.close();
	        }
	        catch(SQLException SE){    
	            SE.printStackTrace();
	            succeedExSql = false;
	        }
	        catch(Exception e){
	            // 处理 Class.forName 错误
	            e.printStackTrace();
	        }
		}
		//将用户在DataTable中选中的项返回到TextFields中供用户重新编辑
		public void extractData() {
			for(int i = 0; i < 7; i++) {
				this.Editfields[i].setText(Data[this.selectedRow][i]);
			}
		}
		//将用户重新编辑好的数据更新到Data、 DataTable、数据库
		public void changeData() {
			for(int i = 0; i < 7; i++) {
				this.Data[this.selectedRow][i] = this.Editfields[i].getText();
				DataModel.setValueAt(Data[this.selectedRow][i], this.selectedRow , i);
				DataModel.fireTableCellUpdated(this.selectedRow, i);
			}
			String str=this.generateUpdateStat("book", Data[this.selectedRow]);
	        System.out.println(str);
			try{
	            Class.forName(JDBC_DRIVER);
	            Connection Conn	 = DriverManager.getConnection(DB_URL,USER,PASS);
	            Statement Stmt = Conn.createStatement();
	    		Stmt.executeUpdate(str);
	    		Conn.close();
	    		Stmt.close();
	        }
	        catch(SQLException SE){    
	            SE.printStackTrace();
	        }
	        catch(Exception e){
	            // 处理 Class.forName 错误
	            e.printStackTrace();
	        }
		}
		//将用户在DataTable中选中的数据删除，并同时更新到Data、DataTable和数据库
		public void deleteData() {
			for(int i = 0; i < dataAmount - this.selectedRow - 1; i++ ) {
				for(int j =0; j < 7; j++) {
					Data[this.selectedRow + i][j] = Data[this.selectedRow + i + 1][j];
					DataModel.setValueAt(Data[this.selectedRow + i + 1][j], this.selectedRow+i, j);
					DataModel.fireTableCellUpdated(this.selectedRow+i, j);
				}
			}
			for(int j = 0; j < 7; j++) {
			DataModel.setValueAt("", this.dataAmount - 1 , j);
			DataModel.fireTableCellUpdated(this.dataAmount - 1 , j);
			}
			this.dataAmount--;
			String str=this.generateDeleteStat("book", Data[this.selectedRow]);
	        System.out.println(str);
			try{
	            Class.forName(JDBC_DRIVER);
	            Connection Conn	 = DriverManager.getConnection(DB_URL,USER,PASS);
	            Statement Stmt = Conn.createStatement();
	    		Stmt.executeUpdate(str);
	    		Conn.close();
	    		Stmt.close();
	        }
	        catch(SQLException SE){    
	            SE.printStackTrace();
	        }
	        catch(Exception e){
	            // 处理 Class.forName 错误
	            e.printStackTrace();
	        }
		}
		
		public void searchData() {
			GetHttpData FromAPI = new GetHttpData();
			FromAPI.dealWithData(FromAPI.getData(editISBN.getText()));
			if(FromAPI.getIsFound() == false) {
				JOptionPane.showMessageDialog(null, "Not Found From API. Please Manually Enter.", "System Notice" ,JOptionPane.INFORMATION_MESSAGE);
			return ;
			}
			Map BookInfo = FromAPI.getBookInfo();
			for(int i = 0; i < 7; i++) {
				if(BookInfo.get(labelName[i]) == null) {
					Editfields[i].setText("");
					continue;
				}
				Editfields[i].setText(BookInfo.get(labelName[i]).toString());
			}
			
		}
		
		//生成SQL的INSERT语句
		public String generateInsertStat(String tableName, String[] data) {
			StringBuffer SQL = new StringBuffer();
			SQL.append("INSERT INTO ");
			SQL.append(tableName);
			SQL.append(" VALUES(");
			for(int i = 0; i < 7; i++) {
				if(data[i].length() == 0) {
					data[i] = null;
				}
				if(i == 3) {
					try {
						SimpleDateFormat SDF1 = new SimpleDateFormat("yyyy");
						SimpleDateFormat SDF2 = new SimpleDateFormat("yyyy-MM-dd");
						
						data[i] = SDF2.format(SDF1.parse(data[i]));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				if(i == 0 || i == 1 || i==2 || i == 3 || i==5)
					SQL.append("\'");
				SQL.append(data[i]);
				if(i == 0 || i == 1 || i==2 || i == 3 || i==5)
					SQL.append("\'");
				if(i != 6)
					SQL.append(",");
			}
			SQL.append(");");
			return SQL.toString();
			
		}
		//生成SQL的DELETE语句
		public String generateDeleteStat(String tableName, String[] data) {
			StringBuffer SQL = new StringBuffer();
			SQL.append("DELETE FROM ");
			SQL.append(tableName);
			SQL.append(" WHERE BookName=\'");
			SQL.append(data[0]);
			SQL.append("\';");
			return SQL.toString();
			
		}
		//生成SQL的UPDATE语句
		public String generateUpdateStat(String tableName, String[] data) {
			StringBuffer SQL = new StringBuffer();
			for(int i = 0; i < 7; i++) {
				if(data[i].length() == 0) {
					data[i] = "null";
				}
			}
			String str = " SET Author="+"\'"+data[1]+"\',"+"Publisher="+"\'"+data[2]+"\',"+"PublishDate="
					+data[3]+",Price="+data[4]+",ISBN="+"\'"+data[5]+"\'"+",Stock="+data[6];
			SQL.append("UPDATE ");
			SQL.append(tableName);
			SQL.append( str);
			SQL.append(" WHERE BookName=\'");
			SQL.append(data[0]);
			SQL.append("\';");
			return SQL.toString();
		}
			
		//实现ActionListener的具体类
		public class ButtonHander implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				String Cmd = e.getActionCommand();
				if(Cmd.equals("ADD"))
					addData();
				if(Cmd.equals("EXIT"))
					System.exit(0);
				if(Cmd.equals("CHANGE"))
					changeData();
				if(Cmd.equals("DELETE"))
					deleteData();
				if(Cmd.equals("SEARCH"))
					searchData();
			}
		}
		//实现MouseListener的具体类
		public class MouseHander implements MouseListener{
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					extractData();
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				
			}
			@Override
			public void mouseExited(MouseEvent e) {
				
			}
		}
		//实现SelectionListener的具体类
		public class SelectionListener implements ListSelectionListener{
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedRow = DataTable.getSelectedRow();
			}
			
		}

}
