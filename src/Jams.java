import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

////////////////////////////////////////////
//
//  Jams.java
//  A simple mp3 player GUI in Java
//
////////////////////////////////////////////

public class Jams
{	
	private JFrame frame;
	private final int FRAME_WIDTH = 600;
	private JFXPanel fxPanel;
	private JPanel mainPanel;
	private JPanel fileListPanel;
	private ImageIcon pony;
	private JLabel lblImage, lblCurrently, lblName;
	private JButton btnOpen, btnPause, btnPlay, btnStop, btnRemoveSong;
	private JFileChooser chooser;
	private JSlider sldVolume;
	private JMenuBar mnuMenuBar;
	private JMenu mnuMenu;
	private JMenuItem mnuOpen;
	private JMenuItem mnuExit;
	private Media media;
	private MediaPlayer player;
	private ButtonListener btnListener;
	private SliderListener sldListener;
	private MenuListener mnuListener;
	private MouseClickListener mouseClickListener;
	private File[] files;
	private JList<String> fileList;
	private JScrollPane scrollPane;
	private ArrayList<URI> uriList;
	private ArrayList<String> songNames;
	private DefaultListModel<String> listModel;
	private int counter;
	private PrintWriter pwriter;
	private File songFile;
	private JLabel lblTimeLeft, lblTotalTime, lblTimeSeperator, lblTimerPreSpace;
	private int timerIndex;
	private Timer timer;
	
	public Jams() 
	{	
		frame = new JFrame("Jams");
		fxPanel = new JFXPanel();
		mainPanel = new JPanel();
		fileListPanel = new JPanel();
		pony = new ImageIcon("pony.png");
		lblImage = new JLabel(pony);
		btnOpen = new JButton("Open");
		btnPause = new JButton("Pause");
		btnPlay = new JButton("Play");
		btnStop = new JButton("Stop");
		btnRemoveSong = new JButton("Remove Song");
		chooser = new JFileChooser();
		sldVolume = new JSlider(JSlider.HORIZONTAL, 0, 100, 40);
		lblCurrently = new JLabel("Currently Playing: ");
		lblName = new JLabel("");
		mnuMenuBar = new JMenuBar();
		mnuMenu = new JMenu("File");
		mnuOpen = new JMenuItem("Open");
		mnuExit = new JMenuItem("Exit");
		btnListener = new ButtonListener();
		sldListener = new SliderListener();
		mnuListener = new MenuListener();
		mouseClickListener = new MouseClickListener();
		listModel = new DefaultListModel<String>();
		fileList = new JList<String>(listModel);
		uriList = new ArrayList<URI>();
		songNames = new ArrayList<String>();
		scrollPane = new JScrollPane(fileList);
		counter = 0;
		songFile = new File("songs.txt");
		lblTimeLeft = new JLabel("000");
		lblTotalTime = new JLabel("000");
		lblTimeSeperator = new JLabel(" / ");
		lblTimerPreSpace = new JLabel("     ");
		timer = new Timer(1000, new TimerListener());
		
		createAndShowGUI();
		loadFile();
	}//end of constructor
	
