import java.util.concurrent.atomic.AtomicLong;

public class AtomicCounter {

	//private static AtomicCounter instance = null;

	
	private AtomicLong counter;
	private long end;
	private long increment;
	
//	public static AtomicCounter getInstance() {
//		if (instance == null) {
//			instance = new AtomicCounter();
//		}		
//		return instance;
//	}
	
	public AtomicCounter ()  
	{
		counter = new AtomicLong();
//		instance = this;
	}

	public AtomicCounter (long start, long end)  
	{
		this(start,end,1);
	}

	public AtomicCounter (long start, long end,long inc)  
	{
		this();
		setStart(start);
		setEnd(end);
		setIncrement(inc);
	}
	
	
	public void setStart (long l) { counter.set(l); }
	public void setEnd (long l) { end = l; }
	public void setIncrement(long l) {increment = l;}

	public long getNext() {
	
		long l = counter.getAndAdd(increment); // No need for synchronization
		if (increment > 0 && l<=end) return l;
		if (increment < 0 && l>=end) return l;

		return -1;
			
	}
}