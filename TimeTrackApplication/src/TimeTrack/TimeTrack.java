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
	// static String projectListFilePath = new String("/home/serene/TimeTrack/Projects.txt"); // В Ubuntu с относительными
	// путями
	// jar не запускается нормально,
	// возможно дело в jdk
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
		UIManager.put("OptionPane.yesButtonText", "Да");
		UIManager.put("OptionPane.noButtonText", "Нет");
		UIManager.put("OptionPane.cancelButtonText", "Отменить");
		// Определение текущего значения настроек
		settingsFile = new File(SettingsFilePath);
		// Проверка наличия файла настроек
		if (settingsFile.exists()) {
			// Количество записей в журнале
			JournalCounter = Integer.valueOf(getSetting("JournalCounter"));
			// Язык интерфейса
			ResourceLang = getSetting("ResourceLang");
		} else {
			// Количество записей в журнале
			JournalCounter = 1;
			// Язык интерфейса
			ResourceLang = "ru";
			setSettings();
		}
		// Главное окно программы
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
		mainFrame.setTitle("Учёт рабочего времени");

		// Установка способа расположения компонентов
		mainFrame.setLayout(new BoxLayout(mainFrame, BoxLayout.PAGE_AXIS));
		// Возможность менять размер окна
		mainFrame.setResizable(false);

		// Панель меню
		JPanel panelMenu = new JPanel();
		panelMenu.setLayout(new FlowLayout(FlowLayout.LEFT));
		// Установка границ меню
		panelMenu.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), null));
		mainFrame.add(panelMenu);
		// Создаем основное меню
		JMenuBar menuBar = new JMenuBar();
		panelMenu.add(menuBar);

		// Создаем подменю "Проекты"
		JMenu menuProject = new JMenu(Resources.getProjects(ResourceLang));
		menuBar.add(menuProject);
		// Создаем элементы подменю "Проекты"
		JMenuItem addProjectItem = new JMenuItem("Добавить проект");
		JMenuItem editProjectItem = new JMenuItem("Изменить проект");
		JMenuItem deleteProjectItem = new JMenuItem("Удалить проект");
		// Добавляем обработчики событий по выбору команды
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

		// Добавляем созданные элементы подменю "Проекты"
		menuProject.add(addProjectItem);
		menuProject.add(editProjectItem);
		menuProject.add(deleteProjectItem);

		// Создаем подменю "Задачи"
		JMenu menuTask = new JMenu("Задачи");
		menuBar.add(menuTask);
		// Создаем элементы подменю "Задачи"
		JMenuItem addTaskItem = new JMenuItem("Добавить задачу");
		JMenuItem editTaskItem = new JMenuItem("Изменить задачу");
		JMenuItem deleteTaskItem = new JMenuItem("Удалить задачу");
		// Добавляем обработчики событий по выбору команды
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
		// Добавляем созданные элементы подменю "Задачи"
		menuTask.add(addTaskItem);
		menuTask.add(editTaskItem);
		menuTask.add(deleteTaskItem);

		// Создаем подменю "Отчёты"
		JMenu menuReport = new JMenu("Отчёты");
		menuBar.add(menuReport);
		// Создаем элементы подменю "Отчёты"
		JMenuItem dayReportItem = new JMenuItem("Отчёт за день");
		JMenuItem periodReportItem = new JMenuItem("Отчёт за период");
		JMenuItem projectReportItem = new JMenuItem("Отчёт по проекту");
		JMenuItem taskReportItem = new JMenuItem("Отчёт по задаче");
		// Добавляем обработчики событий по выбору команды
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
		// Добавляем созданные элементы подменю "Отчёты"
		menuReport.add(dayReportItem);
		menuReport.add(periodReportItem);
		menuReport.add(projectReportItem);
		menuReport.add(taskReportItem);

		// Создаем подменю "Журнал"
		JMenu menuJournal = new JMenu("Журнал");
		menuBar.add(menuJournal);
		// Создаем элементы подменю "Отчёты"
		JMenuItem editJournalItem = new JMenuItem("Редактировать");
		// Добавляем обработчики событий по выбору команды
		editJournalItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editJournal();
			}
		});
		// Добавляем созданные элементы подменю "Журнал"
		menuJournal.add(editJournalItem);

		// Создаем подменю "Настройки"
		JMenu menuLang = new JMenu("Настройки");
		menuBar.add(menuLang);
		// Создаем элементы подменю "Настройки"
		JMenuItem enLang = new JMenuItem("Английский");
		JMenuItem ruLang = new JMenuItem("Русский");
		// Добавляем обработчики событий по выбору команды
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
		// Добавляем созданные элементы подменю "Настройки"
		menuLang.add(enLang);
		menuLang.add(ruLang);
		// Определяем видимость элементов в зависимости от текущего языка интерфейса
		if (TimeTrack.ResourceLang.equals("ru")) {
			ruLang.setVisible(false);
		}
		if (TimeTrack.ResourceLang.equals("en")) {
			enLang.setVisible(false);
		}

		// Создаем подменю "?"
		JMenu menuHelp = new JMenu("?");
		menuBar.add(menuHelp);
		// Создаем элементы подменю "?"
		JMenuItem helpItem = new JMenuItem("Вызов справки");
		JMenuItem infoItem = new JMenuItem("О программе");
		// Добавляем обработчики событий по выбору команды
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
		// Добавляем созданные элементы подменю "?"
		menuHelp.add(helpItem);
		menuHelp.add(infoItem);

		// Панель проекта
		JPanel panelProject = new JPanel();
		// Установка границ панели
		panelProject.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		panelProject.setLayout(new GridLayout(5, 2, 70, 5));
		mainFrame.add(panelProject);
		// Надпись "Проект"
		panelProject.add(new Label("Проект"));
		// Поле выбора проекта
		projectListBox = new JComboBox<String>();
		projectListFile = new File(projectListFilePath);
		// Загружаем список проектов
		if (projectListFile.exists()) {
			loadProjectsFromFile(projectListBox, projectList, projectListFile);
		}
		panelProject.add(projectListBox);
		// Надпись "Задача"
		panelProject.add(new Label("Задача"));
		// Поле выбора задачи
		projectTaskListBox = new JComboBox<String>();
		taskListFile = new File(taskListFilePath);
		// Загружаем список всех задач
		if (taskListFile.exists()) {
			loadAllTasks(taskList, taskListFile);
		}
		// Находим в списке проектов-объектов проект, выбранный на форме
		Project project = new Project();
		for (int i = 0; i < projectList.size(); i++) {
			if ((projectList.get(i).getName().equals(projectListBox.getSelectedItem().toString()))) {
				project = TimeTrack.projectList.get(i);
			}
			;
		}
		;
		// Загружаем список задач проекта
		if (taskListFile.exists()) {
			loadProjectTasksFromFile(projectTaskListBox, taskListFile, project);
		}
		panelProject.add(projectTaskListBox);
		// Добавляем обработчик события по нажатию
		projectListBox.addActionListener(new ChangeProject(projectListBox, projectTaskListBox));
		// Надпись "Начало работы"
		panelProject.add(new Label("Начало работы"));
		// Устанавливаем русскую локаль, чтобы даты и прочее отражались традиционно
		Locale locale = new Locale("ru", "RU");
		JSpinner.setDefaultLocale(locale);
		// Поле ввода времени начала работы
		JSpinner dateStart = new JSpinner(new SpinnerDateModel());
		// Устанавливаем формат ввода данных: часы и минуты
		dateStart.setEditor(new JSpinner.DateEditor(dateStart, "HH:mm"));
		panelProject.add(dateStart);
		// Надпись "Окончание работы"
		panelProject.add(new Label("Окончание работы"));
		// Поле ввода времени окончания работы
		JSpinner dateFinish = new JSpinner(new SpinnerDateModel());
		dateFinish.setEnabled(false);
		// Устанавливаем формат ввода данных: часы и минуты
		dateFinish.setEditor(new JSpinner.DateEditor(dateFinish, "HH:mm"));
		panelProject.add(dateFinish);
		// Кнопка "Начать работу"
		Button startButton = new Button("Начать работу");
		panelProject.add(startButton);
		// Кнопка "Закончить работу"
		Button finishButton = new Button("Закончить работу");
		finishButton.setEnabled(false);
		panelProject.add(finishButton);
		// Установка фокуса на поле выбора проекта
		projectListBox.setFocusable(true);
		// Установка размера окна по его компонентам
		mainFrame.pack();
		// Появление окна в центре экрана
		mainFrame.setLocationRelativeTo(null);
		// Окно становится видимым
		mainFrame.setVisible(true);
		// Добавление обработчиков нажатия кнопок "Начать работу" и "Закончить работу"
		startButton.addActionListener(
				new StartButtonListener(projectListBox, projectTaskListBox, dateStart, dateFinish, startButton, finishButton));
		finishButton.addActionListener(
				new FinishButtonListener(projectListBox, projectTaskListBox, dateStart, dateFinish, startButton, finishButton));
		// Добавление обработчиков событий окна
		mainFrame.addWindowListener(new MainWindowListener(mainFrame, projectListBox, projectTaskListBox, dateStart, dateFinish,
				startButton, finishButton));
	}

	public static String getSetting(String p_name) {
		String Settings = null;
		String res;
		try {
			// Считывание содержимого файла настроек
			FileInputStream fis = new FileInputStream(SettingsFilePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			Settings = br.readLine();
			// Закрытие потоков
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
			System.out.println("Настройка не найдена!");
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
			// Закрытие потоков
			bw.close();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	};

	public static void loadProjectsFromList(JComboBox<String> projectListBox, ArrayList<Project> projectList) {
		// Считывание проектов из коллекции
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
			// Очистка projectList
			projectList.clear();
			// Считывание проектов из файла
			FileInputStream fis = new FileInputStream(projectListFilePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			while (fis.available() > 0) {
				projectList.add((Project) ois.readObject());
				Collections.sort(projectList);
			}
			// Закрытие потоков
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
			// Считывание задач из файла
			FileInputStream fis = new FileInputStream(taskListFilePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			while (fis.available() > 0) {
				taskList.add((Task) ois.readObject());
				Collections.sort(taskList);
			}
			// Закрытие потоков
			ois.close();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// Заполняем переменную-список задач проекта
		for (int i = 0; i < taskList.size(); i++) {
			if (taskList.get(i).getProject().getName().equals(project.getName())) {
				projectTaskList.add(taskList.get(i));
				Collections.sort(projectTaskList);
			}
			;
		}
		// Заполняем комбобокс-список задач проекта
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
			// Считывание задач из файла
			FileInputStream fis = new FileInputStream(taskListFilePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			while (fis.available() > 0) {
				taskList.add((Task) ois.readObject());
				Collections.sort(taskList);
			}
			// Закрытие потоков
			ois.close();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	};

	public static void addProject(JComboBox<String> p_projectListBox) {
		Frame frame = new Frame("Добавление проекта");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(4, 2, 70, 5));
		// Установка границ окна
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		panel.add(new Label("Название проекта"));
		TextField name = new TextField();
		panel.add(name);
		panel.add(new Label("Дата начала"));
		JSpinner dateStart = new JSpinner(new SpinnerDateModel());
		panel.add(dateStart);
		panel.add(new Label("Дата окончания"));
		JSpinner dateFinish = new JSpinner(new SpinnerDateModel());
		panel.add(dateFinish);
		Button addProjectButton = new Button("Добавить проект");
		panel.add(addProjectButton);
		addProjectButton.addActionListener(new AddProjectButtonListener(p_projectListBox, frame, name, dateStart, dateFinish));
		// Кнопка отмены добавления проекта
		Button cancelButton = new Button("Отмена");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// Появление окна в центре экрана
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	}

	public static void editProject() {
		Frame frame = new Frame("Редактирование проекта");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(4, 2, 70, 5));
		// Установка границ окна
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		panel.add(new Label("Название проекта"));
		TextField projectName = new TextField();
		projectName.setText(projectListBox.getSelectedItem().toString());
		panel.add(projectName);
		// Находим в списке проектов-объектов проект, выбранный на форме
		Project project = new Project();
		for (int i = 0; i < projectList.size(); i++) {
			if ((projectList.get(i).getName().equals(projectName.getText()))) {
				project = projectList.get(i);
			}
			;
		}
		;
		// Дата начала проекта
		panel.add(new Label("Дата начала"));
		dateStart = new JSpinner(new SpinnerDateModel(project.getDateStart(), null, null, Calendar.DATE));
		panel.add(dateStart);
		// Дата окончания проекта
		panel.add(new Label("Дата окончания"));
		dateFinish = new JSpinner(new SpinnerDateModel(project.getDateFinish(), null, null, Calendar.DATE));
		panel.add(dateFinish);
		// Кнопка сохранения проекта
		Button editTaskButton = new Button("Сохранение");
		panel.add(editTaskButton);
		editTaskButton.addActionListener(new EditProjectButtonListener(frame, project, projectName, dateStart, dateFinish));
		// Кнопка отмены редактирования проекта
		Button cancelButton = new Button("Отмена");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// Появление окна в центре экрана
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	}

	public static void deleteProject() {
		Frame frame = new Frame("Удаление проекта");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(2, 2, 70, 5));
		// Установка границ окна
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		panel.add(new Label("Название проекта"));
		JComboBox<String> projectName = new JComboBox<String>();
		loadProjectsFromList(projectName, projectList);
		// Находим правильный проект в списке проектов
		for (int i = 0; i < projectName.getItemCount(); i++) {
			if (projectName.getItemAt(i).toString().equals(projectListBox.getSelectedItem().toString())) {
				projectName.setSelectedIndex(i);
			}
			;
		}
		;
		panel.add(projectName);
		// Кнопка удаления проекта
		Button deleteProjectButton = new Button("Удаление");
		panel.add(deleteProjectButton);
		deleteProjectButton.addActionListener(new DeleteProjectButtonListener(frame, projectName));
		// Кнопка отмены удаления проекта
		Button cancelButton = new Button("Отмена");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// Появление окна в центре экрана
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	}

	public static void addTask() {
		Frame frame = new Frame("Добавление задачи");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(5, 2, 70, 5));
		// Установка границ окна
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		panel.add(new Label("Название проекта"));
		JComboBox<String> projectName = new JComboBox<String>();
		loadProjectsFromList(projectName, projectList);
		// Находим правильный проект в списке проектов
		for (int i = 0; i < projectName.getItemCount(); i++) {
			if (projectName.getItemAt(i).toString().equals(projectListBox.getSelectedItem().toString())) {
				projectName.setSelectedIndex(i);
			}
			;
		}
		;
		panel.add(projectName);
		panel.add(new Label("Название задачи"));
		TextField taskName = new TextField();
		panel.add(taskName);
		panel.add(new Label("Дата начала"));
		JSpinner dateStart = new JSpinner(new SpinnerDateModel());
		panel.add(dateStart);
		panel.add(new Label("Дата окончания"));
		JSpinner dateFinish = new JSpinner(new SpinnerDateModel());
		panel.add(dateFinish);
		Button addTaskButton = new Button("Добавить задачу");
		panel.add(addTaskButton);
		addTaskButton.addActionListener(new AddTaskButtonListener(projectName, frame, taskName, dateStart, dateFinish));
		// Кнопка отмены добавления задачи
		Button cancelButton = new Button("Отмена");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// Появление окна в центре экрана
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	}

	public static void editTask() {
		Frame frame = new Frame("Редактирование задачи");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(5, 2, 70, 5));
		// Установка границ окна
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		panel.add(new Label("Название проекта"));
		JComboBox<String> project = new JComboBox<String>();
		loadProjectsFromList(project, projectList);
		// Находим правильный проект в списке проектов
		for (int i = 0; i < project.getItemCount(); i++) {
			if (project.getItemAt(i).toString().equals(projectListBox.getSelectedItem().toString())) {
				project.setSelectedIndex(i);
			}
			;
		}
		;
		panel.add(project);
		panel.add(new Label("Название задачи"));
		TextField taskName = new TextField();
		taskName.setText(projectTaskListBox.getSelectedItem().toString());
		panel.add(taskName);
		// Находим в списке задач-объектов задачу, выбранную на форме
		Task task = new Task();
		for (int i = 0; i < taskList.size(); i++) {
			if ((taskList.get(i).getName().equals(taskName.getText()))
					& (taskList.get(i).getProject().getName().equals(project.getSelectedItem()))) {
				task = taskList.get(i);
			}
			;
		}
		;
		// Дата начала
		panel.add(new Label("Дата начала"));
		dateStart = new JSpinner(new SpinnerDateModel(task.getDateStart(), null, null, Calendar.DATE));
		panel.add(dateStart);
		// Дата окончания
		panel.add(new Label("Дата окончания"));
		dateFinish = new JSpinner(new SpinnerDateModel(task.getDateFinish(), null, null, Calendar.DATE));
		panel.add(dateFinish);
		// Кнопка сохранения задачи
		Button editTaskButton = new Button("Сохранение");
		panel.add(editTaskButton);
		editTaskButton.addActionListener(new EditTaskButtonListener(frame, task, project, taskName, dateStart, dateFinish));
		// Кнопка отмены редактирования задачи
		Button cancelButton = new Button("Отмена");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// Появление окна в центре экрана
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	}

	public static void deleteTask() {
		Frame frame = new Frame("Удаление задачи");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(5, 2, 70, 5));
		// Установка границ окна
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		panel.add(new Label("Название проекта"));
		TextField projectName = new TextField();
		projectName.setText(projectListBox.getSelectedItem().toString());
		// Находим в списке проектов-объектов проект, выбранный на форме
		Project project = new Project();
		for (int i = 0; i < projectList.size(); i++) {
			if (projectList.get(i).getName().equals(projectName.getText())) {
				project = projectList.get(i);
			}
			;
		}
		;
		panel.add(projectName);
		panel.add(new Label("Название задачи"));
		TextField taskName = new TextField();
		taskName.setText(projectTaskListBox.getSelectedItem().toString());
		panel.add(taskName);
		// Находим в списке задач-объектов задачу, выбранную на форме
		Task task = new Task();
		for (int i = 0; i < taskList.size(); i++) {
			if ((taskList.get(i).getName().equals(taskName.getText())) & (taskList.get(i).getProject().equals(project))) {
				task = taskList.get(i);
			}
			;
		}
		;
		// Дата начала
		panel.add(new Label("Дата начала"));
		dateStart = new JSpinner(new SpinnerDateModel(task.getDateStart(), null, null, Calendar.DATE));
		panel.add(dateStart);
		// Дата окончания
		panel.add(new Label("Дата окончания"));
		dateFinish = new JSpinner(new SpinnerDateModel(task.getDateFinish(), null, null, Calendar.DATE));
		panel.add(dateFinish);
		// Кнопка удаления задачи
		Button deleteTaskButton = new Button("Удаление");
		panel.add(deleteTaskButton);
		deleteTaskButton.addActionListener(new DeleteTaskButtonListener(frame, task, project));
		// Кнопка отмены удаления задачи
		Button cancelButton = new Button("Отмена");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// Появление окна в центре экрана
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
			// Очищаем список записей журнала
			journalList.clear();
			try {
				// Считывание журнала из файла
				FileInputStream fis = new FileInputStream(JournalFilePath);
				ObjectInputStream ois = new ObjectInputStream(fis);
				while (fis.available() > 0) {
					journalList.add((Journal) ois.readObject());
				}
				// Закрытие потоков
				ois.close();
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	};

	public static void dayReport() {
		Frame frame = new Frame("Отчёт за день");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(2, 2, 70, 5));
		// Установка границ окна
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		// Дата отчёта
		panel.add(new Label("Дата"));
		JSpinner reportDate = new JSpinner(new SpinnerDateModel());
		reportDate.setEditor(new JSpinner.DateEditor(reportDate, "dd.MM.yyyy"));
		panel.add(reportDate);
		// Кнопка получения отчёта
		Button getReportButton = new Button("Отчёт");
		panel.add(getReportButton);
		getReportButton.addActionListener(new GetDayReportButtonListener(frame, reportDate));
		// Кнопка отмены получения отчёта
		Button cancelButton = new Button("Отмена");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// Появление окна в центре экрана
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	};

	public static void periodReport() {
		Frame frame = new Frame("Отчёт за период");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(3, 2, 70, 5));
		// Установка границ окна
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		// Дата начала периода
		panel.add(new Label("Дата начала"));
		JSpinner reportStDate = new JSpinner(new SpinnerDateModel());
		reportStDate.setEditor(new JSpinner.DateEditor(reportStDate, "dd.MM.yyyy"));
		panel.add(reportStDate);
		// Дата окончания периода
		panel.add(new Label("Дата окончания"));
		JSpinner reportFnDate = new JSpinner(new SpinnerDateModel());
		reportFnDate.setEditor(new JSpinner.DateEditor(reportFnDate, "dd.MM.yyyy"));
		panel.add(reportFnDate);
		// Кнопка получения отчёта
		Button getReportButton = new Button("Отчёт");
		panel.add(getReportButton);
		getReportButton.addActionListener(new GetPeriodReportButtonListener(frame, reportStDate, reportFnDate));
		// Кнопка отмены получения отчёта
		Button cancelButton = new Button("Отмена");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// Появление окна в центре экрана
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	};

	public static void projectReport() {
		Frame frame = new Frame("Отчёт по проекту");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(2, 2, 80, 5));
		// Установка границ окна
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		// Проект для отчёта
		panel.add(new Label("Проект"));
		JComboBox<String> reportProject = new JComboBox<String>();
		loadProjectsFromList(reportProject, projectList);
		// Находим правильный проект в списке проектов
		for (int i = 0; i < reportProject.getItemCount(); i++) {
			if (reportProject.getItemAt(i).toString().equals(projectListBox.getSelectedItem().toString())) {
				reportProject.setSelectedIndex(i);
			}
			;
		}
		;
		panel.add(reportProject);
		// Кнопка получения отчёта
		Button getReportButton = new Button("Отчёт");
		panel.add(getReportButton);
		getReportButton.addActionListener(new GetProjectReportButtonListener(frame, reportProject));
		// Кнопка отмены получения отчёта
		Button cancelButton = new Button("Отмена");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// Появление окна в центре экрана
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});
	};

	public static void taskReport() {
		Frame frame = new Frame("Отчёт по задаче");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(3, 2, 70, 5));
		// Установка границ окна
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		// Проект для отчёта
		panel.add(new Label("Проект"));
		JComboBox<String> projectName = new JComboBox<String>();
		loadProjectsFromList(projectName, projectList);
		// Находим правильный проект в списке проектов
		for (int i = 0; i < projectName.getItemCount(); i++) {
			if (projectName.getItemAt(i).toString().equals(projectListBox.getSelectedItem().toString())) {
				projectName.setSelectedIndex(i);
			}
			;
		}
		;
		// Находим в списке проектов-объектов проект, выбранный на форме
		Project project = new Project();
		for (int i = 0; i < projectList.size(); i++) {
			if ((projectList.get(i).getName().equals(projectName.getSelectedItem().toString()))) {
				project = projectList.get(i);
			}
			;
		}
		;
		panel.add(projectName);

		// Задача для отчёта
		panel.add(new Label("Задача"));
		// Поле выбора задачи
		JComboBox<String> taskName = new JComboBox<String>();
		// Загружаем список задач проекта
		if (taskListFile.exists()) {
			loadProjectTasksFromFile(taskName, taskListFile, project);
		}
		// Находим правильную задачу в списке задач
		for (int i = 0; i < taskName.getItemCount(); i++) {
			if (taskName.getItemAt(i).toString().equals(projectTaskListBox.getSelectedItem().toString())) {
				taskName.setSelectedIndex(i);
			}
			;
		}
		;
		panel.add(taskName);

		// Добавляем обработчик события по нажатию
		projectName.addActionListener(new ChangeProject(projectName, taskName));

		// Кнопка получения отчёта
		Button getReportButton = new Button("Отчёт");
		panel.add(getReportButton);
		getReportButton.addActionListener(new GetTaskReportButtonListener(frame, projectName, taskName));
		// Кнопка отмены получения отчёта
		Button cancelButton = new Button("Отмена");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// Появление окна в центре экрана
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();
			}
		});

	};

	public static void editJournal() {
		Frame frame = new Frame("Редактирование журнала");
		frame.setResizable(true);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(4, 2, 80, 5));
		// Установка границ окна
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));

		// Проект
		panel.add(new Label("Проект"));
		JComboBox<String> projectName = new JComboBox<String>();
		// Загружаем список проектов
		if (projectListFile.exists()) {
			loadProjectsFromFile(projectName, projectList, projectListFile);
		}
		// Устанавливаем проект, выбранный на главной форме
		projectName.setSelectedItem(projectListBox.getSelectedItem());
		panel.add(projectName);

		// Задача
		panel.add(new Label("Задача"));
		JComboBox<String> taskName = new JComboBox<String>();
		// Загружаем список всех задач
		if (taskListFile.exists()) {
			loadAllTasks(taskList, taskListFile);
		}
		// Находим в списке проектов-объектов проект, выбранный на форме
		Project project = new Project();
		for (int i = 0; i < projectList.size(); i++) {
			if ((projectList.get(i).getName().equals(projectName.getSelectedItem().toString()))) {
				project = projectList.get(i);
			}
			;
		}
		;
		// Загружаем список задач проекта
		if (taskListFile.exists()) {
			loadProjectTasksFromFile(taskName, taskListFile, project);
		}
		// Устанавливаем задачу, выбранную на главной форме
		taskName.setSelectedItem(projectTaskListBox.getSelectedItem());
		panel.add(taskName);
		// Добавляем обработчик события при смене проекта
		projectName.addActionListener(new ChangeProject(projectName, taskName));
		// Дата выполнения работы
		panel.add(new Label("Дата"));
		JSpinner workDate = new JSpinner(new SpinnerDateModel());
		workDate.setEditor(new JSpinner.DateEditor(workDate, "dd.MM.yyyy"));
		panel.add(workDate);
		// Кнопка вызова списка подходящих записей журнала
		Button searchButton = new Button("Поиск");
		panel.add(searchButton);
		searchButton.addActionListener(new SearchButtonListener(frame, projectName, taskName, workDate));
		// Кнопка отмены поиска
		Button cancelButton = new Button("Отмена");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame));
		frame.pack();
		// Появление окна в центре экрана
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

		// Это какая-то шиза, но при записи данных в журнал дата почему-то перещелкивается на день вперед
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
		JFrame frame = new JFrame("О программе");
		frame.setResizable(false);
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new GridLayout(6, 1, 5, 5));
		// Установка границ окна
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		Label lb1 = new Label("Учет рабочего времени\n");
		lb1.setFont(new Font(null, Font.BOLD, 14));
		lb1.setAlignment(Label.CENTER);
		Label lb2 = new Label("Версия 1.0\n");
		lb2.setFont(new Font(null, Font.ITALIC, 12));
		lb2.setAlignment(Label.CENTER);
		Label lb3 = new Label("Программа поставляется по принципу \"as is\". Никакие гарантии не предусматриваются.");
		lb3.setFont(new Font(null, Font.PLAIN, 10));
		lb3.setAlignment(Label.CENTER);
		panel.add(lb1);
		panel.add(lb2);
		panel.add(lb3);
		Button OKButton = new Button("ОК");
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
		// Вычисление длительности проекта
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
			// Запись коллекции проектов в файл
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
			// Закрытие потоков
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
		// Выбираем во временный список задачи изменяемого проекта
		ArrayList<Task> projectTaskList = new ArrayList<Task>();
		for (int i = 0; i < TimeTrack.taskList.size(); i++) {
			if (TimeTrack.taskList.get(i).getProject().getName().equals(project.getName())) {
				projectTaskList.add(TimeTrack.taskList.get(i));
				Collections.sort(projectTaskList);
			}
			;
		}
		;

		// Выбираем во временный список записи в журнале для изменяемого проекта
		ArrayList<Journal> projectJournalList = new ArrayList<Journal>();
		TimeTrack.loadJournal();
		for (int i = 0; i < TimeTrack.journalList.size(); i++) {
			if (TimeTrack.journalList.get(i).getProject().getName().equals(project.getName())) {
				projectJournalList.add(TimeTrack.journalList.get(i));
			}
			;
		}
		;

		// Вычисление длительности проекта
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
		// Обновляем поле проекта в списке текущих задач проекта
		for (int i = 0; i < projectTaskList.size(); i++) {
			projectTaskList.get(i).setProject(project);
		}
		;
		// Обновляем поле проекта в списке записей в журнале для изменяемого проекта
		for (int i = 0; i < projectJournalList.size(); i++) {
			projectJournalList.get(i).setProject(project);
		}
		;
		try {
			// Запись коллекции проектов в файл
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
			// Закрытие потоков
			oos.close();
			// Запись коллекции задач в файл
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
			// Закрытие потоков
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try {
			// Запись объекта журнала в файл
			FileOutputStream fos = new FileOutputStream(TimeTrack.JournalFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.journalList.size(); i++) {
				oos.writeObject(TimeTrack.journalList.get(i));
			}
			;
			// Закрытие потоков
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
		// Удаление проекта
		for (int i = 0; i < TimeTrack.projectList.size(); i++) {
			if (TimeTrack.projectList.get(i).equals(project)) {
				TimeTrack.projectList.remove(i);
			}
			;
		}
		;
		try {
			// Запись коллекции проектов в файл
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
			// Закрытие потоков
			oos.close();
			// Запись коллекции задач в файл
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
			// Закрытие потоков
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		frame.dispose();
		TimeTrack.loadProjectsFromList(TimeTrack.projectListBox, TimeTrack.projectList);
	}

	public void actionPerformed(ActionEvent e) {
		// Находим в списке проектов-объектов проект, выбранный на форме
		Project project = new Project();
		for (int i = 0; i < TimeTrack.projectList.size(); i++) {
			if ((TimeTrack.projectList.get(i).getName().equals(project_name.getSelectedItem().toString()))) {
				project = TimeTrack.projectList.get(i);
			}
			;
		}
		;
		// Выбираем во временный список задачи удаляемого проекта
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
			int reply = JOptionPane.showConfirmDialog(null, "В проекте есть задачи. Удалить проект?", "Удалить проект",
					JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION) {
				// Удаление задач проекта
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
				// Удаление проекта
				DelProject(project);
			} else {
				// Закрытие окна удаления проекта
				frame.dispose();
			}
		} else {
			// Удаление проекта
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
		// Добавляем обработчик события по нажатию (если выбираем не первый в списке проект)
		p_project_list.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Заполнение имени выбранного проекта
				project_name = (String) p_project_list.getSelectedItem();
			}
		});
		// Заполнение имени выбранного проекта
		project_name = (String) p_project_list.getSelectedItem();
		frame = p_frame;
		task_name = p_task_name;
		date_start = p_date_start;
		date_finish = p_date_finish;

	}

	public void actionPerformed(ActionEvent e) {
		// Поиск проекта, к которому относится задача
		for (int i = 0; i < TimeTrack.projectList.size(); i++) {
			if (TimeTrack.projectList.get(i).getName() == project_name) {
				project = TimeTrack.projectList.get(i);
			}
			;
		}
		;
		// Вычисление длительности задачи
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
			// Запись коллекции задач в файл
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
			// Закрытие потоков
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		frame.dispose();

		// Поиск проекта, выбранного на основной форме
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

		// Выбираем во временный список записи в журнале для изменяемой задачи
		ArrayList<Journal> projectJournalList = new ArrayList<Journal>();
		TimeTrack.loadJournal();
		for (int i = 0; i < TimeTrack.journalList.size(); i++) {
			if (TimeTrack.journalList.get(i).getTask().getName().equals(task.getName())) {
				projectJournalList.add(TimeTrack.journalList.get(i));
			}
			;
		}
		;

		// Вычисление длительности задачи
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
		// Обновляем поле задачи в списке записей в журнале для изменяемого задачи
		for (int i = 0; i < projectJournalList.size(); i++) {
			projectJournalList.get(i).setTask(task);
		}
		;
		try {
			// Запись коллекции задач в файл
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
			// Закрытие потоков
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try {
			// Запись объекта журнала в файл
			FileOutputStream fos = new FileOutputStream(TimeTrack.JournalFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.journalList.size(); i++) {
				oos.writeObject(TimeTrack.journalList.get(i));
			}
			;
			// Закрытие потоков
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
		// Удаление задачи
		for (int i = 0; i < TimeTrack.taskList.size(); i++) {
			if (TimeTrack.taskList.get(i).equals(task)) {
				TimeTrack.taskList.remove(i);
			}
			;
		}
		;
		try {
			// Запись коллекции задач в файл
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
			// Закрытие потоков
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

		// Проверяем, отличается ли время начала работы от времени окончания.
		// Если отличается, значит пользователь изменил его вручную - не меняем
		// Если не отличается, то меняем на текущее время.
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

		// Поиск проекта в списке проектов
		for (int i = 0; i < TimeTrack.projectList.size(); i++) {
			if (TimeTrack.projectList.get(i).getName().equals(projectListBox.getSelectedItem())) {
				project = TimeTrack.projectList.get(i);
			}
			;
		}
		;
		// Поиск задачи в списке задач
		for (int i = 0; i < TimeTrack.taskList.size(); i++) {
			if ((TimeTrack.taskList.get(i).getName().equals(taskListBox.getSelectedItem()))
					& (TimeTrack.taskList.get(i).getProject().getName().equals(project.getName()))) {
				task = TimeTrack.taskList.get(i);
			}
			;
		}
		;

		// Создание объекта журнала для выбранной задачи
		Journal newJournal = new Journal();
		newJournal.setId(TimeTrack.JournalCounter);
		// Обновляем переменную счетчика
		TimeTrack.JournalCounter = TimeTrack.JournalCounter + 1;
		TimeTrack.setSettings();
		newJournal.setProject(project);
		newJournal.setTask(task);

		// Вычисление даты начала
		Date dt = (Date) date_start.getValue();
		Calendar cal_st_1 = Calendar.getInstance();
		TimeTrack.ResetTime(cal_st_1);
		Calendar cal_st_2 = Calendar.getInstance();
		cal_st_2.setTimeInMillis(dt.getTime());
		cal_st_1.set(Calendar.HOUR, cal_st_2.get(Calendar.HOUR));
		cal_st_1.set(Calendar.MINUTE, cal_st_2.get(Calendar.MINUTE));
		cal_st_1.set(Calendar.HOUR_OF_DAY, cal_st_2.get(Calendar.HOUR_OF_DAY));
		newJournal.setDateStart(cal_st_1);

		// Вычисление даты окончания
		dt = (Date) date_finish.getValue();
		Calendar cal_fn_1 = Calendar.getInstance();
		TimeTrack.ResetTime(cal_fn_1);
		Calendar cal_fn_2 = Calendar.getInstance();
		cal_fn_2.setTimeInMillis(dt.getTime());
		cal_fn_1.set(Calendar.HOUR, cal_fn_2.get(Calendar.HOUR));
		cal_fn_1.set(Calendar.MINUTE, cal_fn_2.get(Calendar.MINUTE));
		cal_fn_1.set(Calendar.HOUR_OF_DAY, cal_fn_2.get(Calendar.HOUR_OF_DAY));
		newJournal.setDateFinish(cal_fn_1);

		// Вычисление длительности выполнения задачи
		BigDecimal length_ms = new BigDecimal(
				newJournal.getDateFinish().getTimeInMillis() - newJournal.getDateStart().getTimeInMillis());
		length_hr = length_ms.divide(new BigDecimal("3600000"), 2, BigDecimal.ROUND_DOWN);
		length_dt = length_hr.divide(new BigDecimal("8"), 2, BigDecimal.ROUND_DOWN);

		newJournal.setLengthHr(length_hr);
		newJournal.setLengthDt(length_dt);
		TimeTrack.loadJournal();
		TimeTrack.journalList.add(newJournal);
		try {
			// Запись объекта журнала в файл
			FileOutputStream fos = new FileOutputStream(TimeTrack.JournalFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.journalList.size(); i++) {
				oos.writeObject(TimeTrack.journalList.get(i));
			}
			;
			// Закрытие потоков
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		/*
		 * // Отладка: проверяем, что сохраняется в файл журнала
		 * try{
		 * // Считывание задач из файла
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
		 * // Закрытие потоков
		 * ois.close();
		 * } catch(ClassNotFoundException ex) {
		 * ex.printStackTrace();
		 * }
		 * catch(IOException ex) {
		 * ex.printStackTrace();
		 * }
		 */

		// Перещелкиваем время начала работы и время окончания работы на текущее
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
		// Загружаем журнал
		TimeTrack.loadJournal();
		// Ищем записи за нужную дату
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
		// Создаем список задач и проектов дня
		ArrayList<ReportItem> dayTasks = new ArrayList<ReportItem>();
		ArrayList<String> dayProjects = new ArrayList<String>();
		for (int i = 0; i < dayWork.size(); i++) {
			if (dayTasks.size() == 0) {
				dayTasks.add(new ReportItem(dayWork.get(i).getTask().getName(), dayWork.get(i).getProject().getName(),
						dayWork.get(i).getLengthDt(), dayWork.get(i).getLengthHr()));
				dayProjects.add(dayWork.get(i).getProject().getName());
			} else {
				// Проверяем, есть ли уже в списке сегодняшних задач задача из журнала
				// Если есть, то прибавляем часы
				Boolean hasTask = false;
				for (int j = 0; j < dayTasks.size(); j++) {
					if ((dayWork.get(i).getTask().getName().equals(dayTasks.get(j).task))
							& (dayWork.get(i).getProject().getName().equals(dayTasks.get(j).project))) {
						dayTasks.get(j).length_dt = dayTasks.get(j).length_dt.add(dayWork.get(i).getLengthDt());
						dayTasks.get(j).length_hr = dayTasks.get(j).length_hr.add(dayWork.get(i).getLengthHr());
						hasTask = true;
					}
				}
				// Если нет, то добавляем задачу в список
				if (!hasTask) {
					dayTasks.add(new ReportItem(dayWork.get(i).getTask().getName(), dayWork.get(i).getProject().getName(),
							dayWork.get(i).getLengthDt(), dayWork.get(i).getLengthHr()));
				}
				// Проверяем, есть ли уже в списке сегодняшних проектов проект текущей задачи
				Boolean hasProject = false;
				for (int k = 0; k < dayProjects.size(); k++) {
					if (dayWork.get(i).getProject().getName().equals(dayProjects.get(k))) {
						hasProject = true;
					}
				}
				// Если нет, то добавляем его
				if (!hasProject) {
					dayProjects.add(dayWork.get(i).getProject().getName());
				}
			}
		}
		// Окно для вывода данных отчёта
		String report_date = new SimpleDateFormat("dd.MM.yyyy").format(report_calendar.getTime());
		Frame frame1 = new Frame("Отчёт за " + report_date);
		frame1.setResizable(false);
		frame1.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame1.add(panel);
		panel.setLayout(new GridLayout(1, 1, 100, 100));
		// Установка границ окна
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		// Добавляем область вывода отчётных данных
		TextArea reportText = new TextArea(30, 100);
		reportText.setBackground(Color.white);
		reportText.setForeground(Color.black);
		for (int i = 0; i < TimeTrack.projectList.size(); i++) {
			for (int j = 0; j < dayProjects.size(); j++) {
				if (TimeTrack.projectList.get(i).getName().equals(dayProjects.get(j))) {
					reportText.append("Проект \"" + dayProjects.get(j) + "\"\n");
					for (int k = 0; k < dayTasks.size(); k++) {
						if (dayTasks.get(k).project.equals(dayProjects.get(j))) {
							reportText.append(
									"- Задача \"" + dayTasks.get(k).task + "\" - " + dayTasks.get(k).length_hr + " ч.\n");
						}
					}
					reportText.append("\n");
				}
			}
		}
		reportText.setEditable(false);
		panel.add(reportText);
		frame1.pack();
		// Появление окна в центре экрана
		frame1.setLocationRelativeTo(null);
		frame1.setVisible(true);
		// Закрываем окно вызова отчёта
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
		// Загружаем журнал
		TimeTrack.loadJournal();
		// Ищем записи за нужную дату
		ArrayList<Journal> dayWork = new ArrayList<Journal>();
		Calendar report_st_calendar = Calendar.getInstance();
		report_st_calendar.setTime((Date) report_st_date.getValue());
		// Обнуляем время
		TimeTrack.ResetTime(report_st_calendar);
		Calendar report_fn_calendar = Calendar.getInstance();
		report_fn_calendar.setTime((Date) report_fn_date.getValue());
		// Обнуляем время
		TimeTrack.ResetTime(report_fn_calendar);
		if (report_fn_calendar.before(report_st_calendar)) {
			JOptionPane.showMessageDialog(null, "Дата начала периода должна быть меньше даты окончания периода!");
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
			// Создаем список задач и проектов дня
			ArrayList<ReportItem> dayTasks = new ArrayList<ReportItem>();
			ArrayList<String> dayProjects = new ArrayList<String>();
			for (int i = 0; i < dayWork.size(); i++) {
				if (dayTasks.size() == 0) {
					dayTasks.add(new ReportItem(dayWork.get(i).getTask().getName(), dayWork.get(i).getProject().getName(),
							dayWork.get(i).getLengthDt(), dayWork.get(i).getLengthHr()));
					dayProjects.add(dayWork.get(i).getProject().getName());
				} else {
					// Проверяем, есть ли уже в списке сегодняшних задач задача из журнала
					// Если есть, то прибавляем часы
					Boolean hasTask = false;
					for (int j = 0; j < dayTasks.size(); j++) {
						if ((dayWork.get(i).getTask().getName().equals(dayTasks.get(j).task))
								& (dayWork.get(i).getProject().getName().equals(dayTasks.get(j).project))) {
							dayTasks.get(j).length_dt = dayTasks.get(j).length_dt.add(dayWork.get(i).getLengthDt());
							dayTasks.get(j).length_hr = dayTasks.get(j).length_hr.add(dayWork.get(i).getLengthHr());
							hasTask = true;
						}
					}
					// Если нет, то добавляем задачу в список
					if (!hasTask) {
						dayTasks.add(new ReportItem(dayWork.get(i).getTask().getName(), dayWork.get(i).getProject().getName(),
								dayWork.get(i).getLengthDt(), dayWork.get(i).getLengthHr()));
					}
					// Проверяем, есть ли уже в списке сегодняшних проектов проект текущей задачи
					Boolean hasProject = false;
					for (int k = 0; k < dayProjects.size(); k++) {
						if (dayWork.get(i).getProject().getName().equals(dayProjects.get(k))) {
							hasProject = true;
						}
					}
					// Если нет, то добавляем его
					if (!hasProject) {
						dayProjects.add(dayWork.get(i).getProject().getName());
					}
				}
			}
			// Окно для вывода данных отчёта
			String report_st_date = new SimpleDateFormat("dd.MM.yyyy").format(report_st_calendar.getTime());
			String report_fn_date = new SimpleDateFormat("dd.MM.yyyy").format(report_fn_calendar.getTime());
			Frame frame1 = new Frame("Отчёт за период с " + report_st_date + " по " + report_fn_date);
			frame1.setResizable(false);
			frame1.setLayout(new FlowLayout(FlowLayout.LEFT));
			JPanel panel = new JPanel();
			frame1.add(panel);
			panel.setLayout(new GridLayout(1, 1, 100, 100));
			// Установка границ окна
			panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
			// Добавляем область вывода отчётных данных
			TextArea reportText = new TextArea(30, 100);
			reportText.setBackground(Color.white);
			reportText.setForeground(Color.black);
			for (int i = 0; i < TimeTrack.projectList.size(); i++) {
				for (int j = 0; j < dayProjects.size(); j++) {
					if (TimeTrack.projectList.get(i).getName().equals(dayProjects.get(j))) {
						reportText.append("Проект \"" + dayProjects.get(j) + "\"\n");
						for (int k = 0; k < dayTasks.size(); k++) {
							if (dayTasks.get(k).project.equals(dayProjects.get(j))) {
								reportText.append(
										"- Задача \"" + dayTasks.get(k).task + "\" - " + dayTasks.get(k).length_hr + " ч.\n");
							}
						}
						reportText.append("\n");
					}
				}
			}
			reportText.setEditable(false);
			panel.add(reportText);
			frame1.pack();
			// Появление окна в центре экрана
			frame1.setLocationRelativeTo(null);
			frame1.setVisible(true);
			// Закрываем окно вызова отчёта
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
		// Загружаем журнал
		TimeTrack.loadJournal();
		// Ищем записи по нужному проекту
		ArrayList<Journal> dayWork = new ArrayList<Journal>();
		for (int i = 0; i < TimeTrack.journalList.size(); i++) {
			if (TimeTrack.journalList.get(i).getProject().getName().equals(report_project.getSelectedItem().toString())) {
				dayWork.add(TimeTrack.journalList.get(i));
			}
			;
		}
		;
		// Создаем список задач по дням для выбранного проекта
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
				// Проверяем, есть ли уже в списке задач запись по задаче из журнала за данный день
				// Если есть, то прибавляем часы
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
				// Если нет, то добавляем задачу в список
				if (!hasTask) {
					projectTasks.add(new ReportItem(dayWork.get(i).getTask().getName(), date_start,
							dayWork.get(i).getLengthDt(), dayWork.get(i).getLengthHr()));
				}
				// Проверяем, есть ли уже в списке дней работы по проекту день текущей задачи
				Boolean hasDay = false;
				for (int k = 0; k < projectDays.size(); k++) {
					if (date_start.equals(projectDays.get(k))) {
						hasDay = true;
					}
				}
				// Если нет, то добавляем его
				if (!hasDay) {
					projectDays.add(date_start);
				}
			}
		}
		// Окно для вывода данных отчёта
		Frame frame1 = new Frame("Отчёт по проекту \"" + report_project.getSelectedItem().toString() + "\"");
		frame1.setResizable(false);
		frame1.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame1.add(panel);
		panel.setLayout(new GridLayout(1, 1, 100, 100));
		// Установка границ окна
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		// Добавляем область вывода отчётных данных
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
							"- Задача \"" + projectTasks.get(j).task + "\" - " + projectTasks.get(i).length_hr + " ч.\n");
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
			reportText.append("Итого по проекту: 0 ч.\n");
		} else {
			reportText.append("Итого по проекту: " + totalTime + " ч.\n");
		}
		reportText.setEditable(false);
		panel.add(reportText);
		frame1.pack();
		// Появление окна в центре экрана
		frame1.setLocationRelativeTo(null);
		frame1.setVisible(true);
		// Закрываем окно вызова отчёта
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
		// Находим в списке проектов-объектов проект, выбранный на форме
		Project report_project = new Project();
		for (int i = 0; i < TimeTrack.projectList.size(); i++) {
			if ((TimeTrack.projectList.get(i).getName().equals(project.getSelectedItem().toString()))) {
				report_project = TimeTrack.projectList.get(i);
			}
			;
		}
		;
		// Находим в списке задач-объектов задачу, выбранную на форме
		Task report_task = new Task();
		for (int i = 0; i < TimeTrack.taskList.size(); i++) {
			if ((TimeTrack.taskList.get(i).getName().equals(task.getSelectedItem().toString()))
					& (TimeTrack.taskList.get(i).getProject().getName().equals(project.getSelectedItem().toString()))) {
				report_task = TimeTrack.taskList.get(i);
			}
			;
		}
		;
		// Загружаем журнал
		TimeTrack.loadJournal();
		// Ищем записи по нужной задаче
		ArrayList<Journal> dayWork = new ArrayList<Journal>();
		for (int i = 0; i < TimeTrack.journalList.size(); i++) {
			if ((TimeTrack.journalList.get(i).getTask().getName().equals(report_task.getName()))
					& (TimeTrack.journalList.get(i).getProject().getName().equals(report_project.getName()))) {
				dayWork.add(TimeTrack.journalList.get(i));
			}
		}
		;
		// Создаем список по дням для выбранной задачи
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
				// Проверяем, есть ли уже в списке задач запись по задаче из журнала за данный день
				// Если есть, то прибавляем часы
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
				// Если нет, то добавляем задачу в список
				if (!hasTask) {
					projectTasks.add(new ReportItem(dayWork.get(i).getTask().getName(), date_start,
							dayWork.get(i).getLengthDt(), dayWork.get(i).getLengthHr()));
				}
				// Проверяем, есть ли уже в списке дней работы по проекту день текущей задачи
				Boolean hasDay = false;
				for (int k = 0; k < projectDays.size(); k++) {
					if (date_start.equals(projectDays.get(k))) {
						hasDay = true;
					}
				}
				// Если нет, то добавляем его
				if (!hasDay) {
					projectDays.add(date_start);
				}
			}
		}
		// Окно для вывода данных отчёта
		Frame frame1 = new Frame(
				"Отчёт по задаче \"" + report_task.getName() + "\" проекта \"" + report_project.getName() + "\"");
		frame1.setResizable(false);
		frame1.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame1.add(panel);
		panel.setLayout(new GridLayout(1, 1, 100, 100));
		// Установка границ окна
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		// Добавляем область вывода отчётных данных
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
					reportText.append(report_date + " - " + projectTasks.get(i).length_hr + " ч.\n");
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
			reportText.append("Итого по задаче: 0 ч.\n");
		} else {
			reportText.append("Итого по задаче: " + totalTime + " ч.\n");
		}
		reportText.setEditable(false);
		panel.add(reportText);
		frame1.pack();
		// Появление окна в центре экрана
		frame1.setLocationRelativeTo(null);
		frame1.setVisible(true);
		// Закрываем окно вызова отчёта
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
		// Обнуляем время
		TimeTrack.ResetTime(cur_work_date);
		Calendar next_work_date = Calendar.getInstance();
		next_work_date.setTimeInMillis(cur_work_date.getTimeInMillis());
		next_work_date.add(Calendar.DAY_OF_MONTH, 1);
		// Находим в списке проектов-объектов проект, выбранный на форме
		Project project = new Project();
		for (int i = 0; i < TimeTrack.projectList.size(); i++) {
			if (TimeTrack.projectList.get(i).getName().equals(project_name.getSelectedItem().toString())) {
				project = TimeTrack.projectList.get(i);
			}
			;
		}
		;
		// Находим в списке задач-объектов задачу, выбранную на форме
		Task task = new Task();
		for (int i = 0; i < TimeTrack.taskList.size(); i++) {
			if ((TimeTrack.taskList.get(i).getName().equals(task_name.getSelectedItem().toString()))
					& (TimeTrack.taskList.get(i).getProject().getName().equals(project_name.getSelectedItem().toString()))) {
				task = TimeTrack.taskList.get(i);
			}
			;
		}
		;
		// Загружаем журнал
		TimeTrack.loadJournal();
		// Ищем подходящие записи
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
		// Добавляем обработчики кнопок
		FndWnd.acceptEditButton.addActionListener(new AcceptEditButtonListener(FndWnd, FndWnd.recordId));
		FndWnd.acceptDeleteButton.addActionListener(new AcceptDeleteButtonListener(FndWnd, FndWnd.recordId));
		FndWnd.cancelEditButton.addActionListener(new CancelButtonListener(FndWnd));

		// Заполняем список в окне
		FndWnd.reportText.append("Проект \"" + project.getName() +
				"\", задача \"" + task.getName() + "\"");
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
					", начало: " + date_start +
					", окончание: " + date_finish);
			lastId = journalRecords.get(i).getId();
		}
		// Поле ввода идентификатора записи
		FndWnd.recordId.setText(lastId.toString());
		// Появление окна в центре экрана
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
		// Находим в журнале запись, выбранную на форме
		Journal journal = new Journal();
		for (int i = 0; i < TimeTrack.journalList.size(); i++) {
			if (TimeTrack.journalList.get(i).getId().toString().equals(recordID.getText())) {
				journal = TimeTrack.journalList.get(i);
			}
			;
		}
		;
		// Окно редактирования записи
		Frame frame1 = new Frame("Редактирование записи");
		frame1.setResizable(true);
		frame1.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		frame1.add(panel);
		panel.setLayout(new GridLayout(5, 2, 70, 5));
		// Установка границ окна
		panel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 5, 5), null));
		// Проект
		panel.add(new Label("Проект"));
		TextField projectName = new TextField();
		projectName.setText(journal.getProject().getName());
		projectName.setEditable(false);
		panel.add(projectName);
		// Задача
		panel.add(new Label("Задача"));
		TextField taskName = new TextField();
		taskName.setText(journal.getTask().getName());
		taskName.setEditable(false);
		panel.add(taskName);
		// Время начала работы
		panel.add(new Label("Начало"));
		JSpinner stDate = new JSpinner(new SpinnerDateModel());
		Date dt = new Date();
		dt.setTime(journal.getDateStart().getTimeInMillis());
		stDate.setValue(dt);
		panel.add(stDate);
		// Время окончания работы
		panel.add(new Label("Окончание"));
		JSpinner fnDate = new JSpinner(new SpinnerDateModel());
		dt.setTime(journal.getDateFinish().getTimeInMillis());
		fnDate.setValue(dt);
		panel.add(fnDate);
		// Кнопка вызова списка подходящих записей журнала
		Button editButton = new Button("ОК");
		panel.add(editButton);
		editButton.addActionListener(new EditRecordButtonListener(frame1, journal, stDate, fnDate));
		// Кнопка отмены редактирования
		Button cancelButton = new Button("Отмена");
		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonListener(frame1));
		frame1.pack();
		// Появление окна в центре экрана
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
		// Находим в журнале запись, выбранную на форме
		for (int i = 0; i < TimeTrack.journalList.size(); i++) {
			if (TimeTrack.journalList.get(i).getId().toString().equals(recordID.getText())) {
				// Удаляем запись из журнала
				TimeTrack.journalList.remove(i);
			}
			;
		}
		;
		try {
			// Запись объекта журнала в файл
			FileOutputStream fos = new FileOutputStream(TimeTrack.JournalFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.journalList.size(); i++) {
				oos.writeObject(TimeTrack.journalList.get(i));
			}
			;
			// Закрытие потоков
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
		// Находим в журнале запись, выбранную на форме
		for (int i = 0; i < TimeTrack.journalList.size(); i++) {
			if (TimeTrack.journalList.get(i).getId().equals(journal.getId())) {
				// Вычисление даты начала
				Date dt1 = (Date) date_start.getValue();
				Calendar cal1 = Calendar.getInstance();
				cal1.setTimeInMillis(dt1.getTime());
				TimeTrack.journalList.get(i).setDateStart(cal1);

				// Вычисление даты окончания
				Date dt2 = (Date) date_finish.getValue();
				Calendar cal2 = Calendar.getInstance();
				cal2.setTimeInMillis(dt2.getTime());
				TimeTrack.journalList.get(i).setDateFinish(cal2);
			}
			;
		}
		;
		try {
			// Запись объекта журнала в файл
			FileOutputStream fos = new FileOutputStream(TimeTrack.JournalFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (int i = 0; i < TimeTrack.journalList.size(); i++) {
				oos.writeObject(TimeTrack.journalList.get(i));
			}
			;
			// Закрытие потоков
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
		// Добавляем область вывода отчётных данных
		reportText = new TextArea(10, 60);
		reportText.setBackground(Color.white);
		reportText.setForeground(Color.black);
		reportText.setEditable(false);
		// Надпись "Идентификатор"
		idLabel = new Label("Идентификатор");
		// Поле ввода идентификатора записи
		recordId = new TextField();
		// Кнопка подтверждения редактирования
		acceptEditButton = new Button("Изменить");
		// Кнопка подтверждения удаления
		acceptDeleteButton = new Button("Удалить");
		// Кнопка отмены редактирования
		cancelEditButton = new Button("Отмена");

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

		setTitle("Записи в журнале");
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
		// Находим в списке проектов-объектов проект, выбранный на форме
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

		// Заполнение списка задач
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
		trayIcon = new TrayIcon(ImageIO.read(new File(TimeTrack.IconFilePath)), "Учёт рабочего времени");
		trayIcon.addActionListener(new TrayListener(this));

		PopupMenu popupMenu = new PopupMenu();
		MenuItem item = new MenuItem("Выйти");
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