import java.awt.Color;

public class Tester {
	
	private static Thread transmitter;
	
	/**
	 * Simulates the transmission of 3 numbers every 1ms.
	 * The numbers are pseudo random, and scaled to form a sort of sawtooth waveform.
	 * This is used to check for proper autoscaling of charts, etc.
	 */
	@SuppressWarnings("deprecation")
	public static void startTransmission() {
		
		if(transmitter != null)
			transmitter.stop();
		
		transmitter = new Thread(new Runnable() {
			@Override public void run() {
				
				while(true) {
					double scalar = ((System.currentTimeMillis() % 30000) - 15000) / 100.0;
					double[] newSamples = new double[] {
						(System.nanoTime() % 100) * scalar * 1.0,
						(System.nanoTime() % 100) * scalar * 0.8,
						(System.nanoTime() % 100) * scalar * 0.6
					};
					Controller.insertSamples(newSamples);
					Controller.insertSamples(newSamples);
					Controller.insertSamples(newSamples);
					Controller.insertSamples(newSamples);
					Controller.insertSamples(newSamples);
					Controller.insertSamples(newSamples);
					Controller.insertSamples(newSamples);
					Controller.insertSamples(newSamples);
					Controller.insertSamples(newSamples);
					Controller.insertSamples(newSamples);
					try { Thread.sleep(1); } catch(Exception e) {} // wait 1ms
				}
				
			}
		});
		transmitter.setName("Test Transmitter");
		transmitter.start();
		
	}
	
	@SuppressWarnings("deprecation")
	public static void stopTransmission() {
		
		if(transmitter != null)
			transmitter.stop();
		
	}
	
	public static void populateDataStructure() {
		
		Controller.removeAllDatasets();
		
		int location = 0;
		BinaryProcessor processor = Controller.getBinaryProcessors()[0];
		String name = "";
		Color color = null;
		String unit = "Volts";
		double conversionFactorA = 1;
		double conversionFactorB = 1;
		
		location = 0;
		name = "Waveform A";
		color = Color.RED;
		Controller.insertDataset(location, processor, name, color, unit, conversionFactorA, conversionFactorB);
		
		location = 1;
		name = "Waveform B";
		color = Color.GREEN;
		Controller.insertDataset(location, processor, name, color, unit, conversionFactorA, conversionFactorB);
		
		location = 2;
		name = "Waveform C";
		color = Color.BLUE;
		Controller.insertDataset(location, processor, name, color, unit, conversionFactorA, conversionFactorB);
		
	}

}
