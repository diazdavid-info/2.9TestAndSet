package david;

import java.util.concurrent.atomic.AtomicInteger;

public class TestAndSet implements Runnable {
	
	public static final int NUMERO_SUMADO = 10000;
	public static final long NUM_VECES = 10000;
	
	private volatile long _suma = 0;
	protected AtomicInteger _cerrojo = new AtomicInteger(0);
	
	private static long sumaN(long acumulador, int n){
		long total = acumulador;
		for (int i = 0; i < n; i++) {
			total += 1;
		}
		return total;
	}
	
	@Override
	public void run() {
		for (int i = 1; i <= NUM_VECES; i++) {
			entradaSeccionCritica();
			_suma = sumaN(_suma, NUMERO_SUMADO);
			salidaSeccionCritica();
		}
		
	}
	
	protected void entradaSeccionCritica() {
		while(!testAndSet());
	}
	
	protected void salidaSeccionCritica() {
		_cerrojo.set(0);
	}
	
	protected boolean testAndSet() {
		//System.out.println(_cerrojo.get());
		return _cerrojo.compareAndSet(0, 1);
	}
	
	public long getSuma() {
		return _suma;
	}
	
	public static void main(String[] args) throws InterruptedException {
		TestAndSet race = new TestAndSet();
		Thread t1, t2;
		
		t1 = new Thread(race, "Hebra0");
		t2 = new Thread(race, "Hebra1");
		
		t1.start();
		t2.start();
		
		long resultadoEsperado = NUMERO_SUMADO * NUM_VECES * 2;
		
		t1.join();
		t2.join();
		
		System.out.println("El resultado final es "+race.getSuma());
		System.out.println("Esperamos "+resultadoEsperado);
		
		if(race.getSuma() != resultadoEsperado){
			System.out.println("NO COINCIDE");
		}
	}

	

}