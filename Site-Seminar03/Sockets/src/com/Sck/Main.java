package com.Sck;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

	@SuppressWarnings({ "null", "unused" })
	public static void main(String[] args) 
	{		
		
		//============
		
		String arr1[];
		String arr2[][];
		String str = null;	
		
		Implementation imp = null ;
		Socket socket_message;
						
		//============
		
						
		System.out.println("Welcome to simple java console interprate."
				 		 + "Select the appropriate command:\n" 
				 		 + "Press 'ping' to selest the command: ping" 
				 		 + "\nPress 'echo' to selest the command: echo" 
				 		 + "\nPress 'login' to selest the command: login: <login name>, <login password>" 
				 		 + "\nPress 'list' to selest the command: list"
						 + "\nPress 'msg' to selest the command: msg: <name of the msg>, <text of the msg>"
						 + "\nPress 'file' to selest the command: file: <username>, <filename>"
						 + "\nPress 'exit' to selest the command: exit");
		
		//============
		
		System.out.println();
		System.out.println("\nEnter the command: ");
		
		//============
		
		
		
		//=============
		
		
		try
		{		
			
			Socket socket = new Socket("lv.rst.uk.to", 151);
			socket_message = new Socket("lv.rst.uk.to", 151); 
			
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));   
			PrintWriter outnewMessage = new PrintWriter(socket_message.getOutputStream(),true);
			BufferedReader innewMessage = new BufferedReader(new InputStreamReader(socket_message.getInputStream()));
			
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader (isr);
			
			
			
			Timer timeToReceiveMsg = new Timer();
			boolean isClosed = false;
			boolean isTimerStarted = false;
			
			while (!isClosed)
			{
				
				str = br.readLine();
				arr1 = str.split(" ");
				
				switch (arr1[0]) 
				{
			    	case "ping": 	imp.ping(socket);  break;
			        case "echo": 	imp.echo(socket,String.join(" ", partsout(arr1,1)));  break;
			        
			        case "login": 	if(imp.login(socket,arr1[1],arr1[2]) && imp.login(socket_message, arr1[1], arr1[2]) && !isTimerStarted);
						            isTimerStarted = true;
						            timeToReceiveMsg.schedule(new MyTimerTask(), 0, 1000);	break;
		            
			        case "list": 	imp.list(socket);  break;
			        case "msg": 	imp.msg(socket, arr1[1], String.join(" ", partsout(arr1,2)));  break;
			        case "file":  	imp.file(socket,arr1[1],arr1[2]);  break;
			        
			        case "exit":  	isr.close();
		            				br.close();
		            				isClosed=true;
						            isTimerStarted = false;
						            timeToReceiveMsg.cancel();
						            socket.close();
						            socket_message.close();
						            System.out.println("Program is closed");  break;
						            
			        default: 		System.out.println("Incorrect command! Try again.");  break;
				}
			}	
			
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println("ArrayIndexOutOfBoundsException!");
		}
		catch(IOException exc)
		{
			System.out.println("IOException!");
		}
		catch(Exception exc)
		{
			System.out.println("Exception!");
		}
		
		
	}
			
	//================
	
		static String[] partsout(String[] array, int index)
		{
			String [] result = new String[array.length-index];
			for (int i=index; i<(array.length); i++)
			{
				result[i-index] = array[i];
			}
			return result;
		}
		
		static Object deserialize(byte[] data) throws IOException, ClassNotFoundException 
		{
		    ByteArrayInputStream bin = new ByteArrayInputStream(data);
		    ObjectInputStream ois = new ObjectInputStream(bin);
		    return ois.readObject();
		}
		
		
	static Implementation imp;
		
		public static class MyTimerTask extends TimerTask
		{
			public void run()
			{
				try 
				{
					Socket socket_message = null;
					imp.receiveMsg(socket_message);
					imp.receiveFile(socket_message);
				} 
				catch (ClassNotFoundException e) 
				{
					System.out.println("ClassNotFoundException!");
				} 
				catch (Exception e) 
				{
					System.out.println("Exception!");
				}
			}
		}

}
