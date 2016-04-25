package bataille;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;

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
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class BatailleGUI extends MIDlet {

	private final String RMS_DB_NAME = "recordStoreName";

	private Display monDisplay;
	private Form formInfosJoueur;
	private Form formJeu;
	private CommandListener cl;
	private Command commandSaveInfosJoueur;
	private Command exit;
	private ItemCommandListener icl;
	private Command commandRetournerCarte;
	private Command commandPoserCarte;
	private Command commandRamasserCarte;
	private Command commandPoserCarteBataille;
	private Command newGame;
	private RecordStore rs = null;
	private Carte carteJoueurToReturn;
	private Carte carteIAToReturn;
	private boolean _isPlayerWin = false;

	Random random;
	private Hashtable enseigneCarte;
	private Hashtable valeurCarte;

	private Pile pileJoueur;
	private Pile pileIA;

	private Stack cartesEnJeu;

	private Personne player;
	private TextField nom;
	private TextField prenom;
	private Gauge age;
	private ChoiceGroup sexeChoiceGroup;

	public BatailleGUI() {
		// constructeur
		monDisplay = Display.getDisplay(this);
		formInfosJoueur = new Form("Informations du joueur");
		formJeu = new Form("Jeu de bataille");
		cl = new GestionEvenements();
		icl = new GestionEvenements();
		exit = new Command("Exit", Command.EXIT, 1);
		commandSaveInfosJoueur = new Command("Enregister", Command.SCREEN, 1);
		commandRetournerCarte = new Command("Retourner carte", Command.OK, 2);
		commandPoserCarte = new Command("Poser carte", Command.OK, 2);
		commandRamasserCarte = new Command("Ramasser carte", Command.OK, 2);
		commandPoserCarteBataille = new Command("Poser carte", Command.OK, 2);
		newGame = new Command("Nouvelle Partie", Command.SCREEN, 1);
		carteJoueurToReturn = null;
		carteIAToReturn = null;
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
		cartesEnJeu = new Stack();

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
				System.out.println("Joueur :" + carte.getValeurString() + " " + carte.getEnseigneString());
				tempStackJoueur.addElement(carte);
			} else {
				System.out.println("IA :" + carte.getValeurString() + " " + carte.getEnseigneString());
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

		System.out.println("Start bdd part (RMS)...");
		RMS_loadDB();

		player = RMS_findInDB();
		if (player != null) {
			System.out.println("Found player : " + player.getId());
		} else {
			System.err.println("Nobody found.");
		}

		showInfosJoueur(player);
	}

	private void showInfosJoueur(Personne player) {
		nom = new TextField("Nom", player == null ? "nom" : player.getNom(), 10, TextField.ANY);
		prenom = new TextField("Prenom", player == null ? "prenom" : player.getPrenom(), 10, TextField.ANY);
		age = new Gauge("Age (0-99)", true, 99, player == null ? 18 : player.getAge());
		sexeChoiceGroup = new ChoiceGroup("Sexe", Choice.EXCLUSIVE, Enum.sexe, null);

		formInfosJoueur.append(nom);
		formInfosJoueur.append(prenom);
		formInfosJoueur.append(age);

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
		formInfosJoueur.addCommand(exit);

		monDisplay.setCurrent(formInfosJoueur);
	}

	private void _beginingOfTheGame() {
		formJeu.deleteAll();
		formJeu.append(new StringItem("",
				"Vous êtes sur le point de commencer la partie, cliquer sur poser carte pour commencer !"));
		ImageItem imgPoserCarte = new ImageItem("Poser carte", null, ImageItem.LAYOUT_CENTER, "");
		formJeu.append(imgPoserCarte);
		formJeu.addCommand(exit);
		imgPoserCarte.addCommand(commandPoserCarte);
		imgPoserCarte.setItemCommandListener(icl);
		monDisplay.setCurrent(formJeu);
	}

	private void pushCard(boolean isBataille) {
		try {
			if (pileJoueur.size() == 0 || pileIA.size() == 0) {
				gestionFinDeJeu(pileJoueur.size() == 0, null);
			} else {
				System.out.println("Should stop");
				// Gestion poser card quand c'est en pleine bataille
				if (isBataille) {
					// Il faut ajouter une carte de chaque joueur aux cartes en
					// jeu
					cartesEnJeu.push(tirerUneCarte(pileJoueur));
					cartesEnJeu.push(tirerUneCarte(pileIA));
				}
				// On doit recheck si on a encore des carte parce qu'on en a
				// potentiellement poser avec une bataille !
				if (pileJoueur.size() == 0 || pileIA.size() == 0) {
					gestionFinDeJeu(pileJoueur.size() == 0, null);
				} else {
					System.out.println("Should Stop");
					// On va tirer les cartes mais on ne va afficher que le
					// verso
					// On tire la carte du joueur
					carteJoueurToReturn = tirerUneCarte(pileJoueur);
					// On tire la carte de l'IA
					carteIAToReturn = tirerUneCarte(pileIA);
					// On ajoute les cartes qu'on vient de tirer aux cartes en
					// jeu (cartes que le joueur qui va gagner va pouvoir
					// récupérer
					cartesEnJeu.push(carteJoueurToReturn);
					cartesEnJeu.push(carteIAToReturn);
					/// On prend le verso des cartes
					Image imgCarteJoueur = Image.createImage(Image.createImage("cartes/noir_Verso.png"), 0, 0, 46, 64,
							0);
					Image imgCarteIA = Image.createImage(Image.createImage("cartes/rouge_Verso.png"), 0, 0, 46, 64, 0);
					_showJeu(imgCarteJoueur, imgCarteIA, true, false, false, false, false);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void returnCard() {
		try {

			// Récupération de l'image de la carte joueur (tirer lors du
			// pushCard)
			Image imgCarteJoueur = Image
					.createImage(Image.createImage("cartes/" + carteJoueurToReturn.getPathImgFile()), 0, 0, 46, 64, 0);
			// Récupéation de l'image de la carte IA (tirer lors du pushCard)
			Image imgCarteIA = Image.createImage(Image.createImage("cartes/" + carteIAToReturn.getPathImgFile()), 0, 0,
					46, 64, 0);
			// Checker qui a gagné
			Boolean isPlayerWin = carteJoueurGagne(carteJoueurToReturn, carteIAToReturn);
			if (isPlayerWin == null) {
				// Gérer la bataille
				_showJeu(imgCarteJoueur, imgCarteIA, false, true, false, true, false);
			} else {
				_isPlayerWin = isPlayerWin.booleanValue();
				// Ce n'est pas une bataille, il faut ramasser les cartes
				System.out.println("isPlayerWin : " + isPlayerWin);
				System.out.println("cartes joueur : " + pileJoueur.size() + ", cartes ia : " + pileIA.size());
				_showJeu(imgCarteJoueur, imgCarteIA, false, true, false, false, isPlayerWin.booleanValue());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void takeCard() {
		deplacerCarte(pileJoueur, cartesEnJeu, pileIA, _isPlayerWin);
		// If it is not the end of the game
		_showJeu(null, null, false, false, true, false, false);
	}

	/**
	 * _showJeu will just show cards (images that we pass in arguments and add
	 * command depending of the moment of the game
	 * 
	 * @param imgCarteJoueur
	 * @param imgCarteIA
	 * @param pushCards
	 * @param returnCards
	 * @param takeCards
	 * @param bataille
	 * @param carteIAToReturn
	 * @param carteJoueurToReturn
	 */
	private void _showJeu(Image imgCarteJoueur, Image imgCarteIA, boolean pushCards, boolean returnCards,
			boolean takeCards, boolean bataille, boolean isPlayerWin) {
		// pour redessiner completement
		formJeu.deleteAll();

		// Affichage du nombre de cartes de l'IA
		StringItem nbCartesIA = new StringItem("Nombre de cartes de l'IA", Integer.toString(pileIA.size()));
		nbCartesIA.setLayout(StringItem.LAYOUT_LEFT);
		formJeu.append(nbCartesIA);

		// Affichage de la carte de l'ia
		if (imgCarteIA != null) {
			ImageItem imgItemCarteIA = new ImageItem("", imgCarteIA, ImageItem.LAYOUT_CENTER, "");
			formJeu.append(imgItemCarteIA);
		}

		// Affichage de l'action en fonction du moment du jeu
		// pushCards est vrai quand on vient de poser de carte, il faut donc
		// ensuite les retourner
		if (pushCards) {
			ImageItem imgRetournerCarte = new ImageItem("Retourner carte", null, ImageItem.LAYOUT_CENTER, "");
			imgRetournerCarte.setItemCommandListener(icl);
			imgRetournerCarte.addCommand(commandRetournerCarte);
			formJeu.append(imgRetournerCarte);
			formJeu.addCommand(exit);
		}
		// On vient de retourner les cartes
		if (returnCards) {
			// Gérer cas bataille
			if (bataille) {
				formJeu.append(new StringItem("", "Bataille !!! Posez une carte et retournez-en une autre !"));
				ImageItem imgPoserCarte = new ImageItem("Poser carte", null, ImageItem.LAYOUT_CENTER, "");
				imgPoserCarte.setItemCommandListener(icl);
				imgPoserCarte.addCommand(commandPoserCarteBataille);
				formJeu.append(imgPoserCarte);
				formJeu.addCommand(exit);
			}
			// Pas de bataille
			else {
				if (isPlayerWin) {
					formJeu.append(new StringItem("", "Vous avez gagner cette bataille !"));
				} else {
					formJeu.append(new StringItem("", "Vous avez perdu cette bataille..."));
				}
				ImageItem imgRamasserCarte = new ImageItem("Ramasser carte", null, ImageItem.LAYOUT_CENTER, "");
				imgRamasserCarte.setItemCommandListener(icl);
				imgRamasserCarte.addCommand(commandRamasserCarte);
				formJeu.append(imgRamasserCarte);
				formJeu.addCommand(exit);
			}
		}
		// On vient de ramasser les cartes
		if (takeCards) {
			formJeu.append(new StringItem("", "Next round ! Poser une carte !"));
			ImageItem imgPoserCarte = new ImageItem("Poser carte", null, ImageItem.LAYOUT_CENTER, "");
			imgPoserCarte.setItemCommandListener(icl);
			imgPoserCarte.addCommand(commandPoserCarte);
			formJeu.append(imgPoserCarte);
			formJeu.addCommand(exit);
		}

		// Affichage de la carte du joueur
		if (imgCarteJoueur != null) {
			ImageItem imgItemCarteJoueur = new ImageItem("", imgCarteJoueur,
					ImageItem.LAYOUT_NEWLINE_BEFORE | ImageItem.LAYOUT_CENTER, "");
			formJeu.append(imgItemCarteJoueur);
		}
		// Affichage du nombre de cartes du joueur
		StringItem nbCartesJoueur = new StringItem("Nombre de cartes du joueur", Integer.toString(pileJoueur.size()));
		nbCartesJoueur.setLayout(StringItem.LAYOUT_LEFT);
		formJeu.append(nbCartesJoueur);

		// Affichage de tous les éléments
		monDisplay.setCurrent(formJeu);
	}

	private void gestionFinDeJeu(boolean isPlayerLooseTheGame, Personne joueur) {
		formJeu.deleteAll();
		formJeu.append(new StringItem("Vainqueur : ",
				!isPlayerLooseTheGame ? joueur == null ? "Joueur" : joueur.getPrenom() : "IA"));
		formJeu.addCommand(newGame);
	}

	private void deplacerCarte(Pile cartesJoueur, Stack cartesEnJeu, Pile cartesIA, boolean isPlayerWin) {
		if (isPlayerWin) {
			ajouterCarteALaPile(cartesJoueur, cartesEnJeu);
		} else {
			ajouterCarteALaPile(cartesIA, cartesEnJeu);
		}
	}

	private void ajouterCarteALaPile(Pile pileAAugmenter, Stack cartesAAjouter) {
		pileAAugmenter.ajouterCarte(cartesAAjouter);
	}

	private Carte tirerUneCarte(Pile stackDeCartes) {
		// On ne doit pas prendre une carte au hasard
		// On doit prendre la dernière carte de la pile
		// et on doit mettre les cartes au début de la pile lorsqu'on les
		// ramasses
		/*
		 * Random random = new Random(); int indexCarteHasard =
		 * random.nextInt(stackDeCartes.size());
		 */
		Carte carte = stackDeCartes.pop();
		// Une fois qu'on a récupérer la carte, il faut la retirer de sa pile
		// pour ne pas la retirer lors de bataille !
		Stack tmpStack = new Stack();
		tmpStack.push(carte);
		stackDeCartes.retirerCarte(tmpStack);
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
			rs = RecordStore.openRecordStore(RMS_DB_NAME, true);
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

	private Personne RMS_findInDB() {
		Personne foundPeople = null;

		try {
			// pas de RecordFilter car on prend la première personne en bdd
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

	private void RMS_savePlayer() {
		Personne p = player;

		boolean isNewPlayer = (p == null);
		if (isNewPlayer) {
			p = new Personne();

			try {
				p.setId(rs.getNextRecordID());
			} catch (RecordStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("isNewPlayer " + isNewPlayer + ", p.getId() : " + p.getId());

		p.setNom(nom.getString());
		p.setPrenom(prenom.getString());
		p.setAge(age.getValue());
		p.setSexe(sexeChoiceGroup.getString(sexeChoiceGroup.getSelectedIndex()));

		// maj variable globale
		player = p;

		byte[] dataInBytes = p.serialise();
		try {
			if (isNewPlayer) {
				// insertion des nouvelles infos
				rs.addRecord(dataInBytes, 0, dataInBytes.length);
			} else {
				// maj des anciennes infos
				rs.setRecord(p.getId(), dataInBytes, 0, dataInBytes.length);
			}
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class GestionEvenements implements CommandListener, ItemCommandListener {
		// CommandListener
		public void commandAction(Command c, Displayable d) {
			if (d == formInfosJoueur) {
				if (c == commandSaveInfosJoueur) {
					RMS_savePlayer();
					_beginingOfTheGame();
				}
			}
			if (c == newGame) {
				// Start une nouvelle partie
				// Réinitialisation des tâches
				// Remove de la commande new game
				formJeu.removeCommand(newGame);
				System.out.println("New Game");
				initCartes();
				_beginingOfTheGame();
			}
			if (c == exit) {
				{
					try {
						destroyApp(false);
					} catch (MIDletStateChangeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					notifyDestroyed();
				}
			}
		}

		// ItemCommandListener
		public void commandAction(Command c, Item item) {

			if (c == commandRetournerCarte) {
				returnCard();
			}
			if (c == commandPoserCarte) {
				pushCard(false);
			}
			if (c == commandPoserCarteBataille) {
				pushCard(true);
			}
			if (c == commandRamasserCarte) {
				takeCard();
			}

		}

	}

}