package it.unimib.disco.summarization.tests;

import it.unimib.disco.summarization.utility.NTriple;
import it.unimib.disco.summarization.utility.NTripleAnalysis;

import java.util.ArrayList;
import java.util.List;

public class NTripleAnalysisInspector implements NTripleAnalysis{

	private List<NTriple> processed = new ArrayList<NTriple>();
	
	@Override
	public NTripleAnalysis track(NTriple triple) {
		processed.add(triple);
		return this;
	}
	
	public int countProcessed(){
		return processed.size();
	}
}