package it.unibo.deathnote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.deathnote.api.DeathNote;
import it.unibo.deathnote.impl.DeathNoteImpl;

class TestDeathNote {

    private static final String VICTIM_NAME = "Name Surname";
    private static final String ANOTHER_NAME = "Another name and surname";
    private static final String KARTING_ACCIDENT = "Karting accident";
    private static final String ANOTHER_ACCIDENT = "Random accident";

    private static final int INVALID_CAUSE_TIMEOUT = 100;
    private static final int INVALID_DETAILS_TIMEOUT = 6100;

    private DeathNoteImpl deathnote;

    @BeforeEach
    void init() {
        deathnote = new DeathNoteImpl();
    }

    /**
     * Tests that rule number 0 and
     * negative rules don't exist.
     */
    @Test
    void testIllegalRuleNumber() {
        for (final int index: List.of(-1, 0, DeathNote.RULES.size()+1)) {
            try {
                deathnote.getRule(index);
                fail("Expected IllegalArgumentException because there is no rule with this number");
            } catch (IllegalArgumentException e) {
                assertNotNull(e.getMessage());
                assertFalse(e.getMessage().isBlank());
            }
        }
    }

    /**
     * Checks that none of the rules is null or blank.
     */
    @Test
    void testRules() {
        for (int i = 1; i <= DeathNote.RULES.size(); i++) {
                final var rule = deathnote.getRule(i);
                assertNotNull(rule);
                assertFalse(rule.isBlank());
        }
    }

    /**
     * Checks that the human whose name is written in this DeathNote will die.
     */
    @Test
    void testActualDeath() {
        assertFalse(deathnote.isNameWritten(VICTIM_NAME));
        deathnote.writeName(VICTIM_NAME);
        assertTrue(deathnote.isNameWritten(VICTIM_NAME));
        assertFalse(deathnote.isNameWritten(ANOTHER_NAME));
        assertFalse(deathnote.isNameWritten(""));

    }

    @Test
    void testDeathCause() throws InterruptedException {
        try {
            deathnote.writeDeathCause(ANOTHER_ACCIDENT);
            fail("Expected IllegalStateException because no name was written yet");
        } catch (IllegalStateException e) {
            assertNotNull(e.getMessage());
            assertFalse(e.getMessage().isBlank());
        }

        deathnote.writeName(VICTIM_NAME);
        assertEquals("Heart attack", deathnote.getDeathCause(VICTIM_NAME));
        deathnote.writeName(ANOTHER_NAME);
        assertTrue(deathnote.writeDeathCause(KARTING_ACCIDENT));
        assertEquals("Karting accident", deathnote.getDeathCause(ANOTHER_NAME));
        Thread.sleep(INVALID_CAUSE_TIMEOUT);
        assertFalse(deathnote.writeDeathCause(ANOTHER_ACCIDENT));
        assertEquals("Karting accident", deathnote.getDeathCause(ANOTHER_NAME));
    }

    @Test
    void testDeathDetails() throws InterruptedException {
        try {
            deathnote.writeDetails("Details");
            fail("Expected IllegalStateException because no name was written yet");
        } catch (IllegalStateException e) {
            assertNotNull(e.getMessage());
            assertFalse(e.getMessage().isBlank());
        }

        deathnote.writeName(VICTIM_NAME);
        assertTrue(deathnote.getDeathDetails(VICTIM_NAME).isBlank());
        assertEquals("ran for too long", deathnote.writeDetails("ran for too long"));
        deathnote.writeName(ANOTHER_NAME);
        Thread.sleep(INVALID_DETAILS_TIMEOUT);
        assertFalse(deathnote.writeDetails("Other details"));
        assertEquals("", deathnote.getDeathDetails(ANOTHER_NAME));
    }
}
   