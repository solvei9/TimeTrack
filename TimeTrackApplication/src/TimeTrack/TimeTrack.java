package TimeTrack;

import static javax.swing.GroupLayout.Alignment.BASELINE;

import java.awt.AWTException;
import java.awt.Button;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class TimeTrack {
	static String projectListFilePath = new String("D:/Tmp/Projects.txt");
	static String taskListFilePath = new String("D:/Tmp/Tasks.txt");
	static String JournalFilePath = new String("D:/Tmp/Journal.txt");
	static String SettingsFilePath = new String("D:/Tmp/Settings.txt");
	static String IconFilePath = new String("D:/Tmp/icons/alarm-clock-blue.jpg");
	static String HelpFilePath = new String("D:/Tmp/TimeTrack.chm");
	// static String projectListFilePath = new String("/home/serene/TimeTrack/Projects.txt"); // � Ubuntu � ��������������
	// ������
	// jar �� ����������� ���������,
	// �������� ���� � jdk
	// static String taskListFilePath = new String("/home/serene/TimeTrack/Tasks.txt");
	// static String JournalFilePath = new String("/home/serene/TimeTrack/Journal.txt");
	// static String SettingsFilePath = new String("/home/serene/TimeTrack/Settings.txt");
	// static String IconFilePath = new String("/home/serene/TimeTrack/Icons/alarm-clock-blue.png");
	// static String HelpFilePath = new String("/home/serene/TimeTrack/Doc/TimeTrack.chm");
	static File projectListFile;
	static File taskListFile;
	static File journalFile;
	static File settingsFile;
	static ArrayList<Project> projectList = new ArrayList<Project>();
	static ArrayList<Task> taskList = new ArrayList<Task>();
	static ArrayList<Journal> journalList = new ArrayList<Journal>();
	static Integer JournalCounter;
	static String ResourceLang;
	static TrayWindow mainFrame;
	static JComboBox<String> projectListBox;
	static JComboBox<String> projectTaskListBox;
	static JSpinner dateStart;
	static JSpinner dateFinish;

	public static void main(String[] args) {
		UIManager.put("OptionPane.yesButtonText", "��");
		UIManager.put("OptionPane.noButtonText", "���");
		UIManager.put("OptionPane.cancelButtonText", "��������");
		// ����������� �������� �������� ��������
		settingsFile = new File(SettingsFilePath);
		// �������� ������� ����� ��������
		if (settingsFile.exists()) {
			// ���������� ������� � �������
			JournalCounter = Integer.valueOf(getSetting("JournalCounter"));
			// ���� ����������
			ResourceLang = getSetting("ResourceLang");
		} else {
			// ���������� ������� � �������
			JournalCounter = 1;
			// ���� ����������
			ResourceLang = "ru";
			setSettings();
		}
		// ������� ���� ���������
		try {
			mainFrame = new TrayWindow("");
			createMainFrame(mainFrame);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
	}

	public static void createMainFrame(TrayWindow mainFrame) {
		mainFrame.removeAll();
		mainFrame.setTitle("���� �������� �������");

		// ��������� ������� ������������ �����������
		mainFrame.setLayout(new BoxLayout(mainFrame, BoxLayout.PAGE_AXIS));
		// ����������� ������ ������ ����
		mainFrame.setResizable(false);

		// ������ ����
		JPanel panelMenu = new JPanel();
		panelMenu.setLayout(new FlowLayout(FlowLayout.LEFT));
		// ��������� ������ ����
		panelMenu.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), null));
		mainFrame.add(panelMenu);
		// ������� �������� ����
		JMenuBar menuBar = new JMenuBar();
		panelMenu.add(menuBar);

		// ������� ������� "�������"
		JMenu menuProject = new JMenu(Resources.getProjects(ResourceLang));
		menuBar.add(menuProject);
		// ������� �������� ������� "�������"
		JMenuItem addProjectItem = new JMenuItem("�������� ������");
		JMenuItem editProjectItem = new JMenuItem("�������� ������");
		JMenuItem deleteProjectItem = new JMenuItem("������� ������");
		// ��������� ����������� ������� �� ������ �������
		addProjectItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addProject(projectListBox);
			}
		});
		editProjectItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editProject();
			}
		});
		deleteProjectItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteProject();
			}
		});

		// ��������� ��������� �������� ������� "�������"
		menuProject.add(addProjectItem);
		menuProject.add(editProjectItem);
		menuProject.add(deleteProjectItem);

		// ������� ������� "������"
		JMenu menuTask = new JMenu("������");
		menuBar.add(menuTask);
		// ������� �������� ������� "������"
		JMenuItem addTaskItem = new JMenuItem("�������� ������");
		JMenuItem editTaskItem = new JMenuItem("�������� ������");
		JMenuItem deleteTaskItem = new JMenuItem("������� ������");
		// ��������� ����������� ������� �� ������ �������
		addTaskItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addTask();
			}
		});
		editTaskItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editTask();
			}
		});
		deleteTaskItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteTask();
			}
		});
		// ��������� ��������� �������� ������� "������"
		menuTask.add(addTaskItem);
		menuTask.add(editTaskItem);
		menuTask.add(deleteTaskItem);

		// ������� ������� "������"
		JMenu menuReport = new JMenu("������");
		menuBar.add(menuReport);
		// ������� �������� ������� "������"
		JMenuItem dayReportItem = new JMenuItem("����� �� ����");
		JMenuItem periodReportItem = new JMenuItem("����� �� ������");
		JMenuItem projectReportItem = new JMenuItem("����� �� �������");
		JMenuItem taskReportItem = new JMenuItem("����� �� ������");
		// ��������� ����������� ������� �� ������ �������
		dayReportItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dayReport();
			}
		});
		periodReportItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				periodReport();
			}
		});
		projectReportItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				projectReport();
			}
		});
		taskReportItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				taskReport();
			}
		});
		// ��������� ��������� �������� ������� "������"
		menuReport.add(dayReportItem);
		menuReport.add(periodReportItem);
		menuReport.add(projectReportItem);
		menuReport.add(taskReportItem);

		// ������� ������� "������"
		JMenu menuJournal = new JMenu("������");
		menuBar.add(menuJournal);
		// ������� �������� ������� "������"
		JMenuItem editJournalItem = new JMenuItem("�������������");
		// ��������� ����������� ������� �� ������ �������
		editJournalItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editJournal();
			}
		});
		// ��������� ��������� �������� ������� "������"
		menuJournal.add(editJournalItem);

		// ������� ������� "���������"
		JMenu menuLang = new JMenu("���������");
		menuBar.add(menuLang);
		// ������� �������� ������� "���������"
		JMenuItem enLang = new JMenuItem("����������");
		JMenuItem ruLang = new JMenuItem("�������");
		// ��������� ����������� ������� �� ������ �������
		enLang.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeLang("en");
			}
		});
		ruLang.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeLang("ru");
			}
		});
		// ��������� ��������� �������� ������� "���������"
		menuLang.add(enLang);
		menuLang.add(ruLang);
		// ���������� ��������� ��������� � ����������� �� �������� ����� ����������
		if (TimeTrack.ResourceLang.equals("ru")) {
			ruLang.setVisible(false);
		}
		if (TimeTrack.ResourceLang.equals("en")) {
			enLang.setVisible(false);
		}

		// ������� ������� "?"
		JMenu menuHelp = new JMenu("?");
		menuBar.add(menuHelp);
		// ������� �������� ������� "?"
		JMenuItem helpItem = new JMenuItem("����� �������");
		JMenuItem infoItem = new JMenuItem("� ���������");
		// ��������� ����������� ������� �� ������ �������
		helpItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				callHelp();
			}
		});
		infoItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				callInfo();
			}
		});
		// ��������� ��������� �������� ������� "?"
		menuHelp.add(helpItem);
		menuHelp.add(infoItem);

		// ������ �������
		JPanel panelProject = new JPanel();
		// ��������� ������ ������
		panelProject.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		panelProject.setLayout(new GridLayout(5, 2, 70, 5));
		mainFrame.add(panelProject);
		// ������� "������"
		panelProject.add(new Label("������"));
		// ���� ������ �������
		projectListBox = new JComboBox<String>();
		projectListFile = new File(projectListFilePath);
		// ��������� ������ ��������
		if (projectListFile.exists()) {
			loadProjectsFromFile(projectListBox, projectList, projectListFile);
		}
		panelProject.add(projectListBox);
		// ������� "������"
		panelProject.add(new Label("������"));
		// ���� ������ ������
		projectTaskListBox = new JComboBox<String>();
		taskListFile = new File(taskListFilePath);
		// ��������� ������ ���� �����
		if (taskListFile.exists()) {
			loadAllTasks(taskList, taskListFile);
		}
		// ������� � ������ ��������-�������� ������, ��������� �� �����
		Project project = new Project();
		for (int i = 0; i < projectList.size(); i++) {
			if ((projectList.get(i).getName().equals(projectListBox.getSelectedItem().toString()))) {
				project = TimeTrack.projectList.get(i);
			}
			;
		}
		;
		// ��������� ������ ����� �������
		if (taskListFile.exists()) {
			loadProjectTasksFromFile(projectTaskListBox, taskListFile, project);
		}
		panelProject.add(projectTaskListBox);
		// ��������� ���������� ������� �� �������
		projectListBox.addActionListener(new ChangeProject(projectListBox, projectTaskListBox));
		// ������� "������ ������"
		panelProject.add(new Label("������ ������"));
		// ������������� ������� ������, ����� ���� � ������ ���������� �����������
		Locale locale = new Locale("ru", "RU");
		JSpinner.setDefaultLocale(locale);
		// ���� ����� ������� ������ ������
		JSpinner dateStart = new JSpinner(new SpinnerDateModel());
		// ������������� ������ ����� ������: ���� � ������
		dateStart.setEditor(new JSpinner.DateEditor(dateStart, "HH:mm"));
		panelProject.add(dateStart);
		// ������� "��������� ������"
		panelProject.add(new Label("��������� ������"));
		// ���� ����� ������� ��������� ������
		JSpinner dateFinish = new JSpinner(new SpinnerDateModel());
		dateFinish.setEnabled(false);
		// ������������� ������ ����� ������: ���� � ������
		dateFinish.setEditor(new JSpinner.DateEditor(dateFinish, "HH:mm"));
		panelProject.add(dateFinish);
		// ������ "������ ������"
		Button startButton = new Button("������ ������");
		panelProject.add(startButton);
		// ������ "��������� ������"
		Button finishButton = new Button("��������� ������");
		finishButton.setEnabled(false);
		panelProject.add(finishButton);
		// ��������� ������ �� ���� ������ �������
		projectListBox.setFocusable(true);
		// ��������� ������� ���� �� ��� �����������
		mainFrame.pack();
		// ��������� ���� � ������ ������
		mainFrame.setLocationRelativeTo(null);
		// ���� ���������� �������
		mainFrame.setVisible(true);
		// ���������� ������������ ������� ������ "������ ������" � "��������� ������"
		startButton.addActionListener(
				new StartButtonListener(projectListBox, projectTaskListBox, dateStart, dateFinish, startButton, finishButton));
		finishButton.addActionListener(
				new FinishButtonListener(projectListBox, projectTaskListBox, dateStart, dateFinish, startButton, finishButton));
		// ���������� ������������ ������� ����
		mainFrame.addWindowListener(new MainWindowListener(mainFrame, projectListBox, projectTaskListBox, dateStart, dateFinish,
				startButton, finishButton));
	}

	public static String getSetting(String p_name) {
		String Settings = null;
		String res;
		try {
			// ���������� ����������� ����� ��������
			FileInputStream fis = new FileInputStream(SettingsFilePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			Settings = br.readLine();
			// �������� �������
			br.close();
			fis.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		Integer indFirst = Settings.indexOf(p_name + "=");
		if (indFirst >= 0) {
			Integer indLast = Settings.indexOf(";", indFirst);
			res = Settings.substring(indFirst + p_name.length() + 1, indLast);
		} else {
			System.out.println("��������� �� �������!");
			res = "-1";
		}
		return res;
	};

	public static void setSettings() {
		try {
			FileOutputStream fos = new FileOutputStream(SettingsFilePath);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			bw.append("JournalCounter=" + TimeTrack.JournalCounter + ";");
			bw.append("ResourceLang=" + TimeTrack.ResourceLang + ";");
			bw.flush();
			// �������� �������
			bw.close();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	};

	public static void loadProjectsFromList(JComboBox<String> projectListBox, ArrayList<Project> projectList) {
		// ���������� �������� �� ���������
		if (projectList.size() > 0) {
			projectListBox.removeAllItems();
			for (int i = 0; i < projectList.size(); i++) {
				projectListBox.addItem(projectList.get(i).getName());
			}
		}
	};

	public static void loadProjectsFromFile(JComboBox<String> projectListBox, ArrayList<Project> projectList,
			File projectListFile) {
		projectListBox.removeAllItems();
		try {
			// ������� projectList
			projectList.clear();
			// ���������� �������� �� �����
			FileInputStream fis = new FileInputStream(projectListFilePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			while (fis.available() > 0) {
				projectList.add((Project) ois.readObject());
				Collections.sort(projectList);
			}
			// �������� �������
			ois.close();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if (projectList.size() > 0) {
			for (int i = 0; i < projectList.size(); i++) {
				projectListBox.addItem(projectList.get(i).getName());
			}
			;
		}
		;
	};

	public static void loadProjectTasksFromFile(JComboBox<String> projectTaskListBox, File taskListFile, Project project) {
		ArrayList<Task> taskList = new ArrayList<Task>();
		ArrayList<Task> projectTaskList = new ArrayList<Task>();
		projectTaskList.clear();
		projectTaskListBox.removeAllItems();
		try {
			// ���������� ����� �� �����
			FileInputStream fis = new FileInputStream(taskListFilePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			while (fis.available() > 0) {
				taskList.add((Task) ois.readObject());
				Collections.sort(taskList);
			}
			// �������� �������
			ois.close();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// ��������� ����������-������ ����� �������
		for (int i = 0; i < taskList.size(); i++) {
			if (taskList.get(i).getProject().getName().equals(project.getName())) {
				projectTaskList.add(taskList.get(i));
				Collections.sort(projectTaskList);
			}
			;
		}
		// ��������� ���������-������ ����� �������
		if (projectTaskList.size() > 0) {
			for (int i = 0; i < projectTaskList.size(); i++) {
				projectTaskListBox.addItem(projectTaskList.get(i).getName());
			}
			;
		}
		;
	};

	public static void loadAllTasks(ArrayList<Task> taskList, File taskListFile) {
		taskList.clear();
		try {
			// ���������� ����� �� �����
			FileInputStream fis = new FileInputStream(taskListFilePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			while (fis.available() > 0) {
				taskList.add((Task) ois.readObject());
				Collections.sort(taskList);
			}
			// �������� �������
			ois.close();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	};

	public static void addProject(JComboBox<String> p_projectListBox) {
		Frame frame = new Frame("���������� �������");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(4, 2, 70, 5));
		// ��������� ������ ����
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		panel.add(new Label("�������� �������"));
		TextField name = new TextField();
		panel.add(name);
		panel.add(new Label("���� ������"));
		JSpinner dateStart = new JSpinner(new SpinnerDateModel());
		panel.add(dateStart);
		panel.add(new Label("���� ���������"));
		JSpinner dateFinish = new JSpinner(new SpinnerDateModel());
		panel.add(dateFinish);
		Button addProjectButton = new Button("�������� ������");
		panel.add(addProjectButton);
		addProjectButton.addActionListener(new AddProjectButtonListener(p_projectListBox, frame, name, dateStart, dateFinish));
		// ������ ������ ���������� �������
		Button cancelButton = new Button("������");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// ��������� ���� � ������ ������
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	}

	public static void editProject() {
		Frame frame = new Frame("�������������� �������");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(4, 2, 70, 5));
		// ��������� ������ ����
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		panel.add(new Label("�������� �������"));
		TextField projectName = new TextField();
		projectName.setText(projectListBox.getSelectedItem().toString());
		panel.add(projectName);
		// ������� � ������ ��������-�������� ������, ��������� �� �����
		Project project = new Project();
		for (int i = 0; i < projectList.size(); i++) {
			if ((projectList.get(i).getName().equals(projectName.getText()))) {
				project = projectList.get(i);
			}
			;
		}
		;
		// ���� ������ �������
		panel.add(new Label("���� ������"));
		dateStart = new JSpinner(new SpinnerDateModel(project.getDateStart(), null, null, Calendar.DATE));
		panel.add(dateStart);
		// ���� ��������� �������
		panel.add(new Label("���� ���������"));
		dateFinish = new JSpinner(new SpinnerDateModel(project.getDateFinish(), null, null, Calendar.DATE));
		panel.add(dateFinish);
		// ������ ���������� �������
		Button editTaskButton = new Button("����������");
		panel.add(editTaskButton);
		editTaskButton.addActionListener(new EditProjectButtonListener(frame, project, projectName, dateStart, dateFinish));
		// ������ ������ �������������� �������
		Button cancelButton = new Button("������");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// ��������� ���� � ������ ������
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	}

	public static void deleteProject() {
		Frame frame = new Frame("�������� �������");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(2, 2, 70, 5));
		// ��������� ������ ����
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		panel.add(new Label("�������� �������"));
		JComboBox<String> projectName = new JComboBox<String>();
		loadProjectsFromList(projectName, projectList);
		// ������� ���������� ������ � ������ ��������
		for (int i = 0; i < projectName.getItemCount(); i++) {
			if (projectName.getItemAt(i).toString().equals(projectListBox.getSelectedItem().toString())) {
				projectName.setSelectedIndex(i);
			}
			;
		}
		;
		panel.add(projectName);
		// ������ �������� �������
		Button deleteProjectButton = new Button("��������");
		panel.add(deleteProjectButton);
		deleteProjectButton.addActionListener(new DeleteProjectButtonListener(frame, projectName));
		// ������ ������ �������� �������
		Button cancelButton = new Button("������");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// ��������� ���� � ������ ������
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	}

	public static void addTask() {
		Frame frame = new Frame("���������� ������");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(5, 2, 70, 5));
		// ��������� ������ ����
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		panel.add(new Label("�������� �������"));
		JComboBox<String> projectName = new JComboBox<String>();
		loadProjectsFromList(projectName, projectList);
		// ������� ���������� ������ � ������ ��������
		for (int i = 0; i < projectName.getItemCount(); i++) {
			if (projectName.getItemAt(i).toString().equals(projectListBox.getSelectedItem().toString())) {
				projectName.setSelectedIndex(i);
			}
			;
		}
		;
		panel.add(projectName);
		panel.add(new Label("�������� ������"));
		TextField taskName = new TextField();
		panel.add(taskName);
		panel.add(new Label("���� ������"));
		JSpinner dateStart = new JSpinner(new SpinnerDateModel());
		panel.add(dateStart);
		panel.add(new Label("���� ���������"));
		JSpinner dateFinish = new JSpinner(new SpinnerDateModel());
		panel.add(dateFinish);
		Button addTaskButton = new Button("�������� ������");
		panel.add(addTaskButton);
		addTaskButton.addActionListener(new AddTaskButtonListener(projectName, frame, taskName, dateStart, dateFinish));
		// ������ ������ ���������� ������
		Button cancelButton = new Button("������");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// ��������� ���� � ������ ������
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	}

	public static void editTask() {
		Frame frame = new Frame("�������������� ������");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(5, 2, 70, 5));
		// ��������� ������ ����
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		panel.add(new Label("�������� �������"));
		JComboBox<String> project = new JComboBox<String>();
		loadProjectsFromList(project, projectList);
		// ������� ���������� ������ � ������ ��������
		for (int i = 0; i < project.getItemCount(); i++) {
			if (project.getItemAt(i).toString().equals(projectListBox.getSelectedItem().toString())) {
				project.setSelectedIndex(i);
			}
			;
		}
		;
		panel.add(project);
		panel.add(new Label("�������� ������"));
		TextField taskName = new TextField();
		taskName.setText(projectTaskListBox.getSelectedItem().toString());
		panel.add(taskName);
		// ������� � ������ �����-�������� ������, ��������� �� �����
		Task task = new Task();
		for (int i = 0; i < taskList.size(); i++) {
			if ((taskList.get(i).getName().equals(taskName.getText()))
					& (taskList.get(i).getProject().getName().equals(project.getSelectedItem()))) {
				task = taskList.get(i);
			}
			;
		}
		;
		// ���� ������
		panel.add(new Label("���� ������"));
		dateStart = new JSpinner(new SpinnerDateModel(task.getDateStart(), null, null, Calendar.DATE));
		panel.add(dateStart);
		// ���� ���������
		panel.add(new Label("���� ���������"));
		dateFinish = new JSpinner(new SpinnerDateModel(task.getDateFinish(), null, null, Calendar.DATE));
		panel.add(dateFinish);
		// ������ ���������� ������
		Button editTaskButton = new Button("����������");
		panel.add(editTaskButton);
		editTaskButton.addActionListener(new EditTaskButtonListener(frame, task, project, taskName, dateStart, dateFinish));
		// ������ ������ �������������� ������
		Button cancelButton = new Button("������");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// ��������� ���� � ������ ������
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	}

	public static void deleteTask() {
		Frame frame = new Frame("�������� ������");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(5, 2, 70, 5));
		// ��������� ������ ����
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		panel.add(new Label("�������� �������"));
		TextField projectName = new TextField();
		projectName.setText(projectListBox.getSelectedItem().toString());
		// ������� � ������ ��������-�������� ������, ��������� �� �����
		Project project = new Project();
		for (int i = 0; i < projectList.size(); i++) {
			if (projectList.get(i).getName().equals(projectName.getText())) {
				project = projectList.get(i);
			}
			;
		}
		;
		panel.add(projectName);
		panel.add(new Label("�������� ������"));
		TextField taskName = new TextField();
		taskName.setText(projectTaskListBox.getSelectedItem().toString());
		panel.add(taskName);
		// ������� � ������ �����-�������� ������, ��������� �� �����
		Task task = new Task();
		for (int i = 0; i < taskList.size(); i++) {
			if ((taskList.get(i).getName().equals(taskName.getText())) & (taskList.get(i).getProject().equals(project))) {
				task = taskList.get(i);
			}
			;
		}
		;
		// ���� ������
		panel.add(new Label("���� ������"));
		dateStart = new JSpinner(new SpinnerDateModel(task.getDateStart(), null, null, Calendar.DATE));
		panel.add(dateStart);
		// ���� ���������
		panel.add(new Label("���� ���������"));
		dateFinish = new JSpinner(new SpinnerDateModel(task.getDateFinish(), null, null, Calendar.DATE));
		panel.add(dateFinish);
		// ������ �������� ������
		Button deleteTaskButton = new Button("��������");
		panel.add(deleteTaskButton);
		deleteTaskButton.addActionListener(new DeleteTaskButtonListener(frame, task, project));
		// ������ ������ �������� ������
		Button cancelButton = new Button("������");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// ��������� ���� � ������ ������
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	}

	public static void loadJournal() {
		journalFile = new File(JournalFilePath);
		if (journalFile.exists()) {
			// ������� ������ ������� �������
			journalList.clear();
			try {
				// ���������� ������� �� �����
				FileInputStream fis = new FileInputStream(JournalFilePath);
				ObjectInputStream ois = new ObjectInputStream(fis);
				while (fis.available() > 0) {
					journalList.add((Journal) ois.readObject());
				}
				// �������� �������
				ois.close();
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	};

	public static void dayReport() {
		Frame frame = new Frame("����� �� ����");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(2, 2, 70, 5));
		// ��������� ������ ����
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		// ���� ������
		panel.add(new Label("����"));
		JSpinner reportDate = new JSpinner(new SpinnerDateModel());
		reportDate.setEditor(new JSpinner.DateEditor(reportDate, "dd.MM.yyyy"));
		panel.add(reportDate);
		// ������ ��������� ������
		Button getReportButton = new Button("�����");
		panel.add(getReportButton);
		getReportButton.addActionListener(new GetDayReportButtonListener(frame, reportDate));
		// ������ ������ ��������� ������
		Button cancelButton = new Button("������");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// ��������� ���� � ������ ������
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	};

	public static void periodReport() {
		Frame frame = new Frame("����� �� ������");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(3, 2, 70, 5));
		// ��������� ������ ����
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		// ���� ������ �������
		panel.add(new Label("���� ������"));
		JSpinner reportStDate = new JSpinner(new SpinnerDateModel());
		reportStDate.setEditor(new JSpinner.DateEditor(reportStDate, "dd.MM.yyyy"));
		panel.add(reportStDate);
		// ���� ��������� �������
		panel.add(new Label("���� ���������"));
		JSpinner reportFnDate = new JSpinner(new SpinnerDateModel());
		reportFnDate.setEditor(new JSpinner.DateEditor(reportFnDate, "dd.MM.yyyy"));
		panel.add(reportFnDate);
		// ������ ��������� ������
		Button getReportButton = new Button("�����");
		panel.add(getReportButton);
		getReportButton.addActionListener(new GetPeriodReportButtonListener(frame, reportStDate, reportFnDate));
		// ������ ������ ��������� ������
		Button cancelButton = new Button("������");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// ��������� ���� � ������ ������
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	};

	public static void projectReport() {
		Frame frame = new Frame("����� �� �������");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(2, 2, 80, 5));
		// ��������� ������ ����
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		// ������ ��� ������
		panel.add(new Label("������"));
		JComboBox<String> reportProject = new JComboBox<String>();
		loadProjectsFromList(reportProject, projectList);
		// ������� ���������� ������ � ������ ��������
		for (int i = 0; i < reportProject.getItemCount(); i++) {
			if (reportProject.getItemAt(i).toString().equals(projectListBox.getSelectedItem().toString())) {
				reportProject.setSelectedIndex(i);
			}
			;
		}
		;
		panel.add(reportProject);
		// ������ ��������� ������
		Button getReportButton = new Button("�����");
		panel.add(getReportButton);
		getReportButton.addActionListener(new GetProjectReportButtonListener(frame, reportProject));
		// ������ ������ ��������� ������
		Button cancelButton = new Button("������");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// ��������� ���� � ������ ������
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	};

	public static void taskReport() {
		Frame frame = new Frame("����� �� ������");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(3, 2, 70, 5));
		// ��������� ������ ����
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		// ������ ��� ������
		panel.add(new Label("������"));
		JComboBox<String> projectName = new JComboBox<String>();
		loadProjectsFromList(projectName, projectList);
		// ������� ���������� ������ � ������ ��������
		for (int i = 0; i < projectName.getItemCount(); i++) {
			if (projectName.getItemAt(i).toString().equals(projectListBox.getSelectedItem().toString())) {
				projectName.setSelectedIndex(i);
			}
			;
		}
		;
		// ������� � ������ ��������-�������� ������, ��������� �� �����
		Project project = new Project();
		for (int i = 0; i < projectList.size(); i++) {
			if ((projectList.get(i).getName().equals(projectName.getSelectedItem().toString()))) {
				project = projectList.get(i);
			}
			;
		}
		;
		panel.add(projectName);

		// ������ ��� ������
		panel.add(new Label("������"));
		// ���� ������ ������
		JComboBox<String> taskName = new JComboBox<String>();
		// ��������� ������ ����� �������
		if (taskListFile.exists()) {
			loadProjectTasksFromFile(taskName, taskListFile, project);
		}
		// ������� ���������� ������ � ������ �����
		for (int i = 0; i < taskName.getItemCount(); i++) {
			if (taskName.getItemAt(i).toString().equals(projectTaskListBox.getSelectedItem().toString())) {
				taskName.setSelectedIndex(i);
			}
			;
		}
		;
		panel.add(taskName);

		// ��������� ���������� ������� �� �������
		projectName.addActionListener(new ChangeProject(projectName, taskName));

		// ������ ��������� ������
		Button getReportButton = new Button("�����");
		panel.add(getReportButton);
		getReportButton.addActionListener(new GetTaskReportButtonListener(frame, projectName, taskName));
		// ������ ������ ��������� ������
		Button cancelButton = new Button("������");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// ��������� ���� � ������ ������
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});

	};

	public static void editJournal() {
		Frame frame = new Frame("�������������� �������");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(4, 2, 80, 5));
		// ��������� ������ ����
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));

		// ������
		panel.add(new Label("������"));
		JComboBox<String> projectName = new JComboBox<String>();
		// ��������� ������ ��������
		if (projectListFile.exists()) {
			loadProjectsFromFile(projectName, projectList, projectListFile);
		}
		// ������������� ������, ��������� �� ������� �����
		projectName.setSelectedItem(projectListBox.getSelectedItem());
		panel.add(projectName);

		// ������
		panel.add(new Label("������"));
		JComboBox<String> taskName = new JComboBox<String>();
		// ��������� ������ ���� �����
		if (taskListFile.exists()) {
			loadAllTasks(taskList, taskListFile);
		}
		// ������� � ������ ��������-�������� ������, ��������� �� �����
		Project project = new Project();
		for (int i = 0; i < projectList.size(); i++) {
			if ((projectList.get(i).getName().equals(projectName.getSelectedItem().toString()))) {
				project = projectList.get(i);
			}
			;
		}
		;
		// ��������� ������ ����� �������
		if (taskListFile.exists()) {
			loadProjectTasksFromFile(taskName, taskListFile, project);
		}
		// ������������� ������, ��������� �� ������� �����
		taskName.setSelectedItem(projectTaskListBox.getSelectedItem());
		panel.add(taskName);
		// ��������� ���������� ������� ��� ����� �������
		projectName.addActionListener(new ChangeProject(projectName, taskName));
		// ���� ���������� ������
		panel.add(new Label("����"));
		JSpinner workDate = new JSpinner(new SpinnerDateModel());
		workDate.setEditor(new JSpinner.DateEditor(workDate, "dd.MM.yyyy"));
		panel.add(workDate);
		// ������ ������ ������ ���������� ������� �������
		Button searchButton = new Button("�����");
		panel.add(searchButton);
		searchButton.addActionListener(new SearchButtonListener(frame, projectName, taskName, workDate));
		// ������ ������ ������
		Button cancelButton = new Button("������");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// ��������� ���� � ������ ������
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	};

	public static void changeLang(String p_lang) {
		TimeTrack.ResourceLang = p_lang;
		TimeTrack.setSettings();
		createMainFrame(mainFrame);
	};

	public static Calendar ResetTime(Calendar cal) {
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		return cal;
	}

	public static Calendar StrToCalend(String p_str) {
		String str = p_str;
		Integer dt = Integer.parseInt(str.substring(8, 10));
		Integer mn = Integer.parseInt(str.substring(5, 7));
		Integer yr = Integer.parseInt(str.substring(0, 4));
		Calendar res = Calendar.getInstance();
		res.set(yr, mn, dt);
		res.set(Calendar.DATE, dt);
		res.set(Calendar.MONTH, mn - 1);
		res.set(Calendar.YEAR, yr);
		res.set(Calendar.HOUR_OF_DAY, 0);
		res.set(Calendar.HOUR, 0);
		res.set(Calendar.MINUTE, 0);
		res.set(Calendar.SECOND, 0);
		res.set(Calendar.MILLISECOND, 0);
		return res;
	};

	public static Calendar StrToCalendTime(String p_str) {
		String str = p_str;
		Integer hr = Integer.parseInt(str.substring(0, 2));
		Integer mi = Integer.parseInt(str.substring(3, 5));
		Calendar res = Calendar.getInstance();

		// ��� �����-�� ����, �� ��� ������ ������ � ������ ���� ������-�� ��������������� �� ���� ������
		Integer dt = res.get(Calendar.DAY_OF_MONTH);
		res.set(Calendar.DAY_OF_MONTH, dt - 1);

		res.set(Calendar.HOUR_OF_DAY, hr);
		res.set(Calendar.HOUR, hr);
		res.set(Calendar.MINUTE, mi);
		res.set(Calendar.SECOND, 0);
		res.set(Calendar.MILLISECOND, 0);
		return res;
	};

	public static void callHelp() {
		Desktop desktop = null;
		if (Desktop.isDesktopSupported()) {
			desktop = Desktop.getDesktop();
		}
		try {
			desktop.open(new File(HelpFilePath));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void callInfo() {
		JFrame frame = new JFrame("� ���������");
		frame.setResizable(false);
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(6, 1, 5, 5));
		// ��������� ������ ����
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		Label lb1 = new Label("���� �������� �������\n");
		lb1.setFont(new Font(null, Font.BOLD, 14));
		lb1.setAlignment(Label.CENTER);
		Label lb2 = new Label("������ 1.0\n");
		lb2.setFont(new Font(null, Font.ITALIC, 12));
		lb2.setAlignment(Label.CENTER);
		Label lb3 = new Label("��������� ������������ �� �������� \"as is\". ������� �������� �� �����������������.");
		lb3.setFont(new Font(null, Font.PLAIN, 10));
		lb3.setAlignment(Label.CENTER);
		panel.add(lb1);
		panel.add(lb2);
		panel.add(lb3);
		Button OKButton = new Button("��");
		OKButton.addActionListener(new OKButtonListener(frame));
		frame.add(OKButton);

		GroupLayout layout = new GroupLayout(frame.getContentPane());
		frame.getContentPane().setLayout(layout);

		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lb1)
						.addComponent(lb2)
						.addComponent(lb3))
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addComponent(OKButton))));

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createSequentialGroup()
						.addComponent(lb1)
						.addComponent(lb2)
						.addComponent(lb3)
						.addComponent(OKButton)));

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	}
}