	private void createAndShowGUI()
	{
		sldVolume.setSize(200, 25);
		sldVolume.setMajorTickSpacing(20);
		sldVolume.setMinorTickSpacing(10);
		sldVolume.setPaintTicks(true);
		sldVolume.setPaintLabels(true);
		
		chooser.setMultiSelectionEnabled(true);
		
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		scrollPane.setPreferredSize(new Dimension(500, 300));
		
		btnOpen.addActionListener(btnListener);
		btnPause.addActionListener(btnListener);
		btnPlay.addActionListener(btnListener);
		btnStop.addActionListener(btnListener);
		btnRemoveSong.addActionListener(btnListener);
		sldVolume.addChangeListener(sldListener);
		mnuOpen.addActionListener(mnuListener);
		mnuExit.addActionListener(mnuListener);
		fileList.addMouseListener(mouseClickListener);
		
		mnuMenuBar.add(mnuMenu);
		mnuMenu.add(mnuOpen);
		mnuMenu.add(mnuExit);
		
		mainPanel.setPreferredSize(new Dimension(FRAME_WIDTH, 100));
		mainPanel.add(lblImage);
		mainPanel.add(btnOpen);
		mainPanel.add(btnPause);
		mainPanel.add(btnPlay);
		mainPanel.add(btnStop);
		mainPanel.add(sldVolume);
		mainPanel.add(btnRemoveSong);
		mainPanel.add(lblTimerPreSpace);
		mainPanel.add(lblTimeLeft);
		mainPanel.add(lblTimeSeperator);
		mainPanel.add(lblTotalTime);
		
		fileListPanel.setPreferredSize(new Dimension(FRAME_WIDTH, 400));
		fileListPanel.add(lblCurrently);
		fileListPanel.add(lblName);
		fileListPanel.add(scrollPane);
		
		frame.setSize(600, 525);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		frame.add(fxPanel);
		frame.setJMenuBar(mnuMenuBar);
		frame.add(mainPanel);
		frame.add(fileListPanel);
		frame.setVisible(true);
	}//end of createAndShowGUI
		
