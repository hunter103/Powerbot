package com.hunterz103.rsbot.scripts.dragonScales.tasks;

import com.hunterz103.rsbot.scripts.dragonScales.BlueDragonScalePicker;
import com.hunterz103.rsbot.scripts.dragonScales.enums.Place;
import com.hunterz103.rsbot.scripts.framework.Task;
import com.hunterz103.rsbot.util.Pathing;
import org.powerbot.script.util.Condition;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.script.wrappers.TilePath;

import java.util.concurrent.Callable;

/**
 * Created by Brian on 2/5/14.
 */
public class WalkToDungeon extends Task<BlueDragonScalePicker> {

    Pathing pathing = new Pathing(ctx);
    TilePath pathToWall = ctx.movement.newTilePath(new Tile(2952, 3382, 0), new Tile(2941, 3372, 0), new Tile(2940, 3356, 0));
    TilePath pathToDung = ctx.movement.newTilePath(new Tile(2931, 3372, 0), new Tile(2921, 3378, 0), new Tile(2906, 3384, 0), new Tile(2892, 3388, 0), new Tile(2886, 3394, 0));

    public WalkToDungeon(BlueDragonScalePicker arg0) {
        super(arg0);
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public boolean activate() {
        return !Place.DRAGONS.area.contains(ctx.players.local()) &&!Place.INNER_DUNGEON.area.contains(ctx.players.local()) && !ctx.bank.isOpen() && ctx.backpack.select().count() != 28;
    }

    @Override
    public void execute() {
        if (ctx.players.local().getLocation().getX() >= 2936) { //Before the wall jump
            if (pathToWall.getEnd().distanceTo(ctx.players.local()) > 3) {
                pathing.walkPath(pathToWall, 1, 1, 2);
            } else {
                final GameObject wall = ctx.objects.select().id(11844).poll();

                if (wall.isValid()) {
                    ctx.camera.turnTo(wall);
                    sleep(300, 500);

                    if (wall.interact("Climb-over")) {
                        script.log("Climbing over wall.");
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return ctx.players.local().getLocation().getX() <= 2935 || !ctx.players.local().isInMotion();
                            }
                        }, 400, 10);
                    }
                }
            }
        } else { //Post wall jump
            if (Place.OUT_OF_DUNGEON.area.contains(ctx.players.local())) { //NEXT TO STEPS
                final GameObject dungeon = ctx.objects.select().id(66991).poll();

                if (!dungeon.isInViewport()) {
                    ctx.camera.turnTo(dungeon);
                    sleep(300, 500);
                }

                if (dungeon.interact("Climb-down")) {
                    script.log("Going into dungeon.");
                    Condition.wait(new Callable() {
                        @Override
                        public Object call() throws Exception {
                            return Place.INNER_DUNGEON.area.contains(ctx.players.local()) || !ctx.players.local().isInMotion();
                        }
                    }, 300, 20);
                }
            } else { //AWAY FROM STEPS
                pathing.walkPath(pathToDung, 2, 3, 3);
            }
        }
    }
}