class AddProjectButtonListener implements ActionListener {
	private JComboBox<String> projectListBox;
	private Frame frame;
	private TextField project_name;
	private JSpinner date_start;
	private JSpinner date_finish;

	public AddProjectButtonListener(JComboBox<String> p_projectListBox, Frame p_frame, TextField p_project_name,
			JSpinner p_date_start, JSpinner p_date_finish) {
		projectListBox = p_projectListBox;
		frame = p_frame;
		project_name = p_project_name;
		date_start = p_date_start;
		date_finish = p_date_finish;

	}

	public void actionPerformed(ActionEvent e) {
		// ���������� ������������ �������
		Date time_start = (Date) date_start.getValue();
		Date time_finish = (Date) date_finish.getValue();
		BigDecimal length_ms = new BigDecimal((time_finish.getTime() - time_start.getTime()));
		BigDecimal length_hr = length_ms.divide(new BigDecimal("3600000"), 2, BigDecimal.ROUND_DOWN);
		BigDecimal length_work_hr = length_hr.divide(new BigDecimal("3"), 2, BigDecimal.ROUND_DOWN);
		BigDecimal length_dt = length_hr.divide(new BigDecimal("8"), 2, BigDecimal.ROUND_DOWN);
		BigDecimal length_work_dt = length_dt.divide(new BigDecimal("3"), 2, BigDecimal.ROUND_DOWN);

		Project newProject = new Project();
		newProject.setName(project_name.getText());
		newProject.setDateStart((Date) date_start.getValue());
		newProject.setDateFinish((Date) date_finish.getValue());
		newProject.setLengthHr(length_work_hr);
		newProject.setLengthDt(length_work_dt);
		TimeTrack.projectList.add(newProject);
		Collections.sort(TimeTrack.projectList);
		TimeTrack.loadProjectsFromList(projectListBox, TimeTrack.projectList);
		try {
			// ������ ��������� �������� � ����
			if (TimeTrack.projectListFile.exists()) {
				TimeTrack.projectListFile.delete();
			}
			;
			FileOutputStream fos = new FileOutputStream(TimeTrack.projectListFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.projectList.size(); i++) {
				oos.writeObject(TimeTrack.projectList.get(i));
			}
			;
			// �������� �������
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		frame.dispose();
	}
}

class EditProjectButtonListener implements ActionListener {
	private Frame frame;
	private Project project;
	private TextField project_name;
	private JSpinner date_start;
	private JSpinner date_finish;

