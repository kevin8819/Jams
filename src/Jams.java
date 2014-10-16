import java.awt.*;
import java.awt.event.*;
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
import javax.swing.event.*;

/**
 * Jams.java
 * A simple mp3 player GUI in Java
 * @author kevin
 */
public class Jams
{	
	private JFrame frame;
	private final int FRAME_WIDTH = 600;
	private final int FRAME_HEIGHT = 550;
	private JFXPanel fxPanel;
	private JPanel mainPanel;
	private JPanel fileListPanel;
	private JLabel lblCurrently, lblName;
	private JButton btnOpen, btnPause, btnPlay, btnStop, btnRemoveSong;
	private final int BUTTON_WIDTH = 120;
	private final int BUTTON_HEIGHT = 25;
	private JFileChooser chooser;
	private JSlider sldVolume;
	private JMenuBar mnuMenuBar;
	private JMenu mnuFile;
	private JMenuItem mnuFileOpen;
	private JMenuItem mnuFileExit;
	private Media media;
	private MediaPlayer player;
	private File[] files;
	private JList<String> fileList;
	private JScrollPane scrollPane;
	private ArrayList<URI> uriList;
	private ArrayList<String> fileNamesList;
	private DefaultListModel<String> listModel;
	private int counter;
	private PrintWriter pwriter;
	private File playlistFile;
	private JLabel lblTimeLeft, lblTotalTime, lblTimeSeperator, lblTimerPreSpace;
	private int timerIndex;
	private Timer timer;
	private JProgressBar progressBar;
	private int progressBarIndex;
	
	/**
	 * Constructor. Calls various build methods to build and set up the GUI.
	 * Also calls the loadFile method to load the list of files.
	 */
	public Jams() 
	{	
		buildButtons();
		buildLabels();
		buildVolumeSlider();
		buildPlaylistSystem();
		buildTimerSystem();
		buildMenu();
		buildMainPanel();
		buildFileListPanel();
		buildFrame();
		loadFile();
	}//Constructor
	
