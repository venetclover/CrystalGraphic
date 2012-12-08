package crystal.util;

import java.io.BufferedInputStream;
import java.lang.Runtime;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * This program reads a text file line by line and print to the console. It uses
 * FileOutputStream to read the file.
 * 
 */

public class ImageBuilder {
	private static Vector<String> image_list = new Vector<String>();
	private static Vector<Boolean> img_write = new Vector<Boolean>();
	// public static String dir = "/nfs/none/users2/joechiu/";
	public static String dir;
	public static String target;

	/*
	 * public void main(String[] args) throws IOException { String filename =
	 * "/Users/joechiu/Dropbox/IVCS/Mytest.java"; String log_element =
	 * "change image"; ArrayList<String> log = new ArrayList<String>();
	 * log.add(log_element); String change_element = "Mytest.m";
	 * ArrayList<String> changeFiles = new ArrayList<String>();
	 * changeFiles.add(change_element);
	 * 
	 * target = "test_joe"; dir = "/Users/joechiu/Dropbox/IVCS/";
	 * ImageDirBuilder("/Users/joechiu/Dropbox/IVCS/", changeFiles, log, "1"); }
	 */

	public void ImageFileBuilder(String filename, String log, String name)
			throws IOException {
		if (filename.substring(filename.length() - 5, filename.length())
				.equals(".java")) {
			buildJava(filename, log, name);
		} else if (filename.substring(filename.length() - 2, filename.length())
				.equals(".m")) {
			buildMatlab(filename, log, name);
		} else {
			// build(filename);
		}
	}

	public void ImageDirBuilder(String delpath, ArrayList<String> changedFiles,
			ArrayList<String> log, String name) throws IOException {
		try {
			File file = new File(delpath);
			if (!file.isDirectory()) {
				for (int k = 0; k < changedFiles.size(); k++) {
					if (file.getAbsolutePath().compareTo(
							delpath + changedFiles.get(k)) == 0) {
						ImageFileBuilder(delpath + "/" + file.getName(),
								log.get(k), name);
						break;
					}
				}
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File childfile = new File(delpath + "/" + filelist[i]);
					if (!childfile.isDirectory()) {
						for (int k = 0; k < changedFiles.size(); k++) {
							// if(childfile.getAbsolutePath().compareTo(delpath+"/"+changedFiles.get(k))
							// == 0)
							if (changedFiles.get(k).contains(
									childfile.getName())) {
								ImageFileBuilder(
										delpath + "/" + childfile.getName(),
										log.get(k), name);
								changedFiles.get(k);
								break;
							}
						}
					} else if (childfile.isDirectory()) {
						ImageDirBuilder(delpath + "/" + filelist[i],
								changedFiles, log, name);
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Exception:" + e.getMessage());
		}
	}

	public ImageBuilder(String delpath, ArrayList<String> changedFiles,
			ArrayList<String> log, String target_name, String name,
			String selfDir) throws IOException {
		// System.out.println("del"+delpath+",number"+name);
		this.target = target_name;
		this.dir = selfDir;
		ImageDirBuilder(delpath, changedFiles, log, name);
	}

	public static void build(String filename) {
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (!deleteDir(files[i]))
					return false;
			}
		}
		return dir.delete();
	}