	public EditProjectButtonListener(Frame p_frame, Project p_project, TextField p_project_name, JSpinner p_date_start,
			JSpinner p_date_finish) {
		frame = p_frame;
		project = p_project;
		project_name = p_project_name;
		date_start = p_date_start;
		date_finish = p_date_finish;

	}

	public void actionPerformed(ActionEvent e) {
		// �������� �� ��������� ������ ������ ����������� �������
		ArrayList<Task> projectTaskList = new ArrayList<Task>();
		for (int i = 0; i < TimeTrack.taskList.size(); i++) {
			if (TimeTrack.taskList.get(i).getProject().getName().equals(project.getName())) {
				projectTaskList.add(TimeTrack.taskList.get(i));
				Collections.sort(projectTaskList);
			}
			;
		}
		;

		// �������� �� ��������� ������ ������ � ������� ��� ����������� �������
		ArrayList<Journal> projectJournalList = new ArrayList<Journal>();
		TimeTrack.loadJournal();
		for (int i = 0; i < TimeTrack.journalList.size(); i++) {
			if (TimeTrack.journalList.get(i).getProject().getName().equals(project.getName())) {
				projectJournalList.add(TimeTrack.journalList.get(i));
			}
			;
		}
		;

		// ���������� ������������ �������
		Date time_start = (Date) date_start.getValue();
		Date time_finish = (Date) date_finish.getValue();
		BigDecimal length_ms = new BigDecimal((time_finish.getTime() - time_start.getTime()));
		BigDecimal length_hr = length_ms.divide(new BigDecimal("3600000"), 2, BigDecimal.ROUND_DOWN);
		BigDecimal length_work_hr = length_hr.divide(new BigDecimal("3"), 2, BigDecimal.ROUND_DOWN);
		BigDecimal length_dt = length_hr.divide(new BigDecimal("8"), 2, BigDecimal.ROUND_DOWN);
		BigDecimal length_work_dt = length_dt.divide(new BigDecimal("3"), 2, BigDecimal.ROUND_DOWN);

		project.setName(project_name.getText());
		project.setDateStart((Date) date_start.getValue());
		project.setDateFinish((Date) date_finish.getValue());
		project.setLengthHr(length_work_hr);
		project.setLengthDt(length_work_dt);
		// ��������� ���� ������� � ������ ������� ����� �������
		for (int i = 0; i < projectTaskList.size(); i++) {
			projectTaskList.get(i).setProject(project);
		}
		;
		// ��������� ���� ������� � ������ ������� � ������� ��� ����������� �������
		for (int i = 0; i < projectJournalList.size(); i++) {
			projectJournalList.get(i).setProject(project);
		}
		;
		try {
			// ������ ��������� �������� � ����
			if (TimeTrack.projectListFile.exists()) {
				TimeTrack.projectListFile.delete();
			}
			;
			FileOutputStream fos = new FileOutputStream(TimeTrack.projectListFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.projectList.size(); i++) {
				oos.writeObject(TimeTrack.projectList.get(i));
			}
			;
			// �������� �������
			oos.close();
			// ������ ��������� ����� � ����
			if (TimeTrack.taskListFile.exists()) {
				TimeTrack.taskListFile.delete();
			}
			;
			fos = new FileOutputStream(TimeTrack.taskListFilePath);
			oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.taskList.size(); i++) {
				oos.writeObject(TimeTrack.taskList.get(i));
			}
			;
			// �������� �������
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try {
			// ������ ������� ������� � ����
			FileOutputStream fos = new FileOutputStream(TimeTrack.JournalFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.journalList.size(); i++) {
				oos.writeObject(TimeTrack.journalList.get(i));
			}
			;
			// �������� �������
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		frame.dispose();
		TimeTrack.loadProjectsFromList(TimeTrack.projectListBox, TimeTrack.projectList);
	}
}

class DeleteProjectButtonListener implements ActionListener {
	private Frame frame;
	private JComboBox<String> project_name;

