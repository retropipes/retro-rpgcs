/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze;

import java.io.IOException;
import java.util.Arrays;

import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractMazeObject;
import com.puttysoftware.retrorpgcs.maze.objects.Empty;
import com.puttysoftware.retrorpgcs.maze.objects.Monster;
import com.puttysoftware.retrorpgcs.maze.objects.Tile;
import com.puttysoftware.retrorpgcs.maze.objects.WallOff;
import com.puttysoftware.retrorpgcs.maze.objects.WallOn;
import com.puttysoftware.retrorpgcs.maze.utilities.DirectionResolver;
import com.puttysoftware.retrorpgcs.maze.utilities.RandomGenerationRule;
import com.puttysoftware.storage.FlagStorage;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

final class LayeredTower implements Cloneable {
    private static final int MAX_VISION_RADIUS = 16;
    private static final int MAX_FLOORS = 1;
    private static final int MAX_COLUMNS = 250;
    private static final int MAX_ROWS = 250;

    public static int getMaxColumns() {
        return LayeredTower.MAX_COLUMNS;
    }

    // Static methods
    public static int getMaxFloors() {
        return LayeredTower.MAX_FLOORS;
    }

    public static int getMaxRows() {
        return LayeredTower.MAX_ROWS;
    }

    public static boolean radialScan(final int cx, final int cy, final int r,
            final int tx, final int ty) {
        return Math.abs(tx - cx) <= r && Math.abs(ty - cy) <= r;
    }

    public static LayeredTower readLayeredTowerV1(final XDataReader reader)
            throws IOException {
        int y, x, z, e, mazeSizeX, mazeSizeY, mazeSizeZ;
        mazeSizeX = reader.readInt();
        mazeSizeY = reader.readInt();
        mazeSizeZ = reader.readInt();
        final var lt = new LayeredTower(mazeSizeX, mazeSizeY, mazeSizeZ);
        for (x = 0; x < lt.getColumns(); x++) {
            for (y = 0; y < lt.getRows(); y++) {
                for (z = 0; z < lt.getFloors(); z++) {
                    for (e = 0; e < MazeConstants.LAYER_COUNT; e++) {
                        lt.setMazeCell(RetroRPGCS.getInstance().getObjects()
                                .readMazeObject(reader,
                                        FormatConstants.MAZE_FORMAT_LATEST),
                                y, x, z, e);
                        if (lt.getMazeCell(y, x, z, e) == null) {
                            return null;
                        }
                    }
                    lt.visionData.setCell(reader.readBoolean(), y, x, z);
                    final var hasNote = reader.readBoolean();
                    if (hasNote) {
                        final var mn = MazeNote.readNote(reader);
                        lt.noteData.setNote(mn, y, x, z);
                    }
                }
            }
        }
        for (y = 0; y < 3; y++) {
            lt.playerStartData[y] = reader.readInt();
            lt.playerLocationData[y] = reader.readInt();
            lt.savedPlayerLocationData[y] = reader.readInt();
            lt.findResult[y] = reader.readInt();
        }
        lt.horizontalWraparoundEnabled = reader.readBoolean();
        lt.verticalWraparoundEnabled = reader.readBoolean();
        lt.visionMode = reader.readInt();
        lt.visionModeExploreRadius = reader.readInt();
        lt.visionMode = reader.readInt();
        lt.visionModeExploreRadius = reader.readInt();
        lt.visionRadius = reader.readInt();
        lt.initialVisionRadius = reader.readInt();
        lt.regionSize = reader.readInt();
        return lt;
    }

    // Properties
    private final LowLevelAMODataStore data;
    private LowLevelAMODataStore savedTowerState;
    private final FlagStorage visionData;
    private final LowLevelNoteDataStore noteData;
    private final int[] playerStartData;
    private final int[] playerLocationData;
    private final int[] savedPlayerLocationData;
    private final int[] findResult;
    private boolean horizontalWraparoundEnabled;
    private boolean verticalWraparoundEnabled;
    private int visionMode;
    private int visionModeExploreRadius;
    private int visionRadius;
    private int initialVisionRadius;
    private int regionSize;

