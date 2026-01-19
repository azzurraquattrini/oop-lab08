package it.unibo.deathnote.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import it.unibo.deathnote.api.DeathNote;

public class DeathNoteImpl implements DeathNote {

    private final Map<String, Death> deaths;
    private String lastWrittenName;

    /**
     * Creates a new Death Note with
     * no name written on it.
     */
    public DeathNoteImpl() {
        this.deaths = new LinkedHashMap<>();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getRule(int ruleNumber) {
        if (ruleNumber < 1 || ruleNumber > RULES.size()) {
            throw new IllegalArgumentException("No such rule number exists");
        }
        return RULES.get(ruleNumber - 1);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void writeName(final String name) {
        Objects.requireNonNull(name);
        this.lastWrittenName = name;
        deaths.put(name, new Death());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean writeDeathCause(final String cause) {
        return updateDeath(
            cause,
            new DeathTransformer() {
                @Override
                public Death call(final Death input) {
                    return input.writeCause(cause);
                }
            }
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean writeDetails(final String details) {
        return updateDeath(
            details,
            new DeathTransformer() {
                @Override
                public Death call(final Death input) {
                    return input.writeDetails(details);
                }
            }
        );
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getDeathCause(final String name) {
        return getDeath(name).cause;
    }

    /**
     * @inheritDoc
    */
    @Override
    public String getDeathDetails(final String name) {
        return getDeath(name).details;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isNameWritten(final String name) {
        return deaths.containsKey(name);
    }

    private Death getDeath(final String name) {
        final var death = deaths.get(name);
        if (death == null) {
            throw new IllegalArgumentException("The name is not written in this Deathnote");
        }
        return death;
    }

    private boolean updateDeath (final String update, final DeathTransformer operation) {
          if (lastWrittenName == null) {
            throw new IllegalStateException("No name written yet");
            }
            if (update == null) {
                throw new IllegalStateException("No update provided");
            }
            final var previous = deaths.get(lastWrittenName);
            final var updated = operation.call(previous);
            if (previous.equals(updated)) {
                return false;
            } else {
                deaths.put(lastWrittenName, updated);
                return true;
        }
    }

    private interface DeathTransformer {
        Death call(Death input);
    }

    private static final class Death {
        
        private static final int VALID_CAUSE_TIMEOUT = 40;
        private static final int VALID_DETAILS_TIMEOUT = 6000 + VALID_CAUSE_TIMEOUT;
        private static final String DEFAULT_CAUSE = "Heart attack";
        
        private final String cause;
        private final String details;
        private final long timeOfDeath;

        private Death(final String cause, final String details) {
            this.cause = cause;
            this.details = details;
            this.timeOfDeath = System.currentTimeMillis();
        }

        private Death() {
            this(DEFAULT_CAUSE, "");
        }

        private Death writeCause(final String cause) {
            Objects.requireNonNull(cause);
            if (System.currentTimeMillis() < timeOfDeath + VALID_CAUSE_TIMEOUT) {
                return new Death(cause, this.details);
            }
            else {
                return this;
            }
        }

        private Death writeDetails(final String details) {
            Objects.requireNonNull(details);
            if (System.currentTimeMillis() < timeOfDeath + VALID_DETAILS_TIMEOUT) {
                return new Death(this.cause, details);
            }
            else {
                return this;
            }
        }

    }

    
}
