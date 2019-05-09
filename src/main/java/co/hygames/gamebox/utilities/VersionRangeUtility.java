/*
 * GameBox
 * Copyright (C) 2019  Niklas Eicker
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package co.hygames.gamebox.utilities;

import co.hygames.gamebox.utilities.versioning.SemanticVersion;

import java.text.ParseException;

public class VersionRangeUtility {
    private static final String CONSTRAIN_OPERATOR_EQUAL = "=";
    private static final String CONSTRAIN_OPERATOR_EQUAL_OR_LARGER = ">=";
    private static final String CONSTRAIN_OPERATOR_EQUAL_OR_SMALLER = "<=";
    private static final String CONSTRAIN_OPERATOR_TWIDDLE_WAKKA = "~>";

    public static boolean isInVersionRange(SemanticVersion version, String versionRange) throws ParseException {
        String[] constrains = versionRange.split(",");
        for (String constrain : constrains) {
            String constrainedVersion;
            String constrainOperator;
            String[] constrainParts = constrain.split(" ");
            if (constrainParts.length > 2 || constrainParts.length == 0) {
                throw new IllegalArgumentException("The constrain '" + constrain + "' has an illegal number of parts!");
            }
            if (constrainParts.length == 2) {
                constrainOperator = constrainParts[0];
                constrainedVersion = constrainParts[1];
            } else {
                constrainOperator = "=";
                constrainedVersion = constrainParts[0];
            }
            if (isInVersionRange(version, constrainedVersion, constrainOperator)) {
                continue;
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean isInVersionRange(SemanticVersion version, String constrainedVersion, String constrainOperator) throws ParseException {
        switch (constrainOperator) {
            case CONSTRAIN_OPERATOR_EQUAL:
                return version.equals(new SemanticVersion(constrainedVersion));

            case CONSTRAIN_OPERATOR_EQUAL_OR_LARGER:
                return version.equals(new SemanticVersion(constrainedVersion)) || version.equals(new SemanticVersion(constrainedVersion))
            break;
            case CONSTRAIN_OPERATOR_EQUAL_OR_SMALLER:
                break;
            case CONSTRAIN_OPERATOR_TWIDDLE_WAKKA:
                break;
            default:
                throw new IllegalArgumentException("Unknown version range operator: " + constrainOperator);
        }

    }

    public static SemanticVersion getLowestAllowedVersion(String versionRange) {
        try {
            return new SemanticVersion("1.0.0");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SemanticVersion getHighestAllowedVersion(String versionRange) {
        try {
            return new SemanticVersion("1.0.0");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