    // Constructors
    public LayeredTower(final int rows, final int cols, final int floors) {
        this.data = new LowLevelAMODataStore(cols, rows, floors,
                MazeConstants.LAYER_COUNT);
        this.savedTowerState = new LowLevelAMODataStore(cols, rows, floors,
                MazeConstants.LAYER_COUNT);
        this.visionData = new FlagStorage(cols, rows, floors);
        this.noteData = new LowLevelNoteDataStore(cols, rows, floors);
        this.playerStartData = new int[3];
        Arrays.fill(this.playerStartData, -1);
        this.playerLocationData = new int[3];
        Arrays.fill(this.playerLocationData, -1);
        this.savedPlayerLocationData = new int[3];
        Arrays.fill(this.savedPlayerLocationData, -1);
        this.findResult = new int[3];
        Arrays.fill(this.findResult, -1);
        this.horizontalWraparoundEnabled = false;
        this.verticalWraparoundEnabled = false;
        this.visionMode = MazeConstants.VISION_MODE_EXPLORE_AND_LOS;
        this.visionModeExploreRadius = 2;
        this.visionRadius = LayeredTower.MAX_VISION_RADIUS;
        this.regionSize = 8;
    }

    public LayeredTower(final LayeredTower copyFrom) {
        this.data = new LowLevelAMODataStore(copyFrom.data);
        this.savedTowerState = new LowLevelAMODataStore(
                copyFrom.savedTowerState);
        this.visionData = new FlagStorage(copyFrom.visionData);
        this.noteData = new LowLevelNoteDataStore(copyFrom.noteData);
        this.playerStartData = new int[3];
        this.playerLocationData = new int[3];
        this.savedPlayerLocationData = new int[3];
        this.findResult = new int[3];
        System.arraycopy(copyFrom.playerStartData, 0, this.playerStartData, 0,
                3);
        System.arraycopy(copyFrom.playerLocationData, 0,
                this.playerLocationData, 0, 3);
        System.arraycopy(copyFrom.savedPlayerLocationData, 0,
                this.savedPlayerLocationData, 0, 3);
        System.arraycopy(copyFrom.savedPlayerLocationData, 0, this.findResult,
                0, 3);
        this.horizontalWraparoundEnabled = copyFrom.horizontalWraparoundEnabled;
        this.verticalWraparoundEnabled = copyFrom.verticalWraparoundEnabled;
        this.visionMode = copyFrom.visionMode;
        this.visionModeExploreRadius = copyFrom.visionModeExploreRadius;
        this.visionRadius = copyFrom.visionRadius;
        this.regionSize = copyFrom.regionSize;
    }

    private boolean areCoordsInBounds(final int x1, final int y1, final int x2,
            final int y2) {
        int fx1, fx2, fy1, fy2;
        if (this.isHorizontalWraparoundEnabled()) {
            fx1 = this.normalizeColumn(x1);
            fx2 = this.normalizeColumn(x2);
        } else {
            fx1 = x1;
            fx2 = x2;
        }
        if (this.isVerticalWraparoundEnabled()) {
            fy1 = this.normalizeRow(y1);
            fy2 = this.normalizeRow(y2);
        } else {
            fy1 = y1;
            fy2 = y2;
        }
        return fx1 >= 0 && fx1 <= this.getRows() && fx2 >= 0
                && fx2 <= this.getRows() && fy1 >= 0 && fy1 <= this.getColumns()
                && fy2 >= 0 && fy2 <= this.getColumns();
    }

    public void createNote(final int x, final int y, final int z) {
        this.noteData.setNote(new MazeNote(), y, x, z);
    }

    public boolean doesPlayerExist() {
        var res = true;
        for (final int element : this.playerStartData) {
            res = res && element != -1;
        }
        return res;
    }

    public void fillFloor(final AbstractMazeObject bottom,
            final AbstractMazeObject top, final int z) {
        int x, y, e;
        for (x = 0; x < this.getColumns(); x++) {
            for (y = 0; y < this.getRows(); y++) {
                for (e = 0; e < MazeConstants.LAYER_COUNT; e++) {
                    if (e == MazeConstants.LAYER_GROUND) {
                        this.setMazeCell(bottom, y, x, z, e);
                    } else {
                        this.setMazeCell(top, y, x, z, e);
                    }
                }
            }
        }
    }

