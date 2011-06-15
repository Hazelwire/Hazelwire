package org.hazelwire.gui;

import java.util.ArrayList;

import org.hazelwire.modules.Option;

public class DummyData
{

	/**
	 * Deze methode heb ik gebruikt om dummydata aan te maken. Misschien is het
	 * voor jou handig om te kijken hoe dat ging, zodat je het na kunt doen.
	 * 
	 */
	public static ArrayList<Mod> dummydata()
	{
		/*
		 * Overbodig voor jou. Dit is een lijst met tags, waaruit ik selecties
		 * maak voor mijn dummy modules.
		 */
		ArrayList<Tag> tags = Tag.dummyData();
		/*
		 * Lijst met Mods (Module klasse) die wordt teruggegeven naar de
		 * singleton klasse ModsBookkeeper. Dat is waar jou data ook heen
		 * moeten: de ArrayList mods bevat alle modules in het systeem (Zie ook
		 * klasse ModsBookkeeper).
		 */
		ArrayList<Mod> result = new ArrayList<Mod>();
		for (int i = 0; i < 25; i++)
		{
			/*
			 * Stap 1: maak een Module aan (klasse Mod). Je maakt deze als
			 * volgt: new Mod(<modName>); met Modname een String. Voeg de module
			 * toe aan je lijst. IMPORTANT: modules worden uit elkaar gehouden
			 * op basis van hun naam, modName. Ik weet niet wat er gebeurt als
			 * je twee modules met dezelfde naam toevoegt.
			 */
			Mod m = new Mod("Module " + (i + 1));
			result.add(m);
			/*
			 * Stap 2: maak een lijst met Tags (klasse Tag). Tags maak je als
			 * volgt aan: new Tag(<Tagname>); waar Tagname een String is. In
			 * deze lijst moeten alle tags die bij een bepaalde challenge horen
			 * (laten we zeggen, Challenge A). IMPORTANT: voor Tags geldt
			 * hetzelfde. Worden uit elkaar gehouden op basis van hun naam.
			 */
			m.addTag(tags.get(i % 6));
			m.addTag(tags.get((i + 1) % 6));
			/*
			 * Vergeet de loop maar even :P.
			 */
			for (int j = 0; j < 4; j++)
			{
				/*
				 * Stap 3: maak een Challenge aan (klasse Challenge). Dit doe je
				 * zo: new Challenge(<chalName>, TagList, <description>,
				 * <points>); Met chalName en description als Strings, points
				 * als integer en Taglist als lijst (die je hiervoor hebt
				 * gemaakt). Voeg de challenge toe aan de module met methode
				 * addChallenge(<Challenge>). Je kunt ook een challenge
				 * toevoegen door de methode addChallenge(<chalName>, tagList,
				 * <description>, <points>); zoals hieronder. IMPORTANT: Ook
				 * challenges worden uit elkaar gehouden op basis van hun naam.
				 */
				m.addChallenge(j,j + "th challenge",(j + 1) * 25);
				/*
				 * Stap 4: voeg packages toe aan de module. Dit kan met de
				 * methode addPackage(<packName>), waar packName een String is.
				 * Het staat hier misschien een beetje raar in de loop, maar dat
				 * was gewoon gemakszucht.
				 */
				m.addPackage("Package Flee Flo Flum " + (j ^ 2));
				/*
				 * Stap 5: voeg eventueel opties toe aan de module. In de GUI
				 * zijn opties niet meer dan Strings. De lijst met opties is
				 * niets anders dan een HashMap<String, String>. Ik weet niet
				 * wat jij nog van plan was met de opties, maar relevante
				 * klassen kunnen dan OptionsComposite en OptionsCompListener
				 * zijn. Op dit moment voeg je zo een optie toe:
				 * addOption(<optionName>, <DefaultValue>); Hier wordt dus ook
				 * verschil gemaakt tussen opties op basis van hun naam. Het
				 * staat hier misschien een beetje raar in de loop, maar dat was
				 * gewoon gemakszucht.
				 */
				m.addOption(new Option("Option name " + j, "Value is relative"));
			}
			/*
			 * Stap 6: herhaal voor alle modules.
			 */
		}
		/*
		 * Stap 7: vergeet niet om de Modules als lijst op te slaan in
		 * ModsBookkeeper.
		 */
		return result;
	}

	/*
	 * Verder zijn er nog een aantal onderdelen van de GUI waar ik nog niks mee
	 * heb gedaan. Dit zijn vooral de knoppen: de Import knop op het eerste
	 * tabblad laat nu, wanneer je er op klikt een FileDialog zien. Daar kun je
	 * dan iets selecteren. Er wordt nu nog niks gedaan met die data, maar dat
	 * kun je aanpassen in de klasse importMouseListener. De tweede knop is de
	 * Browse knop op het tweede tabblad. Als je op deze knop (of op het
	 * textvlak ernaast) klikt, krijg je ook een filebrowser. Die zet nu het pad
	 * naar het geselecteerde bestand in het textvlak, maar doet daar verder nog
	 * niks mee. Dit is aan te passen in de klasse BrowseMouseListener. De
	 * downloadknop doet nog niks. Hij heeft wel een listener: DownLoadListener.
	 * Het linkeronderste panel is niet echt gedetailleerd omdat ik niet wist in
	 * hoeverre deze dingen nog van toepassing waren. Ten slotte zijn er de
	 * onderste drie knoppen Import, Generate en Export. Deze knoppen doen nog
	 * helemaal niks. Voor ieder van deze knoppen is al een listener klasse:
	 * voor Import is dat ConfigImportMouseListener, voor Generate is dat
	 * GenerateListener en voor Export is dat ConfigExportListener.
	 */

	/*
	 * Je roept de GUI aan als new GUIBuilder();. Zorg er dan wel voor dat er
	 * wat modules in je modulelijst staan (aangezien je nog niet kunt
	 * importeren). Nu staan alle dummymodules er nog in, dus die kun je
	 * gebruiken om een beetje rond te kijken. Ik denk dat je eerst je eigen
	 * spul moet aanroepen, omdat je de modules moet "inlezen" in mijn systeem.
	 * In ieder geval, als je met een standaardset wil starten. Als je alles wil
	 * importeren, kun je ook wel met de GUI beginnen waarschijnlijk. Ik hoop
	 * dat het te begrijpen is :P. En anders moet je maar even emailen ofzo.
	 */

}
