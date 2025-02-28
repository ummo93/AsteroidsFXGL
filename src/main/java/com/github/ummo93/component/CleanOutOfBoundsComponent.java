package com.github.ummo93.component;

import com.almasb.fxgl.entity.component.Component;

import static com.almasb.fxgl.dsl.FXGL.getWindowService;

public class CleanOutOfBoundsComponent extends Component {
    @Override
    public void onUpdate(double dt) {
        if (entity.getY() > getWindowService().getPrefHeight()) {
            entity.removeFromWorld();
        }
    }
}
