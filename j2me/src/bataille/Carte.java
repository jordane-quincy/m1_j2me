package bataille;

public class Carte {

	private int valeur;
	private String valeurString;
	private String enseigne;
	private String enseigneString;
	private String pathImgFile;

	public Carte(int valeur, String valeurString, String enseigne, String enseigneString, String pathImgFile) {
		this.valeur = valeur;
		this.valeurString = valeurString;
		this.enseigne = enseigne;
		this.enseigneString = enseigneString;
		this.pathImgFile = pathImgFile;
	}

	/**
	 * @return the valeur
	 */
	public int getValeur() {
		return valeur;
	}

	/**
	 * @param valeur
	 *            the valeur to set
	 */
	public void setValeur(int valeur) {
		this.valeur = valeur;
	}

	public String getValeurString() {
		return valeurString;
	}

	public void setValeurString(String valeurString) {
		this.valeurString = valeurString;
	}

	/**
	 * @return the enseigne
	 */
	public String getEnseigne() {
		return enseigne;
	}

	/**
	 * @param enseigne
	 *            the enseigne to set
	 */
	public void setEnseigne(String enseigne) {
		this.enseigne = enseigne;
	}

	public String getEnseigneString() {
		return enseigneString;
	}

	public void setEnseigneString(String enseigneString) {
		this.enseigneString = enseigneString;
	}

	/**
	 * @return the pathImgFile
	 */
	public String getPathImgFile() {
		return pathImgFile;
	}

	/**
	 * @param pathImgFile
	 *            the pathImgFile to set
	 */
	public void setPathImgFile(String pathImgFile) {
		this.pathImgFile = pathImgFile;
	}

	/**
	 * Override toString()
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Carte " + this.valeurString + " de " + this.enseigne;
	}
}
