/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze;

import java.io.File;
import java.io.IOException;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.randomrange.RandomLongRange;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.VersionException;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractMazeObject;
import com.puttysoftware.retrorpgcs.maze.objects.Empty;
import com.puttysoftware.retrorpgcs.maze.objects.Monster;
import com.puttysoftware.retrorpgcs.maze.objects.Tile;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class Maze {
    private static final int MIN_LEVELS = 1;
    private static final int MAX_LEVELS = 55;

    public static int getMaxColumns() {
        return LayeredTower.getMaxColumns();
    }

    public static int getMaxFloors() {
        return LayeredTower.getMaxFloors();
    }

    public static int getMaxLevels() {
        return Maze.MAX_LEVELS;
    }

    public static int getMaxRows() {
        return LayeredTower.getMaxRows();
    }

    // Static methods
    public static String getMazeTempFolder() {
        return System.getProperty("java.io.tmpdir") + File.separator
                + "RetroRPGCS";
    }

    public static int getMinLevels() {
        return Maze.MIN_LEVELS;
    }

    // Methods
    public static Maze getTemporaryBattleCopy() {
        final var temp = new Maze();
        temp.addLevel(RetroRPGCS.getBattleMazeSize(),
                RetroRPGCS.getBattleMazeSize(), 1);
        temp.fill(new Tile(), new Empty());
        return temp;
    }

    // Properties
    private LayeredTower mazeData;
    private int startW;
    private int locW;
    private int saveW;
    private int levelCount;
    private int activeLevel;
    private String basePath;
    private PrefixIO prefixHandler;
    private SuffixIO suffixHandler;
    private final int[] savedStart;

    // Constructors
    public Maze() {
        this.mazeData = null;
        this.levelCount = 0;
        this.startW = 0;
        this.locW = 0;
        this.saveW = 0;
        this.activeLevel = 0;
        this.savedStart = new int[4];
        final var random = new RandomLongRange(0, Long.MAX_VALUE).generate();
        final var randomID = Long.toHexString(random);
        this.basePath = System.getProperty("java.io.tmpdir") + File.separator
                + "RetroRPGCS" + File.separator + randomID + ".maze";
        final var base = new File(this.basePath);
        final var success = base.mkdirs();
        if (!success) {
            CommonDialogs.showErrorDialog(
                    "Maze temporary folder creation failed!", "RetroRPGCS");
        }
    }

    public boolean addLevel(final int rows, final int cols, final int floors) {
        if (this.levelCount < Maze.getMaxLevels()) {
            if (this.mazeData != null) {
                try (var writer = this.getLevelWriter()) {
                    // Save old level
                    this.writeMazeLevel(writer);
                } catch (final IOException io) {
                    // Ignore
                }
            }
            this.mazeData = new LayeredTower(rows, cols, floors);
            this.levelCount++;
            this.activeLevel = this.levelCount - 1;
            return true;
        } else {
            return false;
        }
    }

    public void createNote(final int x, final int y, final int z) {
        this.mazeData.createNote(x, y, z);
    }

    public boolean doesLevelExistOffset(final int level) {
        return this.activeLevel + level < this.levelCount
                && this.activeLevel + level >= 0;
    }

    public boolean doesPlayerExist() {
        return this.mazeData.doesPlayerExist();
    }

    private void fill(final AbstractMazeObject bottom,
            final AbstractMazeObject top) {
        this.mazeData.fillFloor(bottom, top, 0);
    }

    public void fillLevelRandomly() {
        this.mazeData.fillRandomly(this, this.activeLevel);
    }

    public void fullScanButton(final int l) {
        this.mazeData.fullScanButton(l);
    }

    public String getBasePath() {
        return this.basePath;
    }

    public AbstractMazeObject getCell(final int row, final int col,
            final int floor, final int extra) {
        return this.mazeData.getMazeCell(row, col, floor, extra);
    }

    public int getColumns() {
        return this.mazeData.getColumns();
    }

    public int getFloors() {
        return this.mazeData.getFloors();
    }

    private XDataReader getLevelReader() throws IOException {
        return new XDataReader(this.basePath + File.separator + "level"
                + this.activeLevel + ".xml", "level");
    }

    private XDataWriter getLevelWriter() throws IOException {
        return new XDataWriter(this.basePath + File.separator + "level"
                + this.activeLevel + ".xml", "level");
    }

    public MazeNote getNote(final int x, final int y, final int z) {
        return this.mazeData.getNote(x, y, z);
    }

    public int getPlayerLocationX() {
        return this.mazeData.getPlayerRow();
    }

    public int getPlayerLocationY() {
        return this.mazeData.getPlayerColumn();
    }

    public int getPlayerLocationZ() {
        return this.mazeData.getPlayerFloor();
    }

    public int getRows() {
        return this.mazeData.getRows();
    }

    public int getStartLevel() {
        return this.startW;
    }

    public boolean hasNote(final int x, final int y, final int z) {
        return this.mazeData.hasNote(x, y, z);
    }

    public boolean isSquareVisible(final int x1, final int y1, final int x2,
            final int y2) {
        return this.mazeData.isSquareVisible(x1, y1, x2, y2);
    }

    public void offsetPlayerLocationW(final int newPlayerLevel) {
        this.locW += newPlayerLevel;
    }

    public void offsetPlayerLocationX(final int newPlayerRow) {
        this.mazeData.offsetPlayerRow(newPlayerRow);
    }

    public void offsetPlayerLocationY(final int newPlayerColumn) {
        this.mazeData.offsetPlayerColumn(newPlayerColumn);
    }

    public void postBattle(final Monster m, final int xLoc, final int yLoc,
            final boolean player) {
        this.mazeData.postBattle(m, xLoc, yLoc, player);
    }

    public Maze readMaze() throws IOException {
        final var m = new Maze();
        // Attach handlers
        m.setPrefixHandler(this.prefixHandler);
        m.setSuffixHandler(this.suffixHandler);
        // Make base paths the same
        m.basePath = this.basePath;
        var version = 0;
        // Create metafile reader
        try (var metaReader = new XDataReader(
                m.basePath + File.separator + "metafile.xml", "maze")) {
            // Read metafile
            version = m.readMazeMetafile(metaReader);
        } catch (final IOException ioe) {
            throw ioe;
        }
        // Create data reader
        try (var dataReader = m.getLevelReader()) {
            // Read data
            m.readMazeLevel(dataReader, version);
            return m;
        } catch (final IOException ioe) {
            throw ioe;
        }
    }

    private void readMazeLevel(final XDataReader reader) throws IOException {
        this.readMazeLevel(reader, FormatConstants.MAZE_FORMAT_LATEST);
    }

    private void readMazeLevel(final XDataReader reader,
            final int formatVersion) throws IOException {
        if (formatVersion == FormatConstants.MAZE_FORMAT_LATEST) {
            this.mazeData = LayeredTower.readLayeredTowerV1(reader);
            this.mazeData.readSavedTowerState(reader, formatVersion);
        } else {
            throw new VersionException(
                    "Unknown maze format version: " + formatVersion + "!");
        }
    }

    private int readMazeMetafile(final XDataReader reader) throws IOException {
        var ver = FormatConstants.MAZE_FORMAT_LATEST;
        if (this.prefixHandler != null) {
            ver = this.prefixHandler.readPrefix(reader);
        }
        this.levelCount = reader.readInt();
        this.startW = reader.readInt();
        this.locW = reader.readInt();
        this.saveW = reader.readInt();
        this.activeLevel = reader.readInt();
        for (var y = 0; y < 4; y++) {
            this.savedStart[y] = reader.readInt();
        }
        if (this.suffixHandler != null) {
            this.suffixHandler.readSuffix(reader, ver);
        }
        return ver;
    }

    public void resetVisibleSquares() {
        this.mazeData.resetVisibleSquares();
    }

    public void restore() {
        this.mazeData.restore();
    }

    public void restorePlayerLocation() {
        this.locW = this.saveW;
        this.mazeData.restorePlayerLocation();
    }

    public void save() {
        this.mazeData.save();
    }

    public void savePlayerLocation() {
        this.saveW = this.locW;
        this.mazeData.savePlayerLocation();
    }

    public void setCell(final AbstractMazeObject mo, final int row,
            final int col, final int floor, final int extra) {
        this.mazeData.setMazeCell(mo, row, col, floor, extra);
    }

    public void setPlayerLocationX(final int newPlayerRow) {
        this.mazeData.setPlayerRow(newPlayerRow);
    }

    public void setPlayerLocationY(final int newPlayerColumn) {
        this.mazeData.setPlayerColumn(newPlayerColumn);
    }

    public void setPlayerLocationZ(final int newPlayerFloor) {
        this.mazeData.setPlayerFloor(newPlayerFloor);
    }

    public void setPlayerToStart() {
        this.mazeData.setPlayerToStart();
    }

    public void setPrefixHandler(final PrefixIO xph) {
        this.prefixHandler = xph;
    }

    public void setStartColumn(final int newStartColumn) {
        this.mazeData.setStartColumn(newStartColumn);
    }

    public void setStartFloor(final int newStartFloor) {
        this.mazeData.setStartFloor(newStartFloor);
    }

    public void setStartRow(final int newStartRow) {
        this.mazeData.setStartRow(newStartRow);
    }

    public void setSuffixHandler(final SuffixIO xsh) {
        this.suffixHandler = xsh;
    }

    public void switchLevel(final int level) {
        this.switchLevelInternal(level);
    }

    private void switchLevelInternal(final int level) {
        if (this.activeLevel != level) {
            if (this.mazeData != null) {
                try (var writer = this.getLevelWriter()) {
                    // Save old level
                    this.writeMazeLevel(writer);
                } catch (final IOException io) {
                    // Ignore
                }
            }
            this.activeLevel = level;
            try (var reader = this.getLevelReader()) {
                // Load new level
                this.readMazeLevel(reader);
            } catch (final IOException io) {
                // Ignore
            }
        }
    }

    public void switchLevelOffset(final int level) {
        this.switchLevelInternal(this.activeLevel + level);
    }

    public void tickTimers(final int floor) {
        this.mazeData.tickTimers(floor);
    }

    public void updateMonsterPosition(final int move, final int xLoc,
            final int yLoc, final Monster monster) {
        this.mazeData.updateMonsterPosition(move, xLoc, yLoc, monster);
    }

    public void updateVisibleSquares(final int xp, final int yp, final int zp) {
        this.mazeData.updateVisibleSquares(xp, yp, zp);
    }

    public void writeMaze() throws IOException {
        try {
            // Create metafile writer
            try (var metaWriter = new XDataWriter(
                    this.basePath + File.separator + "metafile.xml", "maze")) {
                // Write metafile
                this.writeMazeMetafile(metaWriter);
            }
            // Create data writer
            try (var dataWriter = this.getLevelWriter()) {
                // Write data
                this.writeMazeLevel(dataWriter);
            }
        } catch (final IOException ioe) {
            throw ioe;
        }
    }

    private void writeMazeLevel(final XDataWriter writer) throws IOException {
        // Write the level
        this.mazeData.writeLayeredTower(writer);
        this.mazeData.writeSavedTowerState(writer);
    }

    private void writeMazeMetafile(final XDataWriter writer)
            throws IOException {
        if (this.prefixHandler != null) {
            this.prefixHandler.writePrefix(writer);
        }
        writer.writeInt(this.levelCount);
        writer.writeInt(this.startW);
        writer.writeInt(this.locW);
        writer.writeInt(this.saveW);
        writer.writeInt(this.activeLevel);
        for (var y = 0; y < 4; y++) {
            writer.writeInt(this.savedStart[y]);
        }
        if (this.suffixHandler != null) {
            this.suffixHandler.writeSuffix(writer);
        }
    }
}
