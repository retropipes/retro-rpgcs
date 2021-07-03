/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.utilities;

public class DirectionResolver {
    public static final String resolveDirectionConstantToName(final int dir) {
        String res = null;
        res = switch (dir) {
        case DirectionConstants.DIRECTION_NORTH -> DirectionConstants.DIRECTION_NORTH_NAME;
        case DirectionConstants.DIRECTION_SOUTH -> DirectionConstants.DIRECTION_SOUTH_NAME;
        case DirectionConstants.DIRECTION_WEST -> DirectionConstants.DIRECTION_WEST_NAME;
        case DirectionConstants.DIRECTION_EAST -> DirectionConstants.DIRECTION_EAST_NAME;
        case DirectionConstants.DIRECTION_SOUTHEAST -> DirectionConstants.DIRECTION_SOUTHEAST_NAME;
        case DirectionConstants.DIRECTION_SOUTHWEST -> DirectionConstants.DIRECTION_SOUTHWEST_NAME;
        case DirectionConstants.DIRECTION_NORTHWEST -> DirectionConstants.DIRECTION_NORTHWEST_NAME;
        case DirectionConstants.DIRECTION_NORTHEAST -> DirectionConstants.DIRECTION_NORTHEAST_NAME;
        case DirectionConstants.DIRECTION_NONE -> DirectionConstants.DIRECTION_NONE_NAME;
        default -> DirectionConstants.DIRECTION_INVALID_NAME;
        };
        return res;
    }

    public static final int resolveRelativeDirection(final int dirX,
            final int dirY) {
        final var fdX = (int) Math.signum(dirX);
        final var fdY = (int) Math.signum(dirY);
        if (fdX == 0 && fdY == 0) {
            return DirectionConstants.DIRECTION_NONE;
        } else if (fdX == 0 && fdY == -1) {
            return DirectionConstants.DIRECTION_NORTH;
        } else if (fdX == 0 && fdY == 1) {
            return DirectionConstants.DIRECTION_SOUTH;
        } else if (fdX == -1 && fdY == 0) {
            return DirectionConstants.DIRECTION_WEST;
        } else if (fdX == 1 && fdY == 0) {
            return DirectionConstants.DIRECTION_EAST;
        } else if (fdX == 1 && fdY == 1) {
            return DirectionConstants.DIRECTION_SOUTHEAST;
        } else if (fdX == -1 && fdY == 1) {
            return DirectionConstants.DIRECTION_SOUTHWEST;
        } else if (fdX == -1 && fdY == -1) {
            return DirectionConstants.DIRECTION_NORTHWEST;
        } else if (fdX == 1 && fdY == -1) {
            return DirectionConstants.DIRECTION_NORTHEAST;
        } else {
            return DirectionConstants.DIRECTION_INVALID;
        }
    }

    public static final int[] unresolveRelativeDirection(final int dir) {
        return new int[] { unresolveRelativeDirectionX(dir),
                unresolveRelativeDirectionY(dir) };
    }

    public static final int unresolveRelativeDirectionX(final int dir) {
        return switch (dir) {
        case DirectionConstants.DIRECTION_NORTH -> {
            yield 0;
        }
        case DirectionConstants.DIRECTION_SOUTH -> {
            yield 0;
        }
        case DirectionConstants.DIRECTION_WEST -> {
            yield -1;
        }
        case DirectionConstants.DIRECTION_EAST -> {
            yield 1;
        }
        case DirectionConstants.DIRECTION_SOUTHEAST -> {
            yield 1;
        }
        case DirectionConstants.DIRECTION_SOUTHWEST -> {
            yield -1;
        }
        case DirectionConstants.DIRECTION_NORTHWEST -> {
            yield -1;
        }
        case DirectionConstants.DIRECTION_NORTHEAST -> {
            yield 1;
        }
        default -> 0;
        };
    }

    public static final int unresolveRelativeDirectionY(final int dir) {
        return switch (dir) {
        case DirectionConstants.DIRECTION_NORTH -> {
            yield -1;
        }
        case DirectionConstants.DIRECTION_SOUTH -> {
            yield 1;
        }
        case DirectionConstants.DIRECTION_WEST -> {
            yield 0;
        }
        case DirectionConstants.DIRECTION_EAST -> {
            yield 0;
        }
        case DirectionConstants.DIRECTION_SOUTHEAST -> {
            yield 1;
        }
        case DirectionConstants.DIRECTION_SOUTHWEST -> {
            yield 1;
        }
        case DirectionConstants.DIRECTION_NORTHWEST -> {
            yield -1;
        }
        case DirectionConstants.DIRECTION_NORTHEAST -> {
            yield -1;
        }
        default -> 0;
        };
    }
}
