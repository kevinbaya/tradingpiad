package market.mtgox;

import java.math.BigDecimal;

import market.Currency;
import market.ExchangeError;
import market.Order;
import market.VirtualData;
import market.Wallet;
import utilities.Item;
import utilities.LinkedList;

/**
 * Classe implementant la gestion des donnees virtuelles (ordres virtuels et simulation de l'execution des ordres).
 * En faite, elle ne fait qu'appeler les methodes correpondantes de la classe VirtualData.
 *
 */
public class MarketMtgoxVirtual extends MarketMtgoxLive{

	private VirtualData virtData;
	
	public MarketMtgoxVirtual(Currency cur1, Currency cur2, Wallet wallet, long timeDelta, long endTime) throws ExchangeError {
		super(cur1, cur2, timeDelta, endTime);
		virtData= new VirtualData(this,wallet);
	}

	@Override
	public LinkedList<Order> getOpenAsks() {
		return virtData.getOpenAsks();
	}

	@Override
	public LinkedList<Order> getOpenBids() {
		return virtData.getOpenBids();
	}

	@Override
	public void addAsk(Order o) {
		virtData.addAsk(o);
	}

	@Override
	public void addBid(Order o) {
		virtData.addBid(o);
	}

	@Override
	public void cancelOrder(Item<Order> item) {
		virtData.cancelOrder(item);
	}

	@Override
	public void check() {
		virtData.checkNewTrades();
	}

	@Override
	public Wallet getWallet() {
		return virtData.getWallet();
	}
	@Override
	public BigDecimal getInMarketCur1(){
		return virtData.getInMarketCur1();
	}
	
	@Override
	public BigDecimal getInMarketCur2(){
		return virtData.getInMarketCur2();
	}
	
	public LinkedList<Order> getExecutedBids(){
		return virtData.hist_bids;  
	}

	
}
