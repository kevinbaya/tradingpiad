package utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import market.Currency;
import market.ExchangeError;
import market.Market;
import market.Trade;
import market.Trades;

/**
 * Quelques fonctions utilitaires en vrac
 *
 */
public class Util {

	/**
	 * Inverser un tableau
	 * @param tab Un tableau
	 */
	public static <E> void reverse(E[] tab) {
		for (int i = 0; i < tab.length / 2; i++) {
			E tmp = tab[i];
			tab[i] = tab[tab.length - 1 - i];
			tab[tab.length - 1 - i] = tmp;
		}
	}

	/**
	 * Telecharger le contenu d'un lien sur le web
	 * @param url Le lien dont on souhaite telecharger le contenu
	 * @return Une chaine de caractere contenant le contenu retournee par la reuqte http
	 * @throws ExchangeError
	 */
	public static String getData(URL url) throws ExchangeError{
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setUseCaches(false);
			conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.172 Safari/537.22");
			// Set standard HTTP/1.1 no-cache headers.
			conn.addRequestProperty("Cache-Control", "private, no-store, no-cache, must-revalidate,max-age=0");
			conn.setReadTimeout(200*1000);

			
			InputStream is = conn.getInputStream();
			return convertStreamToString(is);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ExchangeError(e1.getMessage());
		}
	}
	
	
	/**
	 * Methode pour filtrer que les echanges jamais vu dans le cas ou on peut pas le faire avec l'API (btc-e et bitstamp)
	 */
	public static Trade[] filterRecentTrade(Trade[] tmp_last_trades, Trades hist) {
		if (tmp_last_trades.length == 0)
			return tmp_last_trades;
		else {
			int nb_new_trades = 0;// On va compter le nombre de vrais nouveaux
			// trades
			if (hist.size() > 0) {
				// tant que les echanges recus sont plus recents que le dernier
				// recu
				while (nb_new_trades<tmp_last_trades.length && !tmp_last_trades[nb_new_trades].tid.equals(hist.getLast().tid)) {
					nb_new_trades++;
				}
			} else
				// si l'historique est vide, ca veut dire que tous les trades
				// sont nouveaux
				nb_new_trades = tmp_last_trades.length;

			// La on les ajoute � l'envers car btce inverse l'ordre
			Trade[] last_trades = new Trade[nb_new_trades];
			for (int j = 0; j < last_trades.length; j++)
				last_trades[j] = tmp_last_trades[nb_new_trades - 1 - j];

			return last_trades;
		}
	}
	
	/**
	 * Convertir un InputStream en String
	 */
	public static String convertStreamToString(InputStream is) {
	    Scanner s = new java.util.Scanner(is,"UTF-8").useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	public static ArrayList<Currency> getCurrencyList(Market[] markets){
		// Get the list of all the currency used by the trader agent
				ArrayList<Currency> listCurrencies = new ArrayList<Currency>(markets.length * 2);
				for (Market m : markets) {
					if (!listCurrencies.contains(m.cur1))
						listCurrencies.add(m.cur1);
					if (!listCurrencies.contains(m.cur2))
						listCurrencies.add(m.cur2);
				}
				return listCurrencies;
	}
}
