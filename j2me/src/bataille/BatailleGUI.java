package bataille;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
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

	Random random;
	private Hashtable enseigneCarte;
	private Hashtable valeurCarte;

	private Pile pileJoueur;
	private Pile pileIA;

	private Stack carteJoueurEnJeu;
	private Stack cartesIAEnJeu;

	public BatailleGUI() {
		// constructeur
		monDisplay = Display.getDisplay(this);
		formInfosJoueur = new Form("Informations joueur");
		formJeu = new Form("Jeu de bataille");
		cl = new GestionEvenements();
		icl = new GestionEvenements();
		commandSaveInfosJoueur = new Command("Enregister", Command.SCREEN, 1);
		commandTirerCarte = new Command("Tirer", Command.OK, 2);
		formInfosJoueur.addCommand(commandSaveInfosJoueur);

		formInfosJoueur.setCommandListener(cl);
		formJeu.setCommandListener(cl);

		random = new Random();
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

		Vector cartes = new Vector();
		for (Enumeration enseigneEnumeration = enseigneCarte.keys(); enseigneEnumeration.hasMoreElements();) {
			String enseigne = (String) enseigneEnumeration.nextElement();
			for (Enumeration valeurCarteEnumeration = valeurCarte.keys(); valeurCarteEnumeration.hasMoreElements();) {
				Integer valeur = ((Integer) valeurCarteEnumeration.nextElement());

				String enseigneDeLaCarte = (String) enseigneCarte.get(enseigne);
				String valeurDeLaCarte = (String) valeurCarte.get(valeur);

				String nomDuFichierImg = enseigneDeLaCarte + "_" + valeurDeLaCarte + ".png";
				cartes.addElement(
						new Carte(valeur.intValue(), valeurDeLaCarte, enseigne, enseigneDeLaCarte, nomDuFichierImg));
			}
		}

		// distribute all cards : half for player, half for ia
		Stack tempStackJoueur = new Stack();
		Stack tempStackIA = new Stack();
		carteJoueurEnJeu = new Stack();
		cartesIAEnJeu = new Stack();

		// to preserve how many cartes have to be splitted between players
		// (because cartes size will decrease each time)
		int nbCartes = cartes.size();
		for (int i = 0; i < nbCartes; i++) {
			System.out.println("cartes.size() : " + cartes.size());
			int indexRandom = 0;
			Carte carte = null;
			while (carte == null) {
				indexRandom = random.nextInt(cartes.size());
				carte = (Carte) cartes.elementAt(indexRandom);
			}
			cartes.removeElementAt(indexRandom);
			if (i % 2 == 0) {
				tempStackJoueur.addElement(carte);
			} else {
				tempStackIA.addElement(carte);
			}
		}
		pileJoueur = new Pile(tempStackJoueur);
		pileIA = new Pile(tempStackIA);
		System.out.println("cartesJoueur size : " + pileJoueur.size() + ", cartesIA size :" + pileIA.size());

		cartes = null; // free mem
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
			// Image imgStackJoueur =
			// Image.createImage(Image.createImage("cartes/" +
			// "rouge_Verso.png"), 0, 0, 46, 64, 0);
			formJeu.append(new ImageItem("Nb cartes : " + String.valueOf(pileJoueur.size()), null,
					ImageItem.LAYOUT_LEFT, null, ImageItem.PLAIN));
			// new TextField("Nb cartes : ", String.valueOf(pileJoueur.size()),
			// 15, TextField.UNEDITABLE));

			Carte carteJoueur = tirerUneCarte(pileJoueur);
			carteJoueurEnJeu.push(carteJoueur);
			Image imgCarteJoueur = Image.createImage(Image.createImage("cartes/" + carteJoueur.getPathImgFile()), 0, 0,
					46, 64, 0);

			// ajout de vide a gauche pour center la carte
			formJeu.append(new Spacer((monDisplay.getCurrent().getWidth() - imgCarteJoueur.getWidth()) / 2, 0));

			int indexFormImgCarteJoueur = formJeu.append(imgCarteJoueur);

			ImageItem imgTirerCarte = new ImageItem("Tirer carte", null, ImageItem.LAYOUT_CENTER, "");
			formJeu.append(imgTirerCarte);
			imgTirerCarte.addCommand(commandTirerCarte);
			imgTirerCarte.setItemCommandListener(icl);

			Carte carteIA = tirerUneCarte(pileIA);
			cartesIAEnJeu.push(carteIA);
			Image imgCarteIA = Image.createImage(Image.createImage("cartes/" + carteIA.getPathImgFile()), 0, 0, 46, 64,
					0);

			// ajout de vide a gauche pour center la carte
			formJeu.append(new Spacer((monDisplay.getCurrent().getWidth() - imgCarteIA.getWidth()) / 2, 0));

			formJeu.append(imgCarteIA);

			monDisplay.setCurrent(formJeu);

			Boolean isPlayerWin = carteJoueurGagne(carteJoueur, carteIA);
			if (isPlayerWin == null) {
				System.out.println("Bataille !");
				Alert alert = new Alert("Bataille !");
				alert.setType(AlertType.INFO);
				alert.setTimeout(5000);
				monDisplay.setCurrent(alert, formJeu);

				// FIXME: afficher la bataille au joueur (pas de popup ni de
				// toast en jme, si ?)
				// formJeu.get(indexFormImgCartesoueur).
				// imgCarteJoueur.getGraphics().fillRect(0, 0,
				// imgCarteJoueur.getWidth(), imgCarteJoueur.getHeight());

				// chaque joueur prend la carte du haut de sa pile et la met en
				// jeu face cachée avant un nouveau tirage
				carteJoueurEnJeu.push(pileJoueur.pop());
				cartesIAEnJeu.push(pileIA.pop());

				// FIXME: avec le sleep l'app est bien bloquée mais malgré le
				// setCurrent au dessus, on voit toujours les anciennes cartes
				// (celle du tour d'avant la bataille) :-( WTF ?!
				// try {
				// Thread.sleep(2000);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

				showJeu(player);
			} else {
				System.out.println("isPlayerWin : " + isPlayerWin);
				deplacerCarte(pileJoueur, carteJoueurEnJeu, pileIA, cartesIAEnJeu, isPlayerWin.booleanValue());
				System.out.println("cartes joueur : " + pileJoueur.size() + ", cartes ia : " + pileIA.size());
			}

			if (pileJoueur.size() == 0 || pileIA.size() == 0) {
				gestionFinDeJeu(pileJoueur.size() == 0, player);
			}

		} catch (IOException exception) {
			formJeu.append("erreur chargement image");
		}

		monDisplay.setCurrent(formJeu);
	}

	private void gestionFinDeJeu(boolean isPlayerWinTheGame, Personne joueur) {
		formJeu.deleteAll();

		formJeu.append(new TextField("Vainqueur : ",
				isPlayerWinTheGame ? joueur == null ? "Joueur" : joueur.getPrenom() : "IA", 25, TextField.UNEDITABLE));
	}

	private void deplacerCarte(Pile cartesJoueur, Stack cartesJoueurEnJeu, Pile cartesIA, Stack cartesIAEnJeu,
			boolean isPlayerWin) {
		if (isPlayerWin) {
			echangerCarte(cartesJoueur, cartesJoueurEnJeu, cartesIA, cartesIAEnJeu);
		} else {
			echangerCarte(cartesIA, cartesIAEnJeu, cartesJoueur, cartesJoueurEnJeu);
		}
	}

	private void echangerCarte(Pile pileAReduire, Stack cartesARetirer, Pile pileAAugmenter, Stack cartesAAjouter) {
		pileAReduire.retirerCarte(cartesARetirer);
		pileAAugmenter.ajouterCarte(cartesAAjouter);
	}

	private Carte tirerUneCarte(Pile stackDeCartes) {
		Random random = new Random();
		int indexCarteHasard = random.nextInt(stackDeCartes.size());
		Carte carte = stackDeCartes.obtenirCarte(indexCarteHasard);
		System.out.println("carte tirer : " + carte);
		return carte;
	}

	private Boolean carteJoueurGagne(Carte carteJoueur, Carte carteIA) {
		int valeurCarteJoueur = carteJoueur.getValeur();
		int valeurCarteIA = carteIA.getValeur();
		return valeurCarteJoueur == valeurCarteIA ? null : new Boolean(valeurCarteJoueur > valeurCarteIA);
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

	class GestionEvenements implements CommandListener, ItemCommandListener {
		// CommandListener
		public void commandAction(Command c, Displayable d) {
			if (d == formInfosJoueur) {
				if (c == commandSaveInfosJoueur) {
					showJeu(null);
				}
			}
		}

		// ItemCommandListener
		public void commandAction(Command c, Item item) {

			if (c == commandTirerCarte) {
				showJeu(null);
			}

		}

	}

}