	/**
	 * Instantiates and sets up the JFrame.
	 * Sets up the size, layout, menus, content panes, etc.
	 */
	private void buildFrame()
	{
		fxPanel = new JFXPanel();
		frame = new JFrame("Jams");
		
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT); //600x550
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		frame.getContentPane().add(fxPanel);
		frame.setJMenuBar(mnuMenuBar);
		frame.getContentPane().add(mainPanel);
		frame.getContentPane().add(fileListPanel);
		frame.setVisible(true);
	}//buildFrame
	
	/**
	 * Instantiates and sets up the main panel of the GUI. 
	 * The main panel holds all the buttons, volume slider, and timer labels
	 */
	private void buildMainPanel()
	{
		mainPanel = new JPanel();
		
		mainPanel.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT - 450)); //600x100
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
	}//buildMainPanel
	
	/**
	 * Instantiates and sets up the file list panel of the GUI.
	 * The file list panel holds the currently playing label as well as the
	 * scroll pane that contains file names.
	 */
	private void buildFileListPanel()
	{
		fileListPanel = new JPanel();
		fileListPanel.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT - 150)); //600x425
		
		scrollPane = new JScrollPane(fileList);
		scrollPane.setPreferredSize(new Dimension(FRAME_WIDTH - 100, FRAME_HEIGHT - 250)); //500x300
		
		fileListPanel.add(lblCurrently);
		fileListPanel.add(lblName);
		fileListPanel.add(progressBar);
		fileListPanel.add(scrollPane);
	}//buildFileListPanel
	
	/**
	 * Instantiates and sets up the menu system including
	 * the menu bar, menu, and menu items.
	 */
	private void buildMenu()
	{
		mnuMenuBar = new JMenuBar();
		mnuFile = new JMenu("File");
		mnuFileOpen = new JMenuItem("Open");
		mnuFileExit = new JMenuItem("Exit");
		
		mnuFileOpen.addActionListener(new MyMenuListener());
		mnuFileExit.addActionListener(new MyMenuListener());
		
		mnuFile.add(mnuFileOpen);
		mnuFile.add(mnuFileExit);
		mnuMenuBar.add(mnuFile);
	}//buildMenu
	
	/**
	 * Instantiates and sets up the buttons.
	 * Sets the color, size, mnemonic, tooltip, and adds 
	 * the action listener for each button.
	 */
	private void buildButtons()
	{
		btnOpen = new JButton("Open");
		btnPause = new JButton("Pause");
		btnPlay = new JButton("Play");
		btnStop = new JButton("Stop");
		btnRemoveSong = new JButton("Remove Song");
		
		btnOpen.setBackground(Color.WHITE);
		btnPause.setBackground(Color.WHITE);
		btnPlay.setBackground(Color.WHITE);
		btnStop.setBackground(Color.WHITE);
		btnRemoveSong.setBackground(Color.WHITE);
		
		btnOpen.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT)); //120x25
		btnPause.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		btnPlay.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		btnStop.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		btnRemoveSong.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		
		btnOpen.setMnemonic('o');
		btnPause.setMnemonic('u');
		btnPlay.setMnemonic('p');
		btnStop.setMnemonic('s');
		btnRemoveSong.setMnemonic('r');
		
		btnOpen.setToolTipText("Click to open a file chooser dialog");
		btnPause.setToolTipText("Click to pause the currently playing file");
		btnPlay.setToolTipText("Click to play the currently selected file");
		btnStop.setToolTipText("Click to stop the currently selected file");
		btnRemoveSong.setToolTipText("Click to remove the currently selected file from the playlist");
		
		btnOpen.addActionListener(new MyButtonListener());
		btnPause.addActionListener(new MyButtonListener());
		btnPlay.addActionListener(new MyButtonListener());
		btnStop.addActionListener(new MyButtonListener());
		btnRemoveSong.addActionListener(new MyButtonListener());
	}//buildButtons
	
	/**
	 * Instantiates and sets up the labels.
	 * The labels show the file currently playing as well as the timer
	 */
	private void buildLabels()
	{
		lblCurrently = new JLabel("Currently Playing: ");
		lblName = new JLabel("");
		lblTimeLeft = new JLabel("000");
		lblTotalTime = new JLabel("000");
		lblTimeSeperator = new JLabel(" / ");
		lblTimerPreSpace = new JLabel("     ");
	}//buildLabels
	
	/**
	 * Instantiates and sets up the volume slider.
	 * Adds the change listener to the slider as well.
	 */
	private void buildVolumeSlider()
	{
		sldVolume = new JSlider(JSlider.HORIZONTAL, 0, 100, 33);
		
		sldVolume.addChangeListener(new MySliderListener());
		sldVolume.setSize(200, 25);
		sldVolume.setMajorTickSpacing(50);
		sldVolume.setMinorTickSpacing(10);
		sldVolume.setPaintTicks(true);
		sldVolume.setForeground(Color.BLACK);
		sldVolume.setToolTipText("Slider to change the volume");
	}//buildVolumeSlider
	
	/**
	 * Instantiates and sets up the playlist system.
	 * The playlist system uses a file chooser to select files, and contains
	 * ArrayList objects to handle the file names and locations.
	 */
	private void buildPlaylistSystem()
	{
		chooser = new JFileChooser();
		listModel = new DefaultListModel<String>();
		fileList = new JList<String>(listModel);
		uriList = new ArrayList<URI>();
		fileNamesList = new ArrayList<String>();		
		playlistFile = new File("playlist.txt");
		counter = 0;
		
		chooser.setMultiSelectionEnabled(true);
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileList.addMouseListener(new MyMouseListener());
	}//buildPlaylistSystem
	
	/**
	 * Instantiates and sets up the timer system.
	 * The timer system includes a timer object and progress bar that
	 * is sync'd with the timer.
	 */
	private void buildTimerSystem()
	{
		timer = new Timer(1000, new MyTimerListener());
		progressBar = new JProgressBar(0, 100);
		
		progressBar.setPreferredSize(new Dimension(FRAME_WIDTH - 100, 15)); //500x15
		progressBar.setBackground(Color.WHITE);
	}//buildTimerSystem
	
	/**
	 * Private class to handle button events generated by the GUI.
	 * @author kevin
	 *
	 */
	private class MyButtonListener implements ActionListener 
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
					//catch exception when pause is pushed but no player exists
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
					//catch exception when play is pushed but no player exists
				}

			}
			else if(event.getSource() == btnStop)
			{	
				try 
				{	
					player.stop();
					timer.stop();
					lblTimeLeft.setText("000");
					lblTotalTime.setText("000");
					lblName.setText("");
					progressBar.setValue(0);
				}
				catch(NullPointerException e) 
				{	
					//catch exceptions when stop is pushed but no player exists
				}
			}
			else if(event.getSource() == btnRemoveSong)
			{	
				removeFile();
			}
			else
			{
				System.out.println("Error occurred in MyButtonListener");
			}
		}
	}//MyButtonListener

	/**
	 * Private class to handle the change events for the volume slider.
	 * @author kevin
	 *
	 */
	private class MySliderListener implements ChangeListener
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
				//catch exceptions when volume is changed but no player exists
			}
		}
	}//MySliderListener
	
	/**
	 * Private class to handle the events for the menu system
	 * @author kevin
	 *
	 */
	private class MyMenuListener implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent event) 
		{	
			if(event.getSource() == mnuFileOpen) 
			{	
				openFile();
			}
			else if(event.getSource() == mnuFileExit)
			{	
				System.exit(0);
			}
			else
			{
				System.out.println("Error occurred in MyMenuListener");
			}
		}
	}//MyMenuListener
	
	/**
	 * Private class to handle the events for the user using the mouse on the JList.
	 * Extends MouseAdapter so no need to add all the unused methods, only need to
	 * add mouseClicked method.
	 * @author kevin
	 *
	 */
	private class MyMouseListener extends MouseAdapter 
	{
		@Override
		public void mouseClicked(MouseEvent event) 
		{	
			if(event.getClickCount() == 2)  //represents double-click
			{	
				playFile();
			}
		}
	}//MyMouseListener
	
	/**
	 * Private class to handle the timer generated events.
	 * The timer will generate an event once every 1000 milliseconds.
	 * @author kevin
	 *
	 */
	private class MyTimerListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{
			timerIndex--;
			progressBarIndex++;
			lblTimeLeft.setText("" + timerIndex);
			progressBar.setValue(progressBarIndex);
			if(timerIndex == -1)
			{
				timer.stop();
				fileList.setSelectedIndex(fileList.getSelectedIndex() + 1);
				if((fileList.getSelectedIndex() + 1) == fileNamesList.size())
				{
					fileList.setSelectedIndex(0);
				}

				playFile();
			}
		}
	}//MyTimerListener
	
	/**
	 * Method to handle opening a file. The method uses the JFileChooser to choose
	 * one or more files. The files are added to the URI ArrayList. Next the String
	 * ArrayList is filled with the actual file names. Last the files are added to
	 * the JList using the file names.
	 */
	private void openFile() 
	{
		if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			files = chooser.getSelectedFiles();
			
			for(int i = 0; i < files.length; i++)  
			{  
				uriList.add(files[i].toURI());
			}
			
			for(int i = 0; i < files.length; i++)  
			{
				fileNamesList.add(files[i].getName());
			}
			
			for(int i = 0; i < files.length; i++)  
			{		
				listModel.addElement(fileNamesList.get(i + counter));
			}
			
			counter += files.length;
			writeFile();
		}
	}//openFile
	
	/**
	 * Plays a file using the Media and MediaPlayer. A new Media and MediaPlayer
	 * object needs to be created for every new file that is played. The progress
	 * bar and timer text is set up when the user selects the file.
	 */
	private void playFile() 
	{	
		try 
		{	
			player.stop();
		}
		catch(NullPointerException e)
		{
			//catch exceptions when no player exists
		}

		try 
		{
			media = new Media(uriList.get(fileList.getSelectedIndex()).toString());
			player = new MediaPlayer(media);
			player.setVolume(sldVolume.getValue() / 100.0);  //divide by 100.0 to get a double between 0 and 1
			player.play();
			lblName.setText(fileNamesList.get(fileList.getSelectedIndex()));
			
			try
			{
				//sleep so that player.getStopTime() doesn't return "UNKNOWN"
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				
			}

			StringBuilder sb = new StringBuilder(player.getStopTime().toString());
			sb.delete((sb.length() - 3), sb.length());  //delete the " ms" on the end of the string

			double d = Double.parseDouble(sb.toString());
			d /= 1000;  //convert milliseconds to seconds
			timerIndex = (int) d;  //get rid of decimal point
			lblTimeLeft.setText("" + timerIndex);
			lblTotalTime.setText("" + timerIndex);
			progressBar.setValue(0);  //reset progress bar
			progressBar.setMaximum(timerIndex);
			progressBarIndex = 0;
			timer.start();
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			//catch exception if file is removed and play is pressed
		}
	}//playFile
	
	/**
	 * Removes a file from the GUI. The file gets deleted from
	 * the JList, URI list, String list. Then calls the writeFile
	 * method to rewrite the playlist text file.
	 */
	private void removeFile() 
	{
		try
		{
			int toRemove = fileList.getSelectedIndex();
			listModel.removeElementAt(toRemove);
			uriList.remove(toRemove);
			fileNamesList.remove(toRemove);
			counter--;
			writeFile();
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			//catch exception if remove file is pressed but no file is selected
		}
	}//removeFile
	
	/**
	 * Writes the file list locations (URI) to a text file using
	 * a PrintWriter object. The PrintWriter will automatically
	 * generate a playlist text file if one is not found.
	 */
	private void writeFile()
	{
		try 
		{
			pwriter = new PrintWriter("playlist.txt");
		}
		catch (FileNotFoundException e) 
		{
			//catch file not found exception
		}
		
		for(int i = 0; i < listModel.getSize(); i++)
		{
			URI u = uriList.get(i);
			pwriter.println(u.getPath());
		}
		
		pwriter.close();
	}//writeFile
	
	/**
	 * Loads the files from a text file into the GUI JList.
	 * The file URI are loaded into a String ArrayList, which are then
	 * added to the URI list and the file names list and the JList.
	 */
	private void loadFile()
	{
		ArrayList<String> s = new ArrayList<String>();
		Scanner scan;
		
		try 
		{
			scan = new Scanner(playlistFile);
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
				fileNamesList.add(loadFileArray[i].getName());
			}
			
			for(int i = 0; i < loadFileArray.length; i++)
			{
				listModel.addElement(fileNamesList.get(i));
			}
			
			counter += loadFileArray.length;
		} 
		catch (FileNotFoundException e) 
		{
			//catch exception when program is loaded but no playlist.txt file exists
			//file will be automatically created by PrintWriter later
		}
	}//loadFile
	
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
	}//main
}//Jams