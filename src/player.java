import java.util.ArrayList;
import java.util.List;

public class player{
    public int id;
    public String name;
    public int team;
    List<card> main = new ArrayList<>();//La liste doivent ẽtre trié par    
    List<card> temp = new ArrayList<>();//main de cartes temporaires  (pendant son propre tour)
    List<Integer> trio = new ArrayList<>();//stockage de trios completés    
   


    public player(int id, String n, int s, int t){
        this.id=id;
        this.name = n;
        this.team = t;
    }
    public void distribuerMain(List<card> m){
        this.main = m;

    }
    public void supprimerMain(card c){
        this.main.remove(c);

    }
    public card cardDemande(boolean plusGrand){
        if(plusGrand){ 
            return(this.main.get(this.main.size()-1));
            }
        else{
            return(this.main.get(0));
        }
    }
    public card devoile(player joueurCible, boolean plusGrand){//devoiler de la main d'un joueur
        //System.out.println(joueurCible.name);
        card cardChoisi = joueurCible.cardDemande(plusGrand);
        this.temp.add(cardChoisi);
        return cardChoisi;
        
    }
    public card devoile(card carte){//devoler de la table
        //System.out.println(joueurCible.name);
        card cardChoisi = table.cardDemande(carte);
        this.temp.add(cardChoisi);

        return cardChoisi;
        
    }
    public void ajoutTemp(card c){
        this.temp.add(c);
        System.out.println(temp);
    }
    public void finTour(){
        this.temp.clear();  
      }
    public void trioCheck(){
        int[] trioqt = {0,0,0,0,0,0,0,0,0,0,0,0};
        for(int i=0;i<this.temp.size();i++){
            int intUE = Integer.parseInt(this.temp.get(i).UE);
            trioqt[intUE]++;
            if(trioqt[intUE] == 3){
                System.out.println("Trio atteint");
                    trio.add(intUE);
                finTour();
                
            }
        }
    }
    public void addPoint(){}
}