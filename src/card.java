public class card {
    public String UE;// =Número de la carte au trio normal
    public int liason;//mode picante
    public boolean faceup;
    public int ou; //ou la carte est: 0=table
    //1,2,3.. = joueur 1,2,3,... (ID JOUEUR)
    //-1, -2, -3 = trio completé par un joueur -(ID JOUEUR) 

    public card(String UE, int l, boolean f, int ou){
        this.UE = UE;
        this.liason = l;
        this.faceup = f;
        this.ou = ou;
    }
    public boolean flip(){
        this.faceup = !this.faceup;
        return(this.faceup);
    }


}