	public DeleteProjectButtonListener(Frame p_frame, JComboBox<String> p_project_name) {
		frame = p_frame;
		project_name = p_project_name;
	}

	private void DelProject(Project project) {
		// �������� �������
		for (int i = 0; i < TimeTrack.projectList.size(); i++) {
			if (TimeTrack.projectList.get(i).equals(project)) {
				TimeTrack.projectList.remove(i);
			}
			;
		}
		;
		try {
			// ������ ��������� �������� � ����
			if (TimeTrack.projectListFile.exists()) {
				TimeTrack.projectListFile.delete();
			}
			;
			FileOutputStream fos = new FileOutputStream(TimeTrack.projectListFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.projectList.size(); i++) {
				oos.writeObject(TimeTrack.projectList.get(i));
			}
			;
			// �������� �������
			oos.close();
			// ������ ��������� ����� � ����
			if (TimeTrack.taskListFile.exists()) {
				TimeTrack.taskListFile.delete();
			}
			;
			fos = new FileOutputStream(TimeTrack.taskListFilePath);
			oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.taskList.size(); i++) {
				oos.writeObject(TimeTrack.taskList.get(i));
			}
			;
			// �������� �������
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		frame.dispose();
		TimeTrack.loadProjectsFromList(TimeTrack.projectListBox, TimeTrack.projectList);
	}

	public void actionPerformed(ActionEvent e) {
		// ������� � ������ ��������-�������� ������, ��������� �� �����
		Project project = new Project();
		for (int i = 0; i < TimeTrack.projectList.size(); i++) {
			if ((TimeTrack.projectList.get(i).getName().equals(project_name.getSelectedItem().toString()))) {
				project = TimeTrack.projectList.get(i);
			}
			;
		}
		;
		// �������� �� ��������� ������ ������ ���������� �������
		ArrayList<Task> projectTaskList = new ArrayList<Task>();
		for (int i = 0; i < TimeTrack.taskList.size(); i++) {
			if (TimeTrack.taskList.get(i).getProject().getName().equals(project.getName())) {
				projectTaskList.add(TimeTrack.taskList.get(i));
				Collections.sort(projectTaskList);
			}
			;
		}
		;
		if (!projectTaskList.isEmpty()) {
			int reply = JOptionPane.showConfirmDialog(null, "� ������� ���� ������. ������� ������?", "������� ������",
					JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION) {
				// �������� ����� �������
				for (int i = 0; i < projectTaskList.size(); i++) {
					for (int j = 0; j < TimeTrack.taskList.size(); j++) {
						if (projectTaskList.get(i).equals(TimeTrack.taskList.get(j))) {
							TimeTrack.taskList.remove(j);
						}
						;
					}
					;
				}
				;
				// �������� �������
				DelProject(project);
			} else {
				// �������� ���� �������� �������
				frame.dispose();
			}
		} else {
			// �������� �������
			DelProject(project);
		}
		;
	}
}

class AddTaskButtonListener implements ActionListener {
	private String project_name;
	private Project project;
	private Frame frame;
	private TextField task_name;
	private JSpinner date_start;
	private JSpinner date_finish;

	public AddTaskButtonListener(final JComboBox<String> p_project_list, Frame p_frame, TextField p_task_name,
			JSpinner p_date_start, JSpinner p_date_finish) {
		// ��������� ���������� ������� �� ������� (���� �������� �� ������ � ������ ������)
		p_project_list.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ���������� ����� ���������� �������
				project_name = (String) p_project_list.getSelectedItem();
			}
		});
		// ���������� ����� ���������� �������
		project_name = (String) p_project_list.getSelectedItem();
		frame = p_frame;
		task_name = p_task_name;
		date_start = p_date_start;
		date_finish = p_date_finish;

	}

	public void actionPerformed(ActionEvent e) {
		// ����� �������, � �������� ��������� ������
		for (int i = 0; i < TimeTrack.projectList.size(); i++) {
			if (TimeTrack.projectList.get(i).getName() == project_name) {
				project = TimeTrack.projectList.get(i);
			}
			;
		}
		;
		// ���������� ������������ ������
		Date time_start = (Date) date_start.getValue();
		Date time_finish = (Date) date_finish.getValue();
		BigDecimal length_ms = new BigDecimal((time_finish.getTime() - time_start.getTime()));
		BigDecimal length_hr = length_ms.divide(new BigDecimal("3600000"), 2, BigDecimal.ROUND_DOWN);
		BigDecimal length_work_hr = length_hr.divide(new BigDecimal("3"), 2, BigDecimal.ROUND_DOWN);
		BigDecimal length_dt = length_hr.divide(new BigDecimal("8"), 2, BigDecimal.ROUND_DOWN);
		BigDecimal length_work_dt = length_dt.divide(new BigDecimal("3"), 2, BigDecimal.ROUND_DOWN);

		Task newTask = new Task();
		newTask.setName(task_name.getText());
		newTask.setDateStart((Date) date_start.getValue());
		newTask.setDateFinish((Date) date_finish.getValue());
		newTask.setProject(project);
		newTask.setLengthHr(length_work_hr);
		newTask.setLengthDt(length_work_dt);
		TimeTrack.taskList.add(newTask);
		Collections.sort(TimeTrack.taskList);
		try {
			// ������ ��������� ����� � ����
			if (TimeTrack.taskListFile.exists()) {
				TimeTrack.taskListFile.delete();
			}
			;
			FileOutputStream fos = new FileOutputStream(TimeTrack.taskListFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.taskList.size(); i++) {
				oos.writeObject(TimeTrack.taskList.get(i));
			}
			;
			// �������� �������
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		frame.dispose();

		// ����� �������, ���������� �� �������� �����
		for (int i = 0; i < TimeTrack.projectList.size(); i++) {
			if (TimeTrack.projectList.get(i).getName() == TimeTrack.projectListBox.getSelectedItem().toString()) {
				project = TimeTrack.projectList.get(i);
			}
			;
		}
		;
		TimeTrack.loadProjectTasksFromFile(TimeTrack.projectTaskListBox, TimeTrack.taskListFile, project);
	}
}

class EditTaskButtonListener implements ActionListener {
	private JComboBox<String> project_list;
	private Task task;
	private Frame frame;
	private TextField task_name;
	private JSpinner date_start;
	private JSpinner date_finish;

	public EditTaskButtonListener(Frame p_frame, Task p_task, JComboBox<String> p_project_list, TextField p_task_name,
			JSpinner p_date_start, JSpinner p_date_finish) {
		frame = p_frame;
		task = p_task;
		task_name = p_task_name;
		date_start = p_date_start;
		date_finish = p_date_finish;
		project_list = p_project_list;

	}

	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < TimeTrack.projectList.size(); i++) {
			if (TimeTrack.projectList.get(i).getName().equals(project_list.getSelectedItem().toString())) {
				task.setProject(TimeTrack.projectList.get(i));
			}
			;
		}
		;

		// �������� �� ��������� ������ ������ � ������� ��� ���������� ������
		ArrayList<Journal> projectJournalList = new ArrayList<Journal>();
		TimeTrack.loadJournal();
		for (int i = 0; i < TimeTrack.journalList.size(); i++) {
			if (TimeTrack.journalList.get(i).getTask().getName().equals(task.getName())) {
				projectJournalList.add(TimeTrack.journalList.get(i));
			}
			;
		}
		;

		// ���������� ������������ ������
		Date time_start = (Date) date_start.getValue();
		Date time_finish = (Date) date_finish.getValue();
		BigDecimal length_ms = new BigDecimal((time_finish.getTime() - time_start.getTime()));
		BigDecimal length_hr = length_ms.divide(new BigDecimal("3600000"), 2, BigDecimal.ROUND_DOWN);
		BigDecimal length_work_hr = length_hr.divide(new BigDecimal("3"), 2, BigDecimal.ROUND_DOWN);
		BigDecimal length_dt = length_hr.divide(new BigDecimal("8"), 2, BigDecimal.ROUND_DOWN);
		BigDecimal length_work_dt = length_dt.divide(new BigDecimal("3"), 2, BigDecimal.ROUND_DOWN);

		task.setName(task_name.getText());
		task.setDateStart((Date) date_start.getValue());
		task.setDateFinish((Date) date_finish.getValue());
		task.setLengthHr(length_work_hr);
		task.setLengthDt(length_work_dt);
		// ��������� ���� ������ � ������ ������� � ������� ��� ����������� ������
		for (int i = 0; i < projectJournalList.size(); i++) {
			projectJournalList.get(i).setTask(task);
		}
		;
		try {
			// ������ ��������� ����� � ����
			if (TimeTrack.taskListFile.exists()) {
				TimeTrack.taskListFile.delete();
			}
			;
			FileOutputStream fos = new FileOutputStream(TimeTrack.taskListFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.taskList.size(); i++) {
				oos.writeObject(TimeTrack.taskList.get(i));
			}
			;
			// �������� �������
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try {
			// ������ ������� ������� � ����
			FileOutputStream fos = new FileOutputStream(TimeTrack.JournalFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.journalList.size(); i++) {
				oos.writeObject(TimeTrack.journalList.get(i));
			}
			;
			// �������� �������
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		frame.dispose();
		TimeTrack.loadProjectsFromList(TimeTrack.projectListBox, TimeTrack.projectList);
		for (int i = 0; i < TimeTrack.projectListBox.getItemCount(); i++) {
			if (TimeTrack.projectListBox.getItemAt(i) == task.getProject().getName()) {
				TimeTrack.projectListBox.setSelectedIndex(i);
			}
		}
		for (int i = 0; i < TimeTrack.projectTaskListBox.getItemCount(); i++) {
			if (TimeTrack.projectTaskListBox.getItemAt(i) == task.getName()) {
				TimeTrack.projectTaskListBox.setSelectedIndex(i);
			}
		}
	}
}

