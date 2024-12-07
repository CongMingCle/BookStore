package basicinfo;

import java.util.Date;


public class Book {
	
	protected String bookName;
	protected String author;
	protected String publisher;
	protected double price;
	protected Date date;

	public void setBookName(String name){
		bookName=name;
	}
	public void setAuthor(String au){
		author=au;
	}
	public void setPublisher(String pb){
		publisher=pb;
	}
	public void setPrice(double p){
		price=p;
	}
	
	public void setDate(Date d){
		date=d;
	}

	public String getBookName(){
		return bookName;
	}
	public String getAuthor(){
		return author;
	}
	public String getPublisher(){
		return publisher;
	}
	public double getPrice(){
		return price;
	}
	public Date getDate(){
		return date;
	}
	
	public void display(){
		System.out.println("书名："+getBookName());
		System.out.println("作者："+getAuthor());
		System.out.println("出版商："+getPublisher());
		System.out.println("价格："+getPrice());
		System.out.println("出版日期："+getDate());
	}
	public Book(){
		
	}
	public Book(String bookName){
		this.bookName=bookName;
	}
	public Book(String name,String author){
		this.bookName=name;
		this.author=author;
	}
	public Book(String name,String au,String pb,double p){
		this(name,au);
		this.publisher=pb;
		this.price=p;
	}
	public Book(String name,String au,String pb,double p,Date date){
		this(name,au,pb,p);
		this.date=date;
	}
}
