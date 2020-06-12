import java.util.Random;

public class EvalMults {
	public int myTurnMult;
	public int rawModifier;
	public int spaceValue;
	public int pressureModifier;
	public int castleValue;
	public EvalMults(int mTM, int rM, int sV, int pM, int cV) {
		myTurnMult = mTM;
		rawModifier = rM;
		spaceValue = sV;
		pressureModifier = pM;
		castleValue = cV;
	}
	public EvalMults dupe() {
		return new EvalMults(myTurnMult, rawModifier, spaceValue, pressureModifier, castleValue);
	}
	public void changeValues(int magnitude) {
		Random ran = new Random();
		myTurnMult += (ran.nextInt((magnitude*2)+1) - magnitude);
		rawModifier += (ran.nextInt((magnitude*2)+1) - magnitude);
		spaceValue += (ran.nextInt((magnitude*2)+1) - magnitude);
		pressureModifier += (ran.nextInt((magnitude*2)+1) - magnitude);
		castleValue += (ran.nextInt((magnitude*2)+1) - magnitude);
		
	}
	public String toString() {
		return "mTM: " + myTurnMult + " rM: " + rawModifier + " sV: " + spaceValue + " pM: " + pressureModifier + " cV: " + castleValue;
	}
}
