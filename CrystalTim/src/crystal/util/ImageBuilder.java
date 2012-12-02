package crystal.util;

import java.io.BufferedInputStream;
import java.lang.Runtime; 
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * This program reads a text file line by line and print to the console. It uses
 * FileOutputStream to read the file.
 * 
 */

public class ImageBuilder {
	private static Vector<String> image_list = new Vector<String>();
	private static Vector<Boolean> img_write = new Vector<Boolean>();
	//public static String dir = "/nfs/none/users2/joechiu/"; 
	public static String dir = "/Users/Timothy/Dropbox"; 
	
/*	public static void main(String[] args) throws IOException {
		String filename = "/Users/joechiu/Dropbox/IVCS/Mytest.java";
		String log = "change image";
		
		if(filename.substring(filename.length() - 5, filename.length()).equals(".java"))
		{
			buildJava(filename,log);
		}
		else if(filename.substring(filename.length() - 2, filename.length()).equals(".m"))
		{
			buildMatlab(filename,log);
		}
		else
		{
			build(filename);
		}
	}*/
	
	public void ImageFileBuilder(String filename, String log) throws IOException
	{
		if(filename.substring(filename.length() - 5, filename.length()).equals(".java"))
		{
			buildJava(filename, log);
		}
		else if(filename.substring(filename.length() - 2, filename.length()).equals(".m"))
		{
			buildMatlab(filename, log);
		}
		else
		{
			build(filename);
		}
	}
	
	public void ImageDirBuilder(String delpath,  ArrayList<String> changedFiles, ArrayList<String> log) throws IOException
	{	
		try {
		      File file = new File(delpath);
		      if (!file.isDirectory()) 
		      {
		    	  for(int k=0;k<changedFiles.size();k++)
		    	  {
		    		  if(file.getAbsolutePath().compareTo(delpath+changedFiles.get(k)) == 1)
		    		  {
		    			  ImageFileBuilder(file.getName(),log.remove(k));
		    			  break;
		    		  }
		    	  }
		      }
		      else if (file.isDirectory()) 
		      {
		    	  String[] filelist = file.list();
		    	  for (int i = 0; i < filelist.length; i++) 
		    	  {
		    		  File childfile = new File(delpath + "\\" + filelist[i]);
		    		  if (!childfile.isDirectory()) 
		    		  {
		    			  for(int k=0;k<changedFiles.size();k++)
				    	  {
				    		  if(childfile.getAbsolutePath().compareTo(delpath+changedFiles.get(k)) == 1)
				    		  {
				    			  ImageFileBuilder(childfile.getName(),log.remove(k));
				    			  break;
				    		  }
				    	  }
		    		  }
		    		  else if (childfile.isDirectory()) {
		    			  ImageDirBuilder(delpath + "\\" + filelist[i], changedFiles, log);
		    		  }
		    	  }
		      }
		    }
		    catch (FileNotFoundException e) {
		      System.out.println("Exception:" + e.getMessage());
		    }
	}
	
	public ImageBuilder(String delpath,  ArrayList<String> changedFiles, ArrayList<String> log) throws IOException
	{
		ImageDirBuilder(delpath, changedFiles, log);
	}
	
