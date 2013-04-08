package market.bitstamp;

import java.net.MalformedURLException;
import java.net.URL;

import utilities.Util;

import market.Currency;
import market.EndOfRun;
import market.ExchangeError;

public abstract class MarketBitstampLive extends MarketBitstamp {

	private URL url_trades;
	private URL url_depth;
	private URL url_ticker;
	final private long timeDelta;
	final private long startTime;
	final private long endTime;
	private long currentTime;

	public MarketBitstampLive(Currency cur1, Currency cur2, long timeDelta,long endTime) throws ExchangeError {
		super(cur1, cur2);
		this.startTime=System.currentTimeMillis();
		this.currentTime=this.startTime;
		this.timeDelta=timeDelta;
		this.endTime=endTime;
		
		// Intilisation des urls de chaque requetes
		try {
			url_ticker = new URL("https://www.bitstamp.net/api/ticker/");
			url_depth = new URL("https://www.bitstamp.net/api/order_book/");
			url_trades = new URL("https://www.bitstamp.net/api/transactions/?timedelta=600");

		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.exit(0);// On sort, ce cas ne doit pas arriver !
		}
	}

	@Override
	public void updateTicker() throws ExchangeError {
		jsonTicker= Util.getData(url_ticker);
		super.updateTicker();
	}

	@Override
	public void updateDepth() throws ExchangeError {
		jsonDepth= Util.getData(url_depth);
		super.updateDepth();
	}

	@Override
	public void updateTrades() throws ExchangeError {
		jsonTrades= Util.getData(url_trades);
		super.updateTrades();
	}
	
	@Override
	public void waitTimeDelta() throws EndOfRun{
		try {
			Thread.sleep(timeDelta);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
		if(System.currentTimeMillis() >= endTime)
			throw new EndOfRun();
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