    public void fillFloorRandomly(final Maze maze, final int z, final int w) {
        // Pre-Pass
        final var objects = RetroRPGCS.getInstance().getObjects();
        final AbstractMazeObject pass1FillBottom = new Tile();
        final AbstractMazeObject pass1FillTop = new Empty();
        RandomRange r = null;
        int x, y, e;
        // Pass 1
        this.fillFloor(pass1FillBottom, pass1FillTop, z);
        // Pass 2
        final var columns = this.getColumns();
        final var rows = this.getRows();
        for (e = 0; e < MazeConstants.LAYER_COUNT; e++) {
            final var objectsWithoutPrerequisites = objects
                    .getAllWithoutPrerequisiteAndNotRequired(e);
            if (objectsWithoutPrerequisites != null) {
                r = new RandomRange(0, objectsWithoutPrerequisites.length - 1);
                for (x = 0; x < columns; x++) {
                    for (y = 0; y < rows; y++) {
                        final var placeObj = objectsWithoutPrerequisites[r
                                .generate()];
                        final var okay = placeObj.shouldGenerateObject(maze, x,
                                y, z, w, e);
                        if (okay) {
                            this.setMazeCell(objects.getNewInstanceByName(
                                    placeObj.getName()), y, x, z, e);
                            placeObj.editorGenerateHook(y, x, z);
                        }
                    }
                }
            }
        }
        // Pass 3
        for (var layer = 0; layer < MazeConstants.LAYER_COUNT; layer++) {
            final var requiredObjects = objects.getAllRequired(layer);
            if (requiredObjects != null) {
                final var row = new RandomRange(0, this.getRows() - 1);
                final var column = new RandomRange(0, this.getColumns() - 1);
                int randomColumn, randomRow;
                for (x = 0; x < requiredObjects.length; x++) {
                    final var currObj = requiredObjects[x];
                    final var min = currObj.getMinimumRequiredQuantity(maze);
                    var max = currObj.getMaximumRequiredQuantity(maze);
                    if (max == RandomGenerationRule.NO_LIMIT) {
                        // Maximum undefined, so define it relative to this maze
                        max = this.getRows() * this.getColumns() / 10;
                        // Make sure max is valid
                        if (max < min) {
                            max = min;
                        }
                    }
                    final var howMany = new RandomRange(min, max);
                    final var generateHowMany = howMany.generate();
                    for (y = 0; y < generateHowMany; y++) {
                        randomRow = row.generate();
                        randomColumn = column.generate();
                        if (currObj.shouldGenerateObject(maze, randomRow,
                                randomColumn, z, w, layer)) {
                            this.setMazeCell(
                                    objects.getNewInstanceByName(
                                            currObj.getName()),
                                    randomColumn, randomRow, z, layer);
                            currObj.editorGenerateHook(y, x, z);
                        } else {
                            while (!currObj.shouldGenerateObject(maze,
                                    randomColumn, randomRow, z, w, layer)) {
                                randomRow = row.generate();
                                randomColumn = column.generate();
                            }
                            this.setMazeCell(
                                    objects.getNewInstanceByName(
                                            currObj.getName()),
                                    randomColumn, randomRow, z, layer);
                            currObj.editorGenerateHook(y, x, z);
                        }
                    }
                }
            }
        }
    }

    public void fillRandomly(final Maze maze, final int w) {
        for (var z = 0; z < this.getFloors(); z++) {
            this.fillFloorRandomly(maze, z, w);
        }
    }

    public void fullScanButton(final int l) {
        int u, v, z;
        z = LayeredTower.MAX_FLOORS - 1;
        // Perform the scan
        for (u = 0; u < this.getColumns(); u++) {
            for (v = 0; v < this.getRows(); v++) {
                final var testObj = this.getMazeCell(v, u, z, l);
                if (testObj instanceof WallOff) {
                    this.setMazeCell(new WallOn(), v, u, z, l);
                } else if (testObj instanceof WallOn) {
                    this.setMazeCell(new WallOff(), v, u, z, l);
                }
            }
        }
    }

