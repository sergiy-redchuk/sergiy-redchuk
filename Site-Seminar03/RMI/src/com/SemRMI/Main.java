package com.SemRMI;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import lpi.server.rmi.IServer;
import lpi.server.rmi.IServer.ArgumentException;
import lpi.server.rmi.IServer.FileInfo;
import lpi.server.rmi.IServer.Message;
import lpi.server.rmi.IServer.ServerException;

public class Main 
{

	//======> Static values:
	
		static IServer proxy;
		static String sessionID = null;
		
	
	//======
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws NotBoundException, ArgumentException, ServerException, RemoteException 
	{
		
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
		
		
				
		try
		{
			
			Registry registry = LocateRegistry.getRegistry("lv.rst.uk.to", 152);
			proxy = (IServer)registry.lookup(IServer.RMI_SERVER_NAME);
			
			String str;
			String arr[];
			
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader reader = new BufferedReader(isr);
			
			Timer timeToReceiveMsg = new Timer();
			boolean isClosed = false;
			boolean isTimerStarted = false;
			
			Some_Imp imp = null;
			
			//=======
			
			while(!isClosed)
			{
				str = reader.readLine();
				arr = str.split(" ");
				
				switch(arr[0])
				{
					case "ping": 
								try
								{
									 proxy.ping();
								}
								catch(RemoteException Rexc)
								{
									System.out.println("RemoteException!!");
								}
								break;
					
					case "echo":
								try
								{
									System.out.println(proxy.echo(String.join(" ", partsout(arr, 1))));
								}
								catch(RemoteException Rexc)
								{
									System.out.println("RemoteException !!");
								}
								break;
								
					case "login":
								try
								{
									sessionID = proxy.login(arr[1], arr[2]);
									System.out.println(sessionID);
									
									if(sessionID != null && !isTimerStarted)
									{
										isTimerStarted = true;
									}
									timeToReceiveMsg.schedule(new MyFirstTimerTask(), 0, 1000);
									
								}
								catch(RemoteException Rexc)
								{
									System.out.println("RemoteException!!");
								}
								break;
								
					case "list":
								try
								{
//									System.out.println(proxy.listUsers(sessionID));
									System.out.println("List users: " + Arrays.toString(proxy.listUsers(sessionID)));
									
								}
								catch(RemoteException Rexc)
								{
									System.out.println("RemoteException!!");
								}
								break;
								
					case "ShowList":
								try
								{
									
									TimerTask task = new MySecondTimerTask();
									Timer timer = new Timer();
									timer.schedule(task, 7000, 17000);
									task.run();
									
//									System.out.println("List users: " + Arrays.toString(proxy.listUsers(sessionID)));
																		
								}
								catch(Exception exc)
								{
									System.out.println("Exception!!");
								}
								break;
								
					case "msg":
								try
								{
									Message NewMessage = new Message(arr[1], String.join(" ", partsout(arr, 2)));
									proxy.sendMessage(sessionID, NewMessage);
								}
								catch(RemoteException Rexc)
								{
									System.out.println("RemoteException!!");
								}
								
					case "file":
								try
								{
									FileInfo NewFileInfo = new FileInfo(arr[1], new File(arr[2]));
									proxy.sendFile(sessionID, NewFileInfo);									
								}
								catch(RemoteException Rexc)
								{
									System.out.println("RemoteException!!");
								}
								break;
								
					case "exit":
								try 
								{
									proxy.exit(sessionID);
									isClosed = true;
								} 
								catch (RemoteException ex) 
								{
									System.out.println("RemoteException!!");
								}
								break;
								
					default:
							System.out.println("Invalid command");
							break;
					
				}
				
			}
			
		}
		catch(ArgumentException exc)
		{
			System.out.println("ArgumentException!!");
		}
		catch(IOException exc)
		{
			System.out.println("IOException!!");
		}
		catch(Exception exc)
		{
			System.out.println("Exception!!");
		}
		
		
		
	}
	
		
	//======> Static methods:
	
	static String[] partsout(String[] array, int index) 
	{
		String[] result = new String[array.length - index];
		for (int i = index; i < (array.length); i++) 
		{
			result[i - index] = array[i];
		}
		return result;
	}
	
	//---
	
	
	
	//============> static classes:
	
	public static class MyFirstTimerTask extends TimerTask 
	{
		@Override
		public void run() 
		{
			try 
			{
				Message ReceivedMessage = proxy.receiveMessage(sessionID);
				if(ReceivedMessage != null)
				{
					System.out.println("Incoming Message " + ReceivedMessage.getMessage() + " from " + ReceivedMessage.getSender());
				}
				
				FileInfo ReceivedFile = proxy.receiveFile(sessionID);
				if(ReceivedFile != null) 
				{
					Path path = Paths.get("D:\\Desktop ", ReceivedFile.getFilename());
					Path content = Files.write(path, ReceivedFile.getFileContent(), StandardOpenOption.CREATE);
					System.out.println("Incoming File:" + ReceivedFile.getFilename() + "from" + ReceivedFile.getSender());
				}
			} 
			catch (RemoteException ex) 
			{
				System.out.println("RemoteException!!");
			} 
			catch (IOException e) 
			{
				System.out.println("IOException!!");
			}
		}
	}
	
	//------
	
	public static class MySecondTimerTask extends TimerTask
	{

		@Override
		public void run() 
		{
			try
			{
				
				Date date = new Date();
				System.out.println("List updated: " + date.toString());
				System.out.println("List users: " + Arrays.toString(proxy.listUsers(sessionID)));
				System.out.println("------------------------------------------");
			}
			catch (ArgumentException e) 
			{
				System.out.println("ArgumentException!!");
			} 
			catch (ServerException e) 
			{
				System.out.println("ServerException!!");
			} 
			catch (RemoteException e) 
			{
				System.out.println("RemoteException!!");
			}
		}
		
	}
	
	//============
}
