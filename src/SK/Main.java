package SK;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
       Parent root = FXMLLoader.load(getClass().getResource("/SK.fxml"));
       Scene scene = new Scene(root);
       stage.setScene(scene);
       stage.show();
       stage.setResizable(true);
       stage.setTitle("Svet igara - game index (by 83deadpool)");
       stage.getIcons().add(new Image(getClass().getResourceAsStream("/sk.png")));
       stage.setOnCloseRequest((WindowEvent event) -> {
           Platform.exit();
       });
    }
    
    public static void main(String[] args) {
    	launch(args);
    }
    
}
