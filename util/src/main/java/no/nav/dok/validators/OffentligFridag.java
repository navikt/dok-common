package no.nav.dok.validators;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OffentligFridag {
		private OffentligFridag() {
			//Privat constructor for å hindre instanser.
		}

		public static boolean erOffentligFridag(LocalDate dato) {
			List<LocalDate> offentligFridager = finnBevegeligeOffentligFridagerUtenHelgPerAAr(dato.getYear());

			return offentligFridager.stream().anyMatch(dato::isEqual);

		}

		private static List<LocalDate> finnBevegeligeOffentligFridagerUtenHelgPerAAr(int aar) {
			List<LocalDate> bevegeligeOffentligFridager = new ArrayList<>();

			// legger til de satte offentligFridagene
			bevegeligeOffentligFridager.add(LocalDate.of(aar, 1, 1));
			bevegeligeOffentligFridager.add(LocalDate.of(aar, 5, 1));
			bevegeligeOffentligFridager.add(LocalDate.of(aar, 5, 17));
			bevegeligeOffentligFridager.add(LocalDate.of(aar, 12, 25));
			bevegeligeOffentligFridager.add(LocalDate.of(aar, 12, 26));

			// regner ut påskedag
			LocalDate paaskedag = utledPaaskedag(aar);

			// søndag før påske; Palmesøndag
			bevegeligeOffentligFridager.add(paaskedag.minusDays(7));

			// torsdag før påske; Skjærtorsdag
			bevegeligeOffentligFridager.add(paaskedag.minusDays(3));

			// fredag før påske; Langfredag
			bevegeligeOffentligFridager.add(paaskedag.minusDays(2));

			// 1.påskedag
			bevegeligeOffentligFridager.add(paaskedag);

			// 2.påskedag
			bevegeligeOffentligFridager.add(paaskedag.plusDays(1));

			// Kristi Himmelfartsdag
			bevegeligeOffentligFridager.add(paaskedag.plusDays(39));

			// 1.pinsedag
			bevegeligeOffentligFridager.add(paaskedag.plusDays(49));

			// 2.pinsedag
			bevegeligeOffentligFridager.add(paaskedag.plusDays(50));

			return bevegeligeOffentligFridager;
		}

		private static LocalDate utledPaaskedag(int aar) {
			int a = aar % 19;
			int b = aar / 100;
			int c = aar % 100;
			int d = b / 4;
			int e = b % 4;
			int f = (b + 8) / 25;
			int g = (b - f + 1) / 3;
			int h = ((19 * a) + b - d - g + 15) % 30;
			int i = c / 4;
			int k = c % 4;
			int l = (32 + (2 * e) + (2 * i) - h - k) % 7;
			int m = (a + (11 * h) + (22 * l)) / 451;
			int n = (h + l - (7 * m) + 114) / 31; // Tallet på måneden
			int p = (h + l - (7 * m) + 114) % 31; // Tallet på dagen

			return LocalDate.of(aar, n, p + 1);
		}
}
