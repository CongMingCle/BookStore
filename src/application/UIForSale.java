package application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import application.UIForManager.SelectionListener;

public class UIForSale {
	String[] headers = {"ISBN Number", "Book Name", "Single Price", "Discount", "Amount"};
	String[][] Data = new String[100][5];
	int dataAmount = 0;
	int selectedRow = -1;
	boolean flag = true;//区分用户在单元格的手动更新和数据库导入的自动更新。
	
	final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	final String DB_URL = "jdbc:mysql://localhost:3306/bookstore";
	final String USER = "root";
	final String PASS;
	
	JFrame frame = new JFrame("Book Sale");
	
	JLabel label1 = new JLabel();
	JLabel label2 = new JLabel("user name");
	
	JLabel label3 = new JLabel("Enter ISBN");
	JLabel label4 = new JLabel("Total money:");
	JLabel label5 = new JLabel("Total Amount");
	
	JTextField tfd2 = new JTextField(20);//填写用户名
	JTextField tfd3 = new JTextField(30);//填写ISBN
	JTextField tfd4 = new JTextField(15);//显示数量
	JTextField tfd5 = new JTextField(15);//显示总金额
	
	JButton btn1 = new JButton("Add");
	JButton btn2 = new JButton("Delete");
	JButton btn3 = new JButton("Finish");
	
	JPanel pnl1 = new JPanel();
	JPanel pnl2 = new JPanel();
	JPanel pnl3 = new JPanel();
	JPanel pnl4 = new JPanel();
	JPanel pnl12 = new JPanel();
	JPanel pnl21 = new JPanel();
	JPanel pnl22 = new JPanel();
	
	DefaultTableModel DataModel = new DefaultTableModel(Data, headers);
	//改写JTable的方法，使得DataTable的单元格不可编辑
	JTable DataTable = new JTable(DataModel) {
		public boolean isCellEditable(int row, int column)
        {
			if(column == 3 || column == 4)
				return true;
			else
				return false;
        }
	};
	JScrollPane ScPane = new JScrollPane();

	public UIForSale(String password) {
		this.PASS = password;
		InterfaceForSaler();
	}
	
