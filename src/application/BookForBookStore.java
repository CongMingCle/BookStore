package application;

import java.util.Date;

import basicinfo.Book;


public class BookForBookStore extends Book{
	private int stock;
	private String ISBN;
	
	public BookForBookStore() {
	}
	public BookForBookStore(String bookName,String author,String publisher,Date date, double price,String ISBN,int stock){
		super(bookName,author,publisher,price,date);
		this.stock=stock;
		this.ISBN = ISBN;
	}
	
	public void setStock(int stock){
		this.stock=stock;
	}
	
	public int getStock(){
		return this.stock;
	}
	
	public void addStock(){
		this.stock+=1;
	}
	public void addStock(int n){
		this.stock+=n;	
	}
	
	public void reduceStock(){
		this.stock-=1;
	}
	public void reduceStock(int n){
		this.stock-=n;
	}
	public void setISBN(String ISBN) {
		this.ISBN = ISBN;
	}
	public String getISBN() {
		return this.ISBN;
	}
	public double sale(){
		this.reduceStock();
		return this.getPrice();
		
	}
	public double sale(int n,double discount){
		this.reduceStock(n);
		return this.getPrice()*n*discount;
	}

	public void display(){
		super.display();
		System.out.println("库存："+getStock());
	}
	public void inidisplay() {
		super.display();
	}
	

}