	private class ButtonListener implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{
			if(event.getSource() == btnOpen) 
			{	
				openFile();
			}
			else if(event.getSource() == btnPause) 
			{	
				try
				{
					if(player.getStatus() != MediaPlayer.Status.PAUSED)
					{
						player.pause();
						timer.stop();
					}
					else
					{
						player.play();
						timer.start();
					}
				}
				catch(NullPointerException e)
				{
					//catch exception when pause is pushed but no file is playing
				}
			}
			else if(event.getSource() == btnPlay) 
			{
				try
				{
					if(player.getStatus() == MediaPlayer.Status.PAUSED)
					{
						player.play();
						timer.start();
					}
					else
					{
						playFile();
					}
				}
				catch(NullPointerException e)
				{
					//catch exception when play is pushed but no file is paused
				}

			}
			else if(event.getSource() == btnRemoveSong)
			{	
				removeFile();
			}
			else 
			{	
				try 
				{	
					player.stop();
					timer.stop();
					lblTimeLeft.setText("000");
					lblTotalTime.setText("000");
					lblName.setText("");
				}
				catch(NullPointerException e) 
				{	
					//catch exceptions when stop is pushed but no file is playing
				}
			}
		}
	}//end of ButtonListener

	private class SliderListener implements ChangeListener
	{
		@Override
		public void stateChanged(ChangeEvent event) 
		{	
			try 
			{	
				player.setVolume(sldVolume.getValue() / 100.0);  //divide by 100.0 to get a double between 0 and 1
			}
			catch(NullPointerException e) 
			{	
				//catch exceptions when volume is changed but no file is playing
			}
		}
	}//end of SliderListener
	
	private class MenuListener implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent event) 
		{	
			if(event.getSource() == mnuOpen) 
			{	
				openFile();
			}
			else 
			{	
				System.exit(0);
			}
		}
	}//end of MenuListener
	
	private class MouseClickListener implements MouseListener 
	{
		@Override
		public void mouseClicked(MouseEvent e) 
		{	
			if(e.getClickCount() == 2)  //represents double-click
			{	
				playFile();
			}
		}

		@Override
		public void mousePressed(MouseEvent e) 
		{
			//nothing
		}

		@Override
		public void mouseReleased(MouseEvent e) 
		{
			//nothing
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			//nothing
		}

		@Override
		public void mouseExited(MouseEvent e) 
		{
			//nothing
		}
	}//end of mouseClickListener
	
	private class TimerListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{
			lblTimeLeft.setText("" + (timerIndex - 1));
			timerIndex--;
			if(timerIndex == -1)
			{
				timer.stop();
				fileList.setSelectedIndex(fileList.getSelectedIndex() + 1);
				playFile();
			}
		}
	}//end of TimerListener
	
	public void openFile() 
	{
		if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			files = chooser.getSelectedFiles();
			
			for(int i = 0; i < files.length; i++)  //use URI ArrayList to fill with files converted to URI
			{  
				uriList.add(files[i].toURI());
			}
			
			for(int i = 0; i < files.length; i++)  //use String ArrayList to fill with the names of the files
			{
				songNames.add(files[i].getName());
			}
			
			for(int i = 0; i < files.length; i++)  //adding the song names to the JList
			{		
				listModel.addElement(songNames.get(i + counter));
			}
			
			counter += files.length;
			writeFile();
		}
	}//end of openFile
	
	public void playFile() 
	{	
		try 
		{	
			player.stop();
		}
		catch(NullPointerException e) 
		{	
			//catch exceptions when player is not playing
		}

		try 
		{
			media = new Media(uriList.get(fileList.getSelectedIndex()).toString());
			player = new MediaPlayer(media);
			player.setVolume(sldVolume.getValue() / 100.0);  //divide by 100.0 to get a double between 0 and 1
			player.play();
			lblName.setText(songNames.get(fileList.getSelectedIndex()));
			
			try
			{
				Thread.sleep(100);  //sleep so that player.getStopTime() doesn't return "UNKNOWN"
			}
			catch (InterruptedException e)
			{
				//nothing
			}

			StringBuilder sb = new StringBuilder(player.getStopTime().toString());
			sb.delete((sb.length() - 3), sb.length());  //delete the " ms" on the end of the string

			double d = Double.parseDouble(sb.toString());
			d /= 1000;  //convert milliseconds to seconds
			timerIndex = (int) d;  //get rid of decimal point
			lblTimeLeft.setText("" + timerIndex);
			lblTotalTime.setText("" + timerIndex);
			timer.start();
		}
		catch(ArrayIndexOutOfBoundsException e) 
		{
			//catch exception if file is removed and play is pressed
		}
	}//end of playFile
	

	
	public void removeFile() 
	{
		int toRemove = fileList.getSelectedIndex();
		listModel.removeElementAt(toRemove);
		uriList.remove(toRemove);
		songNames.remove(toRemove);
		counter--;
		writeFile();
	}//end of removeFile
	
	
	public void writeFile()
	{
		try 
		{
			pwriter = new PrintWriter("songs.txt");
		}
		catch (FileNotFoundException e) 
		{
			//catch file not found exception
			//shouldn't occur because PrintWriter will create blank file if it's not found
		}
		
		for(int i = 0; i < listModel.getSize(); i++)
		{
			URI u = uriList.get(i);
			pwriter.println(u.getPath());
		}
		
		pwriter.close();
	}//end of writeFile
	
	
	public void loadFile()
	{
		ArrayList<String> s = new ArrayList<String>();
		Scanner scan;
		
		try 
		{
			scan = new Scanner(songFile);
			while(scan.hasNextLine())
			{
				s.add(scan.nextLine());
			}
			scan.close();
			
			File[] loadFileArray = new File[s.size()];

			for(int i = 0; i < loadFileArray.length; i++)
			{
				loadFileArray[i] = new File(s.get(i));
			}
			
			for(int i = 0; i < loadFileArray.length; i++)
			{
				uriList.add(loadFileArray[i].toURI()); 
			}
			
			for(int i = 0; i < loadFileArray.length; i++)
			{
				songNames.add(loadFileArray[i].getName());
			}
			
			for(int i = 0; i < loadFileArray.length; i++)
			{
				listModel.addElement(songNames.get(i));
			}
			
			counter += loadFileArray.length;
		} 
		catch (FileNotFoundException e) 
		{
			//catch exception when class is loaded but no songs.txt file exists
			//file will be automatically created by PrintWriter later
		}
	}//end of loadFile
	
	public static void main(String[] args) 
	{	
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				new Jams();
			}
		});
	}//end of main
}//end of Jams