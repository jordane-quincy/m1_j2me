package bataille;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Stack;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class BatailleGUI extends MIDlet {

	private Display monDisplay;
	private Form formInfosJoueur;
	private Form formJeu;
	private CommandListener cl;
	private Command commandSaveInfosJoueur;
	private ItemCommandListener icl;
	private Command commandTirerCarte;
	private RecordStore rs = null;

	private Hashtable enseigneCarte;
	private Hashtable valeurCarte;
	private Stack cartes;

	private final String SEPARATEUR = "_";

	public BatailleGUI() {
		// constructeur
		monDisplay = Display.getDisplay(this);
		formInfosJoueur = new Form("Informations joueur");
		formJeu = new Form("Jeu de bataille");
		cl = new GestionEvenements();
		icl = new GestionItemEvenements();
		commandSaveInfosJoueur = new Command("Enregister", Command.SCREEN, 1);
		commandTirerCarte = new Command("Tirer", Command.OK, 2);
		formInfosJoueur.addCommand(commandSaveInfosJoueur);

		formInfosJoueur.setCommandListener(cl);
		formJeu.setCommandListener(cl);

		initCartes();

	}

	private void initEnseigneCarte() {
		enseigneCarte = new Hashtable();
		enseigneCarte.put("Coeur", "coeur");
		enseigneCarte.put("Carreau", "carreau");
		enseigneCarte.put("Trèfle", "trefle");
		enseigneCarte.put("Pique", "pique");
	}

	private void initValeurCarte() {
		valeurCarte = new Hashtable();
		valeurCarte.put(new Integer(2), "Deux");
		valeurCarte.put(new Integer(3), "Trois");
		valeurCarte.put(new Integer(4), "Quatre");
		valeurCarte.put(new Integer(5), "Cinq");
		valeurCarte.put(new Integer(6), "Six");
		valeurCarte.put(new Integer(7), "Sept");
		valeurCarte.put(new Integer(8), "Huit");
		valeurCarte.put(new Integer(9), "Neuf");
		valeurCarte.put(new Integer(10), "Dix");
		valeurCarte.put(new Integer(11), "Valet");
		valeurCarte.put(new Integer(12), "Dame");
		valeurCarte.put(new Integer(13), "Roi");
		valeurCarte.put(new Integer(14), "As");
	}

	private void initCartes() {
		initEnseigneCarte();
		initValeurCarte();

		cartes = new Stack();
		for (Enumeration enseigneEnumeration = enseigneCarte.keys(); enseigneEnumeration.hasMoreElements();) {
			String enseigne = (String) enseigneEnumeration.nextElement();
			for (Enumeration valeurCarteEnumeration = valeurCarte.keys(); valeurCarteEnumeration.hasMoreElements();) {
				Integer valeur = ((Integer) valeurCarteEnumeration.nextElement());

				String enseigneDeLaCarte = (String) enseigneCarte.get(enseigne);
				String valeurDeLaCarte = (String) valeurCarte.get(valeur);

				String nomDuFichierImg = enseigneDeLaCarte + "_" + valeurDeLaCarte + ".png";
				// System.out.println("nomDuFichierImg : " + nomDuFichierImg);
				cartes.addElement(nomDuFichierImg);

			}
		}
	}

	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// liberation de la memoire
		monDisplay = null;
		formInfosJoueur = null;
		formJeu = null;

		enseigneCarte = null;
		valeurCarte = null;

		System.out.println("Close bdd part (RMS)...");
		RMS_closeDB();

		System.out.println("fin de l'application");
	}

	protected void startApp() throws MIDletStateChangeException {
		System.out.println("Lancement ...");
		/*
		 * formInfosJoueur.append(new Gauge("Age (0-10)", true, 10, 2)); try {
		 * formInfosJoueur.append(Image.createImage("java_black.png")); } catch
		 * (IOException exception) { formInfosJoueur.append(
		 * "erreur chargement image"); }
		 * 
		 * formInfosJoueur.append(new TextField("label", "text", 10,
		 * TextField.ANY));
		 * 
		 * formInfosJoueur.append(new ChoiceGroup("Sexe", Choice.EXCLUSIVE, new
		 * String[] { "Homme", "Femme" }, null));
		 * 
		 * DateField dateField = new DateField("date :", DateField.DATE);
		 * dateField.setDate(new Date()); formInfosJoueur.append(dateField);
		 * 
		 * monDisplay.setCurrent(formInfosJoueur);
		 */
		System.out.println("Start bdd part (RMS)...");
		RMS_loadDB();

		//
		// Personne p1 = new Personne();
		// p1.setId(0);
		// p1.setNom("Quincy");
		// RMS_addRow(p1);
		//
		// Personne p2 = new Personne();
		// p2.setId(1);
		// p2.setNom("Duriez");
		// RMS_addRow(p2);

		Personne finded = RMS_searchDB("Duriez");
		if (finded != null) {
			System.out.println("Found : " + finded.getId());
		} else {
			System.err.println("J-B not found!");
		}

		Personne player = RMS_getPlayerFromDB();

		showInfosJoueur(player);

	}

	private void showInfosJoueur(Personne player) {
		formInfosJoueur.append(new TextField("Nom", player == null ? "nom" : player.getNom(), 10, TextField.ANY));
		formInfosJoueur
				.append(new TextField("Prenom", player == null ? "prenom" : player.getPrenom(), 10, TextField.ANY));
		formInfosJoueur.append(new Gauge("Age (0-99)", true, 99, player == null ? 18 : player.getAge()));

		ChoiceGroup sexeChoiceGroup = new ChoiceGroup("Sexe", Choice.EXCLUSIVE, Enum.sexe, null);
		if (player != null) {
			int itemIndexToSelect = 0;
			for (int i = 0; i < sexeChoiceGroup.size(); i++) {
				String choiceElement = sexeChoiceGroup.getString(i);
				if (choiceElement.equals(player.getSexe())) {
					itemIndexToSelect = i;
					// no need to continue the loop (sexe found)
					break;
				}
			}
			sexeChoiceGroup.setSelectedIndex(itemIndexToSelect, true);
		}
		formInfosJoueur.append(sexeChoiceGroup);

		formInfosJoueur.addCommand(commandSaveInfosJoueur);

		monDisplay.setCurrent(formInfosJoueur);
	}

	private void showJeu(Personne player) {
		// pour redessiner completement
		formJeu.deleteAll();

		try {
			String carteJoueur = tirerUneCarte();
			Image imgCarteJoueur = Image.createImage(Image.createImage("cartes/" + carteJoueur), 0, 0, 32, 64, 0);

			// ajout de vide a gauche pour center la carte
			formJeu.append(new Spacer((monDisplay.getCurrent().getWidth() - imgCarteJoueur.getWidth()) / 2,
					(monDisplay.getCurrent().getHeight() - imgCarteJoueur.getHeight()) / 2));

			formJeu.append(imgCarteJoueur);

			ImageItem imgTirerCarte = new ImageItem("Tirer carte", null, ImageItem.LAYOUT_CENTER, "");
			formJeu.append(imgTirerCarte);
			imgTirerCarte.addCommand(commandTirerCarte);
			imgTirerCarte.setItemCommandListener(icl);

			String carteIA = tirerUneCarte();
			Image imgCarteIA = Image.createImage(Image.createImage("cartes/" + carteIA), 0, 0, 32, 64, 0);

			// ajout de vide a gauche pour center la carte
			formJeu.append(new Spacer((monDisplay.getCurrent().getWidth() - imgCarteIA.getWidth()) / 2,
					(monDisplay.getCurrent().getHeight() - imgCarteIA.getHeight()) / 2));

			formJeu.append(imgCarteIA);

			boolean isPlayerWin = carteJoueurGagne(carteJoueur, carteIA);
			System.out.println("isPlayerWin : " + isPlayerWin);

		} catch (IOException exception) {
			formJeu.append("erreur chargement image");
		}

		monDisplay.setCurrent(formJeu);
	}

	private String tirerUneCarte() {
		Random random = new Random();
		// random de 0 (inclus) a X (exclus) donc +1 pour qu'il soit inclus
		/*
		 * int indexEnseigneHasard = random.nextInt(enseigneCarte.size() + 1);
		 * 
		 * Enumeration enseigneE = enseigneCarte.elements(); String enseigneKey
		 * = null; for (int i = 0; i <= indexEnseigneHasard; i++) { enseigneKey
		 * = enseigneE.nextElement(); } String enseigne = (String)
		 * enseigneCarte.get(enseigneKey);
		 * 
		 * int indexCarteHasard = random.nextInt(valeurCarte.size() + 1);
		 * Enumeration carteE = valeurCarte.elements(); Integer carteKey = null;
		 * for (int i = 0; i <= indexCarteHasard; i++) { carteKey =
		 * carteE.nextElement(); } String carte = (String)
		 * valeurCarte.get(carteKey);
		 */

		int indexCarteHasard = random.nextInt(cartes.size());
		String carte = (String) cartes.elementAt(indexCarteHasard);
		System.out.println("carte tirer : " + carte);
		return carte;
	}

	private boolean carteJoueurGagne(String carteJoueur, String carteIA) {
		return getValeurDepuisCarte(carteJoueur) > getValeurDepuisCarte(carteIA);
	}

	private int getValeurDepuisCarte(String carte) {
		int indexDebutValeur = carte.indexOf(SEPARATEUR) + SEPARATEUR.length();
		int indexFinValeur = carte.indexOf(".");

		String cleValeur = carte.substring(indexDebutValeur, indexFinValeur);
		System.out.println("cleValeur : " + cleValeur);
		return ((Integer) valeurCarte.get(cleValeur)).intValue();
	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	private void RMS_loadDB() {
		try {
			rs = RecordStore.openRecordStore("recordStoreName", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void RMS_closeDB() {
		try {
			rs.closeRecordStore();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Personne RMS_searchDB(final String nomRecherche) {

		Personne foundPeople = null;

		RecordFilter rf = new RecordFilter() {

			public boolean matches(byte[] candidateData) {
				Personne pInDb = Personne.initialise(candidateData);

				return nomRecherche.equalsIgnoreCase(pInDb.getNom());
			}
		};

		try {
			RecordEnumeration enum = rs.enumerateRecords(rf, null, false);
			if (enum.hasNextElement()) {
				byte[] personFoundData = enum.nextRecord();
				Personne personFound = Personne.initialise(personFoundData);
				foundPeople = personFound;
			}
		} catch (Exception e) { // TODO Auto-generated catch block
								// e.printStackTrace();
		}

		return foundPeople;
	}

	private Personne RMS_getPlayerFromDB() {

		Personne foundPeople = null;

		try {
			RecordEnumeration enum = rs.enumerateRecords(null, null, false);
			if (enum.hasNextElement()) {
				byte[] personFoundData = enum.nextRecord();
				Personne personFound = Personne.initialise(personFoundData);
				foundPeople = personFound;
			}
		} catch (Exception e) { // TODO Auto-generated catch block
								// e.printStackTrace();
		}

		return foundPeople;
	}

	private void RMS_addRow(Personne p) {
		if (p == null) {
			System.err.println("No personne to store");
		} else {
			byte[] dataInBytes = p.serialise();
			try {
				rs.addRecord(dataInBytes, 0, dataInBytes.length);
			} catch (RecordStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class GestionEvenements implements CommandListener {
		public void commandAction(Command c, Displayable d) {
			if (d == formInfosJoueur) {
				if (c == commandSaveInfosJoueur) {
					showJeu(null);
				}
			}
		}

	}

	class GestionItemEvenements implements ItemCommandListener {

		public void commandAction(Command c, Item item) {

			if (c == commandTirerCarte) {
				showJeu(null);
			}

		}

	}

}