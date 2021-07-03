/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.abc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.BitSet;
import java.util.Objects;

import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.FormatConstants;
import com.puttysoftware.retrorpgcs.maze.Maze;
import com.puttysoftware.retrorpgcs.maze.MazeConstants;
import com.puttysoftware.retrorpgcs.maze.utilities.ImageColorConstants;
import com.puttysoftware.retrorpgcs.maze.utilities.RandomGenerationRule;
import com.puttysoftware.retrorpgcs.maze.utilities.TypeConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.BattleImageManager;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public abstract class AbstractMazeObject implements RandomGenerationRule {
    private static int templateColor = ImageColorConstants.COLOR_NONE;
    public static final int DEFAULT_CUSTOM_VALUE = 0;
    protected static final int CUSTOM_FORMAT_MANUAL_OVERRIDE = -1;

    public static int getTemplateColor() {
        return AbstractMazeObject.templateColor;
    }

    public static void setTemplateColor(final int newTC) {
        AbstractMazeObject.templateColor = newTC;
    }

    // Properties
    private boolean solid;
    private boolean friction;
    private final boolean blocksLOS;
    private int timerValue;
    private int initialTimerValue;
    private boolean timerActive;
    protected BitSet type;
    private AbstractMazeObject saved;

    public AbstractMazeObject() {
        this.solid = false;
        this.friction = true;
        this.blocksLOS = false;
        this.type = new BitSet(TypeConstants.TYPES_COUNT);
        this.timerValue = 0;
        this.initialTimerValue = 0;
        this.timerActive = false;
        this.setTypes();
    }

    // Constructors
    public AbstractMazeObject(final boolean isSolid, final boolean sightBlock) {
        this.solid = isSolid;
        this.friction = true;
        this.blocksLOS = sightBlock;
        this.type = new BitSet(TypeConstants.TYPES_COUNT);
        this.timerValue = 0;
        this.initialTimerValue = 0;
        this.timerActive = false;
        this.setTypes();
    }

    public AbstractMazeObject(final boolean isSolid, final boolean hasFriction,
            final boolean sightBlock) {
        this.solid = isSolid;
        this.friction = hasFriction;
        this.blocksLOS = sightBlock;
        this.type = new BitSet(TypeConstants.TYPES_COUNT);
        this.timerValue = 0;
        this.initialTimerValue = 0;
        this.timerActive = false;
        this.setTypes();
    }

    public final void activateTimer(final int ticks) {
        this.timerActive = true;
        this.timerValue = ticks;
        this.initialTimerValue = ticks;
    }

    public boolean arrowHitBattleCheck() {
        return !this.isSolid();
    }

    public BufferedImageIcon battleRenderHook() {
        return BattleImageManager.getImage(this.getName(),
                this.getBattleBaseID(), AbstractMazeObject.getTemplateColor());
    }

    // Methods
    @Override
    public AbstractMazeObject clone() {
        try {
            final AbstractMazeObject copy = this.getClass().getConstructor()
                    .newInstance();
            copy.solid = this.solid;
            copy.friction = this.friction;
            copy.type = (BitSet) this.type.clone();
            copy.timerValue = this.timerValue;
            copy.initialTimerValue = this.initialTimerValue;
            copy.timerActive = this.timerActive;
            copy.type = (BitSet) this.type.clone();
            return copy;
        } catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            RetroRPGCS.getInstance().handleError(e);
            return null;
        }
    }

    public boolean defersSetProperties() {
        return false;
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public void determineCurrentAppearance(final int x, final int y,
            final int z) {
        // Do nothing
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public void editorGenerateHook(final int x, final int y, final int z) {
        // Do nothing
    }

    public boolean enabledInBattle() {
        return true;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || !(obj instanceof AbstractMazeObject)) {
            return false;
        }
        final var other = (AbstractMazeObject) obj;
        if (this.friction != other.friction) {
            return false;
        }
        if (this.solid != other.solid) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (this.timerActive != other.timerActive) {
            return false;
        }
        if (this.timerValue != other.timerValue) {
            return false;
        }
        if (this.initialTimerValue != other.initialTimerValue) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public AbstractMazeObject gameRenderHook(final int x, final int y,
            final int z) {
        return this;
    }

    public abstract int getBaseID();

    public int getBattleBaseID() {
        if (this.enabledInBattle()) {
            return this.getGameBaseID();
        } else {
            return ObjectImageConstants.OBJECT_IMAGE_NONE;
        }
    }

    public int getCustomFormat() {
        return 0;
    }

    abstract public int getCustomProperty(int propID);

    abstract public String getDescription();

    public int getGameBaseID() {
        return this.getBaseID();
    }

    public String getGameName() {
        return this.getName();
    }

    public final String getIdentifier() {
        return this.getName();
    }

    abstract public int getLayer();

    @Override
    public int getMaximumRequiredQuantity(final Maze maze) {
        return RandomGenerationRule.NO_LIMIT;
    }

    @Override
    public int getMinimumRequiredQuantity(final Maze maze) {
        return RandomGenerationRule.NO_LIMIT;
    }

    abstract public String getName();

    abstract public String getPluralName();

    public AbstractMazeObject getSavedObject() {
        if (this.saved == null) {
            throw new NullPointerException("Saved object == NULL!");
        }
        return this.saved;
    }

    public boolean hasFriction() {
        return this.friction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.friction, this.solid, this.timerValue, this.initialTimerValue, this.timerActive,
                this.type);
    }

    public boolean isMoving() {
        return false;
    }

    public boolean isOfType(final int testType) {
        return this.type.get(testType);
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    public boolean isSightBlocking() {
        return this.blocksLOS;
    }

    public boolean isSolid() {
        return this.solid;
    }

    public boolean isSolidInBattle() {
        if (this.enabledInBattle()) {
            return this.isSolid();
        } else {
            return false;
        }
    }

    /**
     *
     * @param ie
     * @param dirX
     * @param dirY
     * @param inv
     */
    public void moveFailedAction(final boolean ie, final int dirX,
            final int dirY) {
        SoundManager.playSound(SoundConstants.SOUND_WALK_FAILED);
        RetroRPGCS.getInstance().showMessage("Can't go that way");
    }

    public boolean overridesDefaultPostMove() {
        return false;
    }

    public abstract void postMoveAction(final boolean ie, final int dirX,
            final int dirY);

    // Scripting
    /**
     *
     * @param ie
     * @param dirX
     * @param dirY
     * @param inv
     * @return
     */
    public boolean preMoveAction(final boolean ie, final int dirX,
            final int dirY) {
        return true;
    }

    /**
     *
     * @param reader
     * @param formatVersion
     * @return
     * @throws IOException
     */
    protected AbstractMazeObject readMazeObjectHook(final XDataReader reader,
            final int formatVersion) throws IOException {
        // Dummy implementation, subclasses can override
        return this;
    }

    public final AbstractMazeObject readMazeObjectV1(final XDataReader reader,
            final String ident) throws IOException {
        if (ident.equals(this.getIdentifier())) {
            final var savedIdent = reader.readString();
            if (!savedIdent.equals("NULL")) {
                this.saved = RetroRPGCS.getInstance().getObjects()
                        .readSavedMazeObject(reader, savedIdent,
                                FormatConstants.MAZE_FORMAT_LATEST);
            }
            final var cc = this.getCustomFormat();
            if (cc == AbstractMazeObject.CUSTOM_FORMAT_MANUAL_OVERRIDE) {
                return this.readMazeObjectHook(reader,
                        FormatConstants.MAZE_FORMAT_LATEST);
            } else {
                for (var x = 0; x < cc; x++) {
                    final var cx = reader.readInt();
                    this.setCustomProperty(x + 1, cx);
                }
            }
            return this;
        } else {
            return null;
        }
    }

    abstract public void setCustomProperty(int propID, int value);

    public void setSavedObject(final AbstractMazeObject newSaved) {
        if (newSaved == null) {
            throw new IllegalArgumentException("New saved object == NULL!");
        }
        this.saved = newSaved;
    }

    protected abstract void setTypes();

    @Override
    public boolean shouldGenerateObject(final Maze maze, final int row,
            final int col, final int floor, final int level, final int layer) {
        if (layer == MazeConstants.LAYER_OBJECT) {
            // Handle object layer
            // Limit generation of other objects to 20%, unless required
            if (this.isOfType(TypeConstants.TYPE_PASS_THROUGH) || this.isRequired()) {
                return true;
            } else {
                final var r = new RandomRange(1, 100);
                if (r.generate() <= 20) {
                    return true;
                } else {
                    return false;
                }
            }
        } else // Handle ground layer
        if (this.isOfType(TypeConstants.TYPE_FIELD)) {
            // Limit generation of fields to 20%
            final var r = new RandomRange(1, 100);
            if (r.generate() <= 20) {
                return true;
            } else {
                return false;
            }
        } else {
            // Generate other ground at 100%
            return true;
        }
    }

    public final void tickTimer(final int dirX, final int dirY) {
        if (this.timerActive) {
            this.timerValue--;
            if (this.timerValue == 0) {
                this.timerActive = false;
                this.initialTimerValue = 0;
                this.timerExpiredAction(dirX, dirY);
            }
        }
    }

    /**
     *
     * @param dirX
     * @param dirY
     */
    public void timerExpiredAction(final int dirX, final int dirY) {
        // Do nothing
    }

    public final void writeMazeObject(final XDataWriter writer)
            throws IOException {
        writer.writeString(this.getIdentifier());
        if (this.saved == null) {
            writer.writeString("NULL");
        } else {
            this.saved.writeMazeObject(writer);
        }
        final var cc = this.getCustomFormat();
        if (cc == AbstractMazeObject.CUSTOM_FORMAT_MANUAL_OVERRIDE) {
            this.writeMazeObjectHook(writer);
        } else {
            for (var x = 0; x < cc; x++) {
                final var cx = this.getCustomProperty(x + 1);
                writer.writeInt(cx);
            }
        }
    }

    /**
     *
     * @param writer
     * @throws IOException
     */
    protected void writeMazeObjectHook(final XDataWriter writer)
            throws IOException {
        // Do nothing - but let subclasses override
    }
}
