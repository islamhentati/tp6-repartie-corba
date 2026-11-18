package corbaClient;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;

import corbaBanque.Compte;
import corbaBanque.IBanqueRemote;
import corbaBanque.IBanqueRemoteHelper;

public class BanqueClient {

    public static void main(String[] args) {
        try {
            ORB orb = ORB.init(args, null);

            // a. Propriétés de l'annuaire JNDI (jndi.properties)
            Properties env = new Properties();
            env.put("java.naming.corba.orb", orb);
            Context ctx = new InitialContext(env);

            // b. Référence de l'objet distant
            org.omg.CORBA.Object ref = (org.omg.CORBA.Object) ctx.lookup("BK");

            // c. narrow -> création du Stub
            IBanqueRemote banque = IBanqueRemoteHelper.narrow(ref);

            // d. Test de chaque méthode
            System.out.println("=== 1. Création de comptes ===");
            banque.creerCompte(new Compte(1, 1000f));
            banque.creerCompte(new Compte(2, 500f));
            banque.creerCompte(new Compte(3, 250.5f));
            System.out.println("3 comptes créés.");

            System.out.println("\n=== 2. Versement de 200 sur le compte 1 ===");
            banque.verser(200f, 1);
            afficher(banque.getCompte(1));

            System.out.println("\n=== 3. Retrait de 100 du compte 2 ===");
            banque.retirer(100f, 2);
            afficher(banque.getCompte(2));

            System.out.println("\n=== 4. Consultation du compte 3 ===");
            afficher(banque.getCompte(3));

            System.out.println("\n=== 5. Liste de tous les comptes ===");
            for (Compte c : banque.getComptes()) {
                afficher(c);
            }

            System.out.println("\n=== 6. Conversion Euro -> Dinar Tunisien ===");
            float montant = 100f;
            System.out.println(montant + " EUR = " + banque.conversion(montant) + " DT");

            System.out.println("\n=== 7. Gestion des erreurs ===");
            try {
                banque.retirer(99999f, 1);
            } catch (SystemException e) {
                System.out.println("Retrait refusé : " + e.getMessage());
            }
            try {
                banque.getCompte(404);
            } catch (SystemException e) {
                System.out.println("Consultation refusée : " + e.getMessage());
            }

        } catch (NamingException e) {
            System.err.println("Erreur JNDI (tnameserv et BanqueServer sont-ils lancés ?) : " + e);
        } catch (Exception e) {
            System.err.println("Erreur client : " + e);
            e.printStackTrace();
        }
    }

    private static void afficher(Compte c) {
        System.out.println("Compte n°" + c.code + " | solde = " + c.solde);
    }
}
