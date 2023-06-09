package de.unima.ki.anyburl.threads;


import java.util.ArrayList;


import de.unima.ki.anyburl.Learn;
import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.algorithm.PathSampler;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.structure.Path;
import de.unima.ki.anyburl.structure.Rule;
import de.unima.ki.anyburl.structure.RuleFactory;
import de.unima.ki.anyburl.structure.RuleZero;

/**
 * 
 * The worker thread responsible for learning rules in the reinforced learning setting.
 * 
 */
public class Scorer extends Thread {
	

	private TripleSet triples;
	private PathSampler sampler;
	
	// private int entailedCounter = 1;

	
	private int createdRules = 0;
	private int storedRules = 0;
	private double producedScore = 0.0;
	
	private int id = 0;
	


	// this is not really well done, exactly one of them has to be true all the time
	private boolean mineParamCyclic = true;
	private boolean mineParamAcyclic = false;
	private boolean mineParamZero = false;
	
	private int mineParamLength = 1; // possible values are 1 and 2 (if non-cyclic), or 1, 2, 3, 4, 5 if (cyclic)

	
	private boolean ready = false;
	
	private boolean onlyXY = false;
	
	
	
	// ***** lets go ******
	
	public Scorer(TripleSet triples, int id) {
		this.triples = triples;
		this.sampler = new PathSampler(triples);
		this.id = id;
	}
	
	public void setSearchParameters(boolean zero, boolean cyclic, boolean acyclic, int len) {
		this.mineParamZero = zero;
		this.mineParamCyclic = cyclic;
		this.mineParamAcyclic = acyclic;
		this.mineParamLength = len;
		this.ready = true;
		this.onlyXY = false;
		if (this.mineParamCyclic) {
			if (this.mineParamLength > Settings.MAX_LENGTH_GROUNDED_CYCLIC)	{
				this.onlyXY = true;
			}
		}
		//System.out.println("THREAD-" + this.id + " using parameters C=" + this.mineParamCyclic + " L=" + this.mineParamLength);
	}
	
