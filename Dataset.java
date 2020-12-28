import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Defines all of the details about a CSV column or Binary packet element, and stores all of its samples.
 */
public class Dataset {
	
	// constants defined at constructor-time
	final int    location;
	final BinaryProcessor processor;
	final String name;
	final Color  color;
	final String unit;
	final double conversionFactorA;
	final double conversionFactorB;
	final double conversionFactor;

	// samples are stored in an array of double[]'s, each containing 1M doubles, and allocated as needed.
	// access to the samples is controlled with an atomic integer, providing lockless concurrency if only there is only one writer.
	final int slotSize = (int) Math.pow(2, 20); // 1M doubles per slot
	final int slotCount = (Integer.MAX_VALUE / slotSize) + 1;
	double[][] slot;
	AtomicInteger size;
	
	/**
	 * Creates a new object that describes one dataset and stores all of its samples.
	 * 
	 * @param location             CSV column number, or Binary byte offset.
	 * @param processor            Data processor for the raw samples in the Binary data packet. (Ignored in CSV mode.)
	 * @param name                 Descriptive name of what the samples represent.
	 * @param color                Color to use when visualizing the samples.
	 * @param unit                 Descriptive name of how the samples are quantified.
	 * @param conversionFactorA    This many unprocessed LSBs...
	 * @param conversionFactorB    ... equals this many units.
	 */
	public Dataset(int location, BinaryProcessor processor, String name, Color color, String unit, double conversionFactorA, double conversionFactorB) {
		
		this.location          = location;
		this.processor         = processor;
		this.name              = name;
		this.color             = color;
		this.unit              = unit;
		this.conversionFactorA = conversionFactorA;
		this.conversionFactorB = conversionFactorB;
		this.conversionFactor  = conversionFactorB / conversionFactorA;
		
		slot = new double[slotCount][];
		size = new AtomicInteger(0);
		
	}
	
	/**
	 * @return    The name of this dataset.
	 */
	@Override public String toString() {
		
		return name;
		
	}
	
	/**
	 * @return    Count of the stored samples.
	 */
	public int size() {
		
		return size.get();
		
	}
	
	/**
	 * @param index    Which sample to obtain.
	 * @return         The sample.
	 */
	public double get(int index) {
		
		int slotNumber = index / slotSize;
		int slotIndex  = index % slotSize;
		return slot[slotNumber][slotIndex];
		
	}
	
	/**
	 * @param value    New raw sample to be converted and then appended to the dataset.
	 */
	public void add(double value) {
		
		int currentSize = size.get();
		int slotNumber = currentSize / slotSize;
		int slotIndex  = currentSize % slotSize;
		if(slotIndex == 0)
			slot[slotNumber] = new double[slotSize];
		slot[slotNumber][slotIndex] = value * conversionFactor;
		size.incrementAndGet();
		
	}
	
	/**
	 * Empties the dataset, but does not free any memory.
	 */
	public void clear() {
		
		size.set(0);
		
	}
	
}
