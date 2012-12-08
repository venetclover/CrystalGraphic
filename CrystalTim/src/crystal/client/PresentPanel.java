package crystal.client;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class PresentPanel extends JPanel {

	// The pictures folder path
	//public static String picPath = "./dropbox/";
	//private String picPath = "./dropbox/";
	
	private JDialog presentDialog;
	
	private String picPath ;
	private String dir;
	private ArrayList<String> image_map;
	
	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	// The picture tpyes can be shown by this software
	private static String[] picTypes = {".jpg", ".png", ".gif", ".bmp"};

	// The value can be 2 or 3. 
	// There are 7 pictures it is 3. 5 or 6 pictures it is asigned 3 
	public static int layer;	
	
	// Whitespace between pictures
	public static int seprator = 5;
	
	// Properties of a picture
	public static int pictureWidth = 10;
	public static int pictureHeight = 10;
	
	// Properties of the label display the information
	// which picture you have selected
	public static int disLabelWidth = 100;
	public static int disLabelHeight = 20;
	
	public static int panelWidth = 100;
	public static int panelHeight = 100;
	
	// This matric determine the location of each picture
	// Also determine there are three pictures each line
	private int[][] locationMatric = new int[][] { { 0, 0 }, { 0, 1 }, { 0, 2 },
			                                       { 1, 0 }, { 1, 1 }, { 1, 2 },
			                                       { 3, 0 }, { 3, 1 }, { 3, 2 } };
	
	private int number;	// The number of pictures
	private static int selectedIndex; // The index of the picture you have selected
	private String[] picNames; // All picture names in the picPath
	JFrame parentFrame;

	JLabel[] labels;	//	The labels display the pictures
	JLabel disLabel;	//	The label display the information
	JButton next;	// The next button

	GridBagLayout layout = new GridBagLayout();
	GridBagConstraints layoutC = new GridBagConstraints();

	public String getSelectedFile(){
		return picPath + picNames[selectedIndex];
	}
	
	
	public static String[] filterFiles(String[] files){
		List<String> picNameList = new ArrayList<String>();
		for (String file : files){
			for (String type : picTypes){
				if (file.endsWith(type)){
					picNameList.add(file);
					break;
				}
			}
		}
		
		String[] picNames = new String[picNameList.size()];
		for (int i = 0; i < picNames.length;i ++){
			picNames[i] = picNameList.get(i);
		}
		
		return picNames;
	}
	
	public PresentPanel(String[] files, JFrame parentFrame, String dir, ArrayList<String> image_map) {
		super();
		this.dir = dir;
		this.image_map =  image_map;
		picNames = files;
		this.parentFrame = parentFrame;
		this.number = files.length;
		this.labels = new JLabel[number];
		layer = number > 7 ? 3 : 2;
		init();
	}
	
	public PresentPanel(String[] files, JFrame parentFrame,String strPath, String dir, ArrayList<String> image_map) {
		super();
		this.dir = dir;
		this.image_map =  image_map;
		this.setPicPath(strPath);
		picNames = files;
		this.parentFrame = parentFrame;
		this.number = files.length;
		this.labels = new JLabel[number];
		layer = number > 7 ? 3 : 2;
		init();
	}

	void init() {
		panelWidth = 100;
		panelHeight = 100;
		int h[] = {0, 0, 0};
		int w[] = {0, 0, 0};
		//this.setSize(width, height)
		this.setLayout(layout);
		layoutC.gridwidth = pictureWidth;
		layoutC.gridheight = pictureHeight;
		
		//Set each label of picture
		for (int i = 0; i < number; i++) {
			labels[i] = new JLabel();
			labels[i].setName(i + "");
			layoutC.gridy = locationMatric[i][0] * layoutC.gridheight
					+ seprator;
			layoutC.gridx = locationMatric[i][1] * layoutC.gridwidth + seprator;
			
			//System.out.println("x:"+layoutC.gridx +" y:"+layoutC.gridy);
			
			labels[i].addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseClicked(MouseEvent e) {
					// Something to do when a picture is selected
					//disLabel.setText(image_map.get(Integer.parseInt(((JLabel) e.getSource()).getName())));
					selectedIndex = Integer.parseInt(((JLabel) e.getSource())
							.getName());
					
					String strPath = dir+"/"+ image_map.get(Integer.parseInt(((JLabel) e.getSource())
							.getName()))+"/";//$$$$
					File file = new File(strPath);
					if (file.isDirectory()) {
						String[] files = PresentPanel.filterFiles(file.list());
						PresentPanelDetail presentPanel = new PresentPanelDetail(files, parentFrame,strPath,dir);
						presentPanel.setPicPath(strPath);
						//if(presentDialog!=null)
							//presentDialog.dispose();
						presentDialog = new JDialog(parentFrame);
						presentDialog.setTitle( image_map.get(Integer.parseInt(((JLabel) e.getSource()).getName())));
						presentDialog.setSize(presentPanel.panelWidth+50,presentPanel.panelHeight+50);
						//presentDialog.setSize(500, 500);
						presentDialog.setLocation(e.getX()+300, e.getY()+100);
						presentDialog.add(presentPanel);
						presentDialog
								.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						presentDialog.setVisible(true);
					} else {
						System.err.println("There is no " + strPath
								+ "folder!");
						return;
					}

				}
			});
			
			//$$$$$$
			Image image_scale;
			try {  
	            BufferedImage src = ImageIO.read(new File(picPath +"/"+ picNames[i])); 
	            int width = src.getWidth();  
	            int height = src.getHeight();             
	            image_scale = src.getScaledInstance(width / 2, height / 2,Image.SCALE_SMOOTH);  
	            labels[i].setIcon(new ImageIcon(image_scale));
	            
	            w[i/3] += width/2;
	            h[i%3] += height/2;
	            
				//labels[i].setIcon(new ImageIcon(picPath + picNames[i]));
				this.add(labels[i], layoutC);
			} catch (IOException e) {  
		            e.printStackTrace();  
		    }  
		}
		
		for(int j=0;j<3;j++)
		{
			if(w[j] > panelWidth)
				panelWidth = w[j];
			
			if(h[j] > panelHeight)
				panelHeight = h[j];
		}

		disLabel = new JLabel();
		next = new JButton("Next");

		// The label for display message
		layoutC.gridx = seprator;
		layoutC.gridy = 450;
		layoutC.gridwidth = disLabelWidth;
		layoutC.gridheight = disLabelHeight;
		this.add(disLabel, layoutC);

		// The next button
		layoutC.gridx = disLabelWidth + 2 * seprator;
		next.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//	When next button is clicked
				JOptionPane.showMessageDialog(null, picPath +"/"+ picNames[selectedIndex]);
				
			}
		});
		//this.add(next, layoutC);

		this.setVisible(true);

	}

}
