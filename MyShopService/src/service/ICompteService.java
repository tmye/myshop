/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entites.Compte;

/**
 *
 * @author Christ
 */
public interface ICompteService {

    public void ajouter(Compte compte);

    public void modifier(Compte compte);

    public void supprimer(Compte compte);

    public Compte findById(Compte compte);
}