    private void generateOneMonster() {
        final var row = new RandomRange(0, this.getRows() - 1);
        final var column = new RandomRange(0, this.getColumns() - 1);
        final var zLoc = this.getPlayerFloor();
        int randomRow, randomColumn;
        randomRow = row.generate();
        randomColumn = column.generate();
        var currObj = this.getMazeCell(randomRow, randomColumn, zLoc,
                MazeConstants.LAYER_OBJECT);
        if (!currObj.isSolid()) {
            final var m = new Monster();
            m.setSavedObject(currObj);
            this.setMazeCell(m, randomRow, randomColumn, zLoc,
                    MazeConstants.LAYER_OBJECT);
        } else {
            while (currObj.isSolid()) {
                randomRow = row.generate();
                randomColumn = column.generate();
                currObj = this.getMazeCell(randomRow, randomColumn, zLoc,
                        MazeConstants.LAYER_OBJECT);
            }
            final var m = new Monster();
            m.setSavedObject(currObj);
            this.setMazeCell(m, randomRow, randomColumn, zLoc,
                    MazeConstants.LAYER_OBJECT);
        }
    }

    public int getColumns() {
        return this.data.getShape()[0];
    }

    public int getFloors() {
        return this.data.getShape()[2];
    }

    public AbstractMazeObject getMazeCell(final int row, final int col,
            final int floor, final int extra) {
        var fR = row;
        var fC = col;
        final var fF = floor;
        if (this.verticalWraparoundEnabled) {
            fC = this.normalizeColumn(fC);
        }
        if (this.horizontalWraparoundEnabled) {
            fR = this.normalizeRow(fR);
        }
        return this.data.getMazeCell(fC, fR, fF, extra);
    }

    public MazeNote getNote(final int x, final int y, final int z) {
        return this.noteData.getNote(y, x, z);
    }

    public int getPlayerColumn() {
        return this.playerLocationData[0];
    }

    public int getPlayerFloor() {
        return this.playerLocationData[2];
    }

    public int getPlayerRow() {
        return this.playerLocationData[1];
    }

    public int getRows() {
        return this.data.getShape()[1];
    }

    public boolean hasNote(final int x, final int y, final int z) {
        return this.noteData.getNote(y, x, z) != null;
    }

    public boolean isHorizontalWraparoundEnabled() {
        return this.horizontalWraparoundEnabled;
    }

    public boolean isSquareVisible(final int x1, final int y1, final int x2,
            final int y2) {
        if (this.visionMode == MazeConstants.VISION_MODE_NONE) {
            return true;
        } else {
            var result = false;
            if ((this.visionMode
                    | MazeConstants.VISION_MODE_EXPLORE) == this.visionMode) {
                result = result || this.isSquareVisibleExplore(x2, y2);
                if (result && (this.visionMode
                        | MazeConstants.VISION_MODE_LOS) == this.visionMode) {
                    if (this.areCoordsInBounds(x1, y1, x2, y2)) {
                        // In bounds
                        result = result
                                || this.isSquareVisibleLOS(x1, y1, x2, y2);
                    } else {
                        // Out of bounds
                        result = result
                                && this.isSquareVisibleLOS(x1, y1, x2, y2);
                    }
                }
            } else if (this.areCoordsInBounds(x1, y1, x2, y2)) {
                // In bounds
                result = result || this.isSquareVisibleLOS(x1, y1, x2, y2);
            } else {
                // Out of bounds
                result = result && this.isSquareVisibleLOS(x1, y1, x2, y2);
            }
            return result;
        }
    }

    private boolean isSquareVisibleExplore(final int x2, final int y2) {
        final var zLoc = this.getPlayerFloor();
        int fx2, fy2;
        if (this.isHorizontalWraparoundEnabled()) {
            fx2 = this.normalizeColumn(x2);
        } else {
            fx2 = x2;
        }
        if (this.isVerticalWraparoundEnabled()) {
            fy2 = this.normalizeRow(y2);
        } else {
            fy2 = y2;
        }
        try {
            return this.visionData.getCell(fx2, fy2, zLoc);
        } catch (final ArrayIndexOutOfBoundsException aioobe) {
            return true;
        }
    }

