package it.unimib.disco.summarization.test.system;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.BaseMatcher;

 
public class StringMatcher {
	public static Matcher<String> containsStringNTimes(final String s, final int n) {
		return new BaseMatcher<String>() {
 
			@Override
			public void describeTo(final Description description) {
				description.appendText("a string containing "+s+" " +n+" times.");
				
			}
 
			@Override
			public boolean matches(final Object item) {
				String i = (String)item;
				int cont = 0;
				while(i.contains(s)){
					cont++;
					int index = i.indexOf(s);
					i = i.substring(index+s.length());
				}
				
				return n==cont;
			}
 
			@Override
			public void describeMismatch(final Object item, final Description mismatchDescription) {
				mismatchDescription.appendText("was ").appendText((String)item);
			}
			
		};
	}
}