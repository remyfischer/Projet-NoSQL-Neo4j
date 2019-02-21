import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.util.Random;


public class Main {

    public enum NodeType implements Label {

        Person, Product;

    }

    public enum RelationType implements RelationshipType{

        Knows, Buys;

    }


    public static void main(String[] args) {

        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
        GraphDatabaseService graphdB = (GraphDatabaseService) dbFactory.newEmbeddedDatabase(new File("C:\\Users\\remyf\\Desktop\\neo4j-community-3.5.3\\data\\databases\\graph.db"));

        String query =
                "MATCH (n )\n" +
                "OPTIONAL MATCH (n)-[r]-()\n" +
                "DELETE n,r";


        int i, j, k, finwhile, minRand, maxRand, rand;
        Random r = new Random();

        // VARIABLE D'ENVIRONNEMENT

        int nbPersonnes = 200000;
        int nbAmisMax = 15;
        int nbAmisMin = 7;

        int nbProduits = 80000;
        int nbProduitsMax = 5;
        int nbProduitsMin = 1;

        // FIN VARIABLE D'ENVIRONNEMENT

        String[] tabNomRandom = new String[10];
        String[] tabPrenomRandom = new String[10];
        String[] tabNom = new String[1000000];
        String[] tabPrenom = new String[1000000];
        int[] tabRelations = new int[100];
        int[] tabProduits = new int[5];

        tabNomRandom[0] = "Dupont";
        tabNomRandom[1] = "Durand";
        tabNomRandom[2] = "Morel";
        tabNomRandom[3] = "Muller";
        tabNomRandom[4] = "Faure";
        tabNomRandom[5] = "Guerin";
        tabNomRandom[6] = "Legrand";
        tabNomRandom[7] = "Lemaire";
        tabNomRandom[8] = "Barbier";
        tabNomRandom[9] = "Schmitt";

        tabPrenomRandom[0] = "Jean";
        tabPrenomRandom[1] = "Clara";
        tabPrenomRandom[2] = "Rémy";
        tabPrenomRandom[3] = "Julie";
        tabPrenomRandom[4] = "Hervé";
        tabPrenomRandom[5] = "Marie";
        tabPrenomRandom[6] = "Laurent";
        tabPrenomRandom[7] = "Mélanie";
        tabPrenomRandom[8] = "Paul";
        tabPrenomRandom[9] = "Cécile";

        // Initialisation du tableau des Noms, Prénoms et Id

        maxRand = 9;
        minRand = 0;

        for (i = 0 ; i < nbPersonnes ; i++){

            rand = minRand + r.nextInt(maxRand - minRand);
            tabNom[i] = tabNomRandom[rand];
            rand = minRand + r.nextInt(maxRand - minRand);
            tabPrenom[i] = tabPrenomRandom[rand];

        }


        try (Transaction tx = graphdB.beginTx()) {

            graphdB.execute(query);

            //MyNodes[] NodePerson = new MyNodes[nbPersonnes];
            Node[] NodePerson = new Node[nbPersonnes];
            Node[] NodeProduct = new Node[nbProduits];


            // Création des Nodes pour chaque Personnes

            for (i = 0 ; i < nbPersonnes ; i++){


               NodePerson[i] = graphdB.createNode(NodeType.Person);
               NodePerson[i].setProperty("PersonID", i);
               NodePerson[i].setProperty("Prenom", tabPrenom[i]);
               NodePerson[i].setProperty("Nom", tabNom[i]);


            }

            // Création des Relations entre chaque Personnes

            for (i = 0 ; i < nbPersonnes ; i++) {

                // Initialisation d'un tableau permettant d'éviter des doublons d'amitié

                for (j = 0; j < 100; j++) {

                    tabRelations[j] = -1;

                }

                // Génération aléatoire du nombre d'amis à affecter à une personne

                maxRand = nbAmisMax;
                minRand = nbAmisMin;

                rand = minRand + r.nextInt(maxRand - minRand);

                int boucle = rand;

                // Affectation des amis avec protection pour éviter d'être ami avec soit même et pour éviter les doublons

                for (j = 0; j < boucle; j++) {

                    maxRand = nbPersonnes - 1;
                    minRand = 0;
                    finwhile = 0;

                    do {

                        rand = minRand + r.nextInt(maxRand - minRand);

                        if (rand != i) {

                            finwhile = 1;

                        }

                        for (k = 0; k < 100; k++) {

                            if (tabRelations[k] == rand) {

                                finwhile = 0;

                            }

                        }

                        if (finwhile == 1) tabRelations[j] = rand;

                    } while (finwhile != 1);

                    NodePerson[i].createRelationshipTo(NodePerson[rand], RelationType.Knows);


                }


            }

            // Création de n Produits

            for (i = 0 ; i < nbProduits ; i++){

                NodeProduct[i] = graphdB.createNode(NodeType.Product);
                NodeProduct[i].setProperty("ProductID", i);

            }

            // Génération aléatoire de liens d'achats entre des personnes et des produits

            for (i = 0 ; i < nbPersonnes ; i++){

                // Initialisation d'un tableau évitant les doublons de produits

                for (j = 0 ; j < 5 ; j++){

                    tabProduits[j] = -1;

                }

                // Définition aléatoire du nombre de produits achetés par une personne

                maxRand = nbProduitsMax;
                minRand = nbProduitsMin;

                rand = minRand + r.nextInt(maxRand - minRand);

                int boucle = rand;

                // Affectation des achats avec protection pour éviter d'acheter le même produit

                for (j = 0 ; j < boucle ; j++){

                    maxRand = nbProduits-1;
                    minRand = 0;

                    do{

                        rand = minRand + r.nextInt(maxRand - minRand);
                        finwhile = 1;

                        for (k = 0 ; k < 5 ; k++){

                            if (tabProduits[k] == rand){

                                finwhile = 0;

                            }

                        }

                        if (finwhile == 1) tabRelations[j] = rand;

                    } while (finwhile != 1);

                    NodePerson[i].createRelationshipTo(NodeProduct[rand], RelationType.Buys);

                }

            }

            tx.success();



        }
        graphdB.shutdown();

    }



}
