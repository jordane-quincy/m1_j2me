package bataille;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
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
	private Command commandSaveInfosJoueur;
	private RecordStore rs = null;

	public BatailleGUI() {
		// constructeur
		monDisplay = Display.getDisplay(this);
		formInfosJoueur = new Form("Informations joueur");
		formJeu = new Form("Jeu de bataille");
		CommandListener cl = new GestionEvenements();
		commandSaveInfosJoueur = new Command("Enregister", Command.SCREEN, 1);
		formInfosJoueur.addCommand(commandSaveInfosJoueur);

		formInfosJoueur.setCommandListener(cl);
		formJeu.setCommandListener(cl);
	}

	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// liberation de la memoire
		monDisplay = null;
		formInfosJoueur = null;
		formJeu = null;

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
			System.out.println("d : " + d);
			System.out.println("c : " + c);
			if (d == formInfosJoueur) {
				if (c == commandSaveInfosJoueur) {
					monDisplay.setCurrent(formJeu);
				}
			}
		}

	}

}