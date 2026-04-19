package com.school.gestion.model;

public class Classe {
    private int idClasse;
    private String nom;
    private String niveau;
    private int capacite;
    private String nomComplet;
    private String matriculeEnseignant;
    private int effectifActuel;

    public Classe() {}

    public Classe(String nom, String niveau, int capacite) {
        this.nom = nom;
        this.niveau = niveau;
        this.capacite = capacite;
        this.nomComplet = niveau + " - " + nom;
    }

    public int getIdClasse() { return idClasse; }
    public void setIdClasse(int idClasse) { this.idClasse = idClasse; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public String getNomComplet() { return nomComplet != null ? nomComplet : niveau + " - " + nom; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }
    public String getMatriculeEnseignant() { return matriculeEnseignant; }
    public void setMatriculeEnseignant(String matriculeEnseignant) { this.matriculeEnseignant = matriculeEnseignant; }
    public int getEffectifActuel() { return effectifActuel; }
    public void setEffectifActuel(int effectifActuel) { this.effectifActuel = effectifActuel; }

    public double getRemplissage() { return (double) effectifActuel / capacite; }
    public boolean isFull() { return effectifActuel >= capacite; }
}
