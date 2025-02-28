package com.github.ummo93.component;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.play;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class KeyboardControlComponent extends Component {
    private double speed;

    public KeyboardControlComponent(double speed) {
        this.speed = speed;
    }

    @Override
    public void onAdded() {
        Input input = getInput();
        input.addAction(new UserAction("Turn left") {
            @Override
            protected void onAction() {
                entity.translateX(-speed);
            }
        }, KeyCode.A);
        input.addAction(new UserAction("Turn right") {
            @Override
            protected void onAction() {
                entity.translateX(speed);
            }
        }, KeyCode.D);
        input.addAction(new UserAction("Fire") {
            @Override
            protected void onActionBegin() {
                spawn("bullet", entity.getCenter().add(0, -5));
                play("lazer.wav");
            }
        }, KeyCode.SPACE);
    }

}
