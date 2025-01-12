package com.helloworldconsulting;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    public static PropertiesModel setProperty(String fileName) throws IOException {

        Properties prop = new Properties();
        PropertiesModel pm = new PropertiesModel();
        if(!fileName.isEmpty()){
            prop.load(new FileInputStream(fileName));
        }else{
            try(InputStream defaultProp = PropertiesLoader.class.getResourceAsStream("/config.properties")){
                prop.load(defaultProp);
            }catch (Exception e){
                System.out.println("Properties file not found");
            }
        }
        pm.setMail(prop.getProperty("Mail"));
        pm.setPassword(prop.getProperty("Password"));

        System.out.println(pm.getMail());
        System.out.println(pm.getPassword());

        return pm;
    }
}
