package gehos.comun.util;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;

public class TimerInterceptor extends EmptyInterceptor {

	public void afterTransactionBegin(Transaction tx){
		tx.setTimeout(600000);
	}
	
}
