package print_4_4;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class FileWriter 
{
	
	public static void write(String fileName, String text) {
		
	    //Определяем файл
	    File file = new File(fileName);
	 
	    try {
	        //проверяем, что если файл не существует то создаем его
	        if(!file.exists()){
	            file.createNewFile();
	        }
	 
	        //PrintWriter обеспечит возможности записи в файл
	        PrintWriter out = new PrintWriter(file.getAbsoluteFile());
	 
	        try {
	            //Записываем текст у файл
	            out.print(text);
	        } finally {
	            //После чего мы должны закрыть файл
	            //Иначе файл не запишется
	            out.close();
	        }
	    } catch(IOException e) {
	        throw new RuntimeException(e);
	    }
	}

}


