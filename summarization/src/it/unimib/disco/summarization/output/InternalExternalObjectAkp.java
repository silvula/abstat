package it.unimib.disco.summarization.output;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class InternalExternalObjectAkp
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		String pathFile = args[0];
		String dataset = args[1];
		String payLevelDomain = args[2];
		
		objectAkpInternalExternal(pathFile,dataset,payLevelDomain);
	}
	
	private static void objectAkpInternalExternal(String pathFile, String dataset, String payLevelDomain) throws FileNotFoundException, IOException
	{
		/*Per leggere da file .txt l'input.*/
		String fileObjectAkpsPath = pathFile;
		BufferedReader brObjectAkps = new BufferedReader(new FileReader(fileObjectAkpsPath));
		
		/*Per scrivere su file .txt l'output.*/
		FileWriter fwObjectAkps = new FileWriter("../data/summaries/"+dataset+"/patterns/object-akp-new.txt");
		BufferedWriter bwObjectAkps = new BufferedWriter(fwObjectAkps);
		
		/*Cuore dell'Algoritmo.*/
		boolean trovatoPrimoDoppioCancelletto = false;
		boolean trovatoSecondoDoppioCancelletto = false;
		boolean trovatoTerzoDoppioCancelletto = false;
		String lineRead = null;
		String subjectObjectAkp = "";
		String propertyObjectAkp = "";
		String objectObjectAkp = "";
		String numberOfInstances = "";
		String typeOfObjectAkp = "";
		
		lineRead = brObjectAkps.readLine();
		
		while (lineRead != null)
		{
			for (int i = 0; i < lineRead.length()-4; i++)
			{
				if (trovatoPrimoDoppioCancelletto == false) 
				{
					if ((lineRead.charAt(i) != '#') && (lineRead.charAt(i+1) != '#'))
					{
						subjectObjectAkp += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) != '#') && (lineRead.charAt(i+1) == '#'))
					{
						subjectObjectAkp += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) == '#') && (lineRead.charAt(i+1) != '#'))
					{
						subjectObjectAkp += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) == '#') && (lineRead.charAt(i+1) == '#'))
					{
						trovatoPrimoDoppioCancelletto = true;
					}
				}
				
				if (trovatoPrimoDoppioCancelletto == true)
				{
					if (trovatoSecondoDoppioCancelletto == false)
					{
						if ((lineRead.charAt(i+2) != '#') && (lineRead.charAt(i+3) != '#'))
						{
							propertyObjectAkp += lineRead.charAt(i+2);
						}
						if ((lineRead.charAt(i+2) != '#') && (lineRead.charAt(i+3) == '#'))
						{
							propertyObjectAkp += lineRead.charAt(i+2);
						}
						if ((lineRead.charAt(i+2) == '#') && (lineRead.charAt(i+3) != '#'))
						{
							propertyObjectAkp += lineRead.charAt(i+2);
						}
						if ((lineRead.charAt(i+2) == '#') && (lineRead.charAt(i+3) == '#'))
						{
							trovatoSecondoDoppioCancelletto = true;
						}
					}
					if (trovatoSecondoDoppioCancelletto == true)
					{
						if (trovatoTerzoDoppioCancelletto == false)
						{
							if ((lineRead.charAt(i+4) != '#') && (lineRead.charAt(i+5) != '#'))
							{
								objectObjectAkp += lineRead.charAt(i+4);
							}
							if ((lineRead.charAt(i+4) != '#') && (lineRead.charAt(i+5) == '#'))
							{
								objectObjectAkp += lineRead.charAt(i+4);
							}
							if ((lineRead.charAt(i+4) == '#') && (lineRead.charAt(i+5) != '#'))
							{
								objectObjectAkp += lineRead.charAt(i+4);
							}
							if ((lineRead.charAt(i+4) == '#') && (lineRead.charAt(i+5) == '#'))
							{
								trovatoTerzoDoppioCancelletto = true;
							}
						}
						if (trovatoTerzoDoppioCancelletto == true)
						{
							if (lineRead.charAt(i+4) != '#')
							{
								numberOfInstances += lineRead.charAt(i+4);
							}
						}
					}
				}
			}
			
			/*Un akp è definito 'interno' se sia il soggetto sia l'oggetto dell'akp provengono da "http://dbpedia.org". Altrimenti è definito 'esterno'.*/
			if ((subjectObjectAkp.contains(payLevelDomain)) && (objectObjectAkp.contains(payLevelDomain)))
			{
				typeOfObjectAkp = "internalObjectAkp";
			}
			else
			{
				typeOfObjectAkp = "externalObjectAkp";
			}
			
			bwObjectAkps.write(subjectObjectAkp);
			bwObjectAkps.write("##");
			bwObjectAkps.write(propertyObjectAkp);
			bwObjectAkps.write("##");
			bwObjectAkps.write(objectObjectAkp);
			bwObjectAkps.write("##");
			bwObjectAkps.write(numberOfInstances);
			bwObjectAkps.write("##");
			bwObjectAkps.write(typeOfObjectAkp);
			bwObjectAkps.write("\n");
			
			subjectObjectAkp = "";
			propertyObjectAkp = "";
			objectObjectAkp = "";
			numberOfInstances = "";
			typeOfObjectAkp = "";
			trovatoPrimoDoppioCancelletto = false;
			trovatoSecondoDoppioCancelletto = false;
			trovatoTerzoDoppioCancelletto = false;
			
			lineRead = brObjectAkps.readLine();
		}
		
		/*Chiudo le connessioni con i file.*/
		brObjectAkps.close();
		bwObjectAkps.close();
		fwObjectAkps.close();
	}
}