class DeleteTaskButtonListener implements ActionListener {
	private Frame frame;
	private Task task;
	private Project project;

	public DeleteTaskButtonListener(Frame p_frame, Task p_task, Project p_project) {
		frame = p_frame;
		project = p_project;
		task = p_task;

	}

	public void actionPerformed(ActionEvent e) {
		// �������� ������
		for (int i = 0; i < TimeTrack.taskList.size(); i++) {
			if (TimeTrack.taskList.get(i).equals(task)) {
				TimeTrack.taskList.remove(i);
			}
			;
		}
		;
		try {
			// ������ ��������� ����� � ����
			if (TimeTrack.taskListFile.exists()) {
				TimeTrack.taskListFile.delete();
			}
			;
			FileOutputStream fos = new FileOutputStream(TimeTrack.taskListFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.taskList.size(); i++) {
				oos.writeObject(TimeTrack.taskList.get(i));
			}
			;
			// �������� �������
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		frame.dispose();
		TimeTrack.loadProjectTasksFromFile(TimeTrack.projectTaskListBox, TimeTrack.taskListFile, project);
	}
}

class CancelButtonListener implements ActionListener {
	private Frame frame;

	public CancelButtonListener(Frame p_frame) {
		frame = p_frame;
	}

	public void actionPerformed(ActionEvent e) {
		frame.dispose();
	}
}

class StartButtonListener implements ActionListener {
	private JComboBox<String> projectListBox;
	private JComboBox<String> taskListBox;
	private Button startButton;
	private Button finishButton;
	private JSpinner date_start;
	private JSpinner date_finish;

	public StartButtonListener(JComboBox<String> p_projectListBox, JComboBox<String> p_taskListBox, JSpinner p_date_start,
			JSpinner p_date_finish, Button p_startButton, Button p_finishButton) {
		projectListBox = p_projectListBox;
		taskListBox = p_taskListBox;
		startButton = p_startButton;
		finishButton = p_finishButton;
		date_start = p_date_start;
		date_finish = p_date_finish;

	}

	public void actionPerformed(ActionEvent e) {
		projectListBox.setEnabled(false);
		taskListBox.setEnabled(false);
		startButton.setEnabled(false);
		finishButton.setEnabled(true);
		date_start.setEnabled(false);
		date_finish.setEnabled(true);
	}
}

class FinishButtonListener implements ActionListener {
	private JComboBox<String> projectListBox;
	private Project project;
	private JComboBox<String> taskListBox;
	private Task task;
	private Button startButton;
	private Button finishButton;
	private JSpinner date_start;
	private JSpinner date_finish;
	private BigDecimal length_hr;
	private BigDecimal length_dt;

	public FinishButtonListener(final JComboBox<String> p_projectListBox, final JComboBox<String> p_taskListBox,
			JSpinner p_date_start, JSpinner p_date_finish, Button p_startButton, Button p_finishButton) {
		projectListBox = p_projectListBox;
		taskListBox = p_taskListBox;
		startButton = p_startButton;
		finishButton = p_finishButton;
		date_start = p_date_start;
		date_finish = p_date_finish;

	}

	public void actionPerformed(ActionEvent e) {
		projectListBox.setEnabled(true);
		taskListBox.setEnabled(true);
		startButton.setEnabled(true);
		finishButton.setEnabled(false);
		date_start.setEnabled(true);
		date_finish.setEnabled(false);

		// ���������, ���������� �� ����� ������ ������ �� ������� ���������.
		// ���� ����������, ������ ������������ ������� ��� ������� - �� ������
		// ���� �� ����������, �� ������ �� ������� �����.
		Date d1 = (Date) date_start.getValue();
		Calendar cal1 = Calendar.getInstance();
		cal1.setTimeInMillis(d1.getTime());
		cal1.set(Calendar.SECOND, 0);
		cal1.set(Calendar.MILLISECOND, 0);
		Date d2 = (Date) date_finish.getValue();
		Calendar cal2 = Calendar.getInstance();
		cal2.setTimeInMillis(d2.getTime());
		cal2.set(Calendar.SECOND, 0);
		cal2.set(Calendar.MILLISECOND, 0);
		if (cal1.compareTo(cal2) == 0) {
			date_finish.setValue(new Date());
		}

		// ����� ������� � ������ ��������
		for (int i = 0; i < TimeTrack.projectList.size(); i++) {
			if (TimeTrack.projectList.get(i).getName().equals(projectListBox.getSelectedItem())) {
				project = TimeTrack.projectList.get(i);
			}
			;
		}
		;
		// ����� ������ � ������ �����
		for (int i = 0; i < TimeTrack.taskList.size(); i++) {
			if ((TimeTrack.taskList.get(i).getName().equals(taskListBox.getSelectedItem()))
					& (TimeTrack.taskList.get(i).getProject().getName().equals(project.getName()))) {
				task = TimeTrack.taskList.get(i);
			}
			;
		}
		;

		// �������� ������� ������� ��� ��������� ������
		Journal newJournal = new Journal();
		newJournal.setId(TimeTrack.JournalCounter);
		// ��������� ���������� ��������
		TimeTrack.JournalCounter = TimeTrack.JournalCounter + 1;
		TimeTrack.setSettings();
		newJournal.setProject(project);
		newJournal.setTask(task);

		// ���������� ���� ������
		Date dt = (Date) date_start.getValue();
		Calendar cal_st_1 = Calendar.getInstance();
		TimeTrack.ResetTime(cal_st_1);
		Calendar cal_st_2 = Calendar.getInstance();
		cal_st_2.setTimeInMillis(dt.getTime());
		cal_st_1.set(Calendar.HOUR, cal_st_2.get(Calendar.HOUR));
		cal_st_1.set(Calendar.MINUTE, cal_st_2.get(Calendar.MINUTE));
		cal_st_1.set(Calendar.HOUR_OF_DAY, cal_st_2.get(Calendar.HOUR_OF_DAY));
		newJournal.setDateStart(cal_st_1);

		// ���������� ���� ���������
		dt = (Date) date_finish.getValue();
		Calendar cal_fn_1 = Calendar.getInstance();
		TimeTrack.ResetTime(cal_fn_1);
		Calendar cal_fn_2 = Calendar.getInstance();
		cal_fn_2.setTimeInMillis(dt.getTime());
		cal_fn_1.set(Calendar.HOUR, cal_fn_2.get(Calendar.HOUR));
		cal_fn_1.set(Calendar.MINUTE, cal_fn_2.get(Calendar.MINUTE));
		cal_fn_1.set(Calendar.HOUR_OF_DAY, cal_fn_2.get(Calendar.HOUR_OF_DAY));
		newJournal.setDateFinish(cal_fn_1);

		// ���������� ������������ ���������� ������
		BigDecimal length_ms = new BigDecimal(
				newJournal.getDateFinish().getTimeInMillis() - newJournal.getDateStart().getTimeInMillis());
		length_hr = length_ms.divide(new BigDecimal("3600000"), 2, BigDecimal.ROUND_DOWN);
		length_dt = length_hr.divide(new BigDecimal("8"), 2, BigDecimal.ROUND_DOWN);

		newJournal.setLengthHr(length_hr);
		newJournal.setLengthDt(length_dt);
		TimeTrack.loadJournal();
		TimeTrack.journalList.add(newJournal);
		try {
			// ������ ������� ������� � ����
			FileOutputStream fos = new FileOutputStream(TimeTrack.JournalFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.journalList.size(); i++) {
				oos.writeObject(TimeTrack.journalList.get(i));
			}
			;
			// �������� �������
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		/*
		 * // �������: ���������, ��� ����������� � ���� �������
		 * try{
		 * // ���������� ����� �� �����
		 * FileInputStream fis = new FileInputStream(TimeTrack.JournalFilePath);
		 * ObjectInputStream ois = new ObjectInputStream(fis);
		 * while(fis.available()>0){
		 * Journal tst = (Journal) ois.readObject();
		 * System.out.println("Project:" + tst.getProject().getName());
		 * System.out.println("Task: " + tst.getTask().getName());
		 * System.out.println(tst.getDateStart());
		 * System.out.println(tst.getDateFinish());
		 * System.out.println(tst.getLengthHr());
		 * System.out.println(tst.getLengthDt());
		 * }
		 * // �������� �������
		 * ois.close();
		 * } catch(ClassNotFoundException ex) {
		 * ex.printStackTrace();
		 * }
		 * catch(IOException ex) {
		 * ex.printStackTrace();
		 * }
		 */

		// ������������� ����� ������ ������ � ����� ��������� ������ �� �������
		date_start.setValue(new Date());
		date_finish.setValue(new Date());
	}
}

class GetDayReportButtonListener implements ActionListener {
	private Frame frame;
	private JSpinner report_date;

	private class ReportItem {
		private String task;
		private String project;
		private BigDecimal length_dt;
		private BigDecimal length_hr;

		public ReportItem(String task, String project, BigDecimal length_dt, BigDecimal length_hr) {
			super();
			this.task = task;
			this.project = project;
			this.length_dt = length_dt;
			this.length_hr = length_hr;
		}
	}

	public GetDayReportButtonListener(Frame p_frame, JSpinner p_report_date) {
		report_date = p_report_date;
		frame = p_frame;
	}

