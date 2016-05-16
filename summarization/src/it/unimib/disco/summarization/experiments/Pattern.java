package it.unimib.disco.summarization.experiments;

public class Pattern {
    private Concept subj;
    private String pred;
    private Concept obj;
    private String color;
    private String predShort;
    private int instances;
    private int freq;
   
    public Pattern(Concept subj, String pred, Concept obj){
        this.subj = subj;
        this.pred = pred;
        this.obj = obj;
        color = "B";
        instances = 0;
        freq = 0;
        predShort = obtainPredShort(pred);
    }
   
    
    public Concept getSubj(){ return subj; }
    public void setSubj(Concept value){ subj = value; }
   
    public String getPred(){ return pred; }
    public void setPred(String value){ pred = value; }
   
    public Concept getObj(){ return obj; }
    public void setObj(Concept value){ obj = value; }
   
    public String getColor(){ return color; }
    public void setColor(String value){ color = value; }
   
    public int getInstances(){ return instances; }
    public void setInstances(int value){ instances = value; }
   
    public int getFreq(){ return freq; }
    public void setFreq(int value){ freq = value; }
    
    private static String obtainPredShort(String arg){
		int index = arg.lastIndexOf("/");
		return arg.substring(index + 1);
	}
   
    
    public String toString(){
       // return  /*subj.getDepth()+","+ obj.getDepth()+*/+freq+","+instances+"("+subj.getName() +","+ pred+","+ obj.getName()+")"+ color+",";
        return  freq+","+ instances+ "("+subj.getName() +","+ predShort+","+ obj.getName()+")";
    }
   
   
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((obj == null) ? 0 : obj.hashCode());
        result = prime * result + ((pred == null) ? 0 : pred.hashCode());
        result = prime * result + ((subj == null) ? 0 : subj.hashCode());
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
        Pattern other = (Pattern) obj;
        if (this.obj == null) {
            if (other.obj != null)
                return false;
        } else if (!this.obj.equals(other.obj))
            return false;
        if (pred == null) {
            if (other.pred != null)
                return false;
        } else if (!pred.equals(other.pred))
            return false;
        if (subj == null) {
            if (other.subj != null)
                return false;
        } else if (!subj.equals(other.subj))
            return false;
        return true;
    }
 
   
   
}