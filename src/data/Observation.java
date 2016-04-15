package data;

/**
 * Storage object for observation data
 * @author Oliver Palmer
 *
 */
public class Observation {

	private float x;
	private float y;
	private float heading;
	private float value;
	
	public Observation(float x, float y, float heading, float value) {
		this.x = x;
		this.y = y;
		this.heading = heading;
		this.value = value;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getHeading() {
		return heading;
	}

	public void setHeading(float heading) {
		this.heading = heading;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Observation [x=" + x + ", y=" + y + ", heading=" + heading + ", value=" + value + "]";
	}
	
	
}
