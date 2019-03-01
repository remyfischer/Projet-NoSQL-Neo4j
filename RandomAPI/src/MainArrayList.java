import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;


public class MainArrayList {

    public enum NodeType implements Label {

        Person, Product;

    }

    public enum RelationType implements RelationshipType{

        Knows, Buys;

    }


    public static void main(String[] args) {

        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
        GraphDatabaseService graphdB = (GraphDatabaseService) dbFactory.newEmbeddedDatabase(new File("C:\\Users\\remyf\\Desktop\\neo4j-community-3.5.3\\data\\databases\\graph.db"));

        // Commande CYPHER permettant de réinitialiser la base de données

        String query =
                "MATCH (n )\n" +
                        "OPTIONAL MATCH (n)-[r]-()\n" +
                        "DELETE n,r";


        int i, j, k, finwhile, minRand, maxRand, rand;
        Random r = new Random();

        // VARIABLE D'ENVIRONNEMENT

        int nbPersonnes = 500000;
        int nbAmisMax = 20;
        int nbAmisMin = 10;

        int nbProduits = 500000;
        int nbProduitsMax = 5;
        int nbProduitsMin = 0;

        // FIN VARIABLE D'ENVIRONNEMENT

        ArrayList<String> tabNomRandom = new ArrayList<>();
        ArrayList<String> tabPrenomRandom = new ArrayList<>();
        ArrayList<String> tabNom = new ArrayList<>();
        ArrayList<String> tabPrenom = new ArrayList<>();
        int[] tabRelations = new int[nbAmisMax];
        int[] tabProduits = new int[nbProduitsMax];
        // ArrayList<Integer> tabRelations = new ArrayList<>();
        //ArrayList<Integer> tabProduits = new ArrayList<>();

        tabNomRandom.add(0, "Dupont");
        tabNomRandom.add(1, "Durand");
        tabNomRandom.add(2, "Morel");
        tabNomRandom.add(3,"Muller");
        tabNomRandom.add(4, "Faure");
        tabNomRandom.add(5, "Guerin");
        tabNomRandom.add(6, "Legrand");
        tabNomRandom.add(7, "Lemaire");
        tabNomRandom.add(8, "Barbier");
        tabNomRandom.add(9, "Schmitt");

        tabPrenomRandom.add(0, "Jean");
        tabPrenomRandom.add(1, "Clara");
        tabPrenomRandom.add(2, "Rémy");
        tabPrenomRandom.add(3, "Julie");
        tabPrenomRandom.add(4, "Hervé");
        tabPrenomRandom.add(5, "Marie");
        tabPrenomRandom.add(6, "Laurent");
        tabPrenomRandom.add(7, "Mélanie");
        tabPrenomRandom.add(8, "Paul");
        tabPrenomRandom.add(9, "Cécile");


        // Initialisation du tableau des Noms, Prénoms et Id

        maxRand = 9;
        minRand = 0;

        for (i = 0 ; i < nbPersonnes ; i++){

            rand = minRand + r.nextInt(maxRand - minRand);
            tabNom.add(i, tabNomRandom.get(rand));
            rand = minRand + r.nextInt(maxRand - minRand);
            tabPrenom.add(i, tabPrenomRandom.get(rand));

        }


        try (Transaction tx = graphdB.beginTx()) {

            graphdB.execute(query);

            ArrayList<Node> NodePerson = new ArrayList<>();
            ArrayList<Node> NodeProduct = new ArrayList<>();

            // Création des Nodes pour chaque Personnes

            System.out.println("Création personnes");

            for (i = 0 ; i < nbPersonnes ; i++){

                Node nodePerson = graphdB.createNode(NodeType.Person);
                nodePerson.setProperty("PersoID", i);
                nodePerson.setProperty("Prenom", tabPrenom.get(i));
                nodePerson.setProperty("nom", tabNom.get(i));
                NodePerson.add(nodePerson);

            }

            // Création des Relations entre chaque Personnes

            System.out.println("Création relations entre les personnes");

            for (i = 0 ; i < nbPersonnes ; i++) {


                // Initialisation d'un tableau permettant d'éviter des doublons d'amitié

                for (j = 0; j < nbAmisMax; j++) {

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

                    rand = minRand + r.nextInt(maxRand - minRand);


                    do {

                        rand = minRand + r.nextInt(maxRand - minRand);

                        if (rand != i) {

                            finwhile = 1;

                        }

                        for (k = 0; k < boucle; k++) {

                            if(tabRelations[k] == rand){

                                finwhile = 0;

                            }

                        }

                        if (finwhile == 1) tabRelations[j] = rand;;

                    } while (finwhile != 1);

                    NodePerson.get(i).createRelationshipTo(NodePerson.get(rand), RelationType.Knows);



                }


            }

            System.out.println("Relations entre les personnes crées");

            // Création de n Produits

            System.out.println("Création produits");

            for (i = 0 ; i < nbProduits ; i++) {

                Node nodeProduct = graphdB.createNode(NodeType.Product);
                nodeProduct.setProperty("ProductID", i);
                NodeProduct.add(nodeProduct);

            }

            // Génération aléatoire de liens d'achats entre des personnes et des produits

            System.out.println("Création relations entre les personnes et les produits");

            for (i = 0 ; i < nbPersonnes ; i++){



                // Initialisation d'un tableau évitant les doublons de produits

                for (j = 0 ; j < nbProduitsMax ; j++){

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

                        for (k = 0 ; k < boucle ; k++){

                            if (tabProduits[k] == rand){

                                finwhile = 0;

                            }

                        }

                        if (finwhile == 1) tabRelations[j] = rand;

                    } while (finwhile != 1);

                    NodePerson.get(i).createRelationshipTo(NodeProduct.get(rand), RelationType.Buys);

                }

            }

            System.out.println("Relations personnes-produits créées");

            tx.success();



        }
        graphdB.shutdown();

    }



}
