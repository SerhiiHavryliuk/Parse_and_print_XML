package print_4_4;

//import FileWorker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Parse_xml extends JFrame 
{

	private static String fileHtmlWrite = "D://print/print.html"; //
	static String htmlAll;
	static String htmlStart = "<!DOCTYPE html> \n <html> \n <head> \n <title></title> \n <meta charset = \"Windows-1251\"> \n <link rel=\"stylesheet\" href = \"style.css\"> \n </head> \n <body>";
	static String htmlEnd = "</body> /n </html>";
	//private static final String SPACE = "   ";
	
	//для FilleWorker
	//private static String text = "This new text \nThis new text2\nThis new text3\nThis new text4\n";
    private static String fileRead = "D://importxml/test.xml"; // чтение файла
    private static String fileWrite = "D://importxml/xmltext.xml"; // запись в фаил
    private static String text_replace;

	/**
	 * Создаем интерфейс приложения.
	 */
	public static void createGUI() {
		final JFrame frame = new JFrame("Program ParseXML version 4.4");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final Font font = new Font("Verdana", Font.PLAIN, 13);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		final JTextArea textArea = new JTextArea(15, 10);
		panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
		textArea.setFont(font);

		JButton parseButton = new JButton("Parse XML");
		parseButton.setFont(font);
		panel.add(parseButton, BorderLayout.SOUTH);

		parseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//Чтение файла
			        String textFromFile = FileWorker.read(fileRead);
			        System.out.println(textFromFile);
			    	// замена символов		        
			        text_replace = textFromFile.replaceAll("<documentsign>", "<!--<documentsign>");
			        text_replace =  text_replace.replaceAll("</documentsign>", "<documentsign>-->");
			    	//Запись в файл
			        FileWorker.write(fileWrite, text_replace);
			      //Удаление файла test.xml      
			        FileWorker.delete(fileRead);
			        
					Document doc = getDocument();
					showDocument(doc, textArea);
					htmlAll = htmlStart + textArea.getText() + htmlEnd; // формирование всего html документа
					FileWriter.write(fileHtmlWrite, htmlAll); // запись html документа
					
					 //Удаление файла xmltest.xml      
			        FileWorker.delete(fileWrite);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(frame,"Ошибка чтения файла! " + ex.getMessage() + " нет в папке importxml");
				}
			}
		});

		frame.getContentPane().add(panel);

		frame.setPreferredSize(new Dimension(800, 500));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/**
	 * Возвращает объект Document, который является объектным представлением
	 * XML документа.
	 */
	private static Document getDocument() throws Exception {
		try {
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			f.setValidating(false);
			DocumentBuilder builder = f.newDocumentBuilder();
			return builder.parse(new File("D://importxml/xmltext.xml"));
		} catch (Exception exception) {
			String message = "XML parsing error!";
			throw new Exception(message);
		}
	}

	private static void showDocument(Document doc, JTextArea textArea) {
		StringBuffer content = new StringBuffer();
		Node node = doc.getChildNodes().item(0);
		ApplicationNode appNode = new ApplicationNode(node);

		//content.append("____________________");

		List<ClassNode> classes = appNode.getClasses();

		for (int i = 0; i < classes.size(); i++) {
			ClassNode classNode = classes.get(i);
			//content.append(SPACE + "Class: " + classNode.getName() + " \n");

			List<MethodNode> methods = classNode.getMethods();

			for (int j = 0; j < methods.size(); j++) {
				MethodNode methodNode = methods.get(j);
				content.append(/*SPACE + SPACE + "______________ документ____________ " + */"\n"
						+ methodNode.getName() + " \n");
			}
		}

		textArea.setText(content.toString());
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame.setDefaultLookAndFeelDecorated(true);
				createGUI();
			}
		});
	}

	/**
	 * Объектное представление приложения.
	 */
	public static class ApplicationNode {

		Node node;

		public ApplicationNode(Node node) {
			this.node = node;
		}

		public List<ClassNode> getClasses() {
			ArrayList<ClassNode> classes = new ArrayList<ClassNode>();

			/**
			 * Получаем список дочерних узлов для данного узла XML, который
			 * соответствует приложению application. Здесь будут располагаться
			 * все узлы Node, каждый из которых является объектным
			 * представлением тега class для текущего тега application.
			 */
			NodeList classNodes = node.getChildNodes();

			for (int i = 0; i < classNodes.getLength(); i++) {
				Node node = classNodes.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {

					/**
					 * Создаем на основе Node узла своё объектное
					 * представление класса.
					 */
					ClassNode classNode = new ClassNode(node);
					classes.add(classNode);
				}
			}

			return classes;
		}

	}

	/**
	 * Объектное представление класса.
	 */
	public static class ClassNode {

		Node node;

		/**
		 * Создаем новый экземпляр объекта на основе Node узла.
		 */
		public ClassNode(Node node) {
			this.node = node;
		}

		/**
		 * Возвращает список методов класса.
		 */
		public List<MethodNode> getMethods() {
			ArrayList<MethodNode> methods = new ArrayList<MethodNode>();

			/**
			 * Получаем список дочерних узлов для данного узла XML, 
			 * который соответствует классу class. Здесь будут располагаться 
			 * все узлы Node, каждый из которых является объектным 
			 * представлением тега method для текущего тега class.
			 */
			NodeList methodNodes = node.getChildNodes();

			for (int i = 0; i < methodNodes.getLength(); i++) {
				node = methodNodes.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {

					/**
					 * Создаем на основе Node узла своё объектное представление
					 * метода.
					 */
					MethodNode methodNode = new MethodNode(node);
					methods.add(methodNode);
				}
			}

			return methods;
		}

		/**
		 * Возвращае имя класса.
		 */
		public String getName() {

			/**
			 * Получаем атрибуты узла метода.
			 */
			NamedNodeMap attributes = node.getAttributes();

			/**
			 * Получаем узел аттрибута.
			 */
			Node nameAttrib = attributes.getNamedItem("name");

			/**
			 * Возвращаем значение атрибута.
			 */
			return nameAttrib.getNodeValue();
		}
	}

	/**
	 * Объектное представление сущности метод класса.
	 */
	public static class MethodNode {

		Node node;

		/**
		 * Создаем новый экземпляр объекта на основе Node узла.
		 */
		public MethodNode(Node node) {
			this.node = node;
		}

		/**
		 * Возвращает имя метода.
		 */
		public String getName() {

			/**
			 * Получаем атрибуты узла метода.
			 */
			NamedNodeMap attributes = node.getAttributes();

			/**
			 * Получаем узел аттрибута.
			 */
			Node doc_series = attributes.getNamedItem("series");
			Node doc_number = attributes.getNamedItem("number");
			Node doc_lastname = attributes.getNamedItem("lastname");
			Node doc_firstname = attributes.getNamedItem("firstname");
			Node doc_middlename = attributes.getNamedItem("middlename");
			Node doc_universityname = attributes.getNamedItem("universityname");
			Node doc_qualificationname = attributes.getNamedItem("qualificationname");
			Node doc_specdirprofname = attributes.getNamedItem("specdirprofname");
			Node doc_educationqualificationnamerod = attributes.getNamedItem("educationqualificationnamerod");
			Node doc_bossworkpost = attributes.getNamedItem("bossworkpost");
			Node doc_boss = attributes.getNamedItem("boss");
			Node doc_dategive = attributes.getNamedItem("dategive");

			/**
			 * Возвращаем значение атрибута.
			 */
			String str ="<div class = doc_series >" + doc_series.getNodeValue()  + " № " +
					doc_number.getNodeValue() + "</div>" +"\n" + 
					"<div class = doc_lastname >" + doc_lastname.getNodeValue()  + "</div>" +
					"<div class = doc_firstname >" + doc_firstname.getNodeValue()+  "</div>" + "\n"+
					"<div class = doc_middlename >" + doc_middlename.getNodeValue() +  "</div>" + "\n" + 
					"<div class = doc_year> закiнчив у 2015 роцi </div>" + "\n" +
					"<div class = doc_universityname >" + doc_universityname.getNodeValue() +  "</div>" + "\n" +
					"<div class = doc_stypin > здобув ступiнь молодшого спецiалiста </div>" + "\n" +
					"<div class = doc_spec >за спецiальнiстью </div>" + "\n" +
					/*"<div class = doc_qualificationname >" +doc_qualificationname.getNodeValue() +  "</div>" + "\n" +*/
					"<div class = doc_specdirprofname >" +doc_specdirprofname.getNodeValue() +  "</div>" + "\n" +
					"<div class = doc_cvalific >та здобув квалiфiкацiю </div>" + "\n" +
					"<div class = doc_educationqualificationnamerod >" +doc_educationqualificationnamerod.getNodeValue() + "</div>" + "\n" +
					"<div class = doc_bossworkpost >" +doc_bossworkpost.getNodeValue() + ": " +
					doc_boss.getNodeValue()+  "</div >" + "\n" +
					"<div class = doc_dategive >" + doc_dategive.getNodeValue() + "</div>";

			return str;
		}

	}




}