	public void actionPerformed(ActionEvent e) {
		// ��������� ������
		TimeTrack.loadJournal();
		// ���� ������ �� ������ ����
		ArrayList<Journal> dayWork = new ArrayList<Journal>();
		Calendar report_calendar = Calendar.getInstance();
		report_calendar.setTime((Date) report_date.getValue());
		TimeTrack.ResetTime(report_calendar);
		Calendar st_calendar = Calendar.getInstance();
		Calendar fn_calendar = Calendar.getInstance();
		for (int i = 0; i < TimeTrack.journalList.size(); i++) {
			st_calendar = TimeTrack.journalList.get(i).getDateStart();
			TimeTrack.ResetTime(st_calendar);
			fn_calendar = TimeTrack.journalList.get(i).getDateStart();
			TimeTrack.ResetTime(fn_calendar);
			if ((st_calendar.get(Calendar.DATE) == (report_calendar.get(Calendar.DATE))) &
					(st_calendar.get(Calendar.MONTH) == (report_calendar.get(Calendar.MONTH))) &
					(st_calendar.get(Calendar.YEAR) == (report_calendar.get(Calendar.YEAR))) &
					(fn_calendar.get(Calendar.DATE) == (report_calendar.get(Calendar.DATE))) &
					(fn_calendar.get(Calendar.MONTH) == (report_calendar.get(Calendar.MONTH))) &
					(fn_calendar.get(Calendar.YEAR) == (report_calendar.get(Calendar.YEAR)))) {
				dayWork.add(TimeTrack.journalList.get(i));
			}
			;
		}
		;
		// ������� ������ ����� � �������� ���
		ArrayList<ReportItem> dayTasks = new ArrayList<ReportItem>();
		ArrayList<String> dayProjects = new ArrayList<String>();
		for (int i = 0; i < dayWork.size(); i++) {
			if (dayTasks.size() == 0) {
				dayTasks.add(new ReportItem(dayWork.get(i).getTask().getName(), dayWork.get(i).getProject().getName(),
						dayWork.get(i).getLengthDt(), dayWork.get(i).getLengthHr()));
				dayProjects.add(dayWork.get(i).getProject().getName());
			} else {
				// ���������, ���� �� ��� � ������ ����������� ����� ������ �� �������
				// ���� ����, �� ���������� ����
				Boolean hasTask = false;
				for (int j = 0; j < dayTasks.size(); j++) {
					if ((dayWork.get(i).getTask().getName().equals(dayTasks.get(j).task))
							& (dayWork.get(i).getProject().getName().equals(dayTasks.get(j).project))) {
						dayTasks.get(j).length_dt = dayTasks.get(j).length_dt.add(dayWork.get(i).getLengthDt());
						dayTasks.get(j).length_hr = dayTasks.get(j).length_hr.add(dayWork.get(i).getLengthHr());
						hasTask = true;
					}
				}
				// ���� ���, �� ��������� ������ � ������
				if (!hasTask) {
					dayTasks.add(new ReportItem(dayWork.get(i).getTask().getName(), dayWork.get(i).getProject().getName(),
							dayWork.get(i).getLengthDt(), dayWork.get(i).getLengthHr()));
				}
				// ���������, ���� �� ��� � ������ ����������� �������� ������ ������� ������
				Boolean hasProject = false;
				for (int k = 0; k < dayProjects.size(); k++) {
					if (dayWork.get(i).getProject().getName().equals(dayProjects.get(k))) {
						hasProject = true;
					}
				}
				// ���� ���, �� ��������� ���
				if (!hasProject) {
					dayProjects.add(dayWork.get(i).getProject().getName());
				}
			}
		}
		// ���� ��� ������ ������ ������
		String report_date = new SimpleDateFormat("dd.MM.yyyy").format(report_calendar.getTime());
		Frame frame1 = new Frame("����� �� " + report_date);
		frame1.setResizable(false);
		frame1.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame1.add(panel);
		panel.setLayout(new GridLayout(1, 1, 100, 100));
		// ��������� ������ ����
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		// ��������� ������� ������ �������� ������
		TextArea reportText = new TextArea(30, 100);
		reportText.setBackground(Color.white);
		reportText.setForeground(Color.black);
		for (int i = 0; i < TimeTrack.projectList.size(); i++) {
			for (int j = 0; j < dayProjects.size(); j++) {
				if (TimeTrack.projectList.get(i).getName().equals(dayProjects.get(j))) {
					reportText.append("������ \"" + dayProjects.get(j) + "\"\n");
					for (int k = 0; k < dayTasks.size(); k++) {
						if (dayTasks.get(k).project.equals(dayProjects.get(j))) {
							reportText.append(
									"- ������ \"" + dayTasks.get(k).task + "\" - " + dayTasks.get(k).length_hr + " �.\n");
						}
					}
					reportText.append("\n");
				}
			}
		}
		reportText.setEditable(false);
		panel.add(reportText);
		frame1.pack();
		// ��������� ���� � ������ ������
		frame1.setLocationRelativeTo(null);
		frame1.setVisible(true);
		// ��������� ���� ������ ������
		frame.dispose();

		frame1.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	}
}

class GetPeriodReportButtonListener implements ActionListener {
	private Frame frame;
	private JSpinner report_st_date;
	private JSpinner report_fn_date;

	private class ReportItem {
		private String task;
		private String project;
		private BigDecimal length_dt;
		private BigDecimal length_hr;

		public ReportItem(String task, String project, BigDecimal length_dt, BigDecimal length_hr) {
			super();
			this.task = task;
			this.project = project;
			this.length_dt = length_dt;
			this.length_hr = length_hr;
		}
	}

	public GetPeriodReportButtonListener(Frame p_frame, JSpinner p_report_st_date, JSpinner p_report_fn_date) {
		report_st_date = p_report_st_date;
		report_fn_date = p_report_fn_date;
		frame = p_frame;
	}

	public void actionPerformed(ActionEvent e) {
		// ��������� ������
		TimeTrack.loadJournal();
		// ���� ������ �� ������ ����
		ArrayList<Journal> dayWork = new ArrayList<Journal>();
		Calendar report_st_calendar = Calendar.getInstance();
		report_st_calendar.setTime((Date) report_st_date.getValue());
		// �������� �����
		TimeTrack.ResetTime(report_st_calendar);
		Calendar report_fn_calendar = Calendar.getInstance();
		report_fn_calendar.setTime((Date) report_fn_date.getValue());
		// �������� �����
		TimeTrack.ResetTime(report_fn_calendar);
		if (report_fn_calendar.before(report_st_calendar)) {
			JOptionPane.showMessageDialog(null, "���� ������ ������� ������ ���� ������ ���� ��������� �������!");
		} else {
			for (int i = 0; i < TimeTrack.journalList.size(); i++) {
				Calendar st_calendar = Calendar.getInstance();
				st_calendar = TimeTrack.journalList.get(i).getDateStart();
				TimeTrack.ResetTime(st_calendar);
				Calendar fn_calendar = Calendar.getInstance();
				fn_calendar = TimeTrack.journalList.get(i).getDateFinish();
				TimeTrack.ResetTime(fn_calendar);
				if ((st_calendar.compareTo(report_st_calendar) >= 0)
						& (fn_calendar.compareTo(report_fn_calendar) <= 0)) {
					dayWork.add(TimeTrack.journalList.get(i));
				}
				;
			}
			;
			// ������� ������ ����� � �������� ���
			ArrayList<ReportItem> dayTasks = new ArrayList<ReportItem>();
			ArrayList<String> dayProjects = new ArrayList<String>();
			for (int i = 0; i < dayWork.size(); i++) {
				if (dayTasks.size() == 0) {
					dayTasks.add(new ReportItem(dayWork.get(i).getTask().getName(), dayWork.get(i).getProject().getName(),
							dayWork.get(i).getLengthDt(), dayWork.get(i).getLengthHr()));
					dayProjects.add(dayWork.get(i).getProject().getName());
				} else {
					// ���������, ���� �� ��� � ������ ����������� ����� ������ �� �������
					// ���� ����, �� ���������� ����
					Boolean hasTask = false;
					for (int j = 0; j < dayTasks.size(); j++) {
						if ((dayWork.get(i).getTask().getName().equals(dayTasks.get(j).task))
								& (dayWork.get(i).getProject().getName().equals(dayTasks.get(j).project))) {
							dayTasks.get(j).length_dt = dayTasks.get(j).length_dt.add(dayWork.get(i).getLengthDt());
							dayTasks.get(j).length_hr = dayTasks.get(j).length_hr.add(dayWork.get(i).getLengthHr());
							hasTask = true;
						}
					}
					// ���� ���, �� ��������� ������ � ������
					if (!hasTask) {
						dayTasks.add(new ReportItem(dayWork.get(i).getTask().getName(), dayWork.get(i).getProject().getName(),
								dayWork.get(i).getLengthDt(), dayWork.get(i).getLengthHr()));
					}
					// ���������, ���� �� ��� � ������ ����������� �������� ������ ������� ������
					Boolean hasProject = false;
					for (int k = 0; k < dayProjects.size(); k++) {
						if (dayWork.get(i).getProject().getName().equals(dayProjects.get(k))) {
							hasProject = true;
						}
					}
					// ���� ���, �� ��������� ���
					if (!hasProject) {
						dayProjects.add(dayWork.get(i).getProject().getName());
					}
				}
			}
			// ���� ��� ������ ������ ������
			String report_st_date = new SimpleDateFormat("dd.MM.yyyy").format(report_st_calendar.getTime());
			String report_fn_date = new SimpleDateFormat("dd.MM.yyyy").format(report_fn_calendar.getTime());
			Frame frame1 = new Frame("����� �� ������ � " + report_st_date + " �� " + report_fn_date);
			frame1.setResizable(false);
			frame1.setLayout(new FlowLayout(FlowLayout.LEFT));
			JPanel panel = new JPanel();
			frame1.add(panel);
			panel.setLayout(new GridLayout(1, 1, 100, 100));
			// ��������� ������ ����
			panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
			// ��������� ������� ������ �������� ������
			TextArea reportText = new TextArea(30, 100);
			reportText.setBackground(Color.white);
			reportText.setForeground(Color.black);
			for (int i = 0; i < TimeTrack.projectList.size(); i++) {
				for (int j = 0; j < dayProjects.size(); j++) {
					if (TimeTrack.projectList.get(i).getName().equals(dayProjects.get(j))) {
						reportText.append("������ \"" + dayProjects.get(j) + "\"\n");
						for (int k = 0; k < dayTasks.size(); k++) {
							if (dayTasks.get(k).project.equals(dayProjects.get(j))) {
								reportText.append(
										"- ������ \"" + dayTasks.get(k).task + "\" - " + dayTasks.get(k).length_hr + " �.\n");
							}
						}
						reportText.append("\n");
					}
				}
			}
			reportText.setEditable(false);
			panel.add(reportText);
			frame1.pack();
			// ��������� ���� � ������ ������
			frame1.setLocationRelativeTo(null);
			frame1.setVisible(true);
			// ��������� ���� ������ ������
			frame.dispose();

			frame1.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					((Window) e.getSource()).dispose();
				}
			});
		}
	}
}

class GetProjectReportButtonListener implements ActionListener {
	private Frame frame;
	private JComboBox<String> report_project;

	private class ReportItem {
		private String task;
		private String date_start;
		private BigDecimal length_dt;
		private BigDecimal length_hr;

		public ReportItem(String task, String date_start, BigDecimal length_dt, BigDecimal length_hr) {
			super();
			this.task = task;
			this.date_start = date_start;
			this.length_dt = length_dt;
			this.length_hr = length_hr;
		}
	}

	public GetProjectReportButtonListener(Frame p_frame, JComboBox<String> p_report_project) {
		report_project = p_report_project;
		frame = p_frame;
	}

