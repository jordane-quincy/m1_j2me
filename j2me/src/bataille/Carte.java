package bataille;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Carte {

	private int valeur;
	private String valeurString;
	private String enseigne;
	private String enseigneString;
	private String pathImgFile;

	private Carte() {
		
	}
	public Carte(int valeur, String valeurString, String enseigne, String enseigneString, String pathImgFile) {
		this.valeur = valeur;
		this.valeurString = valeurString;
		this.enseigne = enseigne;
		this.enseigneString = enseigneString;
		this.pathImgFile = pathImgFile;
	}
	
	static Carte initialise(byte[] data) {
		Carte c = new Carte();

		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bis);

		try {
			c.setValeur(dis.readInt());
			c.setValeurString(dis.readUTF());
			c.setEnseigne(dis.readUTF());
			c.setEnseigneString(dis.readUTF());
			c.setPathImgFile(dis.readUTF());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return c;
	}

	byte[] serialise() {
		byte[] data = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		try {
			dos.writeInt(this.getValeur());
			dos.writeUTF(this.getValeurString());
			dos.writeUTF(this.getEnseigne());
			dos.writeUTF(this.getEnseigneString());
			dos.writeUTF(this.getPathImgFile());
			data = bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;
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
