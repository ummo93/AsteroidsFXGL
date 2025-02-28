package com.github.ummo93;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.github.ummo93.factory.SpaceEntityFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static javafx.application.Platform.exit;

public class GameApp extends GameApplication {
    private final int START_STAR_COUNT = 50;
    private final String SCORE_TEXT = "SCORE: %d";
    private int score = 0;
    private double speed = 0.3;
    private Text textPixels;

    public enum Type {
        ASTEROID,
        PLAYER_SHIP,
        BULLET,
        BACKGROUND_STAR
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Space Shooter");
        settings.setVersion("1.0");
        settings.setWidth(480);
        settings.setHeight(800);
        settings.setManualResizeEnabled(true);
        settings.setScaleAffectedOnResize(false);
    }

    @Override
    protected void initUI() {
        textPixels = new Text();
        textPixels.setTranslateX(25);
        textPixels.setTranslateY(35);
        textPixels.setText(SCORE_TEXT.formatted(score));
        textPixels.setFont(getUIFactoryService().newFont(24));
        textPixels.setFill(Color.WHITE);
        getGameScene().addUINode(textPixels);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        var scene = getGameScene();
        var world = getGameWorld();
        world.addEntityFactory(new SpaceEntityFactory());
        scene.setBackgroundColor(Color.BLACK);
        scene.setCursorInvisible();
        scene.setBackgroundRepeat("background.png");
    }

    @Override
    protected void initGame() {
        run(() -> spawn("asteroid", getContextData()), Duration.seconds(1));
        run(() -> speed += 0.1, Duration.seconds(10));
        run(() -> spawn("star", getContextData()), Duration.seconds(0.5));
        loopBGM("bgm.mp3");
        spawn("player");
        initBackgroundStars();
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(Type.PLAYER_SHIP, Type.ASTEROID, this::onAsteroidCollision);
        onCollisionBegin(Type.BULLET, Type.ASTEROID, this::onAsteroidCollision);
    }

    @Override
    protected void onUpdate(double dt) {
        getGameWorld().getEntities().forEach(entity -> {
            if (entity.getY() > getAppHeight()) {
                if (entity.isType(Type.ASTEROID)) {
                    score--;
                    updateUiLabels();
                }
            }
        });
    }

    private SpawnData getContextData() {
        return new SpawnData().put("speedMultiplier", speed);
    }

    private void updateUiLabels() {
        textPixels.setText(SCORE_TEXT.formatted(score));
    }

    private void onAsteroidCollision(Entity other, Entity asteroid) {
        if (other.getType() == Type.PLAYER_SHIP) {
            play("lost_in_space.wav");
            gameOver();
            return;
        }
        if (other.getType() == Type.BULLET) {
            other.removeFromWorld();
            score++;
            updateUiLabels();
        }
        var explosion = spawn("explosion", asteroid.getCenter());
        explosion.setScaleX(asteroid.getScaleX());
        explosion.setScaleY(asteroid.getScaleY());
        asteroid.removeFromWorld();
        play("blast.wav");
    }

    private void gameOver() {
        showConfirm("Game Over! Play again?", yes -> {
            if (yes) {
                getGameWorld().getEntitiesCopy().forEach(Entity::removeFromWorld);
                score = 0;
                updateUiLabels();
                spawn("player");
                initBackgroundStars();
                speed = 1;
            } else {
                exit();
            }
        });
    }

    private void initBackgroundStars() {
        for (int i = 0; i < START_STAR_COUNT; i++) {
            var star = getGameWorld().spawn("star");
            star.translateX(random(0, getAppWidth()));
            star.translateY(random(0, getAppHeight()));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
