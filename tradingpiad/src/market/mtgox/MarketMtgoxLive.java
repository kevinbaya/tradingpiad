package market.mtgox;

import java.net.MalformedURLException;
import java.net.URL;

import market.Currency;
import market.EndOfRun;
import market.ExchangeError;
import utilities.Util;

/**
 * Classe implementant la procedure de recuperation des donnees json dans le cas d'une simulation live ou d'une possible implementation reelle
 *
 */
public abstract class MarketMtgoxLive extends MarketMtgox {

	private URL url_ticker;
	private URL url_depth;
	private URL url_trades;
	private String str_ticker;
	private String str_depth;
	private String str_trades;
	
	final private long timeDelta;
	final private long startTime;
	final private long endTime;
	private long currentTime;

	public MarketMtgoxLive(Currency cur1, Currency cur2, long timeDelta,long endTime) throws ExchangeError {
		super(cur1, cur2);
		this.startTime=System.currentTimeMillis();
		this.currentTime=this.startTime;
		this.timeDelta=timeDelta;
		this.endTime=endTime;
		str_ticker = "http://data.mtgox.com/api/0/data/ticker.php?Currency=" + cur2.name();
		str_depth = "https://data.mtgox.com/api/0/data/getDepth.php?Currency=" + cur2.name();
		str_trades = "http://data.mtgox.com/api/0/data/getTrades.php?" + cur2.name();

		try {
			url_ticker = new URL(str_ticker);
			url_depth = new URL(str_depth);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.exit(0);// On sort, ce cas ne doit pas arriver
		}
	}

	@Override
	public void updateTicker() throws ExchangeError {
		super.jsonTicker=Util.getData(url_ticker);
		super.updateTicker();
	}

	@Override
	public void updateDepth() throws ExchangeError {
		super.jsonDepth= Util.getData(url_depth);
		super.updateDepth();
	}

	@Override
	public void updateTrades() throws ExchangeError {
		try {
			if (trades == null || trades.size() == 0) {
				url_trades = new URL(str_trades);
			} else {
				url_trades = new URL(str_trades + "&since=" + trades.getLast().tid);
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		super.jsonTrades= Util.getData(url_trades);
		super.updateTrades();
	}
	
	@Override
	public void nextTimeDelta() throws EndOfRun{
		currentTime = System.currentTimeMillis() ;
		if(currentTime>= endTime)
			throw new EndOfRun();
	}
	
	@Override
	public void sleep(){
		try {
			Thread.sleep(timeDelta); // Si on prends les donnees en live, on doit vraimennt attendre
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	@Override
	public long getStartTime(){
		return startTime;
	}
	
	@Override
	public  long getCurrentTime(){
		return currentTime;
	}
	
	@Override
	public  long getEndTime(){
		return endTime;
	}
}