	public static void build(String filename){
		File file = new File(filename);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		
		try {
			fis = new FileInputStream(file);
			
			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			
			// dis.available() returns 0 if the file does not have more lines.
			while (dis.available() != 0) {
				
				// this statement reads the line from the file and print it to
				// the console.
				System.out.println(dis.readLine());
			}
			
			// dispose all the resources after using them.
			fis.close();
			bis.close();
			dis.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void buildMatlab(String filename, String log) throws IOException{
		File file = new File(filename);
		String newfile_name = filename.substring(0,filename.length()-2)+"_BuildImage.m";
		File newfile = new File(newfile_name);
		Runtime run = Runtime.getRuntime();
		int change=0;
		
		if(!newfile.createNewFile())
		{
			System.out.println("Fail: cannot create a newfile");
		}
		
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		DataOutputStream dos = null;
		
		String source_code;
		int start,end;
		int i;
		String temp_str;
		
		try {
			
//step 1 - initial Image
			fis = new FileInputStream(file);
			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			
			// dis.available() returns 0 if the file does not have more lines.
			while (dis.available() != 0) {
				source_code = dis.readLine();
				
				if(source_code.contains("%<Image>"))
				{
					start = source_code.indexOf("=");
					image_list.add(source_code.substring(0, start).replaceAll(" ", ""));
					//img_write.add(false);
				}
			}
			fis.close();
			bis.close();
			dis.close();
			
//step 2 - get modified log - from parameter
			
//step 3 - check Image state
			System.out.println("========<Image tag>========");
			for(i=0;i<image_list.size();)
			{
				System.out.print(image_list.get(i));
				if(log.contains(image_list.get(i)))
				{
					System.out.print("------->(changed)");
					i++;
				}
				else
					image_list.remove(i);
				System.out.println("");	
			}
			change = image_list.size();
			
//step 4 & 5 - find position and write document 			
			fis = new FileInputStream(file);
			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			
			fos = new FileOutputStream(newfile);
			// Here BufferedInputStream is added for fast reading.
			bos = new BufferedOutputStream(fos);
			dos = new DataOutputStream(bos);
			
			// dis.available() returns 0 if the file does not have more lines.
			while (dis.available() != 0) {
				
				source_code = dis.readLine();
				temp_str =  source_code.replaceAll(" ", "");
				
				for(i=0;i<image_list.size();i++)
				{
					//if(temp_str.contains(image_list.get(i)+"="))
					if(temp_str.equals("clear") || (temp_str.contains("clear"+image_list.get(i)) && !source_code.contains("clear"+image_list.get(i))))
					{
						dos.writeChars("set(figure(1),'visible','off');\n" +
								"imshow("+image_list.get(i)+")\n" +
								" print(gcf,'-djpeg','"+dir+"saved.jpeg') \n" +
								"set(figure(1),'visible','on');\n");
						
						image_list.remove(i);
						i--;
					}
				}
				
				dos.writeChars(source_code+"\n");
			}
			
			for(i=0;i<image_list.size();i++)
			{
				dos.writeChars("set(figure(1),'visible','off');\n" +
						"imshow("+image_list.get(i)+")\n" +
						" print(gcf,'-djpeg','"+dir+"saved.jpeg') \n" +
						"set(figure(1),'visible','on');\n");
			}
			dos.flush();
			fis.close();
			bis.close();
			dis.close();
			fos.close();
			bos.close();
			dos.close();
			// dispose all the resources after using them.
			
			
// step 5 - save Image result
			if(change != 0)
			{
				run.exec("mcc -m "+newfile_name);
				run.exec("./" + newfile_name.substring(0, newfile_name.length()-2));
			}
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void buildJava(String filename, String log) throws IOException{
		File file = new File(filename);
		String newfile_name = filename.substring(0,filename.length()-5)+"_BuildImage.java";
		File newfile = new File(newfile_name);
		Runtime run = Runtime.getRuntime();
		int change = 0;
		
		if(!newfile.createNewFile())
		{
			System.out.println("Fail: cannot create a newfile");
		}
		
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		DataOutputStream dos = null;
		
		String source_code;
		int start,end;
		int i;
		String temp_str;
		
		try {
			
//step 1 - initial Image
			fis = new FileInputStream(file);
			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			
			// dis.available() returns 0 if the file does not have more lines.
			while (dis.available() != 0) {
				source_code = dis.readLine();
				
				if(source_code.contains("/*<Image>*/") && source_code.contains("/*</Image>*/"))
				{
					start = source_code.indexOf("/*<Image>*/");
					end = source_code.indexOf("/*</Image>*/");
					
					image_list.add(source_code.substring(start+11, end).replaceAll(" ", ""));
					//img_write.add(false);
				}
			}
			fis.close();
			bis.close();
			dis.close();
			
//step 2 - get modified log - from parameter
			
//step 3 - check Image state
			System.out.println("========<Image tag>========");
			for(i=0;i<image_list.size();)
			{
				System.out.print(image_list.get(i));
				if(log.contains(image_list.get(i)))
				{
					System.out.print("------->(changed)");
					i++;
				}
				else
					image_list.remove(i);
				System.out.println("");	
			}
			change = image_list.size();
			
//step 4 & 5 - find position and write document 			
			fis = new FileInputStream(file);
			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			
			fos = new FileOutputStream(newfile);
			// Here BufferedInputStream is added for fast reading.
			bos = new BufferedOutputStream(fos);
			dos = new DataOutputStream(bos);
			
			dos.writeChars("import java.awt.Image;\n" +
					"import java.io.File;\n");
			
			source_code = dis.readLine();
			temp_str =  source_code.replaceAll(" ", "");
			while(!temp_str.contains("main(") && dis.available() != 0)
			{
				dos.writeChars(source_code+"\n");
				source_code = dis.readLine();
				temp_str =  source_code.replaceAll(" ", "");
			}
			if(temp_str.contains("main("))
			{
				dos.writeChars("BufferedImage ixmxgx89483345257873;\n "+
							 "File output_ixmxgx89483345257873;\n");
			}
			dos.writeChars(source_code+"\n");
			
			// dis.available() returns 0 if the file does not have more lines.
			while (dis.available() != 0) {
				
				source_code = dis.readLine();
				temp_str =  source_code.replaceAll(" ", "");
				
				for(i=0;i<image_list.size();i++)
				{
					if(temp_str.contains("free("+image_list.get(i)+")") 
					//		|| temp_str.contains(image_list.get(i)+"=")
							)
					{	
						dos.writeChars("try {\n" +
								"ixmxgx89483345257873 = " + image_list.get(i) + ";\n" +
								"output_ixmxgx89483345257873 = new File(\""+dir+"saved.jpg\");\n" +
								"ImageIO.write(ixmxgx89483345257873, \"jpg\", output_ixmxgx89483345257873);\n" +
							"} catch (IOException e) {\n" +
								"e.printStackTrace();\n"+
							"}\n");
						
						image_list.remove(i);
						i--;
					}
				}
				
				dos.writeChars(source_code+"\n");
			}
			
			for(i=0;i<image_list.size();i++)
			{
				dos.writeChars("try {\n" +
								"ixmxgx89483345257873 = " + image_list.get(i) + ";\n" +
								"output_ixmxgx89483345257873 = new File(\""+dir+"saved.jpg\");\n" +
								"ImageIO.write(ixmxgx89483345257873, \"jpg\", output_ixmxgx89483345257873);\n" +
							"} catch (IOException e) {\n" +
								"e.printStackTrace();\n"+
							"}\n");
			}
			dos.flush();
			fis.close();
			bis.close();
			dis.close();
			fos.close();
			bos.close();
			dos.close();
			// dispose all the resources after using them.
			
			
// step 5 - save Image result
			if(change != 0)
			{
				run.exec("javac "+newfile_name);
				run.exec("java " + newfile_name.substring(0,newfile_name.length()-5));	
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}