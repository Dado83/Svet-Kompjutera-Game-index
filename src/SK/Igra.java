package SK;

import java.io.Serializable;


public class Igra implements Serializable {
	
    private static final long serialVersionUID = -3408039902845658861L;
    private String naslov;
    private String autor;
    private int ocjena;
    private String godina;
    String link;
    
    Igra(String n, String a, int o, String g, String l){
        naslov = n;
        autor = a;
        ocjena = o;
        godina = g;
        link = l;
    }
    
    Igra(String n, String a, String g, String l){
        super();
    }
    
    public void setTitle(String n){
        naslov = n;
    }
    
    public void setAuthor(String a){
        autor = a;
    }
    
    public void setScore(int o){
        ocjena = o;
    }
    
    public void setYear(String g){
        godina = g;
    }
    
    public String getNaslov(){
        if(naslov.isEmpty()){
            return link;
        }
        return naslov;
    }
    
    public String getAutor(){
        return autor;
    }
    
    public int getOcjena(){
        return ocjena;
    }
    
    public String getGodina(){
        return godina;
    }
    
    public int getGod(){
        int g = Integer.parseInt(getGodina().substring(3, 7));
        return g;
    }
    
    @Override
    public String toString(){
        return naslov + "::" + autor + "::" + ocjena + "::" + godina + "::" + link ;
    }

}
