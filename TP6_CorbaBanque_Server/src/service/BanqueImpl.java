package service;

import java.util.Map;
import java.util.TreeMap;

import org.omg.CORBA.BAD_PARAM;

import corbaBanque.Compte;
import corbaBanque.IBanqueRemotePOA;

public class BanqueImpl extends IBanqueRemotePOA {

    private static final double TAUX_EUR_DT = 3.4;

    private final Map<Integer, Compte> comptes = new TreeMap<Integer, Compte>();

    @Override
    public synchronized void creerCompte(Compte cpte) {
        if (comptes.containsKey(cpte.code)) {
            throw new BAD_PARAM("Le compte " + cpte.code + " existe déjà");
        }
        if (cpte.solde < 0) {
            throw new BAD_PARAM("Le solde initial doit être positif");
        }
        comptes.put(cpte.code, new Compte(cpte.code, cpte.solde));
        System.out.println("[Serveur] Compte créé : " + cpte.code + " (solde = " + cpte.solde + ")");
    }

    @Override
    public synchronized void verser(float mt, int code) {
        if (mt <= 0) {
            throw new BAD_PARAM("Le montant à verser doit être positif");
        }
        Compte c = chercher(code);
        c.solde += mt;
        System.out.println("[Serveur] Versement de " + mt + " sur le compte " + code);
    }

    @Override
    public synchronized void retirer(float mt, int code) {
        if (mt <= 0) {
            throw new BAD_PARAM("Le montant à retirer doit être positif");
        }
        Compte c = chercher(code);
        if (c.solde < mt) {
            throw new BAD_PARAM("Solde insuffisant sur le compte " + code);
        }
        c.solde -= mt;
        System.out.println("[Serveur] Retrait de " + mt + " du compte " + code);
    }

    @Override
    public synchronized Compte getCompte(int code) {
        Compte c = chercher(code);
        return new Compte(c.code, c.solde);
    }

    @Override
    public synchronized Compte[] getComptes() {
        Compte[] tab = new Compte[comptes.size()];
        int i = 0;
        for (Compte c : comptes.values()) {
            tab[i++] = new Compte(c.code, c.solde);
        }
        return tab;
    }

    @Override
    public double conversion(float mt) {
        return mt * TAUX_EUR_DT;
    }

    private Compte chercher(int code) {
        Compte c = comptes.get(code);
        if (c == null) {
            throw new BAD_PARAM("Compte " + code + " introuvable");
        }
        return c;
    }
}