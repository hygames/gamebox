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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package co.hygames.gamebox.utilities;

import co.hygames.gamebox.GameBox;
import co.hygames.gamebox.module.GameBoxModule;
import co.hygames.gamebox.player.GbPlayer;

/**
 * @author Niklas Eicker
 */
public enum Permission {
    ADMIN_GLOBAL("admin.global.{}", true),
    ADMIN_LANGUAGE("admin.language.{}", true),
    ADMIN_SETTINGS("admin.settings.{}", true),
    ADMIN_MODULES("admin.modules.{}", true),
    PLAY("play.{}", true);

    private String perm;
    private boolean perGame = false;

    Permission(String perm) {
        this.perm = GameBox.versionInfo.getId() + "." + perm;
    }

    Permission(String perm, boolean perGame) {
        this(perm);
        this.perGame = perGame;
    }

    public boolean assignedTo(GbPlayer gbPlayer) {
        if (this.perGame) throw new IllegalArgumentException("Module specific permission checked without module context!");
        // ToDo
        throw new UnsupportedOperationException("permissions are not implemented yet");
    }

    public boolean assignedForModule(GbPlayer gbPlayer, GameBoxModule module) {
        return assignedForModule(gbPlayer, module.getIdentifier());
    }

    public boolean assignedForModule(GbPlayer gbPlayer, String moduleId) {
        if (!this.perGame) throw new IllegalArgumentException("General permission checked with module context!");
        String perm = this.perm.replace("{}", moduleId);
        // ToDo
        throw new UnsupportedOperationException("permissions are not implemented yet");
    }
}
