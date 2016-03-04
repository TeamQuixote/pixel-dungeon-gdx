package com.teamquixote.ai;

import com.watabou.pixeldungeon.Dungeon;

public class DungeonStats {

    private Dungeon dungeon;

    public DungeonStats(Dungeon dungeon){
        this.dungeon = dungeon;
    }

    public int getTotalDiscoveredTiles(){
        int count = 0;
        for(int i = 0; i < this.dungeon.level.visited.length; i++) {
            if(this.dungeon.level.visited[i]){
                count++;
            }
        }
        return count;
    }
}