	private String getType() {
		if (this.mineParamZero) return "Zero";
		if (this.mineParamCyclic) return "Cyclic";
		if (this.mineParamAcyclic) return "Acyclic";
		return "";		
	}
	
	
	public void run() {
		
		while (!Learn.areAllThere()) {
			Learn.heyYouImHere(this.id);
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.out.println("THREAD-" + this.id + " waiting for the others");
		}	
		System.out.println("THREAD-" + this.id + " starts to work with L=" + this.mineParamLength + " C=" + this.getType() + " ");
		
		// outer loop is missing

		boolean done = false;
		while (done == false) {
			if (!Learn.active(this.id, this.storedRules, this.createdRules, this.producedScore, this.mineParamZero, this.mineParamCyclic, this.mineParamAcyclic, this.mineParamLength) || !ready) {
				this.createdRules = 0;
				this.storedRules = 0;
				this.producedScore = 0.0;
				try { Thread.sleep(10);	}
				catch (InterruptedException e) { e.printStackTrace();}
			}
			else {
				
				long start = System.currentTimeMillis();
				// search for zero rules
				if (mineParamZero) {
					Path path = sampler.samplePath(this.mineParamLength + 1, false);
					// System.out.println("zero (sample with steps=" + (this.mineParamLength+1) + "):" + path);
					
					if (path != null) {
						ArrayList<Rule> learnedRules = RuleFactory.getGeneralizations(path, false);
						if (!Learn.active) {
							try { Thread.sleep(10); }
							catch (InterruptedException e) { e.printStackTrace(); }
						}
						else {
							for (Rule learnedRule : learnedRules) {
								this.createdRules++;
								if (learnedRule.isTrivial()) continue;
								if (Learn.isStored(learnedRule)) {
									
									//long t1 = System.currentTimeMillis();
									learnedRule.computeScores(this.triples);
									//long t2 = System.currentTimeMillis();
									//if (t2 - t1 > 500) {
									//	System.out.println("* elapsed: " + (t2 - t1) + " >>> " + learnedRule);
									//}
					
									if (learnedRule.getConfidence() >= Settings.THRESHOLD_CONFIDENCE && learnedRule.getCorrectlyPredicted() >= Settings.THRESHOLD_CORRECT_PREDICTIONS && (!(learnedRule instanceof RuleZero) || learnedRule.getCorrectlyPredicted() > Settings.THRESHOLD_CORRECT_PREDICTIONS_ZERO)) {
										if (Learn.active) {
											Learn.storeRule(learnedRule);
											// System.out.println(">>> " +  learnedRule);
											this.producedScore += getScoringGain(learnedRule, learnedRule.getCorrectlyPredicted(), learnedRule.getConfidence(), learnedRule.getAppliedConfidence());
											this.storedRules++;	
										}
									}
								}
							}
						}
					}
					
					/*
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					*/
				}
				
				// search for cyclic rules
				if (mineParamCyclic) {
					Path path = sampler.samplePath(this.mineParamLength + 1, true);
					if (path != null && path.isValid()) {
						// System.out.println(path);
						ArrayList<Rule> learnedRules = RuleFactory.getGeneralizations(path, this.onlyXY);
						// System.out.println(learnedRules.size());
						if (!Learn.active) {
							try { Thread.sleep(10); }
							catch (InterruptedException e) { e.printStackTrace(); }
						}
						else {
							for (Rule learnedRule : learnedRules) {
								this.createdRules++;
								if (learnedRule.isTrivial()) continue;
								// if (learnedRule.isRedundantACRule(triples)) continue;
								// long l2;
								// long l1 = System.currentTimeMillis();
								if (Learn.isStored(learnedRule)) {
									
									//long t1 = System.currentTimeMillis();
									learnedRule.computeScores(this.triples);
									//long t2 = System.currentTimeMillis();
									//if (t2 - t1 > 500) {
									//	System.out.println("* elapsed: " + (t2 - t1) + " >>> " + learnedRule);
									//}
								
									if (learnedRule.getConfidence() >= Settings.THRESHOLD_CONFIDENCE && learnedRule.getCorrectlyPredicted() >= Settings.THRESHOLD_CORRECT_PREDICTIONS && (!(learnedRule instanceof RuleZero) || learnedRule.getCorrectlyPredicted() > Settings.THRESHOLD_CORRECT_PREDICTIONS_ZERO)) {
										if (Learn.active) {
											Learn.storeRule(learnedRule);											
											// this.producedScore += getScoringGain(learnedRule.getCorrectlyPredictedMax(), learnedRule.getConfidenceMax());
											this.producedScore += getScoringGain(learnedRule, learnedRule.getCorrectlyPredicted(), learnedRule.getConfidence(), learnedRule.getAppliedConfidence());
											this.storedRules++;
										}
									}
								}
								else {
									// l2 = System.currentTimeMillis();
								}
								
								// if (l2 - l1 > 100) System.out.println("uppps");
							}
						}
					}	
				}
				// search for acyclic rules
				if (mineParamAcyclic) {
					Path path = sampler.samplePath(mineParamLength + 1, false);
					if (path != null && path.isValid()) {
						ArrayList<Rule> learnedRules = RuleFactory.getGeneralizations(path, false);
						if (!Learn.active) {
							try { Thread.sleep(10); }
							catch (InterruptedException e) { e.printStackTrace(); }
						}
						else {
							for (Rule learnedRule : learnedRules) {
								this.createdRules++;
								if (learnedRule.isTrivial()) continue;
								// long l2;
								//long l1 = System.currentTimeMillis();
								if (Learn.isStored(learnedRule)) {
									// l2 = System.currentTimeMillis();
									
									//long t1 = System.currentTimeMillis();
									learnedRule.computeScores(this.triples);
									//long t2 = System.currentTimeMillis();
									//if (t2 - t1 > 500) {
									//	System.out.println("* elapsed: " + (t2 - t1) + " >>> " + learnedRule);
									//}

									
									if (learnedRule.getConfidence() >= Settings.THRESHOLD_CONFIDENCE && learnedRule.getCorrectlyPredicted() >= Settings.THRESHOLD_CORRECT_PREDICTIONS && (!(learnedRule instanceof RuleZero) || learnedRule.getCorrectlyPredicted() > Settings.THRESHOLD_CORRECT_PREDICTIONS_ZERO)) {
										if (Learn.active) {
											Learn.storeRule(learnedRule);
											this.producedScore += getScoringGain(learnedRule, learnedRule.getCorrectlyPredicted(), learnedRule.getConfidence(), learnedRule.getAppliedConfidence());
											this.storedRules++;	
										}
									}
								}
								else {
									
								}
								// if (l2 - l1 > 200) System.out.println("uppps");
							}
						}
					}	
				}
			}
			
			
		}
	}
	
	public double getScoringGain(Rule rule, int correctlyPredicted, double confidence, double appliedConfidence ) {
		if (Settings.REWARD == 1) return (double)correctlyPredicted;
		if (Settings.REWARD == 2) return (double)correctlyPredicted * confidence;
		if (Settings.REWARD == 3) return (double)correctlyPredicted * appliedConfidence;
		if (Settings.REWARD == 4) return (double)correctlyPredicted * appliedConfidence * appliedConfidence;
		if (Settings.REWARD == 5) return (double)correctlyPredicted * appliedConfidence / Math.pow(2, (rule.bodysize()-1));
		return 0.0;
	}

}
