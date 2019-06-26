package br.com.senac.heroapp.modelo;

public class HeroApp {
    private int Id;
    private String hero;
    private String classe;
    private String raking;

    public HeroApp(int id, String hero, String classe, String raking) {
        Id = id;
        this.hero = hero;
        this.classe = classe;
        this.raking = raking;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getHero() {
        return hero;
    }

    public void setHero(String hero) {
        this.hero = hero;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getRaking() {
        return raking;
    }

    public void setRaking(String raking) {
        this.raking = raking;
    }
}