	public void InterfaceForSaler() {
		
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
		
		frame.setBounds(200, 200, 800, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		pnl1.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnl21.setLayout(new FlowLayout());
		pnl22.setLayout(new FlowLayout());
		pnl3.setLayout(new FlowLayout());
		pnl2.setLayout(new BorderLayout());
		
		this.setClock();
		pnl1.add(label1);
		
		
		pnl21.add(label3);
		pnl21.add(tfd3);
		pnl21.add(btn1);
		btn1.addActionListener(new ButtonHander());
		
		DataModel.addTableModelListener(new TableHander());
		ListSelectionModel SelectionModel = DataTable.getSelectionModel();//获取DataTable中的选择模型
		SelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//设置为只能选择一个单元格
		SelectionModel.addListSelectionListener(new SelectionHander());//为选择模型添加监听器
		
		ScPane.setViewportView(DataTable);
		btn2.addActionListener(new ButtonHander());
		
		pnl22.add(btn2);
		pnl2.add(ScPane, BorderLayout.CENTER);
		pnl2.add(pnl22, BorderLayout.SOUTH);
		pnl2.add(pnl21, BorderLayout.NORTH);
		
		tfd4.setEditable(false);
		tfd5.setEditable(false);
		
		btn3.addActionListener(new ButtonHander());
		pnl3.add(label4);
		pnl3.add(tfd4);
		pnl3.add(label5);
		pnl3.add(tfd5);
		pnl3.add(btn3);
		
		frame.add(pnl1, BorderLayout.NORTH);
		frame.add(pnl2, BorderLayout.CENTER);
		frame.add(pnl3, BorderLayout.SOUTH);
		frame.setVisible(true);
	}
	
	public JLabel setClock() {
		Timer time = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				label1.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
			}
		});
		time.start();
		return label1;
	}
	
	public int calAmount() {
		int tempSum = 0;
		for(int i = 0; i < dataAmount; i++) {
			tempSum = tempSum + Integer.parseInt(Data[i][4]);
		}
		return tempSum;
	}
	
	public double calMoney() {
		double tempSum = 0.0;
		for(int i =0; i < dataAmount; i++) {
			tempSum = tempSum + (Double.parseDouble(Data[i][2]) * Double.parseDouble(Data[i][3]) * Integer.parseInt(Data[i][4]));
		}
		return (double)Math.round(tempSum * 100) / 100;
	}
	
	public class TableHander implements TableModelListener{

		@Override
		public void tableChanged(TableModelEvent e) {
			int col = e.getColumn();
			int row = e.getFirstRow();
			if(flag) {
				
				Data[row][col] = (String) DataModel.getValueAt(row, col);
				System.out.println(Data[row][col]);
				tfd5.setText(Integer.toString(calAmount()));
				tfd4.setText(Double.toString(calMoney()));
			}
		}
		
	}
	
	public class SelectionHander implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			selectedRow = DataTable.getSelectedRow();
			
		}
		
	}
	
	public class ButtonHander implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String Cmd = e.getActionCommand();
			if(Cmd.equals("Add"))
				try {
					addData();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if(Cmd.equals("Finish"))
				finishData();
			if(Cmd.equals("Delete"))
				deleteData();
			
		}
		private void finishData() {
			//单次下单写入日志文件
			if(dataAmount < 1) {
				return ;
			}
			try {
				String pathDes = System.getProperty("user.dir") + "\\log\\" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv";
				File desFile = new File(pathDes);
				FileWriter FW = new FileWriter(desFile, true);
				BufferedWriter BW = new BufferedWriter(FW);
				for(int i =0; i < dataAmount; i++) {
					BW.write(new SimpleDateFormat("HH:mm:ss").format(new Date()) + ",");
					for(int j = 0; j < 5; j++) {
						BW.write(Data[i][j] + ",");
					}
					BW.write("\n");
					
					
				}
				BW.flush();
				FW.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//数据库库存更新
			try{
	            Class.forName(JDBC_DRIVER);
	            Connection Conn	 = DriverManager.getConnection(DB_URL,USER,PASS);
	            Statement Stmt = Conn.createStatement();
	            
	            for(int i =0; i < dataAmount; i++) {
	            	String sql = "UPDATE book " + "SET Stock=Stock-1 " + "WHERE ISBN=\"" + Data[i][0] + "\";";
	            	Stmt.executeUpdate(sql);
				}
	    		Conn.close();
	    		Stmt.close();
	        }catch(SQLException SE){    
	            SE.printStackTrace();
	        }catch(Exception e){
	            e.printStackTrace();
	        }
			//清空JTable,回到初始状态
			flag = false;
			for(int i = 0; i < dataAmount; i++) {
				for(int j = 0; j < 5; j++) {
					DataModel.setValueAt(null, i, j);
					DataModel.fireTableCellUpdated(i, j);
				}
			}
			Data = new String[100][5];
			dataAmount = 0;
			selectedRow = -1;
			flag = true;
			tfd3.setText(null);
			tfd5.setText(Integer.toString(calAmount()));
			tfd4.setText(Double.toString(calMoney()));
		}
			
		private void deleteData() {
			//System.out.println(selectedRow);
			flag = false;
			if(selectedRow < 0 || dataAmount < 1) {
				flag = true;
				return ;
			}
			System.out.println("a");
			for(int i = 0; i < dataAmount - selectedRow - 1; i++ ) {
				for(int j =0; j < 5; j++) {
					Data[selectedRow + i][j] = Data[selectedRow + i + 1][j];
					DataModel.setValueAt(Data[selectedRow + i][j], selectedRow + i, j);
					DataModel.fireTableCellUpdated(selectedRow + i, j);
				}
			}
			for(int j = 0; j < 5; j++) {
				DataModel.setValueAt("", dataAmount - 1 , j);
				DataModel.fireTableCellUpdated(dataAmount - 1 , j);
			}
			dataAmount--;
			tfd5.setText(Integer.toString(calAmount()));
			tfd4.setText(Double.toString(calMoney()));
			flag = true;
			
		}
		
		private void addData() throws SQLException {
			String ISBN = tfd3.getText();
			
			flag = false;
			
			String sql = "SELECT ISBN,BookName,Price FROM book WHERE ISBN=\"" + ISBN + "\"";
			try{
	            Class.forName(JDBC_DRIVER);
	            Connection Conn	 = DriverManager.getConnection(DB_URL,USER,PASS);
	            Statement Stmt = Conn.createStatement();
	            ResultSet RS = Stmt.executeQuery(sql);
	            
	            if(RS.next()) {
		            Data[dataAmount][0] = RS.getString("ISBN");
					Data[dataAmount][1] = RS.getString("BookName");
					Data[dataAmount][2] = Double.toString(RS.getDouble("Price"));
					Data[dataAmount][3] = "1.0";
					Data[dataAmount][4] = "1";
	            }
	            else {
	            	JOptionPane.showMessageDialog(null, "Not Exist This Book", "System Notice" ,JOptionPane.WARNING_MESSAGE);
	            	return ;
	            }
				for(int i = 0; i < 5; i++) {
					DataModel.setValueAt(Data[dataAmount][i], dataAmount, i);
					DataModel.fireTableCellUpdated(dataAmount, i);
				}
				dataAmount++;
	    		Conn.close();
	    		Stmt.close();
	        }catch(SQLException SE){    
	            SE.printStackTrace();
	        }catch(Exception e){
	            e.printStackTrace();
	        }
			tfd5.setText(Integer.toString(calAmount()));
			tfd4.setText(Double.toString(calMoney()));
			flag = true;
		}	
	}
}
