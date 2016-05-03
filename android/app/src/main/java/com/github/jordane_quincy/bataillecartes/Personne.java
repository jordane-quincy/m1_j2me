package com.github.jordane_quincy.bataillecartes;

import java.io.Serializable;

/**
 * Created by jordane on 03/05/2016.
 */
public class Personne implements Serializable {

    static final long serialVersionUID = 42L;

        private int id;
        private String nom;
        private String prenom;
        private int age;
        private String sexe;

        public Personne() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }
/*
        static Personne initialise(byte[] data) {
            Personne p = new Personne();

            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bis);

            try {
                p.setId(dis.readInt());
                p.setNom(dis.readUTF());
                p.setPrenom(dis.readUTF());
                p.setAge(dis.readInt());
                p.setSexe(dis.readUTF());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return p;
        }

        byte[] serialise() {
            byte[] data = null;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            try {
                dos.writeInt(this.getId());
                dos.writeUTF(this.getNom());
                dos.writeUTF(this.getPrenom());
                dos.writeInt(this.getAge());
                dos.writeUTF(this.getSexe());

                data = bos.toByteArray();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return data;
        }
*/
        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getSexe() {
            return sexe;
        }

        public void setSexe(String sexe) {
            this.sexe = sexe;
        }

    /**
     * Override toString()
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Personne id " + this.id + "\n nom : " + this.nom + "\n prenom : " + this.prenom+ "\n age : " + this.age+ "\n sexe : " + this.sexe;
    }

}
