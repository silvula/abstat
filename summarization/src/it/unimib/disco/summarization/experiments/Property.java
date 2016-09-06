package it.unimib.disco.summarization.experiments;

import com.hp.hpl.jena.ontology.OntProperty;

public class Property  {

	private OntProperty property;
	private int depth;
	
	public Property(OntProperty ontProp){
		property = ontProp;
	}

	public Property(OntProperty ontProp, int depth){
		this(ontProp);
		this.depth = depth;
	}
	
	public String getURI(){ return property.getURI();}
	
	public OntProperty getOntProp(){ return property;}
	public int getDepth(){ return depth; }
	public void setDepth(int arg) { depth = arg; } 
	
	
	public String toString(){
		return getURI();// + "$$" + depth;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((property == null) ? 0 : property.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Property other = (Property) obj;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.getURI().equals(other.property.getURI()))
			return false;
		return true;
	}
}
