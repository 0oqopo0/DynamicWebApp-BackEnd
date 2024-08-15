package com.DynamicWebApp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DynamicWebAppBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(DynamicWebAppBackEndApplication.class, args);
		
//		var today = new Date();
        ////////////////////////////////////////////////////////////
        //      Current Date And Time   dd-MM-yyyy HH:mm:ss      //   
        //////////////////////////////////////////////////////////
	    LocalDateTime myDateObj = LocalDateTime.now();
	    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss");
	    String formattedDate = myDateObj.format(myFormatObj);
        
		System.out.println("////////////////////////////////////////////////////////////////");
		System.out.println("// App Running Bratann \"M.Sh\"" + " || "+ formattedDate+"       //");
		System.out.println("//////////////////////////////////////////////////////////////");
	}
}