	public static void buildMatlab(String filename, String log, String name)
			throws IOException {
		File file = new File(filename);

		File imagebuilderfile = new File(filename.substring(0,
				filename.length() - file.getName().length())
				+ "BuildImage/");
		if (!imagebuilderfile.exists()) {
			System.out.println("Mkdir: "
					+ filename.substring(0, filename.length()
							- file.getName().length()) + "BuildImage/");
			imagebuilderfile.mkdir();
		}

		String newfile_name = filename.substring(0, filename.length()
				- file.getName().length())
				+ "BuildImage/" + file.getName();
		File newfile = new File(newfile_name);
		// System.out.println(newfile_name+" XD");
		Runtime run = Runtime.getRuntime();
		int change = 0;

		// System.out.println("target:"+newfile_name);

		if (newfile.exists())
			newfile.delete();

		if (!newfile.createNewFile()) {
			System.out.println("Fail: cannot create a newfile");
		}
		// FileInputStream fis = null;
		// BufferedInputStream bis = null;
		BufferedReader dis = null;

		// FileOutputStream fos = null;
		// BufferedOutputStream bos = null;
		BufferedWriter dos = null;

		String source_code;
		int start, end;
		int i;
		String temp_str;

		try {

			// step 1 - initial Image
			// fis = new FileInputStream(file);
			// Here BufferedInputStream is added for fast reading.
			// bis = new BufferedInputStream(fis);
			// dis = new DataInputStream(bis);

			dis = new BufferedReader(new FileReader(file));

			// dis.available() returns 0 if the file does not have more lines.
			while ((source_code = dis.readLine()) != null) {
				// System.out.println(source_code);
				if (source_code.contains("%<Image>")) {
					start = source_code.indexOf("=");
					if (start > 0)
						image_list.add(source_code.substring(0, start)
								.replaceAll(" ", ""));
					// img_write.add(false);
				}
			}
			// fis.close();
			// bis.close();
			dis.close();

			// step 2 - get modified log - from parameter

			// step 3 - check Image state
			System.out.println("========<Image tag>========");
			for (i = 0; i < image_list.size();) {
				System.out.print(image_list.get(i));
				if (log.contains(image_list.get(i))) {
					System.out.print("------->(changed)");
					i++;
				} else
					image_list.remove(i);
				System.out.println("");
			}
			change = image_list.size();

			// step 4 & 5 - find position and write document
			// fis = new FileInputStream(file);
			// Here BufferedInputStream is added for fast reading.
			// bis = new BufferedInputStream(fis);
			// dis = new DataInputStream(bis);

			dis = new BufferedReader(new FileReader(file));

			// fos = new FileOutputStream(newfile);
			// Here BufferedInputStream is added for fast reading.
			// bos = new BufferedOutputStream(fos);
			dos = new BufferedWriter(new FileWriter(newfile));

			// remove all image and create a new one
			File temp_dir2 = new File(dir + "/img/");
			if (!temp_dir2.exists()) {
				new File(dir + "/img/").mkdir();
				System.out.println("mkdir: " + dir + "/img/");
			}

			File temp_dir = new File(dir + "/img/" + target + "/");

			// deleteDir(temp_dir);
			if (!temp_dir.exists()) {
				new File(dir + "/img/" + target + "/").mkdir();
				System.out.println("mkdir: " + dir + "/img/" + target + "/");
			}
			// dis.available() returns 0 if the file does not have more lines.
			while ((source_code = dis.readLine()) != null) {
				// System.out.println(source_code);
				// source_code = dis.readLine();

				temp_str = source_code.replaceAll(" ", "");

				for (i = 0; i < image_list.size(); i++) {
					// if(temp_str.contains(image_list.get(i)+"="))
					if (temp_str.equals("clear")
							|| (temp_str.contains("clear" + image_list.get(i)) && !source_code
									.contains("clear" + image_list.get(i)))) {
						try {
							if (!(new File(dir + "/img/" + target + "/"
									+ image_list.get(i) + "/").isDirectory())) {
								new File(dir + "/img/" + target + "/"
										+ image_list.get(i) + "/").mkdir();
							}
						} catch (SecurityException e) {
							e.printStackTrace();
						}

						/*
						 * dos.writeChars("set(figure(1),'visible','off');\n" +
						 * "imshow("+image_list.get(i)+")\n" +
						 * " print(gcf,'-djpeg','"
						 * +dir+"/img/"+target+"/"+image_list
						 * .get(i)+"/"+name+".jpg') \n" +
						 * "set(figure(1),'visible','on');\n");
						 */

						dos.write("imwrite(" + image_list.get(i) + ", '" + dir
								+ "/img/" + target + "/" + image_list.get(i)
								+ "/" + name + ".jpg');\n");
						image_list.remove(i);
						i--;
					}
				}

				dos.write(source_code + "\n");
			}

			for (i = 0; i < image_list.size(); i++) {
				try {
					if (!(new File(dir + "/img/" + target + "/"
							+ image_list.get(i) + "/").isDirectory())) {
						new File(dir + "/img/" + target + "/"
								+ image_list.get(i) + "/").mkdir();
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				}

				/*
				 * dos.writeChars("set(figure(1),'visible','off');\n" +
				 * "imshow("+image_list.get(i)+")\n" +
				 * " print(gcf,'-djpeg','"+dir
				 * +"/img/"+target+"/"+image_list.get(i)+"/"+name+".jpg') \n"+
				 * "set(figure(1),'visible','on');\n");
				 */
				dos.write("imwrite(" + image_list.get(i) + ", '" + dir
						+ "/img/" + target + "/" + image_list.get(i) + "/"
						+ name + ".jpg');\n");

			}
			dos.flush();
			// fis.close();
			// bis.close();
			dis.close();
			// fos.close();
			// bos.close();
			dos.close();
			// dispose all the resources after using them.

			copyfile(newfile, file);

			// step 5 - save Image result
			if (change != 0) {
				System.out
						.println("matlab_exe: \"C:\\Program Files\\MATLAB\\R2012a\\bin\\matlab.exe\" -nodesktop -nodisplay -nosplash -minimize -r \"run "
								+ filename
								+ "; quit;\" -logfile jfdoijffdoisjfdoi.txt");
				Process process = run
						.exec("\"C:\\Program Files\\MATLAB\\R2012a\\bin\\matlab.exe\" -nodesktop -nodisplay -nosplash -minimize -r \"run "
								+ filename
								+ "; quit;\" -logfile jfdoijffdoisjfdoi.txt");

				Thread.sleep(20000);
				// process.waitFor();
				// run.exec("matlab -nodesktop -nodisplay -r run("+newfile_name.substring(0,
				// newfile_name.length()-2)+")");
				// run.exec("./" + newfile_name.substring(0,
				// newfile_name.length()-2));
				// System.exit(0);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void buildJava(String filename, String log, String name)
			throws IOException {
		File file = new File(filename);
		String newfile_name = filename.substring(0, filename.length() - 5)
				+ "_BuildImage.java";
		File newfile = new File(newfile_name);
		Runtime run = Runtime.getRuntime();
		int change = 0;

		if (newfile.exists())
			newfile.delete();

		if (!newfile.createNewFile()) {
			System.out.println("Fail: cannot create a newfile");
		}

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		DataOutputStream dos = null;

		String source_code;
		int start, end;
		int i;
		String temp_str;

		try {

			// step 1 - initial Image
			fis = new FileInputStream(file);
			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);

			// dis.available() returns 0 if the file does not have more lines.
			while (dis.available() != 0) {
				source_code = dis.readLine();

				if (source_code.contains("/*<Image>*/")
						&& source_code.contains("/*</Image>*/")) {
					start = source_code.indexOf("/*<Image>*/");
					end = source_code.indexOf("/*</Image>*/");

					image_list.add(source_code.substring(start + 11, end)
							.replaceAll(" ", ""));
					// img_write.add(false);
				}
			}
			fis.close();
			bis.close();
			dis.close();

			// step 2 - get modified log - from parameter

			// step 3 - check Image state
			System.out.println("========<Image tag>========");
			for (i = 0; i < image_list.size();) {
				System.out.print(image_list.get(i));
				if (log.contains(image_list.get(i))) {
					System.out.print("------->(changed)");
					i++;
				} else
					image_list.remove(i);
				System.out.println("");
			}
			change = image_list.size();

			// step 4 & 5 - find position and write document
			fis = new FileInputStream(file);
			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);

			fos = new FileOutputStream(newfile);
			// Here BufferedInputStream is added for fast reading.
			bos = new BufferedOutputStream(fos);
			dos = new DataOutputStream(bos);

			dos.writeChars("import java.awt.Image;\n"
					+ "import java.io.File;\n");

			source_code = dis.readLine();
			temp_str = source_code.replaceAll(" ", "");
			while (!temp_str.contains("main(") && dis.available() != 0) {
				dos.writeChars(source_code + "\n");
				source_code = dis.readLine();
				temp_str = source_code.replaceAll(" ", "");
			}
			if (temp_str.contains("main(")) {
				dos.writeChars("BufferedImage ixmxgx89483345257873;\n "
						+ "File output_ixmxgx89483345257873;\n");
			}
			dos.writeChars(source_code + "\n");

			// dis.available() returns 0 if the file does not have more lines.
			while (dis.available() != 0) {

				source_code = dis.readLine();
				temp_str = source_code.replaceAll(" ", "");

				for (i = 0; i < image_list.size(); i++) {
					if (temp_str.contains("free(" + image_list.get(i) + ")")
					// || temp_str.contains(image_list.get(i)+"=")
					) {
						try {
							if (!(new File(dir + image_list.get(i) + "/")
									.isDirectory())) {
								new File(dir + image_list.get(i) + "/").mkdir();
							}
						} catch (SecurityException e) {
							e.printStackTrace();
						}

						dos.writeChars("try {\n"
								+ "ixmxgx89483345257873 = "
								+ image_list.get(i)
								+ ";\n"
								+ "output_ixmxgx89483345257873 = new File(\""
								+ dir
								+ image_list.get(i)
								+ "/"
								+ name
								+ "\");\n"
								+ "ImageIO.write(ixmxgx89483345257873, \"jpg\", output_ixmxgx89483345257873);\n"
								+ "} catch (IOException e) {\n"
								+ "e.printStackTrace();\n" + "}\n");

						image_list.remove(i);
						i--;
					}
				}

				dos.writeChars(source_code + "\n");
			}

			for (i = 0; i < image_list.size(); i++) {
				try {
					if (!(new File(dir + image_list.get(i) + "/").isDirectory())) {
						new File(dir + image_list.get(i) + "/").mkdir();
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				}

				dos.writeChars("try {\n"
						+ "ixmxgx89483345257873 = "
						+ image_list.get(i)
						+ ";\n"
						+ "output_ixmxgx89483345257873 = new File(\""
						+ dir
						+ image_list.get(i)
						+ "/"
						+ name
						+ "\");\n"
						+ "ImageIO.write(ixmxgx89483345257873, \"jpg\", output_ixmxgx89483345257873);\n"
						+ "} catch (IOException e) {\n"
						+ "e.printStackTrace();\n" + "}\n");
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
			if (change != 0) {
				run.exec("javac " + newfile_name);
				run.exec("java "
						+ newfile_name.substring(0, newfile_name.length() - 5));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void copyfile(File f1, File f2) {
		try {
			// File f1 = new File(srFile);
			// File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);

			// For Append the file.
			// OutputStream out = new FileOutputStream(f2,true);

			// For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			System.out.println("File copied.");
		} catch (FileNotFoundException ex) {
			System.out
					.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}