    private boolean isSquareVisibleLOS(final int x1, final int y1, final int x2,
            final int y2) {
        int fx1, fx2, fy1, fy2;
        fx1 = x1;
        fx2 = x2;
        fy1 = y1;
        fy2 = y2;
        final var zLoc = this.getPlayerFloor();
        final var dx = Math.abs(fx2 - fx1);
        final var dy = Math.abs(fy2 - fy1);
        int sx, sy;
        if (fx1 < fx2) {
            sx = 1;
        } else {
            sx = -1;
        }
        if (fy1 < fy2) {
            sy = 1;
        } else {
            sy = -1;
        }
        var err = dx - dy;
        int e2;
        do {
            if (fx1 == fx2 && fy1 == fy2) {
                break;
            }
            // Does object block LOS?
            try {
                final var obj = this.getMazeCell(fx1, fy1, zLoc,
                        MazeConstants.LAYER_OBJECT);
                // This object blocks LOS
                if (obj.isSightBlocking() && (fx1 != x1 || fy1 != y1)) {
                    return false;
                }
            } catch (final ArrayIndexOutOfBoundsException aioobe) {
                // Void blocks LOS
                return false;
            }
            e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                fx1 = fx1 + sx;
            }
            if (e2 < dx) {
                err = err + dx;
                fy1 = fy1 + sy;
            }
        } while (true);
        // No objects block LOS
        return true;
    }

    public boolean isVerticalWraparoundEnabled() {
        return this.verticalWraparoundEnabled;
    }

    private int normalizeColumn(final int column) {
        var fC = column;
        if (fC < 0) {
            fC += this.getColumns();
            while (fC < 0) {
                fC += this.getColumns();
            }
        } else if (fC > this.getColumns() - 1) {
            fC -= this.getColumns();
            while (fC > this.getColumns() - 1) {
                fC -= this.getColumns();
            }
        }
        return fC;
    }

    private int normalizeRow(final int row) {
        var fR = row;
        if (fR < 0) {
            fR += this.getRows();
            while (fR < 0) {
                fR += this.getRows();
            }
        } else if (fR > this.getRows() - 1) {
            fR -= this.getRows();
            while (fR > this.getRows() - 1) {
                fR -= this.getRows();
            }
        }
        return fR;
    }

    public void offsetPlayerColumn(final int newPlayerColumn) {
        this.playerLocationData[0] += newPlayerColumn;
    }

    public void offsetPlayerRow(final int newPlayerRow) {
        this.playerLocationData[1] += newPlayerRow;
    }

    public void postBattle(final Monster m, final int xLoc, final int yLoc,
            final boolean player) {
        final var saved = m.getSavedObject();
        final var zLoc = this.getPlayerFloor();
        if (!player) {
            this.setMazeCell(saved, xLoc, yLoc, zLoc,
                    MazeConstants.LAYER_OBJECT);
        }
        this.generateOneMonster();
    }

    public void readSavedTowerState(final XDataReader reader,
            final int formatVersion) throws IOException {
        int x, y, z, e, sizeX, sizeY, sizeZ;
        sizeX = reader.readInt();
        sizeY = reader.readInt();
        sizeZ = reader.readInt();
        this.savedTowerState = new LowLevelAMODataStore(sizeY, sizeX, sizeZ,
                MazeConstants.LAYER_COUNT);
        for (x = 0; x < sizeY; x++) {
            for (y = 0; y < sizeX; y++) {
                for (z = 0; z < sizeZ; z++) {
                    for (e = 0; e < MazeConstants.LAYER_COUNT; e++) {
                        this.savedTowerState.setMazeCell(
                                RetroRPGCS.getInstance().getObjects()
                                        .readMazeObject(reader, formatVersion),
                                y, x, z, e);
                    }
                }
            }
        }
    }

    public void resetVisibleSquares() {
        for (var x = 0; x < this.getRows(); x++) {
            for (var y = 0; y < this.getColumns(); y++) {
                for (var z = 0; z < this.getFloors(); z++) {
                    this.visionData.setCell(false, x, y, z);
                }
            }
        }
    }

    public void restore() {
        int y, x, z, e;
        for (x = 0; x < this.getColumns(); x++) {
            for (y = 0; y < this.getRows(); y++) {
                for (z = 0; z < this.getFloors(); z++) {
                    for (e = 0; e < MazeConstants.LAYER_COUNT; e++) {
                        this.setMazeCell(
                                this.savedTowerState.getMazeCell(x, y, z, e), y,
                                x, z, e);
                    }
                }
            }
        }
    }

    public void restorePlayerLocation() {
        System.arraycopy(this.savedPlayerLocationData, 0,
                this.playerLocationData, 0, this.playerLocationData.length);
    }

    public void save() {
        int y, x, z, e;
        for (x = 0; x < this.getColumns(); x++) {
            for (y = 0; y < this.getRows(); y++) {
                for (z = 0; z < this.getFloors(); z++) {
                    for (e = 0; e < MazeConstants.LAYER_COUNT; e++) {
                        this.savedTowerState.setMazeCell(
                                this.getMazeCell(y, x, z, e), x, y, z, e);
                    }
                }
            }
        }
    }

    public void savePlayerLocation() {
        System.arraycopy(this.playerLocationData, 0,
                this.savedPlayerLocationData, 0,
                this.playerLocationData.length);
    }

    public void setMazeCell(final AbstractMazeObject mo, final int row,
            final int col, final int floor, final int extra) {
        var fR = row;
        var fC = col;
        final var fF = floor;
        if (this.verticalWraparoundEnabled) {
            fC = this.normalizeColumn(fC);
        }
        if (this.horizontalWraparoundEnabled) {
            fR = this.normalizeRow(fR);
        }
        this.data.setMazeCell(mo, fC, fR, fF, extra);
    }

    public void setPlayerColumn(final int newPlayerColumn) {
        this.playerLocationData[0] = newPlayerColumn;
    }

    public void setPlayerFloor(final int newPlayerFloor) {
        this.playerLocationData[2] = newPlayerFloor;
    }

    public void setPlayerRow(final int newPlayerRow) {
        this.playerLocationData[1] = newPlayerRow;
    }

    public void setPlayerToStart() {
        System.arraycopy(this.playerStartData, 0, this.playerLocationData, 0,
                this.playerStartData.length);
    }

    public void setStartColumn(final int newStartColumn) {
        this.playerStartData[0] = newStartColumn;
    }

    public void setStartFloor(final int newStartFloor) {
        this.playerStartData[2] = newStartFloor;
    }

    public void setStartRow(final int newStartRow) {
        this.playerStartData[1] = newStartRow;
    }

    public void tickTimers(final int floor) {
        int x, y;
        // Tick all MazeObject timers
        for (x = 0; x < this.getColumns(); x++) {
            for (y = 0; y < this.getRows(); y++) {
                final var mo = this.getMazeCell(y, x, floor,
                        MazeConstants.LAYER_OBJECT);
                if (mo != null) {
                    mo.tickTimer(y, x);
                }
            }
        }
    }

    // Methods
    public void updateMonsterPosition(final int move, final int xLoc,
            final int yLoc, final Monster monster) {
        final var app = RetroRPGCS.getInstance();
        final var dirMoveX = DirectionResolver
                .unresolveRelativeDirectionX(move);
        final var dirMoveY = DirectionResolver
                .unresolveRelativeDirectionY(move);
        final var pLocX = this.getPlayerRow();
        final var pLocY = this.getPlayerColumn();
        final var zLoc = this.getPlayerFloor();
        try {
            final var there = this.getMazeCell(xLoc + dirMoveX, yLoc + dirMoveY,
                    zLoc, MazeConstants.LAYER_OBJECT);
            final var ground = this.getMazeCell(xLoc + dirMoveX,
                    yLoc + dirMoveY, zLoc, MazeConstants.LAYER_GROUND);
            if (!there.isSolid() && !there.getName().equals("Monster")) {
                if (LayeredTower.radialScan(xLoc, yLoc, 0, pLocX, pLocY)) {
                    if (app.getMode() != RetroRPGCS.STATUS_BATTLE) {
                        app.getGameManager().stopMovement();
                        app.getBattle().doBattle();
                        this.postBattle(monster, xLoc, yLoc, false);
                    }
                } else {
                    // Move the monster
                    this.setMazeCell(monster.getSavedObject(), xLoc, yLoc, zLoc,
                            MazeConstants.LAYER_OBJECT);
                    monster.setSavedObject(there);
                    this.setMazeCell(monster, xLoc + dirMoveX, yLoc + dirMoveY,
                            zLoc, MazeConstants.LAYER_OBJECT);
                    // Does the ground have friction?
                    if (!ground.hasFriction()) {
                        // No - move the monster again
                        this.updateMonsterPosition(move, xLoc + dirMoveX,
                                yLoc + dirMoveY, monster);
                    }
                }
            }
        } catch (final ArrayIndexOutOfBoundsException aioob) {
            // Do nothing
        }
    }

    public void updateVisibleSquares(final int xp, final int yp, final int zp) {
        if ((this.visionMode
                | MazeConstants.VISION_MODE_EXPLORE) == this.visionMode) {
            for (var x = xp - this.visionModeExploreRadius; x <= xp
                    + this.visionModeExploreRadius; x++) {
                for (var y = yp - this.visionModeExploreRadius; y <= yp
                        + this.visionModeExploreRadius; y++) {
                    int fx, fy;
                    if (this.isHorizontalWraparoundEnabled()) {
                        fx = this.normalizeColumn(x);
                    } else {
                        fx = x;
                    }
                    if (this.isVerticalWraparoundEnabled()) {
                        fy = this.normalizeRow(y);
                    } else {
                        fy = y;
                    }
                    var alreadyVisible = false;
                    try {
                        alreadyVisible = this.visionData.getCell(fx, fy, zp);
                    } catch (final ArrayIndexOutOfBoundsException aioobe) {
                        // Ignore
                    }
                    if (!alreadyVisible) {
                        if ((this.visionMode
                                | MazeConstants.VISION_MODE_LOS) == this.visionMode) {
                            if (this.isSquareVisibleLOS(x, y, xp, yp)) {
                                try {
                                    this.visionData.setCell(true, fx, fy, zp);
                                } catch (final ArrayIndexOutOfBoundsException aioobe) {
                                    // Ignore
                                }
                            }
                        } else {
                            try {
                                this.visionData.setCell(true, fx, fy, zp);
                            } catch (final ArrayIndexOutOfBoundsException aioobe) {
                                // Ignore
                            }
                        }
                    }
                }
            }
        }
    }

    public void writeLayeredTower(final XDataWriter writer) throws IOException {
        int y, x, z, e;
        writer.writeInt(this.getColumns());
        writer.writeInt(this.getRows());
        writer.writeInt(this.getFloors());
        for (x = 0; x < this.getColumns(); x++) {
            for (y = 0; y < this.getRows(); y++) {
                for (z = 0; z < this.getFloors(); z++) {
                    for (e = 0; e < MazeConstants.LAYER_COUNT; e++) {
                        this.getMazeCell(y, x, z, e).writeMazeObject(writer);
                    }
                    writer.writeBoolean(this.visionData.getCell(y, x, z));
                    final var hasNote = this.noteData.getNote(y, x, z) != null;
                    writer.writeBoolean(hasNote);
                    if (hasNote) {
                        this.noteData.getNote(y, x, z).writeNote(writer);
                    }
                }
            }
        }
        for (y = 0; y < 3; y++) {
            writer.writeInt(this.playerStartData[y]);
            writer.writeInt(this.playerLocationData[y]);
            writer.writeInt(this.savedPlayerLocationData[y]);
            writer.writeInt(this.findResult[y]);
        }
        writer.writeBoolean(this.horizontalWraparoundEnabled);
        writer.writeBoolean(this.verticalWraparoundEnabled);
        writer.writeInt(this.visionMode);
        writer.writeInt(this.visionModeExploreRadius);
        writer.writeInt(this.visionMode);
        writer.writeInt(this.visionModeExploreRadius);
        writer.writeInt(this.visionRadius);
        writer.writeInt(this.initialVisionRadius);
        writer.writeInt(this.regionSize);
    }

    public void writeSavedTowerState(final XDataWriter writer)
            throws IOException {
        int x, y, z, e;
        writer.writeInt(this.getColumns());
        writer.writeInt(this.getRows());
        writer.writeInt(this.getFloors());
        for (x = 0; x < this.getColumns(); x++) {
            for (y = 0; y < this.getRows(); y++) {
                for (z = 0; z < this.getFloors(); z++) {
                    for (e = 0; e < MazeConstants.LAYER_COUNT; e++) {
                        this.savedTowerState.getMazeCell(y, x, z, e)
                                .writeMazeObject(writer);
                    }
                }
            }
        }
    }
}
