import java.util.ArrayList;

import javax.swing.JOptionPane;


public class Dreiecke {
	
	int anzahl_der_dreiecke = 0;
	ArrayList<Linie> linien = new ArrayList<>();
	ArrayList<Linie> bearbeitete_linien = new ArrayList<Linie>();
	ArrayList<Schnittpunkt> schnittpunkte = new ArrayList<Schnittpunkt>();
	
	/*
	 * startet das Programm
	 */
	public static void main(String args[]) {
		new Dreiecke();
	}
	
	/*
	 * Konstruktor
	 */
	public Dreiecke() {
		String input = JOptionPane.showInputDialog("");
		linien = bekommeLinienVomInput(input);
		
		for(Linie linie: linien) {
			ArrayList<Schnittpunkt> schnittpunkte_dieser_linie = bekommeAlleSchnittpunkteDieserLinie(linie, bearbeitete_linien);
			System.out.println("Anzahl der neuen Schnitpunkte: " + schnittpunkte_dieser_linie.size());
			schnittpunkte.addAll(schnittpunkte_dieser_linie);
			
			for(int i = 0; i < schnittpunkte_dieser_linie.size(); i++) {
				for(int j = i+1; j < schnittpunkte_dieser_linie.size(); j++) {
					if(neuesDreieckEntsteht(schnittpunkte_dieser_linie.get(i), schnittpunkte_dieser_linie.get(j))) {
						System.out.println(schnittpunkte.indexOf(schnittpunkte_dieser_linie.get(i)) + ", " + schnittpunkte.indexOf(schnittpunkte_dieser_linie.get(j)) + "},");
						anzahl_der_dreiecke++;
					}
				}
			}
			
			bearbeitete_linien.add(linie);
		}
		
		System.out.println(anzahl_der_dreiecke);
	}
	
	/*
	 * füllt die Liste der Linien aus den Daten, die in 'input' stehen
	 */
	public ArrayList<Linie> bekommeLinienVomInput(String input) {
		ArrayList<Linie> ergebnis = new ArrayList<Linie>();
		
		int anzahl_der_linien = Integer.parseInt( input.substring(0, input.indexOf(' ')) );
		input = input.substring(input.indexOf(' ')+1, input.length() );
		input += " ";
		
		for(int i = 0; i < anzahl_der_linien; i++) {
			float[] koordinaten = new float[4];
			for(int j = 0; j < 4; j++) {
				int leertaste = input.indexOf(' ');
				koordinaten[j] = Float.parseFloat( input.substring(0, leertaste ));
				input = input.substring(leertaste+1, input.length());
			}
			
			ergebnis.add(new Linie(koordinaten[0], koordinaten[1], koordinaten[2], koordinaten[3]));
		}
		
		return ergebnis;
	}
	
	/*
	 * liefert eine liste mit allen Schnittpunkten zurück, die die neue Linien
	 * mit den bereits vorhandenen Linien hat
	 */
	public ArrayList<Schnittpunkt> bekommeAlleSchnittpunkteDieserLinie(Linie neue_linie, ArrayList<Linie> linien) {
		ArrayList<Schnittpunkt> ergebnis = new ArrayList<Schnittpunkt>();
		
		for(Linie linie: linien) {
			if(!sindParallel(neue_linie, linie)) {
				Schnittpunkt schnittpunkt = bekommeSchnittpunkt(neue_linie, linie);
				if(schnittpunkt != null) {
					ergebnis.add(schnittpunkt);
					System.out.println("Schnittpunkt: " + schnittpunkt.x + "|" + schnittpunkt.y);
				}
			}
			else {
				System.out.println("parallel");
			}
		}
		
		return ergebnis;
	}
	
