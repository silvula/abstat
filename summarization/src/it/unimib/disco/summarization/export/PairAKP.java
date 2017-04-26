package it.unimib.disco.summarization.export;

public class PairAKP {

	private String subject;
	private String object;

	public PairAKP(String subject, String object) {
		super();
		this.subject = subject;
		this.object = object;
	}

	public String getSubject() {
		return subject;
	}

	public String getObject() {
		return object;
	}

	public boolean unorderedEquals(PairAKP p) {
		if (p.getSubject().equals(p.getObject())) {
			if (p.getObject().equals(this.getObject()) && p.getObject().equals(this.getSubject()))
				return true;
			else
				return false;
		} else {
			if ((p.getSubject().equals(this.getSubject()) || p.getObject().equals(this.getSubject()))
					&& (this.getObject().equals(p.getObject()) || this.getObject().equals(p.getSubject()))) {
				return true;
			} else
				return false;
		}
	}

	@Override
	public String toString() {
		return "PairAKP [subject=" + subject + ", object=" + object + "]";
	}
	
	

}
