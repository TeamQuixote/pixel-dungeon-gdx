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
package com.watabou.pixeldungeon.actors;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public abstract class Actor implements Bundlable {

	public Dungeon dungeon = Dungeon.getInstance();

	public static final float TICK	= 1f;

	public float time;
	
	public abstract boolean act();
	
	protected void spend( float time ) {
		this.time += time;
	}
	
	protected void postpone( float time ) {
		if (this.time < Dungeon.getInstance().now + time) {
			this.time = Dungeon.getInstance().now + time;
		}
	}
	
	protected float cooldown() {
		return time - Dungeon.getInstance().now;
	}
	
	protected void diactivate() {
		time = Float.MAX_VALUE;
	}
	
	public void onAdd() {}
	
	public void onRemove() {}
	
	private static final String TIME = "time";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( TIME, time );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		time = bundle.getFloat( TIME );
	}
}