	/*
	 * berechnet den Schnittpunkt der beiden übergebenen Linien
	 * wenn der Punkt nicht auf den Strecken liegt, dann wird null zurückgegeben
	 */
	public Schnittpunkt bekommeSchnittpunkt(Linie a, Linie b) {
		Schnittpunkt ergebnis;
		
		float z1 = (b.stuetzvektor[0] - a.stuetzvektor[0]) / a.richtungsvektor[0];
		float z2 = (b.stuetzvektor[1] - a.stuetzvektor[1]) / a.richtungsvektor[1];
		float n1 = b.richtungsvektor[1] / a.richtungsvektor[1];
		float n2 = b.richtungsvektor[0] / a.richtungsvektor[0];
		if(a.richtungsvektor[0] == 0) {
			z1 = n2 = 0.0f;
		}
		if(a.richtungsvektor[1] == 0) {
			z2 = n1 = 0.0f;
		}
		float s = (z1 - z2) / (n1 - n2);
		if(n1 - n2 == 0) {
			s = 0.0f;
		}
		
		float r = (b.stuetzvektor[0] - a.stuetzvektor[0] + s * b.richtungsvektor[0]) / a.richtungsvektor[0];
		if(a.richtungsvektor[0] == 0) {
			r = 0.0f;
		}
		
		float x = b.stuetzvektor[0] + s * b.richtungsvektor[0];
		float y = b.stuetzvektor[1] + s * b.richtungsvektor[1];
		
		System.out.println("TRIAL: " + x + "|" + y);
		
		ergebnis = new Schnittpunkt(x, y);
		ergebnis.linien.add(a);
		ergebnis.linien.add(b);
		
		if(!a.enthältPunkt(x, y) || !b.enthältPunkt(x, y)) {
			return null;
		}
		
		return ergebnis;
	}
	
	/*
	 * entscheidet ob, die beiden übergebenen Geraden parallel sind
	 */
	public boolean sindParallel(Linie a, Linie b) {
		return (a.richtungsvektor[0] * b.richtungsvektor[1]) == (b.richtungsvektor[0] * a.richtungsvektor[1]);
	}
	
	/*
	 * entscheidet ob die beiden übergeben Schnittpunkte ein Dreieck mit einem
	 * bereits existierenden Punkt bilden
	 */
	public boolean neuesDreieckEntsteht(Schnittpunkt a, Schnittpunkt b) {
		ArrayList<Linie> unterschiedliche_linien = bekommeUnterschiedlicheLinien(a, b);
		for(Schnittpunkt schnittpunkt: schnittpunkte) {
			if(schnittpunkt.verbindetDieseLinien(unterschiedliche_linien)) {
				System.out.print("{" + schnittpunkte.indexOf(schnittpunkt) + ", ");
				return true;
			}
		}
		return false;
	}
	
	/*
	 * liefert eine Liste von den Linien zurück, die nicht durch beiden Schnittpunkte gehen
	 */
	public ArrayList<Linie> bekommeUnterschiedlicheLinien(Schnittpunkt a, Schnittpunkt b) {
		ArrayList<Linie> ergebnis = new ArrayList<Linie>();
		
		for(int i = 0; i < 2; i ++) {
			for(int j = 0; j < 2; j++) {
				if(a.linien.get(i).equals( b.linien.get(j))) {
					ergebnis.add(a.linien.get(1-i)); //1 -> 0, 0 -> 1
					ergebnis.add(b.linien.get(1-j));
				}
			}
		}
		
		return ergebnis;
	}
}

class Linie {
	float[] stuetzvektor = new float[2];
	float[] richtungsvektor = new float[2];
	
	public Linie(float ax, float ay, float bx, float by) {
		stuetzvektor = new float[] {ax, ay};
		richtungsvektor = new float[] {bx-ax, by-ay};
	}
	
	public float getX(float r) {
		return stuetzvektor[0] + r * richtungsvektor[0];
	}
	
	public float getY(float r) {
		return stuetzvektor[1] + r * richtungsvektor[1];
	}
	
	/*
	 * entscheidet ob sich der übergebene Punkt auf der Linie befindet
	 */
	public boolean enthältPunkt(float x, float y) {
		if((x - getX(0)) * (x - getX(1)) > 0) {
			return false;
		}
		if((y - getY(0)) * (y - getY(1)) > 0) {
			return false;
		}
		return true;
	}
}

class Schnittpunkt {
	float x, y;
	ArrayList<Linie> linien = new ArrayList<Linie>();
	
	public Schnittpunkt(float x, float y, ArrayList<Linie> linien) {
		this.x = x;
		this.y = y;
		this.linien = linien;
	}
	
	public Schnittpunkt(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/*
	 * entscheidet ob dieser Punkt die übergebenen Linien verbindet
	 */
	public boolean verbindetDieseLinien(ArrayList<Linie> linien) {
		if(this.linien.get(0).equals( linien.get(0)) && this.linien.get(1).equals( linien.get(1))) {
			return true;
		}
		if(this.linien.get(0).equals( linien.get(1)) && this.linien.get(1).equals( linien.get(0))) {
			return true;
		}
		return false;
	}
}
