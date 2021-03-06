package com.JayPi4c.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * @author JayPi4c
 * @version 1.0.0
 */
public class Init {
	/**
	 * Die statische Funktion begin() &uumlberpr&uumlft zu Beginn, ob der "data"
	 * Ordner existiert. Darauf folgend werden alle "Coinages.co" Dateien auf
	 * vorhanden sein &uumlberpr&uumlft. Ist eine Datei nicht vorhanden, wird diese
	 * generiert. Sollte die Datei vorhanden sein, wird die Datei auf Fehler
	 * &uumlberpr&uumlft, die das Programm evtl. zum Absturz bringen k&oumlnnen.
	 * Verschiedene Fehler sorgen f&uumlr verschiedene Fehlermeldungen:
	 * <p>
	 * <li>Ist die Datei leer, dann wird der Fehlercode 0 ausgegeben
	 * <li>Enth&aumllt eine Zeile nicht 8 Elemente, so wird der Fehlercode 1
	 * ausgegeben. Die 8 Elemente stehen f&uumlr 8 M&uumlnzen pro Jahr (1ct, 2ct,
	 * 5ct, 10ct, 20ct, 50ct, 1&euro, 2&euro). Des Weiteren beinhaltet ein Element
	 * noch eine Zahl f&uumlr das Land, den CountryKey, und eine Zahl, die
	 * wiederspiegelt, ob das Element im Besitz des Users ist.
	 * <li>Besteht ein Element nicht aus 3 Teilen, so wir der Fehlercode 2
	 * ausgegeben.
	 * <li>Entspricht der CountryKey nicht der Zeile in der Datei, so wird der
	 * Fehlercode 3 ausgegeben.
	 * <p>
	 * Sollte ein Fehler erkannt werden, so wird die Datei als Fehlerhaft markiert
	 * und die Datei wird neu generiert, jedoch wird versucht, so viele
	 * Informationen zu retten, wie es nur m&oumlglich ist. Dies geschieht durch die
	 * {@link com.JayPi4c.utils.Util#rescueData(File file, int year) rescueData}
	 * Methode aus der Util-Klasse.
	 * <p>
	 * Gibt es keine Fehler oder die Daten wurde gerettet, so wird abschlie&szligend
	 * die Information ausgegeben, dass die Datei erreichbar ist.
	 * 
	 * 
	 * @since 1.0.0
	 */
	public static void begin() {
		File dataDir = new File(Util.getExecutionPath() + "/data");
		if (!dataDir.exists())
			dataDir.mkdir();

		for (int i = 1999; i <= 2017; i++) {
			File CoinRegistry = new File(Util.getPath(i));
			// System.out.println("Coinregistry" + i + " exists: " + CoinRegistry.exists());
			// Util.log.info("Coinregistry" + i + " exists: " + CoinRegistry.exists());
			if (!CoinRegistry.exists()) {
				// System.out.println("generate: " + CoinRegistry.getAbsolutePath());
				Util.log.info("generate: " + CoinRegistry.getAbsolutePath());
				try {
					Util.genRegistry(i);
				} catch (IOException e) {
					Util.log.info("Could not generate registry!");
				}
			} else {
				boolean damaged = false;

				if (CoinRegistry.length() == 0) {
					// System.out.println("file is damaged!");
					// System.out.println("Errocode: 0; File is empty");
					Util.log.info("file is damaged!");
					Util.log.info("Errocode: 0; File is empty");
					damaged = true;
					try {
						Util.genRegistry(i);
					} catch (IOException e) {
						Util.log.info("Could not generate registry!");
					}
				}
				if (!damaged) {
					BufferedReader CR = null;
					try {
						CR = new BufferedReader(new FileReader(CoinRegistry));
					} catch (FileNotFoundException e) {
						Util.log.info("Could not find registry file");
					}
					String[] lines = new String[Util.getMembersFromYear(i)];
					for (int j = 0; j < Util.getMembersFromYear(i); j++) {
						try {
							lines[j] = CR.readLine();
						} catch (IOException e) {
							e.printStackTrace();
						}

						String[] str = lines[j].split(";");
						if (str.length != 8) {
							// System.out.println("file is damaged!");
							// System.out.println("Errorcode: 1; Line: " + j);
							Util.log.info("file is damaged!");
							Util.log.info("Errorcode: 1; Line: " + j);
							damaged = true;
							break;
						}

						if (!damaged) {
							for (int k = 0; k < str.length; k++) {
								String[] part = str[k].split(",");
								if (part.length != 3) {
									// System.out.println("file is damaged!");
									// System.out.println("Errorcode: 2; Line: " + (j + 1));
									Util.log.info("file is damaged!");
									Util.log.info("Errorcode: 2; Line: " + (j + 1));
									damaged = true;
									break;
								}
								if (!part[0].equals("" + j)) {
									// System.out.println("file is damaged!");
									// System.out.println("Errorcode: 3; Line: " + (j + 1));
									Util.log.info("file is damaged!" + "\n" + "Errorcode: 3; Line: " + (j + 1));
									damaged = true;
									break;
								}
								double val = Double.parseDouble(part[1]);
								double target = Util.getValueFromMultiplicator(k);
								if (val != target) {
									// System.out.println("Error");
									Util.log.info("file is damaged!" + "\n" + "Errorcode: 4; Line " + (j + 1)
											+ "; value:" + "\n" + "\tfound: " + val + "\n" + "\texpected: " + target);
								}
							}
							if (damaged)
								break;
						}
					}

					try {
						CR.close();
					} catch (IOException e) {
						Util.log.info("Error on closing the reader!");
					}
					if (damaged)
						try {
							Util.rescueData(CoinRegistry, i);
						} catch (IOException e) {
							Util.log.info("Could not rescue data!");
						}
				}
			}
			// System.out.println("'coinages" + i + ".co' is accessible");
			// Util.log.info("'coinages" + i + ".co' is accessible");
		}
	}

}