	public void actionPerformed(ActionEvent e) {
		// ��������� ������
		TimeTrack.loadJournal();
		// ���� ������ �� ������� �������
		ArrayList<Journal> dayWork = new ArrayList<Journal>();
		for (int i = 0; i < TimeTrack.journalList.size(); i++) {
			if (TimeTrack.journalList.get(i).getProject().getName().equals(report_project.getSelectedItem().toString())) {
				dayWork.add(TimeTrack.journalList.get(i));
			}
			;
		}
		;
		// ������� ������ ����� �� ���� ��� ���������� �������
		ArrayList<ReportItem> projectTasks = new ArrayList<ReportItem>();
		ArrayList<String> projectDays = new ArrayList<String>();
		Calendar cal_start = Calendar.getInstance();
		for (int i = 0; i < dayWork.size(); i++) {
			cal_start = dayWork.get(i).getDateStart();
			String date_start = new SimpleDateFormat("yyyy.MM.dd").format(cal_start.getTime());
			if (projectTasks.size() == 0) {
				projectTasks.add(new ReportItem(dayWork.get(i).getTask().getName(), date_start, dayWork.get(i).getLengthDt(),
						dayWork.get(i).getLengthHr()));
				projectDays.add(date_start);
			} else {
				// ���������, ���� �� ��� � ������ ����� ������ �� ������ �� ������� �� ������ ����
				// ���� ����, �� ���������� ����
				Boolean hasTask = false;
				for (int j = 0; j < projectTasks.size(); j++) {
					String curr_date_start = new SimpleDateFormat("yyyy.MM.dd").format(cal_start.getTime());
					if ((dayWork.get(i).getTask().getName().equals(projectTasks.get(j).task))
							& (curr_date_start.equals(projectTasks.get(j).date_start))) {
						projectTasks.get(j).length_dt = projectTasks.get(j).length_dt.add(dayWork.get(i).getLengthDt());
						projectTasks.get(j).length_hr = projectTasks.get(j).length_hr.add(dayWork.get(i).getLengthHr());
						hasTask = true;
					}
				}
				// ���� ���, �� ��������� ������ � ������
				if (!hasTask) {
					projectTasks.add(new ReportItem(dayWork.get(i).getTask().getName(), date_start,
							dayWork.get(i).getLengthDt(), dayWork.get(i).getLengthHr()));
				}
				// ���������, ���� �� ��� � ������ ���� ������ �� ������� ���� ������� ������
				Boolean hasDay = false;
				for (int k = 0; k < projectDays.size(); k++) {
					if (date_start.equals(projectDays.get(k))) {
						hasDay = true;
					}
				}
				// ���� ���, �� ��������� ���
				if (!hasDay) {
					projectDays.add(date_start);
				}
			}
		}
		// ���� ��� ������ ������ ������
		Frame frame1 = new Frame("����� �� ������� \"" + report_project.getSelectedItem().toString() + "\"");
		frame1.setResizable(false);
		frame1.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame1.add(panel);
		panel.setLayout(new GridLayout(1, 1, 100, 100));
		// ��������� ������ ����
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		// ��������� ������� ������ �������� ������
		TextArea reportText = new TextArea(30, 100);
		reportText.setBackground(Color.white);
		reportText.setForeground(Color.black);
		BigDecimal totalTime = null;
		BigDecimal.valueOf(0);
		Collections.sort(projectDays);
		String report_date;
		for (int i = 0; i < projectDays.size(); i++) {
			report_date = new SimpleDateFormat("dd.MM.yyyy").format(TimeTrack.StrToCalend(projectDays.get(i)).getTime());
			reportText.append(report_date + "\n");
			for (int j = 0; j < projectTasks.size(); j++) {
				if (projectTasks.get(j).date_start.equals(projectDays.get(i))) {
					reportText.append(
							"- ������ \"" + projectTasks.get(j).task + "\" - " + projectTasks.get(i).length_hr + " �.\n");
					try {
						totalTime = totalTime.add(projectTasks.get(i).length_hr);
					} catch (java.lang.NullPointerException ex) {
						totalTime = projectTasks.get(i).length_hr;
					}
					;

				}
			}
			reportText.append("\n");
		}
		if (totalTime == null) {
			reportText.append("����� �� �������: 0 �.\n");
		} else {
			reportText.append("����� �� �������: " + totalTime + " �.\n");
		}
		reportText.setEditable(false);
		panel.add(reportText);
		frame1.pack();
		// ��������� ���� � ������ ������
		frame1.setLocationRelativeTo(null);
		frame1.setVisible(true);
		// ��������� ���� ������ ������
		frame.dispose();

		frame1.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	}
}

class GetTaskReportButtonListener implements ActionListener {
	private JComboBox<String> project;
	private JComboBox<String> task;
	private Frame frame;

	private class ReportItem {
		private String task;
		private String date_start;
		private BigDecimal length_dt;
		private BigDecimal length_hr;

		public ReportItem(String task, String date_start, BigDecimal length_dt, BigDecimal length_hr) {
			super();
			this.task = task;
			this.date_start = date_start;
			this.length_dt = length_dt;
			this.length_hr = length_hr;
		}
	}

	public GetTaskReportButtonListener(Frame p_frame, JComboBox<String> p_project, JComboBox<String> p_task) {
		project = p_project;
		task = p_task;
		frame = p_frame;
	}

	public void actionPerformed(ActionEvent e) {
		// ������� � ������ ��������-�������� ������, ��������� �� �����
		Project report_project = new Project();
		for (int i = 0; i < TimeTrack.projectList.size(); i++) {
			if ((TimeTrack.projectList.get(i).getName().equals(project.getSelectedItem().toString()))) {
				report_project = TimeTrack.projectList.get(i);
			}
			;
		}
		;
		// ������� � ������ �����-�������� ������, ��������� �� �����
		Task report_task = new Task();
		for (int i = 0; i < TimeTrack.taskList.size(); i++) {
			if ((TimeTrack.taskList.get(i).getName().equals(task.getSelectedItem().toString()))
					& (TimeTrack.taskList.get(i).getProject().getName().equals(project.getSelectedItem().toString()))) {
				report_task = TimeTrack.taskList.get(i);
			}
			;
		}
		;
		// ��������� ������
		TimeTrack.loadJournal();
		// ���� ������ �� ������ ������
		ArrayList<Journal> dayWork = new ArrayList<Journal>();
		for (int i = 0; i < TimeTrack.journalList.size(); i++) {
			if ((TimeTrack.journalList.get(i).getTask().getName().equals(report_task.getName()))
					& (TimeTrack.journalList.get(i).getProject().getName().equals(report_project.getName()))) {
				dayWork.add(TimeTrack.journalList.get(i));
			}
		}
		;
		// ������� ������ �� ���� ��� ��������� ������
		ArrayList<ReportItem> projectTasks = new ArrayList<ReportItem>();
		ArrayList<String> projectDays = new ArrayList<String>();
		Calendar cal_start = Calendar.getInstance();
		for (int i = 0; i < dayWork.size(); i++) {
			cal_start = dayWork.get(i).getDateStart();
			String date_start = new SimpleDateFormat("yyyy.MM.dd").format(cal_start.getTimeInMillis());
			if (projectTasks.size() == 0) {
				projectTasks.add(new ReportItem(dayWork.get(i).getTask().getName(), date_start, dayWork.get(i).getLengthDt(),
						dayWork.get(i).getLengthHr()));
				projectDays.add(date_start);
			} else {
				// ���������, ���� �� ��� � ������ ����� ������ �� ������ �� ������� �� ������ ����
				// ���� ����, �� ���������� ����
				Boolean hasTask = false;
				for (int j = 0; j < projectTasks.size(); j++) {
					String curr_date_start = new SimpleDateFormat("yyyy.MM.dd").format(cal_start.getTimeInMillis());
					if ((dayWork.get(i).getTask().getName().equals(projectTasks.get(j).task))
							& (curr_date_start.equals(projectTasks.get(j).date_start))) {
						projectTasks.get(j).length_dt = projectTasks.get(j).length_dt.add(dayWork.get(i).getLengthDt());
						projectTasks.get(j).length_hr = projectTasks.get(j).length_hr.add(dayWork.get(i).getLengthHr());
						hasTask = true;
					}
				}
				// ���� ���, �� ��������� ������ � ������
				if (!hasTask) {
					projectTasks.add(new ReportItem(dayWork.get(i).getTask().getName(), date_start,
							dayWork.get(i).getLengthDt(), dayWork.get(i).getLengthHr()));
				}
				// ���������, ���� �� ��� � ������ ���� ������ �� ������� ���� ������� ������
				Boolean hasDay = false;
				for (int k = 0; k < projectDays.size(); k++) {
					if (date_start.equals(projectDays.get(k))) {
						hasDay = true;
					}
				}
				// ���� ���, �� ��������� ���
				if (!hasDay) {
					projectDays.add(date_start);
				}
			}
		}
		// ���� ��� ������ ������ ������
		Frame frame1 = new Frame(
				"����� �� ������ \"" + report_task.getName() + "\" ������� \"" + report_project.getName() + "\"");
		frame1.setResizable(false);
		frame1.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame1.add(panel);
		panel.setLayout(new GridLayout(1, 1, 100, 100));
		// ��������� ������ ����
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		// ��������� ������� ������ �������� ������
		TextArea reportText = new TextArea(30, 100);
		reportText.setBackground(Color.white);
		reportText.setForeground(Color.black);
		BigDecimal totalTime = null;
		BigDecimal.valueOf(0);
		Collections.sort(projectDays);
		String report_date;
		for (int i = 0; i < projectDays.size(); i++) {
			for (int j = 0; j < projectTasks.size(); j++) {
				if (projectTasks.get(j).date_start.equals(projectDays.get(i))) {
					report_date = new SimpleDateFormat("dd.MM.yyyy")
							.format(TimeTrack.StrToCalend(projectDays.get(i)).getTimeInMillis());
					reportText.append(report_date + " - " + projectTasks.get(i).length_hr + " �.\n");
					try {
						totalTime = totalTime.add(projectTasks.get(i).length_hr);
					} catch (java.lang.NullPointerException ex) {
						totalTime = projectTasks.get(i).length_hr;
					}
					;

				}
			}
			reportText.append("\n");
		}
		if (totalTime == null) {
			reportText.append("����� �� ������: 0 �.\n");
		} else {
			reportText.append("����� �� ������: " + totalTime + " �.\n");
		}
		reportText.setEditable(false);
		panel.add(reportText);
		frame1.pack();
		// ��������� ���� � ������ ������
		frame1.setLocationRelativeTo(null);
		frame1.setVisible(true);
		// ��������� ���� ������ ������
		frame.dispose();

		frame1.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	}
}

class SearchButtonListener implements ActionListener {
	private JComboBox<String> project_name;
	private JComboBox<String> task_name;
	private JSpinner work_date;
	private Frame frame;

	public SearchButtonListener(Frame p_frame, JComboBox<String> p_project_name, JComboBox<String> p_task_name,
			JSpinner p_work_date) {
		project_name = p_project_name;
		task_name = p_task_name;
		work_date = p_work_date;
		frame = p_frame;
	}

	public void actionPerformed(ActionEvent e) {
		Calendar cur_work_date = Calendar.getInstance();
		Date dt = new Date();
		dt = (Date) work_date.getValue();
		cur_work_date.setTimeInMillis(dt.getTime());
		// �������� �����
		TimeTrack.ResetTime(cur_work_date);
		Calendar next_work_date = Calendar.getInstance();
		next_work_date.setTimeInMillis(cur_work_date.getTimeInMillis());
		next_work_date.add(Calendar.DAY_OF_MONTH, 1);
		// ������� � ������ ��������-�������� ������, ��������� �� �����
		Project project = new Project();
		for (int i = 0; i < TimeTrack.projectList.size(); i++) {
			if (TimeTrack.projectList.get(i).getName().equals(project_name.getSelectedItem().toString())) {
				project = TimeTrack.projectList.get(i);
			}
			;
		}
		;
		// ������� � ������ �����-�������� ������, ��������� �� �����
		Task task = new Task();
		for (int i = 0; i < TimeTrack.taskList.size(); i++) {
			if ((TimeTrack.taskList.get(i).getName().equals(task_name.getSelectedItem().toString()))
					& (TimeTrack.taskList.get(i).getProject().getName().equals(project_name.getSelectedItem().toString()))) {
				task = TimeTrack.taskList.get(i);
			}
			;
		}
		;
		// ��������� ������
		TimeTrack.loadJournal();
		// ���� ���������� ������
		ArrayList<Journal> journalRecords = new ArrayList<Journal>();
		for (int i = 0; i < TimeTrack.journalList.size(); i++) {
			Calendar date_st = Calendar.getInstance();
			date_st.setTimeInMillis(TimeTrack.journalList.get(i).getDateStart().getTimeInMillis());
			TimeTrack.ResetTime(date_st);
			Calendar date_fn = Calendar.getInstance();
			date_fn.setTimeInMillis(TimeTrack.journalList.get(i).getDateFinish().getTimeInMillis());
			TimeTrack.ResetTime(date_fn);
			if ((TimeTrack.journalList.get(i).getTask().getName().equals(task.getName()))
					& (TimeTrack.journalList.get(i).getProject().getName().equals(project.getName()))
					& date_st.compareTo(cur_work_date) == 0
					& date_fn.compareTo(cur_work_date) == 0) {
				journalRecords.add(TimeTrack.journalList.get(i));
			}
		}
		;
		Find FndWnd = new Find();
		// ��������� ����������� ������
		FndWnd.acceptEditButton.addActionListener(new AcceptEditButtonListener(FndWnd, FndWnd.recordId));
		FndWnd.acceptDeleteButton.addActionListener(new AcceptDeleteButtonListener(FndWnd, FndWnd.recordId));
		FndWnd.cancelEditButton.addActionListener(new CancelButtonListener(FndWnd));

		// ��������� ������ � ����
		FndWnd.reportText.append("������ \"" + project.getName() +
				"\", ������ \"" + task.getName() + "\"");
		Number lastId = 0;
		Calendar cal = Calendar.getInstance();
		String date_start;
		String date_finish;
		for (int i = 0; i < journalRecords.size(); i++) {
			cal = journalRecords.get(i).getDateStart();
			date_start = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(cal.getTime());
			cal = journalRecords.get(i).getDateFinish();
			date_finish = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(cal.getTime());
			FndWnd.reportText.append("\n" +
					"ID: " + journalRecords.get(i).getId() +
					", ������: " + date_start +
					", ���������: " + date_finish);
			lastId = journalRecords.get(i).getId();
		}
		// ���� ����� �������������� ������
		FndWnd.recordId.setText(lastId.toString());
		// ��������� ���� � ������ ������
		FndWnd.setLocationRelativeTo(null);
		FndWnd.setVisible(true);
		frame.dispose();
	}
}

