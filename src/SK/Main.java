package SK;

import com.sun.javafx.application.*;
import java.io.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.stage.*;


public class skGUI extends Application {

    
    @Override
    public void init() throws Exception, IOException {
        SK.loadFromFileNet();
    }

    @Override
    public void start(Stage stage) throws Exception {
       Parent root = FXMLLoader.load(getClass().getResource("SK.fxml"));
       Scene scene = new Scene(root);
       stage.setScene(scene);
       stage.show();
       stage.setResizable(true);
       stage.setTitle("Svet igara - pretraga (by 83deadpool)");
       stage.getIcons().add(new Image(getClass().getResourceAsStream("sk.png")));
       stage.setOnCloseRequest((WindowEvent event) -> {
           Platform.exit();
       });
    }
    
    public static void main(String[] args){
       LauncherImpl.launchApplication(skGUI.class, Loader.class, args);
    }
    
}
