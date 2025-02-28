package com.github.ummo93.factory;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.github.ummo93.GameApp;
import com.github.ummo93.component.CleanOutOfBoundsComponent;
import com.github.ummo93.component.KeyboardControlComponent;
import com.github.ummo93.component.MovementComponent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class SpaceEntityFactory implements EntityFactory {
    @Spawns("bullet")
    public Entity createBullet(SpawnData data) {
        return entityBuilder(data)
            .type(GameApp.Type.BULLET)
            .with(new CleanOutOfBoundsComponent())
            .with(new MovementComponent(-500))
            .viewWithBBox(new Rectangle(2, 10, Color.RED))
            .collidable()
            .build();
    }

    @Spawns("asteroid")
    public Entity createAsteroid(SpawnData data) {
        var multiplier = data.hasKey("speedMultiplier") ? (double)data.get("speedMultiplier") : 1.0;
        var speed = 150 * multiplier;
        var animation = texture("asteroid.png")
            .toAnimatedTexture(24, Duration.seconds(random(1, 3)));
        var scale = FXGLMath.random(0.4, 1.0);
        return entityBuilder(data)
            .type(GameApp.Type.ASTEROID)
            .with(new CleanOutOfBoundsComponent())
            .with(new MovementComponent(speed))
            .at(FXGLMath.random(0, getWindowService().getPrefWidth() - 64), 0)
            .scale(scale, scale)
            .viewWithBBox(animation.loop())
            .collidable()
            .build();
    }

    @Spawns("star")
    public Entity createBackgroundStar(SpawnData data) {
        var multiplier = data.hasKey("speedMultiplier") ? (double)data.get("speedMultiplier") : 1.0;
        var speed = 50 * multiplier;
        return entityBuilder(data)
            .type(GameApp.Type.BACKGROUND_STAR)
            .with(new CleanOutOfBoundsComponent())
            .with(new MovementComponent(speed))
            .at(FXGLMath.random(0, getWindowService().getPrefWidth() - 64), 0)
            .view(new Rectangle(1, 1, Color.WHITE))
            .collidable()
            .build();
    }

    @Spawns("explosion")
    public Entity createExplosion(SpawnData data) {
        var texture = texture("explosion-1.png").toAnimatedTexture(8, Duration.seconds(1)).loop();
        var explosion = entityBuilder()
            .viewWithBBox(texture)
            .at(data.getX() - texture.getFitWidth()/2, data.getY() - texture.getFitHeight()/2)
            .build();
        texture.setOnCycleFinished(explosion::removeFromWorld);
        return explosion;
    }

    @Spawns("player")
    public Entity createPlayerShip(SpawnData data) {
        var player =  entityBuilder()
            .type(GameApp.Type.PLAYER_SHIP)
            .with(new KeyboardControlComponent(2.5))
            .viewWithBBox("fighter.png")
            .at(getWindowService().getPrefWidth() / 2, getWindowService().getPrefHeight() - 50)
            .collidable()
            .zIndex(100)
            .build();
        player.yProperty().bind(getWindowService().prefHeightProperty().subtract(50));
        return player;
    }
}
