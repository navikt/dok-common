package no.nav.dok.validators;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static no.nav.dok.validators.OffentligFridag.erOffentligFridag;

class OffentligFridagTest {
	@Test
	void fasteHelligdagerTest() {
		assertTrue(erOffentligFridag(LocalDate.of(2025, 1, 1)));
		assertFalse(erOffentligFridag(LocalDate.of(2025, 1, 2)));
		assertFalse(erOffentligFridag(LocalDate.of(2025, 1, 3)));
		assertFalse(erOffentligFridag(LocalDate.of(2025, 1, 4)));
		assertFalse(erOffentligFridag(LocalDate.of(2025, 1, 5)));

		assertTrue(erOffentligFridag(LocalDate.of(2025, 5, 1)));

		assertFalse(erOffentligFridag(LocalDate.of(2025, 5, 16)));
		assertTrue(erOffentligFridag(LocalDate.of(2025, 5, 17)));
		assertFalse(erOffentligFridag(LocalDate.of(2025, 5, 18)));
		assertTrue(erOffentligFridag(LocalDate.of(2035, 5, 17)));
		assertTrue(erOffentligFridag(LocalDate.of(2015, 5, 17)));

		assertTrue(erOffentligFridag(LocalDate.of(2022, 12, 25)));
		assertTrue(erOffentligFridag(LocalDate.of(2023, 12, 25)));
		assertTrue(erOffentligFridag(LocalDate.of(2024, 12, 25)));
		assertTrue(erOffentligFridag(LocalDate.of(2025, 12, 25)));
		assertTrue(erOffentligFridag(LocalDate.of(2025, 12, 26)));
		assertTrue(erOffentligFridag(LocalDate.of(2026, 12, 26)));
		assertTrue(erOffentligFridag(LocalDate.of(2027, 12, 26)));
		assertTrue(erOffentligFridag(LocalDate.of(2028, 12, 26)));

	}

	@Test
	void bevegeligeHelligdagerTest() {
		assertTrue(erOffentligFridag(LocalDate.of(2025, 4, 17)));
		assertTrue(erOffentligFridag(LocalDate.of(2025, 4, 18)));
		assertTrue(erOffentligFridag(LocalDate.of(2025, 4, 21)));
		assertTrue(erOffentligFridag(LocalDate.of(2025, 5, 29)));
		assertFalse(erOffentligFridag(LocalDate.of(2025, 5, 30)));

		assertTrue(erOffentligFridag(LocalDate.of(2028, 5, 25)));
		assertTrue(erOffentligFridag(LocalDate.of(2028, 6, 5)));
		assertTrue(erOffentligFridag(LocalDate.of(2028, 4, 17)));

		assertTrue(erOffentligFridag(LocalDate.of(2018, 4, 2)));
		assertTrue(erOffentligFridag(LocalDate.of(2018, 3, 30)));
		assertTrue(erOffentligFridag(LocalDate.of(2018, 5, 21)));
	}

}