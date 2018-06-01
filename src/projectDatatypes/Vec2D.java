package projectDatatypes;

//Simple 2D Vector Implementation
public class Vec2D {
	private float x;		//Reduced to unit vector x
	private float y; 		//Reduced to unit vector y
	
	private float magnitude;	//Magnitude of vector
	
	public Vec2D() {
		x = 0;
		y = 0;
		magnitude = 0;
	}
	public Vec2D(float _x, float _y) {
		x = _x;
		y = _y;
		
		Normalize();
	}
	public Vec2D(Vec2D other) {
		this.x = other.x;
		this.y = other.y;
		this.magnitude = other.magnitude;
	}	
	public float getX() {
		return x * magnitude;
	}
	public float getY() {
		return y * magnitude;
	}
	public float getDimension(int dim) {
		if (dim == 0) {
			return getX();
		}
		else {
			return getY();
		}
	}
	public Vec2D Add(Vec2D other){
		Vec2D result = new Vec2D();
		result.x = this.x * this.magnitude + other.x * other.magnitude;
		result.y = this.y * this.magnitude + other.y * other.magnitude;
		result.Normalize();
		return result;
	}
	public Vec2D Sub(Vec2D other){
		Vec2D result = new Vec2D();
		result.x = this.x * this.magnitude - other.x * other.magnitude;
		result.y = this.y * this.magnitude - other.y * other.magnitude;
		result.Normalize();
		return result;
	}
	public Vec2D Multiply(Vec2D other){
		Vec2D result = new Vec2D();
		result.x = this.x * this.magnitude * other.x * other.magnitude;
		result.y = this.y * this.magnitude * other.y * other.magnitude;
		result.Normalize();
		return result;
	}
	public float Dot(Vec2D other){
		float result = 0;
		result += this.x * this.magnitude * other.x * other.magnitude;
		result += this.y * this.magnitude * other.y * other.magnitude;
		return result;
	}	
	public float Distance(Vec2D other) {
		float result = 
				(float) Math.sqrt(Math.pow(this.getX() - other.getX(), 2) + 
				Math.pow(this.getY() - other.getY(), 2));
		return result;
	}
	public Vec2D Scale(float m) {
		Vec2D result = new Vec2D(this);
		result.magnitude *= m;
		return result;
	}
	public Vec2D Unit() {
		Vec2D result = new Vec2D(this);
		result.magnitude = 1;
		return result;
	}
	public float Norm() {
		return magnitude;
	}
	public void Normalize() {
		magnitude = (float) Math.sqrt((Math.pow(x,2) + Math.pow(y, 2)));
		x = x/magnitude;
		y = y/magnitude;
	}
}