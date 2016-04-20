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

public class Jams
{	
	private JFrame frame;
	private final int FRAME_WIDTH = 600;
	private final int FRAME_HEIGHT = 550;
	private JFXPanel fxPanel;
	private JPanel mainPanel;
	private JPanel fileListPanel;
	private JLabel lblCurrently, lblName;
	private JButton btnOpen, btnPause, btnPlay, btnStop, btnRemove;
	private final int BUTTON_WIDTH = 120;
	private final int BUTTON_HEIGHT = 25;
	private JFileChooser jfcchooser;
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
	private JPopupMenu popMenu;
	private JMenuItem popPlay;
	
	public Jams() 
	{	
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch (Exception e)
		{
			System.out.println("Unable to use Windows look and feel, using default instead.");
		}
		
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
		popMenu = new JPopupMenu();
		popPlay = new JMenuItem("Play");
		popMenu.add(popPlay);
		fileList.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				if(event.isPopupTrigger()) {
					popMenu.show(event.getComponent(), event.getX(), event.getY());
					fileList.setSelectedIndex(fileList.locationToIndex(event.getPoint()));
				}
			}
		});
		popPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				playFile();
			}
		});
	}//Constructor
	
	private void buildFrame()
	{
		fxPanel = new JFXPanel();
		frame = new JFrame("Jams");
		
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT); //600x550
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.getContentPane().add(fxPanel);
		frame.setJMenuBar(mnuMenuBar);
		frame.getContentPane().add(mainPanel, BorderLayout.NORTH);
		frame.getContentPane().add(fileListPanel, BorderLayout.CENTER);
		frame.setVisible(true);
	}//buildFrame
	
	private void buildMainPanel()
	{
		mainPanel = new JPanel();
		
		mainPanel.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT - 450)); //600x100
		mainPanel.add(btnOpen);
		mainPanel.add(btnPause);
		mainPanel.add(btnPlay);
		mainPanel.add(btnStop);
		mainPanel.add(sldVolume);
		mainPanel.add(btnRemove);
		mainPanel.add(lblTimerPreSpace);
		mainPanel.add(lblTimeLeft);
		mainPanel.add(lblTimeSeperator);
		mainPanel.add(lblTotalTime);
	}//buildMainPanel
	
	private void buildFileListPanel()
	{
		fileListPanel = new JPanel();
		//fileListPanel.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT - 150)); //600x425
		scrollPane = new JScrollPane(fileList);
		//scrollPane.setPreferredSize(new Dimension(FRAME_WIDTH - 100, FRAME_HEIGHT - 250)); //500x300
		fileListPanel.setLayout(new BorderLayout());
		fileListPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		
		JPanel temp = new JPanel();
		JPanel temp2 = new JPanel();
		JPanel temp3 = new JPanel();
		temp.setLayout(new BorderLayout());
		temp2.setLayout(new FlowLayout());
		temp3.setLayout(new BorderLayout());
		
		temp2.add(lblCurrently);
		temp2.add(lblName);
		temp3.add(progressBar, BorderLayout.CENTER);

		temp.add(temp2, BorderLayout.NORTH);
		temp.add(temp3, BorderLayout.CENTER);

		fileListPanel.add(temp, BorderLayout.NORTH);
		fileListPanel.add(scrollPane, BorderLayout.CENTER);
	}//buildFileListPanel
	
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
	
	private void buildButtons()
	{
		btnOpen = new JButton("Open");
		btnPause = new JButton("Pause");
		btnPlay = new JButton("Play");
		btnStop = new JButton("Stop");
		btnRemove = new JButton("Remove");
		
		btnOpen.setBackground(Color.WHITE);
		btnPause.setBackground(Color.WHITE);
		btnPlay.setBackground(Color.WHITE);
		btnStop.setBackground(Color.WHITE);
		btnRemove.setBackground(Color.WHITE);
		
		btnOpen.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT)); //120x25
		btnPause.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		btnPlay.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		btnStop.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		btnRemove.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		
		btnOpen.setMnemonic('o');
		btnPause.setMnemonic('u');
		btnPlay.setMnemonic('p');
		btnStop.setMnemonic('s');
		btnRemove.setMnemonic('r');
		
		btnOpen.setToolTipText("Click to open a file chooser dialog");
		btnPause.setToolTipText("Click to pause the currently playing file");
		btnPlay.setToolTipText("Click to play the currently selected file");
		btnStop.setToolTipText("Click to stop the currently selected file");
		btnRemove.setToolTipText("Click to remove the currently selected file from the playlist");
		
		btnOpen.addActionListener(new MyButtonListener());
		btnPause.addActionListener(new MyButtonListener());
		btnPlay.addActionListener(new MyButtonListener());
		btnStop.addActionListener(new MyButtonListener());
		btnRemove.addActionListener(new MyButtonListener());
	}//buildButtons
	
	private void buildLabels()
	{
		lblCurrently = new JLabel("Currently Playing: ");
		lblName = new JLabel("");
		lblTimeLeft = new JLabel("000");
		lblTotalTime = new JLabel("000");
		lblTimeSeperator = new JLabel(" / ");
		lblTimerPreSpace = new JLabel("     ");
	}//buildLabels
	
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
	
	private void buildPlaylistSystem()
	{
		jfcchooser = new JFileChooser();
		listModel = new DefaultListModel<String>();
		fileList = new JList<String>(listModel);
		uriList = new ArrayList<URI>();
		fileNamesList = new ArrayList<String>();		
		playlistFile = new File("playlist.txt");
		counter = 0;
		
		jfcchooser.setMultiSelectionEnabled(true);
		//fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		fileList.addMouseListener(new MyMouseListener());
		fileList.setFont(new Font(btnOpen.getFont().toString(), Font.PLAIN, 12));
	}//buildPlaylistSystem
	
	private void buildTimerSystem()
	{
		timer = new Timer(1000, new MyTimerListener());
		progressBar = new JProgressBar(0, 100);
		
		progressBar.setPreferredSize(new Dimension(FRAME_WIDTH - 100, 15)); //500x15
		progressBar.setBackground(Color.WHITE);
	}//buildTimerSystem
	
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
			else if(event.getSource() == btnRemove)
			{	
				removeFile();
			}
			else
			{
				System.out.println("Error occurred in MyButtonListener");
			}
		}
	}//MyButtonListener

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
	
	private void openFile() 
	{
		if(jfcchooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			files = jfcchooser.getSelectedFiles();
			
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
			/*media.getMetadata().addListener(new MapChangeListener<String, Object>() {                 
	            public void onChanged(Change<? extends String, ? extends Object> change) {
	                if (change.wasAdded()) {
	                    if (change.getKey().equals("artist")) {
	                        lblName.setText((String) change.getValueAdded() + "  -  " );
	                     }
	                    if(change.getKey().equals("title")) {
	                    	lblName.setText((String) lblName.getText() + change.getValueAdded());
	                    }
	                }
	            }
	        });*/
			player = new MediaPlayer(media);
			player.setVolume(sldVolume.getValue() / 100.0);  //divide by 100.0 to get a double between 0 and 1
			player.play();
			lblName.setText(fileNamesList.get(fileList.getSelectedIndex()));
			frame.setTitle(fileNamesList.get(fileList.getSelectedIndex()));
			
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