class AcceptEditButtonListener implements ActionListener {
	private JFrame frame;
	private TextField recordID;

	public AcceptEditButtonListener(JFrame p_frame, TextField p_recordID) {
		frame = p_frame;
		recordID = p_recordID;
	}

	public void actionPerformed(ActionEvent e) {
		TimeTrack.loadJournal();
		// ������� � ������� ������, ��������� �� �����
		Journal journal = new Journal();
		for (int i = 0; i < TimeTrack.journalList.size(); i++) {
			if (TimeTrack.journalList.get(i).getId().toString().equals(recordID.getText())) {
				journal = TimeTrack.journalList.get(i);
			}
			;
		}
		;
		// ���� �������������� ������
		Frame frame1 = new Frame("�������������� ������");
		frame1.setResizable(true);
		frame1.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame1.add(panel);
		panel.setLayout(new GridLayout(5, 2, 70, 5));
		// ��������� ������ ����
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		// ������
		panel.add(new Label("������"));
		TextField projectName = new TextField();
		projectName.setText(journal.getProject().getName());
		projectName.setEditable(false);
		panel.add(projectName);
		// ������
		panel.add(new Label("������"));
		TextField taskName = new TextField();
		taskName.setText(journal.getTask().getName());
		taskName.setEditable(false);
		panel.add(taskName);
		// ����� ������ ������
		panel.add(new Label("������"));
		JSpinner stDate = new JSpinner(new SpinnerDateModel());
		Date dt = new Date();
		dt.setTime(journal.getDateStart().getTimeInMillis());
		stDate.setValue(dt);
		panel.add(stDate);
		// ����� ��������� ������
		panel.add(new Label("���������"));
		JSpinner fnDate = new JSpinner(new SpinnerDateModel());
		dt.setTime(journal.getDateFinish().getTimeInMillis());
		fnDate.setValue(dt);
		panel.add(fnDate);
		// ������ ������ ������ ���������� ������� �������
		Button editButton = new Button("��");
		panel.add(editButton);
		editButton.addActionListener(new EditRecordButtonListener(frame1, journal, stDate, fnDate));
		// ������ ������ ��������������
		Button cancelButton = new Button("������");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame1));
		frame1.pack();
		// ��������� ���� � ������ ������
		frame1.setLocationRelativeTo(null);
		frame1.setVisible(true);

		frame1.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
		frame.dispose();
	}
}

class AcceptDeleteButtonListener implements ActionListener {
	private JFrame frame;
	private TextField recordID;

	public AcceptDeleteButtonListener(JFrame p_frame, TextField p_recordID) {
		frame = p_frame;
		recordID = p_recordID;
	}

	public void actionPerformed(ActionEvent e) {
		TimeTrack.loadJournal();
		// ������� � ������� ������, ��������� �� �����
		for (int i = 0; i < TimeTrack.journalList.size(); i++) {
			if (TimeTrack.journalList.get(i).getId().toString().equals(recordID.getText())) {
				// ������� ������ �� �������
				TimeTrack.journalList.remove(i);
			}
			;
		}
		;
		try {
			// ������ ������� ������� � ����
			FileOutputStream fos = new FileOutputStream(TimeTrack.JournalFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.journalList.size(); i++) {
				oos.writeObject(TimeTrack.journalList.get(i));
			}
			;
			// �������� �������
			oos.close();
			TimeTrack.loadJournal();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		frame.dispose();
	}
}

class EditRecordButtonListener implements ActionListener {
	private Frame frame;
	private Journal journal;
	private JSpinner date_start;
	private JSpinner date_finish;

	public EditRecordButtonListener(Frame p_frame, Journal p_journal, JSpinner p_date_start, JSpinner p_date_finish) {
		frame = p_frame;
		journal = p_journal;
		date_start = p_date_start;
		date_finish = p_date_finish;
	}

	public void actionPerformed(ActionEvent e) {
		TimeTrack.loadJournal();
		// ������� � ������� ������, ��������� �� �����
		for (int i = 0; i < TimeTrack.journalList.size(); i++) {
			if (TimeTrack.journalList.get(i).getId().equals(journal.getId())) {
				// ���������� ���� ������
				Date dt1 = (Date) date_start.getValue();
				Calendar cal1 = Calendar.getInstance();
				cal1.setTimeInMillis(dt1.getTime());
				TimeTrack.journalList.get(i).setDateStart(cal1);

				// ���������� ���� ���������
				Date dt2 = (Date) date_finish.getValue();
				Calendar cal2 = Calendar.getInstance();
				cal2.setTimeInMillis(dt2.getTime());
				TimeTrack.journalList.get(i).setDateFinish(cal2);
			}
			;
		}
		;
		try {
			// ������ ������� ������� � ����
			FileOutputStream fos = new FileOutputStream(TimeTrack.JournalFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.journalList.size(); i++) {
				oos.writeObject(TimeTrack.journalList.get(i));
			}
			;
			// �������� �������
			oos.close();
			TimeTrack.loadJournal();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		frame.dispose();
	}
}

class Find extends JFrame {
	private static final long serialVersionUID = 1L;
	public TextArea reportText;
	public Label idLabel;
	public TextField recordId;
	public Button acceptEditButton;
	public Button acceptDeleteButton;
	public Button cancelEditButton;

	public Find() {
		// ��������� ������� ������ �������� ������
		reportText = new TextArea(10, 60);
		reportText.setBackground(Color.white);
		reportText.setForeground(Color.black);
		reportText.setEditable(false);
		// ������� "�������������"
		idLabel = new Label("�������������");
		// ���� ����� �������������� ������
		recordId = new TextField();
		// ������ ������������� ��������������
		acceptEditButton = new Button("��������");
		// ������ ������������� ��������
		acceptDeleteButton = new Button("�������");
		// ������ ������ ��������������
		cancelEditButton = new Button("������");

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(reportText))
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addComponent(idLabel)
								.addComponent(recordId))
						.addGroup(layout.createSequentialGroup()
								.addComponent(acceptEditButton)
								.addComponent(acceptDeleteButton)
								.addComponent(cancelEditButton))));

		layout.linkSize(SwingConstants.HORIZONTAL, idLabel, recordId, acceptEditButton, acceptDeleteButton, cancelEditButton);
		layout.linkSize(SwingConstants.VERTICAL, idLabel, recordId, acceptEditButton, acceptDeleteButton, cancelEditButton);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createSequentialGroup()
						.addComponent(reportText))
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(BASELINE)
								.addComponent(idLabel)
								.addComponent(recordId)))
				.addGroup(layout.createParallelGroup(BASELINE)
						.addComponent(acceptEditButton)
						.addComponent(acceptDeleteButton)
						.addComponent(cancelEditButton)));

		setTitle("������ � �������");
		pack();
	}

}

class ChangeProject implements ActionListener {
	private JComboBox<String> project_name;
	private JComboBox<String> task_name;

	public ChangeProject(JComboBox<String> p_project_name, JComboBox<String> p_task_name) {
		project_name = p_project_name;
		task_name = p_task_name;
	}

	public void actionPerformed(ActionEvent arg0) {
		// ������� � ������ ��������-�������� ������, ��������� �� �����
		Project project = new Project();
		try {
			for (int i = 0; i < TimeTrack.projectList.size(); i++) {
				if ((TimeTrack.projectList.get(i).getName().equals(project_name.getSelectedItem().toString()))) {
					project = TimeTrack.projectList.get(i);
				}
				;
			}
			;
		} catch (NullPointerException ex) {
		}

		// ���������� ������ �����
		if (TimeTrack.taskListFile.exists()) {
			TimeTrack.loadProjectTasksFromFile(task_name, TimeTrack.taskListFile, project);
		}
	}
}

class TrayWindow extends Frame {
	private static final long serialVersionUID = 1L;
	public SystemTray systemTray = SystemTray.getSystemTray();
	public TrayIcon trayIcon;

	public TrayWindow(String p_header) throws IOException, AWTException {
		super(p_header);
		trayIcon = new TrayIcon(ImageIO.read(new File(TimeTrack.IconFilePath)), "���� �������� �������");
		trayIcon.addActionListener(new TrayListener(this));

		PopupMenu popupMenu = new PopupMenu();
		MenuItem item = new MenuItem("�����");
		item.addActionListener(new TrayItemListener(this));
		popupMenu.add(item);
		trayIcon.setPopupMenu(popupMenu);
		systemTray.add(trayIcon);
	}
}

class TrayItemListener implements ActionListener {
	private TrayWindow trWindow;

	public TrayItemListener(TrayWindow p_trWindow) {
		trWindow = p_trWindow;
	}

	public void actionPerformed(ActionEvent e) {
		trWindow.dispatchEvent(new WindowEvent(trWindow, WindowEvent.WINDOW_CLOSING));
	}
}

class TrayListener implements ActionListener {
	private TrayWindow trWindow;

	public TrayListener(TrayWindow p_trWindow) {
		trWindow = p_trWindow;
	}

	public void actionPerformed(ActionEvent e) {
		trWindow.setVisible(true);
		trWindow.setExtendedState(Frame.NORMAL);
	}
}

class MainWindowListener implements WindowListener {
	private TrayWindow trWindow;
	private JComboBox<String> projectListBox;
	private JComboBox<String> taskListBox;
	private JSpinner date_start;
	private JSpinner date_finish;
	private Button startButton;
	private Button finishButton;
	private FinishButtonListener listener;

	public MainWindowListener(TrayWindow p_trWindow, JComboBox<String> p_projectListBox, JComboBox<String> p_taskListBox,
			JSpinner p_date_start, JSpinner p_date_finish, Button p_startButton, Button p_finishButton) {
		trWindow = p_trWindow;
		projectListBox = p_projectListBox;
		taskListBox = p_taskListBox;
		date_start = p_date_start;
		date_finish = p_date_finish;
		startButton = p_startButton;
		finishButton = p_finishButton;
		listener = new FinishButtonListener(projectListBox, taskListBox, date_start, date_finish, startButton, finishButton);
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
		trWindow.systemTray.remove(trWindow.trayIcon);
	}

	public void windowClosing(WindowEvent e) {
		trWindow.systemTray.remove(trWindow.trayIcon);
		if (!startButton.isEnabled() & finishButton.isEnabled()) {
			listener.actionPerformed(new ActionEvent(finishButton, 0, null));
		}
		trWindow.dispose();
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
		trWindow.setVisible(false);
	}

	public void windowOpened(WindowEvent e) {
	}

}

class OKButtonListener implements ActionListener {
	private Frame frame;

	public OKButtonListener(Frame p_frame) {
		frame = p_frame;
	}

	public void actionPerformed(ActionEvent e) {
		frame.dispose();
	}
}