package SK;

import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.*;

/**
 * 
 *
 * @author 83deadpool
 */
public class Loader extends Preloader {
    
    Stage stage;
    Label label;
    Label l;
    
    private Scene createPreloaderScene() {
        label = new Label("uèitavam linkove sa neta...");
        l = new Label("može potrajati ako je konekcija spora");
        label.setFont(new Font(30));
        BorderPane p = new BorderPane();
        p.setCenter(label);
        p.setBottom(l);
        return new Scene(p, 400, 150);        
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setScene(createPreloaderScene());       
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }
    
    @Override
    public void handleStateChangeNotification(StateChangeNotification scn) {
        if (scn.getType() == StateChangeNotification.Type.BEFORE_START) {
            stage.hide();
        }
    }
    
}
