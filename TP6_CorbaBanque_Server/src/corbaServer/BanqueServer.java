package corbaServer;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import service.BanqueImpl;

public class BanqueServer {

    public static void main(String[] args) {
        try {
            // a. Initialiser l'ORB
            ORB orb = ORB.init(args, null);

            // b. Récupérer le RootPOA et activer le POAManager
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            // c. Configurer l'annuaire JNDI (lit jndi.properties)
            Context ctx = new InitialContext();

            // d. Créer le servant
            BanqueImpl banque = new BanqueImpl();

            // e. Enregistrer sa référence dans l'annuaire
            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(banque);
            ctx.rebind("BK", ref);

            System.out.println("Serveur Banque prêt, en attente des clients...");

            // f. Mettre l'ORB en attente des requêtes
            orb.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
