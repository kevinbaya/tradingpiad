package arbitrage;

import java.util.ArrayList;
import java.util.List;

import market.Currency;
import market.Market;

import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import strategies.PriceManager;
import utilities.Util;

/**
 *
 * Permet de trouver la serie d'echange la plus profitable partant d'une monnaie
 * donnee et revenant a cette monnaie.
 * Son implementation consiste en un graphe ou chaque noeud represente une monnaie
 * et chaque arc un echange. Une monnaie a plusieurs noeud pour chaque temps t.
 * Un noeud (monnaie X, t) symbolise "au temps t, l'argent du trader investi est en monnaie X".
 * Ainsi un chemin partant du sommet (monnaie de depart, 0) et arrivant au sommet
 * (monnaie de de depart, D) represente une serie de D echanges revenant � la monnaie de depart.
 *  
 */
public class BestMatchingDeals {


	/**
	 * Ensemble des monnaies sur lesquelles la recherche de la meilleure serie d'echange est effectuee
	 */
	public final ArrayList<Currency> listeMonnaies;
	public CustomVertex sommetDepart,sommetArrivee;

	/**
	 * Graphe representant les differents echanges qu'on peut faire.
	 */
	public SimpleDirectedWeightedGraph<CustomVertex, CustomEdge> graphe;


	/**
	 * @param markets La liste des marches sur lesquels peut operer le trader
	 * @param monnaieDepart La monaie qu'on utilise pour le premier echange et avec laquel on souhaite se retrouver pour
	 */
	public BestMatchingDeals(Market[] markets,Currency monnaieDepart,PriceManager priceManager){
		listeMonnaies = Util.getCurrencyList(markets);
		initGrapheTauxDeChange(markets,monnaieDepart,priceManager);

		//System.out.println("***************************G R A P H E*******************************");
		//System.out.println(graphe);


	}


	/**
	 * @return La liste des echanges optimaux
	 */
	public List<CustomEdge> serieEchangesOptimal(){
		 //List<CustomEdge> shortestPath= DijkstraShortestPath.findPathBetween(graphe,sommetDepart,sommetArrivee);
		List<CustomEdge> shortestPath= BellmanFordShortestPath.findPathBetween(graphe,sommetDepart,sommetArrivee);
		 shortestPath.remove(shortestPath.size()-1);
		 return shortestPath;

	}

	public void initGrapheTauxDeChange(
			Market[] markets,
			Currency monnaieDepart,
			PriceManager priceManager
			)
		{

		// Get the list of all the currency used by the trader agent
		ArrayList<Currency> listCurrencies = Util.getCurrencyList(markets);

		SimpleDirectedWeightedGraph<CustomVertex, CustomEdge> g =new SimpleDirectedWeightedGraph<CustomVertex, CustomEdge>(CustomEdge.class);

		sommetDepart = new CustomVertex(monnaieDepart,0); // Le sommet de depart (au temps t=0)
		sommetArrivee= new CustomVertex(monnaieDepart,listeMonnaies.size()+1);
		g.addVertex(sommetArrivee);
		g.addVertex(sommetDepart);



		CustomVertex sommetArrivee_2 = new CustomVertex(monnaieDepart,2);
		g.addVertex(sommetArrivee_2);
		g.addEdge(sommetArrivee_2, sommetArrivee, new CustomEdge(null, false,priceManager));

		/* Ajout des sommets du temps t=1 et les arretes les reliant au sommet de depart */
		for (Market m : markets) {

			if (m.cur1.equals(monnaieDepart)) {

				//On connecte au sommet de d�part les sommets t=1
				CustomVertex suivantDepart = new CustomVertex(m.cur2, 1);

				g.addVertex(suivantDepart);

				CustomEdge arrete=new CustomEdge(m,false,priceManager);
				g.addEdge(sommetDepart, suivantDepart, arrete);

				// On connecte aux sommets t=1 au sommet d'arrivee t=1 (sauf le sommet de depart)
				CustomEdge arrete2=new CustomEdge(m,true,priceManager);
				g.addEdge(suivantDepart, sommetArrivee_2, arrete2);
			} else if (m.cur2.equals(monnaieDepart)) {

				//On connecte au sommet de d�part aux sommets t=1
				CustomVertex suivantDepart = new CustomVertex(m.cur1, 1);
				g.addVertex(suivantDepart);

				CustomEdge arrete=new CustomEdge(m,true,priceManager);
				g.addEdge(sommetDepart, suivantDepart, arrete);

				// On connecte la couche t=1 au sommet d'arrivee 1
				CustomEdge arrete2=new CustomEdge(m,false,priceManager);
				g.addEdge(suivantDepart, sommetArrivee_2, arrete2);
			}

		}



			for (int t = 2; t < listCurrencies.size(); t++) {
				for(Market m:markets){
					if(m.cur1 !=monnaieDepart && m.cur2 !=monnaieDepart){

							//cur1 -> cur2
							CustomVertex vertexSource= new CustomVertex(m.cur1, t-1);
							CustomVertex vertexCible = new CustomVertex(m.cur2, t);
							g.addVertex(vertexCible);
							CustomEdge arrete=new CustomEdge(m,false,priceManager);
							g.addEdge(vertexSource, vertexCible, arrete);


							// cur2 -> cur1
							CustomVertex vertexSource2= new CustomVertex(m.cur2, t-1);
							CustomVertex vertexCible2 = new CustomVertex(m.cur1, t);
							g.addVertex(vertexCible2);
							CustomEdge arrete2=new CustomEdge(m,true,priceManager);
							g.addEdge(vertexSource2, vertexCible2, arrete2);



					}
				}


				CustomVertex sommetArrivee_t = new CustomVertex(monnaieDepart,t+1);
				g.addVertex(sommetArrivee_t);

				for (Market m : markets) {

					if (m.cur1.equals(monnaieDepart)) {
						CustomVertex precedentArrivee_t = new CustomVertex(m.cur2, t);

						CustomEdge arrete=new CustomEdge(m,true,priceManager);
						g.addEdge(precedentArrivee_t, sommetArrivee_t, arrete);

					} else if (m.cur2.equals(monnaieDepart)) {
						CustomVertex precedentArrivee_t = new CustomVertex(m.cur1, t);

						CustomEdge arrete=new CustomEdge(m,false,priceManager);
						g.addEdge(precedentArrivee_t, sommetArrivee_t, arrete);
					}

				}

				g.addEdge(sommetArrivee_t, sommetArrivee, new CustomEdge(null, false,priceManager));
			}

		this.graphe= g;

	}


}