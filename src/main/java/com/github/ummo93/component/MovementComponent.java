package com.github.ummo93.component;

import com.almasb.fxgl.entity.component.Component;

public class MovementComponent extends Component {
    private double speed;

    public MovementComponent(double speed) {
        this.speed = speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    @Override
    public void onUpdate(double dt) {
        entity.translateY(speed * dt);
    }
}
