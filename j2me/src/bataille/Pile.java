package bataille;

import java.util.Stack;
import java.util.Vector;

public class Pile {

	private Vector vecteur;

	public Pile(Stack cartes) {
		vecteur = new Vector();
		ajouterCarte(cartes);
	}

	public void retirerCarte(Stack cartesEnJeu) {
		while (!cartesEnJeu.empty()) {
			Carte carteEnJeu = (Carte) cartesEnJeu.pop();
			vecteur.removeElement(carteEnJeu);
		}
	}

	public void ajouterCarte(Stack cartesEnJeu) {
		while (!cartesEnJeu.empty()) {
			Carte carteEnJeu = (Carte) cartesEnJeu.pop();
			vecteur.insertElementAt(carteEnJeu, 0);
		}
	}

	public Carte obtenirCarte(int index) {
		return (Carte) vecteur.elementAt(index);
	}

	public Carte pop() {
		return (Carte) vecteur.elementAt(vecteur.size() - 1); // -1 car l'index
																// commence a 0
	}

	public int size() {
		return vecteur.size();
	}
}
