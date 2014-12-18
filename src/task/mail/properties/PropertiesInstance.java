package task.mail.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PropertiesInstance {
	private MyXMLProperties properties = null;
	public PropertiesInstance(String projectConfigProperties){
		properties = new MyXMLProperties();
		try {
			properties.load(new FileInputStream(projectConfigProperties));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public MyXMLProperties getProperties() {
		
		return properties;
	}
}
