package SK;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/*
 * 
 * @author 83deadpool
 * 
 */


public class SK {
    
    private Igra igra;
    static ArrayList<String> linkURLs = new ArrayList<>();
    static HashSet<Igra> igraLista = new HashSet<>();
    static String html;
    static StringProperty brojIgara = new SimpleStringProperty("0");
    static int j;
    static StringProperty brojLinkova = new SimpleStringProperty("0");
    static int count;
    static StringProperty progresMain = new SimpleStringProperty("0");
   
    
    /*-----------------------------------------
     * ucitavanje liste igrara iz fajla sa neta
     * Glavna metoda koja pokrece program
     * Ona je pozvana pri pokretanju aplikacije!!!
     * -------------------------------------------
     */
    public static HashSet<Igra> loadFromFileNet() {
        try {
            System.out.println("Pocinjem ucitavat linkove...");
            URL url = new URL("http://fairplay.hol.es/skIgre");
            ObjectInputStream in = new ObjectInputStream(url.openStream());
            igraLista = (HashSet<Igra>)in.readObject();
            System.out.println("Broj linkova: " + igraLista.size());
            return igraLista;
        } catch (MalformedURLException ex) {
            Logger.getLogger(SK.class.getName()).log(Level.SEVERE, null, ex);
            Platform.exit();
        } catch (IOException ex) {
            Logger.getLogger(SK.class.getName()).log(Level.SEVERE, null, ex);
            Platform.exit();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SK.class.getName()).log(Level.SEVERE, null, ex);
            Platform.exit();
        }
        return null;
        }
    
    
    /*
     * metoda pretrage koja ce biti pozivana u pretrazi u kontroler klasi
     * ne poziva se zasebno
     */
    public ArrayList<Igra> pretragaKomplet(HashSet<Igra> iLista, String n, String aut, int o, int g1, int g2) throws FileNotFoundException, IOException{
        
        ArrayList<Igra> a = new ArrayList<>(iLista);
        Collections.sort(a, (Igra o1, Igra o2) -> o2.link.compareTo(o1.link));
        ArrayList<Igra> rezultat = new ArrayList<>();

        a.stream().filter((i) -> ((i.getNaslov().toLowerCase().contains(n.toLowerCase())) && (i.getAutor().toLowerCase()
                .contains(aut.toLowerCase())) && (i.getOcjena() >= o) && (i.getGod() >= g1) && (i.getGod() <= g2)))
                .forEachOrdered((i) -> {rezultat.add(i);});
            html = this.writeToHTML(rezultat);
            return rezultat;
        }
    
     
    /*
     * pravljenje _html tabele iz liste igara koja ce onda biti ucitana u webview
     * ne poziva se zasebno
     */
    public String writeToHTML(ArrayList<Igra> a){
        StringBuilder _html = new StringBuilder();
        _html.append("<table>");
        _html.append("<tr><th>datum</th><th>naslov</th><th>autor</th><th>ocjena</th></tr>");
        a.forEach((i) -> {
            _html.append("<tr><td>").append(i.getGodina()).append("</td><td><a href=").append(i.link).append(">")
                    .append(i.getNaslov()).append("</a></td><td>").append(i.getAutor()).append("</td><td>")
                    .append(i.getOcjena()).append("</td></tr>");
        });
        _html.append("</table>");
        return _html.toString(); 
     }
    
}