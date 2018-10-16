package updatereview;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import gamereview.GameReview;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.commons.net.ftp.FTPClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class UpdateController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label loadedIndexes;
    @FXML
    private Label indexSize;
    @FXML
    private Label avgIterTime;
    @FXML
    private Label timeNeededToUpdate;
    @FXML
    private Label timeRemaining;
    @FXML
    private Label correctedTitles;
    @FXML
    private Label timeElapsed;
    @FXML
    private Label title;
    @FXML
    private Button update;

    StringProperty iteration = new SimpleStringProperty();
    StringProperty totalTime = new SimpleStringProperty();
    StringProperty remainingTime = new SimpleStringProperty();
    StringProperty elapsedT = new SimpleStringProperty();
    StringProperty correctedTitle = new SimpleStringProperty();
    StringProperty corrected = new SimpleStringProperty();
    StringProperty numberOfGames = new SimpleStringProperty();
    StringProperty loadedGames = new SimpleStringProperty();

    private static final Logger LOGGER = Logger.getLogger(UpdateController.class.getName());

    private int step;
    private int correct;
    private long elapsedTime;

    private List<String> linkURLs = new ArrayList<>();
    private Set<GameReview> games = new HashSet<>();
    private List<Double> avgSingleIndexLoadTime = new ArrayList<>();

    Thread updateThread;
    Task loadLinksTask;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("entering loadLinksURLs()");
        LOGGER.info("Povezujem se na SK\n...ucitavam linkove...");
        Element element = null;

        try {
            element = Jsoup.connect("http://www.sk.rs/indexes/sections/op.html").get();
        } catch (IOException e1) {
            LOGGER.severe("ERROR in jsoup connect to SK");
        }
        Elements elements = element.select("a[href]");

        elements.forEach((e) -> {
            linkURLs.add("http://www.sk.rs" + e.attr("href").substring(5));
        });
        LOGGER.info("Broj linkova prije filtera: " + linkURLs.size());

        List<String> tempList = linkURLs.stream().filter(ss -> !ss.contains("indexe")).collect(Collectors.toList());
        linkURLs = new ArrayList<>(tempList);
        LOGGER.info("Broj linkova poslije filtera: " + linkURLs.size());
    }

    @FXML
    public void saveToFile() throws IOException, InterruptedException {
        update.setDisable(true);

        loadLinksTask = new Task() {

            @Override
            protected Object call() throws Exception {
                long startTime = System.nanoTime();

                int size = linkURLs.size();
                numberOfGames.set(size + "");

                for (int num = 0; num < linkURLs.size(); num++) {
                    long startOfIteration = System.nanoTime();
                    step = num;

                    games.add(setGameReviewData(linkURLs.get(num)));
                    LOGGER.info("Ucitanih igara: " + games.size() + "/" + linkURLs.size());

                    double result = System.nanoTime() - startOfIteration;
                    LOGGER.info("Vreme potrebno za 1 iteraciju: " + result / 1000000000 + " sekundi.");

                    int timeNeededToComplete = (int) (((result * size) / 1000000000) / 60);
                    LOGGER.info("Potrebno vreme za ucitavanje svih linkova: " + timeNeededToComplete + " minuta.");

                    int timeLefttoComplete = (int) (((result * (size - step)) / 1000000000) / 60);
                    LOGGER.info("Potrebno vreme za ucitavanje ostatka linkova: " + timeLefttoComplete + " minuta.");

                    elapsedTime = (startOfIteration - startTime) / 1000000000 / 60;
                    LOGGER.info("Proteklo vrijeme ucitavanja linkova... " + elapsedTime);

                    avgSingleIndexLoadTime.add(result / 1000000000);

                    updateProgress(step, size);

                    if ((step % 10) == 0) {
                        Platform.runLater(() -> {

                            iteration.set("" + result);
                            totalTime.set("" + timeNeededToComplete);
                            remainingTime.set("" + timeLefttoComplete);
                            elapsedT.set("" + elapsedTime);

                        });

                    } else {
                        Platform.runLater(() -> {
                            loadedGames.set("" + step);
                        });

                    }
                    step++;
                }
                return null;
            }
        };

        Task correctTitleTask = new Task() {
            @Override
            protected Object call() throws Exception {
                step = 1;
                correct = 0;
                int size = games.size();

                for (GameReview i : games) {
                    if (i.getTitle().contains("http") || i.getTitle().equals("")) {
                        Element element = null;
                        try {
                            element = Jsoup.connect(i.getLink()).get();
                        } catch (IOException e1) {
                            LOGGER.severe("error connecting to game link using jsoup");
                        }
                        Elements date = element.select("img[alt]");
                        List<Element> titleAttribute = new ArrayList<>();
                        date.forEach((e) -> {
                            titleAttribute.add(e);
                        });
                        if (titleAttribute.size() < 10) {
                        } else {
                            i.setTitle(titleAttribute.get(9).attr("alt"));
                        }
                        LOGGER.info(i.getTitle() + " ...ISPRAVLJEN");
                        correct++;
                    }

                    Platform.runLater(() -> {
                        correctedTitle.set(i.getTitle() + " ...ISPRAVLJEN");
                        corrected.set("" + correct);
                    });

                    updateProgress(step, size);
                    step++;
                }
                return null;
            }
        };

        correctTitleTask.setOnSucceeded(
                (e) -> {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<Set<GameReview>>() {
                        }.getType();
                        FileChooser fc = new FileChooser();
                        String desktop = System.getProperty("user.home") + "/desktop";
                        File file = new File(desktop);
                        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("JSON files", "*.json");
                        fc.getExtensionFilters().add(filter);
                        fc.setInitialDirectory(file);
                        fc.setTitle("Snimi...");
                        fc.setInitialFileName("SKGameIndex");
                        Stage stage = (Stage) root.getScene().getWindow();
                        File saveFile = fc.showSaveDialog(stage);
                        OutputStream outStream = new FileOutputStream(saveFile);
                        Writer streamWriter = new OutputStreamWriter(outStream, Charset.forName("utf-8").newEncoder());
                        try (BufferedWriter writer = new BufferedWriter(streamWriter)) {
                            String toJson = gson.toJson(games, type);
                            writer.write(toJson);
                        }
                        File uploadFile = fc.showOpenDialog(stage);
                        FTPClient ftp = new FTPClient();
                        InputStream inputStream = new FileInputStream(uploadFile);
                        ftp.connect("files9.hostinger.in");

                        String[] loggonData = new String[2];
                        BufferedReader reader = new BufferedReader(new FileReader(new File("C:/ftp.txt")));
                        String line;
                        int i = 0;
                        while ((line = reader.readLine()) != null) {
                            loggonData[i] = line;
                            i++;
                        }

                        ftp.login(loggonData[0], loggonData[1]);
                        ftp.storeFile("SKGameIndex.json", inputStream);

                        inputStream.close();
                        if (ftp.isConnected()) {
                            ftp.logout();
                            ftp.disconnect();
                        }
                    } catch (IOException ee) {
                        LOGGER.severe("nisam snimio fajl");
                        ee.getLocalizedMessage();
                    }
                }
        );

        loadLinksTask.setOnSucceeded(
                (e) -> {
                    Thread titleThread = new Thread(correctTitleTask);
                    titleThread.setDaemon(true);
                    titleThread.start();
                }
        );

        updateThread = new Thread(loadLinksTask);
        updateThread.setDaemon(true);
        updateThread.start();

        progressBar.progressProperty().bind(loadLinksTask.progressProperty());
        progressIndicator.progressProperty().bind(correctTitleTask.progressProperty());
        loadedIndexes.textProperty().bind(loadedGames);
        indexSize.textProperty().bind(numberOfGames);
        avgIterTime.textProperty().bind(iteration);
        timeNeededToUpdate.textProperty().bind(totalTime);
        timeRemaining.textProperty().bind(remainingTime);
        timeElapsed.textProperty().bind(elapsedT);
        title.textProperty().bind(correctedTitle);
        correctedTitles.textProperty().bind(corrected);
    }

    private GameReview setGameReviewData(String link) {
        LOGGER.info("entering setData()");
        LOGGER.info("adding " + link);
        GameReview game = null;
        Element element = null;
        try {
            element = Jsoup.connect(link).get();
        } catch (IOException e1) {
            LOGGER.severe("ERROR in 'doc = Jsoup.connect(link).get();'");
        }

        Elements title = element.select(".na");
        Elements score = element.select(".oc");
        Elements author = element.select(".pd");
        Elements date = element.select("img[alt]");
        Elements platform = element.select(".kz");

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
