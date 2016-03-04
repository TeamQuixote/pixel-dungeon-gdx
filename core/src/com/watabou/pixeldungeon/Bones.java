/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pixeldungeon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.items.Gold;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.rings.Ring;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Bones {

	private static final String BONES_FILE	= "bones.dat";
	
	private static final String LEVEL	= "level";
	private static final String ITEM	= "item";
	
	private static int depth = -1;
	private static Item item;
	
	public static void leave(Dungeon dungeon) {
		
		item = null;
		switch (Random.Int( 4 )) {
		case 0:
			item = dungeon.hero.belongings.weapon;
			break;
		case 1:
			item = dungeon.hero.belongings.armor;
			break;
		case 2:
			item = dungeon.hero.belongings.ring1;
			break;
		case 3:
			item = dungeon.hero.belongings.ring2;
			break;
		}
		if (item == null) {
			if (dungeon.gold > 0) {
				item = new Gold( Random.IntRange( 1, dungeon.gold ) );
			} else {
				item = new Gold( 1 );
			}
		}
		
		depth = dungeon.depth;
		
		Bundle bundle = new Bundle();
		bundle.put( LEVEL, depth );
		bundle.put( ITEM, item );
		
		try {
            if(Game.instance != null) {
                OutputStream output = Game.instance.openFileOutput(BONES_FILE);
                Bundle.write(bundle, output);
                output.close();
            }
		} catch (IOException e) {

		}
	}
	
	public static Item get(Dungeon dungeon) {
        if(Game.instance == null)
            return null;

		if (depth == -1) {
			try {
				InputStream input = Game.instance.openFileInput( BONES_FILE ) ;
				Bundle bundle = Bundle.read( input );
				input.close();
				
				depth = bundle.getInt( LEVEL );
				item = (Item)bundle.get( ITEM );
				
				return get(dungeon);
				
			} catch (IOException e) {
				return null;
			}
			
		} else {
			if (depth == dungeon.depth) {
				Game.instance.deleteFile( BONES_FILE );
				depth = 0;
				
				if (!item.stackable) {
					item.cursed = true;
					item.cursedKnown = true;
					if (item.isUpgradable()) {
						int lvl = (dungeon.depth - 1) * 3 / 5 + 1;
						if (lvl < item.level) {
							item.degrade( item.level - lvl );
						}
						item.levelKnown = false;
					}
				}
				
				if (item instanceof Ring) {
					((Ring)item).syncGem();
				}
				
				return item;
			} else {
				return null;
			}
		}
	}
}
