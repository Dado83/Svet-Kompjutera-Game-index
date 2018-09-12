/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package updatereview;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import gamereview.GameReview;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * FXML Controller class
 *
 * @author PC-Admin
 */
public class UpdateController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label label;
    @FXML
    private Label brojLinkova;
    private int linkovi;

    private int br;

    ArrayList<GameReview> ii;
    @FXML
    private Label iteracija;
    @FXML
    private Label ukupnoVr;
    @FXML
    private Label preostaloVr;

    StringProperty iter = new SimpleStringProperty();
    StringProperty ukVr = new SimpleStringProperty();
    StringProperty preostaloVrijeme = new SimpleStringProperty();
    StringProperty protekloVr = new SimpleStringProperty();
    StringProperty nasIsp = new SimpleStringProperty();
    StringProperty ispravljeno = new SimpleStringProperty();
    StringProperty brojIgara = new SimpleStringProperty();
    StringProperty ucitanihIgara = new SimpleStringProperty();

    @FXML
    private Label protekloVrijeme;
    @FXML
    private Label naslov;
    @FXML
    private Button update;

    int step;
    int ispr;
    @FXML
    private Label isprNaslova;

    Thread links;
    Task loadLinks;
    HashSet<GameReview> igre = new HashSet<>();

    private static final Logger LOGGER = Logger.getLogger(UpdateController.class.getName());
    private List<String> linkURLs = new ArrayList<>();
    private int size;
    private Set<GameReview> games = new HashSet<>();
    private long elapsedTime;
    private long startTime;
    private List<Double> avgSingleIndexLoadTime = new ArrayList<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        LOGGER.info("entering loadLinksURLs()");
        LOGGER.info("Povezujem se na SK\n...ucitavam linkove...");
        Element temp = null;

        try {
            temp = Jsoup.connect("http://www.sk.rs/indexes/sections/op.html").get();
        } catch (IOException e1) {
            LOGGER.severe("ERROR in jsoup connect to SK");
        }
        Elements links = temp.select("a[href]");

        links.forEach((e) -> {
            linkURLs.add("http://www.sk.rs" + e.attr("href").substring(5));
        });
        LOGGER.info("Broj linkova prije filtera: " + linkURLs.size());

        List<String> tempList = linkURLs.stream().filter(ss -> !ss.contains("indexe")).collect(Collectors.toList());
        linkURLs = new ArrayList<>(tempList);
        LOGGER.info("Broj linkova poslije filtera: " + linkURLs.size());

    }

    //update sa neta
    @FXML
    public void saveToFile() throws IOException, InterruptedException {

        update.setDisable(true);

        loadLinks = new Task() {

            @Override
            protected Object call() throws Exception {

                long mjerenje = System.nanoTime();

                int size = linkURLs.size();
                brojIgara.set(size + "");
                for (int br = 0; br < linkURLs.size(); br++) {
                    long start = System.nanoTime();
                    step = br;
                    games.add(setGameReviewData(linkURLs.get(br)));
                    LOGGER.info("Ucitanih igara: " + games.size() + "/" + linkURLs.size());

                    double result = System.nanoTime() - start;
                    LOGGER.info("Vreme potrebno za 1 iteraciju: " + result / 1000000000 + " sekundi.");

                    int timeNeededToComplete = (int) (((result * size) / 1000000000) / 60);
                    LOGGER.info("Potrebno vreme za ucitavanje svih linkova: " + timeNeededToComplete + " minuta.");

                    int remainingTime = (int) (((result * (size - step)) / 1000000000) / 60);
                    LOGGER.info("Potrebno vreme za ucitavanje ostatka linkova: " + remainingTime + " minuta.");

                    elapsedTime = (start - mjerenje) / 1000000000 / 60;
                    LOGGER.info("Proteklo vrijeme ucitavanja linkova... " + elapsedTime);

                    avgSingleIndexLoadTime.add(result / 1000000000);

                    updateProgress(step, size);

                    if ((step % 10) == 0) {
                        Platform.runLater(() -> {

                            iter.set("" + result);
                            ukVr.set("" + timeNeededToComplete);
                            preostaloVrijeme.set("" + remainingTime);
                            protekloVr.set("" + elapsedTime);

                        });
                    } else {
                        Platform.runLater(() -> {
                            ucitanihIgara.set("" + step);
                        });

                    }
                    step++;
                }

                return null;

            }
        };

        Task ispraviNaslove = new Task() {
            @Override
            protected Object call() throws Exception {
                step = 1;
                ispr = 0;
                int size = games.size();
                for (GameReview i : games) {
                    if (i.getTitle().contains("http") || i.getTitle().equals("")) {
                        Element doc = null;
                        try {
                            doc = Jsoup.connect(i.getLink()).get();
                        } catch (IOException e1) {
                            LOGGER.severe("error connecting to game link using jsoup");
                        }
                        Elements date = doc.select("img[alt]");
                        List<Element> alt = new ArrayList<>();
                        date.forEach((e) -> {
                            alt.add(e);
                        });
                        if (alt.size() < 10) {
                        } else {
                            i.setTitle(alt.get(9).attr("alt"));
                        }
                        LOGGER.info(i.getTitle() + " ...ISPRAVLJEN");
                        ispr++;
                    }
                    Platform.runLater(() -> {
                        nasIsp.set(i.getTitle() + " ...ISPRAVLJEN");
                        ispravljeno.set("" + ispr);
                    });
                    updateProgress(step, size);
                    step++;
                }
                return null;
            }
        };

        ispraviNaslove.setOnSucceeded(
                (e) -> {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<Set<GameReview>>() {}.getType();
                        FileChooser fc = new FileChooser();
                        String desktop = System.getProperty("user.home") + "/desktop";
                        File file = new File(desktop);
                        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("TEXT files", "*.txt");
                        fc.getExtensionFilters().add(filter);
                        fc.setInitialDirectory(file);
                        fc.setTitle("Snimi kao 'skIgre'");
                        fc.setInitialFileName("SKGameIndex");
                        Stage stage = (Stage) pane.getScene().getWindow();
                        File file1 = fc.showSaveDialog(stage);
                        OutputStream outStream = new FileOutputStream(file1);
                        Writer streamWriter = new OutputStreamWriter(outStream, Charset.forName("utf-8").newEncoder());
                        try (BufferedWriter writer = new BufferedWriter(streamWriter)) {
                            String toJson = gson.toJson(games, type);
                            writer.write(toJson);
                        }

                    } catch (IOException ee) {
                        LOGGER.severe("nisam snimio fajl");
                        ee.getLocalizedMessage();
                    }
                }
        );

        loadLinks.setOnSucceeded(
                (e) -> {
                    Thread naslovi = new Thread(ispraviNaslove);
                    naslovi.setDaemon(true);
                    naslovi.start();
                }
        );
        links = new Thread(loadLinks);

        links.setDaemon(true);
        links.start();

        progressBar.progressProperty().bind(loadLinks.progressProperty());
        progressIndicator.progressProperty().bind(ispraviNaslove.progressProperty());
        label.textProperty().bind(ucitanihIgara);
        brojLinkova.textProperty().bind(brojIgara);
        iteracija.textProperty().bind(iter);
        ukupnoVr.textProperty().bind(ukVr);
        preostaloVr.textProperty().bind(preostaloVrijeme);
        protekloVrijeme.textProperty().bind(protekloVr);
        naslov.textProperty().bind(nasIsp);
        isprNaslova.textProperty().bind(ispravljeno);
    }

    private GameReview setGameReviewData(String link) {
        LOGGER.info("entering setData()");
        LOGGER.info("adding " + link);
        GameReview game = null;
        Element doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e1) {
            LOGGER.severe("ERROR in 'doc = Jsoup.connect(link).get();'");
        }

        Elements title = doc.select(".na");
        Elements score = doc.select(".oc");
        Elements author = doc.select(".pd");
        Elements date = doc.select("img[alt]");
        Elements platform = doc.select(".kz");

        List<Element> dateTemp = new ArrayList<>();
        date.forEach((e) -> {
            dateTemp.add(e);
        });
        String year = "";
        year = dateTemp.stream().filter((e) -> (e.attr("alt").matches("[0-9]+"))).map((e) -> e.attr("alt") + ".")
                .reduce(year, String::concat);

        if (score.text().matches("[0-9]+")) {
            if (platform.isEmpty() || !isPlatformHtmlElement(platform.first().text())
                    || isPCSpecsHtmlElement(platform.first().text())) {
                game = new GameReview(title.text(), author.text(), score.text(), year, link, "PC");
            } else {
                game = new GameReview(title.text(), author.text(), score.text(), year, link, platform.first().text());
            }
        } else {
            if (platform.isEmpty() || !isPlatformHtmlElement(platform.first().text())
                    || isPCSpecsHtmlElement(platform.first().text())) {
                game = new GameReview(title.text(), author.text(), year, link, "PC");
            } else {
                game = new GameReview(title.text(), author.text(), year, link, platform.first().text());
            }
        }
        LOGGER.info(game.getPlatform());
        return game;
    }

    private boolean isPlatformHtmlElement(String element) {
        String string = element.toLowerCase();
        boolean platform = (string.contains("pc") || string.contains("windows") || string.contains("xbox")
                || string.contains("ps3") || string.contains("ps2") || string.contains("ps4")
                || string.contains("playstation") || string.contains("ds") || string.contains("wii")
                || string.contains("nintendo") || string.contains("3ds") || string.contains("psvita")
                || string.contains("gameboy") || string.contains("sega") || string.contains("atari")
                || string.contains("gamecube") || string.contains("dreamcast") || string.contains("gage")
                || string.contains("nes") || string.contains("mac") || string.contains("linux"));
        return platform;
    }

    private boolean isPCSpecsHtmlElement(String element) {
        String string = element.toLowerCase();
        boolean specs = string.contains("ram");
        return specs;
    }
}
