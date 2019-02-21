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

        int nbPersonnes = 150000;
        int nbAmisMax = 15;
        int nbAmisMin = 7;

        int nbProduits = 80000;
        int nbProduitsMax = 5;
        int nbProduitsMin = 1;

        // FIN VARIABLE D'ENVIRONNEMENT

        ArrayList<String> tabNomRandom = new ArrayList<>();
        ArrayList<String> tabPrenomRandom = new ArrayList<>();
        ArrayList<String> tabNom = new ArrayList<>();
        ArrayList<String> tabPrenom = new ArrayList<>();
        ArrayList<Integer> tabRelations = new ArrayList<>();
        ArrayList<Integer> tabProduits = new ArrayList<>();

        tabNomRandom.add("Dupont");
        tabNomRandom.add("Durand");
        tabNomRandom.add("Morel");
        tabNomRandom.add("Muller");
        tabNomRandom.add("Faure");
        tabNomRandom.add("Guerin");
        tabNomRandom.add("Legrand");
        tabNomRandom.add("Lemaire");
        tabNomRandom.add("Barbier");
        tabNomRandom.add("Schmitt");

        tabPrenomRandom.add("Jean");
        tabPrenomRandom.add("Clara");
        tabPrenomRandom.add("Rémy");
        tabPrenomRandom.add("Julie");
        tabPrenomRandom.add("Hervé");
        tabPrenomRandom.add("Marie");
        tabPrenomRandom.add("Laurent");
        tabPrenomRandom.add("Mélanie");
        tabPrenomRandom.add("Paul");
        tabPrenomRandom.add("Cécile");


        // Initialisation du tableau des Noms, Prénoms et Id

        maxRand = 9;
        minRand = 0;

        for (i = 0 ; i < nbPersonnes ; i++){

            rand = minRand + r.nextInt(maxRand - minRand);
            tabNom.add(i, tabNomRandom.get(rand));
            rand = minRand + r.nextInt(maxRand - minRand);
            tabPrenom.add(i, tabPrenom.get(rand));

        }


        try (Transaction tx = graphdB.beginTx()) {

            graphdB.execute(query);

            ArrayList<Node> NodePerson = new ArrayList<>();
            ArrayList<Node> NodeProduct = new ArrayList<>();

            // Création des Nodes pour chaque Personnes

            for (i = 0 ; i < nbPersonnes ; i++){

                Node nodePerson = graphdB.createNode(NodeType.Person);
                nodePerson.setProperty("PersoID", i);
                nodePerson.setProperty("Prenom", tabPrenom.get(i));
                nodePerson.setProperty("nom", tabNom.get(i));
                NodePerson.add(nodePerson);

            }

            // Création des Relations entre chaque Personnes

            for (i = 0 ; i < nbPersonnes ; i++) {

                // Initialisation d'un tableau permettant d'éviter des doublons d'amitié

                for (j = 0; j < nbAmisMax; j++) {

                    tabRelations.set(j, -1);

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

                        for (k = 0; k < boucle; k++) {

                            if(tabRelations.get(k) == rand){

                                finwhile = 0;

                            }

                        }

                        if (finwhile == 1) tabRelations.add(j, rand);

                    } while (finwhile != 1);

                    NodePerson.get(i).createRelationshipTo(NodePerson.get(rand), RelationType.Knows);

                }


            }

            // Création de n Produits

            for (i = 0 ; i < nbProduits ; i++) {

                Node nodeProduct = graphdB.createNode(NodeType.Product);
                nodeProduct.setProperty("ProductID", i);
                NodeProduct.add(nodeProduct);

            }

            // Génération aléatoire de liens d'achats entre des personnes et des produits

            for (i = 0 ; i < nbPersonnes ; i++){

                // Initialisation d'un tableau évitant les doublons de produits

                for (j = 0 ; j < nbProduitsMax ; j++){

                    tabProduits.set(j, -1);

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

                            if (tabProduits.get(k) == rand){

                                finwhile = 0;

                            }

                        }

                        if (finwhile == 1) tabRelations.add(j, rand);

                    } while (finwhile != 1);

                    NodePerson.get(i).createRelationshipTo(NodeProduct.get(rand), RelationType.Buys);

                }

            }

            tx.success();



        }
        graphdB.shutdown();

    }



}
