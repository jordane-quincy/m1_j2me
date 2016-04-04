package bataille;

import java.io.IOException;
import java.util.Date;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Ticker;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class BatailleGUI extends MIDlet {

	private Display monDisplay;
	private Form form;
	private Ticker ticker;//
	private RecordStore rs = null;

	public BatailleGUI() {
		// constructeur
		monDisplay = Display.getDisplay(this);
		form = new Form("Form principal");
		ticker = new Ticker("Ticker message");
		form.setTicker(ticker);
	}

	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// liberation de la memoire
		monDisplay = null;
		form = null;
		ticker = null;
		System.out.println("fin de l'application");
	}

	protected void startApp() throws MIDletStateChangeException {
		// lancement

		System.out.println("Demo start ...");
		// Start program here

		form.append(new Gauge("Age (0-10)", true, 10, 2));
		try {
			form.append(Image.createImage("java_black.png"));
		} catch (IOException exception) {
			form.append("erreur chargement image");
		}

		form.append(new TextField("label", "text", 10, TextField.ANY));

		form.append(new ChoiceGroup("Sexe", Choice.EXCLUSIVE, new String[] { "Homme", "Femme" }, null));

		DateField dateField = new DateField("date :", DateField.DATE);
		dateField.setDate(new Date());
		form.append(dateField);

		monDisplay.setCurrent(form);

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

		System.out.println("Close bdd part (RMS)...");
		RMS_closeDB();
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

}