package de.unima.ki.anyburl.structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.exceptions.RuleFunctionalityBasicSupportOnly;


/**
 * A rule with an empty body, that fires only because something is asked.
 * This rule is always a rule of length 0, no specific conditions needs to be checked.
 * 
 *  gender(X,male) <= 
 *  
 *  This rule say that if you are asked about the gender of something, you should give the answer "mail".
 *  Its important to understand that this rule should only fire if its already known from the context of the question,
 *  that the question itself makes sense. 
 *  
 *
 */
public class RuleZero extends Rule {


	public RuleZero(RuleUntyped r) {
		super(r);
	}

	@Override
	public void computeScores(TripleSet ts) {
		
		String c = this.getHead().getConstant();
		String tr = this.getTargetRelation();
		boolean cIsHead = this.getHead().isLeftC();
		
		ArrayList<Triple> triplesTR = ts.getTriplesByRelation(tr);
		Set<String> triplesTRC = ts.getEntities(tr, c, cIsHead);
		
		this.predicted = triplesTR.size();
		this.correctlyPredicted = triplesTRC.size();
		this.confidence = (double)correctlyPredicted / (double)predicted;

	}

	@Override
	public HashSet<String> computeTailResults(String head, TripleSet ts) {
		HashSet<String> results = new HashSet<String>();
		if (this.getHead().isRightC()) {
			results.add(this.getHead().getRight());
		}
		return results;
	}

	@Override
	public HashSet<String> computeHeadResults(String tail, TripleSet ts) {
		HashSet<String> results = new HashSet<String>();
		if (this.getHead().isLeftC()) {
			results.add(this.getHead().getLeft());
		}
		return results;
	}
	
	
	public double getAppliedConfidence() {
		return Settings.RULE_ZERO_WEIGHT * super.getAppliedConfidence();
	}
	
	
	public boolean isPredictedX(String leftValue, String rightValue, Triple forbidden, TripleSet ts) {
		throw new RuleFunctionalityBasicSupportOnly();
	}

	public boolean isRefinable() {
		return false;
	}


	public Triple getRandomValidPrediction(TripleSet ts) {
		throw new RuleFunctionalityBasicSupportOnly();
	}


	public Triple getRandomInvalidPrediction(TripleSet ts) {
		throw new RuleFunctionalityBasicSupportOnly();
	}


	public ArrayList<Triple> getPredictions(TripleSet ts) {
		throw new RuleFunctionalityBasicSupportOnly();
	}

	public boolean isSingleton(TripleSet triples) {
		throw new RuleFunctionalityBasicSupportOnly();
	}
	
	/**
	 * This method computes usually for an x and y value pair, if there is a body grounding in the given triple set.
	 * If this specific case of a zero rule it returns the prediction if the prediction can be made.
	 * 
	 * @param xValue The value of the X variable.
	 * @param yValue The value of the Y variable.
	 * @param excludedGroundings The triples that are forbidden to be used.
	 * 
	 * @return A set which contains the prediction itself if the prediction can be made. An empty set if the prediction cannot be made.
	 */
	public HashSet<Triple> getTripleExplanation(String head, String tail, HashSet<Triple> blockedTriples, TripleSet ts) {
		HashSet<Triple> groundings = new HashSet<Triple>();
		Triple prediction = new Triple(head, this.getTargetRelation(), tail);
		if (blockedTriples.contains(prediction)) return groundings;
		if (this.isXRule() && tail.equals(this.getHead().getRight())) {
			groundings.add(prediction);
			return groundings;
		}
		if (this.isYRule() && head.equals(this.getHead().getLeft())) {
			groundings.add(prediction);
			return groundings;
		}
		return groundings;
	}
	
	/**
	 * Does not recompute the scores of the zeor rule, but simply returns the scores of that rule.
	 */
	public int[] computeScores(Rule that, TripleSet triples) {
		
		return new int[2];
	}

}
